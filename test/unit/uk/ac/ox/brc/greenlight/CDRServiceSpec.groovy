package uk.ac.ox.brc.greenlight

import com.mirth.results.client.PatientModel
import com.mirth.results.client.result.ResultModel
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import uk.ac.ox.brc.greenlight.Audit.CDRLog
import uk.ac.ox.ndm.mirth.datamodel.dsl.clinical.patient.Consent
import uk.ac.ox.ndm.mirth.datamodel.dsl.core.Facility
import uk.ac.ox.ndm.mirth.datamodel.exception.rest.ClientException
import uk.ac.ox.ndm.mirth.datamodel.rest.client.KnownFacility
import uk.ac.ox.ndm.mirth.datamodel.rest.client.KnownOrganisation
import uk.ac.ox.ndm.mirth.datamodel.rest.client.KnownPatientStatus

import java.text.SimpleDateFormat

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(CDRService)
@Mock([ConsentForm,Patient,ConsentFormTemplate,CDRLog])
class CDRServiceSpec extends Specification {



	def setup(){
		service.patientService = Mock(PatientService)
		service.consentFormService = Mock(ConsentFormService)
		service.CDRLogService = Mock(CDRLogService)
	}

	void "connectToCDRAndSendConsentForm sends consent into CDR and returns success"(){
		setup:
		def nhsNumber = "1234567890"
		def hospitalNumber = "123"
		def consentForm = new ConsentForm(template:new ConsentFormTemplate(cdrUniqueId: "GEL_CSC_V1") )
		//Mock the internal methods of the Service
		service.metaClass.createCDRClient   = {
			def client = new Object()
			client.metaClass.createOrUpdatePatientConsent = {Consent consent, uk.ac.ox.ndm.mirth.datamodel.dsl.clinical.patient.Patient patient, KnownFacility receivingFacility,String consentStatus, String appliedOrganisation,Collection<String> patientGroupsInOrganisation ->
					def result = new ResultModel<PatientModel>()
					result.metaClass.isOperationSucceeded = {
						return  true
					}
					result.metaClass.getConditionDetailsAsString = {
						return  "Detail_Result_of_Actions"
					}
					return result
			}
			return client
		}
		service.metaClass.createCDRFacility = {new Facility()}
		service.metaClass.findKnownOrganisation = {return KnownOrganisation.GEL_PILOT}
		service.metaClass.findPatientGroup = {return "GEL_CSC_V1"}
		service.metaClass.findKnownFacility = {return KnownFacility.TEST}
		service.metaClass.grailsApplication.getConfig = { [cdr:[knownFacility:"TEST",organisation:"Greenlight"] ]  }

		def consentDetailsMap = service.buildConsentDetailsMap(consentForm,consentForm.template)

		when:
		def result = service.connectToCDRAndSendConsentForm(nhsNumber,hospitalNumber,null,consentDetailsMap,true);

		then:
		result.success
		result.log == "Detail_Result_of_Actions"
	}

	void "connectToCDRAndSendConsentForm sends blank value for NHSNumber if it has a generic(1111111111) value"(){
		setup:
		def nhsNumber = "1111111111"
		def hospitalNumber = "123"
		def consentForm = new ConsentForm(template:new ConsentFormTemplate(cdrUniqueId: "GEL_CSC_V1") )
		//Mock the internal methods of the Service
		service.metaClass.createCDRClient   = {
			def client = new Object()
			client.metaClass.createOrUpdatePatientConsent = {Consent consent, uk.ac.ox.ndm.mirth.datamodel.dsl.clinical.patient.Patient patient, KnownFacility receivingFacility,String consentStatus, String appliedOrganisation,Collection<String> patientGroupsInOrganisation ->

				//it should not have NHSAlias as blank is passed for patient NHS number
				assert patient.getNHSAlias() == null

				def result = new ResultModel<PatientModel>()
				result.metaClass.isOperationSucceeded = {
					return  true
				}
				result.metaClass.getConditionDetailsAsString = {
					return  "Detail_Result_of_Actions"
				}
				return result
			}
			return client
		}
		service.metaClass.createCDRFacility = {new Facility()}
		service.metaClass.findKnownOrganisation = {return KnownOrganisation.GEL_PILOT}
		service.metaClass.findPatientGroup = {return "GEL_CSC_V1"}
		service.metaClass.findKnownFacility = {return KnownFacility.TEST}
		service.metaClass.grailsApplication.getConfig = { [cdr:[knownFacility:"TEST",organisation:"Greenlight"] ]  }

		def consentDetailsMap = service.buildConsentDetailsMap(consentForm,consentForm.template)


		when:
		1 * service.patientService.isGenericNHSNumber(_) >> {true}
		def result = service.connectToCDRAndSendConsentForm(nhsNumber,hospitalNumber,null,consentDetailsMap,true);

		then:
		result.success
		result.log == "Detail_Result_of_Actions"
	}

