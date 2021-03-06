package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional
import uk.ac.ox.brc.greenlight.ConsentForm.ConsentStatus


class ConsentFormService {

	def consentEvaluationService
	def patientService
	def consentFormService

	/**
	 * Get the latest consent form for these patient objects.
	 * They should actually be several patient objects with the same NHS or hospital number
	 * @param patients
	 */
	Collection getLatestConsentForms(patients) {

		// Store as a map of ConsentFormTemplate:ConsentForm pairs
		Map<ConsentFormTemplate, ConsentForm> latestTests = [:]

		patients.each { patient ->
			// Find the max date for each form template
			patient.consents.each { consent ->
				// Only if the formStatus is NORMAL
				if(consent?.formStatus == ConsentForm.FormStatus.NORMAL) {
					// Only update the map if the key doesn't exist or the new value is newer than the old value
					if (!latestTests.containsKey(consent.template) || consent.consentDate > latestTests[consent.template].consentDate) {
						latestTests[consent.template] = consent
					}
				}
			}
		}
		return latestTests.values() as List
	}


	def search(params) {
		def nhsNumber = params["nhsNumber"];
		def hospitalNumber = params["hospitalNumber"];
		def consentTakerName = params["consentTakerName"];

		def consentDateFrom = params["consentDateFrom"];
		def consentDateTo = params["consentDateTo"];


		def formIdFrom = params["formIdFrom"]?.trim();
		def formIdTo = params["formIdTo"]?.trim();

		def comment = params["comment"]?.trim();

		def criteria = ConsentForm.createCriteria()
		def results = criteria.list {
			if (consentDateFrom && consentDateTo) {
				if (consentDateFrom.compareTo(consentDateTo) <= 0)
					between('consentDate', consentDateFrom, consentDateTo)
			}


			if (formIdFrom && formIdTo && formIdFrom.size() > 0 && formIdTo.size() > 0) {
				if (formIdFrom.compareTo(formIdTo) <= 0)
					between('formID', formIdFrom, formIdTo)
			}

			if (consentTakerName && consentTakerName.size() > 0) {
				like('consentTakerName', consentTakerName + "%")
			}
			patient
					{
						if (hospitalNumber && hospitalNumber.size() > 0) {
							like("hospitalNumber", hospitalNumber + "%")
						}
						if (nhsNumber && nhsNumber.size() > 0) {
							like("nhsNumber", nhsNumber + "%")
						}
					}
			if (comment && comment.size()>0){
				ilike("comment","%${comment}%")
			}
			order("consentDate", "desc")
		}
		return results;
	}

	@Transactional
	def save(Patient patient, ConsentForm consentForm) {
		try {

			patient.save()

			//calculate and save consentStatus
			def status = consentEvaluationService.getConsentStatus(consentForm)
			consentForm.consentStatus = status

			consentForm.save(flush: true)
			return true
		}
		catch (Exception ex) {
			return false
		}
	}


	@Transactional
	def delete(ConsentForm consentForm) {
		try {
			consentForm.delete(flush: true)
			return true
		}
		catch (Exception ex) {
			return false
		}
	}


	def getConsentFormByFormId(formId) {
		// FormId which ends to 00000 is general and
		//we can have more that one int
		if (formId.endsWith("00000"))
			return -1;

		def consent = ConsentForm.find("from ConsentForm as c where c.formID = :formId", [formId: formId]);
		if (consent) {
			return consent.id
		}
		return -1;
	}


	def findAndExport(params) {
		def searchResult = search(params)
		exportConsentObjectsListToCSV(searchResult)
	}
	

	def exportAllConsentFormsToCSV() {
		def allConsents = ConsentForm.list()
		exportConsentObjectsListToCSV(allConsents)
	}

	
	def exportConsentObjectsListToCSV(consentForms){
		StringBuilder sb = new StringBuilder()
		def headers = [
				"consentId",
				"consentDate",
				"uploadDate",
				"consentformID",
				"consentTakerName",
				"formStatus",
				"patientNHS",
				"patientMRN",
				"patientName",
				"patientSurName",
				"patientDateOfBirth",
				"templateName",
				"consentResult",
				"responses",
				"comments"
		];
		sb.append(headers.join(','))
		sb.append("\n")

		consentForms?.each { consent ->
			sb.append([
					consent.id as String,
					consent.consentDate.format("dd-MM-yyyy"),
					consent?.attachedFormImage?.dateOfUpload?.format("dd-MM-yyyy"),
					consent.formID as String,
					(consent.consentTakerName ? consent.consentTakerName : ""),
					consent.formStatus as String,
					(consent?.patient.nhsNumber ? consent?.patient.nhsNumber : ""),
					(consent?.patient.hospitalNumber ? consent?.patient.hospitalNumber : ""),
					(consent?.patient.givenName ? consent?.patient.givenName : ""),
					(consent?.patient.familyName ? consent?.patient.familyName :""),
					consent?.patient.dateOfBirth.format("dd-MM-yyyy"),
					consent?.template?.namePrefix,
					consent?.consentStatus as String,
					consent?.responses?.collect { it.answer as String }.join("|"),
					escapeForCSV(consent.comment)
			].join(','))
			sb.append("\n")
		}
		return sb.toString()
	}

