package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import grails.test.spock.IntegrationSpec
import groovy.sql.Sql
import spock.lang.Specification

import java.sql.Timestamp

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
class DatabaseCleanupServiceSpec extends IntegrationSpec {

	def databaseCleanupService
	def dataSource

	def AddOrphanResponses() {

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

	def AddConsentFormsForAttachment() {

		def attachment1= new Attachment(id: 1, fileName: 'a.jpg', dateOfUpload: new Date(),
				attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flush: true , failOnError:true )


		def attachment2= new Attachment(id: 1, fileName: 'a.jpg', dateOfUpload: new Date(),
				attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flush: true , failOnError:true )

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
				.save(flush: true , failOnError:true )


		def patient1= new Patient(
				givenName: "Eric",
				familyName: "Clapton",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "1002",
				nhsNumber: "1234567890",
				consents: []
		).save(flush: true , failOnError:true )


		def patient2= new Patient(
				givenName: "Eric",
				familyName: "Clapton",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "1002",
				nhsNumber: "1234567890",
				consents: []
		).save(flush: true , failOnError:true )


		def consent1 = new ConsentForm(
				attachedFormImage: attachment1,
				template: template,
				patient: patient1,
				consentDate: new Date([year:2014,month:01,date:01]),
				consentTakerName: "Edward",
				formID: "GEN12345",
				formStatus: ConsentForm.FormStatus.NORMAL,
				comment: "a simple unEscapedComment, with characters \' \" \n "
		).save(flush: true , failOnError:true )
		consent1.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[0]))
		consent1.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[1]))
		consent1.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[2]))
		consent1.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[3]))
		consent1.save(flush: true , failOnError:true )

		def consent2 = new ConsentForm(
				attachedFormImage: attachment1,
				template: template,
				patient: patient1,
				consentDate: new Date([year:2014,month:01,date:01]),
				consentTakerName: "Edward",
				formID: "GEN99999",
				formStatus: ConsentForm.FormStatus.NORMAL,
				comment: "a simple unEscapedComment, with characters \' \" \n "
		).save(flush: true , failOnError:true )
		consent2.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[0]))
		consent2.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[1]))
		consent2.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[2]))
		consent2.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[3]))
		consent2.save(flush: true , failOnError:true )


		def consent3 = new ConsentForm(
				attachedFormImage: attachment2,
				template: template,
				patient: patient2,
				consentDate: new Date([year:2014,month:01,date:01]),
				consentTakerName: "Edward",
				formID: "GEN67890",
				formStatus: ConsentForm.FormStatus.NORMAL,
				comment: "a simple unEscapedComment, with characters \' \" \n "
		).save(flush: true , failOnError:true )
		consent3.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[0]))
		consent3.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[1]))
		consent3.save(flush: true , failOnError:true )

		def consent4 = new ConsentForm(
				attachedFormImage: attachment2,
				template: template,
				patient: patient2,
				consentDate: new Date([year:2014,month:01,date:01]),
				consentTakerName: "Edward",
				formID: "GEN11111",
				formStatus: ConsentForm.FormStatus.NORMAL,
				comment: "a simple unEscapedComment, with characters \' \" \n "
		).save(flush: true , failOnError:true )
		consent4.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[0]))
		consent4.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[1]))
		consent4.save(flush: true , failOnError:true )

	}

	void "DatabaseCleanup removes orphan responses"() {

		given:"A number of orphan responses already exists"
		AddOrphanResponses()
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

	void "RemoveDuplicateConsentForm will removed duplicate consents attached to an attachment"(){
		given:"A number of attachments are annotated more than once"
		AddConsentFormsForAttachment()
		//we have two attachment which each one has 2 consentForm attached to them
		Patient.list().size() == 2
		ConsentForm.list().size() == 4

		Attachment.list().size() == 2
		Response.list().size()  == 12

		//the consent forms that are annotated for the same attachment and should be removed
		def expectedRemovedIds = ConsentForm.where {formID=="GEN99999" || formID=="GEN11111"}.collect {it.id}

		when:"RemoveDuplicateConsentForm is called"
		def removed = databaseCleanupService.RemoveDuplicateConsentForm()

		then:"Duplicate consent forms will be removed"
		removed == expectedRemovedIds
		Patient.list().size() == 2
		ConsentForm.list().size() == 2
		Attachment.list().size() == 2
		Response.list().size()  == 6
	}
}