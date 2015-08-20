package uk.ac.ox.brc.greenlight.marshaller

import grails.converters.JSON
import uk.ac.ox.brc.greenlight.Question
import uk.ac.ox.brc.greenlight.Response

/**
 * Created by soheil on 20/08/2014.
 */
class QuestionMarshaller {
	void register() {
		JSON.registerObjectMarshaller(Question) { question ->
			return [
					id :  question?.id,
					name: question?.name,
					labelIfNotYes: question?.labelIfNotYes,
					defaultResponse: question?.defaultResponse,
					studyForm: question?.studyForm
			]
		}
	}
}
