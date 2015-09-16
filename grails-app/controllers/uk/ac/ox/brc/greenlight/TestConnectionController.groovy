package uk.ac.ox.brc.greenlight

import grails.plugin.springsecurity.annotation.Secured

class TestConnectionController {

	def demographicService
	def CDRService
	def grailsLinkGenerator

	@Secured("ROLE_ADMIN")
	def index() {
		def result = demographicService.testConnection()
		render model:[result:result],view:"index"
	}

	@Secured("ROLE_ADMIN")
	def cdr() {
		def result = CDRService.findPatient("TEST_TEST_TEST","TEST_TEST_TEST")
		result['serverLink'] = "${grailsLinkGenerator.serverBaseURL}"
		render model:[result:result],view:"cdr"
	}
}
