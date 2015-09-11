package uk.ac.ox.brc.greenlight

import uk.ac.ox.brc.greenlight.Audit.CDRLog

class CDRLogService {

	def CDRService

	def save(Long patientId, String nhsNumber,String hospitalNumber,Map consentDetailsMap,boolean persistedInCDR,String resultDetail,Exception exception, CDRLog.CDRActionType actionType) {

		def dateTimePersistedInCDR
		if(persistedInCDR){
			dateTimePersistedInCDR = new Date()
		}


		def cdr = new CDRLog(
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

				patientId: patientId?.toString(),
				nhsNumber: nhsNumber,
				hospitalNumber: hospitalNumber,

				actionDate: new Date(),
				action: actionType,
				persistedInCDR: persistedInCDR,
				dateTimePersistedInCDR: dateTimePersistedInCDR,
				resultDetail: resultDetail,
				//Check if it is a connectionError or mirthResult exception?
				connectionError: isConnectionError(exception)
				)
		cdr.save(failOnError: true)
	}


	def resendCDRLogRecordToCDR(CDRLog record) {

		if(!record){
			return [success: false, log: "CRDLog record not found!", cdrLog:null]
		}

		if(record.persistedInCDR) {
			return [success: false, log: "This record is persisted in CDR, can not send it again!", cdrLog:record]
		}

		if(countAllNotPersistedBeforeThis(record)>0){
			return [sucess: false, log:"There are older CDRLog records for this consent which are not resolved yet, please resolve those first",cdrLog: record]
		}

		def callResult
		if(record.action == CDRLog.CDRActionType.ADD) {
			//consentDetailsMap parameter has the same structure as CDRLog, so passing CDRLog object works fine as well
			callResult = CDRService.connectToCDRAndSendConsentForm(record.nhsNumber, record.hospitalNumber,{},record.properties,false)
		}else{
			callResult = CDRService.connectToCDRAndRemoveConsentFrom(record.nhsNumber, record.hospitalNumber,{},record.properties,false)
		}

		if(!record.attemptsCount) {
			record.attemptsCount = 0
		}

		def log = (callResult?.log ? callResult?.log : "")
		if(callResult.success) {
			record.persistedInCDR = true
			record.dateTimePersistedInCDR = new Date()
			log = "${log} Successfully sent to CDR"
		}

		def connectionError = isConnectionError(callResult?.exception)
		record.attemptsCount++
		//date,Time|callResult|connectionError|callResult.log
		record.attemptsLog = "${(record?.attemptsLog ? record?.attemptsLog+"\n" : "")}${new Date().format("dd/MM/yyyy HH:mm:ss")}|${callResult.success}|${connectionError}|${log}"


		record.save(flush: true,failOnError: true)
		return [success: true, log: "Successfully passed to CDR", cdrLog:record]
	}
	}
}
