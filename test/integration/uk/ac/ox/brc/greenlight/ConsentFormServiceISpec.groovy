package uk.ac.ox.brc.greenlight

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.spock.IntegrationSpec
import spock.lang.Specification

/**
 * Created by soheil on 28/03/2014.
 *
 * FIXME this should be a unit test, with mocks for domain objects.
 */
class ConsentFormServiceISpec extends IntegrationSpec {

    def consentFormService


    def "Delete action will delete consentForm and its responses"() {

		given:
		def attachment= new Attachment(id: 1, fileName: 'a.jpg', dateOfUpload: new Date(),
				attachmentType: Attachment.AttachmentType.IMAGE, content: []).save()

		def template=new ConsentFormTemplate(
				id: 1,
				name: "ORB1",
				templateVersion: "1.1",
				namePrefix: "GNR",
		).addToQuestions(new Question(name: 'I read1...')
		).save()


		def patient= new Patient(
				givenName: "Eric",
				familyName: "Clapton",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "1002",
				nhsNumber: "1234567890",
				consents: []
		).save()

		def consent = new ConsentForm(
				attachedFormImage: attachment,
				template: template,
				patient: patient,
				consentDate: new Date([year:2014,month:01,date:01]),
				consentTakerName: "Edward",
				formID: "GEN12345",
				formStatus: ConsentForm.FormStatus.NORMAL,
		).save()

		consent.addToResponses(new Response(answer: Response.ResponseValue.YES))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES))
		consent.save()

        when:"Delete action is called"
        ConsentForm.count() == 1
        def cons= ConsentForm.first()
        cons.responses.size() == 2
        consentFormService.delete(cons)


        then:"It deletes the consentForm and its responses"
        ConsentForm.count() == 0
        Response.count() == 0
        Patient.count() == 1
    }
}