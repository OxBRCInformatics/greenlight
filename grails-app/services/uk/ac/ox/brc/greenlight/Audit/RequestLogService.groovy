package uk.ac.ox.brc.greenlight.Audit

import grails.transaction.Transactional

@Transactional
class RequestLogService {

	def springSecurityService

	def add(requestString,responseString,requestType) {


		if(!requestString || requestString.isEmpty()){
			return
		}
		def currentUser = springSecurityService?.currentUser
		try {
			new RequestLog(
					userRole: (currentUser ? currentUser?.username : ""),
					requestString: requestString,
					//responseString: responseString,
					requestType: requestType).save(failOnError: true)
		}catch (Exception ex){
			//Ignore the exception as this is a RequestLog and it should not effect the main process
		}
	}
}