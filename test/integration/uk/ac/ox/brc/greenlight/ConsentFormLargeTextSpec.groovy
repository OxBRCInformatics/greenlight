package uk.ac.ox.brc.greenlight

import grails.test.spock.IntegrationSpec

/**
 * Created by nasullah on 20/05/2014.
 */
class ConsentFormLargeTextSpec extends IntegrationSpec {

    def   consentFormService
    def   consentEvaluationService

    def setup() {
        def attachment= new Attachment(id: 1, fileName: 'a.jpg', dateOfUpload: new Date(),
                attachmentType: Attachment.AttachmentType.IMAGE, content: []).save()

		def question1 =  new Question(name: 'I read1...')
		def question2 =  new Question(name: 'I read2...')
		def question3 =  new Question(name: 'I read3...')
		def question4 =  new Question(name: 'I read4...')

        def template=new ConsentFormTemplate(
                id: 1,
                name: "ORB1",
                templateVersion: "1.1",
                namePrefix: "GNR")
				.addToQuestions(question1)
				.addToQuestions(question2)
				.addToQuestions(question3)
				.addToQuestions(question4)
        .save()


        def patient= new Patient(
            givenName: "Eric",
            familyName: "Clapton",
            dateOfBirth: new Date("30/03/1945"),
            hospitalNumber: "1002",
            nhsNumber: "1234567890",
            consents: []
            ).save()

       def consent = new ConsentForm(
			   	accessGUID: UUID.randomUUID().toString(),
                attachedFormImage: attachment,
                template: template,
                patient: patient,
                consentDate: new Date([year:2014,month:01,date:01]),
                consentTakerName: "Edward",
                formID: "GEN12345",
                formStatus: ConsentForm.FormStatus.NORMAL,
                comment: "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters" +
                        "Large text comment more than 255 characters"
        ).save()

        consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: question1))
        consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: question2))
        consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: question3))
        consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: question4))
        consent.save()
    }

//    def "Delete action will delete consentForm and its responses"() {
//
//		given:"A number of consentForms are available"
//		assert ConsentForm.count() == 1
//		def cons = ConsentForm.first()
//		assert cons.responses.size() == 4
//		assert Response.count() == 4
//
//		when:"deleting a consentForm"
//        consentFormService.delete(cons)
//
//
//        then:"the consentForm and its responses are all deleted"
//        ConsentForm.count() == 0
//        Response.count() == 0
//
//		and:"it keeps the patient record"
//        Patient.count() == 1
//    }
}