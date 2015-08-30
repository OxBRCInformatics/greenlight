package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.joda.time.DateTime
import spock.lang.Specification
import uk.ac.ox.brc.greenlight.ConsentForm.ConsentStatus

/**
 * Created by rb on 01/04/2014.
 */
@TestFor(ConsentFormService)
@grails.test.mixin.Mock([ConsentForm, ConsentFormTemplate])
class ConsentFormServiceUnitSpec extends Specification {

	def setup(){
		service.consentEvaluationService = Mock(ConsentEvaluationService)
		service.CDRService = Mock(CDRService)
		service.grailsLinkGenerator = Mock(LinkGenerator)
	}
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
		service.consentEvaluationService = Mock(ConsentEvaluationService)


		def patientsHospitalNumber = ["123456789","1111111111","987654321"]
		def patients =  [
				new Patient(id:1, givenName  : 'A', familyName : 'B', hospitalNumber : '123456789'),
				new Patient(id:2, givenName  : 'A', familyName : 'B', hospitalNumber : '123456789'),
				new Patient(id:3, givenName  : 'Z', familyName : 'Z', hospitalNumber : '987654321'),
				new Patient(id:5, givenName  : 'Mr0', familyName : 'Mr0', hospitalNumber : '111111111'),
				new Patient(id:5, givenName  : 'emptyHospitalNumber', familyName : 'emptyHospitalNumber', nhsNumber : '0000000000'),
		]

		def consentsPatient0 = [
				new ConsentForm(consentDate:Date.parse("yyy-MM-dd HH:mm:ss","2015-05-21 14:10:00") ,consentStatus: ConsentStatus.FULL_CONSENT,template: new ConsentFormTemplate(namePrefix: "GEL"),formID:"GEL123" )
		]


		def consentsPatient1 = [
						new ConsentForm(consentDate:Date.parse("yyy-MM-dd HH:mm:ss","2015-05-21 14:10:00") ,consentStatus: ConsentStatus.FULL_CONSENT,template: new ConsentFormTemplate(namePrefix: "GEL"),formID:"GEL123"),
						new ConsentForm(consentDate:Date.parse("yyy-MM-dd HH:mm:ss","2015-05-21 14:10:00") ,consentStatus: ConsentStatus.FULL_CONSENT,template: new ConsentFormTemplate(namePrefix: "CDA"),formID:"CDA123")
		]
		def consentsPatient2 = [
				new ConsentForm(consentDate:Date.parse("yyy-MM-dd HH:mm:ss","2015-05-21 14:10:00") ,consentStatus: ConsentStatus.FULL_CONSENT,template: new ConsentFormTemplate(namePrefix: "GEL"),formID:"GEL123"),
				new ConsentForm(consentDate:Date.parse("yyy-MM-dd HH:mm:ss","2015-05-21 14:10:00") ,consentStatus: ConsentStatus.FULL_CONSENT,template: new ConsentFormTemplate(namePrefix: "ABC"),formID:"ABC123"),
				new ConsentForm(consentDate:Date.parse("yyy-MM-dd HH:mm:ss","2015-05-21 14:10:00") ,consentStatus: ConsentStatus.FULL_CONSENT,template: new ConsentFormTemplate(namePrefix: "DEF"),formID:"DEF123")
		]

		service.metaClass.getLatestConsentForms = { param ->
			if(param[0].givenName == 'A')
				return consentsPatient1
			else if(param[0].givenName == 'Z')
				return consentsPatient2
			else if(param[0].givenName == 'Mr0')
				return consentsPatient0
		}

		when:
		def result = service.getPatientWithMoreThanOneConsentForm()

		then:
		1 * service.patientService.groupPatientsByHospitalNumber() >> {return patientsHospitalNumber}
		3 * service.patientService.findAllByNHSOrHospitalNumber(_) >> {
			if(it[0] == "123456789"){
				return [patients[0],patients[1]]
			}else if(it[0] == "987654321"){
				return [patients[2]]
			}else {
				return [patients[3]]
			}
		}

		result.size() == 2
		result[0].consentsString == "GEL[21-05-2015;GEL123]|CDA[21-05-2015;CDA123]"
		result[1].consentsString == "GEL[21-05-2015;GEL123]|ABC[21-05-2015;ABC123]|DEF[21-05-2015;DEF123]"
	}

	def "getLatestConsentForms returns the latest NORMAL consent forms for a patient"() {

		given:
		def formTemplates = [
				new ConsentFormTemplate(name: "FORM1", namePrefix: "fm1", templateVersion: "1", questions: []),
				new ConsentFormTemplate(name: "FORM2", namePrefix: "fm2", templateVersion: "1", questions: []),
				new ConsentFormTemplate(name: "FORM3", namePrefix: "fm3", templateVersion: "1", questions: [])
		]

		def completedForms = [
				new ConsentForm(template: formTemplates[0], consentDate: now,formStatus: ConsentForm.FormStatus.SPOILED),
				new ConsentForm(template: formTemplates[1], consentDate: now),
				new ConsentForm(template: formTemplates[2], consentDate: now,formStatus: ConsentForm.FormStatus.SPOILED), // latest one from formTemplates[2] template but SPOILED
				new ConsentForm(template: formTemplates[2], consentDate: now-1,formStatus: ConsentForm.FormStatus.DECLINED),
				new ConsentForm(template: formTemplates[2], consentDate: now-2)
		]
		def latestForms = [completedForms[1],completedForms[4]]

		when: "we get the list of latest NORMAL forms from the service"
		def returnedForms = service.getLatestConsentForms(new Patient(consents: completedForms))

		then: "the most recent NORMAL form for each type is returned"
		returnedForms.size() == 2
		returnedForms.containsAll(latestForms)
		latestForms.containsAll(returnedForms)
	}

	def "save will update consentStatus and cdr details"(){
		given:
		def patient = new Patient()
		def consent = new ConsentForm()

		when:
		service.save(patient,consent)

		then:
		1 * service.consentEvaluationService.getConsentStatus(_) >> {ConsentStatus.NON_CONSENT}
		1 * service.CDRService.saveOrUpdateConsentForm(_,_) >> {return "success_TEST"}
		consent.consentStatus == ConsentStatus.NON_CONSENT
		consent.passedToCDR
		consent.savedInCDRStatus == "success_TEST"
		new DateTime(consent.dateTimePassedToCDR).toLocalDate().compareTo(new DateTime().toLocalDate()) == 0 // check the date
	}

	def "getAccessGUIDUrl returns URL to consentForm by accessGUID"(){

		given:
		def consentForm = new ConsentForm(accessGUID: "1234-5678-0000")

		when:
		def url = service.getAccessGUIDUrl(consentForm)

		then:
		1 * service.grailsLinkGenerator.getServerBaseURL() >> {"HTTP-BASE-URL://APP-URL"}
		url == "HTTP-BASE-URL://APP-URL/consent/1234-5678-0000"

	}
}