	void "connectToCDRAndRemoveConsentFrom sends blank value for NHSNumber if it has a generic(1111111111) value"(){
		setup:
		def nhsNumber = "1111111111"
		def hospitalNumber = "123"
		def consentForm = new ConsentForm(template:new ConsentFormTemplate(cdrUniqueId: "GEL_CSC_V1") )
		//Mock the internal methods of the Service
		service.metaClass.createCDRClient   = {
			def client = new Object()
			client.metaClass.removePatientConsent = {Consent consent, uk.ac.ox.ndm.mirth.datamodel.dsl.clinical.patient.Patient patient, KnownFacility receivingFacility,KnownOrganisation appliedOrganisation, Collection<String> patientGroups ->

				//it should not have NHSAlias as blank is passed for patient NHS number
				assert patient.getNHSAlias() == null

				def result = new ResultModel<PatientModel>()
				result.metaClass.isOperationSucceeded = {
					return  true
				}
				result.metaClass.getConditionDetailsAsString = {
					return  "Detail_Result_of_Actions"
				}
				return result
			}
			return client
		}
		service.metaClass.createCDRFacility = {new Facility()}
		service.metaClass.findKnownOrganisation = {return KnownOrganisation.GEL_PILOT}
		service.metaClass.findPatientGroup = {return "GEL_CSC_V1"}
		service.metaClass.findKnownFacility = {return KnownFacility.TEST}
		service.metaClass.grailsApplication.getConfig = { [cdr:[knownFacility:"TEST",organisation:"Greenlight"] ]  }
		def consentDetailsMap = service.buildConsentDetailsMap(consentForm,consentForm.template)

		when:
		1 * service.patientService.isGenericNHSNumber(_) >> {true}
		def result = service.connectToCDRAndRemoveConsentFrom(nhsNumber,hospitalNumber,null,consentDetailsMap,true);

		then:
		result.success
		result.log == "Detail_Result_of_Actions"
	}

	void "connectToCDRAndSendConsentForm returns exception message when has error"(){
		setup:
		def nhsNumber = "1234567890"
		def hospitalNumber = "123"
		def consentForm = new ConsentForm(template:new ConsentFormTemplate(cdrUniqueId: "GEL_CSC_V1") )
		//Mock the internal methods of the Service
		service.metaClass.createCDRClient   = {
			def client = new Object()
			client.metaClass.createOrUpdatePatientConsent = {Consent consent, uk.ac.ox.ndm.mirth.datamodel.dsl.clinical.patient.Patient patient, KnownFacility receivingFacility,String consentStatus, String appliedOrganisation,Collection<String> patientGroupsInOrganisation ->
					throw new ClientException("Exception in calling CDR")
			}
			return client
		}
		service.metaClass.createCDRFacility = {new Facility()}
		service.metaClass.findKnownOrganisation = { return KnownOrganisation.GEL_PILOT}
		service.metaClass.findPatientGroup = {return "GEL_CSC_V1"}
		service.metaClass.findKnownFacility = {return KnownFacility.TEST}
		service.metaClass.grailsApplication.getConfig = { [cdr:[knownFacility:"TEST",organisation:"Greenlight"] ]  }

		def consentDetailsMap = service.buildConsentDetailsMap(consentForm,consentForm.template)

		when:
		def result = service.connectToCDRAndSendConsentForm(nhsNumber,hospitalNumber,null,consentDetailsMap,true);

		then:
		!result.success
		result.log == "Exception in calling CDR"
	}

