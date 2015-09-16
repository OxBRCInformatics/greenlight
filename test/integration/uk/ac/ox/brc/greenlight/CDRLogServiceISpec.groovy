package uk.ac.ox.brc.greenlight

import grails.test.spock.IntegrationSpec
import uk.ac.ox.brc.greenlight.Audit.CDRLog

/**
 * Created by soheil on 11/09/2015.
 */


class CDRLogServiceISpec extends IntegrationSpec {

	def CDRLogService

	def "countAllNotPersistedBeforeThis finds all CDRLog records which are before this CDRLog and belong to the same consent and are not persisedInCDR yet"() {

		when:
		def record = createCRDLogRecord()
		record.persistedInCDR = false
		record.consentAccessGUID = "123-456-789"
		record.actionDate = new Date()
		record.save(flush: true, failOnError: true)

		def afterRecord = createCRDLogRecord()
		afterRecord.persistedInCDR = false
		afterRecord.consentAccessGUID = "123-456-789"
		afterRecord.actionDate = new Date()
		afterRecord.save(flush: true, failOnError: true)


		def beforeRecord1 = createCRDLogRecord()
		beforeRecord1.persistedInCDR = false
		beforeRecord1.consentAccessGUID = "123-456-789"
		beforeRecord1.actionDate = new Date().minus(1)
		beforeRecord1.save(flush: true, failOnError: true)


		def beforeRecord2 = createCRDLogRecord()
		beforeRecord2.persistedInCDR = false
		beforeRecord2.consentAccessGUID = "123-456-789"
		beforeRecord2.actionDate = new Date().minus(2)
		beforeRecord2.save(flush: true, failOnError: true)

		then:
		CDRLogService.countAllNotPersistedBeforeThis(afterRecord) == 3
	}

	private def createCRDLogRecord(){
		new CDRLog(
				consentId: "1",
				consentFormId: "ABC12345",
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

