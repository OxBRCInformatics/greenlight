package uk.ac.ox.brc.greenlight



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile

@Transactional(readOnly = true)
class ConsentFormController {

	static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

	def index(Integer max) {
		params.max = Math.min(max ?: 10, 100)
		respond ConsentForm.list(params), model:[consentFormInstanceCount: ConsentForm.count()]
	}

	def show(ConsentForm consentFormInstance) {
		respond consentFormInstance
	}

	def create() {
		respond new ConsentForm(params)
	}

	@Transactional
	def save(ConsentForm consentFormInstance) {
	 
		def uploadedFile=request.getFile('scannedForm');
		
		
				if (uploadedFile.size==0) {
					flash.message = 'File cannot be empty, Please select a file.'
					render(view: 'create')
					return
				}
		
		
				if(request instanceof MultipartHttpServletRequest) {
					MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
					CommonsMultipartFile file = (CommonsMultipartFile)multiRequest.getFile("scannedForm");
					consentFormInstance.scannedForm = file.bytes
				}
		
		
				if (consentFormInstance == null) {
					notFound()
					return
				}
		
				if (consentFormInstance.hasErrors()) {
					respond consentFormInstance.errors, view:'create'
					return
				}
		
				consentFormInstance.save flush:true
				flash.message = message(code: 'default.created.message', args: [
					message(code: 'consentFormInstance.label', default: 'ConsentForm'),
					consentFormInstance.id
				])
				redirect (action:"create")
				return
		
		
		
				request.withFormat {
					form {
						flash.message = message(code: 'default.created.message', args: [
							message(code: 'consentFormInstance.label', default: 'ConsentFormIns'),
							consentFormInstance.id
						])
						redirect consentFormInstance
					}
					'*' { respond consentFormInstance, [status: CREATED] }
				}
				
				
	}
	
	
	def viewImage = {		
			  def consentForm = ConsentForm.get( params.id )
			  byte[] image = consentForm.scannedForm;
			  response.outputStream << image		
			}
	
	

	def edit(ConsentForm consentFormInstance) {
		respond consentFormInstance
	}

	@Transactional
	def update(ConsentForm consentFormInstance) {
		if (consentFormInstance == null) {
			notFound()
			return
		}

		if (consentFormInstance.hasErrors()) {
			respond consentFormInstance.errors, view:'edit'
			return
		}

		consentFormInstance.save flush:true

		request.withFormat {
			form {
				flash.message = message(code: 'default.updated.message', args: [
					message(code: 'ConsentForm.label', default: 'ConsentForm'),
					consentFormInstance.id
				])
				redirect consentFormInstance
			}
			'*'{ respond consentFormInstance, [status: OK] }
		}
	}

	@Transactional
	def delete(ConsentForm consentFormInstance) {

		if (consentFormInstance == null) {
			notFound()
			return
		}

		consentFormInstance.delete flush:true

		request.withFormat {
			form {
				flash.message = message(code: 'default.deleted.message', args: [
					message(code: 'ConsentForm.label', default: 'ConsentForm'),
					consentFormInstance.id
				])
				redirect action:"index", method:"GET"
			}
			'*'{ render status: NO_CONTENT }
		}
	}


	 


protected void notFound() {
	request.withFormat {
		form {
			flash.message = message(code: 'default.not.found.message', args: [
				message(code: 'consentFormInstance.label', default: 'ConsentForm'),
				params.id
			])
			redirect action: "index", method: "GET"
		}
		'*'{ render status: NOT_FOUND }
	}
}
}
