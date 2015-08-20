package uk.ac.ox.brc.greenlight

import grails.test.mixin.Mock
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
				accessGUID: UUID.randomUUID().toString(),
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

	def AddMoreThanOneConsentFormsToAttachments() {

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
				accessGUID: UUID.randomUUID().toString(),
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
				accessGUID: UUID.randomUUID().toString(),
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
				accessGUID: UUID.randomUUID().toString(),
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
				accessGUID: UUID.randomUUID().toString(),
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
		AddMoreThanOneConsentFormsToAttachments()
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


	private void AddConsentFormWithoutConsentStatus(){

		def attachment1 = new Attachment(id: 1, fileName: 'a.jpg', dateOfUpload: new Date(),	attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flush: true , failOnError:true )
		def attachment2 = new Attachment(id: 1, fileName: 'a.jpg', dateOfUpload: new Date(),	attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flush: true , failOnError:true )
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
				hospitalNumber: "1000",
				nhsNumber: "0987654321",
				consents: []
		).save(flush: true , failOnError:true )


		def consent1 = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment1,
				template: template,
				consentDate: new Date([year:2014,month:01,date:01]),
				consentTakerName: "Edward",
				formID: "GEN12345",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patient1).save(flush: true , failOnError:true )
		consent1.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[0]))
		consent1.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[1]))
		consent1.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[2]))
		consent1.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[3]))
		consent1.save(flush: true , failOnError:true )


		def consent2 = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: template,
				consentDate: new Date([year:2014,month:01,date:01]),
				consentTakerName: "Edward",
				formID: "GEN12345",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patient2).save(flush: true , failOnError:true )
		consent2.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[0]))
		consent2.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[1]))
		consent2.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[2]))
		consent2.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions[3]))
		consent2.save(flush: true , failOnError:true )
	}


	void "dataStatusReport updates consentStatus attribute in all ConsentForm objects"(){

		given:"all consent objects have null/NONE_CONSENT in consentStatus attribute"

		databaseCleanupService.consentEvaluationService = Mock(ConsentEvaluationService)

		AddConsentFormWithoutConsentStatus()
		Attachment.count()	 == 2
		ConsentForm.count()	 == 2
		ConsentForm.list().each { consentForm ->
			assert consentForm?.consentStatus == ConsentForm.ConsentStatus.NON_CONSENT
		}

		when:
		def updateCount = databaseCleanupService.updateAllConsentStatus()

		then:"ConsentStatus of all consentForms will be updated"
		2 * databaseCleanupService.consentEvaluationService.getConsentStatus(_) >>{return ConsentForm.ConsentStatus.CONSENT_WITH_LABELS}
		ConsentForm.list().each { consentForm ->
			assert consentForm?.consentStatus == ConsentForm.ConsentStatus.CONSENT_WITH_LABELS
		}
		updateCount == 2
	}

	void "databaseStatusReports returns reports about DB status"(){

		when:""
		def patient= new Patient(
				givenName: "A",
				familyName: "A",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "123",
				nhsNumber: "5555555555",
				consents: []
		).save(flush: true , failOnError:true )


		def attachment1 = new Attachment(id: 1, fileName: 'a.jpg', dateOfUpload: new Date(),	attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flush: true , failOnError:true )
		def attachment2 = new Attachment(id: 1, fileName: 'a.jpg', dateOfUpload: new Date(),	attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flush: true , failOnError:true )

		//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		def questions1 = [
				new Question(name: 'I read1...'),
				new Question(name: 'I read2...'),
				new Question(name: 'I read3...'),
				new Question(name: 'I read4...')
		];
		def template1=new ConsentFormTemplate(
				id: 1,
				name: "ORB1",
				templateVersion: "1.1",
				namePrefix: "GNR")
				.addToQuestions(questions1[0])
				.addToQuestions(questions1[1])
				.addToQuestions(questions1[2])
				.addToQuestions(questions1[3])
				.save(flush: true , failOnError:true )
		def consent11 = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment1,
				template: template1,
				consentDate: new Date([year:2014,month:01,date:01]),
				consentTakerName: "Edward",
				formID: "GEN12345",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patient).save(flush: true , failOnError:true )
		consent11.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions1[0]))
		consent11.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions1[1]))
		consent11.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions1[2]))
		consent11.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions1[3]))
		consent11.save(flush: true , failOnError:true )

		def consent12 = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment1,
				template: template1,
				consentDate: new Date([year:2014,month:01,date:01]),
				consentTakerName: "Edward",
				formID: "GEN67890",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patient).save(flush: true , failOnError:true )
		consent12.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions1[0]))
		consent12.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions1[1]))
		consent12.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions1[2]))
		consent12.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions1[3]))
		consent12.save(flush: true , failOnError:true )

		//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		def questions2 = [
				new Question(name: 'I read1...'),
				new Question(name: 'I read2...'),
				new Question(name: 'I read3...'),
				new Question(name: 'I read4...')
		];
		def template2=new ConsentFormTemplate(
				id: 1,
				name: "ORB1",
				templateVersion: "1.1",
				namePrefix: "CDR")
				.addToQuestions(questions2[0])
				.addToQuestions(questions2[1])
				.addToQuestions(questions2[2])
				.addToQuestions(questions2[3])
				.save(flush: true , failOnError:true )


		def consent2 = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: template2,
				consentDate: new Date([year:2014,month:01,date:01]),
				consentTakerName: "Edward",
				formID: "CDR12345",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patient).save(flush: true , failOnError:true )
		consent2.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions2[0]))
		consent2.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions2[1]))
		consent2.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions2[2]))
		consent2.addToResponses(new Response(answer: Response.ResponseValue.YES,question: questions2[3]))
		consent2.save(flush: true , failOnError:true )
		//@@@@@@@@@@@@@@@@@@@@@@@@






		//Patient with more than on MRN numbers
		def patient1= new Patient(
				givenName: "A",
				familyName: "A",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "123",
				nhsNumber: "1234567890",
				consents: []
		).save(flush: true , failOnError:true )

		def patient2= new Patient(
				givenName: "Eric",
				familyName: "Clapton",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "456",
				nhsNumber: "1234567890",
				consents: []
		).save(flush: true , failOnError:true )



		//Patient with more than one DOB
		new Patient(
				givenName: "A",
				familyName: "A",
				dateOfBirth: new Date("10/03/1945"),
				hospitalNumber: "123",
				nhsNumber: "8529637410",
				consents: []
		).save(flush: true , failOnError:true )

		new Patient(
				givenName: "B",
				familyName: "B",
				dateOfBirth: new Date("01/03/1945"),
				hospitalNumber: "123",
				nhsNumber: "8529637410",
				consents: []
		).save(flush: true , failOnError:true )



		def patientTest = new Patient(
				givenName: "B",
				familyName: "B",
				dateOfBirth: new Date("01/03/1930"),
				hospitalNumber: "12388",
				nhsNumber: "8529007410",
				consents: []
		).save(flush: true , failOnError:true )


		def patientWithGenericFormID1 = new Patient(
				givenName: "A",
				familyName: "A",
				dateOfBirth: new Date("04/01/1940"),
				hospitalNumber: "18809",
				nhsNumber: "7410009630",
				consents: []
		).save(flush: true, failOnError: true)
		def con = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: template2,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edward",
				formID: "CDR00000",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patientTest).save(flush: true, failOnError: true)
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[0]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[1]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[2]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[3]))
		con.save(flush: true, failOnError: true)


		def patientWithGenericFormID2 = new Patient(
				givenName: "A",
				familyName: "A",
				dateOfBirth: new Date("04/01/1940"),
				hospitalNumber: "18809",
				nhsNumber: "7410009630",
				consents: []
		).save(flush: true, failOnError: true)
		con = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: template2,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edward",
				formID: "GEL00000",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patientTest).save(flush: true, failOnError: true)
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[0]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[1]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[2]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[3]))
		con.save(flush: true, failOnError: true)


		//ADD GEL consentTemplate
		def GELTemp1 = new ConsentFormTemplate(name: "GEL1",templateVersion: "Version 1.0 dated  25.08.2014",namePrefix: "GEL")
				.save(flush: true , failOnError:true )
		def GELTemp2 = new ConsentFormTemplate(name: "GEL1",templateVersion: "Version 2 dated 14.10.14",namePrefix: "GEL")
				.save(flush: true , failOnError:true )

		 new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: GELTemp1,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edward",
				formID: "GEL11000",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patientTest).save(flush: true, failOnError: true)
		new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: GELTemp2,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edward",
				formID: "GEL12000",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patientTest).save(flush: true, failOnError: true)

		//@@@@@@@@@@@@@@@@@@@@@@@@@@@@

		def result = databaseCleanupService.patientDBReport()

		////
		def patientEmptyGivenName = new Patient(
				familyName: "A",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "123",
				nhsNumber: "1010101010",
				consents: []
		).save(flush: true, failOnError: true)
		con = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: template2,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edward",
				formID: "CDR12345",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patientEmptyGivenName).save(flush: true, failOnError: true)
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[0]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[1]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[2]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[3]))
		con.save(flush: true, failOnError: true)
		////
		def patientEmptyHospitalNumber = new Patient(
				givenName: "A",
				familyName: "A",
				dateOfBirth: new Date("30/03/1945"),
				nhsNumber: "1010101010",
				consents: []
		).save(flush: true, failOnError: true)
		con = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: template2,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edward",
				formID: "CDR89123",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patientEmptyHospitalNumber).save(flush: true, failOnError: true)
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[0]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[1]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[2]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[3]))
		con.save(flush: true, failOnError: true)
		////
		def patientEmptyNHSNumber = new Patient(
				givenName: "A",
				familyName: "A",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "123",
				consents: []
		).save(flush: true, failOnError: true)
		con = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: template2,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edward",
				formID: "CDR67123",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patientEmptyNHSNumber).save(flush: true, failOnError: true)
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[0]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[1]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[2]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[3]))
		con.save(flush: true, failOnError: true)



		def patientGenericNHSNumber = new Patient(
				givenName: "A",
				familyName: "A",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "123",
				nhsNumber: "1111111111",
				consents: []
		).save(flush: true, failOnError: true)
		con = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: template2,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edward",
				formID: "CDR67000",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patientGenericNHSNumber).save(flush: true, failOnError: true)
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[0]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[1]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[2]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[3]))
		con.save(flush: true, failOnError: true)




		def patientGenericHospitalNumber = new Patient(
				givenName: "A",
				familyName: "A",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "123",
				nhsNumber: "0000000000",
				consents: []
		).save(flush: true, failOnError: true)
		con = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: template2,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edward",
				formID: "CDR11000",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patientGenericHospitalNumber).save(flush: true, failOnError: true)
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[0]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[1]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[2]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[3]))
		con.save(flush: true, failOnError: true)




		def patientWithSameNHSNumberAndDifferentDOB1 = new Patient(
				givenName: "A",
				familyName: "A",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "123",
				nhsNumber: "7418529630",
				consents: []
		).save(flush: true, failOnError: true)
		con = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: template2,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edward",
				formID: "CDR11000",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patientWithSameNHSNumberAndDifferentDOB1).save(flush: true, failOnError: true)
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[0]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[1]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[2]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[3]))
		con.save(flush: true, failOnError: true)

		def patientWithSameNHSNumberAndDifferentDOB2 = new Patient(
				givenName: "A",
				familyName: "A",
				dateOfBirth: new Date("04/01/1941"),
				hospitalNumber: "123",
				nhsNumber: "7418529630",
				consents: []
		).save(flush: true, failOnError: true)
		con = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: template2,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edward",
				formID: "CDR11000",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patientWithSameNHSNumberAndDifferentDOB2).save(flush: true, failOnError: true)
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[0]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[1]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[2]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[3]))
		con.save(flush: true, failOnError: true)


		def patientWithSameNHSNumberAndDifferentDOB3 = new Patient(
				givenName: "A",
				familyName: "A",
				dateOfBirth: new Date("04/01/1945"),
				hospitalNumber: "123890",
				nhsNumber: "1111111111",
				consents: []
		).save(flush: true, failOnError: true)
		con = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: template2,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edward",
				formID: "CDR11000",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patientWithSameNHSNumberAndDifferentDOB3).save(flush: true, failOnError: true)
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[0]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[1]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[2]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[3]))
		con.save(flush: true, failOnError: true)


		def patientWithSameNHSNumberAndDifferentDOB4 = new Patient(
				givenName: "A",
				familyName: "A",
				dateOfBirth: new Date("04/01/1948"),
				hospitalNumber: "12300",
				nhsNumber: "1111111111",
				consents: []
		).save(flush: true, failOnError: true)
		con = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: template2,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edward",
				formID: "CDR11000",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patientWithSameNHSNumberAndDifferentDOB4).save(flush: true, failOnError: true)
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[0]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[1]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[2]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[3]))
		con.save(flush: true, failOnError: true)




		def patientWithSameHospitalNumberAndDifferentDOB1 = new Patient(
				givenName: "A",
				familyName: "A",
				dateOfBirth: new Date("04/01/1941"),
				hospitalNumber: "18809",
				nhsNumber: "7418529630",
				consents: []
		).save(flush: true, failOnError: true)
		con = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: template2,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edward",
				formID: "CDR11000",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patientWithSameHospitalNumberAndDifferentDOB1).save(flush: true, failOnError: true)
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[0]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[1]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[2]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[3]))
		con.save(flush: true, failOnError: true)

		def patientWithSameHospitalNumberAndDifferentDOB2 = new Patient(
				givenName: "A",
				familyName: "A",
				dateOfBirth: new Date("04/01/1940"),
				hospitalNumber: "18809",
				nhsNumber: "7418529630",
				consents: []
		).save(flush: true, failOnError: true)
		con = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: template2,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edward",
				formID: "CDR11000",
				formStatus: ConsentForm.FormStatus.NORMAL,
				patient: patientWithSameHospitalNumberAndDifferentDOB2).save(flush: true, failOnError: true)
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[0]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[1]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[2]))
		con.addToResponses(new Response(answer: Response.ResponseValue.YES, question: questions2[3]))
		con.save(flush: true, failOnError: true)

		def databaseStatusReport = databaseCleanupService.databaseStatusReports()

		then: ""
		result
		result['consentWithGenericFormID'].size() == 2
		result['GELConsentsV1'].size() == 1
		result['GELConsentsV2'].size() == 1

		databaseStatusReport.size() == 5
		databaseStatusReport['ConsentFormCount'] == 18
		databaseStatusReport['consentFormsWithEmptyFields'].size() == 3
		databaseStatusReport['consentFormWithGenericIDs'].size()   == 4
		databaseStatusReport['nhsNumberWithMoreThanOneDOB'].size() == 1
		databaseStatusReport['nhsNumberWithMoreThanOneDOB']["7418529630"] ==  "1941-04-01 ,1940-04-01 ,1947-06-03 "
		databaseStatusReport['hospitalNumberWithMoreThanOneDOB'].size()  == 2
		databaseStatusReport['hospitalNumberWithMoreThanOneDOB']["123"]  == "1941-04-01 ,1947-06-03 "
		databaseStatusReport['hospitalNumberWithMoreThanOneDOB']["18809"]== "1941-04-01 ,1940-04-01 "
	}


	void "addDefaultValidResponses will add default validResponses to questions which do not have validResponses"(){

		when:
		def criteria = Question.where {
			validResponses?.size() == 0
		}
		def expectedCount = criteria.count()
		def recordsUpdated = databaseCleanupService.addDefaultValidResponses()
		def afterUpdateCount = criteria.count()

		then:
		recordsUpdated   == expectedCount
		afterUpdateCount == 0
	}

	void "updateConsentTemplateVersion will update consentTemplate version"(){

		given:"default consent form template exist"
		//remove all consentFormTemplates
		ConsentFormTemplate.deleteAll(ConsentFormTemplate.list())
		new ConsentFormTemplate(
				name: "Pre-2014 ORB consent form",
				namePrefix: "PRE",
				templateVersion: "Version 1.2 dated 3rd March 2009"
		).save(failOnError: true)

		new ConsentFormTemplate(
				name: "100,000 Genomes Project – Cancer Sequencing Consent Form",
				namePrefix: "GEL",
				templateVersion: "Version 2 dated 14.10.14"
		).save(failOnError: true)

		new ConsentFormTemplate(
				name: "100,000 Genomes Project – Cancer Sequencing Consent Form",
				namePrefix: "GEL",
				templateVersion: "Version 1.0 dated  25.08.2014"
		).save(failOnError: true)

		assert !ConsentFormTemplate.findByNameAndTemplateVersion("Pre-2014 ORB consent form","Version 1.2 dated 03.03.2009")
		assert !ConsentFormTemplate.findByNameAndTemplateVersion("100,000 Genomes Project – Cancer Sequencing Consent Form","Version 2 dated 14.10.2014")
		assert !ConsentFormTemplate.findByNameAndTemplateVersion("100,000 Genomes Project – Cancer Sequencing Consent Form","Version 1.0 dated 25.08.2014")


		when: "updateConsentTemplateVersion called"
		databaseCleanupService.updateConsentTemplateVersion()

		then:"consentTemplate version will be updated"
		ConsentFormTemplate.findByNameAndTemplateVersion("Pre-2014 ORB consent form","Version 1.2 dated 03.03.2009")
		ConsentFormTemplate.findByNameAndTemplateVersion("100,000 Genomes Project – Cancer Sequencing Consent Form","Version 2 dated 14.10.2014")
		ConsentFormTemplate.findByNameAndTemplateVersion("100,000 Genomes Project – Cancer Sequencing Consent Form","Version 1.0 dated 25.08.2014")
	}

	void "updateCDRUniqueId will add uniqueId to consentFormTemplate"(){

		given:"Default consentFormTemplates are available"
		//remove all consentFormTemplates
		ConsentFormTemplate.deleteAll(ConsentFormTemplate.list())
		new ConsentFormTemplate(
				name: "ORB General Consent Form",
				namePrefix: "GEN",
				templateVersion: "v1 October 2013").save(failOnError: true)

		new ConsentFormTemplate(
				name: "ORB Specific Programme Clinically Relevant Genomics - Oncology Consent Form for Adults",
				namePrefix: "CRA",
				templateVersion: "v1 October 2013").save(failOnError: true)

		new ConsentFormTemplate(
				name: "100,000 Genomes Project – Cancer Sequencing Consent Form",
				namePrefix: "GEL",
				templateVersion: "Version 1.0 dated 25.08.2014" //"Version 1.0 dated  25.08.2014"
						).save(failOnError: true)

		new ConsentFormTemplate(
				name: "100,000 Genomes Project – Cancer Sequencing Consent Form",
				namePrefix: "GEL",
				templateVersion: "Version 2 dated 14.10.2014"//"Version 2 dated 14.10.14"
				).save(failOnError: true)

		new ConsentFormTemplate(
				name: "Pre-2014 ORB consent form",
				namePrefix: "PRE",
				templateVersion: "Version 1.2 dated 03.03.2009" //"Version 1.2 dated 3rd March 2009"
				).save(failOnError: true)

		new ConsentFormTemplate(
				name: "ORB General Consent Form",
				namePrefix: "GEN",
				templateVersion: "v2 April 2014").save(failOnError: true)

		assert !ConsentFormTemplate.findByCdrUniqueId("ORB_PRE_V1_2")
		assert !ConsentFormTemplate.findByCdrUniqueId("ORB_GEN_V1")
		assert !ConsentFormTemplate.findByCdrUniqueId("ORB_CRA_V1")
		assert !ConsentFormTemplate.findByCdrUniqueId("GEL_CSC_V1")
		assert !ConsentFormTemplate.findByCdrUniqueId("GEL_CSC_V2")
		assert !ConsentFormTemplate.findByCdrUniqueId("ORB_GEN_V2")

		when:
		databaseCleanupService.updateCDRUniqueId()

		then:
		ConsentFormTemplate.findByCdrUniqueId("ORB_PRE_V1_2")
		ConsentFormTemplate.findByCdrUniqueId("ORB_GEN_V1")
		ConsentFormTemplate.findByCdrUniqueId("ORB_CRA_V1")
		ConsentFormTemplate.findByCdrUniqueId("GEL_CSC_V1")
		ConsentFormTemplate.findByCdrUniqueId("GEL_CSC_V2")
		ConsentFormTemplate.findByCdrUniqueId("ORB_GEN_V2")
	}
}