package uk.ac.ox.brc.greenlight

import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import org.json.JSONObject
import grails.converters.JSON
import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

import java.sql.Timestamp


class ConsentFormCompletionControllerSpec extends IntegrationSpec {

    def consentFormController =new ConsentFormCompletionController()
	def grailsLinkGenerator

    def setup() {
        def attachment1= new Attachment( fileName: 'a.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flash: true)
        def attachment2=  new Attachment(fileName: 'a.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flash: true)

		//As we need to also mock the service which is used inside the controller
		//so we need to add the following line
		consentFormController.demographicService = Mock(DemographicService)

		def template1=new ConsentFormTemplate(
                name: "ORB1",
                templateVersion: "1.1",
                namePrefix: "GNR",
        ).addToQuestions(new Question(name: 'I read1...')
        ).addToQuestions(new Question(name: 'I read1...')
        ).addToQuestions(new Question(name: 'I read1...')
        ).addToQuestions(new Question(name: 'I read1...')
        ).addToQuestions(new Question(name: 'I read1...')
        ).addToQuestions(new Question(name: 'I read1...')
        ).save(flash: true)

        def template2=new ConsentFormTemplate(
                name: "ORB2",
                namePrefix: "GNR",
                templateVersion: "1.1"
        ).addToQuestions(new Question(name: 'I read2...')
        ).addToQuestions(new Question(name: 'I read2...')
        ).addToQuestions(new Question(name: 'I read2...')
        ).save(flash: true)



        def eric = new Patient(
                givenName: "Eric",
                familyName: "Clapton",
                dateOfBirth: new Date("30/03/1945"),
                hospitalNumber: "1001",
                nhsNumber: "123-456-7891",
                consents: []
        ).save(flash: true)



        def consentForm = new ConsentForm([
                attachedFormImage: attachment1,
                template:template1,
                consentDate: new Date(),
                consentTakerName:"ABC",
                formID:"GEN12345",
                formStatus : ConsentForm.FormStatus.NORMAL,
                responses:[],
                patient: eric
        ]).save(flash:true)
    }

    private void initParams(consentFormTemplate,attachment,formId)
    {
        consentFormController.params['questionsSize'] = consentFormTemplate.questions.size();
        consentFormTemplate.getQuestions().eachWithIndex() { obj,index ->
            consentFormController.params["responses.${index}"] = Response.ResponseValue.YES;
        }
        consentFormController.params['commandInstance'] = [
                patient: [
                        givenName: 'givName1',
                        familyName: 'familyName1',
                        dateOfBirth_year: "2014",
                        dateOfBirth_month: "1",
                        dateOfBirth_day: "1",

                        nhsNumber: '1234567890',
                        hospitalNumber: '123'
                ],
                consentForm: [
                        consentDate_year:"2014",
                        consentDate_month:"1",
                        consentDate_day:"1",
                        formID: formId,
                        consentTakerName: 'ABC'
                ],
                attachmentId:attachment.id.toString(),
                consentFormTemplateId: consentFormTemplate.id,
                template: consentFormTemplate
        ]
    }

    void "Test that Create action, returns the right attachment"() {

        setup:
        def attachment= new Attachment( fileName: 'ABC.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flash: true)


        when: "The Create action is called with a attachmentId parameter"
        consentFormController.params['attachmentId'] = attachment.id
        consentFormController.create()
        def model =  consentFormController.modelAndView.model


        then: "A model is generated containing the attachment instance"
        consentFormController.response.text != 'not found'
        model.commandInstance.attachment
        model.commandInstance.attachment.id == attachment.id
        model.commandInstance.attachment.fileName == 'ABC.jpg'
    }

	void "Test that Create action, will give error if the attachment is already annotated"() {

		setup:"an Annotated attachment exists"
		def attachment = Attachment.first()

		when: "The Create action is called with a attachmentId parameter"
		consentFormController.params['attachmentId'] = attachment.id
		consentFormController.create()
		def model =  consentFormController.modelAndView.model


		then: "A model is generated containing the attachment instance and flash has the error message"
		consentFormController.response.text != 'not found'
		model.commandInstance.attachment
		model.commandInstance.attachment.id == attachment.id

		consentFormController.flash.error
		consentFormController.flash.annotatedBefore
		consentFormController.flash.annotatedBeforeLink == grailsLinkGenerator.link([controller: "consentFormCompletion",action: "show",id:attachment?.consentForm?.id])
	}

    void "Test that Save action, creates the Patient"() {
        given:"An UnAnnotated attachment exists"
		def attachment= new Attachment(id: 200, fileName: 'a.jpg', dateOfUpload: new Date(),
				attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flush:true)
        def patientCountBefore = Patient.count();

        when: "Save action is executed, consentForm is created"
        initParams(ConsentFormTemplate.first(),attachment,"GEN97890")
        consentFormController.save()

        then:
        consentFormController.response.redirectedUrl == "/attachment/annotatedList"
        Patient.count() == patientCountBefore + 1
    }

    void "Test that Save action, creates the ConsentForm"() {
		given:"An UnAnnotated attachment exists"
		def attachment= new Attachment(id: 200, fileName: 'a.jpg', dateOfUpload: new Date(),
				attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flush:true)

        when: "Save action is executed, consentForm is created"
		def consentFormCountBefore = ConsentForm.count();
		initParams(ConsentFormTemplate.first(),attachment,"GEN12349")
        consentFormController.save()

        then:
        consentFormController.response.redirectedUrl == "/attachment/annotatedList"
        ConsentForm.count() == consentFormCountBefore + 1

        cleanup:
        def attachmentAfter= Attachment.first();
        attachmentAfter.consentForm=null
        attachmentAfter.save()
    }


	void "Test that Save action, does not create ConsentForm if attachment is already attached to a consent"() {
		given:"An UnAnnotated attachment exists"
		def attachment= new Attachment(id: 200, fileName: 'a.jpg', dateOfUpload: new Date(),
				attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flush:true)
		def template = ConsentFormTemplate.first()
		def patient= new Patient(
				givenName: "Eric",
				familyName: "Clapton",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "1002",
				nhsNumber: "1234567890",
				consents: []
		)
		def consent = new ConsentForm(
				attachedFormImage: attachment,
				template: template,
				patient: patient,
				consentDate: new Date([year:2014,month:01,date:01]),
				consentTakerName: "Edward",
				formID: "GEN12345",
				formStatus: ConsentForm.FormStatus.NORMAL,
				comment: "a simple unEscapedComment, with characters \' \" \n "
		)
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: template.questions[0]))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: template.questions[1]))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: template.questions[2]))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: template.questions[3]))


		def consentFormCountBefore = ConsentForm.count();
		def responsesCountBefore = Response.count();
		def patientCountBefore   = Patient.count();


		//another user tries to attach this attachment to another consent
		when: "Save action is executed, consentForm is created"
		initParams(template,attachment,"GEN12349")
		consentFormController.save()

		then:
		ConsentForm.count() == consentFormCountBefore
		Patient.count() == patientCountBefore
		Response.count() == responsesCountBefore
		consentFormController.flash.error
		consentFormController.flash.annotatedBefore
		consentFormController.flash.annotatedBeforeLink == grailsLinkGenerator.link([controller: "consentFormCompletion",action: "show",id:attachment?.consentForm?.id])
	}

    void "Test that Save action, updates the Attachment"() {
        given:"An attachment is not assigned to a consent"
        def attachment= Attachment.first();
        attachment.consentForm=null
        attachment.save()
        def consentTemplate = ConsentFormTemplate.first()

        when: "Save action is executed, attachment is updated"
        initParams(consentTemplate,attachment,"GEN78905")
        consentFormController.save()

        then:
        consentFormController.response.redirectedUrl == "/attachment/annotatedList"
        attachment.consentForm!=null

        cleanup:
        def attachmentAfter= Attachment.get(attachment.id);
        attachmentAfter.consentForm=null
        attachmentAfter.save()

    }

    void "Test that Edit action redirects to list page when called by an unAvailable consentForm"()
    {
        when:"An Unavailable attachment is requested for edit"
        consentFormController.params['id'] = 0
        consentFormController.edit()

        then:"request is redirected to consent list"
        consentFormController.response.redirectedUrl == "/consentForm/annotatedList"
    }

    void "Test that FormIdCheck will return correct JSON value when no formId is passed"()
    {
        when:"calling checkFormId"
        consentFormController.request.contentType =""
        consentFormController.response.format="json"
        consentFormController.checkFormId();

        then:"it returns -1 for non-Existing formId"
        consentFormController.response
        consentFormController.response.json.consentFormId == -1
    }


    void "Test that FormIdCheck will return correct JSON value when formId does not exists"()
    {
        when:"calling checkFormId"
        consentFormController.params["id"] ="123"
        consentFormController.response.format="json"
        consentFormController.checkFormId();

        then:"it returns -1 for formId which does not exist"
        consentFormController.response.json
        consentFormController.response.json.consentFormId == -1
    }


    void "Test that FormIdCheck will return the right consentFormId when formId exists"()
    {
        when:"calling checkFormId"
        def consentFormId = ConsentForm.list()[0].id
        consentFormController.response.format="json"
        consentFormController.params["id"] ="GEN12345"
        consentFormController.checkFormId();

        then:"it returns correct consentForm.Id"
        consentFormController.response
        consentFormController.response.json.consentFormId == consentFormId
    }

    void "Test that FormIdCheck will return -1 when formId ends with 00000"()
    {
        when:"calling checkFormId"
        consentFormController.response.format="json"
        consentFormController.params["id"] ="GEN00000"
        consentFormController.checkFormId();

        then:"it returns correct consentForm.Id"
        consentFormController.response
        consentFormController.response.json.consentFormId == -1
    }


	void "findDemographic should return patient demographic"() {

		when: "nhsNumber is passed"
		consentFormController.params['nhsNumber'] = "ABC"
		consentFormController.response.format = "json"
		consentFormController.findDemographic()


		then: "findDemographic returns patient demographic"
		consentFormController.response
		1 * consentFormController.demographicService.findPatient(_) >> {
			[		ACTIVE_MRN: "10221601",
					GIVENNAME: "John",
					FAMILYNAME: "Smith",
					SEX: "1",
					DOB: Date.parse("yyyy-MM-dd","2010-05-17")
			]
		}
		//json returned from controller changes the format of the Date type
		//it needs a custom marshaller to handle date
		//so we do not check DOB as its value in jSON is '2010-05-16T23:00:00Z'
		//consentFormController.response.json.DOB == Date.parse("yyyy-MM-dd","2010-05-17")
		consentFormController.response.json.patient.DOB_day == 17
		consentFormController.response.json.patient.ACTIVE_MRN == "10221601"
 		consentFormController.response.json.patient.GIVENNAME == "John"
		consentFormController.response.json.patient.SEX ==  "1"
		consentFormController.response.json.patient.FAMILYNAME == "Smith"
		consentFormController.response.json.patient.DOB_year == 2010
		consentFormController.response.json.patient.DOB_month == 4
	}


	void "parseGELBarcode returns GEL participant details"() {

		when: "GELBarcode is passed"
		consentFormController.params['GELBarcode'] = "123...123"
		consentFormController.response.format = "json"
		consentFormController.parseGELBarcode()


		then: "parseGELBarcode returns participant detail"
		consentFormController.response
		1 * consentFormController.GELBarcodeParserService.parseGELBarcodeString(_) >> {
			[
					error  : "",
					success: true,
					result : [
							participantId     : "12345",
							NHSNumber         : "1234567890",
							participantDetails: [
									hospitalNumber: "160048",
									forenames     : "Adam",
									surname       : "Smith",
									dateOfBirth   : "23/07/1985",
									dobYear  : "1985",
									dobMonth : "7",
									dobDay  : "23"
							],
							diseaseType       : "Rare Disease",
							SLF               : [
									version: "1.1.1",
									date   : "03.03.2015"
							]
					]
			]
		}

		consentFormController.response.json.GELParticipant == [
				error  : "",
				success: true,
				result : [
						participantId     : "12345",
						NHSNumber         : "1234567890",
						participantDetails: [
								hospitalNumber: "160048",
								forenames     : "Adam",
								surname       : "Smith",
								dateOfBirth   : "23/07/1985",
								dobYear  : "1985",
								dobMonth : "7",
								dobDay  : "23"
						],
						diseaseType       : "Rare Disease",
						SLF               : [
								version: "1.1.1",
								date   : "03.03.2015"
						]
				]
		]
	}
}