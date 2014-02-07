package uk.ac.ox.brc.greenlight

class ConsentForm {

    Attachment attachedFormImage
    ConsentFormTemplate template

    Date consentDate
    String consentTakerName
    String formID
    FormStatus formStatus = FormStatus.STANDARD
    List<Response> responses
    String comment

    static auditable = true

    static belongsTo = [
            patient: Patient
    ]

    static hasMany = [
            responses:Response
    ]

    static constraints = {
        attachedFormImage nullable: true //remove this later :)
        formID matches: '[a-zA-Z]{3}\\d{5}'
        template nullable: true
        consentDate nullable: true
        consentTakerName nullable: true
        patient nullable: true
        comment nullable: true
    }

    enum FormStatus {
        STANDARD("Standard"),INVALID("Invalid"), DECLINED("Declined");
        final String value;
        FormStatus(String value) { this.value = value; }
        String toString() { value; }
        String getKey() { name(); }
    }


}