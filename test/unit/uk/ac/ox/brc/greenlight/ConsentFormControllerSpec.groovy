package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import greenlight.Study
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
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
		 1 * controller.consentFormService.getLatestConsentForms(_) >> {return completedForms}
		 1 * controller.studyService.getStudy() >> {return new Study(description:"New Study")}
		 controller.modelAndView.model.consents.size() == 2
		 controller.modelAndView.model.consents[0].form.name == "FORM1"
		 controller.modelAndView.model.consents[0].form.version == "12"
		 controller.modelAndView.model.consents[0].form.namePrefix == "fm1"
	 }

	def "showConsentFormByAccessGUID returns NOT FOUND if can't find consentForm based on accessGUID"(){

		given:"A consent already exists"

		when:"called without accessGUID parameter"
		controller.showConsentFormByAccessGUID()

		then:"returns NOT FOUND"
		controller.flash.error == "Not Found"
		controller?.modelAndView?.model == null

		when:"called for a GUID which is not available"
		controller.params["accessGUID"] = "NOT-AVAILABLE"
		controller.showConsentFormByAccessGUID()

		then:"returns NOT FOUND"
		1 * controller.consentFormService.searchByAccessGUID("NOT-AVAILABLE") >> {null}
		controller.flash.error == "Not Found"
		controller?.modelAndView?.model == null

		when:"called for a GUID which is available"
		controller.params.format = "html"
		controller.flash?.error = null
		controller.params["accessGUID"] = "123-456-789"
		controller.showConsentFormByAccessGUID()

		then:"returns the consent"
		1 * controller.consentFormService.searchByAccessGUID(_) >> {createConsent()}
		1 * controller.consentEvaluationService.getConsentLabels(_) >> { ["Do not contact","No incidental findings"]}
		controller.flash?.error == null
		controller.modelAndView.model.success == true
		controller.modelAndView.model.consent
				/*[
				id: null,
				consentDate: DateTimeFormat.forPattern("yyyy-MM-dd").print(new DateTime(2015,01,25,0,0)),
				consentTakerName: "Edward",
				formID: "GEN12345",
				comment: "TestComment",
				formStatus: ConsentForm.FormStatus.NORMAL.toString(),
				consentStatus: ConsentForm.ConsentStatus.FULL_CONSENT.toString(),
				consentStatusLabels : ["Do not contact","No incidental findings"],
				responses : [
							[answer:Response.ResponseValue.YES.toString(), ]
						new Response(answer: Response.ResponseValue.YES,question: new Question(name: 'I read1...')),
							 new Response(answer: Response.ResponseValue.YES,question: new Question(name: 'I read2...'))]
				,
				attachmentFileUrl: "1.jpg",
				patient: [
						id: null,
						givenName: "Eric",
						familyName: "Clapton",
						dateOfBirth: DateTimeFormat.forPattern("yyyy-MM-dd").print(new DateTime(1980,12,25,0,0)),
						hospitalNumber: "1002",
						nhsNumber: "1234567890",
				]
		]*/
	}

	private def createConsent(){
		def attachment= new Attachment(fileUrl: '1.jpg', dateOfUpload: new Date(),attachmentType: Attachment.AttachmentType.IMAGE, content: [])

		def question1 =  new Question(name: 'I read1...')
		def question2 =  new Question(name: 'I read2...')

		def template=new ConsentFormTemplate(
				name: "ORB1",
				templateVersion: "1.1",
				namePrefix: "GNR",questions: [question1,question2])

		def patient= new Patient(
				givenName: "Eric",
				familyName: "Clapton",
				dateOfBirth: new org.joda.time.DateTime(1980,12,25,0,0),
				hospitalNumber: "1002",
				nhsNumber: "1234567890",
				consents: []
		)
		def consent = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment,
				template: template,
				patient: patient,
				consentDate: new org.joda.time.DateTime(2015,01,25,0,0),
				consentTakerName: "Edward",
				formID: "GEN12345",
				formStatus: ConsentForm.FormStatus.NORMAL,
				consentStatus: ConsentForm.ConsentStatus.FULL_CONSENT,
				comment: "TestComment",
				responses: [
						new Response(answer: Response.ResponseValue.YES,question: question1),
						new Response(answer: Response.ResponseValue.YES,question: question2)
				]
		)
		consent
	}
}