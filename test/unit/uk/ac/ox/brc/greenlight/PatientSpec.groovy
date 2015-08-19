package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class PatientSpec extends Specification {

    void "equals will check if two patients are equals"() {

		when:
		def patient1 = new Patient(givenName : "A",familyName: "B",dateOfBirth: new Date(2000,11,05),nhsNumber: "1234567890",hospitalNumber	:"123")
		def patient2 = new Patient(givenName : "A",familyName: "B",dateOfBirth: new Date(2000,11,05),nhsNumber: "1234567890",hospitalNumber	:"123")
		def patient3 = new Patient(givenName : "C",familyName: "D",dateOfBirth: new Date(2000,11,05),nhsNumber: "1234567890",hospitalNumber	:"123")

		then:
		patient1 == patient2
		assertEquals(patient1,patient2)
		patient1 != patient3
	}
}
