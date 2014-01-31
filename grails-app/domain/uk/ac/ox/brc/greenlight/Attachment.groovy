package uk.ac.ox.brc.greenlight

class Attachment {

    byte[] content
    Date dateOfUpload
    AttachmentType attachmentType
    String fileName

    static belongsTo = [
            consentForm: ConsentForm
    ]

    static constraints = {
        consentForm nullable: true
        content maxSize: 1024*1024*100
    }

    enum AttachmentType{
        IMAGE,
        PDF
    }
}

