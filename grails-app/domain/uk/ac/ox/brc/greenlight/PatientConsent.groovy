package uk.ac.ox.brc.greenlight

class PatientConsent {

    ConsentForm consentForm

    String clinicianName

    static belongsTo = [
        patient: Patient
    ]

    static constraints = {
        consentForm nullable: true //remove this later :)
    }
}
