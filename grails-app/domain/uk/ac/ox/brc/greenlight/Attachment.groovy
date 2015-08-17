package uk.ac.ox.brc.greenlight

import org.codehaus.groovy.grails.plugins.orm.auditable.Stamp

@Stamp
class Attachment {

    byte[] content // deprecated. Use the fileUrl instead
	String fileUrl // The location of the attachment file on disk. Replaces `content`

    Date dateOfUpload
    AttachmentType attachmentType
    String fileName // name of file as it was uploaded

    static belongsTo = [
            consentForm: ConsentForm
    ]


	static auditable =  [ignore:['version','lastUpdated','lastUpdatedBy','createdBy','dateCreated']]

	//the status of the uploaded file
	String uploadStatus;
	//error message in case of failure in uploading the attachment
	String uploadMessage;
	static transients = ['uploadStatus','uploadMessage']

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