	def "findKnownPatientStatus returns KnownPatientStatus based on ConsentForm.ConsentStatus"(){
		when:
		def actual = service.findKnownPatientStatus(consentStatus)
		assert ConsentForm.ConsentStatus.values().size() == 3
		assert KnownPatientStatus.values().size() == 5

		then:
		actual == expected

		where:
		consentStatus							|	expected
		ConsentForm.ConsentStatus.NON_CONSENT	|	KnownPatientStatus.NON_CONSENT
		ConsentForm.ConsentStatus.FULL_CONSENT	|	KnownPatientStatus.CONSENTED
		ConsentForm.ConsentStatus.CONSENT_WITH_LABELS	|	KnownPatientStatus.RESTRICTED_CONSENT
		'UN-KNOWN'										|	null
	}


	def "findKnownOrganisation will return Organisation enum value"() {
		when:
		def actual = service.findKnownOrganisation(consentPrefix)
		assert KnownOrganisation.values().size() == 4

		then:
		actual == expected

		where:
		consentPrefix | expected
		"GEL"         | KnownOrganisation.GEL_PILOT
		"GLM"         | KnownOrganisation.GEL_MAIN
		"GEN"         | KnownOrganisation.ORB_GEN
		"CRA"         | KnownOrganisation.ORB_CRA
		"UNKNOWN"     | null

	}

 	def "findPatientGroup will return patientGroup(ConsentTemplateType) enum value"(){
		when:
		def actual = service.findPatientGroup(organisationName, consentUniqueId)
		assert KnownOrganisation.values().size() == 4
		//assert KnownOrganisation.GEL_PILOT.getPatientGroups()

		then:
		actual == expected

		where:
		organisationName            | consentUniqueId | expected
		KnownOrganisation.GEL_PILOT | "GEL_CSC_V1"    | "GEL_CSC_V1"
		KnownOrganisation.GEL_PILOT | "GEL_CSC_V2"    | "GEL_CSC_V2"
		KnownOrganisation.GEL_MAIN  | "GEL_MAN_V2"    | "GEL_MAN_V2"
		KnownOrganisation.ORB_GEN   | "ORB_GEN_V1"    | "ORB_GEN_V1"
		KnownOrganisation.ORB_GEN   | "ORB_GEN_V2"    | "ORB_GEN_V2"
		KnownOrganisation.ORB_GEN   | "ORB_PRE_V1_2"  | "ORB_PRE_V1_2"
		KnownOrganisation.ORB_CRA   | "ORB_CRA_V1"    | "ORB_CRA_V1"
		KnownOrganisation.ORB_CRA   | "UNKNOWN"       | null
	}

	def "Check if Greenlight uses the latest KnownOrganisation"() {
		expect:
		assert KnownOrganisation.values().size() == 4
		assert KnownOrganisation.GEL_PILOT.getPatientGroupIds().sort().toList() == ["GEL_CSC_V1","GEL_CSC_V2"]
		assert KnownOrganisation.GEL_MAIN.getPatientGroupIds().sort().toList()  == ["GEL_MAN_V2"]
		assert KnownOrganisation.ORB_GEN.getPatientGroupIds().sort().toList()   == ["ORB_GEN_V1","ORB_GEN_V2","ORB_PRE_V1_2"]
		assert KnownOrganisation.ORB_CRA.getPatientGroupIds().sort().toList()   == ["ORB_CRA_V1"]
	}

	def "findKnownFacility will return KnownFacility enum value"(){
		when:
		def actual = service.findKnownFacility(facilityName)
		assert KnownFacility.values().size() == 2

		then:
		actual == expected

		where:
		facilityName	|	expected
		"TEST"			|	KnownFacility.TEST
		"PRODUCTION"	|	KnownFacility.PRODUCTION
	}


	def "saveOrUpdateConsentForm passes consent to CDR if it is a new consent"() {
		given:"A new consent is added"
		def patient     = new Patient(nhsNumber: "1234567890",hospitalNumber: "123").save(failOnError: true,flush: true)
		def template    = new ConsentFormTemplate(name:"temp1",namePrefix:"TEMP",templateVersion: "V1" ).save(failOnError: true,flush: true)
		def consentForm = new ConsentForm(formID: "GEL12345",accessGUID: "456", template:template, patient:patient,consentDate: new Date()).save(failOnError: true,flush: true)

		def addNewConsentCalled = false
		service.metaClass.addNewConsent = { Patient pan, ConsentForm con->
			addNewConsentCalled = true
			return [success:true,log:"Sent_Log_TEXT",exception:null]
		}

		def removedConsentFormCalled = false
		service.metaClass.removeConsentForm = { ptn , con -> removedConsentFormCalled=true }

		when:"saveOrUpdateConsentForm called"
		def result = service.saveOrUpdateConsentForm(patient,consentForm,true)

		then:"addNewConsent should be called"
		addNewConsentCalled
		!removedConsentFormCalled
		result.success == true
		result.log == "Sent_Log_TEXT"
	}

