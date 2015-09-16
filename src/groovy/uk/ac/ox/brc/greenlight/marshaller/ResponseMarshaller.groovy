package uk.ac.ox.brc.greenlight.marshaller

import grails.converters.JSON
import uk.ac.ox.brc.greenlight.Response

/**
 * Created by soheil on 20/08/2014.
 */
class ResponseMarshaller {
	void register() {
		JSON.registerObjectMarshaller(Response) { response ->
			return [
					id : response?.id,
					answer:response?.answer?.toString(),
					question: response?.question,
					description: response?.description
			]
		}
	}
}
