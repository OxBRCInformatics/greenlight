package uk.ac.ox.brc.greenlight


import spock.lang.*

/**
 * added by soheil 08.2015
 */
class ConsentFormISpec extends Specification {


	void "isChanged checks if the object is changed"() {

		given:"ConsentForm exists"
		createAnnotatedAttachments()

		when:"Properties of a ConsentForm is changed"
		def con = ConsentForm.findByConsentTakerName("ABC1")
		con.consentTakerName = "DEF1"

		then:"isChanged returns true"
		con.isChanged()

		when:"Responses are updated"
		con = ConsentForm.findByConsentTakerName("ABC2")
		con.responses[0].answer = Response.ResponseValue.BLANK

		then:"isChanged returns true"
		con.isChanged()

		when:"ConsentForm is not changed"
		con = ConsentForm.findByConsentTakerName("ABC3")

		then:"isChanged returns false"
		!con.isChanged()
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
				name: "ORB2",
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
				consentTakerName: "ABC1",
				formID: "ABC12345",
				formStatus: ConsentForm.FormStatus.NORMAL,
				consentStatus: ConsentForm.ConsentStatus.CONSENT_WITH_LABELS.CONSENT_WITH_LABELS
		).save(flush: true);
		consent1.addToResponses(new Response(question: template1.questions[0],answer: Response.ResponseValue.YES))
		consent1.save(failOnError: true)



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
				consentTakerName: "ABC2",
				formID: "DEF12345",
				consentStatus: ConsentForm.ConsentStatus.CONSENT_WITH_LABELS.FULL_CONSENT,
				formStatus: ConsentForm.FormStatus.DECLINED
		).save();
		consent2.addToResponses(new Response(question: template1.questions[0],answer: Response.ResponseValue.YES))
		consent2.save(failOnError: true)


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
				consentTakerName: "ABC3",
				formID: "GHI12345",
				formStatus: ConsentForm.FormStatus.SPOILED,
				consentStatus: ConsentForm.ConsentStatus.CONSENT_WITH_LABELS.NON_CONSENT
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

}
