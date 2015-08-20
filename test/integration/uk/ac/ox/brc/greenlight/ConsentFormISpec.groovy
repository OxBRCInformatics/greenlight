package uk.ac.ox.brc.greenlight

import grails.test.spock.IntegrationSpec
import grails.validation.ValidationException

/**
 * Added by Soheil 19.08.2015
 * Test ConsentForm domain class
 */
class ConsentFormISpec extends IntegrationSpec {

	void "Gorm will add a GUID into accessGUID before inserting the object"() {

		when:"ConsentForm is inserted"
		def con = createTestConsent()
		con.save(failOnError: true,flush: true)

		then:"accessGUID is added"
		con.accessGUID != null


		when:"ConsentForm is updated"
		con.comment = "TEST COMMENT"
		def accessGUID = con.accessGUID
		con.save(failOnError: true,flush: true)

		then:"accessGUID is not changed"
		accessGUID == con.accessGUID
	}


	void "accessGUID should be unique"() {

		given:"A number of consentForms are available"
		def con1 = createTestConsent()
		con1.save(failOnError: true,flush: true)
		def con2 = createTestConsent()
		con2.save(failOnError: true,flush: true)

		when:"consentForm is saved with an available accessGUID"
		con2.accessGUID = con1.accessGUID //use accessGUID of another consentForm
		con2.validate()

		then:"consentForm has Validation Error"
		con2.hasErrors()

	}


	private def createTestConsent(){
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
				comment: "a simple unEscapedComment, with characters \' \" \n "
		)

		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: question1))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: question2))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: question3))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: question4))
		return consent
	}
}
