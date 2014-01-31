package uk.ac.ox.brc.greenlight

class Attachment {

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
