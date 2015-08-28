package uk.ac.ox.brc.greenlight

import com.mirth.results.models.AttachmentModel
import grails.transaction.Transactional
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
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

	def saveOrUpdateConsentForm(ConsentForm consentForm) {

		ConsentForm oldConsentForm
		//it is in Update mode
		if(consentForm.id){
			oldConsentForm = ConsentForm.get(consentForm.id)
		}

		//if it is an Update for a previously saved consentForm
		if(oldConsentForm) {

			//if patientDetails are changed
			if(!oldConsentForm.patient.equals(consentForm.patient)){

				//remove oldConsent from CDR

				//add the new consent into CDR

			}
			//if consentFormDetails are changed
			else if(!oldConsentForm.equals(consentForm)) {

				//if formTemplate is changed
				if (oldConsentForm.template.id != consentForm.template.id) {

					//remove oldConsent from CDR

					//add the new consent into CDR

				}
				//oldConsentForm formStatus was NORMAL and now it is changed to DECLINED or SPOILED, so make the old one DECLINED or SPOILED
				else if (oldConsentForm.formStatus  == ConsentForm.FormStatus.NORMAL &&
						 consentForm.formStatus != ConsentForm.FormStatus.NORMAL) {
					//add the new consent into CDR (so make the old one DECLINED or SPOILED)

				} //oldConsentForm formStatus was NOT NORMAL and now it is NORMAL, so send it
				else if (oldConsentForm.formStatus != ConsentForm.FormStatus.NORMAL &&
						consentForm.formStatus == ConsentForm.FormStatus.NORMAL) {
					//add the new consent into CDR (so make the old one NORMAL)
				}
					//just other fields are updated, so just send it to CDR again
				else {

					//add the new consent into CDR
				}
			}
		}//it is NEW consentForm and not sent to CDR before
		else {
			//add it into CDR
		}
	}

	def expireConsentForm(ConsentForm consentForm){

	}

	def deleteConsentForm(ConsentForm  consentForm){

	}

	private def sendConsentToCDR(ConsentForm consentForm){

	}
}
