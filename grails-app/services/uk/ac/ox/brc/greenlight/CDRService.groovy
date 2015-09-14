package uk.ac.ox.brc.greenlight

import com.mirth.results.client.PatientModel
import com.mirth.results.client.result.ResultModel
import uk.ac.ox.brc.greenlight.Audit.CDRLog
import uk.ac.ox.ndm.mirth.datamodel.dsl.MirthModelDsl
import uk.ac.ox.ndm.mirth.datamodel.dsl.clinical.patient.Consent
import uk.ac.ox.ndm.mirth.datamodel.dsl.core.Facility
import uk.ac.ox.ndm.mirth.datamodel.exception.rest.ClientException
import uk.ac.ox.ndm.mirth.datamodel.exception.rest.PatientNotFoundException
import uk.ac.ox.ndm.mirth.datamodel.rest.client.KnownFacility
import uk.ac.ox.ndm.mirth.datamodel.rest.client.KnownOrganisation
import uk.ac.ox.ndm.mirth.datamodel.rest.client.KnownPatientStatus
import uk.ac.ox.ndm.mirth.datamodel.rest.client.MirthRestClient

/**
 * added by Soheil on 18.08.2015
 * This service will pass consentForms into CDR (Clinical Document Repository)
 */

//@Transactional
class CDRService {
	def grailsApplication

	def consentFormService
	def patientService
	def CDRLogService


	static transactional = false

	def removeConsentForm(patient,consentForm){
		if(!consentForm.savedInCDR || consentForm.formStatus != ConsentForm.FormStatus.NORMAL){
			return [success: true,log:"no operation required"]
		}else {
			//Remove it from CDR
			def removeResult = CDR_Remove_Consent(patient.id, patient.nhsNumber, patient.hospitalNumber, consentForm, consentForm.template)
			//find latest consent which are the same type of the old consent which is NOT sent to CDR and pass it
			ConsentForm latestConsent = consentFormService.findLatestConsentOfSameTypeBeforeThisConsentWhichIsNotSavedInCDR(patient.nhsNumber, patient.hospitalNumber, consentForm, consentForm.template)
			if (latestConsent) {
				//Pass it to CDR
				def sendResult = CDR_Send_Consent(patient.id, patient.nhsNumber, patient.hospitalNumber, latestConsent, latestConsent?.template)

				//Persist the status of this latestConsent into DB
				latestConsent.save(flush: true)
			}
			return removeResult
		}
	}

	//Pre process before sending to CDR
	def addNewConsent(Patient patient, ConsentForm consentForm){

		if(consentForm.formStatus != ConsentForm.FormStatus.NORMAL){
			return [success: true,log:"no operation required"]
		}

		//Are there any consents newer than this of this type which are saved in CDR, so do not do any operation
		def newerSavedInCDRConsents = consentFormService.findConsentsOfSameTypeAfterThisConsentWhichAreSavedInCDR(patient.nhsNumber,patient.hospitalNumber,consentForm)
		if(newerSavedInCDRConsents.size() > 0){
			return [success: true,log:"no operation required"]
		}

		//Is there any consent older than this of this type which are saved in CDR, so remove them from CDR
		ConsentForm olderSavedInCDRConsent = consentFormService.findAnyConsentOfSameTypeBeforeThisConsentWhichIsSavedInCDR(patient.nhsNumber,patient.hospitalNumber,consentForm)
		if(olderSavedInCDRConsent) {
			//Remove it from CDR
			def removeResult = CDR_Remove_Consent(patient.id, patient.nhsNumber, patient.hospitalNumber, olderSavedInCDRConsent, olderSavedInCDRConsent.template)
		}

		//Pass it to CDR
		def sendResult = CDR_Send_Consent(patient.id, patient.nhsNumber, patient.hospitalNumber, consentForm,consentForm.template)
		return sendResult
	}

