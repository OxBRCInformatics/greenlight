package uk.ac.ox.brc.greenlight

import grails.rest.RestfulController

/**
 * API endpoint for retrieving the consent status for a patient. The patient
 * could be queried for by NHS number or hospital number.
 */
class ConsentStatusController{

	static defaultAction = "getStatus"
	static allowedMethods = [ getStatus: "GET"]

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

		// Attempt to find the patient
		def patient = patientService.findByNHSOrHospitalNumber(lookupId)
		if(!patient){
			response.errors = true
			response.message = "The lookup ID could not be found"
		}else{
			// Patient exists, let's get the consents
			response.errors = false
			response.nhsNumber = patient.nhsNumber
			response.hospitalNumber = patient.hospitalNumber
			response.consents = []

			def consents = consentFormService.getLatestConsentForms(patient)
			consents.each{ consentForm ->
				response.consents.push([
						form: [
						        name: consentForm.template.name,
								version: consentForm.template.version,
						],
						lastCompleted: consentForm.consentDate,
						consentStatus: consentEvaluationService.getConsentStatus(consentForm).name()
				])
			}

		}
		respond response
	}
}
