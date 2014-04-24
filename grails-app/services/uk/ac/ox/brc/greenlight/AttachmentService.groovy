package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional
import org.springframework.web.multipart.MultipartFile

@Transactional
class  AttachmentService {

	def fileUploadService

    def getAllAttachments() {
		Attachment.list(sort: 'dateOfUpload', order: 'desc');
    }

	/**
	 * Create a new attachment from a filename and byte array
	 * @param uploadedFile The uploaded file
	 * @return the created (and saved if possible) attachment object.
	 */
	Attachment create(MultipartFile uploadedFile){


		Attachment attachment = new Attachment(
				fileName: uploadedFile.name,
				attachmentType: Attachment.AttachmentType.IMAGE,
				dateOfUpload: new Date()
		)
		attachment.save()

		if (attachment.validate()) {
			String filePath = fileUploadService.uploadFile(uploadedFile, attachment.id + ".jpg", 'attachments')
			attachment.fileUrl = filePath
			attachment.save()
		}
		else {
			log.error("Saving attachment failed: " + attachment.errors)
		}

	}

    def save(Attachment attachment) {
        attachment.save(flush: true);
    }

    byte[] getContent(def id) {
        def attachment = Attachment.get(id)
        byte[] content = null
        if (attachment)
            content = attachment?.content;
        return content
    }
}

