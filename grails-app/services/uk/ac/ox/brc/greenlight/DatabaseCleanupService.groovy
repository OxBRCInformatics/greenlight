package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional
import groovy.sql.Sql

@Transactional
class DatabaseCleanupService {

	def consentEvaluationService
	def patientService

	def dataSource

	def cleanOrphanResponses() {
		//have to do this to cleanup the old records,
		//as we forgot to set all-delete-orphan in ConsentForm class
		//removes the orphan records in Response
		ConsentForm.list().collect().each { consentForm ->
			//Load all responses which are associated to this consent (actual and orphan) order 'desc' to get the latest ones at the beginning of the list
			def allResponses = Response.findAll("from Response as r where r.consentForm.id = :consentFormId order by r.id desc ", [consentFormId: consentForm.id]);

			//Get the latest responses (actual ones) which are associated to this consent
			def actualOnes = allResponses.subList(0, consentForm.responses.size())

			//Add the actual ones if they are not in the responses of the form
			actualOnes.collect().each { actualOne ->
				if (!consentForm.responses.contains(actualOne)) {
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
				if (consentForm.responses.contains(it)) {
					consentForm.removeFromResponses(it)
					consentForm.save()
				}
				//delete the orphan response
				else
					it.delete(flush: true)
			}
		}
	}

	def RemoveDuplicateConsentForm() {
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
					catch (Exception exp) {
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

	def updateAllConsentStatus() {

		def updatedCount = 0
		//go through all ConsentForms and update its consentStatus
		ConsentForm.list().each { consentForm ->
			def consentStatus = consentEvaluationService.getConsentStatus(consentForm)
			consentForm?.consentStatus = consentStatus
			consentForm.save(failOnError: true)
			updatedCount++
		}
		updatedCount
	}

	//helpful Queries
	def patientDBReport() {

		def result = [:]
		result.put('PatientCount', Patient.count())

		result.put('ConsentFormCount', ConsentForm.count())


 		def emptyHospitalNumber = Patient.createCriteria().list {
			isNull('hospitalNumber')
			projections {
				property('id', 'id')
			}
		}
		result.put('NullHospitalNumber', emptyHospitalNumber)


		def emptyNHSNumber = Patient.createCriteria().list {
			isNull('nhsNumber')
			projections {
				property('id', 'id')
			}
		}
		result.put('emptyNHSNumber', emptyNHSNumber)



		def emptyGivenName = Patient.createCriteria().list {
			isNull("givenName")
			projections {
				property('id', 'id')
			}
		}
		result.put('NullGivenName', emptyGivenName)

		def emptyFamilyName = Patient.createCriteria().list {
			isNull("familyName")
			projections {
				property('id', 'id')
			}
		}
		result.put('NullFamilyName', emptyFamilyName)


		def patientWithGenericNHSNumber = Patient.createCriteria().list {
			eq('nhsNumber','1111111111')
			projections {
				property('id', 'id')
			}
		}
		result.put('patientWithGenericNHSNumber', patientWithGenericNHSNumber)

		def patientWithGenericNHSNumberAndDifferentMRN = Patient.executeQuery("select hospitalNumber,count(*) from Patient where nhsNumber='1111111111' group by hospitalNumber ")
		result.put('patientWithGenericNHSNumberAndDifferentMRN', patientWithGenericNHSNumberAndDifferentMRN)


		def nhsNumberMoreThanOne = Patient.executeQuery("select nhsNumber , count(*) from Patient group by nhsNumber having count(*) > 1")
		result.put("nhsNumberMoreThanOne", nhsNumberMoreThanOne)


		def nhsNumberWithMoreThanOneMRN = [:]
		def groupedPatients = patientService.groupPatientsByNHSNumber()
		groupedPatients.each { nhsNumber ->
			def hospitalNumbers = Patient.executeQuery("select hospitalNumber from Patient as p where p.nhsNumber='${nhsNumber}' group by hospitalNumber")
			if (hospitalNumbers.size() > 1) {
				nhsNumberWithMoreThanOneMRN.put(nhsNumber, hospitalNumbers)
			}
		}
		result.put('nhsNumberWithMoreThanOneMRN', nhsNumberWithMoreThanOneMRN)



		def nhsNumberWithMoreThanOneConsentOfOneType = [:]
		groupedPatients.each { num ->
			def res = ConsentForm.executeQuery("select cn.template.namePrefix,count(*) from ConsentForm as cn where cn.patient.nhsNumber='${num}' group by cn.template.namePrefix having count(*)>1")
			if (res.size() > 0) {
				nhsNumberWithMoreThanOneConsentOfOneType.put(num, res)
			}
		}
		result.put('nhsNumberWithMoreThanOneConsentOfOneType', nhsNumberWithMoreThanOneConsentOfOneType)



		def nhsNumberWithMoreThanOneDOB = [:]
		groupedPatients.each { nhsNumber ->
			def dobs = Patient.executeQuery("select dateOfBirth from Patient as p where p.nhsNumber='${nhsNumber}' group by dateOfBirth")
			if (dobs.size() > 1) {
				nhsNumberWithMoreThanOneDOB.put(nhsNumber, dobs)
			}
		}
		result.put('nhsNumberWithMoreThanOneDOB', nhsNumberWithMoreThanOneDOB)

		def patientObjectsWithEmptyConsent = Patient.executeQuery("select p.id,p.nhsNumber from Patient as p where p.consents is empty")
		result.put('patientObjectsWithEmptyConsent', patientObjectsWithEmptyConsent)


		def consentWithGenericFormID = ConsentForm.createCriteria().list {
			like("formID","%00000")
			projections {
				property('id', 'id')
			}
		}
		result.put('consentWithGenericFormID', consentWithGenericFormID)


		def GELConsentsV1 = ConsentForm.executeQuery("select c.id,c.formStatus from ConsentForm as c where c.template.namePrefix='GEL' and c.template.templateVersion='Version 1.0 dated  25.08.2014'")
		def GELConsentsV2 = ConsentForm.executeQuery("select c.id,c.formStatus from ConsentForm as c where c.template.namePrefix='GEL' and c.template.templateVersion='Version 2 dated 14.10.14'")
		result.put('GELConsentsV1', GELConsentsV1)
		result.put('GELConsentsV2', GELConsentsV2)

		result
	}


	def databaseStatusReports(){

		def result = [:]
		result.put('ConsentFormCount', ConsentForm.count())

		//ConsentForms with Empty Fields
		def consentFormsWithEmptyFields = ConsentForm.executeQuery("select c.formID,c.patient.nhsNumber,c.patient.familyName,c.patient.givenName,c.patient.hospitalNumber from ConsentForm as c where (c.patient.nhsNumber IS NULL) OR (c.patient.hospitalNumber IS NULL) OR (c.patient.givenName IS NULL) OR (c.patient.familyName IS NULL)")
		result.put('consentFormsWithEmptyFields', consentFormsWithEmptyFields)

		//ConsentForms with Generic IDs
		def consentFormsWithGenericIDs = ConsentForm.executeQuery("select c.formID,c.patient.nhsNumber,c.patient.familyName,c.patient.givenName, c.patient.hospitalNumber from ConsentForm as c where c.patient.nhsNumber in ('1111111111','0000000000')")
		result.put('consentFormWithGenericIDs', consentFormsWithGenericIDs)


		//NSHNumber with more than one DOB
		def nhsNumberWithMoreThanOneDOB = [:]
		def groupedPatientsByNHSNumber = patientService.groupPatientsByNHSNumber()
		groupedPatientsByNHSNumber.each { nhsNumber ->
			if(nhsNumber !='1111111111' && nhsNumber!='0000000000') {
				def dobs = Patient.executeQuery("select dateOfBirth from Patient as p where p.nhsNumber='${nhsNumber}' and p.consents is not empty  group by dateOfBirth")
				def dobStr = ""
				if (dobs.size() > 1) {
					dobStr = dobs.join(",").replace('00:00:00.0', '')
					nhsNumberWithMoreThanOneDOB.put(nhsNumber, dobStr)
				}
			}
		}
		result.put('nhsNumberWithMoreThanOneDOB', nhsNumberWithMoreThanOneDOB)


		//HospitalNumber with more than one DOB
		def hospitalNumberWithMoreThanOneDOB = [:]
		def groupedPatientsByHospitalNumber = patientService.groupPatientsByHospitalNumber()
		groupedPatientsByHospitalNumber.each { hospitalNumber ->
			def dobs = Patient.executeQuery("select dateOfBirth from Patient as p where p.hospitalNumber='${hospitalNumber}' and p.consents is not empty   group by dateOfBirth")
			def dobStr = ""
			if (dobs.size() > 1) {
				dobStr = dobs.join(",").replace('00:00:00.0','')
				hospitalNumberWithMoreThanOneDOB.put(hospitalNumber, dobStr)
			}
		}
		result.put('hospitalNumberWithMoreThanOneDOB', hospitalNumberWithMoreThanOneDOB)

		result
	}


	def addDefaultValidResponses(){

		def updatedCount = 0
		Question.list().each { question ->
			//If it is Empty or Null, update it
			if(!question.validResponses) {
				updatedCount++
				question.addToValidResponses(Response.ResponseValue.YES)
				question.addToValidResponses(Response.ResponseValue.NO)
				question.addToValidResponses(Response.ResponseValue.BLANK)
				question.addToValidResponses(Response.ResponseValue.AMBIGUOUS)

				question.defaultResponse = Response.ResponseValue.BLANK
				question.save(failOnError: true, flush: true)
			}
		}
		updatedCount
	}


	def updateConsentTemplateVersion(){

		def orbTemplate = ConsentFormTemplate.findByNameAndTemplateVersion("Pre-2014 ORB consent form","Version 1.2 dated 3rd March 2009")
		if(orbTemplate){
			orbTemplate.templateVersion = "Version 1.2 dated 03.03.2009"
			orbTemplate.save(flush: true)
		}

		def gelTemp = ConsentFormTemplate.findByNameAndTemplateVersion("100,000 Genomes Project – Cancer Sequencing Consent Form","Version 2 dated 14.10.14")
		if(gelTemp){
			gelTemp.templateVersion = "Version 2 dated 14.10.2014"
			gelTemp.save(flush: true)
		}

		def gelTemp2 = ConsentFormTemplate.findByNameAndTemplateVersion("100,000 Genomes Project – Cancer Sequencing Consent Form","Version 1.0 dated  25.08.2014")
		if(gelTemp2){
			gelTemp2.templateVersion = "Version 1.0 dated 25.08.2014"
			gelTemp2.save(flush: true)
		}
	}


	def updateCDRUniqueId() {

		def formTemplate = ConsentFormTemplate.findByNameAndTemplateVersion("ORB General Consent Form", "v1 October 2013")
		if (formTemplate) {
			formTemplate.cdrUniqueId = "ORB_GEN_V1"
			formTemplate.save(flush: true)
		}

		formTemplate = ConsentFormTemplate.findByNameAndTemplateVersion("ORB Specific Programme Clinically Relevant Genomics - Oncology Consent Form for Adults", "v1 October 2013")
		if (formTemplate) {
			formTemplate.cdrUniqueId = "ORB_CRA_V1"
			formTemplate.save(flush: true)
		}

		formTemplate = ConsentFormTemplate.findByNameAndTemplateVersion("100,000 Genomes Project – Cancer Sequencing Consent Form", "Version 1.0 dated 25.08.2014")
		if (formTemplate) {
			formTemplate.cdrUniqueId = "GEL_CSC_V1"
			formTemplate.save(flush: true)
		}

		formTemplate = ConsentFormTemplate.findByNameAndTemplateVersion("100,000 Genomes Project – Cancer Sequencing Consent Form", "Version 2 dated 14.10.2014")
		if (formTemplate) {
			formTemplate.cdrUniqueId = "GEL_CSC_V2"
			formTemplate.save(flush: true)
		}

		formTemplate = ConsentFormTemplate.findByNameAndTemplateVersion("Pre-2014 ORB consent form", "Version 1.2 dated 03.03.2009")
		if (formTemplate) {
			formTemplate.cdrUniqueId = "ORB_PRE_V1_2"
			formTemplate.save(flush: true)
		}

		formTemplate = ConsentFormTemplate.findByNameAndTemplateVersion("ORB General Consent Form", "v2 April 2014")
		if (formTemplate) {
			formTemplate.cdrUniqueId = "ORB_GEN_V2"
			formTemplate.save(flush: true)
		}
	}


	def addAccessGUIDtoConsentForms(){

		def total = ConsentForm.count()
		//check if consentForms don't have accessGUID (a proper GUID), then update them
		def consentsWithAccessGUID = ConsentForm.executeQuery("from ConsentForm as c where c.accessGUID like '%-%-%-%'")
		if(consentsWithAccessGUID.size() != 0){
			return [total:total,updated:0]
		}

		//update all accessGUIDs
		def updated = 0
		ConsentForm.list().each { consent ->
			consent.accessGUID = UUID.randomUUID().toString()
			consent.save(flush:true,failOnError: true)
			updated++
		}
		return [total:total,updated:updated]
	}

}