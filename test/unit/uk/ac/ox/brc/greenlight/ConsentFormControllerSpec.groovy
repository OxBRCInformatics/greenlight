package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import greenlight.Study
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import spock.lang.Specification
import uk.ac.ox.brc.greenlight.Audit.RequestLog
import uk.ac.ox.brc.greenlight.Audit.RequestLogService

/**
 * Created by soheil on 01/04/2014.
 */
@TestFor(ConsentFormController)
class ConsentFormControllerSpec extends Specification{

    def setup()
    {
        controller.consentFormService = Mock(ConsentFormService)
		controller.attachmentService = Mock(AttachmentService)

		controller.requestLogService = Mock(RequestLogService)


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

		when:"called without accessGUID parameter"
		controller.showConsentFormByAccessGUID()

		then:"returns NOT FOUND"
		controller.flash.error == "Not Found"
		controller?.modelAndView?.model?.success == false
		controller?.modelAndView?.model?.error == "Not Found"
		!controller?.modelAndView?.model?.consent

		when:"called for a GUID which is not available"
		controller.params["accessGUID"] = "NOT-AVAILABLE"
		controller.showConsentFormByAccessGUID()

		then:"returns NOT FOUND"
		1 * controller.consentFormService.searchByAccessGUID("NOT-AVAILABLE") >> {null}
		controller.flash.error == "Not Found"
		controller?.modelAndView?.model?.success == false
		controller?.modelAndView?.model?.error   == "Not Found"
		!controller?.modelAndView?.model?.consent
	}

	def "showConsentFormByAccessGUID returns consent details"(){

		when:"called for a GUID which is available"
		controller.params.format = "html"
		controller.flash?.error = null
		controller.params["accessGUID"] = "123-456-789"
		controller.showConsentFormByAccessGUID()

		then:"returns consent"
		1 * controller.consentFormService.searchByAccessGUID(_) >> {createConsent()}
		1 * controller.consentEvaluationService.getConsentLabels(_) >> { ["Do not contact","No incidental findings"]}
		1 * controller.attachmentService.getAttachmentFileName(_) >> {"1.jpg"}
		controller.flash?.error == null
		controller.modelAndView.model.success == true
		controller.modelAndView.model.consent.patient == [
			givenName: "MrA",
			familyName: "MrB",
			dateOfBirth: DateTimeFormat.forPattern("dd/MM/yyyy").print(new DateTime(1980,12,25,0,0)),
			hospitalNumber: "1002",
			nhsNumber: "1234567890"
		]
		controller.modelAndView.model.consent.attachment == [
				dateOfUpload:DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").print(new DateTime(2015,8,19,0,0)),
				fileName:"1.jpg"
		]
		controller.modelAndView.model.consent.consentFormType == [
				name: "ORB1",
				version: "1.1",
				namePrefix: "GNR"
		]

		controller.modelAndView.model.consent.formID == "GEN12345"
		controller.modelAndView.model.consent.consentDate == DateTimeFormat.forPattern("dd/MM/yyyy").print(new DateTime(2015,1,25,0,0))
		controller.modelAndView.model.consent.consentTakerName == "ABC"
		controller.modelAndView.model.consent.formStatus      == ConsentForm.FormStatus.NORMAL.toString()
		controller.modelAndView.model.consent.consentStatus   == ConsentForm.ConsentStatus.FULL_CONSENT.toString()
		controller.modelAndView.model.consent.consentStatusLabels == ["Do not contact", "No incidental findings"]
		controller.modelAndView.model.consent.comment == "TestComment"

		controller.modelAndView.model.consent.responses[0].question == "I read1..."
		controller.modelAndView.model.consent.responses[0].answer   == Response.ResponseValue.YES.toString()
		controller.modelAndView.model.consent.responses[0].optional == false

		controller.modelAndView.model.consent.responses[1].question == "I read2..."
		controller.modelAndView.model.consent.responses[1].answer   == Response.ResponseValue.NO.toString()
		controller.modelAndView.model.consent.responses[1].optional == true
	}

	def "showConsentFormByAccessGUID returns consent details in JSON"(){

		when:"called for a GUID which is available"
		controller.request.method = "POST"
		controller.params.format = "json"
		controller.params["accessGUID"] = "123-456-789"
		controller.showConsentFormByAccessGUID()

		then:"returns consent"
		1 * controller.consentFormService.searchByAccessGUID(_) >> {createConsent()}
		1 * controller.consentEvaluationService.getConsentLabels(_) >> { ["Do not contact","No incidental findings"]}
		controller.flash?.error == null
		controller.response.json.success == true
		controller.response.json.consent
	}