	def "saveOrUpdateConsentForm passes consent to CDR and removes the old one if it is an updated consent or patient"() {
		given:"consent details are updated"
		def patient     = new Patient(nhsNumber: "1234567890",hospitalNumber: "123").save(failOnError: true,flush: true)
		def template    = new ConsentFormTemplate(name:"temp1",namePrefix:"TEMP",templateVersion: "V1" ).save(failOnError: true,flush: true)

		def consentForm = new ConsentForm(formID: "GEL12345",accessGUID: "456", template:template, consentStatusLabels: "OLD-VALUE", patient:patient,consentDate: new Date()).save(failOnError: true,flush: true)
		//patient and consent are updated
		patient.hospitalNumber = "UPDATED"
		patient.nhsNumber      = "9876543210"
		consentForm.consentStatus = ConsentForm.ConsentStatus.FULL_CONSENT
		consentForm.consentStatusLabels = "NEW-VALUE"


		def addNewConsentCalled = false
		service.metaClass.addNewConsent = { Patient ptn, ConsentForm con->
			addNewConsentCalled = true
			assert ptn.id 		 == patient.id
			assert ptn.nhsNumber == "9876543210" //should have old value
			assert ptn.hospitalNumber == "UPDATED"	//should have old value
			assert con.id == consentForm.id
			assert con.template    == consentForm.template
			assert con.accessGUID  == consentForm.accessGUID
			assert con.consentDate == consentForm.consentDate
			assert con.consentTakerName == consentForm.consentTakerName
			assert con.formID == consentForm.formID
			assert con.formStatus    == consentForm.formStatus
			assert con.consentStatus == consentForm.consentStatus
			assert con.comment == consentForm.comment
			assert con.consentStatusLabels == consentForm.consentStatusLabels
			assert con.savedInCDR == consentForm.savedInCDR
		}

		def removedConsentFormCalled = false
		service.metaClass.removeConsentForm = { def  ptn, def  con ->
			removedConsentFormCalled = true
			assert ptn.id 		 == patient.id
			assert ptn.nhsNumber == "1234567890" //should have old value
			assert ptn.hospitalNumber == "123"	//should have old value
			assert con.id == consentForm.id
			assert con.template    == consentForm.getPersistentValue("template")
			assert con.accessGUID  == consentForm.getPersistentValue("accessGUID")
			assert con.consentDate == consentForm.getPersistentValue("consentDate")
			assert con.consentTakerName == consentForm.getPersistentValue("consentTakerName")
			assert con.formID == consentForm.getPersistentValue("formID")
			assert con.formStatus    == consentForm.getPersistentValue("formStatus")
			assert con.consentStatus == consentForm.getPersistentValue("consentStatus")
			assert con.comment == consentForm.getPersistentValue("comment")
			assert con.consentStatusLabels == consentForm.getPersistentValue("consentStatusLabels")
			assert con.savedInCDR == consentForm.getPersistentValue("savedInCDR")
		}

		when:"saveOrUpdateConsentForm called"
		service.saveOrUpdateConsentForm(patient,consentForm,false)

		then:"addNewConsent and removeConsentForm should be called"
		addNewConsentCalled
		removedConsentFormCalled
	}

	def "saveOrUpdateConsentForm will not pass message to CDR if consent nad patient details are not changed"() {
		given:"A new consent is added"
		def patient     = new Patient(nhsNumber: "1234567890",hospitalNumber: "123").save(failOnError: true,flush: true)
		def template    = new ConsentFormTemplate(name:"temp1",namePrefix:"TEMP",templateVersion: "V1" ).save(failOnError: true,flush: true)
		def consentForm = new ConsentForm(formID: "GEL12345",accessGUID: "456", template:template, patient:patient,consentDate: new Date()).save(failOnError: true,flush: true)


		def addNewConsentCalled = false
		service.metaClass.addNewConsent = { Patient pan, ConsentForm con-> addNewConsentCalled = true }

		def removedConsentFormCalled = false
		service.metaClass.removeConsentForm = { ptn, con -> removedConsentFormCalled = true }

		when:"saveOrUpdateConsentForm called for updating"
		service.saveOrUpdateConsentForm(patient,consentForm,false)

		then:"addNewConsent should be called"
		!addNewConsentCalled
		!removedConsentFormCalled
	}



