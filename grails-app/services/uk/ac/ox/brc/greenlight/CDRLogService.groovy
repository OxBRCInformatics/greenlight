package uk.ac.ox.brc.greenlight

import uk.ac.ox.brc.greenlight.Audit.CDRLog

class CDRLogService {

	def add(String nhsNumber,String hospitalNumber,Map consentDetailsMap,boolean persistedInCDR,String resultDetail,String actionType) {
		new CDRLog(
				consentFormId: consentDetailsMap?.consentFormId,
				consentAccessGUID: consentDetailsMap?.consentAccessGUID,
				consentTemplateId: consentDetailsMap?.consentTemplateId,
				consentDate:consentDetailsMap?.consentDate,
				consentStatus:consentDetailsMap?.consentStatus,
				comment:consentDetailsMap?.comment,
				consentStatusLabels:consentDetailsMap?.consentStatusLabels,
				cdrUniqueId:consentDetailsMap?.cdrUniqueId,
				namePrefix:consentDetailsMap?.namePrefix,
				consentURL:consentDetailsMap?.consentURL,

				nhsNumber: nhsNumber,
				hospitalNumber: hospitalNumber,

				actionDate: new Date(),
				action: actionType,
				persistedInCDR: persistedInCDR,
				resultDetail: resultDetail).save(flush: true,failOnError: true)
	}
}
