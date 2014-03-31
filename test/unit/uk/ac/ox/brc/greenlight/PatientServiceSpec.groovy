package uk.ac.ox.brc.greenlight

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Test for the PathwayService. This was originally using data driven testing, but there
 * was a bizarre bug which meant the domain classes weren't being mocked for every test, so
 * I've just unfactored it out into many statements.
 * @author Ryan Brooks <ryan.brooks@ndm.ox.ac.uk>
 */
@TestFor(PatientService)
@Mock(Patient)
class PatientServiceSpec extends Specification {

	def "findByNHSNumber"(){
		given:
		Patient johnDoe = new Patient(givenName: "John Doe", nhsNumber: "1234567890", hospitalNumber: "OXNHS12345")
		johnDoe.save(flush: true)

		expect: "A valid NHS number returns the right patient"
		service.findByNHSNumber(johnDoe.nhsNumber) == johnDoe

		and: "A valid Hospital number DOESN'T return a patient"
		service.findByNHSNumber(johnDoe.hospitalNumber) == null

		and: "null gets null back"
		service.findByNHSNumber(null) == null

		and: "Any other input gets null back"
		service.findByNHSNumber("1245gunjkdnvbkj3") == null
		service.findByNHSNumber("") == null

		cleanup:
		johnDoe.delete()
	}

	def "findByHospitalNumber"(){
		given:
		Patient johnDoe = new Patient(givenName: "John Doe", nhsNumber: "1234567890", hospitalNumber: "OXNHS12345")
		johnDoe.save(flush: true)

		expect: "A valid hospital number returns the right patient"
		service.findByHospitalNumber(johnDoe.hospitalNumber) == johnDoe

		and: "A valid NHS number DOESN'T return a patient"
		service.findByHospitalNumber(johnDoe.nhsNumber) == null

		and: "null gets null back"
		service.findByHospitalNumber(null) == null

		and: "Any other input gets null back"
		service.findByHospitalNumber("1245gunjkdnvbkj3") == null
		service.findByHospitalNumber("") == null

		cleanup:
		johnDoe.delete()
	}

	def "findByNHSOrHospitalNumber"(){
		given:
		Patient johnDoe = new Patient(givenName: "John Doe", nhsNumber: "1234567890", hospitalNumber: "OXNHS12345")
		johnDoe.save(flush: true)

		expect: "A valid hospital number returns the right patient"
		service.findByNHSOrHospitalNumber(johnDoe.hospitalNumber) == johnDoe

		and: "A valid NHS number DOESN'T return a patient"
		service.findByNHSOrHospitalNumber(johnDoe.nhsNumber) == johnDoe

		and: "null gets null back"
		service.findByNHSOrHospitalNumber(null) == null

		and: "Any other input gets null back"
		service.findByNHSOrHospitalNumber("1245gunjkdnvbkj3") == null
		service.findByNHSOrHospitalNumber("") == null

		cleanup:
		johnDoe.delete()
	}
}
