package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import spock.lang.Specification
import uk.ac.ox.brc.greenlight.ConsentForm.ConsentStatus

/**
 * Created by rb on 01/04/2014.
 */
@TestFor(ConsentFormService)
@grails.test.mixin.Mock([ConsentForm, ConsentFormTemplate])
class ConsentFormServiceUnitSpec extends Specification {

	static Date now = new Date();
    static final String DBL_QUOTE = '\"'
	def "getLatestConsentForms Get the latest consent forms for a patient"() {

		given:
		def formTemplates = [
				new ConsentFormTemplate(name: "FORM1", namePrefix: "fm1", templateVersion: "1", questions: []),
				new ConsentFormTemplate(name: "FORM2", namePrefix: "fm2", templateVersion: "1", questions: [])
		]

		def completedForms = [
				new ConsentForm(template: formTemplates[0], consentDate: now-14), // 2 weeks ago
				new ConsentForm(template: formTemplates[0], consentDate: now-7 ), // 1 week ago
				new ConsentForm(template: formTemplates[1], consentDate: now-1 ),
				new ConsentForm(template: formTemplates[1], consentDate: now-2 ),
				new ConsentForm(template: formTemplates[1], consentDate: now-5 ),
				new ConsentForm(template: formTemplates[1], consentDate: now-2 ),
				new ConsentForm(template: formTemplates[1], consentDate: now-3 )
		]
		def latestForms = [completedForms[1], completedForms[2]]

		when: "we get the list of latest forms from the service"
		def returnedForms = service.getLatestConsentForms(new Patient(consents: completedForms))

		then: "the most recent form for each type is returned"
		returnedForms.size() == latestForms.size()
		returnedForms.containsAll(latestForms)
		latestForms.containsAll(returnedForms)
	}

	def "getLatestConsentForms Get the latest consent forms for the list of patients (with the same NHS or MRN number)"() {

		given:
		def formTemplates = [
				new ConsentFormTemplate(name: "FORM1", namePrefix: "fm1", templateVersion: "1", questions: []),
				new ConsentFormTemplate(name: "FORM2", namePrefix: "fm2", templateVersion: "1", questions: [])
		]

		def completedForms = [
				new ConsentForm(template: formTemplates[0], consentDate: now-14), // 2 weeks ago
				new ConsentForm(template: formTemplates[0], consentDate: now-7 ), // 1 week ago
				new ConsentForm(template: formTemplates[1], consentDate: now-1 ), // 1 day ago
				new ConsentForm(template: formTemplates[1], consentDate: now-2 ), // 2 days ago
				new ConsentForm(template: formTemplates[1], consentDate: now-5 ),
				new ConsentForm(template: formTemplates[1], consentDate: now-2 ),
				new ConsentForm(template: formTemplates[1], consentDate: now-3 )
		]
		def latestForms1 = [completedForms[0],completedForms[1]]
		def latestForms2 = [completedForms[2],completedForms[3]]

		def patients = [new Patient(nhsNumber: "1234567890", consents: latestForms1),new Patient(consents: latestForms2,nhsNumber: "1234567890")]

		when: "we get the list of latest forms from the service"
		def returnedForms = service.getLatestConsentForms(patients)

		then: "the most recent form for each type is returned"
		returnedForms.size() == 2
		returnedForms.containsAll(completedForms[1])
		returnedForms.containsAll(completedForms[2])
	}
    def "Escaping text for CSV output"() {
        expect:
        service.escapeForCSV(inputString) == DBL_QUOTE + expectedString + DBL_QUOTE

        where:
        inputString | expectedString
        "This is some text" | "This is some text"
        "This is some\n text\n" | "This is some\t text\t"
        "," | ","
        DBL_QUOTE | DBL_QUOTE+DBL_QUOTE
		null	  | ""
    }
	def "getPatientWithMoreThanOneConsentForm will return patients who have certain number of full consent form"(){

		given:
		service.patientService 	   = Mock(PatientService)
		service.consentFormService = Mock(ConsentFormService)
		service.consentEvaluationService = Mock(ConsentEvaluationService)


		def patientsNHSNumber = ["123456789","1111111111","987654321"]
		def patients =  [
				new Patient(id:1, givenName  : 'A', familyName : 'B', nhsNumber : '123456789'),
				new Patient(id:2, givenName  : 'A', familyName : 'B', nhsNumber : '123456789'),
				new Patient(id:3, givenName  : 'Z', familyName : 'Z', nhsNumber : '987654321'),
				new Patient(id:5, givenName  : 'Mr0', familyName : 'Mr0', nhsNumber : '111111111'),
		]

		def consentsPatient0 = [
				new ConsentForm(consentStatus: ConsentStatus.FULL_CONSENT,template: new ConsentFormTemplate(namePrefix: "GEL"))
		]


		def consentsPatient1 = [
						new ConsentForm(consentStatus: ConsentStatus.FULL_CONSENT,template: new ConsentFormTemplate(namePrefix: "GEL")),
						new ConsentForm(consentStatus: ConsentStatus.FULL_CONSENT,template: new ConsentFormTemplate(namePrefix: "GEL"))
		]
		def consentsPatient2 = [
				new ConsentForm(consentStatus: ConsentStatus.FULL_CONSENT,template: new ConsentFormTemplate(namePrefix: "GEL")),
				new ConsentForm(consentStatus: ConsentStatus.FULL_CONSENT,template: new ConsentFormTemplate(namePrefix: "GEL")),
				new ConsentForm(consentStatus: ConsentStatus.FULL_CONSENT,template: new ConsentFormTemplate(namePrefix: "GEL"))
		]


		when:
		def result = service.getPatientWithMoreThanOneConsentForm()

		then:
		1 * service.patientService.groupPatientsByNHSNumber() >> {return patientsNHSNumber}
		3 * service.patientService.findAllByNHSOrHospitalNumber(_) >> {
			if(it[0] == "123456789"){
				return [patients[0],patients[1]]
			}else if(it[0] == "987654321"){
				return [patients[2]]
			}else {
				return [patients[3]]
			}
		}
		3 * service.consentFormService.getLatestConsentForms(_) >>{
			if(it[0][0].givenName == 'A')
				return consentsPatient1
			else if(it[0][0].givenName == 'Z')
				return consentsPatient2
			else if(it[0][0].givenName == 'Mr0')
				return consentsPatient0
		}

		result.size() == 2
		result[0].consents.size() == 2
		result[1].consents.size() == 3
	}
}