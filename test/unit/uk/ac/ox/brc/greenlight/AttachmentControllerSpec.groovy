package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by soheil on 17/03/2015.
 */
@TestFor(AttachmentController)
class AttachmentControllerSpec extends Specification {

	void setup(){

	}


	def "save will create one attachment for uploaded JPG file"(){

		given:"JPG file is uploaded"
		//As we need to also mock the service which is used inside the controller, so we need to add the following line
		controller.attachmentService = Mock(AttachmentService)

		Path path = Paths.get("test/resources/singlePageJPG.jpg")
		byte[] jpgFile = Files.readAllBytes(path)

		def multipartFile = new GrailsMockMultipartFile('scannedForms', 'singlePageJPG.jpg', 'image/jpg', jpgFile)
		controller.request.addFile(multipartFile)

		when:
		controller.save()

		then:"one attachment should be created"
		1 * controller.attachmentService.create(_)  >> {new Attachment();}
		controller.modelAndView.model.attachments
		controller.modelAndView.model.attachments.each {
			it.uploadStatus == "Success"
		}
	}


	def "save will create separate attachments for each page in uploaded multi-page PDF"(){

		given:"two-pages PDF is uploaded"
		//As we need to also mock the service which is used inside the controller, so we need to add the following line
		controller.attachmentService = Mock(AttachmentService)

		Path path = Paths.get("test/resources/multiPagePDF.pdf")
		byte[] pdfContent = Files.readAllBytes(path)

		def multipartFile = new GrailsMockMultipartFile('scannedForms', 'multiPagePDF.pdf', 'application/pdf', pdfContent)
		controller.request.addFile(multipartFile)

		when:
		controller.save()

		then:"two attachments should be created"
		2 * controller.attachmentService.create(_)  >> {new Attachment();}
		controller.modelAndView.model.attachments
		controller.modelAndView.model.attachments.each {
			it.uploadStatus == "Success"
		}
	}


	def "save will create one attachment when singleConsentPerPDFFile is true and uploaded file is a multi-page PDF"(){

		given:"two-pages PDF is uploaded and singleConsentPerPDFFile is true"
		//As we need to also mock the service which is used inside the controller, so we need to add the following line
		controller.attachmentService = Mock(AttachmentService)
		controller.PDFService = Mock(PDFService)

		Path path = Paths.get("test/resources/multiPagePDF.pdf")
		byte[] pdfContent = Files.readAllBytes(path)

		def multipartFile = new GrailsMockMultipartFile('scannedForms', 'multiPagePDF.pdf', 'application/pdf', pdfContent)
		controller.request.addFile(multipartFile)

		//if all pages in the PDF belongs to the same consent
		controller.params.singleConsentPerPDFFile = true

		when:
		controller.save()

		then:"one attachment should be created"
		1 * controller.PDFService.convertPDFToSingleImage(_,_) >> {}
		1 * controller.attachmentService.create(_)  >> {new Attachment();}
		controller.modelAndView.model.attachments
		controller.modelAndView.model.attachments[0].uploadStatus == "Success"
	}

	def "save will create more than one attachments when singleConsentPerPDFFile is false and uploaded file is a multi-page PDF"(){

		given:"two-pages PDF is uploaded and singleConsentPerPDFFile is false"
		//As we need to also mock the service which is used inside the controller, so we need to add the following line
		controller.attachmentService = Mock(AttachmentService)
		controller.PDFService = Mock(PDFService)

		Path path = Paths.get("test/resources/multiPagePDF.pdf")
		byte[] pdfContent = Files.readAllBytes(path)

		def multipartFile = new GrailsMockMultipartFile('scannedForms', 'multiPagePDF.pdf', 'application/pdf', pdfContent)
		controller.request.addFile(multipartFile)

		//if all pages in the PDF belongs to the same consent
		controller.params.singleConsentPerPDFFile = false

		when:
		controller.save()

		then:"two attachments should be created and convertPDFToSingleImage should NOT be called"
		0 * controller.PDFService.convertPDFToSingleImage(_,_) >> {}
		2 * controller.attachmentService.create(_)  >> {new Attachment();}
		controller.modelAndView.model.attachments
		controller.modelAndView.model.attachments.each {
			it.uploadStatus == "Success"
		}
	}

