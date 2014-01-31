package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional

@Transactional
class AttachmentService {


    def getAllConsentForms()
    {
          Attachment.list();
    }

}

