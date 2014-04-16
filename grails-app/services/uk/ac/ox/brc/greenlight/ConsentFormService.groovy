package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional


class ConsentFormService {

    def consentEvaluationService
	/**
	 * Get the latest consent form for each type for a specific patient.
	 * Returns an empty list if no consent forms have been entered.
	 * @param A list of consent forms containing the latest for each ConsentFormTemplate
	 */
	Collection getLatestConsentForms(Patient patient){

		// Store as a map of ConsentFormTemplate:ConsentForm pairs
		Map<ConsentFormTemplate, ConsentForm> latestTests = [:]

		// Find the max date for each form template
		patient.consents.each{ consent ->
			// Only update the map if the key doesn't exist or the new value is newer than the old value
			if(!latestTests.containsKey(consent.template) || consent.consentDate > latestTests[consent.template].consentDate ){
				latestTests[consent.template] = consent
			}
		}
		return latestTests.values() as List
	}

    def search(params)
    {
        def nhsNumber = params["nhsNumber"];
        def hospitalNumber = params["hospitalNumber"];
        def consentTakerName = params["consentTakerName"];

        def consentDateFrom = params["consentDateFrom"];
        def consentDateTo = params["consentDateTo"];


        def formIdFrom = (params["formIdFrom"]).trim();
        def formIdTo = (params["formIdTo"]).trim();


        def criteria = ConsentForm.createCriteria()
        def results = criteria.list {
            if(consentDateFrom && consentDateTo){
                if(consentDateFrom.compareTo(consentDateTo)<=0)
                    between('consentDate', consentDateFrom, consentDateTo)
            }


            if(formIdFrom.size()>0 && formIdTo.size()>0){
                if(formIdFrom.compareTo(formIdTo)<=0)
                    between('formID', formIdFrom, formIdTo)
            }

            if(consentTakerName && consentTakerName.size()>0) {
                like('consentTakerName',consentTakerName+"%")
            }
            patient
                    {
                        if(hospitalNumber && hospitalNumber.size()>0){like("hospitalNumber", hospitalNumber+"%")}
                        if(nhsNumber && nhsNumber.size()>0){like("nhsNumber", nhsNumber+"%")}
                    }
            order("consentDate", "desc")
        }
        return results;
    }

	@Transactional
    def save(Patient patient,ConsentForm consentForm) {
        try
        {

            patient.save()
            consentForm.save(flush: true)
            return true
        }
        catch(Exception ex)
        {
            return false
        }
    }


	@Transactional
    def delete(ConsentForm consentForm) {
        try
        {
            consentForm.delete(flush: true)
            return true
        }
        catch(Exception ex)
        {
            return false
        }
    }

    def checkConsent(params)
    {
        def searchInput = params["searchInput"];

        def result=[
            consentForm:null,
            consented:false
        ]

        if(!searchInput)
            return  result;

        def consent = ConsentForm.find("from ConsentForm as c where c.patient.hospitalNumber= :searchInput or c.patient.nhsNumber= :searchInput",[searchInput:searchInput]);
        if(consent){
            result.consentForm=consent
            result.consented=true

            consent.responses.eachWithIndex { value ,i ->
                    if(value.answer!= Response.ResponseValue.YES)
                        result.consented= false
            }

        }
        return result;
    }

    def getConsentFormByFormId(formId)
    {
        if(formId.endsWith("00000"))
            return  -1;

        def consent = ConsentForm.find("from ConsentForm as c where c.formID = :formId",[formId:formId]);
        if(consent){
            return consent.id
        }
        return -1;
    }


    def exportToCSV()
    {
        StringBuilder sb = new StringBuilder()
        def headers =[
                    "consentId",
                    "consentDate",
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

        def consents= ConsentForm.list()
        consents.each { consent ->
           sb.append([
                consent.id.toString(),
                consent.consentDate.format("dd-MM-yyyy"),
                consent.formID.toString(),
                consent.consentTakerName,
                consent.formStatus.toString(),
                consent.patient.nhsNumber,
                consent.patient.hospitalNumber,
                consent.patient.givenName,
                consent.patient.familyName,
                consent.patient.dateOfBirth.format("dd-MM-yyyy"),
                consent.template.namePrefix,
                consentEvaluationService.getConsentStatus(consent).toString(),
                consent.responses.collect { it.answer.toString()}.join("|"),
                escapeForCSV(consent.comment)
            ].join(',') )
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
        String comment = unEscapedComment.replaceAll("\n","\t")
        comment = comment.replaceAll(escapedDblQuote, escapedDblQuote + escapedDblQuote)
        comment = escapedDblQuote + comment + escapedDblQuote

        return  comment
    }
}
