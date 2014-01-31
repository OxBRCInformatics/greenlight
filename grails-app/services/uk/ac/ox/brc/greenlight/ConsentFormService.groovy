package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional

@Transactional
class ConsentFormService {


    def getAllConsentForms()
    {
          Attachment.list();
    }

}

