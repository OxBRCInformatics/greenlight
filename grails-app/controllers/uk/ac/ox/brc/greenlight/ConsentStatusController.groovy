package uk.ac.ox.brc.greenlight

/**
 * API endpoint for retrieving the consent status for a patient. The patient
 * could be queried for by NHS number or hospital number.
 */
class ConsentStatusController {

	static defaultAction = "getStatus"
	static allowedMethods = [ getStatus: "GET"]

	def consentStatusService

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
		def lookupId = params.lookupId

		// If we don't have an ID to lookup, respond with an error
		if(!lookupId){
			response.errors = true
			response.message = "A lookup ID must be provided for 'lookupId'"
			respond response
			return
		}

		// We entrust lookups to the consent status service
		def consentStatus = consentStatusService.getConsentStatus(lookupId)
		if(!consentStatus){
			response.errors = true
			response.message = "The lookup ID could not be found"
		}else{
			response.errors = false
			response.nhsNumber = "1234"
			response.consent = consentStatus
		}
		respond response
	}
}
