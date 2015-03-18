package uk.ac.ox.brc.greenlight

import grails.converters.JSON
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage
import org.apache.tomcat.jni.Shm
import org.grails.datastore.gorm.finders.MethodExpression
import org.hibernate.criterion.CriteriaSpecification
import org.springframework.mock.web.MockMultipartFile

import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class AttachmentController {

    def attachmentService
	def PDFService

	def defaultAction = 'list'

	/*
		We added these lines to make sure that the libraries are all loaded
		and in case of failure it will happen on build process before deployment
		org.springframework.mock.web.MockMultipartFile
		org.springframework.web.multipart.MultipartFile
		org.springframework.web.multipart.MultipartHttpServletRequest
		These are all based on spring-test.jar file which is in lib and wrapper folder
		In case of Grils upgrades we need to update these as well manullay
	 */
	static{
		try {
			Class.forName ("org.springframework.mock.web.MockMultipartFile");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	def listUnAnnotatedAttachments(){
		def data
		def total
		def displayTotal
		def order
		def sortCol

		order = params?.sSortDir_0
		def sortColIndex = params?.iSortCol_0
		def cols =["0": "dateOfUpload", "1":"fileName"]
		sortCol = cols.containsKey(sortColIndex) ? cols[sortColIndex] : "dateOfUpload"


		def query = "select a from Attachment as a where a not in (select c.attachedFormImage from ConsentForm as c) order by " + sortCol + " " + order
 		data = Attachment.executeQuery(query,[max: params.iDisplayLength, offset: params.iDisplayStart]);
		def totalRecords = Attachment.executeQuery(query);


		total = data.size()
		displayTotal = totalRecords.size()

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
		sortCol = cols.containsKey(sortColIndex) ? cols[sortColIndex] : "consentDate"


		data = ConsentForm.list([max: params.iDisplayLength, offset: params.iDisplayStart, sort: sortCol, order: order])
		total = data.size()
		displayTotal = ConsentForm.count()

		def model = [sEcho: params.sEcho, iTotalRecords: total, iTotalDisplayRecords: displayTotal, aaData: data]
		render model as JSON
	}

	/**
	 * Trigger a migration of all attachments in the database.
	 *
	 * The operation is idempotent, so running many times or over items
	 * already migrated is fine.
	 */
	def migrateAll() {
		def results =  attachmentService.migrateAllAttachments()
		respond results as Object, [formats:['xml', 'json']] as Map
	}

    def list() {
        def result = attachmentService.getAllAttachments()
        respond result  , model:[attachments:result ]
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

    def viewPDF = {
        byte[] content= attachmentService.getContent(params?.id);
        def data = "data:application/pdf;base64,${content.encodeBase64().toString()}"
        def result=[content:data]
        render result as JSON
    }

    def create() {

    }

    def showUploaded() {
    }

    def save() {

		def singleConsentPerPDFFile = params.boolean('singleConsentPerPDFFile');

        def attachments = []
        if(request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
            List<MultipartFile> files = multiRequest.getFiles("scannedForms");

			files.each{  file ->
                def okContentTypes = ['image/png', 'image/jpeg', 'image/pjpeg', 'image/jpg', 'image/gif','application/pdf'];
                def confType=file.getContentType();
                if (okContentTypes.contains(confType) && file.size > 0){

					// If it's a PDF split the pages out and convert to an image first
					if(confType=='application/pdf'){

						//create single image and add all pdf pages into it if singleConsentPerPDFFile is TRUE
						if(singleConsentPerPDFFile == true){

							try {
								MockMultipartFile singlePage = PDFService.convertPDFToSingleImage(file, file?.originalFilename)
								Attachment attachment = attachmentService.create(singlePage)
								attachment.uploadStatus = "Success"
								attachments.add(attachment)
							}catch(Throwable ex){
								//just get track of this faulty attachment
								Attachment faultyAttachment = new Attachment(
									fileName: file.originalFilename,
									attachmentType: Attachment.AttachmentType.IMAGE,
									dateOfUpload: new Date ()
								);
								faultyAttachment.uploadStatus = "Failed"
								faultyAttachment.uploadMessage = ex.message
								attachments.add(faultyAttachment)
							}

						}else{

							PDDocument document
							try {
								document = PDDocument.load(file.inputStream)
							}catch(Throwable ex){
								//just get track of this faulty attachment
								Attachment faultyAttachment = new Attachment(
										fileName: file.originalFilename ,
										attachmentType: Attachment.AttachmentType.IMAGE,
										dateOfUpload: new Date ()
								);
								faultyAttachment.uploadStatus = "Failed"
								faultyAttachment.uploadMessage = "Can not read the PDF file!"
								attachments.add(faultyAttachment)
							}

							//check if document is not NULL
							document?.getDocumentCatalog()?.getAllPages().eachWithIndex { PDPage page, pageNumber ->
								try{
									// Create a byte array output stream and write the image to it as an RGB image at 256dpi
									ByteArrayOutputStream baos = new ByteArrayOutputStream()
									ImageIO.write(page.convertToImage(BufferedImage.TYPE_INT_RGB, 256), "jpg", baos)

									String singlePageName = file?.originalFilename + "_page" + pageNumber
									MockMultipartFile singlePage = new MockMultipartFile(singlePageName, baos.toByteArray())
									Attachment attachment = attachmentService.create(singlePage)
									attachment.uploadStatus = "Success"
									attachments.add(attachment)
								}catch(Throwable ex){
									//just get track of this faulty attachment
									Attachment faultyAttachment = new Attachment(
											fileName: file.originalFilename + "[Page:${pageNumber+1}]",
											attachmentType: Attachment.AttachmentType.IMAGE,
											dateOfUpload: new Date ()
									);
									faultyAttachment.uploadStatus = "Failed"
									faultyAttachment.uploadMessage = ex.message
									attachments.add(faultyAttachment)
								}
							}
						}
					}
					else{
						try{
							def attachment = attachmentService.create(file)
							attachment.uploadStatus = "Success"
							attachments.add(attachment)
						}catch(Throwable ex){
							//just get track of this faulty attachment
							Attachment faultyAttachment = new Attachment(
									fileName: file.originalFilename,
									attachmentType: Attachment.AttachmentType.IMAGE,
									dateOfUpload: new Date ()
							);
							faultyAttachment.uploadStatus = "Failed"
							faultyAttachment.uploadMessage = ex.message
							attachments.add(faultyAttachment)
						}
					}
				}
            }
        }else{
			log.error("Attachment save request isn't multipart, ignoring")
		}
        render view: 'showUploaded',model:[attachments:attachments]
    }

}
