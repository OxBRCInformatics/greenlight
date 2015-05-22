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

		def result = [before: responseStr, after: getResponsesStatusStr()]
		respond result as Object, [formats:['xml','json']] as Map
	}

	private def getResponsesStatusStr()	{
		def allResponses = Response.count()
		def allConsentResponses = 0
		ConsentForm.list().each { consent->
			allConsentResponses += consent.responses.size()
		}
		return "AllResponses = ${allResponses}   allConsentResponses = ${allConsentResponses}"
	}

	private def getConsentFormStatus(){
		def consentFormCount = ConsentForm.count()
		def attachmentCount  = Attachment.count()
		def responsesCount   = Response.count()
		return "AllAttachments = ${attachmentCount}   allConsentForms = ${consentFormCount} allResponses=${responsesCount}"
	}

	def RemoveDuplicateConsentForm(){

		def removed
		def before = getConsentFormStatus()
		try {
			removed = databaseCleanupService.RemoveDuplicateConsentForm()
		}
		catch(Exception exception){
			render exception.message
			return
		}

		def after = getConsentFormStatus()

		def result = [before: before, after: after,removed: removed]
		respond result as Object, [formats:['xml','json']] as Map
	}

	def updateAllConsentStatus() {
		def consentFormUpdatedCount = 0

		try {
			consentFormUpdatedCount = databaseCleanupService.updateAllConsentStatus()
		}
		catch (Exception exception) {
			render exception.message
			return
		}

		def result = [updatedRecords: consentFormUpdatedCount]
		respond result as Object, [formats:['xml','json']] as Map	}
}