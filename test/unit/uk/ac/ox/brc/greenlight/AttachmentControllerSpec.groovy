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
	}

}
