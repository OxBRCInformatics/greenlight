package uk.ac.ox.brc.greenlight

import grails.converters.JSON
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile
import org.codehaus.groovy.grails.web.json.JSONArray
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockMultipartHttpServletRequest
import org.springframework.web.multipart.MultipartHttpServletRequest
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by soheil on 21/03/2014.
 */
//@TestFor(AttachmentController)
//@Mock(Attachment)
//class AttachmentControllerISpec extends Specification{
class AttachmentControllerISpec extends IntegrationSpec {

    def attachmentController = new AttachmentController()

	def consentForms = []
	def attachments  = []

    def setup() {
        new Attachment(fileName: '2.jpg', dateOfUpload: new Date([year: 2014, month: 1, date: 1]), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save()
        new Attachment(fileName: '3.jpg', dateOfUpload: new Date([year: 2014, month: 1, date: 2]), attachmentType: Attachment.AttachmentType.PDF, content: []).save()
        new Attachment(fileName: '1.jpg', dateOfUpload: new Date([year: 2014, month: 1, date: 4]), attachmentType: Attachment.AttachmentType.PDF, content: []).save()
        new Attachment(fileName: '4.jpg', dateOfUpload: new Date([year: 2014, month: 1, date: 3]), attachmentType: Attachment.AttachmentType.PDF, content: []).save()
        new Attachment(fileName: '5.jpg', dateOfUpload: new Date([year: 2014, month: 1, date: 5]), attachmentType: Attachment.AttachmentType.PDF, content: []).save()
        new Attachment(fileName: '6.jpg', dateOfUpload: new Date([year: 2014, month: 1, date: 6]), attachmentType: Attachment.AttachmentType.PDF, content: []).save()
        new Attachment(fileName: '7.jpg', dateOfUpload: new Date([year: 2014, month: 1, date: 8]), attachmentType: Attachment.AttachmentType.PDF, content: []).save()


		createAnnotatedAttachments();

		consentForms.clear()
		consentForms.addAll(ConsentForm.list())

		attachments.clear()
		attachments.addAll(Attachment.list())

	}

    def private createAnnotatedAttachments() {
        def attachment1 = new Attachment(id: 1, fileName: '1a.jpg', dateOfUpload: new Date([year: 2014, month: 2, date: 4]), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flash: true)
        def attachment2 = new Attachment(id: 1, fileName: '2a.jpg', dateOfUpload: new Date([year: 2014, month: 2, date: 1]), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flash: true)
        def attachment3 = new Attachment(id: 1, fileName: '3a.jpg', dateOfUpload: new Date([year: 2014, month: 2, date: 2]), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flash: true)

        def template1 = new ConsentFormTemplate(
                id: 1,
                name: "ORB1",
                templateVersion: "1.1",
                namePrefix: "ABC",
        ).addToQuestions(new Question(name: 'I read1...')
        ).save()


		def template2 = new ConsentFormTemplate(
				id: 1,
				name: "ORB1",
				templateVersion: "1.1",
				namePrefix: "DEF",
		).addToQuestions(new Question(name: 'I read1...')
		).save()

		def template3 = new ConsentFormTemplate(
				id: 1,
				name: "ORB1",
				templateVersion: "1.1",
				namePrefix: "GHI",
		).addToQuestions(new Question(name: 'I read1...')
		).save()


        def consent1 = new ConsentForm(
                attachedFormImage: attachment1,
                template: template1,
                consentDate: new Date([year: 2014, month: 01, date: 01]),
                consentTakerName: "Edmin",
                formID: "ABC12345",
                formStatus: ConsentForm.FormStatus.NORMAL
        ).save();



        new Patient(
                givenName: "Patient1",
                familyName: "Clapton",
                dateOfBirth: new Date("30/03/1945"),
                hospitalNumber: "1002",
                nhsNumber: "1234567892",
                consents: []
        ).addToConsents(consent1).save()


		def consent2 = new ConsentForm(
				attachedFormImage: attachment2,
				template: template2,
				consentDate: new Date([year: 2014, month: 01, date: 02]),
				consentTakerName: "Edmin",
				formID: "DEF12345",
				formStatus: ConsentForm.FormStatus.DECLINED
		).save();


		new Patient(
				givenName: "Patient2",
				familyName: "Clapton",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "1002",
				nhsNumber: "1234567890",
				consents: []
		).addToConsents(consent2).save()


		def consent3 = new ConsentForm(
				attachedFormImage: attachment3,
				template: template3,
				consentDate: new Date([year: 2014, month: 01, date: 03]),
				consentTakerName: "Edmin",
				formID: "GHI12345",
				formStatus: ConsentForm.FormStatus.SPOILED
		).save(flush: true);

		new Patient(
				givenName: "Patient3",
				familyName: "Clapton",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "1002",
				nhsNumber: "1234567891",
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



	def "listUnAnnotatedAttachments returns list of un-annotated attachment"()
	{
		given:"A number of attachments exist"
		Attachment.count() == 10 //10 is the number of all attachments

		when:"listUnAnnotatedAttachments action is called"
		attachmentController.params.sSortDir_0 = "asc"
		attachmentController.params.iSortCol_0 = "0"

		attachmentController.params.iDisplayLength = 10
		attachmentController.params.iDisplayStart = 0


		attachmentController.response.format = "json"
		attachmentController.listUnAnnotatedAttachments()

		//7 attachments are not annotated
		then:"returns Un-AnnotatedAttachments in json format"
		new JSONArray(attachmentController.response.json.aaData).size() == 7
		attachmentController.response.json.iTotalRecords == 7
	}


	@Unroll
	def "listUnAnnotatedAttachments returns list of un-annotated attachment in specific order for sortDir=#sortDir and sortCol=#sortCol and topIndex=#topIndex and bottomIndex=#bottomIndex"()
	{
		given:"A number of attachments exist"
		Attachment.count() == 10 //10 is the number of all attachments

		when:"listUnAnnotatedAttachments action is called"
		attachmentController.params.sSortDir_0 = sortDir
		attachmentController.params.iSortCol_0 = sortCol
		attachmentController.response.format = "json"
		attachmentController.params.iDisplayLength = 20
		attachmentController.params.iDisplayStart = 0
		attachmentController.listUnAnnotatedAttachments()

		then:"returns Un-AnnotatedAttachments in json format in specific order"
		new JSONArray(attachmentController.response.json.aaData).size() == 7
		new JSONArray(attachmentController.response.json.aaData)[0].id == attachments[topIndex].id
		new JSONArray(attachmentController.response.json.aaData)[6].id == attachments[bottomIndex].id

		/*
		 "0" = "dateOfUpload"
		 "1" =  "fileName"
   	    new Attachment(fileName: '2.jpg', dateOfUpload: new Date([year: 2014, month: 01, date: 01]),
        new Attachment(fileName: '3.jpg', dateOfUpload: new Date([year: 2014, month: 01, date: 02]),
        new Attachment(fileName: '1.jpg', dateOfUpload: new Date([year: 2014, month: 01, date: 04]),
        new Attachment(fileName: '4.jpg', dateOfUpload: new Date([year: 2014, month: 01, date: 03]),
        new Attachment(fileName: '5.jpg', dateOfUpload: new Date([year: 2014, month: 01, date: 05]),
        new Attachment(fileName: '6.jpg', dateOfUpload: new Date([year: 2014, month: 01, date: 06]),
        new Attachment(fileName: '7.jpg', dateOfUpload: new Date([year: 2014, month: 01, date: 08]),
	   */
		where:
		sortDir	|	sortCol	|	topIndex	| bottomIndex
		"desc"	|	"1"		|		6		|		2
		"asc"	|	"1"		|		2		|		6
		"desc"	|	"0"		|		6		|		0
		"asc"	|	"0"		|		0		|		6
		"desc"	|	"88"	|		6		|		0	//if not a valid column specified, sort it base on date of upload
		"asc"	|	"88"	|		0		|		6	//if not a valid column specified, sort it base on date of upload
	}


	def "listAnnotatedAttachments returns list of all consentForms"()
	{
		setup:"A number of consentForms and attachments exist"
		ConsentForm.count() == 3

		when:"listUnAnnotatedAttachments action is called"
		attachmentController.params.sSortDir_0 = "asc"
		attachmentController.params.iSortCol_0 = "0"
		attachmentController.response.format = "json"
		attachmentController.lisAnnotatedAttachments()

		then:"returns all consentForms in json format"
		new JSONArray(attachmentController.response.json.aaData).size() == 3
		attachmentController.response.json.iTotalRecords == 3
		attachmentController.response.json.iTotalDisplayRecords == 3
	}


	@Unroll
	def "listAnnotatedAttachments returns list of all consentForms specific order sortDire=#sortDir  sortCol=#sortCol  and topIndex=#topIndex and bottomIndex=#bottomIndex"()
	{

		def x
		setup:"A number of consentForms and attachments exist"
		ConsentForm.count() == 3

		when:"listUnAnnotatedAttachments action is called by orderDir and orderColumn"
		attachmentController.params.sSortDir_0 = sortDir
		attachmentController.params.iSortCol_0 = sortCol
		attachmentController.response.format = "json"
		attachmentController.lisAnnotatedAttachments()

		then:"returns all consentForms in json format in the expected order"
		new JSONArray(attachmentController.response.json.aaData)[0].id == consentForms[topIndex].id
		new JSONArray(attachmentController.response.json.aaData)[2].id == consentForms[bottomIndex].id

		/*
		 def sortCol = ["0":"consentDate",
						"1":"formStatus",
						"2":"template.namePrefix",
						"3":"formID",
						"4":"patient.nhsNumber" ]
		//Actual ConsentForm order
		ConsentForm([year: 2014, month: 01, date: 01]),"ABC","ABC12345",ConsentForm.FormStatus.NORMAL, nhsNumber: "1234567892"
		ConsentForm([year: 2014, month: 01, date: 02]),"DEF","DEF12345",ConsentForm.FormStatus.DECLINED, nhsNumber: "1234567890"
		ConsentForm([year: 2014, month: 01, date: 03]),"GHI","GHI12345",ConsentForm.FormStatus.SPOILED , nhsNumber: "1234567891"
	   */
		where:
		sortDir	|	sortCol	|	topIndex|	bottomIndex
		"desc"	|	"0"		|		2	|		0
		"asc"	|	"0"		|		0	|		2

		"desc"	|	"1"		| 		2	|		1
		"asc"	|	"1"		|		1	|		2

		"desc"	|	"2"		|		2	|		0
		"asc"	|	"2"		|		0	|		2

		"desc"	|	"3"		|		2	|		0
		"asc"	|	"3"		|		0	|		2

		"desc"	|	"4"		|		0	|		1
		"asc"	|	"4"		|		1	|		0

		"desc"	|	"88"	|		2	|		0	//if not a valid column specified, sort it base on date of consent
		"asc"	|	"88"	|		0	|		2	//if not a valid column specified, sort it base on date of consent
	}

}