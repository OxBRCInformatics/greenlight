import grails.plugin.springsecurity.SpringSecurityUtils

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

grails.config.locations = [ "classpath:${appName}-config.properties",
                             "classpath:${appName}-config.groovy",
                             "file:${userHome}/.grails/${appName}-config.properties",
                             "file:${userHome}/.grails/${appName}-config.groovy"]

// Some JavaScript references will fail if this is changed. An example is the IE7 polyfill for Bootstrap3.
grails.app.context = "/"

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
 
grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

// log4j configuration
log4j = {
    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}

// Audit logging - grab user ID as well as changes
auditLog {
    actorClosure = { request, session ->
        if (request.applicationContext.springSecurityService.principal instanceof java.lang.String){
            return request.applicationContext.springSecurityService.principal
        }
        def username = request.applicationContext.springSecurityService.principal?.username
        if (SpringSecurityUtils.isSwitched()){
            username = SpringSecurityUtils.switchedUserOriginalUsername+" AS "+username
        }
        return username
    }
}


grails{
    plugin{
        springsecurity{

			password.algorithm = 'SHA-256'
			password.hash.iterations = 1
			logout.postOnly = false

			// page to redirect to if a login attempt fails
            failureHandler.defaultFailureUrl = '/login/authfail/?login_error=1'

            // redirection page for success (including successful registration
            //successHandler.defaultTargetUrl = '/dashboard/'

            // Added by the Spring Security Core plugin:
            userLookup.userDomainClassName = 'uk.ac.ox.brc.greenlight.auth.AppUser'
            userLookup.authorityJoinClassName = 'uk.ac.ox.brc.greenlight.auth.UserRole'
            authority.className = 'uk.ac.ox.brc.greenlight.auth.AppRole'

            //disable to prevent double encryption of passwords
            ui.encodePassword = false

            // User registration: don't add user to any roles by default (this is done by an admin to approve the account)
            ui.register.defaultRoleNames = ['ROLE_PENDING']

            // Grails security password requirements
            ui.password.minLength=8
            ui.password.maxLength=64

            securityConfigType = SecurityConfigType.InterceptUrlMap
            //useSecurityEventListener = true

            securityConfigType = "Annotation"
            controllerAnnotations.staticRules = [
                    '/':                            ['permitAll'],
					'/index':             ['permitAll'],
					'/index.gsp':         ['permitAll'],
                    // Asset pipeline
                    '/assets/**':           ['permitAll'],

					'/bower_compoennts/**': ['permitAll'],

                    // Javascript
					'/js/**':      			['permitAll'],
                    '/js/vendor/**':  		['permitAll'],
                    '/plugins/**/js/**':	['permitAll'],
                    // CSS
                    '/**/css/**':      		['permitAll'],
                    '/css/**': 				['permitAll'],
                    '/**/*.less':           ['permitAll'],
                    // Images
                    '/images/**': 			['permitAll'],
                    '/img/**': 				['permitAll'],

                    // Anonymously acessible pages, e.g. registration & login
                    '/login/*':    			['permitAll'],
                    '/logout/*':    		['permitAll'],
                    //'/register/*':    		['permitAll'],

					// Allow anonymous access to cut up room page and results
					'/consentForm/checkConsent':	['permitAll'],
					'/consentForm/cuttingRoom': 	['permitAll'],
					'/mainAngularApp/index': 		['permitAll'],
					'/mainAngularApp':		 		['permitAll'],

					//just admin access
					'/securityInfo/**': ["hasRole('ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY'],
					'/role': ["hasRole('ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY'],
					'/role/**': ["hasRole('ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY'],
					'/registrationCode': ["hasRole('ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY'],
					'/registrationCode/**': ["hasRole('ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY'],
					'/user': ["hasRole('ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY'],
					'/user/**': ["hasRole('ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY'],
					'/aclClass': ["hasRole('ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY'],
					'/aclClass/**': ["hasRole('ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY'],
					'/aclSid': ["hasRole('ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY'],
					'/aclSid/**': ["hasRole('ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY'],
					'/aclEntry': ["hasRole('ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY'],
					'/aclEntry/**': ["hasRole('ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY'],
					'/aclObjectIdentity': ["hasRole('ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY'],
					'/testConnection/**': ["hasRole('ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY'],

					// Need to be logged in for anything else!
                    '/**':         			["hasAnyRole('ROLE_USER', 'ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY']
            ]
        }
    }
}



//Long story short: JOINED_FILTERS refers to all the configured filters.
//The minus (-) notation means all the previous values but the neglected one.
//Add the mapping here like '/api/consents' BUT
//as configured in UrlMappings, this mapping points to
// ConsentStatusController controller, getStatus action
//[It seems that, action must be mentioned , otherwise @Secured on top of it does not work
//so in ConsentStatusController, we have added @Secured to restrict access for specific roles
grails.plugin.springsecurity.filterChain.chainMap = [
		'/api/**': 'JOINED_FILTERS,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter',  // Stateless chain
		'/api/consents/*': 'JOINED_FILTERS,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter',  // Stateless chain
		'/**': 'JOINED_FILTERS,-restTokenValidationFilter,-restExceptionTranslationFilter'                                                                          // Traditional chain
]


grails.plugin.springsecurity.rest.token.storage.useGorm = true
grails.plugin.springsecurity.rest.token.storage.gorm.tokenDomainClassName	= "uk.ac.ox.brc.greenlight.auth.AuthenticationToken"
grails.plugin.springsecurity.rest.token.storage.gorm.tokenValuePropertyName	= "tokenValue"
grails.plugin.springsecurity.rest.token.storage.gorm.usernamePropertyName	= "username"
grails.plugin.springsecurity.rest.token.storage.gorm.dateCreatedPropertyName	= "dateCreated"
grails.plugin.springsecurity.rest.token.storage.gorm.lastTimeUpdatedPropertyName	= "lastTimeUpdated"
grails.plugin.springsecurity.rest.token.storage.gorm.expiration	= 3600 //token will be expired 1hr after their latest access

//Stop minify and uglify to speed up the build process
grails.assets.minifyJs = false

//EPDS settings
epds.conString.username = "USERNAME"
epds.conString.password = "PASSWORD"
epds.conString.url='jdbc:oracle:thin:@serverName:1521:SIDName'