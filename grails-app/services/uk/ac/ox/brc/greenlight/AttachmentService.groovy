package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional
import org.springframework.mock.web.MockMultipartFile
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

	/**
	 * Migrate all attachments.
	 * @return a map of the results (key: attachment.id, value: successBoolean)
	 */
	def migrateAllAttachments(){
		def attachments = AttachmentService.findAll()
		def results = [:]
		attachments.each { attachment ->
			results[attachment] = migrateAttachmentFromDatabase(attachment)
		}
		return results
	}

	/**
	 * Move the contents of the Attachment from the database into the filesystem. If there is no content in the `content`
	 * attribute then no action is taken and a successful response is given.
	 *
	 * This is an idempotent operation.
	 *
	 * @param attachment The attachment to migrate the data within
	 * @return true if the contents of the attachment are now in the filesystem, regardless of action taken
	 */
	boolean migrateAttachmentFromDatabase(Attachment attachment){
		boolean success = true
		// If the attachment is present and not empty
		if (!(attachment.content == null || attachment.content.length == 0)) {

			// Does the target file exist already?
			File targetFile = "".asType(File)
			if (targetFile.exists()){
				log.error( "The target file ${targetFile} already exists. No action taken.")
				success = false
			}
			else {
				// Create the file from the byte array in the same way as we do for real uploads
				MockMultipartFile uploadFile = new MockMultipartFile(attachment.id as String, attachment.content)
				String filePath = fileUploadService.uploadFile(uploadFile, attachment.id + ".jpg", 'attachments')
				attachment.fileUrl = filePath

				// Verify that the new file's contents are identical
				File externalFile = attachment.fileUrl as File
				if (!externalFile.exists() || externalFile.bytes != attachment.content) {
					log.error("Created external file contents do not match original byte array. Aborting.")
					success = false
				}else{
					log.info("Content migrated successfully for attachment ${attachment.id}")
					// Remove the old data and clean up
					attachment.content = null
					attachment.save()
				}
			}
		}
		return success
	}
}

