package uk.ac.ox.brc.greenlight

import grails.plugin.springsecurity.annotation.Secured

class ConsentFormController {

	def consentEvaluationService
    def consentFormService
	def patientService
	def studyService
	def attachmentService


    def find()
    {
        def result= consentFormService.search(params);
        render view:"search", model:[consentForms:result]
    }

	def findAndExport()
	{
		def csvString = consentFormService.findAndExport(params);

		def fileName ="consentFormReport-"+(new Date()).format("dd-MM-yyyy HH:mm:ss")
		response.setHeader("Content-disposition", "attachment; filename=${fileName}.csv");
		render(contentType: "text/csv;charset=utf-8", text: csvString.toString());
	}

	def searchPatientConsentCount()
	{
		def result = consentFormService.getPatientWithMoreThanOneConsentForm();
		render view:"reportPatientConsentCount", model:[patients:result]
	}

	def exportPatientConsentCount (){

		def csvString = consentFormService.exportPatientWithMoreThanOneConsentForm()
		def fileName ="participants-moreThanOneConsent-"+(new Date()).format("dd-MM-yyyy")
		response.setHeader("Content-disposition", "attachment; filename=${fileName}.csv");
		render(contentType: "text/csv;charset=utf-8", text: csvString.toString());
	}

	/**
	 * Check the consent status for an NHS or hospital number.
	 * @return
	 */
	def checkConsent(){
		String lookupId = params.searchInput
		def model = [searchInput: lookupId]
		// If we don't have an ID to lookup, respond with an error
		if(!lookupId){
			model.errormsg = "A lookup ID must be provided for 'lookupId'"
		}else{
			// Attempt to find the patient
			def patients = patientService.findAllByNHSOrHospitalNumber(lookupId)
			if(patients?.size() == 0){
				model.errormsg = "The lookup ID "+ lookupId +" could not be found"
			}else{
				// Patient exists, let's get the consents
				def consents = consentFormService.getLatestConsentForms(patients)
				model.consents = []
				consents.each{ consentForm ->
					model.consents.push([
							form: [
									name: consentForm.template.name,
									version: consentForm.template.templateVersion,
									namePrefix: consentForm.template.namePrefix
							],
							lastCompleted: consentForm.consentDate,
							consentStatus: consentForm.consentStatus,
							labels: consentEvaluationService.getConsentLabels(consentForm)
					])
				}
			}
		}
		model.studies =  studyService.getStudy()?.description
		render view:"cuttingRoom", model: model
	}

	def export(){
        def csvString = consentFormService.exportAllConsentFormsToCSV()
        def fileName ="consentForms-"+(new Date()).format("dd-MM-yyyy")
        response.setHeader("Content-disposition", "attachment; filename=${fileName}.csv");
		render(contentType: "text/csv;charset=utf-8", text: csvString.toString());
    }

	@Secured(['permitAll'])
	def showConsentFormByAccessGUID() {
		def accessGUID = params["accessGUID"]
		def consent = consentFormService.searchByAccessGUID(accessGUID)
		if(!consent){
			flash.error = "Not Found"
			def result = [success: false, error: "Not Found", consent: null]
			respond result as Object, [model: result] as Map
			return
		}

		def responses = []
		consent?.responses?.each{ response ->
			responses.add(
					[
						question: response?.question?.name,
						answer: response?.answer?.toString(),
						optional: response?.question?.optional
					]
			)
		}

		def consentModel = [
				patient         : [
						nhsNumber     : consent?.patient?.nhsNumber,
						hospitalNumber: consent?.patient?.hospitalNumber,
						givenName     : consent?.patient?.givenName,
						familyName    : consent?.patient?.familyName,
						dateOfBirth   : consent?.patient?.dateOfBirth?.format("yyyy-MM-dd")
				],
				consentFormType: [
						name: consent?.template?.name,
						version: consent?.template?.templateVersion,
						namePrefix:consent?.template?.namePrefix,
				],
				formID          : consent?.formID,
				consentDate     : consent?.consentDate?.format("yyyy-MM-dd"),
				consentTakerName: consent?.consentTakerName,
				formStatus      : consent?.formStatus?.toString(),
				consentStatus   : consent?.consentStatus?.toString(),
				consentStatusLabels: consentEvaluationService.getConsentLabels(consent),
				comment         : consent?.comment,
				responses       : responses,
				attachment      : [
						dateOfUpload: consent?.attachedFormImage?.dateOfUpload.format("yyyy-MM-dd HH:mm:ss"),
						fileName    : attachmentService.getAttachmentFileName(consent?.attachedFormImage)
				]
		]

		/**
		 * if 'attachment' parameter is provided then return the Attachment file
		 */
		if(params["attachment"] != null){
			def filePath = attachmentService.getAttachmentFilePath(consent?.attachedFormImage)
			File file = new File(filePath)
			if (!file.exists()){
				flash.error = "Attachment not found"
				def result = [success: false, error: "Attachment not found", consent: null]
				respond result as Object, [model: result] as Map
				return
			}
			response.setContentType("application/octet-stream")
			response.setHeader("Content-disposition", "attachment; filename=\"${file.name}\"")
			response.outputStream << file.bytes
			response.outputStream.flush()
			response.outputStream.close()
			return null
		}else {
			def result = [success: true, error: null, consent: consentModel]
			respond result as Object, [model: result] as Map
			return
		}
	}
}