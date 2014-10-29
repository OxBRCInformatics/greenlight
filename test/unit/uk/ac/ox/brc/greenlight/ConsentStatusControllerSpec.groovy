package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import spock.lang.*

/**
 *
 */
@TestFor(ConsentStatusController)
class ConsentStatusControllerSpec extends Specification {

	def setup() {
		controller.patientService = Mock(PatientService)
		controller.consentFormService = Mock(ConsentFormService)
		controller.consentEvaluationService = Mock(ConsentEvaluationService)
	}

	def "getStatus returns valid consent status responses for valid patient IDs"(){

		given:
		String requestURL = "/api/aNiceURL"
		String lookupId = "12345"
		Patient patient = new Patient(givenName: "John Doe", nhsNumber: "12345", hospitalNumber: "NHSOXHOSP1",familyName: "Doe",dateOfBirth: new Date(2000,1,1))


		def formTypes = [
				new ConsentFormTemplate(id: 100, name: "form type 1", namePrefix: "GEN", templateVersion: "1.0", questions: [new Question(name: "q1")]),
				new ConsentFormTemplate(id: 231, name: "form type 2", namePrefix: "CRA", templateVersion: "2.0", questions: [new Question(name: "q2"),new Question(name: "q3")])
		]

		def latestConsentForms = [
				new ConsentForm(formID: "GEL123", template: formTypes[0], responses: [new Response(question: formTypes[0].questions[0], answer: Response.ResponseValue.YES)]),
				new ConsentForm(formID: "GEL456",template: formTypes[1], responses: [new Response(question: formTypes[1].questions[0], answer: Response.ResponseValue.YES), new Response(question: formTypes[1].questions[1], answer: Response.ResponseValue.NO)])
		]



		when: "The request contains a valid ID"
		request.forwardURI = requestURL
		params.lookupId = lookupId
		controller.getStatus()

		then: "The controller responds with the consent status"
		1 * controller.patientService.findByNHSOrHospitalNumber(lookupId) >> patient
		1 * controller.consentFormService.getLatestConsentForms(patient) >> latestConsentForms
		2 * controller.consentEvaluationService.getConsentStatus(_) >>> [ConsentStatus.FULL_CONSENT, ConsentStatus.NON_CONSENT]

		model.stringInstanceMap == [
				_self: requestURL,
				errors: false,
				nhsNumber: patient.nhsNumber,
				hospitalNumber: patient.hospitalNumber,
				firstName: patient.givenName,
				lastName: patient.familyName,
				dateOfBirth: patient.dateOfBirth,
				consents: [
						[form: [name: formTypes[0].name, version: formTypes[0].templateVersion],lastCompleted: latestConsentForms[0].consentDate, consentStatus: ConsentStatus.FULL_CONSENT.name(), consentTakerName: latestConsentForms[0].consentTakerName,consentFormId:latestConsentForms[0].formID],
						[form: [name: formTypes[1].name, version: formTypes[1].templateVersion], lastCompleted: latestConsentForms[1].consentDate, consentStatus: ConsentStatus.NON_CONSENT.name(), consentTakerName: latestConsentForms[1].consentTakerName,consentFormId:latestConsentForms[1].formID]
				]
		]
	}

	def "getStatus handles bogus lookupId value #lookupId"(){
		given:
		String requestURL = "/api/someURL"
		request.forwardURI = requestURL

		when: "I call the controller with a nonexistant lookupID"
		params.lookupId = lookupId
		controller.getStatus()

		then: "The controller responds with an error message"
		model.stringInstanceMap == [
				_self: requestURL,
				errors: true,
				message:"The lookup ID could not be found"
		]
		1 * controller.patientService.findByNHSOrHospitalNumber(lookupId) >> null

		where:
		lookupId << ["124141241", '1', "null", "something", "undefined"]
	}

	def "getStatus handles no input"() {

		given:
		String requestURL = "/api/someOtherURL"
		request.forwardURI = requestURL

		when: "I call the controller with no param set"
		controller.getStatus()

		then: "The controller responds with an error message"
		model.stringInstanceMap == [
				_self: requestURL,
				errors: true,
				message: "A lookup ID must be provided for 'lookupId'"
		]

		when: "I call the controller with lookupId set to null"
		request.forwardURI = requestURL
		params.lookupId = null
		controller.getStatus()

		then: "The controller responds with an error message"
		model.stringInstanceMap == [
				_self: requestURL,
				errors: true,
				message: "A lookup ID must be provided for 'lookupId'"
		]
	}


	def "getStatus returns patient demographics"(){

		given:
		String requestURL = "/api/aNiceURL"
		String lookupId = "12345"
		Patient patient = new Patient(givenName: "John Doe", nhsNumber: "12345", hospitalNumber: "NHSOXHOSP1",familyName: "Doe",dateOfBirth: new Date(2000,1,1))


		def formTypes = [
				new ConsentFormTemplate(id: 100, name: "form type 1", namePrefix: "GEN", templateVersion: "1.0", questions: [new Question(name: "q1")]),
				new ConsentFormTemplate(id: 231, name: "form type 2", namePrefix: "CRA", templateVersion: "2.0", questions: [new Question(name: "q2"),new Question(name: "q3")])
		]

		def latestConsentForms = [
				new ConsentForm(formID: "GEL123", template: formTypes[0], responses: [new Response(question: formTypes[0].questions[0], answer: Response.ResponseValue.YES)],consentTakerName: "User1"),
				new ConsentForm(formID: "GEL456",template: formTypes[1], responses: [new Response(question: formTypes[1].questions[0], answer: Response.ResponseValue.YES), new Response(question: formTypes[1].questions[1], answer: Response.ResponseValue.NO)],consentTakerName: "User2")
		]



		when: "The request contains a valid ID"
		request.forwardURI = requestURL
		params.lookupId = lookupId
		controller.getStatus()

		then: "The controller responds with the consent status"
		1 * controller.patientService.findByNHSOrHospitalNumber(lookupId) >> patient
		1 * controller.consentFormService.getLatestConsentForms(patient) >> latestConsentForms
		2 * controller.consentEvaluationService.getConsentStatus(_) >>> [ConsentStatus.FULL_CONSENT, ConsentStatus.NON_CONSENT]

		model.stringInstanceMap == [
				_self: requestURL,
				errors: false,
				nhsNumber: patient.nhsNumber,
				hospitalNumber: patient.hospitalNumber,
				firstName: patient.givenName,
				lastName: patient.familyName,
				dateOfBirth: patient.dateOfBirth,
				consents: [
						[form: [name: formTypes[0].name, version: formTypes[0].templateVersion],lastCompleted: latestConsentForms[0].consentDate, consentStatus: ConsentStatus.FULL_CONSENT.name(), consentTakerName:latestConsentForms[0].consentTakerName,consentFormId:latestConsentForms[0].formID],
						[form: [name: formTypes[1].name, version: formTypes[1].templateVersion], lastCompleted: latestConsentForms[1].consentDate, consentStatus: ConsentStatus.NON_CONSENT.name(), consentTakerName:latestConsentForms[1].consentTakerName,consentFormId:latestConsentForms[1].formID]
				]
		]
	}


}
