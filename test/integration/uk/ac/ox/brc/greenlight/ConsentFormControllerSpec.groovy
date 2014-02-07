package uk.ac.ox.brc.greenlight

import grails.test.spock.IntegrationSpec

import javax.xml.transform.Templates


class ConsentFormControllerSpec extends IntegrationSpec {

    def consentFormController =new ConsentFormCompletionController()


    def setup() {
        def attachment1= new Attachment(id: 1, fileName: 'a.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flash: true)
        def attachment2=  new Attachment(id: 2, fileName: 'a.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flash: true)


        new ConsentFormTemplate(
                id: 1,
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

        new ConsentFormTemplate(
                id: 2,
                name: "ORB2",
                namePrefix: "GNR",
                templateVersion: "1.1"
        ).addToQuestions(new Question(name: 'I read2...')
        ).addToQuestions(new Question(name: 'I read2...')
        ).addToQuestions(new Question(name: 'I read2...')
        ).save(flash: true)



//        def eric = new Patient(
//                givenName: "Eric",
//                familyName: "Clapton",
//                dateOfBirth: new Date("30/03/1945"),
//                hospitalNumber: "1001",
//                nhsNumber: "123-456-7891",
//                consents: []
//        ).save(failOnError: true)
//
//
//
//        def template1 = new ConsentFormTemplate(
//                        id:1,
//                        name:'ORB1',
//                        namePrefix:'GRN',
//                        templateVersion:'V1.1'
//        ).addToQuestions(new Question([name:"I have..."])
//        ).addToQuestions(new Question([name:"I have..."])).save(flush: true);
//
//
//        def template2 = new ConsentFormTemplate(
//                id:2,
//                name:'ORB1',
//                namePrefix:'GRN',
//                templateVersion:'V1.1'
//        ).addToQuestions(new Question([name:"I have..."])
//        ).addToQuestions(new Question([name:"I have..."])).save(flush: true);


//        def consentForm=new ConsentForm(
//          id: 1,
//                Patient : eric,
//                attachedFormImage :attachment1,
//                template:template1,
//                  consentDate:new Date(),
//                  consentTakerName:'Mr A',
//                  formID :'123',
//                  formStatus : ConsentForm.FormStatus.NORMAL
//        ).addToResponses(new Response());

    }

    def cleanup() {
    }

    void "Test that Create action, returns the right attachment instance"() {

        expect:
        Attachment.count() == 2


        when: "The Create action is called with a attachmentId parameter"
        consentFormController.params['attachmentId'] = 1
        consentFormController.create()
        def model =  consentFormController.modelAndView.model


        then: "A model is generated containing the attachment instance"
        consentFormController.response.text != 'not found'
        model.commandInstance.attachment
        model.commandInstance.attachment.id == 1
        model.commandInstance.attachment.fileName == 'a.jpg'
    }

    void "Test that Save action, saves the ConsentForm and Patient and returns the right model"() {
       given:
        def attachmentBefore = Attachment.list()[0]
        def consentFormTemplate = ConsentFormTemplate.list()[0]
        def patientCountBefore = Patient.count();


        consentFormController.params['questionsSize'] = consentFormTemplate.questions.size();

        consentFormTemplate.getQuestions().eachWithIndex() { obj,index ->
            consentFormController.params["responses.${index}"] = Response.ResponseValue.YES;
        }


        consentFormController.params['commandInstance'] = [
                patient: [
                        givenName: 'givName1',
                        familyName: 'familyName1',
                        dateOfBirth: new Date(),
                        nhsNumber: '123-456-1234',
                        hospitalNumber: '123'
                ],
                consentForm: [consentDate: new Date(),
                              formID: "123",
                              consentTakerName: 'ABC'
                ],
                attachmentId:attachmentBefore.id.toString(),
                consentFormTemplateId: consentFormTemplate.id,
                template: consentFormTemplate
        ]


        when: "Save action is executed, patient & consentform are saved, attachment is updated"
        consentFormController.save()
        def attachmentAfter = Attachment.get(attachmentBefore.id)
        def consentForm = ConsentForm.list()[0]
        def patient = Patient.list()[0]

        then:
        consentFormController.response.redirectedUrl =="/attachment/list"
        Attachment.count()==2
        ConsentForm.count()==1
        Patient.count() ==patientCountBefore+1
        attachmentAfter.id == attachmentBefore.id

        consentForm.attachedFormImage.id == attachmentAfter.id
        attachmentAfter.consentForm.id == consentForm.id
        attachmentAfter.consentForm.patient.id == patient.id
        consentForm.getResponses().size()==consentFormTemplate.getQuestions().size()

        consentForm.getResponses()[0].answer == Response.ResponseValue.YES
        consentForm.getResponses()[0].question.id == consentFormTemplate.getQuestions()[0].id
    }

//    void "Edit action is called, with unAvailable consentForm"()
//    {
//        given:
//        consentFormController.params['id'] = 0
//
//        when:"Edit action is called"
//         consentFormController.edit()
//
//        then:"Redirects to Controller:ConsentForm Action:list"
//        consentFormController.response.redirectedUrl =="/consentForm/list"
//    }
//
//
//    void "Edit action is called, with an available ConsentForm"()
//    {
//        given:
//        consentFormController.params['id'] = 0
//
//        when:"Edit action is called"
//        consentFormController.edit()
//
//        then:"Redirects to Controller:ConsentForm Action:list"
//        consentFormController.response.redirectedUrl =="/consentForm/list"
//
//    }
}