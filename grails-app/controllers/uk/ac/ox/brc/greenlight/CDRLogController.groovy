package uk.ac.ox.brc.greenlight

import grails.converters.JSON
import uk.ac.ox.brc.greenlight.Audit.CDRLog

class CDRLogController {

	def CDRLogService

	def resendCDRLogRecordToCDR() {
		def result
		if(!params["id"]){
			result = [success: false, log: "Can not find CDRLog!", cdrLog: null]
		}else {
			def record = CDRLog.get(params["id"])
			result = CDRLogService.resendCDRLogRecordToCDR(record)
		}
		respond result as Object, [model: result] as Map
	}

	def markCDRLogRecordAsPersisted(){
		def result
		if(!params["id"]){
			result = [success: false, log: "Can not find CDRLog!", cdrLog: null]
		}else {
			def record = CDRLog.get(params["id"])
			result = CDRLogService.markCDRLogRecordAsPersisted(record,"")
		}
		respond result as Object, [model: result] as Map
	}

	def unMarkCDRLogRecordIfPersisted(){
		def result
		if(!params["id"]){
			result = [success: false, log: "Can not find CDRLog!", cdrLog: null]
		}else {
			def record = CDRLog.get(params["id"])
			result = CDRLogService.unMarkCDRLogRecordIfPersisted(record,"")
		}
		respond result as Object, [model: result] as Map
	}


	def list(){

	}

	def fetchRecords(){
		def order = params?.sSortDir_0
		def sortColIndex = params?.iSortCol_0
		def cols = [
				"1": "actionDate",
				"2": "action" ,
				"3": "persistedInCDR",
				"4": "consentAccessGUID",
				"5": "nhsNumber",
				"6": "hospitalNumber",
				"7": "dateTimePersistedInCDR",
				"8": "consentDate" ,
				"9": "consentFormId",
				"10": "consentStatus",
				"11": "resultDetail",
				"12": "connectionError",
				"13": "attemptsLog",
				"14": "attemptsCount"];

		if(!order)
			order = "desc"
		def sortCol = cols.containsKey(sortColIndex) ? cols[sortColIndex] : "actionDate"
		def data = CDRLog.createCriteria().list([max: params.iDisplayLength, offset: params.iDisplayStart, sort: sortCol, order: order]){}
		def result = [sEcho: params.sEcho, iTotalRecords: data.size(), iTotalDisplayRecords: data.totalCount, aaData: data]
		respond result as Object, [model: result] as Map
	}
}
