package uk.ac.ox.brc.greenlight

import grails.converters.JSON
import org.apache.tomcat.jni.Shm
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

class AttachmentController {


    def attachmentService




    def list()
    {
        def result = attachmentService.getAllAttachments()
        respond result  , model:[attachments:result ]
    }



    def index() {
        redirect action:'list'
    }

    def show(Attachment attachment) {
       respond attachment,model: [attachment: attachment]
    }

    def delete() {
        def attachment = Attachment.get(params?.id);
        if(!attachment)
        {
            respond null
        }

        if(!attachment.consentForm)
        {
            attachment.delete flush:true
            def result=[id:attachment.id, status:'success'];
            render result as JSON
        }
        else
        {
            render 'Can not delete it as it\'s annotated'
        }
    }


    def viewContent = {
        byte[] content= attachmentService.getContent(params?.id);
        response.outputStream << content
    }

    def create() {
        //def list=params?.attachments;
        //[attachments:list]
    }

    def showUploaded() {

    }

    def save()
    {
        def attachments = []
        if(request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
            List<MultipartFile> files = multiRequest.getFiles("scannedForms");

            for(file in files)
            {
                def okContentTypes = ['image/png', 'image/jpeg', 'image/jpg', 'image/gif'];
                def confType=file.getContentType();
                if (!okContentTypes.contains(confType))
                    continue;

                def attachment= new Attachment();
                attachment.fileName = file?.originalFilename;
                attachment.content = file?.bytes;
                attachment.attachmentType=Attachment.AttachmentType.IMAGE;
                attachment.dateOfUpload=new Date();
                attachmentService.save(attachment)
                attachments.add(attachment);
            }
        }
        render view: 'showUploaded',model:[attachments:attachments]
    }

}