	def saveOrUpdateConsentForm(Patient patient, ConsentForm consentForm, boolean newConsent) {

		//in New mode
		if (newConsent) {
			return addNewConsent(patient,consentForm)
		}
		//in update mode
		else if (!newConsent && (patient.NHSOrHospitalNumberChanged() || consentForm.isChanged()) ) {
			def oldPatient = [id: patient.id,
							  nhsNumber:patient.getPersistentValue("nhsNumber"),
						      hospitalNumber:patient.getPersistentValue("hospitalNumber")]
			def oldConsentForm = [
					id: consentForm.id,
					template    : consentForm.getPersistentValue("template"),
					accessGUID  : consentForm.getPersistentValue("accessGUID"),
					consentDate : consentForm.getPersistentValue("consentDate"),
					consentTakerName : consentForm.getPersistentValue("consentTakerName"),
					formID : consentForm.getPersistentValue("formID"),
					formStatus    : consentForm.getPersistentValue("formStatus"),
					consentStatus : consentForm.getPersistentValue("consentStatus"),
					comment : consentForm.getPersistentValue("comment"),
					consentStatusLabels : consentForm.getPersistentValue("consentStatusLabels"),
					savedInCDR:consentForm.getPersistentValue("savedInCDR") ]

			def removeResult =  removeConsentForm(oldPatient,oldConsentForm)
			def addResult = addNewConsent(patient,consentForm)
			return addResult
		}else{
			return [success: true,log:"no operation required"]
		}
	}



	def buildConsentDetailsMap(consentForm, template){
		[consentId: consentForm.id,
		 consentFormId: consentForm.formID,
		 consentTemplateId: template.id,
		 consentAccessGUID: consentForm.accessGUID,
		 consentDate: consentForm.consentDate,
		 consentStatus: consentForm.consentStatus,
		 comment:consentForm.comment,
		 consentStatusLabels: consentForm.consentStatusLabels,
		 cdrUniqueId: template.cdrUniqueId,
		 namePrefix: template.namePrefix,
		 consentURL: consentFormService.getAccessGUIDUrl(consentForm).toString()]
	}


	def CDR_Remove_Consent(patientId, nhsNumber,hospitalNumber, consentForm,template){
		//Remove it from CDR
		def consentDetailsMap = buildConsentDetailsMap(consentForm,template)
		def removeResult = connectToCDRAndRemoveConsentFrom(nhsNumber, hospitalNumber,{},consentDetailsMap,true)
		CDRLogService.save(patientId,nhsNumber, hospitalNumber, consentDetailsMap, removeResult.success,removeResult.log,removeResult.exception,CDRLog.CDRActionType.REMOVE)

		//update consent status and mention that it is not in CDR
		consentForm.persistedInCDR = false
		consentForm.savedInCDR  = false
		consentForm.savedInCDRStatus = null
		//consentForm.save(flush: true, failOnError: true)
		consentForm.dateTimePassedToCDR = null
		return  removeResult
	}

	def CDR_Send_Consent(patientId, nhsNumber,hospitalNumber,consentForm,template){
		//Pass it to CDR
		def consentDetailsMap = buildConsentDetailsMap(consentForm,template)
		def sendResult = connectToCDRAndSendConsentForm(nhsNumber, hospitalNumber,{}, consentDetailsMap,true)
		CDRLogService.save(patientId, nhsNumber, hospitalNumber, consentDetailsMap, sendResult.success,sendResult.log,sendResult.execption, CDRLog.CDRActionType.ADD)

		//// ASSUME THAT ANY CALL TO CDR IS SUCCESSFUL AND THEN WE HANDLE THAT BY CDRLOG
		consentForm.persistedInCDR = sendResult.success
		if(sendResult.success){
			consentForm.dateTimePersistedInCDR = new Date()
		}else{
			consentForm.dateTimePersistedInCDR = null
		}
		consentForm.savedInCDR  = true //sendResult.success
		consentForm.dateTimePassedToCDR = new Date()
		consentForm.savedInCDRStatus = sendResult.log
		//consentForm.save(flush: true, failOnError: true)
		return sendResult
	}