	/**
	 * Escapes a String for CSV output, following the guidelines of http://en.wikipedia.org/wiki/Comma-separated_values#Basic_rules_and_examples,
	 * specifically:
	 *
	 *  * Double quotes are doubled
	 *  * Everything is enclosed in double quotes to allow use of commas, newlines ,etc.
	 *
	 * @param unEscapedComment A String containing anything
	 * @return the escaped String
	 */
	String escapeForCSV(String unEscapedComment) {

		String escapedDblQuote = "\""
		String comment = unEscapedComment

		//in case of a null value return no-value string
		if (!comment)
			comment = ""

		comment = comment.replaceAll("\n", "\t")
		comment = comment.replaceAll(escapedDblQuote, escapedDblQuote + escapedDblQuote)
		comment = escapedDblQuote + comment + escapedDblQuote

		return comment
	}

	def getPatientWithMoreThanOneConsentForm() {

		def patientHospitalNumbers = patientService.groupPatientsByHospitalNumber()
		def finalResult = []

		patientHospitalNumbers.each { def hospitalNumber ->

			//if hospitalNumber is not null or empty
			if(hospitalNumber?.trim()) {
				// Attempt to find all patient objects having this hospitalNumber
				def patients = patientService.findAllByNHSOrHospitalNumber(hospitalNumber)
				//Find all consent objects related to this patient (these patient objects)
				def consents = consentFormService.getLatestConsentForms(patients)

				//if it has equal/more than 2 consents,
				//so there might be the possibility that there are more than 2 FULL_CONSENTED forms
				if (consents?.size() >= 2) {

					def fullConsentedCount = 0
					def consentsString = ""
					//for each consent, get its status
					consents.each { consentForm ->

						//if it is fully consented, add it into the list
						if (consentForm?.consentStatus == ConsentStatus.FULL_CONSENT) {
							fullConsentedCount++
							if (!consentsString.isEmpty())
								consentsString += "|"
							consentsString += "${consentForm?.template?.namePrefix}[${consentForm?.consentDate?.format("dd-MM-yyyy")};${consentForm?.formID}]"
						}
					}
					//if count is more than / equal two
					if (fullConsentedCount >= 2) {
						def patient = [
								nhsNumber     : patients[0]?.nhsNumber,
								hospitalNumber: patients[0]?.hospitalNumber,
								givenName     : patients[0]?.givenName,
								familyName    : patients[0]?.familyName,
								dateOfBirth   : patients[0]?.dateOfBirth,
								consentsString: consentsString,
								consentsCount:  fullConsentedCount
						]
						finalResult.add(patient)
					}
				}
			}
		}
		finalResult
	}


	def exportPatientWithMoreThanOneConsentForm() {
		StringBuilder sb = new StringBuilder()
		def headers = [
				"patientNHS",
				"patientMRN",
				"patientFirstName",
				"patientLastName",
				"patientDateOfBirth",
 				"consentForms",
				"consentFormsCount"
		];
		sb.append(headers.join(','))
		sb.append("\n")

		def patients = getPatientWithMoreThanOneConsentForm()
		patients.each { patient ->
			sb.append([
					 patient?.nhsNumber,
					(patient?.hospitalNumber?.trim() ?  patient.hospitalNumber : ""),
					(patient?.givenName?.trim() ?  patient.givenName : ""),
					(patient?.familyName?.trim() ?  patient.familyName : ""),
					 patient?.dateOfBirth.format("dd-MM-yyyy"),
					 patient?.consentsString,
					 patient?.consentsCount
			].join(','))
			sb.append("\n")
		}
		return sb.toString()
	}
}
