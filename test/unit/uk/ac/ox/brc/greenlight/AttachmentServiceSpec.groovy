package uk.ac.ox.brc.greenlight

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by rb on 24/04/2014.
 */
@Mock(Attachment)
@TestFor(AttachmentService)
class AttachmentServiceSpec extends Specification {

	def "Migrate attachments into database"() {

		given:
		File inputFile = "resources" + File.separator + "ORBconsentform-empty.jpg" as File
		Attachment attachment = new Attachment(
				fileName: inputFile.name as String,
				content: inputFile.bytes,
				attachmentType: Attachment.AttachmentType.IMAGE,
				dateOfUpload: new Date()
			).save(failOnError: true)
		File expectedFileLocation =  "target${File.separator}attachments${File.separator}${attachment.id}.jpg" as File
		service.fileUploadService = Mock(FileUploadService)


		when: "The migration is carried out"
		boolean result = service.migrateAttachmentFromDatabase(attachment)
		Attachment savedAttachment = Attachment.get(attachment.id)

		then:
		result == true
		savedAttachment.content == null
		savedAttachment.fileUrl == expectedFileLocation.path
		expectedFileLocation.exists()
		expectedFileLocation.bytes.length == inputFile.bytes.length
		1 * service.fileUploadService.uploadFile(_, attachment.id + ".jpg", 'attachments') >> { _, filename, dir ->
			// We need to mock the file copying bits. Ideally we should refactor the code under test to make this easier.
			File path = expectedFileLocation as File
			path.parentFile.mkdirs()
			path.createNewFile()
			path << inputFile.bytes
		}

		cleanup:

		expectedFileLocation.delete()
		expectedFileLocation.parentFile.delete()
	}

	def "Attempting migration from database when no data is present returns true but does nothing"() {

		given:
		Attachment attachment = new Attachment(
				fileName: 'a.jpg',
				attachmentType: Attachment.AttachmentType.IMAGE,
				dateOfUpload: new Date()
		).save(failOnError: true)

		when: "The migration is carried out"
		boolean result = service.migrateAttachmentFromDatabase(attachment)

		then:
		result == true
		attachment.content == null
	}

	// TODO: Migration if the file exists fails
}
