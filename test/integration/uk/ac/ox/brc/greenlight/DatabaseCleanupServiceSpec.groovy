package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import grails.test.spock.IntegrationSpec
import groovy.sql.Sql
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
class DatabaseCleanupServiceSpec extends IntegrationSpec {

	def databaseCleanupService
	def dataSource

	def setup() {

		def attachment= new Attachment(id: 1, fileName: 'a.jpg', dateOfUpload: new Date(),
				attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flush: true)

		def questions = [
				new Question(name: 'I read1...'),
				new Question(name: 'I read2...'),
				new Question(name: 'I read3...'),
				new Question(name: 'I read4...')
		];

		def template=new ConsentFormTemplate(
				id: 1,
				name: "ORB1",
				templateVersion: "1.1",
				namePrefix: "GNR")
				.addToQuestions(questions[0])
				.addToQuestions(questions[1])
				.addToQuestions(questions[2])
				.addToQuestions(questions[3])
				.save(flush: true)


		def patient= new Patient(
				givenName: "Eric",
				familyName: "Clapton",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "1002",
				nhsNumber: "1234567890",
				consents: []
		).save(flush: true)

		def consent = new ConsentForm(
				attachedFormImage: attachment,
				template: template,
				patient: patient,
				consentDate: new Date([year:2014,month:01,date:01]),
				consentTakerName: "Edward",
				formID: "GEN12345",
				formStatus: ConsentForm.FormStatus.NORMAL,
				comment: "a simple unEscapedComment, with characters \' \" \n "
		).save(flush: true)

		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[0]))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[1]))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[2]))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[3]))
		consent.save(flush: true)

		def CONSENT_FORM_ID = ConsentForm.first().id

		//add 4 new responses manually to make old ones orphans and simulate the actual status
		def sql = new Sql(dataSource)
		(0..3).each {
			sql.executeUpdate(
					'insert into RESPONSE (VERSION,ANSWER,CONSENT_FORM_ID,QUESTION_ID,RESPONSES_IDX) values(?, ?, ?, ?, ?)',
					[0, 'NO',CONSENT_FORM_ID,questions[it].id,it])
		}

		//add 4 new responses manually to make old ones orphans and simulate the actual status
		(0..3).each {
			sql.executeUpdate(
					'insert into RESPONSE (VERSION,ANSWER,CONSENT_FORM_ID,QUESTION_ID,RESPONSES_IDX) values(?, ?, ?, ?, ?)',
					[0, 'NO',CONSENT_FORM_ID,questions[it].id,it])
		}

	}


	void "DatabaseCleanup removes orphan responses"() {

		given:"A number of orphan responses already exists"
		assert ConsentForm.count() == 1
		def cons = ConsentForm.first()
		assert cons.responses.size() == 4
		assert Response.count() == 12 //8 orphan responses

		when:"cleanup the responses"
		databaseCleanupService.cleanOrphanResponses()

		then:"no orphan responses exist any longer"
		ConsentForm.first().responses.size() == 4
		Response.count() == 4
	}
}
