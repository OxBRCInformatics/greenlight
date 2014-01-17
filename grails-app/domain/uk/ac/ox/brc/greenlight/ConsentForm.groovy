package uk.ac.ox.brc.greenlight

class ConsentForm {

    byte[] scannedForm
    Date dateOfConsent

    static belongsTo = [
            patientConsent: PatientConsent
    ]
    static constraints = {
        patientConsent nullable: true
        scannedForm maxSize: 1024*1024*1000
    }
}
