package uk.ac.ox.brc.greenlight

class ConsentForm {

    Attachment attachedFormImage
    ConsentFormTemplate template

    Date consentDate
    String consentTakerName
    String formID

    FormStatus status = FormStatus.STANDARD
    List<Response> responses


    static belongsTo = [
            patient: Patient
    ]

    static hasMany = [
            responses:Response
    ]

    static constraints = {
        attachedFormImage nullable: true //remove this later :)
        formID matches: '[A-Z]{3}\\d{5}'
    }

    enum FormStatus {
        INVALID,
        DECLINED,
        STANDARD
    }
}