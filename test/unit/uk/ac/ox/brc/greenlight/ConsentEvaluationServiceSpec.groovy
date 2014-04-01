package uk.ac.ox.brc.greenlight

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ConsentEvaluationService)
@Mock([ConsentForm,Response])
class ConsentEvaluationServiceSpec extends Specification {

	static final NO_CONSENT = ConsentStatus.NON_CONSENT
	static final CONSENT = ConsentStatus.FULL_CONSENT
	static final YES = Response.ResponseValue.YES
	static final NO = Response.ResponseValue.NO
	static final BLANK = Response.ResponseValue.BLANK
	static final AMBIGUOUS = Response.ResponseValue.AMBIGUOUS
    def setup() {
    }

    def cleanup() {
    }

    void "Running the consentEvaluationService"() {

        when:"Calling getConsentStatus with nothing"
        ConsentStatus result = service.getConsentStatus()

        then:"it returns Non_Consent status"
        result == NO_CONSENT
    }

    void "Running consentEvaluationService with a valid consent Form and all items to YES"(){

        when:"Calling getConsentStatus with a valid consent Form and all items to YES"
        ConsentForm consentForm = new ConsentForm();
        10.times {
            consentForm.addToResponses(new Response(answer:YES))
        }

        ConsentStatus result = service.getConsentStatus(consentForm)

        then:"it returns FULL_CONSENT status"
        result == CONSENT
    }


	void "The first item can be anything when we say it's optional, and still return FULL_CONSENT"(){

		given:
		ConsentForm consentForm = new ConsentForm();
		consentForm.addToResponses(new Response(answer:firstResponse, question: new Question(optional: true)))
		otherResponses.each { answer ->
			consentForm.addToResponses(new Response(answer:answer, question: new Question(optional: false)))
		}
		ConsentStatus result = service.getConsentStatus(consentForm)

		expect: "consent to be granted if the first item is anything, but the rest are YES"
		result == expectedResult

		where:
		firstResponse 	| expectedResult	| otherResponses
		YES				| NO_CONSENT		| [YES, NO, NO, NO]
		NO				| NO_CONSENT		| [YES, NO, NO, NO]
		BLANK			| NO_CONSENT		| [YES, NO, NO, NO]
		AMBIGUOUS		| NO_CONSENT		| [YES, NO, NO, NO]
		YES				| CONSENT			| [YES, YES, YES, YES]
		NO				| CONSENT			| [YES, YES, YES, YES]
		BLANK			| CONSENT			| [YES, YES, YES, YES]
		AMBIGUOUS		| CONSENT			| [YES, YES, YES, YES]
	}

	@Unroll
	void "Optional responses #templateOptionalResponses, responses: #responses, yields #expectedResult"(){

		def questions = []

		ConsentFormTemplate formTemplate = new ConsentFormTemplate(questions: questions)
		ConsentForm consentForm = new ConsentForm(template: formTemplate);
		responses.eachWithIndex { answer, i ->
			Question q = new Question(optional: templateOptionalResponses.contains(i))
			questions.push(q)
			consentForm.addToResponses(new Response(answer:answer, question: q))
		}
		ConsentStatus result = service.getConsentStatus(consentForm)

		expect: "consent to be granted if the first item is anything, but the rest are YES"
		result == expectedResult

		where:
		expectedResult	| responses		      			| templateOptionalResponses
		NO_CONSENT		| [YES, YES, NO, NO, NO]   		| []
		CONSENT			| [YES, YES, YES, YES, YES]   	| []
		NO_CONSENT		| [YES, YES, YES, NO, YES]		| [0, 2]
		NO_CONSENT		| [YES, YES, NO, NO, YES]		| [0, 2]
		CONSENT			| [NO, YES, NO, YES, YES]		| [0, 2]
		CONSENT			| [YES, YES, YES, YES, YES]		| [0, 2]
		CONSENT			| [NO, YES, NO, YES, YES]		| [0, 2]

		// Sparse arrays are fine
		NO_CONSENT		| [NO, YES, NO, NO, NO]		| [0, 2, 4]
		CONSENT			| [YES, YES, NO, YES, NO]		| [0, 2, 4]

		// The order shouldn't matter
		CONSENT			| [YES, YES, NO, YES, NO]		| [4, 2, 0]

		// All optional is possible and handled gracefully
		CONSENT			| [NO, NO, NO, NO, NO]			| [0, 1, 2, 3 ,4]
		CONSENT			| [YES, YES, NO, NO, NO]		| [0, 1, 2, 3 ,4]
		CONSENT			| [YES, YES, NO, NO, NO]		| [0, 1, 2, 3 ,4]
		CONSENT			| [NO, YES, NO, NO, NO]			| [0, 1, 2, 3 ,4]
		CONSENT			| [YES, YES, YES, YES, YES]		| [0, 1, 2, 3 ,4]
	}
}