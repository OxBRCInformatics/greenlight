package uk.ac.ox.brc.greenlight

import com.mirth.results.client.PatientModel
import com.mirth.results.client.result.ResultModel
import grails.transaction.Transactional
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

@Transactional
class CDRService {
	def grailsApplication

	def consentFormService
	def attachmentService
	def patientService
	def CDRLogService


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
					ConsentForm olderNotSavedInCDRConsent = consentFormService.findLatestConsentOfSameTypeBeforeThisConsentWhichAreNotSavedInCDR(oldNHSNumber, oldHospitalNumber, consentForm, consentForm.template)
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
					ConsentForm latestConsent = consentFormService.findLatestConsentOfSameTypeBeforeThisConsentWhichAreNotSavedInCDR(patient.nhsNumber, patient.hospitalNumber, consentForm,oldConsentTemplate)
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
						ConsentForm latestConsent = consentFormService.findLatestConsentOfSameTypeAfterThisConsentWhichAreNotSavedInCDR(patient.nhsNumber,patient.hospitalNumber,consentForm,consentForm.template)
						if(latestConsent){
							//Pass it to CDR & it will update the old one on CDR (retire the old one as well as they both have the same template type)
							def sendResult = CDR_Send_Consent(patient.nhsNumber, patient.hospitalNumber, latestConsent,latestConsent?.template)

							//update consent status and mention that it is not in CDR
							consentForm.savedInCDR = false
							consentForm.passedToCDR = false
							consentForm.savedInCDRStatus = null
							consentForm.save(flush: true, failOnError: true)
							return [success: true,log:"no operation required"]
						}else{
							//do nothing just save the new one
							//update consent status and mention that it is not in CDR
							consentForm.savedInCDR = false
							consentForm.passedToCDR = false
							consentForm.savedInCDRStatus = null
							consentForm.save(flush: true, failOnError: true)
							return [success: true,log:"no operation required"]
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
				ConsentForm latestConsent = consentFormService.findLatestConsentOfSameTypeBeforeThisConsentWhichAreNotSavedInCDR(patient.nhsNumber, patient.hospitalNumber, consentForm,consentForm.template)
				if (latestConsent) {
					//Pass it to CDR
					def sendResult = CDR_Send_Consent(patient.nhsNumber, patient.hospitalNumber, latestConsent,latestConsent?.template)
				}
				return removeResult
			}
			//oldConsentForm formStatus was NOT NORMAL and now it is NORMAL, so send it
			else if (consentForm.getPersistentValue("formStatus") != ConsentForm.FormStatus.NORMAL &&
					 consentForm.formStatus == ConsentForm.FormStatus.NORMAL && consentForm.savedInCDR) {
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
		ConsentForm olderSavedInCDRConsent = consentFormService.findAnyConsentOfSameTypeBeforeThisConsentWhichAreSavedInCDR(patient.nhsNumber,patient.hospitalNumber,consentForm)
		if(olderSavedInCDRConsent) {
			//Remove it from CDR
			def removeResult = CDR_Remove_Consent(patient.nhsNumber, patient.hospitalNumber, olderSavedInCDRConsent, olderSavedInCDRConsent.template)
		}

		//Pass it to CDR
		def sendResult = CDR_Send_Consent(patient.nhsNumber, patient.hospitalNumber, consentForm,consentForm.template)
		return sendResult
	}

	def CDR_Remove_Consent(nhsNumber,hospitalNumber,consentForm,template){
		//Remove it from CDR
		def removeResult = connectToCDRAndRemoveConsentFrom(nhsNumber, hospitalNumber, template)
		CDRLogService.add(consentForm.id,template.id,nhsNumber,hospitalNumber,removeResult.success,removeResult.log,"remove")

		//update consent status and mention that it is not in CDR
		consentForm.savedInCDR  = false
		consentForm.passedToCDR = false
		consentForm.savedInCDRStatus = null
		consentForm.dateTimeSavedInCDR = null
		consentForm.save(flush: true, failOnError: true)
		return  removeResult
	}

	def CDR_Send_Consent(nhsNumber,hospitalNumber,consentForm,template){
		//Pass it to CDR
		def sendResult = connectToCDRAndSendConsentForm(nhsNumber, hospitalNumber, consentForm)
		CDRLogService.add(consentForm.id,template.id,nhsNumber,hospitalNumber,sendResult.success,sendResult.log,"save")

		consentForm.savedInCDR  = sendResult.success
		consentForm.passedToCDR = true
		consentForm.dateTimeSavedInCDR = new Date()
		consentForm.savedInCDRStatus = sendResult.log
		consentForm.save(flush: true, failOnError: true)
		return sendResult
	}


