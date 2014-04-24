package uk.ac.ox.brc.greenlight

/**
 * This controller is used to clean orphan records from database
 * In previous version(1.1.0), a number of orphan responses where created
 * as relation between ConsentForm and Response didn't contain  cascade: 'all-delete-orphan'
 * cleanOrphanResponses action will remove orphan responses
 * and update latest responses for each consentForm
 */
class DatabaseCleanUpController {

	def databaseCleanupService

	def cleanOrphanResponses() {
		def responseStr = getResponsesStatusStr()

		try {
			databaseCleanupService.cleanOrphanResponses()
		}
		catch (Exception exception) {
			render exception.message
			return
		}

		responseStr = "Before<br>" + responseStr + "<br>After<br>"+ getResponsesStatusStr()
		render responseStr
	}

	private def getResponsesStatusStr()	{
		def allResponses = Response.count()
		def allConsentResponses = 0
		ConsentForm.list().each { consent->
			allConsentResponses += consent.responses.size()
		}
		return "AllResponses = ${allResponses}   allConsentResponses = ${allConsentResponses}"
	}
}