	def "CDR_Remove_Consent  prepares a consent for removal from CDR and updates its status"(){
		given:"patient and consent are ready to be removed from CDR"
		def patient  = new Patient(nhsNumber: "1234567890",hospitalNumber: "OLD").save(failOnError: true,flush: true)
		def template = new ConsentFormTemplate(name:"temp1",namePrefix:"GEL",templateVersion: "V1" ).save(failOnError: true,flush: true)
		def consentForm = new ConsentForm(formStatus: ConsentForm.FormStatus.SPOILED, formID: "GEL56890",accessGUID: "123", template:template, patient:patient,consentDate: new Date(),savedInCDR: true).save(failOnError: true,flush: true)

		def connectToCDRAndRemoveConsentFrom_Called = false
		service.metaClass.connectToCDRAndRemoveConsentFrom = { String nhsNumber,String  hospitalNumber,Closure patientAlias,Map consentDetailsMap,boolean checkIfWaitingInCDRLog ->
			connectToCDRAndRemoveConsentFrom_Called = true
			return [success: true,log:"Removed_LOG_TEXT",exception:null]
		}

		when:"CDR_Remove_Consent is called"
		def result = service.CDR_Remove_Consent(patient.id,patient.nhsNumber,patient.hospitalNumber,consentForm,template)

		then: "connectToCDRAndRemoveConsentFrom is called and also the status of the consent is updated"
		1 * service.CDRLogService.save(patient.id, patient.nhsNumber,patient.hospitalNumber,_,true,"Removed_LOG_TEXT",null,CDRLog.CDRActionType.REMOVE) >> {}

		connectToCDRAndRemoveConsentFrom_Called
		consentForm.persistedInCDR == false
		consentForm.dateTimePersistedInCDR == null
		consentForm.savedInCDR  == false
		consentForm.savedInCDRStatus   == null
		consentForm.dateTimePassedToCDR == null
		result.success
		result.log == "Removed_LOG_TEXT"
	}

	def "CDR_Send_Consent  prepares a consent for adding to CDR and updates its status"(){
		given:"patient and consent are ready to be saved in CDR"
		def dtf = new SimpleDateFormat("yyyyMMddHH")
		def patient  = new Patient(nhsNumber: "1234567890",hospitalNumber: "OLD").save(failOnError: true,flush: true)
		def template = new ConsentFormTemplate(name:"temp1",namePrefix:"TEMP",templateVersion: "V1" ).save(failOnError: true,flush: true)
		def consentForm = new ConsentForm(formStatus: ConsentForm.FormStatus.SPOILED, formID: "GEL56890",accessGUID: "123", template:template, patient:patient,consentDate: new Date(), savedInCDR: true).save(failOnError: true,flush: true)

		def connectToCDRAndSendConsentForm_Called = false
		service.metaClass.connectToCDRAndSendConsentForm = { String nhsNumber,String  hospitalNumber,Closure patientAlias,Map consentDetailsMap,boolean checkIfWaitingInCDRLog ->
			connectToCDRAndSendConsentForm_Called = true
			return [success: true,log:"SAVED_IN_CDR",exception:null]
		}

		when:"CDR_Send_Consent is called"
		def result = service.CDR_Send_Consent(patient.id,patient.nhsNumber,patient.hospitalNumber,consentForm,template)

		then: "connectToCDRAndSendConsentForm is called and also the status of the consent is updated"
		1 * service.CDRLogService.save(patient.id, patient.nhsNumber,patient.hospitalNumber,_,true,"SAVED_IN_CDR",null,CDRLog.CDRActionType.ADD) >> {}
		connectToCDRAndSendConsentForm_Called
		consentForm.persistedInCDR == true
		dtf.format(consentForm.dateTimePersistedInCDR) == dtf.format(new Date())
		consentForm.savedInCDR  == true
		consentForm.savedInCDRStatus == "SAVED_IN_CDR"
		consentForm.dateTimePassedToCDR
		result.success
		result.log == "SAVED_IN_CDR"
	}


