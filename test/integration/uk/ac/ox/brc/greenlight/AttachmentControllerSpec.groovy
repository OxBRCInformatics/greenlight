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
//class AttachmentControllerSpec extends Specification{
class AttachmentControllerSpec extends IntegrationSpec {

    def attachmentController = new AttachmentController()

    def setup() {
        new Attachment(fileName: '1.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save()
        new Attachment(fileName: '2.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.PDF, content: []).save()
        new Attachment(fileName: '3.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.PDF, content: []).save()
        new Attachment(fileName: '4.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.PDF, content: []).save()
        new Attachment(fileName: '5.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.PDF, content: []).save()
        new Attachment(fileName: '6.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.PDF, content: []).save()
        new Attachment(fileName: '7.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.PDF, content: []).save()
    }

    def private createAnnotatedAttachments() {
        def attachment1 = new Attachment(id: 1, fileName: '1a.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flash: true)
        def attachment2 = new Attachment(id: 1, fileName: '2a.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flash: true)
        def attachment3 = new Attachment(id: 1, fileName: '3a.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flash: true)

        def template = new ConsentFormTemplate(
                id: 1,
                name: "ORB1",
                templateVersion: "1.1",
                namePrefix: "GNR",
        ).addToQuestions(new Question(name: 'I read1...')
        ).save()

		//--------------------------
        def consent1 = new ConsentForm(
                attachedFormImage: attachment1,
                template: template,
                consentDate: new Date([year: 2014, month: 01, date: 01]),
                consentTakerName: "Edmin",
                formID: "GEN12345",
                formStatus: ConsentForm.FormStatus.NORMAL
        ).save();


        new Patient(
                givenName: "Patient1",
                familyName: "Clapton",
                dateOfBirth: new Date("30/03/1945"),
                hospitalNumber: "1002",
                nhsNumber: "1234567890",
                consents: []
        ).addToConsents(consent1).save()


		//--------------------------
		def consent2 = new ConsentForm(
				attachedFormImage: attachment2,
				template: template,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edmin",
				formID: "GEN12345",
				formStatus: ConsentForm.FormStatus.NORMAL
		).save();


		new Patient(
				givenName: "Patient2",
				familyName: "Clapton",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "1002",
				nhsNumber: "1234567890",
				consents: []
		).addToConsents(consent2).save()
		//-------------------------------
		def consent3 = new ConsentForm(
				attachedFormImage: attachment3,
				template: template,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edmin",
				formID: "GEN12345",
				formStatus: ConsentForm.FormStatus.NORMAL
		).save();


		new Patient(
				givenName: "Patient3",
				familyName: "Clapton",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "1002",
				nhsNumber: "1234567890",
				consents: []
		).addToConsents(consent3).save()


		return [attachment1,attachment2,attachment3]
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
        def attachment = createAnnotatedAttachments()[0];
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


    def "Test if save rejects files other than Image and PDF"()
    {
        given:"A sample text file is uploaded"
        def mockFile1 = new MockMultipartFile('scannedForms', 'input.txt','text/plain' , "TestMockContent" as byte[])
        def mockFile2 = new MockMultipartFile('scannedForms', 'input.jpg','image/jpg' , "TestMockContent" as byte[])
        def attCount = Attachment.count()

        when:"uploading a text file"
        attachmentController.metaClass.request = new MockMultipartHttpServletRequest();
        attachmentController.request.addFile(mockFile1)
        attachmentController.request.addFile(mockFile2)
        attachmentController.save();

        then:"the file should not be added"
        Attachment.count() == attCount + 1
        attachmentController.modelAndView.model.attachments.size()==1
    }

	def "listUnAnnotatedAttachments returns list of un-annotated attachment"()
	{
		given:"A number of attachments exist"
		createAnnotatedAttachments();
		Attachment.count() == 10 //all attachment

		when:"listUnAnnotatedAttachments action is called"
		attachmentController.params.sSortDir_0 = "desc"
		attachmentController.params.iSortCol_0 = "0"
		attachmentController.response.format = "json"
		attachmentController.listUnAnnotatedAttachments()

		then:"returns Un-AnnotatedAttachments in json format"
		new JSONArray(attachmentController.response.json.aaData).size() == 7 //7 attachments are not annotated
		attachmentController.response.json.iTotalRecords == 7 //7 attachments are not annotated
	}

	def "listAnnotatedAttachments returns list of all consentForms"()
	{
		given:"A number of consentForms and attachments exist"
		createAnnotatedAttachments();
		ConsentForm.count() == 3

		when:"listUnAnnotatedAttachments action is called"
		attachmentController.params.sSortDir_0 = "desc"
		attachmentController.params.iSortCol_0 = "0"
		attachmentController.response.format = "json"
		attachmentController.lisAnnotatedAttachments()

		then:"returns all consentForms in json format"
		new JSONArray(attachmentController.response.json.aaData).size() == 3
		attachmentController.response.json.iTotalRecords == 3
	}

}