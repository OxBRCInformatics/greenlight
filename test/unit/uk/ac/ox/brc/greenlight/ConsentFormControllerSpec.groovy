package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import greenlight.Study
import spock.lang.Specification

/**
 * Created by soheil on 01/04/2014.
 */
@TestFor(ConsentFormController)
class ConsentFormControllerSpec extends Specification{

    def setup()
    {
        controller.consentFormService = Mock(ConsentFormService)


        controller.patientService = Mock(PatientService)
		controller.studyService = Mock(StudyService)
		controller.consentEvaluationService = Mock(ConsentEvaluationService)
    }

    def "Calling export action will return csv file"()
    {
        when:"calling export action"
        controller.export()

        then:"consentFormService exportAllConsentFormsToCSV method should be called once"
        1 * controller.consentFormService.exportAllConsentFormsToCSV() >>{ return "header1,header2 \r\n data1,data2" }


        then:"it returns a csv contentType"
        controller.response.contentType == "text/csv;charset=utf-8"
    }

    def "Calling export action will return csv file with a correct Name"()
    {
        when:"calling export action"
        def fileName ="consentForms-"+(new Date()).format("dd-MM-yyyy")
        controller.export()

        then:"consentFormService exportAllConsentFormsToCSV method should be called once"
        1 * controller.consentFormService.exportAllConsentFormsToCSV() >>{ return "header1,header2 \r\n data1,data2" }


        then:"it returns a csv file with a correct name"
        controller.response.header("Content-disposition") == "attachment; filename=${fileName}.csv"
    }


	 def "checkConsent will return all consents for an specific patient"()
	 {
		 given:
		 def formTemplates = [
				 new ConsentFormTemplate(name: "FORM1", namePrefix: "fm1", templateVersion: "12", questions: []),
				 new ConsentFormTemplate(name: "FORM2", namePrefix: "fm2", templateVersion: "12", questions: [])
		 ]

		 def now = new Date()
		 def completedForms = [
				 new ConsentForm(template: formTemplates[0], consentDate: now-14), // 2 weeks ago
				 new ConsentForm(template: formTemplates[1], consentDate: now-1 ), // 1 day ago
		 ]

		 when:"No parameter is passed"
		 controller.checkConsent()

		 then:"error message will be returned"
		 controller.modelAndView.model.errormsg == "A lookup ID must be provided for 'lookupId'"


		 when:"Unavailable nhsNumber is passed"
		 def lookupId = "nhsNumber"
		 controller.params.searchInput = lookupId
		 controller.checkConsent()

		 then:"error message will be returned"
		 1 * controller.patientService.findAllByNHSOrHospitalNumber(_) >> {return []}
		 1 * controller.studyService.getStudy() >> {return new Study(description:"New Study")}
		 controller.modelAndView.model.errormsg == "The lookup ID "+ lookupId +" could not be found"


		 when:"available nhsNumber is provided"
		 controller.params.searchInput = lookupId
		 controller.checkConsent()

		 then:"patient consent forms are returned"
		 1 * controller.patientService.findAllByNHSOrHospitalNumber(_) >> {return [new Patient(nhsNumber: lookupId)]}
		 1 * controller.consentFormService.getLatestConsentForms(_,null) >> {return completedForms}
		 1 * controller.studyService.getStudy() >> {return new Study(description:"New Study")}
		 controller.modelAndView.model.consents.size() == 2
		 controller.modelAndView.model.consents[0].form.name == "FORM1"
		 controller.modelAndView.model.consents[0].form.version == "12"
		 controller.modelAndView.model.consents[0].form.namePrefix == "fm1"
	 }
}