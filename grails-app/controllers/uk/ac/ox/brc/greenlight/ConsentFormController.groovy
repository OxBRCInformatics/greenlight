package uk.ac.ox.brc.greenlight

import grails.converters.JSON
import org.springframework.web.multipart.MultipartFile

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile

@Transactional(readOnly = true)
class ConsentFormController {


    def consentFormService



    def index() {
        redirect action:'list'
    }

    def show(Attachment consentFormInstance) {
        respond consentFormInstance
    }

    def save(Attachment consentFormInstance) {

        def uploadedFile=request.getFile('scannedForm');


        if (uploadedFile.size==0) {
            flash.message = 'File cannot be empty, Please select a file.'
            render(view: 'create')
            return
        }


        if(request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
            CommonsMultipartFile file = (CommonsMultipartFile)multiRequest.getFile("scannedForm");

            consentFormInstance.scannedForm = file.bytes
        }


        if (consentFormInstance == null) {
            notFound()
            return
        }

        if (consentFormInstance.hasErrors()) {
            respond consentFormInstance.errors, view:'create'
            return
        }

        consentFormInstance.save flush:true
        flash.message = message(code: 'default.created.message', args: [
                message(code: 'consentFormInstance.label', default: 'Attachment'),
                consentFormInstance.id
        ])
        redirect (action:"create")
        return



        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [
                        message(code: 'consentFormInstance.label', default: 'ConsentFormIns'),
                        consentFormInstance.id
                ])
                redirect consentFormInstance
            }
            '*' { respond consentFormInstance, [status: CREATED] }
        }


    }


    def delete() {
        def consentFormInstance = Attachment.get(params.id);

        if(consentFormInstance && !consentFormInstance.patientConsent)
        {
            consentFormInstance.delete flush:true
            def result=[id:consentFormInstance.id, status:'success'];
            render result as JSON;
        }
    }


    def viewImage = {
        def consentForm = Attachment.get( params.id )
        byte[] image = consentForm.scannedForm;
        response.outputStream << image
    }


    def list()
    {
        [consentForms: consentFormService.getAllConsentForms()]
    }

    def upload()
    {
        def consentForms = []
        if(request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
            List<MultipartFile> files = multiRequest.getFiles("scannedForm");

            for(file in files)
            {
                def okContentTypes = ['image/png', 'image/jpeg', 'image/jpg', 'image/gif'];
                def confType=file.getContentType();
                if (!okContentTypes.contains(confType))
                    continue;

                def consent=new Attachment();
                consent.scannedForm = file.bytes;
                consent.dateOfScan = new Date();
                consent.save(flush: true);
                consentForms.add(consent);
            }
        }
        render view:'create',model:[consentFormInstances:consentForms]
    }

    def create() {
        def list=params?.consentFormInstances;
        [consentFormInstances:list]
    }

}
