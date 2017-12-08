package uk.ac.ox.brc.greenlight

import grails.converters.JSON
import grails.converters.XML
import grails.test.mixin.TestFor
import groovy.json.JsonBuilder
import org.codehaus.groovy.grails.web.json.JSONObject
import spock.lang.*
import uk.ac.ox.brc.greenlight.ConsentForm.ConsentStatus

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
		Patient patient = new Patient(givenName: "John Doe", nhsNumber: "12345", hospitalNumber: "NHSOXHOSP1",familyName: "Doe",dateOfBirth: Date.parse("yyy-MM-dd HH:mm:ss","2015-01-25 02:10:00"))


		def formTypes = [
				new ConsentFormTemplate(id: 100, name: "form type 1", namePrefix: "GEN", templateVersion: "1.0", questions: [new Question(name: "q1")]),
				new ConsentFormTemplate(id: 231, name: "form type 2", namePrefix: "CRA", templateVersion: "2.0", questions: [new Question(name: "q2"),new Question(name: "q3")])
		]

		def latestConsentForms = [
				new ConsentForm(formID: "GEL123", consentStatus: ConsentStatus.FULL_CONSENT,consentDate: Date.parse("yyy-MM-dd HH:mm:ss","2015-05-21 14:10:00"),template: formTypes[0], responses: [new Response(question: formTypes[0].questions[0], answer: Response.ResponseValue.YES)]),
				new ConsentForm(formID: "GEL456", consentStatus: ConsentStatus.NON_CONSENT ,consentDate: Date.parse("yyy-MM-dd HH:mm:ss","2015-05-12 14:10:00"),template: formTypes[1], responses: [new Response(question: formTypes[1].questions[0], answer: Response.ResponseValue.YES), new Response(question: formTypes[1].questions[1], answer: Response.ResponseValue.NO)])
		]



		when: "The request contains a valid ID"
		request.forwardURI = requestURL
		params.lookupId = lookupId
		controller.getStatus()

		then: "The controller responds with the consent status"
		1 * controller.patientService.findAllByNHSOrHospitalNumber(lookupId) >> [patient]
		1 * controller.consentFormService.getLatestConsentForms([patient], ConsentForm.FormStatus.NORMAL) >> latestConsentForms

		model.stringInstanceMap == [
				_self: requestURL,
				errors: false,
				nhsNumber: patient.nhsNumber,
				hospitalNumber: patient.hospitalNumber,
				firstName: patient.givenName,
				lastName: patient.familyName,
				dateOfBirth: "25-01-2015 02:10:00",
				consents: [
						[form: [namePrefix:formTypes[0].namePrefix, name: formTypes[0].name, version: formTypes[0].templateVersion],lastCompleted: "21-05-2015 14:10:00", consentStatus: ConsentStatus.FULL_CONSENT.name(), consentTakerName: latestConsentForms[0].consentTakerName,consentFormId:latestConsentForms[0].formID,consentStatusLabels:""],
						[form: [namePrefix:formTypes[1].namePrefix, name: formTypes[1].name, version: formTypes[1].templateVersion],lastCompleted: "12-05-2015 14:10:00",consentStatus: ConsentStatus.NON_CONSENT.name(), consentTakerName: latestConsentForms[1].consentTakerName,consentFormId:latestConsentForms[1].formID,consentStatusLabels:""]
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
		1 * controller.patientService.findAllByNHSOrHospitalNumber(lookupId) >> []

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
		Patient patient = new Patient(givenName: "John Doe", nhsNumber: "12345", hospitalNumber: "NHSOXHOSP1",familyName: "Doe",dateOfBirth: Date.parse("yyy-MM-dd HH:mm:ss","2015-01-25 02:10:00"))


		def formTypes = [
				new ConsentFormTemplate(id: 100, name: "form type 1", namePrefix: "GEN", templateVersion: "1.0", questions: [new Question(name: "q1")]),
				new ConsentFormTemplate(id: 231, name: "form type 2", namePrefix: "CRA", templateVersion: "2.0", questions: [new Question(name: "q2"),new Question(name: "q3")])
		]

		def latestConsentForms = [
				new ConsentForm(formID: "GEL123", consentStatus: ConsentStatus.FULL_CONSENT,consentDate: Date.parse("yyy-MM-dd HH:mm:ss","2015-05-25 14:10:00"), template: formTypes[0], responses: [new Response(question: formTypes[0].questions[0], answer: Response.ResponseValue.YES)],consentTakerName: "User1"),
				new ConsentForm(formID: "GEL456", consentStatus: ConsentStatus.NON_CONSENT ,consentDate: Date.parse("yyy-MM-dd HH:mm:ss","2015-04-12 14:10:00"),template: formTypes[1], responses: [new Response(question: formTypes[1].questions[0], answer: Response.ResponseValue.YES), new Response(question: formTypes[1].questions[1], answer: Response.ResponseValue.NO)],consentTakerName: "User2")
		]



		when: "The request contains a valid ID"
		request.forwardURI = requestURL
		params.lookupId = lookupId
		controller.getStatus()

		then: "The controller responds with the consent status"
		1 * controller.patientService.findAllByNHSOrHospitalNumber(lookupId) >> [patient]
		1 * controller.consentFormService.getLatestConsentForms([patient], ConsentForm.FormStatus.NORMAL) >> latestConsentForms

		model.stringInstanceMap == [
				_self: requestURL,
				errors: false,
				nhsNumber: patient.nhsNumber,
				hospitalNumber: patient.hospitalNumber,
				firstName: patient.givenName,
				lastName: patient.familyName,
				dateOfBirth: "25-01-2015 02:10:00",
				consents: [
						[form: [namePrefix:formTypes[0].namePrefix,name: formTypes[0].name, version: formTypes[0].templateVersion],lastCompleted: "25-05-2015 14:10:00", consentStatus: ConsentStatus.FULL_CONSENT.name(), consentTakerName:latestConsentForms[0].consentTakerName,consentFormId:latestConsentForms[0].formID,consentStatusLabels:""],
						[form: [namePrefix:formTypes[1].namePrefix,name: formTypes[1].name, version: formTypes[1].templateVersion],lastCompleted: "12-04-2015 14:10:00", consentStatus: ConsentStatus.NON_CONSENT.name(), consentTakerName:latestConsentForms[1].consentTakerName,consentFormId:latestConsentForms[1].formID,consentStatusLabels:""]
				]
		]
	}

	def "Check if JSON marshaller will return the dates in a proper format"(){

		String requestURL = "/api/aNiceURL"
		String lookupId = "12345"
		Patient patient = new Patient(givenName: "John Doe", nhsNumber: "12345", hospitalNumber: "NHSOXHOSP1",familyName: "Doe",dateOfBirth: Date.parse("yyy-MM-dd HH:mm:ss","2015-01-25 02:10:00"))


		def formTypes = [
				new ConsentFormTemplate(id: 100, name: "form type 1", namePrefix: "GEN", templateVersion: "1.0", questions: [new Question(name: "q1")]),
				new ConsentFormTemplate(id: 231, name: "form type 2", namePrefix: "CRA", templateVersion: "2.0", questions: [new Question(name: "q2"),new Question(name: "q3")])
		]

		def latestConsentForms = [
				new ConsentForm(formID: "GEL123", consentStatus: ConsentStatus.FULL_CONSENT, template: formTypes[0], responses: [new Response(question: formTypes[0].questions[0], answer: Response.ResponseValue.YES)],consentTakerName: "User1"),
				new ConsentForm(formID: "GEL456", consentStatus: ConsentStatus.NON_CONSENT ,template: formTypes[1], responses: [new Response(question: formTypes[1].questions[0], answer: Response.ResponseValue.YES), new Response(question: formTypes[1].questions[1], answer: Response.ResponseValue.NO)],consentTakerName: "User2")
		]



		when: "The request contains a valid ID"
		request.forwardURI = requestURL
		params.lookupId = lookupId
		controller.params.format = "json"
		controller.getStatus()


		then: "The controller responds with the consent status"
		1 * controller.patientService.findAllByNHSOrHospitalNumber(lookupId) >> [patient]
		1 * controller.consentFormService.getLatestConsentForms([patient], ConsentForm.FormStatus.NORMAL) >> latestConsentForms


		def expected = new groovy.json.JsonSlurper().parseText ("""
		 {"dateOfBirth":"25-01-2015 02:10:00",
		  "consents":[{"consentTakerName":"User1","form":{"name":"form type 1","namePrefix":"GEN","version":"1.0"},"consentFormId":"GEL123","consentStatusLabels":"","consentStatus":"FULL_CONSENT","lastCompleted":null},
		  			  {"consentTakerName":"User2","form":{"name":"form type 2","namePrefix":"CRA","version":"2.0"},"consentFormId":"GEL456","consentStatusLabels":"","consentStatus":"NON_CONSENT","lastCompleted":null}],
		  "lastName":"Doe","errors":false,"hospitalNumber":"NHSOXHOSP1","nhsNumber":"12345","firstName":"John Doe","_self":"/api/aNiceURL"}"
		""");
		def actualJsonString = controller.response.json.toString()
		def actual = new groovy.json.JsonSlurper().parseText (actualJsonString)
		assert actual == expected
	}
	def "Check if XML marshaller will return the dates in a proper format"() {

		String requestURL = "/api/aNiceURL"
		String lookupId = "12345"
		Patient patient = new Patient(givenName: "John Doe", nhsNumber: "12345", hospitalNumber: "NHSOXHOSP1",familyName: "Doe",dateOfBirth: Date.parse("yyy-MM-dd HH:mm:ss","2015-01-25 02:10:00"))


		def formTypes = [
				new ConsentFormTemplate(id: 100, name: "form type 1", namePrefix: "GEN", templateVersion: "1.0", questions: [new Question(name: "q1")]),
				new ConsentFormTemplate(id: 231, name: "form type 2", namePrefix: "CRA", templateVersion: "2.0", questions: [new Question(name: "q2"),new Question(name: "q3")])
		]

		def latestConsentForms = [
				new ConsentForm(formID: "GEL123", consentStatus: ConsentStatus.FULL_CONSENT, template: formTypes[0], responses: [new Response(question: formTypes[0].questions[0], answer: Response.ResponseValue.YES)],consentTakerName: "User1"),
				new ConsentForm(formID: "GEL456", consentStatus: ConsentStatus.NON_CONSENT ,template: formTypes[1], responses: [new Response(question: formTypes[1].questions[0], answer: Response.ResponseValue.YES), new Response(question: formTypes[1].questions[1], answer: Response.ResponseValue.NO)],consentTakerName: "User2")
		]



		when: "The request contains a valid ID"
		request.forwardURI = requestURL
		params.lookupId = lookupId
		controller.params.format = "xml"
		controller.getStatus()


		then: "The controller responds with the consent status"
		1 * controller.patientService.findAllByNHSOrHospitalNumber(lookupId) >> [patient]
		1 * controller.consentFormService.getLatestConsentForms([patient], ConsentForm.FormStatus.NORMAL) >> latestConsentForms

		controller.response.text == "<?xml version=\"1.0\" encoding=\"UTF-8\"?><map><entry key=\"_self\">/api/aNiceURL</entry><entry key=\"errors\">false</entry><entry key=\"nhsNumber\">12345</entry><entry key=\"hospitalNumber\">NHSOXHOSP1</entry><entry key=\"firstName\">John Doe</entry><entry key=\"lastName\">Doe</entry><entry key=\"dateOfBirth\">25-01-2015 02:10:00</entry><entry key=\"consents\"><map><entry key=\"form\"><entry key=\"name\">form type 1</entry><entry key=\"version\">1.0</entry><entry key=\"namePrefix\">GEN</entry></entry><entry key=\"lastCompleted\" /><entry key=\"consentStatus\">FULL_CONSENT</entry><entry key=\"consentTakerName\">User1</entry><entry key=\"consentFormId\">GEL123</entry><entry key=\"consentStatusLabels\"></entry></map><map><entry key=\"form\"><entry key=\"name\">form type 2</entry><entry key=\"version\">2.0</entry><entry key=\"namePrefix\">CRA</entry></entry><entry key=\"lastCompleted\" /><entry key=\"consentStatus\">NON_CONSENT</entry><entry key=\"consentTakerName\">User2</entry><entry key=\"consentFormId\">GEL456</entry><entry key=\"consentStatusLabels\"></entry></map></entry></map>"

	}


}
