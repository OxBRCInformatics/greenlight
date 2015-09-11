package uk.ac.ox.brc.greenlight

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import uk.ac.ox.brc.greenlight.Audit.CDRLog

import java.text.SimpleDateFormat

/**
 * Created by soheil on 11/09/2015.
 */

@TestFor(CDRLogService)
@Mock([CDRLog])
class CDRLogServiceSpec extends spock.lang.Specification {

	def setup(){
		service.CDRService = Mock(CDRService)
	}

	def "resendCDRLogRecordToCDR does not send a CDRLog to CDR if it is Persisted"(){

		when:"resendCDRLogRecordToCDR called"
		def record = new CDRLog(persistedInCDR: true)
		def result = service.resendCDRLogRecordToCDR(record)

		then:"it fails"
		result == 	[success: false, log: "This record is persisted in CDR, can not send it again!", cdrLog:record]
	}

	def "resendCDRLogRecordToCDR does not send a CDRLog to CDR if record is not found"(){

		when:"resendCDRLogRecordToCDR called"
		def result = service.resendCDRLogRecordToCDR(null)

		then:"it fails"
		result == 	[success: false, log: "CRDLog record not found!", cdrLog:null]
	}

	def "resendCDRLogRecordToCDR sends 'SEND' message into CDR for ADD action"(){

		setup:"a CDRLog record exists"
		def dtf = new SimpleDateFormat("yyyyMMdd")
		def record = createCRDLogRecord()
		record.action = CDRLog.CDRActionType.ADD
		record.persistedInCDR = false

		//Mock Gorm Save and make sure that it is called
		def cdrLogSaveCalled = false
		CDRLog.metaClass.save = {  Map params ->
			cdrLogSaveCalled = true
		}
		when:"resendCDRLogRecordToCDR called"
		def result = service.resendCDRLogRecordToCDR(record)

		then:"it calls connectToCDRAndSendConsentForm"
		1 * service.CDRService.connectToCDRAndSendConsentForm(record.nhsNumber,record.hospitalNumber,_,record.properties,false) >> {
			[success:true,log:"SUCCESS",exception:null]
		}
		record.attemptsCount == 1
		!record.connectionError
		record.persistedInCDR
		record.attemptsLog
		dtf.format(record.dateTimePersistedInCDR) == dtf.format(new Date())
		result == [success: true, log: "Successfully passed to CDR", cdrLog:record]
		cdrLogSaveCalled
	}

	def "resendCDRLogRecordToCDR sends 'REMOVE' message into CDR for REMOVE action"(){

		setup:"a CDRLog record exists"
		def dtf = new SimpleDateFormat("yyyyMMdd")
		def record = createCRDLogRecord()
		record.action = CDRLog.CDRActionType.REMOVE
		record.persistedInCDR = false

		//Mock Gorm Save and make sure that it is called
		def cdrLogSaveCalled = false
		CDRLog.metaClass.save = {  Map params ->
			cdrLogSaveCalled = true
		}
		when:"resendCDRLogRecordToCDR called"
		def result = service.resendCDRLogRecordToCDR(record)

		then:"it calls connectToCDRAndRemoveConsentFrom"
		1 * service.CDRService.connectToCDRAndRemoveConsentFrom(record.nhsNumber,record.hospitalNumber,_,record.properties,false) >> {
			[success:true,log:"SUCCESS",exception:null]
		}
		record.attemptsCount == 1
		!record.connectionError
		record.persistedInCDR
		record.attemptsLog
		dtf.format(record.dateTimePersistedInCDR) == dtf.format(new Date())
		result == [success: true, log: "Successfully passed to CDR", cdrLog:record]
		cdrLogSaveCalled
	}


	private def createCRDLogRecord(){
		new CDRLog(
			consentFormId: "1",
			consentAccessGUID: "123-456-789",
			consentTemplateId: "1",
			consentDate: Date.parse("dd/MM/yyyy","01/09/2000"),
			consentStatus: ConsentForm.ConsentStatus.FULL_CONSENT,
			comment: "NO-COMMENT",
			consentStatusLabels:"Label1 \n Label2",
			cdrUniqueId:"GE_V1",
			namePrefix:"GEL",
			consentURL:"http://GREENLIGHT.COM/consent/123-456-789",

			patientId: "1",
			nhsNumber: "1234567890",
			hospitalNumber: "12345",

			actionDate: Date.parse("dd/MM/yyyy","05/02/2000"),
			action: CDRLog.CDRActionType.ADD,
			persistedInCDR: true,
			dateTimePersistedInCDR: Date.parse("dd/MM/yyyy","05/02/2000"),
			resultDetail: "SAVE_SUCCESSFULLY",
			connectionError: false
		)
	}
}
