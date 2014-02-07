package uk.ac.ox.brc.greenlight

class ConsentForm {

    Attachment attachedFormImage
    ConsentFormTemplate template

    Date consentDate
    String consentTakerName
    String formID
    FormStatus formStatus = FormStatus.STANDARD

    FormStatus status = FormStatus.STANDARD
    List<Response> responses

    static auditable = true

    static belongsTo = [
            patient: Patient
    ]

    static hasMany = [
            responses:Response
    ]

    static constraints = {
        attachedFormImage nullable: true //remove this later :)
        //formID matches: '[A-Z]{3}\\d{5}' //FIXME it latter '[A-Z]{3}\\d{5}'
        template nullable: true //FIXME remove this :)
    }

    enum FormStatus {
        STANDARD("Standard"),INVALID("Invalid"), DECLINED("Declined");
        final String value;
        FormStatus(String value) { this.value = value; }
        String toString() { value; }
        String getKey() { name(); }
    }


}