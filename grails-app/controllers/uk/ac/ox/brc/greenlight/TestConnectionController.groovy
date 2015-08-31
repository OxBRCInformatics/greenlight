package uk.ac.ox.brc.greenlight

import grails.plugin.springsecurity.annotation.Secured

class TestConnectionController {

	def demographicService
	def CDRService

	@Secured("ROLE_ADMIN")
	def index() {
		def result = demographicService.testConnection()
		render model:[result:result],view:"index"
	}

	@Secured("ROLE_ADMIN")
	def cdr() {
		def result = CDRService.testConnection()
		render model:[result:result],view:"cdr"
	}
}
