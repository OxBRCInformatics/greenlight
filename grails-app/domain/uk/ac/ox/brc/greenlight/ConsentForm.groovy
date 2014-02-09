package uk.ac.ox.brc.greenlight

class ConsentForm {

    Attachment attachedFormImage
    ConsentFormTemplate template

    Date consentDate
    String consentTakerName
    String formID
<<<<<<< HEAD
    FormStatus formStatus = FormStatus.NORMAL

    FormStatus status = FormStatus.NORMAL
=======
    FormStatus formStatus = FormStatus.STANDARD
>>>>>>> d9df7640840b145cfe6c705558f1c943db9c254a
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
        NORMAL("Normal"),SPOILED("Spoiled"), DECLINED("Declined");
        final String value;
        FormStatus(String value) { this.value = value; }
        String toString() { value; }
        String getKey() { name(); }
    }


}