package uk.ac.ox.brc.greenlight

import com.mirth.results.models.AttachmentModel
import grails.transaction.Transactional
import uk.ac.ox.ndm.mirth.datamodel.dsl.clinical.patient.Consent
import uk.ac.ox.ndm.mirth.datamodel.dsl.core.Facility
import uk.ac.ox.ndm.mirth.datamodel.exception.rest.ClientException
import uk.ac.ox.ndm.mirth.datamodel.rest.client.KnownFacility
import uk.ac.ox.ndm.mirth.datamodel.rest.client.KnownOrganisation
import uk.ac.ox.ndm.mirth.datamodel.rest.client.MirthRestClient

/**
 * added by Soheil on 18.08.2015
 * This service will pass consentForms into CDR (Clinical Document Repository)
 */

@Transactional
class CDRService {
	def grailsApplication




	def saveOrUpdateConsentForm(Patient patient,ConsentForm consentForm) {

		//if patient has a generic nhsNumber then send '??????????' to CDR
		def nhsNumber = patient.nhsNumber
		if(patient.nhsNumber == '1111111111' || patient.nhsNumber?.isEmpty() || !patient.nhsNumber){
			nhsNumber = '??????????'
		}

		def mrnNumber = patient.hospitalNumber
		if(patient.hospitalNumber?.isEmpty() || !patient.hospitalNumber){
			mrnNumber = '???'
		}

		//if Consent or Patient are in update mode and
		//consent or patient are updated
		if(consentForm.id && patient.id && (consentForm.isChanged() || patient.isDirty())) {

			//if patientDetails are changed
			if(patient.isDirty()){
				//remove oldConsent from CDR
				//add the new consent into CDR
			}
			//if consentFormDetails are changed
			else if(consentForm.isChanged()) {

				//if formTemplate is changed
				if (consentForm.getPersistentValue("template")?.id != consentForm.template.id) {
					//remove oldConsent from CDR
					//add the new consent into CDR
				}
				//oldConsentForm formStatus was NORMAL and now it is changed to DECLINED or SPOILED, so make the old one DECLINED or SPOILED
				else if (consentForm.getPersistentValue("formStatus")  == ConsentForm.FormStatus.NORMAL &&
						 consentForm.formStatus != ConsentForm.FormStatus.NORMAL) {
					//so make the old one DECLINED or SPOILED
					//add the new consent into CDR

				} //oldConsentForm formStatus was NOT NORMAL and now it is NORMAL, so send it
				else if (consentForm.getPersistentValue("formStatus") != ConsentForm.FormStatus.NORMAL &&
						 consentForm.formStatus == ConsentForm.FormStatus.NORMAL) {
					//add the new consent into CDR
				}
					//just other fields are updated, such as responses, so just send it to CDR 'again' & it will update it automatically
				else {

					//add the new consent into CDR
				}
			}
		}//it is a NEW consentForm with a new Patient
		else if (!consentForm.id && !patient.id){
			return sendConsentToCDR(nhsNumber,mrnNumber, consentForm)
		}
	}

	private def expireConsentForm(ConsentForm consentForm){

	}

	private def deleteConsentForm(ConsentForm  consentForm){

	}

	private def sendConsentToCDR(nhsNumber,hospitalNumber, consentForm){

		
		return "success"
	}

	private def getCDRClient(){
		def cdrAccessConfig  = grailsApplication.config?.cdr?.access
		if(!cdrAccessConfig){
			throw new Exception("cdr.access Config is not defined in config file")
		}
		return new MirthRestClient(cdrAccessConfig.username, cdrAccessConfig.password)
	}

	private def getCDRFacility(){
		def cdrFacilityConfig  = grailsApplication.config?.cdr?.facility
		if(!cdrFacilityConfig){
			throw new Exception("cdr.facility Config is not defined in config file")
		}
		Facility greenlight = facility {
			id cdrFacilityConfig.id
			name cdrFacilityConfig.name
			description cdrFacilityConfig.description
		} as Facility
		return greenlight
	}

	 def findKnownOrganisation(cdrUniqueId){
		 def result
		 KnownOrganisation.values().each { value ->
			 if(value.name() == cdrUniqueId) {
				 result = value
				 return
			 }
		 }
		 result
	}

	 def findKnownFacility(name){
		def result
		 KnownFacility.values().each { value ->
			 if(value.name() == name) {
				 result = value
				 return
			 }
		 }
		result
	}

	def findKnownPatientStatus(consentStatus){
		switch (consentStatus){
			case ConsentForm.ConsentStatus.FULL_CONSENT:
				return KnownPatientStatus.CONSENTED;
			case ConsentForm.ConsentStatus.CONSENT_WITH_LABELS:
				return KnownPatientStatus.RESTRICTED_CONSENT;
			case ConsentForm.ConsentStatus.NON_CONSENT:
				return KnownPatientStatus.NON_CONSENT;
			default:
				return null;
		}
	}
}
