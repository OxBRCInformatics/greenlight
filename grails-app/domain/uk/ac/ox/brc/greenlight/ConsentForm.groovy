package uk.ac.ox.brc.greenlight

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class ConsentForm {

    Attachment attachedFormImage
    ConsentFormTemplate template

	String accessGUID

    Date consentDate
    String consentTakerName
    String formID
    FormStatus formStatus = FormStatus.NORMAL
	ConsentStatus consentStatus = ConsentStatus.NON_CONSENT

	boolean passedToCDR
	boolean savedInCDR
	boolean persistedInCDR
	Date dateTimePassedToCDR
	String savedInCDRStatus
	
    List<Response> responses
    String comment

	String consentStatusLabels

    //static auditable = true

    static belongsTo = [
            patient: Patient
    ]

    static hasMany = [
            responses:Response
    ]

    static mapping = {
        responses cascade: 'all-delete-orphan'
        comment type: "text"
		savedInCDRStatus type: "text"
    }

    static constraints = {
		passedToCDR nullable:true, defaultValue:false
		dateTimePassedToCDR nullable: true
		savedInCDRStatus nullable:true
		savedInCDR nullable:true

		accessGUID nullable: false , unique: true

        attachedFormImage nullable: true //remove this later :)
        formID matches: '[a-zA-Z]{3}\\d{5}'
        consentDate nullable: true
        consentTakerName nullable: true
        patient nullable: true
        comment nullable: true
		consentStatus nullable: true
		consentStatusLabels nullable: true
    }

    enum FormStatus {
        NORMAL("Normal"),SPOILED("Spoiled"), DECLINED("Declined");
        final String value;
        FormStatus(String value) { this.value = value; }
        String toString() { value; }
        String getKey() { name(); }
    }

	enum ConsentStatus
	{
		FULL_CONSENT("Full consent"),
		NON_CONSENT("No consent"),
		CONSENT_WITH_LABELS("Consent with restrictions")

		private final String label

		ConsentStatus(String label){
			this.label = label
		}
		String toString() { label; }
		String getKey() { name(); }
	}


	public boolean equals(Object obj) {
		if (obj instanceof ConsentForm) {
			if (template?.id == obj?.template?.id  &&
				consentDate?.compareTo(obj?.consentDate) == 0 &&
				consentTakerName?.toLowerCase() == obj?.consentTakerName?.toLowerCase() &&
				formID?.toLowerCase() == obj?.formID?.toLowerCase() &&
				formStatus == obj?.formStatus &&
				consentStatus == obj?.consentStatus &&
				comment?.toLowerCase() == obj?.comment?.toLowerCase())
					return true
		}
		return false
	}

	public boolean isChanged() {

		if(this.isDirty()) {
			return true
		}
		//check if responses are updated
		def responsesUpdated = false
		responses?.each { response ->
			if (response.isDirty()) {
				responsesUpdated =  true
				return
			}
		}
		if(responsesUpdated){
			return true
		}

		false
	}
}