	def "CDR_Send_Consent  prepares a consent for adding to CDR and updates its status even it CDR returns UNSUCCESSFUL"(){
		given:"patient and consent are ready to be saved in CDR"
		def patient  = new Patient(nhsNumber: "1234567890",hospitalNumber: "OLD").save(failOnError: true,flush: true)
		def template = new ConsentFormTemplate(name:"temp1",namePrefix:"TEMP",templateVersion: "V1" ).save(failOnError: true,flush: true)
		def consentForm = new ConsentForm(formStatus: ConsentForm.FormStatus.SPOILED, formID: "GEL56890",accessGUID: "123", template:template, patient:patient,consentDate: new Date(), savedInCDR: true).save(failOnError: true,flush: true)

		def connectToCDRAndSendConsentForm_Called = false
		service.metaClass.connectToCDRAndSendConsentForm = { String nhsNumber,String  hospitalNumber,Closure patientAlias,Map consentDetailsMap,boolean checkIfWaitingInCDRLog->
			connectToCDRAndSendConsentForm_Called = true
			return [success: false,log:"ERROR_IN_SAVING_IN_CDR",execption:new Exception("ERROR_IN_SAVING_IN_CDR")]
		}

		when:"CDR_Send_Consent is called"
		def result = service.CDR_Send_Consent(patient.id,patient.nhsNumber,patient.hospitalNumber,consentForm,template)

		then: "connectToCDRAndSendConsentForm is called and also the status of the consent is updated"
		1 * service.CDRLogService.save(patient.id, patient.nhsNumber,patient.hospitalNumber,_,false,"ERROR_IN_SAVING_IN_CDR",_,CDRLog.CDRActionType.ADD) >> {}
		connectToCDRAndSendConsentForm_Called
		//// ASSUME THAT ANY CALL TO CDR IS SUCCESSFUL AND THEN WE HANDLE THAT BY CDRLOG
		consentForm.persistedInCDR == false
		consentForm.dateTimePersistedInCDR == null
		consentForm.savedInCDR  == true
		consentForm.savedInCDRStatus == "ERROR_IN_SAVING_IN_CDR"
		consentForm.dateTimePassedToCDR
		!result.success
		result.log == "ERROR_IN_SAVING_IN_CDR"
	}


	def "createCDRFacility creates Greenlight facility for CDR"(){
		when:"createCDRFacility is called"
		def expected = service.createCDRFacility()

		then:"it returns a facility object"
		expected.getModel().id
		expected.getModel().name
		expected.getModel().descr
	}

	def "removeConsentForm will have NOP when consentForm is NOT savedInCDR"(){
		def patient  = new Patient(nhsNumber: "1234567890",hospitalNumber: "OLD").save(failOnError: true,flush: true)
		def template = new ConsentFormTemplate(name:"temp1",namePrefix:"TEMP",templateVersion: "V1" ).save(failOnError: true,flush: true)
		def consentForm = new ConsentForm(savedInCDR: false,formStatus: ConsentForm.FormStatus.NORMAL, formID: "GEL56890",accessGUID: "123", template:template, patient:patient,consentDate: new Date()).save(failOnError: true,flush: true)

		def connectToCDRAndSendConsentForm_Called = false
		service.metaClass.connectToCDRAndSendConsentForm = { String nhsNumber,String  hospitalNumber,Closure patientAlias,Map consentDetailsMap->
			connectToCDRAndSendConsentForm_Called = true
		}

		def connectToCDRAndRemoveConsentFrom_Called = false
		service.metaClass.connectToCDRAndRemoveConsentFrom = { String nhsNumber,String  hospitalNumber,Closure patientAlias,Map consentDetailsMap ->
			connectToCDRAndRemoveConsentFrom_Called = true
		}

		when:"removeConsentForm called"
		def result = service.removeConsentForm(patient,consentForm)

		then:"it doesn't need to pass any messages to CDR"
		0 *  service.consentFormService.findLatestConsentOfSameTypeBeforeThisConsentWhichIsNotSavedInCDR(patient.nhsNumber,patient.hospitalNumber,consentForm,consentForm.template) >> {null}
		result.success
		result.log == "no operation required"
		!connectToCDRAndSendConsentForm_Called
		!connectToCDRAndRemoveConsentFrom_Called
	}