	def "save will handle the error message if it is a multi-page PDF and singleConsentPerPDFFile is TRUE and convertPDFToSingleImage throws an exception"(){

		given:"A large PDF is uploaded and singleConsentPerPDFFile is true"
		//As we need to also mock the service which is used inside the controller, so we need to add the following line
		controller.attachmentService = Mock(AttachmentService)
		controller.PDFService = Mock(PDFService)

		Path path = Paths.get("test/resources/multiPagePDF.pdf")
		byte[] pdfContent = Files.readAllBytes(path)

		def multipartFile = new GrailsMockMultipartFile('scannedForms', 'multiPagePDF.pdf', 'application/pdf', pdfContent)
		controller.request.addFile(multipartFile)

		//if all pages in the PDF belongs to the same consent
		controller.params.singleConsentPerPDFFile = true

		when:
		controller.save()

		then:"No attachment should be created"
		1 * controller.PDFService.convertPDFToSingleImage(_,_) >> { throw new OutOfMemoryError("OutOfMemory in processing the PDF file")}
		0 * controller.attachmentService.create(_)  >> {new Attachment();}
		controller.modelAndView.model.attachments
		controller.modelAndView.model.attachments[0].uploadStatus  == "Failed"
		controller.modelAndView.model.attachments[0].uploadMessage == "OutOfMemory in processing the PDF file"
		controller.modelAndView.model.attachments[0].fileName == "multiPagePDF.pdf"
	}


	def "save will handle the error message if it is a PDF and singleConsentPerPDFFile is FALSE and attachmentService throws an exception"(){

		given:"A multi-page PDF is uploaded and singleConsentPerPDFFile is False"
		//As we need to also mock the service which is used inside the controller, so we need to add the following line
		controller.attachmentService = Mock(AttachmentService)
		controller.PDFService = Mock(PDFService)

		Path path = Paths.get("test/resources/multiPagePDF.pdf")
		byte[] pdfContent = Files.readAllBytes(path)

		def multipartFile = new GrailsMockMultipartFile('scannedForms', 'multiPagePDF.pdf', 'application/pdf', pdfContent)
		controller.request.addFile(multipartFile)

		//if all pages in the PDF belongs to the same consent
		controller.params.singleConsentPerPDFFile = false

		when:
		controller.save()

		then:"No attachment should be created"
		2 * controller.attachmentService.create(_)  >> {throw new Exception("Exception in creating the attachment");}
		controller.modelAndView.model.attachments
		controller.modelAndView.model.attachments.eachWithIndex  { Attachment attachment, int index ->
			attachment.uploadStatus  == "Failed"
			attachment.uploadMessage == "Exception in creating the attachment"
			attachment.fileName == "multiPagePDF.pdf[Page:${index+1}]"
		}

	}

	def "save will handle the error message if it is a non PDF and attachmentService throws an exception\""(){

		given:"JPG file is uploaded"
		//As we need to also mock the service which is used inside the controller, so we need to add the following line
		controller.attachmentService = Mock(AttachmentService)

		Path path = Paths.get("test/resources/singlePageJPG.jpg")
		byte[] jpgFile = Files.readAllBytes(path)

		def multipartFile = new GrailsMockMultipartFile('scannedForms', 'singlePageJPG.jpg', 'image/jpg', jpgFile)
		controller.request.addFile(multipartFile)

		when:
		controller.save()

		then:"no attachment should be created"
		1 * controller.attachmentService.create(_)  >> {throw new Exception("Exception in creating the attachment");}
		controller.modelAndView.model.attachments
		controller.modelAndView.model.attachments[0].uploadStatus  == "Failed"
		controller.modelAndView.model.attachments[0].uploadMessage == "Exception in creating the attachment"
		controller.modelAndView.model.attachments[0].fileName == "singlePageJPG.jpg"
	}

	def "save will handle the error message if it is a corrupted PDF file!"(){

		given:"A multi-page PDF is uploaded and singleConsentPerPDFFile is False"
		//As we need to also mock the service which is used inside the controller, so we need to add the following line
		controller.attachmentService = Mock(AttachmentService)
		controller.PDFService = Mock(PDFService)

		Path path = Paths.get("test/resources/corruptedPDFFile.pdf")
		byte[] pdfContent = Files.readAllBytes(path)

		def multipartFile = new GrailsMockMultipartFile('scannedForms', 'multiPagePDF.pdf', 'application/pdf', pdfContent)
		controller.request.addFile(multipartFile)

		//if all pages in the PDF belongs to the same consent
		controller.params.singleConsentPerPDFFile = false

		when:
		controller.save()

		then:"No attachment should be created"
		0 * controller.attachmentService.create(_)  >> {}
		controller.modelAndView.model.attachments
		controller.modelAndView.model.attachments.eachWithIndex  { Attachment attachment, int index ->
			attachment.uploadStatus  == "Failed"
			attachment.uploadMessage == "Can not read the PDF file!"
			attachment.fileName == "corruptedPDFFile.pdf"
		}

	}

}