	def connectToCDRAndRemoveConsentFrom(String patientNHSNumber,String  patientHospitalNumber,Closure patientAlias, Map consentDetailsMap,boolean checkIsWaitingForResolution) {

		//If it is a generic NHS number, set it to null
		if(patientService.isGenericNHSNumber(patientNHSNumber)) {
			patientNHSNumber = ""
		}

		def cdrKnownFacilityConfig = grailsApplication.config?.cdr?.knownFacility
		def cdrOrganisationConfig = grailsApplication.config?.cdr?.organisation

		if (!cdrKnownFacilityConfig) {
			return [success: false, log: "cdr KnownFacility Config is not defined in config file", execption:null]
		}

		if (!cdrOrganisationConfig) {
			return [success: false, log: "cdr Organisation Config is not defined in config file", execption:null]
		}

		def knownOrganisation = findKnownOrganisation(consentDetailsMap?.namePrefix)
		if (!knownOrganisation) {
			return [success: false, log: "Can not find KnownOrganisation(Consent Form Prefix name) '${consentDetailsMap?.namePrefix}' in CDR KnownOrganisations", execption:null]
		}

		def knownFacility = findKnownFacility(cdrKnownFacilityConfig?.name)
		if (!knownFacility) {
			return [success: false, log: "Can not find KnownFacility '${cdrKnownFacilityConfig?.name}' in CDR KnownFacilities", execption:null]
		}

		//PatientGroup is actually the consentType in CDR definition
		def patientGroup = findPatientGroup(knownOrganisation,consentDetailsMap?.cdrUniqueId)
		if (!patientGroup) {
			return [success: false, log: "Can not find consent form template (PatientGroup) '${consentDetailsMap?.cdrUniqueId}' in CDR PatientGroup", execption:null]
		}
		//create a collection of patientGroups
		Collection<String> patientGroups = []
		patientGroups << patientGroup


		def consentURL = consentDetailsMap.consentURL //  consentFormService.getAccessGUIDUrl(consentForm).toString()

		//CHECK IF THIS CONSENT HAS ANY WAITING STATUS RECORD IN CRD LOG, so do not send it to CDR Actually
		if(checkIsWaitingForResolution && CDRLogService.isConsentWaitingForResolution(consentDetailsMap?.consentAccessGUID)){
			return [success: false, log: "Consent is waiting for resolution by admin in CDRLog", execption:null]
		}

		ResultModel<PatientModel> resultOfAction
		try {
			def client = createCDRClient()
			def greenlight = createCDRFacility()
			def mirthModelDsl = new MirthModelDsl()

			def consent = mirthModelDsl.consent {
				authoringFacility greenlight
				appliesToOrganisation { id cdrOrganisationConfig?.id }
				effectiveOn consentDetailsMap.consentDate
				attachment {
					assert consentURL
					url consentURL
					description 'Greenlight Consent Form'
					sourceFacility greenlight
				}
			}

			uk.ac.ox.ndm.mirth.datamodel.dsl.clinical.patient.Patient patient = mirthModelDsl.patient {
				nhsNumber patientNHSNumber
				mrn patientHospitalNumber
				if(patientAlias) {
					alias patientAlias
				}
			} as uk.ac.ox.ndm.mirth.datamodel.dsl.clinical.patient.Patient

			resultOfAction = client.removePatientConsent(consent,patient,knownFacility,knownOrganisation,patientGroups)
		} catch (ClientException ex) {
			def logMessage = ex?.message
			log.error(logMessage)
			if(logMessage?.length() > 300){
				logMessage = logMessage.substring(0.300) + "..."
			}
			return [success: false, log: logMessage, execption:ex]
		}catch (Exception ex) {
			def logMessage = ex?.message
			log.error(logMessage)
			if(logMessage?.length() > 300){
				logMessage = logMessage.substring(0.300) + "..."
			}
			return [success: false, log: logMessage, execption:ex]
		}

		if (resultOfAction && resultOfAction?.operationSucceeded) {
			return [success: true, log: resultOfAction.conditionDetailsAsString, execption:null]
		} else {
			return [success: false, log: resultOfAction.conditionDetailsAsString, execption:null]
		}
	}