	def "removeConsentForm will have NOP when consentForm is NOT NORMAL"(){
		def patient  = new Patient(nhsNumber: "1234567890",hospitalNumber: "OLD").save(failOnError: true,flush: true)
		def template = new ConsentFormTemplate(name:"temp1",namePrefix:"TEMP",templateVersion: "V1" ).save(failOnError: true,flush: true)
		def consentForm = new ConsentForm(savedInCDR: true,formStatus: ConsentForm.FormStatus.SPOILED, formID: "GEL56890",accessGUID: "123", template:template, patient:patient,consentDate: new Date()).save(failOnError: true,flush: true)

		def connectToCDRAndSendConsentForm_Called = false
		service.metaClass.connectToCDRAndSendConsentForm = { String nhsNumber,String  hospitalNumber,Closure patientAlias,Map consentDetailsMap->
			connectToCDRAndSendConsentForm_Called = true
		}

		def connectToCDRAndRemoveConsentFrom_Called = false
		service.metaClass.connectToCDRAndRemoveConsentFrom = { String nhsNumber,String  hospitalNumber,Closure patientAlias,Map consentDetailsMap ->
			connectToCDRAndRemoveConsentFrom_Called = true
		}

		when:"removeConsentForm called"
		def result = service.removeConsentForm(patient,consentForm)

		then:"it doesn't need to pass any messages to CDR"
		0 *  service.consentFormService.findLatestConsentOfSameTypeBeforeThisConsentWhichIsNotSavedInCDR(patient.nhsNumber,patient.hospitalNumber,consentForm,consentForm.template) >> {null}
		result.success
		result.log == "no operation required"
		!connectToCDRAndSendConsentForm_Called
		!connectToCDRAndRemoveConsentFrom_Called
	}

	def "removeConsentForm will pass a remove message to CDR when consent is savedInCDR"(){
		def patient  = new Patient(nhsNumber: "1234567890",hospitalNumber: "OLD").save(failOnError: true,flush: true)
		def template = new ConsentFormTemplate(name:"temp1",namePrefix:"TEMP",templateVersion: "V1" ).save(failOnError: true,flush: true)
		def consentForm = new ConsentForm(savedInCDR: true,formStatus: ConsentForm.FormStatus.NORMAL, formID: "GEL56890",accessGUID: "123", template:template, patient:patient,consentDate: new Date()).save(failOnError: true,flush: true)

		def connectToCDRAndSendConsentForm_Called = false
		service.metaClass.connectToCDRAndSendConsentForm = { String nhsNumber,String  hospitalNumber,Closure patientAlias,Map consentDetailsMap, boolean checkIfWaitingInCDRLog ->
			connectToCDRAndSendConsentForm_Called = true
		}

		def connectToCDRAndRemoveConsentFrom_Called = false
		service.metaClass.connectToCDRAndRemoveConsentFrom = { String nhsNumber,String  hospitalNumber,Closure patientAlias,Map consentDetailsMap, boolean checkIfWaitingInCDRLog ->
			connectToCDRAndRemoveConsentFrom_Called = true
			[success: true, log:"SUCCESSFULLY REMOVE THE CONSENT"]
		}

		when:"removeConsentForm called"
		def result = service.removeConsentForm(patient,consentForm)

		then:"it sends a remove message to CDR"
		1 *  service.consentFormService.findLatestConsentOfSameTypeBeforeThisConsentWhichIsNotSavedInCDR(patient.nhsNumber,patient.hospitalNumber,consentForm,consentForm.template) >> {null}
		result.success
		result.log == "SUCCESSFULLY REMOVE THE CONSENT"
		!connectToCDRAndSendConsentForm_Called
		connectToCDRAndRemoveConsentFrom_Called
	}

