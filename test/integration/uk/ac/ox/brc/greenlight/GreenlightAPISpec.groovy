package uk.ac.ox.brc.greenlight

import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder
import grails.test.spock.IntegrationSpec
import grails.web.JSONBuilder
import groovy.json.JsonSlurper
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators

/**
 * Created by soheil on 03/11/2014.
 * The aim of this test is mostly to demonstrate how to call Greenlight endpoint API
 *
 * We had to add integration test for this as in unit test
 * we faced some problem in
 * JSON converter in line 38
 * org.apache.commons.lang.UnhandledException: org.codehaus.groovy.grails.web.converters.exceptions.ConverterException: Unconvertable Object of class: java.util.LinkedHashMap
 * There are other solutions for this problem
 * http://stackoverflow.com/questions/11785708/how-do-i-unit-test-a-grails-service-that-uses-a-converter
 */
class GreenlightAPISpec extends IntegrationSpec {

	def "Login"() {
		given: "Greenlight server is running at http://greenlight_ADDRESS_.com"
		//create a mock reply for server
		def builder = new JSONBuilder()
		JSON responseJon = builder.build {
			username = "api"
			roles = ["ROLE_API"]
			access_token = "27dr90vau681mmtr8c14n8halnjqi694"
			token_type = "Bearer"
		}

		//create a mock server that replies to requests to http://greenlight.com/api/login/
		def rest = new RestBuilder()
		final mockServer = MockRestServiceServer.createServer(rest.restTemplate)
		mockServer.expect(MockRestRequestMatchers.requestTo("http://GREENLIGHT_ADDRESS_.com/api/login"))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
				.andRespond(MockRestResponseCreators.withSuccess(responseJon.toString(), MediaType.APPLICATION_JSON))

		when: "Login request is issued"
		//This is the process that happens on client side
		//create user,pass json body
		JSON requestJson = builder.build {
			username = "API_USER_USERNAME"
			password = "API_USER_PASSWORD"
		}
		//call API endpoint
		def response = rest.post("http://GREENLIGHT_ADDRESS_.com/api/login") {
			body requestJson
		}

		//process the response
		def jsonSlurper = new JsonSlurper()
		def responseObject = jsonSlurper.parseText(response.body)

		then: "server response contains token value"
		responseObject == [username: "api",
				roles: ["ROLE_API"],
				access_token: "27dr90vau681mmtr8c14n8halnjqi694",
				token_type: "Bearer"]
		mockServer.verify()
	}

	def "validate"() {
		given: "Greenlight server is running at http://GREENLIGHT_ADDRESS_.com"
		//create a mock reply for server
		def builder = new JSONBuilder()
		JSON responseJon = builder.build {
			username = "api"
			roles = ["ROLE_API"]
			access_token = "27dr90vau681mmtr8c14n8halnjqi694"
			token_type = "Bearer"
		}

		//create a mock server that replies to requests to http://greenlight.com/api/vlidate/
		def rest = new RestBuilder()
		final mockServer = MockRestServiceServer.createServer(rest.restTemplate)
		mockServer.expect(MockRestRequestMatchers.requestTo("http://GREENLIGHT_ADDRESS_.com/api/validate"))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
				.andExpect(MockRestRequestMatchers.header("Authorization", "Bearer 72sns5aoniq891er2r4kjo558g083jsg"))
				.andRespond(MockRestResponseCreators.withSuccess(responseJon.toString(), MediaType.APPLICATION_JSON))

		when: "Login request is issued"
		//This is the process that happens on client side
		//call API endpoint
		def response = rest.post("http://GREENLIGHT_ADDRESS_.com/api/validate") {
			auth "Bearer 72sns5aoniq891er2r4kjo558g083jsg"
		}

		//process the response
		def jsonSlurper = new JsonSlurper()
		def responseObject = jsonSlurper.parseText(response.body)

		then: "server response contains token value and other details"
		responseObject == [username: "api",
				roles: ["ROLE_API"],
				access_token: "27dr90vau681mmtr8c14n8halnjqi694",
				token_type: "Bearer"]
		mockServer.verify()

	}

