package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.springframework.web.multipart.MultipartFile

/**
 * A service to manage MultipartFile uploads
 */
@Transactional
class FileUploadService {



	// We want the whole upload to succeed or fail as a single transaction
	boolean transactional = true

	/**
	 * Upload a file and store it on the server.
	 *
	 * Adapted from http://coderberry.me/blog/2010/10/12/uploading-files-in-grails/
	 *
	 * @param file The file contents
	 * @param name The name to give the file on disk
	 * @param destinationDirectory The (relative) directory to save to
	 * @return The absolute path the to the file on disk. *THIS IS NOT THE ABSOLUTE URL FOR CLIENTS*
	 */
	def String uploadFile(MultipartFile file, String name, String destinationDirectory) {

		// Do some sanity checking of the input
		if (file == null || file.isEmpty()) {
			log.error "File ${file} was null or empty!"
			return null
		}
		else if (name == null || name.isEmpty()) {
			log.error "The name of the file must not be null or the empty string"
			return null
		}
		else if (destinationDirectory == null || destinationDirectory.isEmpty()) {
			log.error "The name of the destinationDirectory must not be null or the empty string"
			return null
		}

		//The parameter for getRealPath() is a 'virtual path' which - unfortunately -
		// is a concept used in the Java docs but not actually defined anywhere.
		// It is assumed to be a path to a resource in your web application and
		// the separator in that case is always '/' regardless of platform.
		//http://stackoverflow.com/questions/25555541/getservletcontext-getrealpath-in-tomcat-8-returns-wrong-path
		//So the 'destinationDirectory' should start with / , otherwise getRealPath will return null
		if (!destinationDirectory.startsWith("/")) {
			destinationDirectory = "/" + destinationDirectory
		}
		def servletContext = ServletContextHolder.servletContext
		def storagePath = servletContext.getRealPath(destinationDirectory)

		// Create storage path directory if it does not exist
		if (storagePath) {
			def storagePathDirectory = new File(storagePath)
			if (!storagePathDirectory.exists()) {
				log.info "CREATING DIRECTORY ${storagePath}: "
				if (!storagePathDirectory.mkdirs()) {
					log.error "FAILED to create directories for uploads: " + storagePath
					return null
				}
			}

			// Store file
			file.transferTo(new File(storagePath + File.separator + name))
			return storagePath + File.separator + name
		}
		else{
			log.error "FAILED to create directories for uploads"
			return null
		}

	}
}

