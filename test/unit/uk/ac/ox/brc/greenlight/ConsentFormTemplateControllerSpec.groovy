package uk.ac.ox.brc.greenlight

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by soheil on 24/06/2015.
 */
@TestFor(ConsentFormTemplateController)
@Mock(ConsentFormTemplate)
class ConsentFormTemplateControllerSpec  extends Specification {

	def setup(){
		new ConsentFormTemplate(name: "A",namePrefix: "AG",templateVersion: "v1 April 2014").save(failOnError: true,flush: true)
		new ConsentFormTemplate(name: "B",namePrefix: "BG",templateVersion: "v4-2015").save(failOnError: true,flush: true)
		new ConsentFormTemplate(name: "A",namePrefix: "AG",templateVersion: "v2 April 2014").save(failOnError: true,flush: true)
	}

	def "list will return all consentTemplate ordered By name and version"(){

		when:
		controller.list()

		then:
		controller.modelAndView.model.consentFormTemplates.size() == 3
		controller.modelAndView.model.consentFormTemplates[0].name == "A"
		controller.modelAndView.model.consentFormTemplates[0].templateVersion == "v1 April 2014"
		controller.modelAndView.model.consentFormTemplates[2].name == "B"
		controller.modelAndView.model.consentFormTemplates[2].templateVersion == "v4-2015"
	}
}