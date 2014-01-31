package uk.ac.ox.brc.greenlight



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class ConsentFormTemplateController {

    def consentFormTemplateService

    def list()
    {
        def model = [consentFormTemplates:consentFormTemplateService.getAll()]
        respond  model, [model:model]
    }
}
