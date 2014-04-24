package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional

class DatabaseCleanUpController {

	def databaseCleanupService

	def cleanOrphanResponses()
	{
		def responseStr = getResponsesStatusStr()

		try {
			databaseCleanupService.cleanOrphanResponses()
		}
		catch (Exception exception)
		{
			render exception.message
			return
		}

		responseStr = "Before<br>" + responseStr + "<br>After<br>"+ getResponsesStatusStr()
		render responseStr
	}

	private def getResponsesStatusStr()
	{
		def allResponses = Response.count()
		def allConsentResponses = 0
		ConsentForm.list().each { consent->
			allConsentResponses += consent.responses.size()
		}
		return "AllResponses = ${allResponses}   allConsentResponses = ${allConsentResponses}"
	}
}