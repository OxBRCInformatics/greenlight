package uk.ac.ox.brc.greenlight.Audit

class CDRLog {

	String consentId
	String consentTemplateId
	String nhsNumber
	String hospitalNumber
	String action
	String result
	String resultDetail

	static constraints = {
		resultDetail type: "text"
	}
}
