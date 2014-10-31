package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import groovy.mock.interceptor.MockFor
import groovy.sql.Sql
import spock.lang.Ignore
import spock.lang.Specification

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Time
import java.sql.Timestamp

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(DemographicService)
class DemographicServiceSpec extends Specification {

	def setup() {
	}

	def cleanup() {
	}


	void "checkPatient"() {
		expect: "returns null when passed null"
		null == service.findPatient(null)

		def nhsNumber = "1234567890"
		when: "nhs number is passed"

		DriverManager.metaClass."static".getConnection = { String url, String username, String password ->
			return null;
		};

		Sql.metaClass.constructor = { Connection con ->
			//mock its inner methods like firstRow
			return [
					firstRow: { def query, def params ->
						//expect that findRow is called with proper params
						assert query == "select ACTIVE_MRN,GIVENNAME,FAMILYNAME,DOB,SEX from PMI.VW_COSD_STAGING_DB where NHSNUMBER=:nhsNum"
						assert params == [nhsNum: nhsNumber]

						//return as mocked result
						[ACTIVE_MRN: "10221601",
								GIVENNAME: "John",
								FAMILYNAME: "Smith",
								SEX: "1",
								/*
								 * as the returned result from JDBC is a java.sql.Timestamp,
								 * so if we create it for test we need to consider that, the constructor takes these:
								 * http://docs.oracle.com/javase/7/docs/api/java/sql/Timestamp.html
								 * year - the year minus 1900
								 * month - 0 to 11
								 * date - 1 to 31,....
								 */
								DOB: new Timestamp(2010 - 1900, 04, 17, 0, 0, 0, 0)
						]
					}
			]
		}

		def result = service.findPatient(nhsNumber)

		then: "returns patient demographic"
		//it should just return these fields
		result.size() == 5
		result.ACTIVE_MRN == "10221601"
		result.GIVENNAME == "John"
		result.FAMILYNAME == "Smith"
		result.SEX == "1"
		result.DOB == new Date(2010, 04, 17)
	}
}
