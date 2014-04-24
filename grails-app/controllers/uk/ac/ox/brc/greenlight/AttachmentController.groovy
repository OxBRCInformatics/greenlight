package uk.ac.ox.brc.greenlight

import grails.converters.JSON
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage
import org.apache.tomcat.jni.Shm
import org.grails.datastore.gorm.finders.MethodExpression
import org.hibernate.criterion.CriteriaSpecification
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class AttachmentController {


    def attachmentService

	def listUnAnnotatedAttachments(){
		def data
		def total
		def displayTotal
		def order
		def sortCol

		order = params?.sSortDir_0
		sortCol = params?.iSortCol_0
		if(sortCol=="0")
			sortCol = "dateOfUpload"
		else if(sortCol=="1")
			sortCol = "fileName"
		else
			sortCol = "dateOfUpload"




	   data = Attachment.findAllByConsentFormIsNull([max: params.iDisplayLength, offset: params.iDisplayStart, sort: sortCol, order: order]);
		//data = Attachment.list([max: params.iDisplayLength, offset: params.iDisplayStart, sort: sortCol, order: order]);
		//data = Attachment.findAllWhere(consentForm: null)
		//def alll = Attachment.list()
//		data = Attachment.findAllByConsentFormIsNull([max: params.iDisplayLength, offset: params.iDisplayStart, sort: sortCol, order: order]);
//		data = Attachment.findAll([max: params.iDisplayLength, offset: params.iDisplayStart, sort: sortCol, order: order])
//				{
//					isNull("consentForm")
//				}

//		data = Attachment.withCriteria([max: params.iDisplayLength, offset: params.iDisplayStart, sort: sortCol, order: order])
//				{
//					isNull("consentForm")
//				}

//		data = Attachment.findAll([max: params.iDisplayLength, offset: params.iDisplayStart, sort: sortCol, order: order],
//				{
//					consentForm == null
//				})




		total = Attachment.count()
		displayTotal = data.size()

		def model = [sEcho: params.sEcho, iTotalRecords: total, iTotalDisplayRecords: displayTotal, aaData: data]
		render model as JSON
	}


	def lisAnnotatedAttachments(){
		def data
		def total
		def displayTotal
		def order
		def sortCol

		order = params?.sSortDir_0
		def sortColIndex = params?.iSortCol_0
		def cols = ["0":"consentDate",
					"1":"formStatus",
				    "2":"template.namePrefix",
					"3":"formID",
					"4":"patient.nhsNumber" ]
		sortCol = cols.containsValue(sortColIndex) ? cols[sortColIndex] : "consentDate"


		data = ConsentForm.list([max: params.iDisplayLength, offset: params.iDisplayStart, sort: sortCol, order: order])
		total = ConsentForm.count()
		displayTotal = data.size()

		def model = [sEcho: params.sEcho, iTotalRecords: total, iTotalDisplayRecords: displayTotal, aaData: data]
		render model as JSON
	}


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
            render "not found"
            return
        }

        if(attachment && !(attachment.consentForm))
        {
            attachment.delete flush:true
            def result=[id:attachment.id, status:'success'];
            render result as JSON
        }
        else
        {
            render "Can not delete it as it's annotated"
            return
        }
    }


    def viewContent = {
        byte[] content= attachmentService.getContent(params?.id);
        response.outputStream << content
    }

    def viewPDF ={
        byte[] content= attachmentService.getContent(params?.id);
        def data = "data:application/pdf;base64,${content.encodeBase64().toString()}"
        def result=[content:data]
        render result as JSON
    }

    def create() {

    }

    def showUploaded() {
    }

    def save()
    {
        def attachments = []
        if(request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
            List<MultipartFile> files = multiRequest.getFiles("scannedForms");

			files.each{ MultipartFile file ->
                def okContentTypes = ['image/png', 'image/jpeg', 'image/pjpeg', 'image/jpg', 'image/gif','application/pdf'];
                def confType=file.getContentType();
                if (okContentTypes.contains(confType) && file.size > 0){

					// If it's a PDF split the pages out and convert to an image first
					if(confType=='application/pdf'){
						PDDocument document = PDDocument.load(file.inputStream)
						document.getDocumentCatalog().getAllPages().eachWithIndex{ PDPage page, pageNumber ->

							// Create a byte array output stream and write the image to it as an RGB image at 256dpi
							ByteArrayOutputStream baos = new ByteArrayOutputStream()
							ImageIO.write(page.convertToImage(BufferedImage.TYPE_INT_RGB, 256), "jpg", baos)

							def attachment= new Attachment()
							attachment.fileName = file?.originalFilename + "_page" + pageNumber
							attachment.content = baos.toByteArray()
							attachment.attachmentType=Attachment.AttachmentType.IMAGE
							attachment.dateOfUpload=new Date()
							attachmentService.save(attachment)
							attachments.add(attachment)
						}
					}
					else{
						def attachment= new Attachment();
						attachment.fileName = file?.originalFilename
						attachment.content = file?.bytes
						attachment.attachmentType=Attachment.AttachmentType.IMAGE
						attachment.dateOfUpload=new Date()
						attachmentService.save(attachment)
						attachments.add(attachment)
					}
				}
            }
        }else{
			log.error("Attachment save request isn't multipart, ignoring")
		}
        render view: 'showUploaded',model:[attachments:attachments]
    }

}
