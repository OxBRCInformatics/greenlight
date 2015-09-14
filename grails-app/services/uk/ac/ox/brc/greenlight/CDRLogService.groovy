package uk.ac.ox.brc.greenlight

import uk.ac.ox.brc.greenlight.Audit.CDRLog

class CDRLogService {

	def CDRService
	def springSecurityService
	def consentFormService

	def transactional = false

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
			return [success: false, log:"There are older CDRLog records for this consent which are not resolved yet, please resolve those first.",cdrLog: record]
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

			//find the corresponding consentForm record and update it
			def consentForm = consentFormService.searchByAccessGUID(record.consentAccessGUID)
			//If it exists then update that but it might have been removed from Greenlight
			if(consentForm){
				consentForm.persistedInCDR = true
				consentForm.dateTimePersistedInCDR = new Date()
				consentForm.save(flush: true)
			}
		}

		def connectionError = isConnectionError(callResult?.exception)
		record.attemptsCount++
		//date,Time|callResult|connectionError|callResult.log
		record.attemptsLog = "${(record?.attemptsLog ? record?.attemptsLog+"\n" : "")}${new Date().format("dd/MM/yyyy HH:mm:ss")}|${callResult.success}|${connectionError}|${log}"


		record.save(flush: true,failOnError: true)
		return [success: true, log: "Successfully passed to CDR", cdrLog:record]
	}

	def markCDRLogRecordAsPersisted(CDRLogId,comment) {
		def record = CDRLog.findById(CDRLogId)

		if(!record){
			return [success: false, log: "CRDLog record not found!", cdrLog:record]
		}

		def username = springSecurityService.currentUser?.username

		record.persistedInCDR = true
		record.dateTimePersistedInCDR = null // it is not actually persisted, just manually marked as persisted
		if(!record.attemptsCount) {
			record.attemptsCount = 0
		}
		record.attemptsCount++
		//date,Time|callResult|connectionError|callResult.log
		record.attemptsLog = "${(record?.attemptsLog ? record?.attemptsLog+"\n" : "")}${new Date().format("dd/MM/yyyy HH:mm:ss")}|True|False	Manually resolved & marked as persisted by Admin(${username})|${comment}"
		record.save(flush: true,failOnError: true)
	}

	def unMarkCDRLogRecordIfPersisted(CDRLogId,comment) {
		def record = CDRLog.findById(CDRLogId)

		if(!record){
			return [success: false, log: "CRDLog record not found!"]
		}

		if(!record.persistedInCDR){
			return [success: false, log: "CRDLog record is not persisted!"]
		}

		def username = springSecurityService.currentUser?.username

		record.persistedInCDR = false
		record.dateTimePersistedInCDR = null // it is not actually persisted, just manually marked as persisted
		if(!record.attemptsCount) {
			record.attemptsCount = 0
		}
		record.attemptsCount++
		//date,Time|callResult|connectionError|callResult.log
		record.attemptsLog = "${(record?.attemptsLog ? record?.attemptsLog+"\n" : "")}${new Date().format("dd/MM/yyyy HH:mm:ss")}	False	False	Manually marked as UnPersisted by Admin(${username})	${comment}"
		record.save(flush: true,failOnError: true)
	}

	def isConnectionError(Exception ex){
		return ex == UnknownHostException ||  ex?.cause == UnknownHostException || ex?.message?.contains("Failed to access the WSDL")
	}

	/**
	 * check if a consent having this consentAccessGUID is in the queue and not persisted yet
 	 * @param consentAccessGUID consent accessGUID
	 * @return true if there are any consent having consentAccessGUID which are not persisted yet
	 */
	def isConsentWaitingForResolution(consentAccessGUID){
		 CDRLog.countByConsentAccessGUIDAndPersistedInCDR(consentAccessGUID,false) > 0
	}

	def countAllNotPersistedBeforeThis(CDRLog record){
		def count = CDRLog.countByConsentAccessGUIDAndActionDateLessThanAndPersistedInCDR(record.consentAccessGUID,record.actionDate,false)
		count
	}
}
