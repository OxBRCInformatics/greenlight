package uk.ac.ox.brc.greenlight

import com.mirth.results.client.PatientModel
import com.mirth.results.client.result.ResultModel
import uk.ac.ox.ndm.mirth.datamodel.dsl.MirthModelDsl
import uk.ac.ox.ndm.mirth.datamodel.dsl.clinical.patient.Consent
import uk.ac.ox.ndm.mirth.datamodel.dsl.core.Facility
import uk.ac.ox.ndm.mirth.datamodel.exception.rest.ClientException
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

	def saveOrUpdateConsentForm(Patient patient, ConsentForm consentForm, boolean newConsent) {

		//in New mode
		if (newConsent) {
			return addNewConsent(patient,consentForm)
		}
		//in update mode
		else if (!newConsent) {

			//Patient updated, consent NOT updated
			if (patient.NHSOrHospitalNumberChanged() && !consentForm.isChanged() ) {

				//old consent was saved in CDR (*)
				if(consentForm.savedInCDR) {
					//remove oldPatient and OldConsent from CDR
					def oldNHSNumber = patient.getPersistentValue("nhsNumber")
					def oldHospitalNumber = patient.getPersistentValue("hospitalNumber")

					//Remove it from CDR (the oldPatient and its consent)
					def removeResult = CDR_Remove_Consent(oldNHSNumber, oldHospitalNumber, consentForm, consentForm.template)

					//find latest before consent which are the same type of the old-patient consent which are NOT sent to CDR and pass it
					ConsentForm olderNotSavedInCDRConsent = consentFormService.findLatestConsentOfSameTypeBeforeThisConsentWhichIsNotSavedInCDR(oldNHSNumber, oldHospitalNumber, consentForm, consentForm.template)
					if (olderNotSavedInCDRConsent) {
						//Pass it to CDR (the oldPatient and its latest before consent)
						def sendResult = CDR_Send_Consent(oldNHSNumber, oldHospitalNumber, olderNotSavedInCDRConsent, olderNotSavedInCDRConsent?.template)
					}

					//act the updated consent as a new consent
					return addNewConsent(patient, consentForm)
				}else{
					//old consent was NOT saved in CDR, so nop (*)
					return [success: true,log:"no operation required"]
				}
				//Consent template has changed
			} else if (!patient.NHSOrHospitalNumberChanged() && consentForm.isChanged() && consentForm.template.id != consentForm.getPersistentValue("template").id ) {

				def oldConsentTemplate = consentForm.getPersistentValue("template")

				//consent was passed to CDR before
				if(consentForm.savedInCDR){
					//Remove oldConsent from CDR
					def removeResult = CDR_Remove_Consent(patient.nhsNumber, patient.hospitalNumber,consentForm,oldConsentTemplate)

					//find latest consent which are the same type of the old-consent which are NOT sent to CDR and pass it
					ConsentForm latestConsent = consentFormService.findLatestConsentOfSameTypeBeforeThisConsentWhichIsNotSavedInCDR(patient.nhsNumber, patient.hospitalNumber, consentForm,oldConsentTemplate)
					if (latestConsent) {
						//Pass it to CDR
						def sendResult = CDR_Send_Consent(patient.nhsNumber, patient.hospitalNumber, latestConsent,latestConsent?.template)
					}
					//act the updated consent as a new consent
					return addNewConsent(patient,consentForm)
				}else{
					//old consent was NOT saved in CDR, so nop
					//as it was not saved in CDR, it means that there are newer consent other than that which are saved in CDR
					//so we do not need to do any operations
					return [success: true,log:"no operation required"]
				}


			}//consentDate changed
			 else if (!patient.NHSOrHospitalNumberChanged() && consentForm.isChanged() && consentForm.consentDate.compareTo(consentForm.getPersistentValue("consentDate")) != 0 ) {

				//NOT saved in CDR before, so it is not the latest one, so act it as a new consent
				if(!consentForm.savedInCDR){
					return addNewConsent(patient,consentForm)
				}else{

					//saved before in CDR, so it is the latest consent
					def oldConsentDate = consentForm.getPersistentValue("consentDate")
					def newConsentDate = consentForm.consentDate

					//is the new date after the old date ?
					if(newConsentDate.compareTo(oldConsentDate) > 0 ){
						//so the consent (which was the latest one) is moved to further date,so just update it in CDR
						def sendResult = CDR_Send_Consent(patient.nhsNumber, patient.hospitalNumber, consentForm,consentForm?.template)
						return sendResult
					}else if (newConsentDate.compareTo(oldConsentDate) < 0 ) {
						//are there any consent after the new date which are not sent ( of course those which are different from consentForm.id )?
						ConsentForm latestConsent = consentFormService.findLatestConsentOfSameTypeAfterThisConsentWhichIsNotSavedInCDR(patient.nhsNumber,patient.hospitalNumber,consentForm,consentForm.template)
						if(latestConsent){
							//Pass it to CDR & it will update the old one on CDR (retire the old one as well, as they both have the same template type)
							def sendResult = CDR_Send_Consent(patient.nhsNumber, patient.hospitalNumber, latestConsent,latestConsent?.template)

							//update consent status and mention that it is not in CDR
							consentForm.savedInCDR = false
							consentForm.passedToCDR = false
							consentForm.savedInCDRStatus = null
							//consentForm.save(flush: true, failOnError: true)
							return [success: true,log:"no operation required"]
						}else{
							//So Pass this one as it is still the latest one
							def sendResult = CDR_Send_Consent(patient.nhsNumber, patient.hospitalNumber, consentForm,consentForm?.template)
							return [success: true,log:sendResult.log]
						}
					}else{
						//it should not get to here
					}
				}
			}
			//oldConsentForm formStatus was NORMAL and now it is changed to DECLINED or SPOILED, so make the old one DECLINED or SPOILED
			else if (consentForm.getPersistentValue("formStatus") == ConsentForm.FormStatus.NORMAL &&
					 consentForm.formStatus != ConsentForm.FormStatus.NORMAL &&  consentForm.savedInCDR) {

				//Remove it from CDR
				def removeResult = CDR_Remove_Consent(patient.nhsNumber, patient.hospitalNumber,consentForm,consentForm.template)

				//find latest consent which are the same type of the old consent which are NOT sent to CDR and pass it
				ConsentForm latestConsent = consentFormService.findLatestConsentOfSameTypeBeforeThisConsentWhichIsNotSavedInCDR(patient.nhsNumber, patient.hospitalNumber, consentForm,consentForm.template)
				if (latestConsent) {
					//Pass it to CDR
					def sendResult = CDR_Send_Consent(patient.nhsNumber, patient.hospitalNumber, latestConsent,latestConsent?.template)
				}
				return removeResult
			}
			//oldConsentForm formStatus was NOT NORMAL and now it is NORMAL, so send it
			else if (consentForm.getPersistentValue("formStatus") != ConsentForm.FormStatus.NORMAL &&
					 consentForm.formStatus == ConsentForm.FormStatus.NORMAL) {
				//act like a new consentForm which is added
				return addNewConsent(patient,consentForm)
			}
			else if(consentForm.isChanged() && consentForm.savedInCDR &&
				    (consentForm.getPersistentValue("consentStatus")!=consentForm.consentStatus || consentForm.getPersistentValue("consentStatusLabels")!=consentForm.consentStatusLabels)){
					//other important details of consent are updated (such as consentStatus,consentStatusLabel, just send it to CDR again
					def sendResult = CDR_Send_Consent(patient.nhsNumber, patient.hospitalNumber, consentForm,consentForm?.template)
					return sendResult
			}else
			{
				//do nothing
			}
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
			def removeResult = CDR_Remove_Consent(patient.nhsNumber, patient.hospitalNumber, olderSavedInCDRConsent, olderSavedInCDRConsent.template)
		}

		//Pass it to CDR
		def sendResult = CDR_Send_Consent(patient.nhsNumber, patient.hospitalNumber, consentForm,consentForm.template)
		return sendResult
	}

	def buildConsentDetailsMap(ConsentForm consentForm,ConsentFormTemplate template){
		[consentFormId: consentForm.id,
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

	def CDR_Remove_Consent(nhsNumber,hospitalNumber,consentForm,template){
		//Remove it from CDR
		def consentDetailsMap = buildConsentDetailsMap(consentForm,template)
		def removeResult = connectToCDRAndRemoveConsentFrom(nhsNumber, hospitalNumber,{},consentDetailsMap)
		CDRLogService.add(nhsNumber, hospitalNumber, consentDetailsMap, removeResult.success,removeResult.log,"remove")

		//update consent status and mention that it is not in CDR
		consentForm.savedInCDR  = false
		consentForm.passedToCDR = false
		consentForm.savedInCDRStatus = null
		//consentForm.save(flush: true, failOnError: true)
		consentForm.dateTimePassedToCDR = null
		return  removeResult
	}

	def CDR_Send_Consent(nhsNumber,hospitalNumber,consentForm,template){
		//Pass it to CDR
		def consentDetailsMap = buildConsentDetailsMap(consentForm,template)
		def sendResult = connectToCDRAndSendConsentForm(nhsNumber, hospitalNumber,{}, consentDetailsMap)
		CDRLogService.add(nhsNumber, hospitalNumber, consentDetailsMap, sendResult.success,sendResult.log,"add")


		//// ASSUME THAT ANY CALL TO CDR IS SUCCESSFUL AND THEN WE HANDLE THAT BY CDRLOG
		consentForm.savedInCDR  = true //sendResult.success
		consentForm.passedToCDR = true
		consentForm.dateTimePassedToCDR = new Date()
		consentForm.savedInCDRStatus = sendResult.log
		//consentForm.save(flush: true, failOnError: true)
		return sendResult
	}


	def connectToCDRAndRemoveConsentFrom(String patientNHSNumber,String  patientHospitalNumber,Closure patientAlias, Map consentDetailsMap) {

		def cdrKnownFacilityConfig = grailsApplication.config?.cdr?.knownFacility
		def cdrOrganisationConfig = grailsApplication.config?.cdr?.organisation

		if (!cdrKnownFacilityConfig) {
			return [success: false, log: "cdr KnownFacility Config is not defined in config file"]
		}

		if (!cdrOrganisationConfig) {
			return [success: false, log: "cdr Organisation Config is not defined in config file"]
		}

		def knownOrganisation = findKnownOrganisation(consentDetailsMap?.namePrefix)
		if (!knownOrganisation) {
			return [success: false, log: "Can not find KnownOrganisation(Consent Form Prefix name) '${consentDetailsMap?.namePrefix}' in CDR KnownOrganisations"]
		}

		def knownFacility = findKnownFacility(cdrKnownFacilityConfig?.name)
		if (!knownFacility) {
			return [success: false, log: "Can not find KnownFacility '${cdrKnownFacilityConfig?.name}' in CDR KnownFacilities"]
		}

		//PatientGroup is actually the consentType in CDR definition
		def patientGroup = findPatientGroup(knownOrganisation,consentDetailsMap?.cdrUniqueId)
		if (!patientGroup) {
			return [success: false, log: "Can not find consent form template (PatientGroup) '${consentDetailsMap?.cdrUniqueId}' in CDR PatientGroup"]
		}
		//create a collection of patientGroups
		Collection<String> patientGroups = []
		patientGroups << patientGroup


		def consentURL = consentDetailsMap.consentURL //  consentFormService.getAccessGUIDUrl(consentForm).toString()

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
			//ex.printStackTrace()
			return [success: false, log: ex.message]
		}catch (Exception ex) {
			//ex.printStackTrace()
			return [success: false, log: ex.message]
		}

		if (resultOfAction && resultOfAction?.operationSucceeded) {
			return [success: true, log: resultOfAction.conditionDetailsAsString]
		} else {
			return [success: false, log: resultOfAction.conditionDetailsAsString]
		}
	}



	def connectToCDRAndSendConsentForm(String patientNHSNumber,String  patientHospitalNumber,Closure patientAlias ,Map consentDetailsMap) {

//		If it is a generic NHS number, set it to null
		if(patientService.isGenericNHSNumber(patientNHSNumber)) {
			patientNHSNumber = ""
		}

		def cdrKnownFacilityConfig = grailsApplication.config?.cdr?.knownFacility
		def cdrOrganisationConfig  = grailsApplication.config?.cdr?.organisation

		if (!cdrKnownFacilityConfig) {
			return [success: false, log: "cdr KnownFacility Config is not defined in config file"]
		}

		if (!cdrOrganisationConfig) {
			return [success: false, log: "cdr Organisation Config is not defined in config file"]
		}

		def knownOrganisation = findKnownOrganisation(consentDetailsMap?.namePrefix)
		if (!knownOrganisation) {
			return [success: false, log: "Can not find KnownOrganisation(Consent Form Prefix name) '${consentDetailsMap?.namePrefix}' in CDR KnownOrganisations"]
		}

		def knownFacility = findKnownFacility(cdrKnownFacilityConfig?.name)
		if (!knownFacility) {
			return [success: false, log: "Can not find KnownFacility '${cdrKnownFacilityConfig?.name}' in CDR KnownFacilities"]
		}

		def knownPatientStatus = findKnownPatientStatus(consentDetailsMap?.consentStatus)
		if (!knownPatientStatus) {
			return [success: false, log: "Can not find KnownPatientStatus '${consentDetailsMap?.consentStatus}' in CDR KnownPatientStatus"]
		}

		//PatientGroup is actually the consentType in CDR definition
		def patientGroup = findPatientGroup(knownOrganisation,consentDetailsMap?.cdrUniqueId)
		if (!patientGroup) {
			return [success: false, log: "Can not find consent(PatientGroup) '${consentDetailsMap?.cdrUniqueId}' in CDR PatientGroup"]
		}
		//create a collection of patientGroups
		Collection<String> patientGroups = []
		patientGroups << patientGroup


		def consentStatusCode = "OPT_IN"
		if(consentDetailsMap.consentStatus == ConsentForm.ConsentStatus.NON_CONSENT) {
			consentStatusCode = "OPT_OUT"
		}

		def consentURL = consentDetailsMap.consentURL // consentFormService.getAccessGUIDUrl(consentForm).toString()

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
			//ex.printStackTrace()
			return [success: false, log: ex.message]
		}catch (Exception ex) {
			//ex.printStackTrace()
			return [success: false, log: ex.message]
		}

		if (resultOfAction && resultOfAction?.operationSucceeded) {
			return [success: true, log: resultOfAction.conditionDetailsAsString]
		} else {
			return [success: false, log: resultOfAction.conditionDetailsAsString]
		}
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
