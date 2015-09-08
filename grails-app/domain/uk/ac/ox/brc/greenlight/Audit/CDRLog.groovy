package uk.ac.ox.brc.greenlight.Audit

import uk.ac.ox.brc.greenlight.ConsentForm

class CDRLog {

	String consentFormId
	String consentTemplateId
	Date consentDate
	ConsentForm.ConsentStatus consentStatus
	String comment
	String consentStatusLabels
	String cdrUniqueId
	String namePrefix
	String consentURL
	String consentAccessGUID

	String nhsNumber
	String hospitalNumber

	Date actionDate
	String action
	boolean persistedInCDR
	String resultDetail

	static constraints = {

		consentFormId nullable:true
		consentTemplateId nullable:true

		comment type:"text" , nullable:true
		consentStatusLabels nullable: true

		nhsNumber nullable:true
		hospitalNumber nullable:true

		resultDetail type: "text"
		consentStatusLabels type:"text"
	}
}
