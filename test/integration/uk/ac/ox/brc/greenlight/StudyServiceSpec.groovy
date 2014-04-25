package uk.ac.ox.brc.greenlight

import grails.test.spock.IntegrationSpec
import greenlight.Study
import spock.lang.Specification

/**
 * Created by soheil on 25/04/2014.
 */

class StudyServiceSpec extends IntegrationSpec {

	def studyService = new StudyService()


	def "having no studies, getStudy will create a new singleton study"(){

		given:"there is no study"
		Study.count() == 0

		when:"getStudy is called"
		studyService.getStudy()

		then:"one study will be created and returned"
		Study.count() == 1
		Study.first().description == "New Study"
	}


	def "having no studies, updateStudy will create a new singleton study"(){

		given:"there is no study"
		Study.count() == 0

		when:"getStudy is called"
		Study study = studyService.getStudy()

		then:"one study will be created and returned"
		Study.count() == 1
		study.description == "New Study"
	}



	def "having one study, getStudy will return that one"(){

		given:"there is one study"
		new Study(description: "A New Study").save(failOnError: true)
		def expectedStudy = Study.first()
		Study.count() == 1

		when:"getStudy is called"
		Study actualStudy = studyService.getStudy()

		then:"that study will be returned"
		expectedStudy.id == actualStudy.id
	}


	def "having one study, updateStudy will update that one"(){

		given:"there is one study"
		new Study(description: "A new Study").save(failOnError: true)
		Study.count() == 1

		when:"updateStudy is called"
		def updatedStudy = studyService.updateStudy("updated Study")

		then:"that study will be updated and returned"
		Study.first().description == "updated Study"
		updatedStudy.description ==  "updated Study"
	}
}