package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import spock.lang.Specification

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
}
