package uk.ac.ox.brc.greenlight

import grails.converters.JSON
import grails.rest.RestfulController
import org.hibernate.FetchMode

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class ConsentFormTemplateController extends RestfulController {

    def consentFormTemplateService

    def list()
    {
        def consentFormTemplates = ConsentFormTemplate.list()
        if(request.xhr)
            render consentFormTemplates as JSON
        else
            respond  consentFormTemplates, model:[consentFormTemplates:consentFormTemplates]
    }


//    def getTemplate()
//    {
//        def template = ConsentFormTemplate.get(params?.id);
//       if(request.xhr)
//            render template as JSON
//       else
//            respond template, model:[template:template]
//    }

    def getQuestions()
    {
        def template = ConsentFormTemplate.get(params?.templateId);
        def questions = template?.questions;

        render(template: "getQuestions", model: [questions: questions])

        //respond questions,  model: ['questions':questions]
        //,'consentFormTemplate':template ]
        // if(request.xhr)
        // render questions as JSON
        // else
        // respond questions, model:[questions:questions]
    }

    def show(ConsentFormTemplate consentFormTemplate)
    {
        if(request.xhr)
            render consentFormTemplate as JSON
        else
            respond  consentFormTemplate, model:[consentFormTemplate:consentFormTemplate]
    }
}
