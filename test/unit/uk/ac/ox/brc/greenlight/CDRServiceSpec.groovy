package uk.ac.ox.brc.greenlight

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(CDRService)
@Mock([ConsentForm,Patient])
class CDRServiceSpec extends Specification {





	void "saveOrUpdateConsentForm saves new consentForm in CDR"() {

		setup:
		def sendConsentToCDRCalled = false
		service.metaClass.sendConsentToCDR = { nhsNumber,hospitalNumber,consent ->
			sendConsentToCDRCalled = true
			return "success"
		}

		when:
		def patient = new Patient()
		def consentForm = new ConsentForm()
		def result = service.saveOrUpdateConsentForm(patient,consentForm)

		then:
		sendConsentToCDRCalled
		result == "success"
	}
	void "saveOrUpdateConsentForm will pass default generic nhs and mrn into CDR when they are not available"() {
		setup:
		def sendConsentToCDRCalled = false
		service.metaClass.sendConsentToCDR = { nhsNumber,hospitalNumber,consent ->
			assert  nhsNumber == "??????????"
			assert hospitalNumber == "???"
			sendConsentToCDRCalled = true
			return "success"
		}
		when:
		def patient = new Patient(nhsNumber: "1111111111")
		def result = service.saveOrUpdateConsentForm(patient,new ConsentForm())

		then:
		sendConsentToCDRCalled
		result == "success"
	}
	def "findKnownPatientStatus returns KnownPatientStatus based on ConsentForm.ConsentStatus"(){
		when:
		def actual = service.findKnownPatientStatus(consentStatus)
		assert ConsentForm.ConsentStatus.values().size() == 3
		assert KnownPatientStatus.values().size() == 5

		then:
		actual == expected

		where:
		consentStatus							|	expected
		ConsentForm.ConsentStatus.NON_CONSENT	|	KnownPatientStatus.NON_CONSENT
		ConsentForm.ConsentStatus.FULL_CONSENT	|	KnownPatientStatus.CONSENTED
		ConsentForm.ConsentStatus.CONSENT_WITH_LABELS	|	KnownPatientStatus.RESTRICTED_CONSENT
		'UN-KNOWN'										|	null
	}

	def "findKnownOrganisation will return KnownOrganisation enum value"(){
		when:
		def actual = service.findKnownOrganisation(organisationName)
		assert KnownOrganisation.values().size() == 6

		then:
		actual == expected

		where:
		organisationName|	expected
		"ORB_PRE_V1_2"	|	KnownOrganisation.ORB_PRE_V1_2
		"ORB_GEN_V1"	|	KnownOrganisation.ORB_GEN_V1
		"ORB_CRA_V1"	|	KnownOrganisation.ORB_CRA_V1
		"GEL_CSC_V1"	|	KnownOrganisation.GEL_CSC_V1
		"GEL_CSC_V2"	|	KnownOrganisation.GEL_CSC_V2
		"ORB_GEN_V2"	|	KnownOrganisation.ORB_GEN_V2
		"UNKNOWN"		|	null
	}

	def "findKnownFacility will return KnownFacility enum value"(){
		when:
		def actual = service.findKnownFacility(facilityName)
		assert KnownFacility.values().size() == 2

		then:
		actual == expected

		where:
		facilityName	|	expected
		"TEST"			|	KnownFacility.TEST
		"PRODUCTION"	|	KnownFacility.PRODUCTION
	}
}
