package uk.ac.ox.brc.greenlight.marshaller

import grails.converters.JSON
import uk.ac.ox.brc.greenlight.ConsentFormTemplate
import uk.ac.ox.brc.greenlight.Question

/**
 * Created by soheil on 20/08/2014.
 */
class ConsentFormTemplateMarshaller {
	void register() {
		JSON.registerObjectMarshaller(ConsentFormTemplate) { template ->
			return [
					id :  template?.id,
					cdrUniqueId: template?.cdrUniqueId,
					name: template?.name,
					namePrefix: template?.namePrefix,
					templateVersion:template?.templateVersion
			]
		}
	}
}
