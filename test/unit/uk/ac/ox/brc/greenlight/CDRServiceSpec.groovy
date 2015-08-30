package uk.ac.ox.brc.greenlight

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(CDRService)
@Mock([ConsentForm,Patient])
class CDRServiceSpec extends Specification {





	void "saveOrUpdateConsentForm saves new consentForm in CDR"() {

		setup:
		def sendConsentToCDRCalled = false
		service.metaClass.sendConsentToCDR = { nhsNumber,hospitalNumber,consent ->
			sendConsentToCDRCalled = true
			return "success"
		}

		when:
		def patient = new Patient()
		def consentForm = new ConsentForm()
		def result = service.saveOrUpdateConsentForm(patient,consentForm)

		then:
		sendConsentToCDRCalled
		result == "success"
	}
}
