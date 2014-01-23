package uk.ac.ox.brc.greenlight

class ConsentForm {

    byte[] scannedForm
    Date dateOfScan




    static belongsTo = [
            patientConsent: PatientConsent
    ]

    static constraints = {
        patientConsent nullable: true
        scannedForm maxSize: 1024*1024*100
    }
}
