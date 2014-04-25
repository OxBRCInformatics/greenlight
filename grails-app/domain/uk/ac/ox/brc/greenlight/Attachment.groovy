package uk.ac.ox.brc.greenlight

class Attachment {

    byte[] content // deprecated. Use the fileUrl instead
	String fileUrl // The location of the attachment file on disk. Replaces `content`

    Date dateOfUpload
    AttachmentType attachmentType
    String fileName // name of file as it was uploaded

    static belongsTo = [
            consentForm: ConsentForm
    ]

    static constraints = {
		content nullable: true
		fileUrl nullable: true // Just until we've migrated old attachments over. Remove in 2.0
        consentForm nullable: true
        content maxSize: 1024*1024*10
    }

    enum AttachmentType{
        IMAGE,
        PDF
    }
}

