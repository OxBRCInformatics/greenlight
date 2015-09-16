package uk.ac.ox.brc.greenlight.Audit

import org.codehaus.groovy.grails.plugins.orm.auditable.Stamp

/**
 * Created by soheil on 17/08/2015.
 * Log all the requests for searching patient consent in REST_API and also CUT-UP room
 */
@Stamp
class RequestLog {

	String userRole
	String requestString
	//String responseString
	RequestType requestType

	static constraints = {
		userRole nullable: true
		requestString nullable: false, blank: true
		//responseString nullable: true , type: 'text'
		requestType nullable: false
	}

	enum RequestType
	{
		REST_API("Rest API"),
		CutUpRoom("Cut-up Room")

		private final String label

		RequestType(String label){
			this.label = label
		}
		String toString() { label; }
		String getKey() { name(); }
	}
}

