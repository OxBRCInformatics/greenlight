package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import groovy.mock.interceptor.MockFor
import groovy.sql.Sql
import spock.lang.Ignore
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(DemographicService)
class DemographicServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }


	def "checkODBC"(){
		expect:
		service.checkODBC()

	}


	void "checkPatient"() {
		expect: "returns null when passed null"
		null == service.findPatient(null)

		/**
		 def mockSql = new MockFor(Sql.class)
		 mockSql.demand.newInstance {def a,def b,def c,def d ->
		 mockSql}mockSql.demand.findRow { def query,def param ->
		 return "AAA"}def result
		 mockSql.use {result = service.findPatient("9446457610")}* This mock approach doesn't work and I couldn't fix it!!
		 * It seems that it doesn't work this way:
		 * http://stackoverflow.com/questions/18825457/groovy-testing-groovy-sql-sql?answertab=active#tab-top
		 * So I mocked Sql in an odd manner.
		 */

		def nhsNumber = "1234567890"
		when: "nhs number is passed"
		//Mock Sql newInstance
		Sql.metaClass."static".newInstance = { String url, String username, String password, String driver ->
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
		def result = service.findPatient(nhsNumber)

		then: "returns patient demographic"
		//it should just return these fields
		result.size() == 5
		result.ACTIVE_MRN == "10221601"
		result.GIVENNAME  == "ABCD"
		result.FAMILYNAME == "HiABC"
		result.SEX == "1"
		result.DOB == new Date(2010, 05, 17)
	}


	def "checkOracle"(){
		expect:
		service.checkOracle(9446457610)==null
	}
}