	def "showConsentFormByAccessGUID is called and have attachment parameter"(){

		when:"called to return the attachment file, passing attachment param"
		controller.params.format = "html"
		controller.flash?.error = null
		controller.params["accessGUID"] = "123-456-789"
		controller.params["attachment"] = ""
		controller.showConsentFormByAccessGUID()

		then:"returns the attachment file"
		1 * controller.consentFormService.searchByAccessGUID(_) >> {
			def consent = createConsent()
			consent.id = 1
			return consent
		}

		1 * controller.consentEvaluationService.getConsentLabels(_) >> { ["Do not contact","No incidental findings"]}
		1 * controller.attachmentService.getAttachmentFilePath(_) >> {"test/resources/1.jpg"}

		controller.flash?.error == null
		controller.response.contentType == "application/octet-stream"
		controller.response.header("Content-disposition") == "attachment; filename=\"1.jpg\""
		controller.response.outputStream
	}

	def "showConsentFormByAccessGUID is called and have attachment parameter but file does not exist"(){

		when:"called to return the attachment file, passing attachment param"
		controller.params.format = "html"
		controller.flash?.error = null
		controller.params["accessGUID"] = "123-456-789"
		controller.params["attachment"] = ""
		controller.showConsentFormByAccessGUID()

		then:"returns the attachment file"
		1 * controller.consentFormService.searchByAccessGUID(_) >> {
			def consent = createConsent()
			consent.id = 1
			return consent
		}
		1 * controller.consentEvaluationService.getConsentLabels(_) >> { ["Do not contact","No incidental findings"]}
		1 * controller.attachmentService.getAttachmentFilePath(_) >> {"test/resources/NOT_AVAILABLE_FILE.jpg"}

		controller.flash?.error == "Attachment not found"
		controller.modelAndView.model.success == false
		controller.modelAndView.model.error == "Attachment not found"
		!controller.modelAndView.model.consent
	}

	private def createConsent() {
		def attachment = new Attachment(fileUrl: '1.jpg', dateOfUpload: new DateTime(2015, 8, 19, 0, 0), attachmentType: Attachment.AttachmentType.IMAGE, content: [])
		attachment.id = 1

		def question1 = new Question(name: 'I read1...', optional: false, defaultResponse: Response.ResponseValue.YES, validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO])
		def question2 = new Question(name: 'I read2...', optional: true, defaultResponse: Response.ResponseValue.YES, validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO])

		def template = new ConsentFormTemplate(
				name: "ORB1",
				templateVersion: "1.1",
				namePrefix: "GNR", questions: [question1, question2])

		def patient = new Patient(
				givenName: "MrA",
				familyName: "MrB",
				dateOfBirth: new org.joda.time.DateTime(1980, 12, 25, 0, 0),
				hospitalNumber: "1002",
				nhsNumber: "1234567890",
				consents: []
		)
		def consent = new ConsentForm(
				accessGUID: "123-456-789",
				attachedFormImage: attachment,
				template: template,
				patient: patient,
				consentDate: new org.joda.time.DateTime(2015, 1, 25, 0, 0),
				consentTakerName: "ABC",
				formID: "GEN12345",
				formStatus: ConsentForm.FormStatus.NORMAL,
				consentStatus: ConsentForm.ConsentStatus.FULL_CONSENT,
				comment: "TestComment",
				responses: [
						new Response(answer: Response.ResponseValue.YES, question: question1),
						new Response(answer: Response.ResponseValue.NO, question: question2)
				]
		)
		consent
	}
	def "checkConsent will call RequestLogService to save details of the request"()
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

		def lookupId = "nhsNumber"

		when:"available nhsNumber is provided"
		controller.params.searchInput = lookupId
		controller.checkConsent()

		then:"patient consent forms are returned"
		1 * controller.consentFormService.getLatestConsentForms(_) >> {return completedForms}
		1 * controller.requestLogService.add("nhsNumber",_,RequestLog.RequestType.CutUpRoom) >> { }
		controller.modelAndView.model.consents.size() == 2
	}
}