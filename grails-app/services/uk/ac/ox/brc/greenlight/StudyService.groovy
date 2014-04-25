package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional
import greenlight.Study

@Transactional
class StudyService {

	Study updateStudy(String description) {

		def study = Study.first()
		if(!study){
			study = new Study()
		}
		study.description = description
		study.save(flush: true)
		return study
	}

	Study getStudy() {

		def study = Study.first()
		if(!study){
			study = new Study(description:"New Study").save(flush: true)
		}
		return study
	}

}
