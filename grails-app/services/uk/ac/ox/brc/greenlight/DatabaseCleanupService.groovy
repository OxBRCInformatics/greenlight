package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional
import groovy.sql.Sql

@Transactional
class DatabaseCleanupService {

	def consentEvaluationService
	def dataSource
 	def cleanOrphanResponses()
	{
		//have to do this to cleanup the old records,
		//as we forgot to set all-delete-orphan in ConsentForm class
		//removes the orphan records in Response
		ConsentForm.list().collect().each { consentForm ->
			//Load all responses which are associated to this consent (actual and orphan) order 'desc' to get the latest ones at the beginning of the list
			def allResponses = Response.findAll("from Response as r where r.consentForm.id = :consentFormId order by r.id desc ",[consentFormId:consentForm.id]);

			//Get the latest responses (actual ones) which are associated to this consent
			def actualOnes = allResponses.subList(0,consentForm.responses.size())

			//Add the actual ones if they are not in the responses of the form
			actualOnes.collect().each { actualOne ->
				if(!consentForm.responses.contains(actualOne)){
					consentForm.addToResponses(actualOne)
					consentForm.save(flush: true)
				}
			}

			//exclude the latest ones (actual ones) from the list
			allResponses.removeAll(actualOnes)

			// remove orphans from database
			allResponses.collect().each {
				//GORM may add old objects to association. Not the latest ones! as we did not add cascade-all-delete before
				//so check it here and remove it from the consent.responses
				if(consentForm.responses.contains(it)){
					consentForm.removeFromResponses(it)
					consentForm.save()
				}
				//delete the orphan response
				else
					it.delete(flush: true)
			}
		}
	}

	def RemoveDuplicateConsentForm(){
		def sql = new Sql(dataSource)
		def attachmentIds = [:]
		def removedConsentForms = []
		//go through all consentForms
		sql.eachRow('select * from consent_form') {

			def consentFormId = it["id"]
			def attachmentId = it["attached_form_image_id"]

			//if this attachment is used before, so removed the consentFrom
			if (attachmentIds.containsKey(attachmentId)) {
				//Keep the first consentForm which has this attachmentId and
				//Remove the other consentForms and their responses which has the attachmentId
				ConsentForm.withTransaction { status ->
					try {
						//remove Responses of the consentForm
						def consent = ConsentForm.get(consentFormId)
						consent.responses.collect().each {
							consent.removeFromResponses(it)
						}
						//makes its attachment null
						consent.attachedFormImage = null
						consent.save(flush: true)

						//remove ConsentForm
						ConsentForm.where { id == consentFormId }.deleteAll()
						removedConsentForms.add(consentFormId)
					}
					catch(Exception exp){
						status.setRollbackOnly()
					}
				}

			} else {
				//a new attachment which is attached to a consentForm
				attachmentIds[attachmentId] = attachmentId
			}
		}
		removedConsentForms
	}

	def updateAllConsentStatus(){

		def updatedCount = 0
		//go through all ConsentForms and update its consentStatus
		ConsentForm.list().each { consentForm ->
			def consentStatus  = consentEvaluationService.getConsentStatus(consentForm)
			consentForm?.consentStatus = consentStatus
			consentForm.save(failOnError: true)
			updatedCount++
		}
		updatedCount
	}
}
