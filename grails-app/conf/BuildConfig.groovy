grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
//    test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
       test:false,
    // configure settings for the run-app JVM
    //run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    run: false,
    // configure settings for the run-war JVM
    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility


    def gebVersion = "0.9.2"
    def seleniumVersion = "2.37.0"



    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()

        // For Geb snapshot
        mavenRepo "http://oss.sonatype.org/content/repositories/snapshots"

        // Spring Security
        mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://repo.spring.io/milestone/"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
        // runtime 'mysql:mysql-connector-java:5.1.24'

		test "org.grails:grails-datastore-test-support:1.0-grails-2.4"

        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes
        compile 'com.googlecode.json-simple:json-simple:1.1.1'
        //compile "org.compass-project:compass:2.2.1" // Removed search for 0.1

        // Testing modules
        test("org.seleniumhq.selenium:selenium-chrome-driver:$seleniumVersion")
        test("org.seleniumhq.selenium:selenium-firefox-driver:$seleniumVersion")

        // You usually only need one of these, but this project uses both
        test "org.gebish:geb-spock:$gebVersion"
        test "org.gebish:geb-junit4:$gebVersion"

		// PDF conversion
		//compile 'org.apache.pdfbox:pdfbox:1.8.8'
    }

    plugins {
        // plugins for the build system only
        build ":tomcat:7.0.55"

        // plugins for the compile step
        compile ":scaffolding:2.1.2"
        compile ':cache:1.1.7'
		compile ":asset-pipeline:1.9.6"

		// plugins needed at runtime but not for compilation
        runtime ":hibernate:3.6.10.17" // or ":hibernate4:4.1.11.6"
        runtime ":database-migration:1.4.0"
        runtime ":jquery:1.11.1"

        // Spring Security
		compile ":spring-security-core:2.0-RC4"
		compile ":spring-security-ui:1.0-RC2"
        compile ":jquery-ui:1.10.3"
        compile ":famfamfam:1.0"
		compile ':mail:1.0.7', {
			excludes 'spring-test'
		}

		//Spring Security REST
		compile ":spring-security-rest:1.4.1.RC1", {
			excludes: 'spring-security-core'
		}

		//This is a REST client builder
		//it can be used in Greenlight clients to call its API endpoints
		//We just use it to show how to call Greenlight API endpoints and
		//added some test for that in GreenlightAPISpec
		compile ":rest-client-builder:2.0.3"


		// Audit logging
		compile ":audit-logging:1.0.5"

        test ":geb:$gebVersion"
    }
}
