package uk.ac.ox.brc.greenlight

import grails.test.spock.IntegrationSpec
import greenlight.Study

/**
 * Created by soheil on 25/04/2014.
 */

class StudyControllerSpec extends IntegrationSpec {

	def controller = new StudyController()

	def "having one study, index action returns that study"() {

		given:"study already exists"
		Study study = new Study(description: "New Study")

		when:"study is called"
		controller.request.method = "POST"
		controller.index()

		then:"one study will be returned"
		controller.modelAndView.viewName == "/study/index"
		controller.modelAndView.model.description == study.description
	}


	def "updateStudy action updates the singleton study"()	{

		given:"study already exists"
		def newDescription =  "Updated Study"
		def study = new Study(description: "New Study")

		when:"update action is called"
		controller.request.method = "POST"
		controller.params.description = newDescription
		controller.updateStudy()

		then:"redirects to update action and returns success message"
		controller.response.redirectedUrl == "/study/index"
		controller.flash.message == "Successfully updated"

		and:"singleton study is updated"
		Study.first().description == newDescription
	}
}