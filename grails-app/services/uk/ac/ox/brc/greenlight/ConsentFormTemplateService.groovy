package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional

@Transactional
class ConsentFormTemplateService {

    def getAll() {
        return ConsentFormTemplate.list();
    }
}
