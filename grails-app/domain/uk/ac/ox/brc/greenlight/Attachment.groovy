package uk.ac.ox.brc.greenlight

class Attachment {

    byte[] scannedForm
    Date dateOfScan




    static belongsTo = [
            consentForm: ConsentForm
    ]

    static constraints = {
        consentForm nullable: true
        scannedForm maxSize: 1024*1024*100
    }
}
