package uk.ac.ox.brc.greenlight

import grails.converters.JSON
import org.springframework.web.multipart.MultipartFile

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile

@Transactional(readOnly = true)
class AttachmentController {


    def attachmentService



    def index() {
        redirect action:'list'
    }

    def show(Attachment attachment) {
        [attachment: attachment]
    }

    def save(Attachment attachment) {

        def uploadedFile=request.getFile('scannedForm');


        if (uploadedFile.size==0) {
            flash.message = 'File cannot be empty, Please select a file.'
            render(view: 'create')
            return
        }


        if(request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
            CommonsMultipartFile file = (CommonsMultipartFile)multiRequest.getFile("scannedForm");

            attachment.scannedForm = file.bytes
        }


        if (attachment == null) {
            notFound()
            return
        }

        if (attachment.hasErrors()) {
            respond attachment.errors, view:'create'
            return
        }

        attachment.save flush:true
        flash.message = message(code: 'default.created.message', args: [
                message(code: 'consentFormInstance.label', default: 'Attachment'),
                attachment.id
        ])
        redirect (action:"create")
        return



        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [
                        message(code: 'consentFormInstance.label', default: 'ConsentFormIns'),
                        attachment.id
                ])
                redirect attachment
            }
            '*' { respond attachment, [status: CREATED] }
        }


    }


    def delete() {
        def attachment = Attachment.get(params.id);

        if(attachment && !attachment.consentForm)
        {
            attachment.delete flush:true
            def result=[id:attachment.id, status:'success'];
            render result as JSON;
        }
    }


    def viewImage = {
        def attachment = Attachment.get( params.id )
        byte[] image = attachment.scannedForm;
        response.outputStream << image
    }


    def list()
    {
        [attachments: attachmentService.getAllConsentForms()]
    }

    def upload()
    {
        def attachments = []
        if(request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
            List<MultipartFile> files = multiRequest.getFiles("scannedForm");

            for(file in files)
            {
                def okContentTypes = ['image/png', 'image/jpeg', 'image/jpg', 'image/gif'];
                def confType=file.getContentType();
                if (!okContentTypes.contains(confType))
                    continue;

                def attachment=new Attachment();
                attachment.scannedForm = file.bytes;
                attachment.dateOfScan = new Date();
                attachment.save(flush: true);
                attachments.add(attachment);
            }
        }
        render view:'create',model:[attachments:attachments]
    }

    def create() {
        def list=params?.attachmentInstances;
        [attachmentInstances:list]
    }

}
