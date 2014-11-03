package uk.ac.ox.brc.greenlight

import com.odobo.grails.plugin.springsecurity.rest.token.storage.TokenNotFoundException
import com.odobo.grails.plugin.springsecurity.rest.token.storage.TokenStorageService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.test.mixin.TestFor
import groovy.time.TimeCategory
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware
import org.springframework.security.core.userdetails.UserDetailsService
import uk.ac.ox.brc.greenlight.auth.AuthenticationToken

/**
 * GORM implementation for token storage. It will look for tokens on the DB using a domain class that will contain the
 * generated token and the username associated.
 *
 * Once the username is found, it will delegate to the configured {@link UserDetailsService} for obtaining authorities
 * information.
 */
class GormTokenStorageService implements TokenStorageService, GrailsApplicationAware {

	/** Dependency injection for the application. */
	GrailsApplication grailsApplication

	UserDetailsService userDetailsService

	Object loadUserByToken(String tokenValue) throws TokenNotFoundException {
		def conf = SpringSecurityUtils.securityConfig
		String usernamePropertyName = conf.rest.token.storage.gorm.usernamePropertyName
		def existingToken = findExistingToken(tokenValue)

		if (existingToken) {
			def username = existingToken."${usernamePropertyName}"
			return userDetailsService.loadUserByUsername(username)
		}

		throw new TokenNotFoundException("Token ${tokenValue.mask()} not found")

	}

	/*
	 * Added by soheil 2.11.2014 (in a dar and gray Sunday!)
	 * As removing expired tokens is not implemented in GORM version of Spring Security REST Plugin
	 * We try to remove expired tokens, in each storeToken
	 * so each time someone asks for saving a new token (in login),
	 * we remove old expired tokens
	 * This process could be done by using a Job scheduler like Quartz
	 * http://grails.org/plugin/quartz
	 * but for now this is the approach that we have selected
	 */

	void storeToken(String tokenValue, Object principal) {
		def conf = SpringSecurityUtils.securityConfig
		String tokenClassName = conf.rest.token.storage.gorm.tokenDomainClassName
		String tokenValuePropertyName = conf.rest.token.storage.gorm.tokenValuePropertyName
		String usernamePropertyName = conf.rest.token.storage.gorm.usernamePropertyName
		String lastTimeUpdatedPropertyName = conf.rest.token.storage.gorm.lastTimeUpdatedPropertyName

		def dc = grailsApplication.getClassForName(tokenClassName)

		//TODO check at startup, not here
		if (!dc) {
			throw new IllegalArgumentException("The specified token domain class '$tokenClassName' is not a domain class ")
		}

		dc.withTransaction { status ->
			def newTokenObject = dc.newInstance((tokenValuePropertyName): tokenValue, (usernamePropertyName): principal.username,(lastTimeUpdatedPropertyName):new Date())
			newTokenObject.save(flush: true)
		}

		//tokenExpiry is in second (so 3600 mean an hour)
		def tokenExpiry = SpringSecurityUtils.securityConfig.rest.token.storage.gorm.expiration
		use(TimeCategory) {
			def now = new Date()
			//find all tokens created before 'now-tokenExpiry'
			//if tokenExpiry is an hour, so now-tokenExpiry means those tokens that were created
			//before an hour ago
			def before =  now - tokenExpiry.seconds
			def expiredTokens = AuthenticationToken."findAllBy${lastTimeUpdatedPropertyName.capitalize()}LessThan"(before)
			AuthenticationToken.withTransaction { status ->
				expiredTokens.each{ expiredToken ->
					expiredToken.delete(flush:true)
				}
			}
			//AuthenticationToken.deleteAll(expiredTokens)
		}

	}

	void removeToken(String tokenValue) throws TokenNotFoundException {
		def existingToken = findExistingToken(tokenValue)
		if (existingToken) {
			existingToken.delete(flush: true)
		} else {
			throw new TokenNotFoundException("Token ${tokenValue.mask()} not found")
		}
	}

	private findExistingToken(String tokenValue) {
		def conf = SpringSecurityUtils.securityConfig
		String tokenClassName = conf.rest.token.storage.gorm.tokenDomainClassName
		String tokenValuePropertyName = conf.rest.token.storage.gorm.tokenValuePropertyName

		/**
		 * Added by Soheil on 27.10.2014
		 * lastUpdatedPropertyName will save the last time that the token was accessed
		 */
		String lastTimeUpdatedPropertyName = conf.rest.token.storage.gorm.lastTimeUpdatedPropertyName


		def dc = grailsApplication.getClassForName(tokenClassName)

		//TODO check at startup, not here
		if (!dc) {
			throw new IllegalArgumentException("The specified token domain class '$tokenClassName' is not a domain class")
		}

		def token
		dc.withTransaction { status ->
			token = dc.findWhere((tokenValuePropertyName): tokenValue)

			/**
			 * Added by Soheil on 27.10.2014
			 * if lastUpdatedPropertyName is provided,
			 * then update it and the token will be reNewed
			 */
			if (token && lastTimeUpdatedPropertyName) {
				token."${lastTimeUpdatedPropertyName}" = new Date()
				token.save(flush: true)
			}
		}

		return token
	}

}

