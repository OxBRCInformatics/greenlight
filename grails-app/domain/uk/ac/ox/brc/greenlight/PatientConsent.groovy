package uk.ac.ox.brc.greenlight

import java.util.Date;

class PatientConsent {

    ConsentForm consentForm
    ConsentStatus consentStatus
    Date date
	
	boolean answer1
	boolean answer2
	boolean answer3
	boolean answer4
	boolean answer5
	boolean answer6
	boolean answer7
	boolean answer8
	boolean answer9
	boolean answer10
	

    String clinicianName

    static belongsTo = [
            patient: Patient
    ]

    static constraints = {
        consentForm nullable: true //remove this later :)
		
		
    }

    enum ConsentStatus
    {
        UNKOWN,
        ALL_CONSENTED,
        NOT_CONSENTED
    }

}

@grails.validation.Validateable
class InputFormCommand {

	
	PatientConsent patientConsentInstance
	ConsentForm consentFormInstance
	Patient patientInstance
	
	
	static constraints = {
	}
}

