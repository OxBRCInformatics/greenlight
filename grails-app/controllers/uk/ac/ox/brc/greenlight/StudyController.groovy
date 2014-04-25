package uk.ac.ox.brc.greenlight

import greenlight.Study

class StudyController {

	def studyService

	def index() {
		def  study = studyService.getStudy()
		render view:"index", model:[description:study.description]
	}

	def updateStudy() {

		def study

		try{
			study = studyService.updateStudy(params["description"])
			flash.message = "Successfully updated"
		}
		catch (Exception exception){
			flash.error = "Error in updating Study!"
			log.error("In updateStudy "+ exception.message)
		}

		redirect action:"index", model:[study:study]
	}
}
