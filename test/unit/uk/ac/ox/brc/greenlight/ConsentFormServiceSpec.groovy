package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by rb on 01/04/2014.
 */
@TestFor(ConsentFormService)
@grails.test.mixin.Mock([ConsentForm, ConsentFormTemplate])
class ConsentFormServiceSpec extends Specification {

	static Date now = new Date();

	def "Get the latest consent forms for a patient"() {

		given:
		def formTemplates = [new ConsentFormTemplate(name: "FORM1", namePrefix: "fm1"), new ConsentFormTemplate(name: "FORM2", namePrefix: "fm2")]

		def completedForms = [
				new ConsentForm(template: formTemplates[0], consentDate: now-14), // 2 weeks ago
				new ConsentForm(template: formTemplates[0], consentDate: now-7 ), // 1 week ago
				new ConsentForm(template: formTemplates[1], consentDate: now-1 ),
				new ConsentForm(template: formTemplates[1], consentDate: now-2 ),
				new ConsentForm(template: formTemplates[1], consentDate: now-5 ),
				new ConsentForm(template: formTemplates[1], consentDate: now-2 ),
				new ConsentForm(template: formTemplates[1], consentDate: now-3 ),
		]
		def latestForms = [completedForms[1], completedForms[2]]

		when: "we get the list of latest forms from the service"
		def returnedForms = service.getLatestConsentForms(new Patient(consents: completedForms))

		then: "the most recent form for each type is returned"
		// Handle both objects as lists
		returnedForms == latestForms as List
	}
}
