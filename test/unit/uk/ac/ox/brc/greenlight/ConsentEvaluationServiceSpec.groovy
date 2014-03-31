package uk.ac.ox.brc.greenlight

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ConsentEvaluationService)
@Mock([ConsentForm,Response])
class ConsentEvaluationServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "Running the consentEvaluationService"() {

        when:"Calling getConsentStatus with nothing"
        ConsentStatus result = service.getConsentStatus()

        then:"it returns Non_Consent status"
        result == ConsentStatus.NON_CONSENT
    }

    void "Running consentEvaluationService with a valid consent Form with any items NO"(){

        when:"Calling getConsentStatus with a consentForm with any items NO"
        ConsentForm consentForm = new ConsentForm();
        10.times {
            consentForm.addToResponses(new Response(answer:Response.ResponseValue.YES))
        }
        consentForm.addToResponses(new Response(answer:Response.ResponseValue.NO))
        3.times {
            consentForm.addToResponses(new Response(answer:Response.ResponseValue.YES))
        }

        ConsentStatus result = service.getConsentStatus(consentForm)

        then:"it returns Non_Consent status"
        result == ConsentStatus.NON_CONSENT
    }

    void "Running consentEvaluationService with a valid consent Form and all items to YES"(){

        when:"Calling getConsentStatus with a valid consent Form and all items to YES"
        ConsentForm consentForm = new ConsentForm();
        10.times {
            consentForm.addToResponses(new Response(answer:Response.ResponseValue.YES))
        }

        ConsentStatus result = service.getConsentStatus(consentForm)

        then:"it returns FULL_CONSENT status"
        result == ConsentStatus.FULL_CONSENT
    }


    void "Evaluating full consent with some items as NO"(){

        when:"Calling getConsentStatus with a valid consent Form and all items to YES and first answer NO"
        ConsentForm consentForm = new ConsentForm();
        consentForm.addToResponses(new Response(answer:Response.ResponseValue.NO))
        10.times {
            consentForm.addToResponses(new Response(answer:Response.ResponseValue.YES))
        }


        ConsentStatus result = service.getConsentStatus(consentForm)

        then:"it returns FULL_CONSENT status"
        result == ConsentStatus.FULL_CONSENT
    }



}