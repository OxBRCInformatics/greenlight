package uk.ac.ox.brc.greenlight



import static org.springframework.http.HttpStatus.*

import java.util.Date;

import uk.ac.ox.brc.greenlight.Patient.Gender;
import grails.transaction.Transactional

@Transactional(readOnly = true)
class PatientConsentController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond PatientConsent.list(params), model:[patientConsentInstanceCount: PatientConsent.count()]
    }

    def show(PatientConsent patientConsentInstance) {
        respond patientConsentInstance
    }

    def create() {
		respond new PatientConsent(params)
	}
	
	
	
	def create2() {
		respond (model:[patientConsentInstance:new PatientConsent(params),
			            consentFormInstance:new ConsentForm(params),
						patientInstance :new Patient(params)
						])
	}
	
	
	
	
	
	@Transactional
	def save(PatientConsent patientConsentInstance) {
		if (patientConsentInstance == null) {
			notFound()
			return
		}

		if (patientConsentInstance.hasErrors()) {
			respond patientConsentInstance.errors, view:'create'
			return
		}

		patientConsentInstance.save flush:true

		request.withFormat {
			form {
				flash.message = message(code: 'default.created.message', args: [message(code: 'patientConsentInstance.label', default: 'PatientConsent'), patientConsentInstance.id])
				redirect patientConsentInstance
			}
			'*' { respond patientConsentInstance, [status: CREATED] }
		}
	}

	
	
	

    @Transactional
    def save2(InputFormCommand inputObj) {
			
		def patient=new Patient();
		patient.givenName  = inputObj.patientInstance.givenName;
		patient.familyName = inputObj.patientInstance.familyName
	    patient.gender= inputObj.patientInstance.gender
		patient.dateOfBirth= inputObj.patientInstance.dateOfBirth	
		patient.nhsNumber= inputObj.patientInstance.nhsNumber
		patient.hospitalNumber= inputObj.patientInstance.hospitalNumber
		
		 
		
		
		patient.save flush:true
		 request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'patientInstance.label', default: 'Patient'), inputObj.patientInstance.id])
                redirect inputObj.patientInstance
            }
            '*' { respond inputObj.patientInstance, [status: CREATED] }
        }
		 
	
		
	
        if (params.patientConsentInstance == null) {
            notFound()
            return
        }

        if (params.patientConsentInstance.hasErrors()) {
            respond patientConsentInstance.errors, view:'create2'
            return
        }

        patientConsentInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'patientConsentInstance.label', default: 'PatientConsent'), patientConsentInstance.id])
                redirect patientConsentInstance
            }
            '*' { respond patientConsentInstance, [status: CREATED] }
        }
    }
	
	
	
	
	
	
	
	

    def edit(PatientConsent patientConsentInstance) {
        respond patientConsentInstance
    }

    @Transactional
    def update(PatientConsent patientConsentInstance) {
        if (patientConsentInstance == null) {
            notFound()
            return
        }

        if (patientConsentInstance.hasErrors()) {
            respond patientConsentInstance.errors, view:'edit'
            return
        }

        patientConsentInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'PatientConsent.label', default: 'PatientConsent'), patientConsentInstance.id])
                redirect patientConsentInstance
            }
            '*'{ respond patientConsentInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(PatientConsent patientConsentInstance) {

        if (patientConsentInstance == null) {
            notFound()
            return
        }

        patientConsentInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'PatientConsent.label', default: 'PatientConsent'), patientConsentInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'patientConsentInstance.label', default: 'PatientConsent'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
