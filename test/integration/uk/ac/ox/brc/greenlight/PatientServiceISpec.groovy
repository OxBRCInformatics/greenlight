package uk.ac.ox.brc.greenlight

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.spock.IntegrationSpec
import spock.lang.Specification

class PatientServiceISpec extends IntegrationSpec {

	def patientService = new PatientService()

	def "groupPatientsByNHSNumber will return list of all patient NHSNumber"() {
		given:
		new Patient(givenName: "John Doe", nhsNumber: "1234567890", hospitalNumber: "OXNHS12345").save(flush: true)
		new Patient(givenName: "John Doe", nhsNumber: "1234567890", hospitalNumber: "OXNHS12345").save(flush: true)
		new Patient(givenName: "John Doe", nhsNumber: "1234567890", hospitalNumber: "OXNHS12345").save(flush: true)
		new Patient(givenName: "John Smi", nhsNumber: "1234507000", hospitalNumber: "OXNHS12005").save(flush: true)
		new Patient(givenName: "A B", nhsNumber: "1234507000", hospitalNumber: "OXNHS12005").save(flush: true)
		new Patient(givenName: "David Man", nhsNumber: "1234568900", hospitalNumber: "OXNHS12605").save(flush: true)

		when:
		def result = patientService.groupPatientsByNHSNumber()

		then: "all patients with this nhsNumber should be returned"
		result.size() == 3
		result[0] == "1234507000"
	}
}
