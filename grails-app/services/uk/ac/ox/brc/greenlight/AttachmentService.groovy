package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional

@Transactional
class AttachmentService {

    def getAllAttachments() {
      Attachment.list(sort: 'dateOfUpload', order: 'desc');

        //Fixme try to use projection and
        //do not load Content image files
        //add a Boolean loadContent parameter to this method
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

