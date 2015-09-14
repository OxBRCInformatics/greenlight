package uk.ac.ox.brc.greenlight.marshaller

import grails.converters.JSON
import uk.ac.ox.brc.greenlight.Audit.CDRLog
import uk.ac.ox.brc.greenlight.ConsentForm

/**
 * Created by soheil on 12/09/2015.
 */
class CDRLogMarshaller {
	void register() {
		JSON.registerObjectMarshaller(CDRLog) { cdrLog ->
			return [
					id: cdrLog.id,
					consentFormId: cdrLog.consentFormId,
					consentTemplateId: cdrLog.consentTemplateId,
					consentDate: cdrLog.consentDate?.format("yyyy-MM-dd"),
					consentStatus: cdrLog.consentStatus?.toString(),
					comment: cdrLog.comment,
					consentStatusLabels: cdrLog.consentStatusLabels,
					cdrUniqueId: cdrLog.cdrUniqueId,
					namePrefix: cdrLog.namePrefix,
					consentURL: cdrLog.consentURL,
					consentAccessGUID: cdrLog.consentAccessGUID,
					patientId: cdrLog.patientId,
					nhsNumber: cdrLog.nhsNumber,
					hospitalNumber: cdrLog.hospitalNumber,
					actionDate: cdrLog.actionDate?.format("yyyy-MM-dd HH:mm:ss"),
					action: cdrLog.action?.toString(),
					persistedInCDR: (cdrLog.persistedInCDR ? "Yes":"No"),
					dateTimePersistedInCDR: cdrLog.dateTimePersistedInCDR?.format("yyyy-MM-dd HH:mm:ss"),
					resultDetail: cdrLog.resultDetail,
					connectionError: (cdrLog.connectionError ? "Yes":"No"),
					attemptsLog: cdrLog.attemptsLog,
					attemptsCount: cdrLog.attemptsCount]
		}
	}
}
