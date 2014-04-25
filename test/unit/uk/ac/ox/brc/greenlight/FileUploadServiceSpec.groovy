package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.ServletContext

@TestFor(FileUploadService)
class FileUploadServiceSpec extends Specification {

	@Unroll
    void "Handling null parameters"(){

		expect: "Null to be returned"
		service.uploadFile(file as MultipartFile, name, destinationDirectory) == null

		where:
		file						| name 			| destinationDirectory
		new MockMultipartFile("a")	| null			| null
		new MockMultipartFile("a")	| null			| "dir"
		new MockMultipartFile("a")	| "File.jpg"	| null
		null						| "File.jpg"	| null
		null						| "File.jpg"	| "dir"
		null						| null			| "dir"
		null						| null			| null
	}

	@Unroll
	void "Handling empty parameters"(){

		expect: "Null to be returned"
		service.uploadFile(file as MultipartFile, name, destinationDirectory) == null

		where:
		file						| name 			| destinationDirectory
		new MockMultipartFile("a")	| ""			| ""
		new MockMultipartFile("a")	| ""			| "dir"
		new MockMultipartFile("a")	| "File.jpg"	| ""
		""							| "File.jpg"	| ""
		""							| "File.jpg"	| "dir"
		""							| ""			| "dir"
		""							| ""			| ""
	}

	void "Uploading valid files"(){

		given:
		service.servletContext = Mock(ServletContext)

		when:
		String filePath = service.uploadFile(file, name, destinationDirectory)

		then:
		filePath == expectedPath
		1 * service.servletContext.getRealPath(destinationDirectory) >> "/tmp/${destinationDirectory}"

		where:
		file										| name			| destinationDirectory 	| expectedPath
		new MockMultipartFile('a', new byte[12])	| 'b'			| 'c'					| '/tmp/c/b'
	}
}
