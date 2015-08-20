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

    List<Response> responses
    String comment

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
    }

    static constraints = {
		accessGUID nullable: false , unique: true
        attachedFormImage nullable: true //remove this later :)
        formID matches: '[a-zA-Z]{3}\\d{5}'
        consentDate nullable: true
        consentTakerName nullable: true
        patient nullable: true
        comment nullable: true
		consentStatus nullable: true
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


}