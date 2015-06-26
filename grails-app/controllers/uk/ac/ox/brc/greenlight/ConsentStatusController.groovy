package uk.ac.ox.brc.greenlight

import grails.plugin.springsecurity.annotation.Secured
import grails.rest.RestfulController

/**
 * API endpoint for retrieving the consent status for a patient. The patient
 * could be queried for by NHS number or hospital number.
 */
class ConsentStatusController{


	def consentFormService
	def consentEvaluationService
	def patientService

	/**
	 * Get the consent status for a patient. Initially looks up NHS number,
	 * and if that's invalid/doesn't return a value it will fall back to
	 * hospital number.
	 *
	 * This behaviour is to handle NHS barcode stickers gracefully, where the
	 * NHS and hospital numbers are next to each other.
	 */
	@Secured(['ROLE_API'])
	def getStatus() {

		def response = [_self: request.forwardURI]
		String lookupId = params.lookupId

		// If we don't have an ID to lookup, respond with an error
		if(!lookupId){
			response.errors = true
			response.message = "A lookup ID must be provided for 'lookupId'"
			respond response
			return
		}

		// Attempt to find all patient objects having this nhs number or hospitalNumber
		def patients = patientService.findAllByNHSOrHospitalNumber(lookupId)
		if(patients?.size() == 0){
			response.errors = true
			response.message = "The lookup ID could not be found"
		}else{
			// Patient exists, let's get the consents
			//so get these details from the first patient object
			//we assume that all the current patient objects with same nhsNumber
			//they all have the same name/family
			response.errors = false
			response.nhsNumber = patients[0]?.nhsNumber
			response.hospitalNumber = patients[0]?.hospitalNumber
			response.firstName = patients[0]?.givenName
			response.lastName = patients[0]?.familyName
			response.dateOfBirth = patients[0]?.dateOfBirth?.format("dd-MM-yyyy HH:mm:ss")
			response.consents = []

			def consents = consentFormService.getLatestConsentForms(patients)
			consents.each{ consentForm ->
				def consentStatusLabel = consentEvaluationService.getConsentLabels(consentForm)

				response.consents.push([
						form: [
						        name: consentForm.template.name,
								version: consentForm.template.templateVersion,
								namePrefix: consentForm.template.namePrefix
						],
						lastCompleted: consentForm.consentDate?.format("dd-MM-yyyy HH:mm:ss"),
						consentStatus: consentForm.consentStatus.name(),
						consentTakerName : consentForm.consentTakerName,
						consentFormId : consentForm.formID,
						consentStatusLabels: consentStatusLabel ? consentStatusLabel : ""
				])
			}
		}
		respond response
	}
}
