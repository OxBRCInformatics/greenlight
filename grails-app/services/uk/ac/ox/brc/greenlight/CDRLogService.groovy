package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional
import uk.ac.ox.brc.greenlight.Audit.CDRLog

class CDRLogService {

	def add(consentFormId, consentTemplateId, nhsNumber, hospitalNumber, result, resultDetail,actionType) {
		new CDRLog(
				consentId: consentFormId,
				consentTemplateId: consentTemplateId,
				nhsNumber: nhsNumber,
				hospitalNumber: hospitalNumber,
				action: actionType,
				result: result,
				resultDetail: resultDetail).save(flush: true)
	}
}
