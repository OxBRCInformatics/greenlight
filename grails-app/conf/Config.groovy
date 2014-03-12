import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

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
    plugins{
        springsecurity{

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
            useSecurityEventListener = true

            securityConfigType = "Annotation"
            controllerAnnotations.staticRules = [
                    '/':                            ['IS_AUTHENTICATED_ANONYMOUSLY'],

                    // Asset pipeline
                    '/assets/**':           ['IS_AUTHENTICATED_ANONYMOUSLY'],

                    // Javascript
                    '/js/**':      			['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/js/vendor/**':  		['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/plugins/**/js/**':	['IS_AUTHENTICATED_ANONYMOUSLY'],
                    // CSS
                    '/**/css/**':      		['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/css/**': 				['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/**/*.less':           ['IS_AUTHENTICATED_ANONYMOUSLY'],
                    // Images
                    '/images/**': 			['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/img/**': 				['IS_AUTHENTICATED_ANONYMOUSLY'],

                    // Anonymously acessible pages, e.g. registration & login
                    '/login/*':    			['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/logout/*':    		['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/register/*':    		['IS_AUTHENTICATED_ANONYMOUSLY'],

                    // Need to be logged in for anything else!
                    '/**':         			["hasAnyRole('ROLE_USER', 'ROLE_ADMIN')",'IS_AUTHENTICATED_FULLY']
            ]
        }
    }
}