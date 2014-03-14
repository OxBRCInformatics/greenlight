package uk.ac.ox.brc.greenlight

import grails.test.spock.IntegrationSpec


class AttachmentControllerSpec extends IntegrationSpec {

    def consentFormController =new ConsentFormCompletionController()

    def setup() {
        def attachment1= new Attachment( fileName: 'a.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flash: true)
        def attachment2=  new Attachment(fileName: 'a.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flash: true)


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

    private void initParams(consentFormTemplate,attachment)
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
                        formID: "GEN12345",
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

    void "Test that Save action, creates the Patient"() {
        given:
        def patientCountBefore = Patient.count();

        when: "Save action is executed, consentForm is created"
        initParams(ConsentFormTemplate.first(),Attachment.first())
        consentFormController.save()

        then:
        consentFormController.response.redirectedUrl =="/attachment/list"
        Patient.count() == patientCountBefore + 1
    }

    void "Test that Save action, creates the ConsentForm"() {
        given:
        def consentFormCountBefore = ConsentForm.count();

        when: "Save action is executed, consentForm is created"
        initParams(ConsentFormTemplate.first(),Attachment.first())
        consentFormController.save()

        then:
        consentFormController.response.redirectedUrl =="/attachment/list"
        ConsentForm.count() == consentFormCountBefore + 1

        cleanup:
        def attachmentAfter= Attachment.first();
        attachmentAfter.consentForm=null
        attachmentAfter.save()
    }

    void "Test that Save action, updates the Attachment"() {
        given:"An attachment is not assigned to a consent"
        def attachment= Attachment.first();
        attachment.consentForm=null
        attachment.save()
        def consentTemplate = ConsentFormTemplate.first()

        when: "Save action is executed, attachment is updated"
        initParams(consentTemplate,attachment)
        consentFormController.save()

        then:
        consentFormController.response.redirectedUrl =="/attachment/list"
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
        consentFormController.response.redirectedUrl =="/consentForm/list"
    }

}