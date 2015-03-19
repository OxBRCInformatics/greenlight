package uk.ac.ox.brc.greenlight

import ar.com.hjg.pngj.ImageInfo
import ar.com.hjg.pngj.ImageLineInt
import ar.com.hjg.pngj.PngReader
import ar.com.hjg.pngj.PngWriter
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour
import ar.com.hjg.pngj.chunks.ChunkLoadBehaviour
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



	/*
		Using PNGJ library to concat PNG files into a single file
		https://code.google.com/p/pngj/wiki/Snippets
	 */
	public  void doTiling(String[] tiles, String dest, int nTilesX) {
		int ntiles = tiles.length;
		int nTilesY = (ntiles + nTilesX - 1) / nTilesX; // integer ceil
		ImageInfo imi1, imi2; // 1:small tile   2:big image
		PngReader pngr = new PngReader(new File(tiles[0]));
		imi1 = pngr.imgInfo;
		PngReader[] readers = new PngReader[nTilesX];
		imi2 = new ImageInfo(imi1.cols * nTilesX, imi1.rows * nTilesY, imi1.bitDepth, imi1.alpha, imi1.greyscale,
				imi1.indexed);
		PngWriter pngw = new PngWriter(new File(dest), imi2, true);
		// copy palette and transparency if necessary (more chunks?)
		pngw.copyChunksFrom(pngr.getChunksList(), ChunkCopyBehaviour.COPY_PALETTE
				| ChunkCopyBehaviour.COPY_TRANSPARENCY);
		pngr.readSkippingAllRows(); // reads only metadata
		pngr.end(); // close, we'll reopen it again soon
		ImageLineInt line2 = new ImageLineInt(imi2);
		int row2 = 0;
		for (int ty = 0; ty < nTilesY; ty++) {
			int nTilesXcur = ty < nTilesY - 1 ? nTilesX : ntiles - (nTilesY - 1) * nTilesX;
			Arrays.fill(line2.getScanline(), 0);
			for (int tx = 0; tx < nTilesXcur; tx++) { // open serveral readers
				readers[tx] = new PngReader(new File(tiles[tx + ty * nTilesX]));
				readers[tx].setChunkLoadBehaviour(ChunkLoadBehaviour.LOAD_CHUNK_NEVER);
				if (!readers[tx].imgInfo.equals(imi1))
					throw new RuntimeException("different tile ? " + readers[tx].imgInfo);
			}
			for (int row1 = 0; row1 < imi1.rows; row1++) {

				for (int tx = 0; tx < nTilesXcur; tx++) {
					ImageLineInt line1 = (ImageLineInt) readers[tx].readRow(row1); // read line
					System.arraycopy(line1.getScanline(), 0, line2.getScanline(), line1.getScanline().length * tx,
							line1.getScanline().length);
				}
				pngw.writeRow(line2, row2); // write to full image
				row2++;
			}
			for (int tx = 0; tx < nTilesXcur; tx++)
				readers[tx].end(); // close readers
		}
		pngw.end(); // close writer
	}


	/*
		Using this method is more efficient as it creates the final image file on the disk not in memory but it is an PNG file
	 */
	MultipartFile convertPDFToSinglePNGImage(MultipartFile pdfFile,String imageFileName) {

		ArrayList<File> tempPageImages = new ArrayList<File>();
		PDDocument document = PDDocument.load(pdfFile.inputStream);
		document.getDocumentCatalog().getAllPages().eachWithIndex{ PDPage page, pageNumber ->

			// Create a byte array output stream and write the image to it as an RGB PNG image at 100dpi
			ByteArrayOutputStream baos = new ByteArrayOutputStream()
			ImageIO.write(page.convertToImage(BufferedImage.TYPE_INT_RGB, 100), "png", baos)

			//create temp fileName
			String singlePageName = pdfFile?.originalFilename + "_page" + pageNumber
			File file = File.createTempFile(singlePageName,".png")
			FileOutputStream fos = new FileOutputStream (file)
			baos.writeTo(fos)
			tempPageImages.add(file)
			//close the stream
			fos.close();
			baos.close();
		}

		String[] files = new String[tempPageImages.size()];
		tempPageImages.eachWithIndex{ File entry, int i ->
			files.putAt(i,entry.absolutePath);
		}

		if(tempPageImages.size() == 0)
			return null;

		//write the final result into a temp PNG file
		File finalTempFile = File.createTempFile("final",".png")
		doTiling(files, finalTempFile.absolutePath, 1);

		String contentType = "image/png";
		byte[] content = Files.readAllBytes(finalTempFile.toPath());
		MultipartFile finalResult = new MockMultipartFile(imageFileName,finalTempFile.name, contentType, content);

		//remove all temp files
		finalTempFile.delete();
		tempPageImages.eachWithIndex{ File entry, int i ->
			entry.delete();
		}
		return  finalResult;
	}

	/*
		It is not an efficient method as it uses memory to create the final image and it might cause OutOfMemory
		but as we have considered DPI as 100, it doen't cause the OutOfMemory, but it is better to use convertPDFToSinglePNGImage
	 */
	MultipartFile convertPDFToSingleJPGImage(MultipartFile pdfFile,String imageFileName) {

		ArrayList<File> tempPageImages = new ArrayList<File>();

		PDDocument document = PDDocument.load(pdfFile.inputStream);
		document.getDocumentCatalog().getAllPages().eachWithIndex{ PDPage page, pageNumber ->

			// Create a byte array output stream and write the image to it as an RGB image at 100dpi
			ByteArrayOutputStream baos = new ByteArrayOutputStream()
			ImageIO.write(page.convertToImage(BufferedImage.TYPE_INT_RGB, 100), "jpg", baos)

			//create temp fileName
			String singlePageName = pdfFile?.originalFilename + "_page" + pageNumber
			File file = File.createTempFile(singlePageName,".jpg")
			FileOutputStream fos = new FileOutputStream (file)
			baos.writeTo(fos)
			tempPageImages.add(file)
			//close the stream
			fos.close();
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
		//Initializing the final image
		BufferedImage finalImg = new BufferedImage(finalWidth , finalHeight, BufferedImage.TYPE_INT_RGB);
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