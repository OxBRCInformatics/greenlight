package uk.ac.ox.brc.greenlight.Audit

import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(RequestLogService)
@Mock(RequestLog)
class RequestLogServiceSpec extends Specification {

	def setup(){
		service.springSecurityService = Mock(SpringSecurityService)
	}

    def "add creates a new RequestLog for CutUpRoom"() {
		when:
		def requestString  = "simpleNHS"
		def responseString = "consented=true {name:'ABC',...}"
		service.add(requestString,responseString,RequestLog.RequestType.CutUpRoom)
		def obj = RequestLog.first()

		then:
		1 * service.springSecurityService.getCurrentUser() >> {return null}
		obj.userRole == null
		obj.requestString	== requestString
		//obj.responseString	== responseString
		obj.requestType  	== RequestLog.RequestType.CutUpRoom
    }

	def "add creates a new RequestLog for REST_API"() {
		when:
		def requestString  = "simpleNHS"
		def responseString = "consented=true {name:'ABC',...}"
		service.add(requestString,responseString,RequestLog.RequestType.REST_API)
		def obj = RequestLog.first()

		then:
		1 * service.springSecurityService.getCurrentUser() >> {return [username:"API_USERNAME"]}
		obj.userRole == "API_USERNAME"
		obj.requestString	== requestString
		//obj.responseString	== responseString
		obj.requestType  	== RequestLog.RequestType.REST_API
	}

	def "add doesn't throw exception in case of error"() {
		when:
		def requestString  = null
		def responseString = null
		service.add(requestString,responseString,RequestLog.RequestType.REST_API)

		then:
		1 * service.springSecurityService.getCurrentUser() >> {return null}
		RequestLog.count() == 0
	}
}