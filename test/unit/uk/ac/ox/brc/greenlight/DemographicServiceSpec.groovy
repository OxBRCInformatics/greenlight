package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import groovy.mock.interceptor.MockFor
import groovy.sql.Sql
import spock.lang.Ignore
import spock.lang.Specification

import java.sql.Connection
import java.sql.DriverManager

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(DemographicService)
class DemographicServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }


	@Ignore
	void "checkPatient"() {
		expect: "returns null when passed null"
		null == service.findPatient(null)

		def nhsNumber = "1234567890"
		when: "nhs number is passed"

		def mock = new MockFor(Sql.class)
		mock.demand.with {
			Sql() { Connection con ->
				//mock its inner methods like findRow
				return [
						firstRow: { def query, def params ->
							//expect that findRow is called with proper params
							assert query  == "select ACTIVE_MRN,GIVENNAME,FAMILYNAME,DOB,SEX from PMI.VW_COSD_STAGING_DB where NHSNUMBER=:nhsNum"
							assert params == [nhsNum: nhsNumber]

							//return as mocked result
							[	ACTIVE_MRN: "10221601",
									GIVENNAME: "ABCD",
									FAMILYNAME: "HiABC",
									SEX: "1",
									DOB: new Date(2010, 05, 17)
							]
						}
				]
			}
		}

		then: "returns patient demographic"
		def result
		mock.use {
			result = service.findPatient(nhsNumber)
		}
		//it should just return these fields
		result.size() == 5
		result.ACTIVE_MRN == "10221601"
		result.GIVENNAME  == "ABCD"
		result.FAMILYNAME == "HiABC"
		result.SEX == "1"
		result.DOB == new Date(2010, 05, 17)
	}



}
