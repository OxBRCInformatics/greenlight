package uk.ac.ox.brc.greenlight

class ConsentFormController {

	def consentEvaluationService
    def consentFormService
	def patientService
	def studyService


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
							formID: consentForm.formID,
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
}
