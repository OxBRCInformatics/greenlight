package uk.ac.ox.brc.greenlight

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification


@TestFor(PatientService)
@Mock(Patient)
class PatientServiceSpec extends Specification {

	def "findAllByNHSOrHospitalNumber"(){
		given:
		def patients = [new Patient(givenName: "John Doe", nhsNumber: "1234567890", hospitalNumber: "OXNHS12345").save(flush: true),
						new Patient(givenName: "John Doe", nhsNumber: "1234567890", hospitalNumber: "OXNHS12345").save(flush: true),
						new Patient(givenName: "John Smith", nhsNumber: "1234507000", hospitalNumber: "OXNHS12005").save(flush: true),
						new Patient(givenName: "David Man", nhsNumber: "1234568900", hospitalNumber: "OXNHS12605").save(flush: true)]

		when: "Passing a valid nhsNumber"
		def result = service.findAllByNHSOrHospitalNumber(patients[0].nhsNumber)

		then:"all patients with this nhsNumber should be returned"
		result.size() == 2
		result[0].id == patients[0].id
		result[1].id == patients[1].id


		when: "Passing another valid nhsNumber"
		result = service.findAllByNHSOrHospitalNumber("1234507000")

		then:"all patients with this nhsNumber should be returned"
		result.size() == 1
		result[0].id == patients[2].id


		when: "Passing a valid Hospital number will return all patients having this hospital number"
		result = service.findAllByNHSOrHospitalNumber(patients[0].hospitalNumber)

		then:"all patients with this Hospital number should be returned"
		result.size() == 2
		result[0].id == patients[0].id
		result[1].id == patients[1].id


		when:"null passed"
		result = service.findAllByNHSOrHospitalNumber(null)

		then:"null will an empty list"
		result == []
	}
}