	def connectToCDRAndSendConsentForm(String patientNHSNumber,String  patientHospitalNumber,Closure patientAlias ,Map consentDetailsMap,boolean checkIsWaitingForResolution) {

//		If it is a generic NHS number, set it to null
		if(patientService.isGenericNHSNumber(patientNHSNumber)) {
			patientNHSNumber = ""
		}

		def cdrKnownFacilityConfig = grailsApplication.config?.cdr?.knownFacility
		def cdrOrganisationConfig  = grailsApplication.config?.cdr?.organisation

		if (!cdrKnownFacilityConfig) {
			return [success: false, log: "cdr KnownFacility Config is not defined in config file", execption:null]
		}

		if (!cdrOrganisationConfig) {
			return [success: false, log: "cdr Organisation Config is not defined in config file", execption:null]
		}

		def knownOrganisation = findKnownOrganisation(consentDetailsMap?.namePrefix)
		if (!knownOrganisation) {
			return [success: false, log: "Can not find KnownOrganisation(Consent Form Prefix name) '${consentDetailsMap?.namePrefix}' in CDR KnownOrganisations", execption:null]
		}

		def knownFacility = findKnownFacility(cdrKnownFacilityConfig?.name)
		if (!knownFacility) {
			return [success: false, log: "Can not find KnownFacility '${cdrKnownFacilityConfig?.name}' in CDR KnownFacilities", execption:null]
		}

		def knownPatientStatus = findKnownPatientStatus(consentDetailsMap?.consentStatus)
		if (!knownPatientStatus) {
			return [success: false, log: "Can not find KnownPatientStatus '${consentDetailsMap?.consentStatus}' in CDR KnownPatientStatus", execption:null]
		}

		//PatientGroup is actually the consentType in CDR definition
		def patientGroup = findPatientGroup(knownOrganisation,consentDetailsMap?.cdrUniqueId)
		if (!patientGroup) {
			return [success: false, log: "Can not find consent(PatientGroup) '${consentDetailsMap?.cdrUniqueId}' in CDR PatientGroup", execption:null]
		}
		//create a collection of patientGroups
		Collection<String> patientGroups = []
		patientGroups << patientGroup


		def consentStatusCode = "OPT_IN"
		if(consentDetailsMap.consentStatus == ConsentForm.ConsentStatus.NON_CONSENT) {
			consentStatusCode = "OPT_OUT"
		}

		def consentURL = consentDetailsMap.consentURL // consentFormService.getAccessGUIDUrl(consentForm).toString()


		//CHECK IF THIS CONSENT HAS ANY WAITING STATUS RECORD IN CRD LOG, so do not send it to CDR Actually
		if(checkIsWaitingForResolution && CDRLogService.isConsentWaitingForResolution(consentDetailsMap?.consentAccessGUID)){
			return [success: false, log: "Consent is waiting for resolution by admin in CDRLog", execption:null]
		}


		ResultModel<PatientModel> resultOfAction
		try {
			def client = createCDRClient()
			def greenlight = createCDRFacility()
			def mirthModelDsl = new MirthModelDsl()


			uk.ac.ox.ndm.mirth.datamodel.dsl.clinical.patient.Patient patient = mirthModelDsl.patient {
				nhsNumber patientNHSNumber
				mrn patientHospitalNumber
				if(patientAlias) {
					alias patientAlias
				}
			} as uk.ac.ox.ndm.mirth.datamodel.dsl.clinical.patient.Patient



			def consent =  mirthModelDsl.consent {
				authoringFacility greenlight
				appliesToOrganisation {id cdrOrganisationConfig?.id}
				effectiveOn consentDetailsMap.consentDate
				consentType { code consentStatusCode }
				attachment {
					description 'Greenlight Consent Form'
					sourceFacility greenlight
					//mimeType AttachmentModel.MimeType.PNG
					//id attachmentService.getAttachmentFileName(consentForm.attachedFormImage)
					// Any notes on the consent
					notes consentDetailsMap.comment
					//note "\n${consentForm?.consentStatusLabels.join['\n']}"
					note "\n${consentDetailsMap?.consentStatusLabels}"
					url consentURL
				}
			} as Consent
			resultOfAction = client.createOrUpdatePatientConsent(consent,patient,knownFacility,knownPatientStatus.toString(),knownOrganisation.toString(),patientGroups)

		}catch (ClientException ex) {
			def logMessage = ex?.message
			log.error(logMessage)
			if(logMessage?.length() > 300){
				logMessage = logMessage.substring(0.300) + "..."
			}
			return [success: false, log: logMessage, execption:ex]
		}catch (Exception ex) {
			def logMessage = ex?.message
			log.error(logMessage)
			if(logMessage?.length() > 300){
				logMessage = logMessage.substring(0.300) + "..."
			}
			return [success: false, log: logMessage, execption:ex]
		}

		if (resultOfAction && resultOfAction?.operationSucceeded) {
			return [success: true, log: resultOfAction.conditionDetailsAsString, execption:null]
		} else {
			return [success: false, log: resultOfAction.conditionDetailsAsString, execption:null]
		}
	}


