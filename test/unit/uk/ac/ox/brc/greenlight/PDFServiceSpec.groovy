package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import org.apache.commons.io.FileUtils
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
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

//	void "convertPDFToSingleJPGImage will create single Image from all pages in the input PDF"() {
//
//		when:
//		//create multipart file from the test fixture
//		Path path = Paths.get("test/resources/multiPagePDF.pdf");
//		String name = "multiPagePDF.pdf";
//		String originalFileName = "multiPagePDF.pdf";
//		String contentType = "application/pdf";
//		byte[] content = Files.readAllBytes(path);
//		MultipartFile pdf = new MockMultipartFile(name,originalFileName, contentType, content);
//
//		//create single image from PDF
//		def finalMultipartFile = service.convertPDFToSingleJPGImage(pdf,"myFileName.jpg");
//		//prepare to compare the content of the created image with the expected image
//		File createdImageFile =  File.createTempFile("myTemp","jpg")
//		FileOutputStream fos = new FileOutputStream(createdImageFile);
//		fos.write(finalMultipartFile.getBytes());
//		fos.close();
//		byte[] dataCreated  = Files.readAllBytes(createdImageFile.toPath());
//		byte[] dataExpected = Files.readAllBytes(new File("test/resources/multiPageJPG.jpg").toPath());
//
//		then:
//		dataCreated.size() == dataExpected.size()
//		dataCreated.eachWithIndex { byte entry, int i ->
//			assert dataCreated[i] == dataExpected[i]
//		}
//
//		cleanup:
//		createdImageFile.delete()
//	}

	void "convertPDFToSinglePNGImage will create single Image from all pages in the input PDF"() {

		when:
		//create multipart file from the test fixture
		Path path = Paths.get("test/resources/multiPagePDF.pdf");
		String name = "multiPagePDF.pdf";
		String originalFileName = "multiPagePDF.pdf";
		String contentType = "application/pdf";
		byte[] content = Files.readAllBytes(path);
		MultipartFile pdf = new MockMultipartFile(name,originalFileName, contentType, content);

		//create single image from PDF
		def finalMultipartFile = service.convertPDFToSinglePNGImage(pdf,"myFileName.png");
		//prepare to compare the content of the created image with the expected image
		File createdImageFile =  File.createTempFile("myTemp","png")
		FileOutputStream fos = new FileOutputStream(createdImageFile);
		fos.write(finalMultipartFile.getBytes());
		fos.close();
		//byte[] dataCreated  = Files.readAllBytes(createdImageFile.toPath());
		//byte[] dataExpected = Files.readAllBytes(new File("test/resources/multiPagePNG.png").toPath());
		//create TEXT from result image and test-fixture image for comparing them :)
		//String createdPNGFile  = FileUtils.readFileToString(createdImageFile, "utf-8");
		//String expectedPNGFile = FileUtils.readFileToString(new File("test/resources/multiPagePNG.png"), "utf-8");
		BufferedImage createdImage  = ImageIO.read(createdImageFile);
		BufferedImage expectedImage = ImageIO.read(new File("test/resources/multiPagePNG.png"));


		then:
		//compare files pixel-by-pixel as other approaches like comparing by text value or byte failed on Travis! just because of Memory heap error!
		createdImage.height == expectedImage.height
		createdImage.width  == expectedImage.width
		//compare pixel by pixel
		for(int x=0;x<expectedImage.width;x++){
			for(int y=0;y<expectedImage.height;y++) {
				assert expectedImage.getRGB(x,y) == createdImage.getRGB(x,y)
			}
		}
		//createdPNGFile == expectedPNGFile
		//createdImageFile.size() == new File("test/resources/multiPagePNG.png").size()
		//dataCreated.eachWithIndex { byte entry, int i ->
		//	assert dataCreated[i] == dataExpected[i]
		//}
		cleanup:
		createdImageFile.delete()
	}
}
