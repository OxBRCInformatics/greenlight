package uk.ac.ox.brc.greenlight.Audit

import uk.ac.ox.brc.greenlight.ConsentForm

class CDRLog {

	//all details of the consent and the consent template that needed for CDR
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

	//all details of the patient that needed for CDR
	String patientId
	String nhsNumber
	String hospitalNumber

	//date and time that this action happened
	Date actionDate
	//the type of action
	CDRActionType action
	//Is the log actually SUCCESSFULLY passed to CDR?
	boolean persistedInCDR
	//the result of passing a message to CDR
	String resultDetail
	static mapping = {
		comment type:"text"
		resultDetail type: "text"
		consentStatusLabels type:"text"
		attemptsLog type:"text"
	}

	static constraints = {

		consentFormId nullable:true // for New consentForm which do not have Id at first BUT we can refer to them by their accessGUID
		consentTemplateId nullable:true

		comment nullable:true
		consentStatusLabels nullable: true

		patientId nullable: true
		nhsNumber nullable:true
		hospitalNumber nullable:true


		attemptsLog nullable:true
		attemptsCount nullable:true
		resultDetail nullable: true
	}


	enum CDRActionType {
		ADD("Add"),
		REMOVE("Remove")
		final String value;
		CDRActionType(String value) { this.value = value; }
		String toString() { value; }
		String getKey() { name(); }
	}
}
