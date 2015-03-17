package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(PDFService)
class PDFServiceSpec extends Specification {


	def setup() {
	}

	def cleanup() {
	}

	void "convertPDFToSingleImage will create single Image from all pages in the input PDF"() {

		when:
		Path path = Paths.get("test/resources/multiPagePDF.pdf");
		String name = "multiPagePDF.pdf";
		String originalFileName = "multiPagePDF.pdf";
		String contentType = "application/pdf";
		byte[] content = Files.readAllBytes(path);
		MultipartFile pdf = new MockMultipartFile(name,originalFileName, contentType, content);

		//create single image from PDF
		def finalMultipartFile = service.convertPDFToSingleImage(pdf,"myFileName.jpg");

		//prepare to compare the content of the created image with the expected image
		File createdImageFile =  File.createTempFile("myTemp","jpg")
		FileOutputStream fos = new FileOutputStream(createdImageFile);
		fos.write(finalMultipartFile.getBytes());
		fos.close();
		String createdJPGFile = createdImageFile.text;
		String expectedJPGFile = new File("test/resources/multiPageJPG.jpg").text;


		then:
		createdJPGFile == expectedJPGFile

		cleanup:
		createdImageFile.delete()
	}
}