	def "removeConsentForm will pass a remove message to CDR when consent is savedInCDR and also passes the consent of the same type which is before this consent"(){
		def dtf = new SimpleDateFormat("yyyyMMdd")
		def patient  = new Patient(nhsNumber: "1234567890",hospitalNumber: "OLD").save(failOnError: true,flush: true)
		def template = new ConsentFormTemplate(name:"temp1",namePrefix:"TEMP",templateVersion: "V1" ).save(failOnError: true,flush: true)
		def consentForm 	  = new ConsentForm(savedInCDR: true,savedInCDRStatus: "",dateTimePassedToCDR: null, formStatus: ConsentForm.FormStatus.NORMAL, formID: "GEL56890",accessGUID: "123", template:template, patient:patient,consentDate: new Date()).save(failOnError: true,flush: true)

		def beforeConsentForm = new ConsentForm(savedInCDR: false,formStatus: ConsentForm.FormStatus.NORMAL, formID: "GEL12345",accessGUID: "456", template:template, patient:patient,consentDate: new Date().minus(10)).save(failOnError: true,flush: true)

		def connectToCDRAndSendConsentForm_Called = false
		service.metaClass.connectToCDRAndSendConsentForm = { String nhsNumber,String  hospitalNumber,Closure patientAlias,Map consentDetailsMap, boolean checkIfWaitingInCDRLog ->
			connectToCDRAndSendConsentForm_Called = true
			//check it the right consent is passed
			assert consentDetailsMap.consentId ==  beforeConsentForm.id
			[success: true, log:"SUCCESSFULLY PASSED THE CONSENT"]
		}

		def connectToCDRAndRemoveConsentFrom_Called = false
		service.metaClass.connectToCDRAndRemoveConsentFrom = { String nhsNumber,String  hospitalNumber,Closure patientAlias,Map consentDetailsMap, boolean checkIfWaitingInCDRLog ->
			connectToCDRAndRemoveConsentFrom_Called = true
			//check it the right consent is passed
			assert consentDetailsMap.consentId ==  consentForm.id
			[success: true, log:"SUCCESSFULLY REMOVE THE CONSENT"]
		}

		when:"removeConsentForm called"
		def result = service.removeConsentForm(patient,consentForm)

		then:"it sends a remove message to CDR"
		1 *  service.consentFormService.findLatestConsentOfSameTypeBeforeThisConsentWhichIsNotSavedInCDR(patient.nhsNumber,patient.hospitalNumber,consentForm,consentForm.template) >> {beforeConsentForm}
		result.success
		result.log == "SUCCESSFULLY REMOVE THE CONSENT"
		connectToCDRAndSendConsentForm_Called
		connectToCDRAndRemoveConsentFrom_Called
		beforeConsentForm.savedInCDR  == true
		dtf.format(beforeConsentForm.dateTimePassedToCDR) == dtf.format(new Date())
		beforeConsentForm.savedInCDRStatus == "SUCCESSFULLY PASSED THE CONSENT"
	}



	def "addNewConsent will have NOP when consentForm is NOT NORMAL"(){
		def patient  = new Patient(nhsNumber: "1234567890",hospitalNumber: "OLD").save(failOnError: true,flush: true)
		def template = new ConsentFormTemplate(name:"temp1",namePrefix:"TEMP",templateVersion: "V1" ).save(failOnError: true,flush: true)
		def consentForm = new ConsentForm(savedInCDR: false,formStatus: ConsentForm.FormStatus.SPOILED, formID: "GEL56890",accessGUID: "123", template:template, patient:patient,consentDate: new Date()).save(failOnError: true,flush: true)


		def CDR_Send_ConsentCalled = false
		service.metaClass.CDR_Send_Consent = { patientId, nhsNumber,hospitalNumber,con,temp ->
			CDR_Send_ConsentCalled = true
		}

		def connectToCDRAndSendConsentForm_Called = false
		service.metaClass.connectToCDRAndSendConsentForm = { String nhsNumber,String  hospitalNumber,Closure patientAlias,Map consentDetailsMap->
			connectToCDRAndSendConsentForm_Called = true
		}

		def connectToCDRAndRemoveConsentFrom_Called = false
		service.metaClass.connectToCDRAndRemoveConsentFrom = { String nhsNumber,String  hospitalNumber,Closure patientAlias,Map consentDetailsMap ->
			connectToCDRAndRemoveConsentFrom_Called = true
		}

		when:"addNewConsent called"
		def result = service.addNewConsent(patient,consentForm)

		then:"it doesn't need to pass any messages to CDR"
		0 *  service.consentFormService.findConsentsOfSameTypeAfterThisConsentWhichAreSavedInCDR(_,_,_) >> {null}
		0 *  service.consentFormService.findAnyConsentOfSameTypeBeforeThisConsentWhichIsSavedInCDR(_,_,_) >> {null}

		result.success
		result.log == "no operation required"
		!connectToCDRAndSendConsentForm_Called
		!connectToCDRAndRemoveConsentFrom_Called
		!CDR_Send_ConsentCalled
	}

}