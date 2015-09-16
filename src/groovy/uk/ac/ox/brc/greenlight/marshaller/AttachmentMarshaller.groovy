package uk.ac.ox.brc.greenlight.marshaller
import grails.converters.JSON
import uk.ac.ox.brc.greenlight.Attachment

/**
 * Created by soheil on 09/04/2014.
 */
class AttachmentMarshaller {
	void register() {
		JSON.registerObjectMarshaller(Attachment) { attachment ->

			return [
					id : attachment.id,
					dateOfUpload : attachment.dateOfUpload.format("yyyy-MM-dd HH:mm:ss"),
					fileName : attachment.fileName,
					consentForm: attachment.consentForm
			]
		}
	}
}