	def connectToCDRAndRemoveConsentFrom(String nhsNumber,String  hospitalNumber,ConsentFormTemplate consentFormTemplate) {

		def cdrKnownFacilityConfig = grailsApplication.config?.cdr?.knownFacility
		def cdrOrganisationConfig = grailsApplication.config?.cdr?.organisation

		if (!cdrKnownFacilityConfig) {
			return [success: false, log: "cdr KnownFacility Config is not defined in config file"]
		}

		if (!cdrOrganisationConfig) {
			return [success: false, log: "cdr Organisation Config is not defined in config file"]
		}

		def knownOrganisation = findKnownOrganisation(consentFormTemplate?.namePrefix)
		if (!knownOrganisation) {
			return [success: false, log: "Can not find KnownOrganisation(Consent Form Prefix name) '${consentFormTemplate?.namePrefix}' in CDR KnownOrganisations"]
		}

		def knownFacility = findKnownFacility(cdrKnownFacilityConfig?.name)
		if (!knownFacility) {
			return [success: false, log: "Can not find KnownFacility '${cdrKnownFacilityConfig?.name}' in CDR KnownFacilities"]
		}

		def consentURL = consentFormService.getAccessGUIDUrl(consentForm).toString()

		ResultModel<PatientModel> resultOfAction
		try {
			def client = getCDRClient()
			def greenlight = getCDRFacility()
			resultOfAction = client.removePatientConsent(nhsNumber, hospitalNumber, knownFacility, knownOrganisation) {
				authoringFacility greenlight
				appliesToOrganisation { id cdrOrganisationConfig?.id }
				effectiveOn consentForm.consentDate
				attachment {
					url consentURL
//					id attachmentService.getAttachmentFileName(consentForm.attachedFormImage)
//					mimeType AttachmentModel.MimeType.PNG
					description 'Greenlight Consent Form'
					sourceFacility greenlight
					// Any notes on the consent
					//notes consentForm.comment
				}
			}
		} catch (ClientException ex) {
			ex.printStackTrace()
			return [success: false, log: ex.message]
		}

		if (resultOfAction && resultOfAction?.operationSucceeded) {
			return [success: true, log: resultOfAction.conditionDetailsAsString]
		} else {
			return [success: false, log: resultOfAction.conditionDetailsAsString]
		}
	}

	def connectToCDRAndSendConsentForm(String nhsNumber,String  hospitalNumber,ConsentForm consentForm) {

		def cdrKnownFacilityConfig = grailsApplication.config?.cdr?.knownFacility
		def cdrOrganisationConfig  = grailsApplication.config?.cdr?.organisation

		if (!cdrKnownFacilityConfig) {
			return [success: false, log: "cdr KnownFacility Config is not defined in config file"]
		}

		if (!cdrOrganisationConfig) {
			return [success: false, log: "cdr Organisation Config is not defined in config file"]
		}

		def knownOrganisation = findKnownOrganisation(consentForm?.template?.namePrefix)
		if (!knownOrganisation) {
			return [success: false, log: "Can not find KnownOrganisation(Consent Form Prefix name) '${consentForm?.template?.namePrefix}' in CDR KnownOrganisations"]
		}

		def knownFacility = findKnownFacility(cdrKnownFacilityConfig?.name)
		if (!knownFacility) {
			return [success: false, log: "Can not find KnownFacility '${cdrKnownFacilityConfig?.name}' in CDR KnownFacilities"]
		}

		def knownPatientStatus = findKnownPatientStatus(consentForm.consentStatus)
		if (!knownPatientStatus) {
			return [success: false, log: "Can not find KnownPatientStatus '${consentForm.consentStatus}' in CDR KnownPatientStatus"]
		}

		//PatientGroup is actually the consentType in CDR definition
		def patientGroup = findPatientGroup(knownOrganisation,consentForm.template.cdrUniqueId)
		if (!patientGroup) {
			return [success: false, log: "Can not find consent(PatientGroup) '${consentForm.template.cdrUniqueId}' in CDR PatientGroup"]
		}
		//create a collection of patientGroups
		Collection<String> patientGroups = []
		patientGroups << patientGroup


		def consentStatusCode = "OPT-IN"
		if(consentForm.consentStatus == ConsentForm.ConsentStatus.NON_CONSENT) {
			consentStatusCode = "OPT-OUT"
		}

		def consentURL = consentFormService.getAccessGUIDUrl(consentForm).toString()

		ResultModel<PatientModel> resultOfAction
		try {
			def client = getCDRClient()
			def greenlight = getCDRFacility()
			resultOfAction = client.createOrUpdatePatientConsent(nhsNumber, hospitalNumber, knownFacility, knownOrganisation,knownPatientStatus) {
				authoringFacility greenlight
				appliesToOrganisation {id cdrOrganisationConfig?.id}
				effectiveOn consentForm.consentDate
				consentType { code consentStatusCode }
				attachment {
					description 'Greenlight Consent Form'
					sourceFacility greenlight
					//mimeType AttachmentModel.MimeType.PNG
					//id attachmentService.getAttachmentFileName(consentForm.attachedFormImage)
					// Any notes on the consent
					notes consentForm.comment
				}
			}
		} catch (ClientException ex) {
			ex.printStackTrace()
			return [success: false, log: ex.message]
		}

		if (resultOfAction && resultOfAction?.operationSucceeded) {
			return [success: true, log: resultOfAction.conditionDetailsAsString]
		} else {
			return [success: false, log: resultOfAction.conditionDetailsAsString]
		}
	}

	def getCDRClient(){
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

	private def getCDRFacility(){
		def cdrFacilityConfig  = grailsApplication.config?.cdr?.facility
		if(!cdrFacilityConfig){
			throw new Exception("cdr.facility Config is not defined in config file")
		}
		Facility greenlight = facility {
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
