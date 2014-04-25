package uk.ac.ox.brc.greenlight

import grails.converters.JSON
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.web.json.JSONArray
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockMultipartHttpServletRequest
import spock.lang.Specification

/**
 * Created by soheil on 21/03/2014.
 */
//@TestFor(AttachmentController)
//@Mock(Attachment)
//class AttachmentControllerISpec extends Specification{
class AttachmentControllerISpec extends IntegrationSpec {

    def attachmentController = new AttachmentController()

    def setup() {
        new Attachment(fileName: 'a.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save()
        new Attachment(fileName: 'b.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.PDF, content: []).save()
    }

    def private createAnnotatedAttachment() {
        def attachment = new Attachment(id: 1, fileName: 'a.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flash: true)

        def template = new ConsentFormTemplate(
                id: 1,
                name: "ORB1",
                templateVersion: "1.1",
                namePrefix: "GNR",
        ).addToQuestions(new Question(name: 'I read1...')
        ).save()

        def consent1 = new ConsentForm(
                attachedFormImage: attachment,
                template: template,
                consentDate: new Date([year: 2014, month: 01, date: 01]),
                consentTakerName: "Edmin",
                formID: "GEN12345",
                formStatus: ConsentForm.FormStatus.NORMAL
        ).save();


        new Patient(
                givenName: "Eric",
                familyName: "Clapton",
                dateOfBirth: new Date("30/03/1945"),
                hospitalNumber: "1002",
                nhsNumber: "1234567890",
                consents: []
        ).addToConsents(consent1).save()
        return attachment

    }


    def "Test if list, returns correct number of Attachment list"() {
        when: "Action list is called"
        def attachmentCount = Attachment.count();
        attachmentController.list()


        then: "attachments should have been returned"
        attachmentController.modelAndView.model.attachments
        attachmentController.modelAndView.model.attachments.size() == attachmentCount
    }

    def "Test if list, returns correct number of Attachment in JSON"() {
        when: "Action list is called"
        def attCount = Attachment.count();
        attachmentController.params.format = "json"
        attachmentController.list()

        then: "attachments should have been returned"
        new JSONArray(attachmentController.response.json).size() == attCount
    }

    def "Test if show returns the correct attachment model"() {
        when: "Action show is called"
        def attachment = Attachment.list()[0]
        attachmentController.params.id = attachment.id;
        //we have to use it if we want it to retrieve object for us in show(Attachment attachment)
        attachmentController.request.method = "POST"
        attachmentController.show()

        then: "the correct attachment should be shown"
        attachmentController.modelAndView.model.attachment.fileName == attachment.fileName
    }

    def "Test if show returns the correct attachment in JSON"() {
        when: "Action show is called"
        def attachment = Attachment.list()[0]
        attachmentController.params.id = attachment.id;
        //we have to use it if we want it to retrieve object for us in show(Attachment attachment)
        attachmentController.request.method = "POST"
        attachmentController.params.format = "json"
        attachmentController.show()

        then: "the correct attachment should be returned in JSON"
        attachmentController.response.json.fileName == attachment.fileName
    }


    def "Test if delete action will delete the correct attachment"() {
        when: "Action delete is called"
        def attachment = Attachment.list()[0]
        def preCount = Attachment.count()
        attachmentController.params.id = attachment.id;
        //we have to use it if we want it to retrieve object for us in show(Attachment attachment)
        attachmentController.request.method = "POST"
        attachmentController.delete()

        then: "the attachment should be deleted and returns success result in JSON"
        Attachment.count() == preCount - 1
        Attachment.countById(attachment.id) == 0
        attachmentController.response.json.status == "success"
        attachmentController.response.json.id == attachment.id
    }

    def "Test if delete action will not delete an annotated attachment"() {
        when: "Action delete is called"
        def attachment = createAnnotatedAttachment();
        def preCount = Attachment.count()
        attachmentController.params.id = attachment.id;
        //we have to use it if we want it to retrieve object for us in show(Attachment attachment)
        attachmentController.request.method = "POST"
        attachmentController.delete()

        then: "it should not delete the attachment and will return an error message"
        Attachment.count() == preCount
        attachmentController.response.text == "Can not delete it as it's annotated"
    }

    def "Test if delete action will return correct message when the attachment does not exists"() {
        when: "Action delete is called for an attachment which does not exist"
        def preCount = Attachment.count()
        attachmentController.params.id = 123456909 // we should generate a random number
        //we have to use it if we want it to retrieve object for us in show(Attachment attachment)
        attachmentController.request.method = "POST"
        attachmentController.delete()

        then: "it should return an error message"
        Attachment.count() == preCount
        attachmentController.response.text == "not found"
    }


    def "Test if viewContent returns the correct content"()
    {
        when: "Action viewContent is called to get the attachment content"
        def input = "DataDataDataData" as byte[]
        def attachment = new Attachment(fileName: 'a.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.IMAGE, content: input ).save()
        attachmentController.params.id = attachment.id
        attachmentController.viewContent()

        then: "it should return the right content"
        attachmentController.response.outputStream.targetStream
        attachmentController.response.outputStream.targetStream.size() == input.size()
    }



//    def "Test if Save, saves the uploaded file correctly"()
//    {
//        given:"A sample image file is uploaded"
//        def mockFile = new MockMultipartFile('scannedForms', 'input.jpg','image/jpg' , "TestMockContent" as byte[])
//        def attCount = Attachment.count()
//
//        when:"uploading an image file"
//        attachmentController.metaClass.request = new MockMultipartHttpServletRequest();
//        attachmentController.request.addFile(mockFile)
//        attachmentController.save();
//
//        then:"the file should not be added"
//        Attachment.count() == attCount + 1
//        attachmentController.modelAndView.model.attachments[0].fileName == "input.jpg"
//        attachmentController.modelAndView.model.attachments[0].attachmentType == Attachment.AttachmentType.IMAGE
//        attachmentController.modelAndView.model.attachments[0].dateOfUpload.compare
//    }
}