package uk.ac.ox.brc.greenlight

import com.odobo.grails.plugin.springsecurity.rest.token.storage.TokenNotFoundException
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GrailsUser
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.spock.IntegrationSpec
import groovy.time.TimeCategory
import org.springframework.security.core.userdetails.UserDetailsService
import uk.ac.ox.brc.greenlight.GormTokenStorageService
import uk.ac.ox.brc.greenlight.*
import uk.ac.ox.brc.greenlight.auth.*

/**
 * Created by soheil on 02/11/2014.
 */

@TestFor(GormTokenStorageService)
@Mock([AuthenticationToken,AppRole,AppUser,UserRole])
class GormTokenStorageServiceSpec extends IntegrationSpec {

	def setup() {
		service.userDetailsService = Mock(UserDetailsService)

		//create API role
		def apiRole = new AppRole(authority: 'ROLE_API').save(flush: true)

		//create two sample API Users
		def apiUser1 = new AppUser(username: 'apiUser1', enabled: true, password: 'apiUser1')
		def apiUser2 = new AppUser(username: 'apiUser2', enabled: true, password: 'apiUser2')
		apiUser1.save(flush: true)
		apiUser2.save(flush: true)

		//Assign Role_API to those users
		UserRole.create apiUser1, apiRole, true
		UserRole.create apiUser2, apiRole, true
	}

	def private storeToken(token, username) {
		def principal = [username: username]
		service.storeToken(token, principal)
		def tokenObject = AuthenticationToken.first()
		tokenObject
	}

	def "storeToken will create a token for the logged in user"() {
		when: "storeToken called"
		def token = "9h4rodinkvv66cef10cm8s7hcmjdk45q"
		def username = "apiUser1"
		def tokenObject = storeToken(token, username)

		then: "token object will be saved for the user"
		tokenObject
		tokenObject.tokenValue == token
		tokenObject.username == username
		tokenObject.username == username
		tokenObject.dateCreated
		tokenObject.lastTimeUpdated
	}


	def "storeToken will also remove all expired tokens"(){

		setup:"One expired token is already available"
		def expiredToken0 = new AuthenticationToken(tokenValue:"EXPIRED-TOKEN0",username:"apiUser1",lastTimeUpdated: new Date()).save(flush: true,failOnError: true)
		def expiredToken1 = new AuthenticationToken(tokenValue:"EXPIRED-TOKEN1",username:"apiUser1",lastTimeUpdated: new Date()).save(flush: true,failOnError: true)
		def expiredToken2 = new AuthenticationToken(tokenValue:"EXPIRED-TOKEN2",username:"apiUser1",lastTimeUpdated: new Date()).save(flush: true,failOnError: true)
		def validToken = new AuthenticationToken(tokenValue:"OLD-VALID-TOKEN",username:"apiUser1",lastTimeUpdated: new Date()).save(flush: true,failOnError: true)

		//Try to expire expiredToken0,expiredToken1,expiredToken2
		//tokenExpiry is in second (so 3600 mean an hour)
		def tokenExpiry = SpringSecurityUtils.securityConfig.rest.token.storage.gorm.expiration
		use(TimeCategory) {

			def now = new Date()
			//lastTimeUpdated, an hour+5minutes ago
			expiredToken0.lastTimeUpdated = now - (tokenExpiry + 5*60).seconds
			expiredToken0.save(flush: true,failOnError: true)

			//lastTimeUpdated, an hour+30minutes ago
			expiredToken1.lastTimeUpdated = now - (tokenExpiry + 3*60).seconds
			expiredToken1.save(flush: true,failOnError: true)

			//lastTimeUpdated, an 2hours ago
			expiredToken2.lastTimeUpdated = now - (tokenExpiry + tokenExpiry).seconds
			expiredToken2.save(flush: true,failOnError: true)

			//valid token, updated a 15 minutes ago
			validToken.lastTimeUpdated = now - 15.minutes
			validToken.save(flush: true,failOnError: true)
		}

		when:"storeToken"
		storeToken("VALID-TOKEN","apiUser2")

		then:"expired token will be removed and valid tokens are available"
		AuthenticationToken.list().size() == 2
		AuthenticationToken.list()[0].tokenValue == "OLD-VALID-TOKEN"
		AuthenticationToken.list()[1].tokenValue == "VALID-TOKEN"
	}


	def "loadUserByToken loads logged in api user"() {
		given: "token is stored for the logged in api user"
		def token = "9h4rodinkvv66cef10cm8s7hcmjdk45q"
		def username = "apiUser1"
		storeToken(token, username)

		when: "loadUserByToken"
		1 * service.userDetailsService.loadUserByUsername(username) >> {
			new GrailsUser(username, "", true, false, false, false, [], 1)
		}
		def userObj = service.loadUserByToken(token)

		then: "returns the user"
		userObj.username == username
	}

	def "loadUserByToken throws exception for not available token"() {
		when: "loadUserByToken"
		service.loadUserByToken("NOT_AVAILABLE")

		then: "throws TokenNotFoundException"
		thrown TokenNotFoundException
	}

	def "removeToken will remove the token"() {
		given: "token is stored for the logged in api user"
		def token = "9h4rodinkvv66cef10cm8s7hcmjdk45q"
		def username = "apiUser1"
		storeToken(token, username)
		assert AuthenticationToken.list().size() == 1

		when: "removeToken"
		service.removeToken(token)

		then: "will remove the token"
		AuthenticationToken.list().size() == 0
	}

	def "removeToken will throw exception if the token doesn't exist"() {

		when: "removeToken"
		service.removeToken("INVALID TOKEN")

		then: "throws TokenNotFoundException"
		thrown TokenNotFoundException
	}

	def "findExistingToken will return the existing token"() {
		given: "a token is saved"
		def token = "9h4rodinkvv66cef10cm8s7hcmjdk45q"
		def username = "apiUser1"
		storeToken(token, username)

		when: "findExistingToken"
		AuthenticationToken tokenObject = service.findExistingToken(token)

		then: "will return the token object"
		tokenObject.username == username
		tokenObject.tokenValue == token
		tokenObject.dateCreated
		tokenObject.lastTimeUpdated
	}


	def "findExistingToken will update the token lastUpdated"() {
		given: "a token is saved"
		def token = "9h4rodinkvv66cef10cm8s7hcmjdk45q"
		def username = "apiUser1"
		def storedTime = new Date()
		storeToken(token, username)

		when: "findExistingToken called"
		//delay 5 seconds before retrieving the token
		def delay = 5
		sleep delay*1000
		AuthenticationToken tokenObject = service.findExistingToken(token)

		def findTime
		use(TimeCategory) {
			findTime = storedTime + delay.seconds
		}

		then: "it will update the lastUpdated property of the token"
		tokenObject.lastTimeUpdated >= findTime

	}

	def "findExistingToken will return null if the token doesn't exist"() {
		when: "findExistingToken is called with un-available token"
		def tokenObject = service.findExistingToken("NOT_AVAILABLE_TOKEN")

		then: "returns null"
		!tokenObject
	}

}
