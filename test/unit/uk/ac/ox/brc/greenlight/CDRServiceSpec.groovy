package uk.ac.ox.brc.greenlight

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import uk.ac.ox.ndm.mirth.datamodel.dsl.clinical.patient.Consent
import uk.ac.ox.ndm.mirth.datamodel.dsl.core.Facility
import uk.ac.ox.ndm.mirth.datamodel.exception.rest.ClientException
import uk.ac.ox.ndm.mirth.datamodel.rest.client.KnownFacility
import uk.ac.ox.ndm.mirth.datamodel.rest.client.KnownOrganisation
import uk.ac.ox.ndm.mirth.datamodel.rest.client.KnownPatientStatus
import uk.ac.ox.ndm.mirth.datamodel.rest.client.MirthRestClient

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

	void "sendConsentToCDR sends consent into CDR and returns success"(){
		setup:
		def nhsNumber = "1234567890"
		def hospitalNumber = "123"
		def consentForm = new ConsentForm(template:new ConsentFormTemplate(cdrUniqueId: "GEL") )
		//Mock the internal methods of the Service
		service.metaClass.getCDRClient   = {
			def client = new Object()
			client.metaClass.createOrUpdatePatientConsent = {
				String consent, String patient, KnownFacility receivingFacility, KnownOrganisation organisation, KnownPatientStatus consentStatus,Closure clsr ->
					def result = new Object()
					result.metaClass.isOperationSucceeded = {
						return  true
					}
					return result
			}
			return client
		}
		service.metaClass.getCDRFacility = {new Object()}
		service.metaClass.findKnownOrganisation = {return KnownOrganisation.GEL_CSC_V1}
		service.metaClass.findKnownFacility = {return KnownFacility.TEST}
		service.metaClass.grailsApplication.getConfig = { [cdr:[knownFacility:"TEST",organisation:"Greenlight"] ]  }

		when:
		def result = service.sendConsentToCDR(nhsNumber,hospitalNumber,consentForm);

		then:
		result == "success"
	}

	void "sendConsentToCDR returns exception message when has error"(){
		setup:
		def nhsNumber = "1234567890"
		def hospitalNumber = "123"
		def consentForm = new ConsentForm(template:new ConsentFormTemplate(cdrUniqueId: "GEL") )
		//Mock the internal methods of the Service
		service.metaClass.getCDRClient   = {
			def client = new Object()
			client.metaClass.createOrUpdatePatientConsent = {
				String consent, String patient, KnownFacility receivingFacility, KnownOrganisation organisation, KnownPatientStatus consentStatus,Closure clsr ->
					throw new ClientException("Exception in calling CDR")
			}
			return client
		}
		service.metaClass.getCDRFacility = {new Object()}
		service.metaClass.findKnownOrganisation = {return KnownOrganisation.GEL_CSC_V1}
		service.metaClass.findKnownFacility = {return KnownFacility.TEST}
		service.metaClass.grailsApplication.getConfig = { [cdr:[knownFacility:"TEST",organisation:"Greenlight"] ]  }

		when:
		def result = service.sendConsentToCDR(nhsNumber,hospitalNumber,consentForm);

		then:
		result == "Exception in calling CDR"
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