	def findPatient(String nhsNumber,String hospitalNumber){
		PatientModel result

		def patientNHSNumber = nhsNumber
		def patientMRNNumber = hospitalNumber
		//pass null to CDR for generic nhsNumber
		if(patientService.isGenericNHSNumber(patientNHSNumber)){
			patientNHSNumber = ""
		}

		try {
			def client = createCDRClient()
			result = client?.findPatientByNHSNumberOrMRN(patientNHSNumber,patientMRNNumber)
		}catch(PatientNotFoundException notFoundException){
			return [success: true, log: "Not Found",patient:null, execption:notFoundException]
		}
		catch (ClientException ex) {
			//ex.printStackTrace()
			return [success: false, log: ex.message,patient:null, execption:ex]
		}

		def patient = [firstName: result.name.first,
					   lastName: result.name.last,
					   dateOfBirth: result.dob.toGregorianCalendar().getTime()]
		return [success: true, log: "",patient:patient,exception:null]
	}

	def createCDRClient(){
		def cdrAccessConfig  = grailsApplication.config?.cdr?.access
		if(!cdrAccessConfig){
			throw new Exception("cdr.access Config is not defined in config file")
		}
		return new MirthRestClient(cdrAccessConfig.username, cdrAccessConfig.password)
	}

	def testConnection(){
		def cdrAccessConfig  = grailsApplication.config?.cdr?.access
		if(!cdrAccessConfig){
			return [success:false,errors: "cdr.access Config is not defined in config file"]
		}

		try {
			new MirthRestClient(cdrAccessConfig.username, cdrAccessConfig.password)
		}
		catch(Exception ex){
			log.error(ex)
			return [success:false,errors:ex.message]
		}
		return [success:true,errors:null]
	}

	def createCDRFacility(){
		def cdrFacilityConfig  = grailsApplication.config?.cdr?.facility
		if(!cdrFacilityConfig){
			throw new Exception("cdr.facility Config is not defined in config file")
		}
		def mirthModelDsl = new MirthModelDsl()
		Facility greenlight = mirthModelDsl.facility {
			id cdrFacilityConfig.id
			name cdrFacilityConfig.name
			description cdrFacilityConfig.description
		} as Facility
		return greenlight
	}

	 def findKnownOrganisation(String consentFormPrefix) {
		 switch (consentFormPrefix) {
			 case "GEL":
				 return KnownOrganisation.GEL_PILOT
			 case "GEN":
			 case "PRE":
				 return KnownOrganisation.ORB_GEN
			 case "CRA":
				 return KnownOrganisation.ORB_CRA
			 default:
				 return null
		 }
	 }

	def findPatientGroup(KnownOrganisation knownOrganisation,String patientGroup){
		knownOrganisation.getPatientGroupIds().find {it == patientGroup }
	}

	 def findKnownFacility(name){
		def result
		 KnownFacility.values().each { value ->
			 if(value.name() == name) {
				 result = value
				 return
			 }
		 }
		result
	}

	def findKnownPatientStatus(consentStatus){
		switch (consentStatus){
			case ConsentForm.ConsentStatus.FULL_CONSENT:
				return KnownPatientStatus.CONSENTED;
			case ConsentForm.ConsentStatus.CONSENT_WITH_LABELS:
				return KnownPatientStatus.RESTRICTED_CONSENT;
			case ConsentForm.ConsentStatus.NON_CONSENT:
				return KnownPatientStatus.NON_CONSENT;
			default:
				return null;
		}
	}
}
