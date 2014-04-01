package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional


class ConsentFormService {

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
        def result=""
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
                    ];
        headers.each { header->
            result = result + header + ",";
        }
        result = result + "\r\n"

        def consents= ConsentForm.list()
        consents.each { consent ->
            result += consent.id.toString() + ","
            result += consent.consentDate.format("dd-MM-yyyy") + ","
            result += consent.formID.toString() + ","
            result = result + consent.consentTakerName.toString() + ","
            result = result + consent.formStatus.toString() + ","
            result = result + consent.patient.nhsNumber.toString() + ","
            result = result + consent.patient.hospitalNumber.toString() + ","
            result = result + consent.patient.givenName.toString() + ","
            result = result + consent.patient.familyName.toString() + ","
            result = result + consent.patient.dateOfBirth.format("dd-MM-yyyy") + ","
            result = result + consent.template.namePrefix.toString() + ","
            result = result + " " + ","

            def resString = ""
            consent.responses.each { response->
                resString += response.answer.toString() +"|"
            }
            result = result + resString + ","
            result = result + "\r\n"
        }
        return result
    }

}