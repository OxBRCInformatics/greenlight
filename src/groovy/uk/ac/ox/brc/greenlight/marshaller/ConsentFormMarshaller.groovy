package uk.ac.ox.brc.greenlight.marshaller


import grails.converters.JSON
import uk.ac.ox.brc.greenlight.ConsentForm
import uk.ac.ox.brc.greenlight.ConsentFormTemplate
import uk.ac.ox.brc.greenlight.Patient
import uk.ac.ox.brc.greenlight.Response

/**
 * Created by soheil on 23/04/2014.
 */
class ConsentFormMarshaller {
	void register() {
		JSON.registerObjectMarshaller(ConsentForm) { consentForm ->
			return [
					id : consentForm.id,
					template : consentForm.template,
					consentDate : consentForm.consentDate.format("yyyy-MM-dd"),
					consentTakerName : consentForm.consentTakerName,
					formID : consentForm.formID,
					formStatus : consentForm.formStatus.toString(),
					consentStatus: consentForm.consentStatus.toString(),
					comment : consentForm.comment,
					patient : consentForm.patient,
					attachment: consentForm.attachedFormImage
					]
		}
	}
}
