package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.image.BufferedImage
import java.nio.file.Files


@Transactional
class PDFService {

	MultipartFile convertPDFToSingleImage(MultipartFile pdfFile,String imageFileName) {

		ArrayList<File> tempPageImages = new ArrayList<File>();

		PDDocument document = PDDocument.load(pdfFile.inputStream);
		document.getDocumentCatalog().getAllPages().eachWithIndex{ PDPage page, pageNumber ->

			// Create a byte array output stream and write the image to it as an RGB image at 256dpi
			ByteArrayOutputStream baos = new ByteArrayOutputStream()
			ImageIO.write(page.convertToImage(BufferedImage.TYPE_INT_RGB, 150), "jpg", baos)

			//create temp fileName
			String singlePageName = pdfFile?.originalFilename + "_page" + pageNumber
			MockMultipartFile singlePage = new MockMultipartFile(singlePageName, baos.toByteArray())

			//create temp file
			File tempFile = File.createTempFile(singlePageName,"jpg")
			File file = new File(tempFile.absolutePath)
			singlePage.transferTo(file)
			tempPageImages.add(file)
			//close the stream
			baos.close();
		}

		if(tempPageImages.size() == 0)
			return null;
		//creating a buffered image array from image files
		BufferedImage[] buffImages = new BufferedImage[tempPageImages.size()];
		int type = 0;
		int finalWidth  = 0;
		int finalHeight = 0;

		for (int i = 0; i < tempPageImages.size(); i++) {
			buffImages[i] = ImageIO.read(tempPageImages[i]);
			//find the max width of all the current pdf pages, it will the
			//width of the final image
			if(buffImages[i].getWidth() > finalWidth) {
				finalWidth = buffImages[i].getWidth();
			}
			//final height is the sum of all the heights
			finalHeight += buffImages[i].getHeight()
		}

		type = buffImages[0].getType();
		finalHeight += 100;
		finalWidth  += 100;
		//Initializing the final image
		BufferedImage finalImg = new BufferedImage(finalWidth , finalHeight, type);
		//make the final image background to WHITE
		def graphics = finalImg.createGraphics()
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, finalImg.getWidth(), finalImg.getHeight());
		//dispose the graphic
		graphics.dispose();

		int positionX = 0;
		int positionY = 0;
		for (int i = 0; i < tempPageImages.size(); i++) {
			//if the width is smaller than the final width, place it in the centre
			if(buffImages[i].getWidth() < finalWidth)
				positionX = (finalWidth - buffImages[i].getWidth()) / 2;

			graphics = finalImg.createGraphics()
			graphics.drawImage(buffImages[i], positionX, positionY, null);
			graphics.dispose();
			//prepare Y position for next image
			positionY += buffImages[i].getHeight();
		}

		//write the final result into a temp JPEG file
		File finalTempFile = File.createTempFile("final",".jpg")
		ImageIO.write(finalImg, "jpeg", finalTempFile);
		String contentType = "image/jpeg";
		byte[] content = Files.readAllBytes(finalTempFile.toPath());
		MultipartFile finalResult = new MockMultipartFile(imageFileName,finalTempFile.name, contentType, content);

		//remove all temp files
		finalTempFile.delete();
		tempPageImages.eachWithIndex{ File entry, int i ->
			entry.delete();
		}
		return  finalResult;
	}
}