	def "Calling Greenlight REST API endpoint"() {
		given: "Greenlight server is running at http://GREENLIGHT_ADDRESS_.com"
		//create a mock reply for server
		//This is just a sample reply, it actually have more fields
		def builder = new JSONBuilder()
		JSON responseJon = builder.build {
			nhsNumber = "1234567890"
			hospitalNumber = "A"
			dateOfBirth = "2014-11-03T00:00:00Z"
			consents = [
					{
						form= {
							name= "ORB"
							version= "1.0"
						}
						lastCompleted= "2014-11-03T00:00:00Z"
						consentStatus= "FULL_CONSENT"
					},
					{
						form= {
							name= "GEL"
							version= "1.0"
						}
						lastCompleted= "2014-11-03T00:00:00Z"
						consentStatus= "FULL_CONSENT"
					}
			]
		}

		//create a mock server that replies to requests to http://greenlight.com/api/consents/
		def rest = new RestBuilder()
		final mockServer = MockRestServiceServer.createServer(rest.restTemplate)
		mockServer.expect(MockRestRequestMatchers.requestTo("http://GREENLIGHT_ADDRESS_.com/api/consents/1234567890.json"))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
				.andExpect(MockRestRequestMatchers.header("Authorization", "Bearer 72sns5aoniq891er2r4kjo558g083jsg"))
				.andRespond(MockRestResponseCreators.withSuccess(responseJon.toString(), MediaType.APPLICATION_JSON))

		when: "Login request is issued"
		//This is the process that happens on client side
		//call API endpoint and pass NHSNumber or MRN number
		def nhsNumber = "1234567890"
		def response = rest.post("http://GREENLIGHT_ADDRESS_.com/api/consents/${nhsNumber}.json") {
			auth "Bearer 72sns5aoniq891er2r4kjo558g083jsg"
		}

		//process the response
		def jsonSlurper = new JsonSlurper()
		def responseObject = jsonSlurper.parseText(response.body)

		then: "server response contains token value"
		responseObject == [
				nhsNumber: "1234567890",
				hospitalNumber: "A",
				dateOfBirth: "2014-11-03T00:00:00Z",
				consents: [
							[
								form : [name: "ORB",
									    version: "1.0"],
								lastCompleted: "2014-11-03T00:00:00Z",
								consentStatus: "FULL_CONSENT"
							],
							[
								form:[	name: "GEL",
									    version: "1.0"	],
								lastCompleted: "2014-11-03T00:00:00Z",
								consentStatus: "FULL_CONSENT"
							]
				]
		]
		mockServer.verify()
	}


	def "logout"() {
		given: "Greenlight server is running at http://GREENLIGHT_ADDRESS_.com"
		//create a mock server that replies to requests to http://greenlight.com/api/logout/
		def rest = new RestBuilder()
		final mockServer = MockRestServiceServer.createServer(rest.restTemplate)
		mockServer.expect(MockRestRequestMatchers.requestTo("http://GREENLIGHT_ADDRESS_.com/api/logout"))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
				.andExpect(MockRestRequestMatchers.header("Authorization", "Bearer 72sns5aoniq891er2r4kjo558g083jsg"))
				.andRespond(MockRestResponseCreators.withSuccess("", MediaType.APPLICATION_JSON))

		when: "Login request is issued"
		//This is the process that happens on client side
		//call API endpoint
		def response = rest.post("http://GREENLIGHT_ADDRESS_.com/api/logout") {
			auth "Bearer 72sns5aoniq891er2r4kjo558g083jsg"
		}

		//a successful logout should have 200HTTP status
		//and an empty body
		then: "server response contains token value"
		response != null
		response.status == 200
		response.body  == ""
		mockServer.verify()
	}
}