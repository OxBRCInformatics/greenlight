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

        def criteria = ConsentForm.createCriteria()
        def results = criteria.list {
            if(consentDateFrom && consentDateTo){
                if(consentDateFrom.compareTo(consentDateTo)<0)
                    between('consentDate', consentDateFrom, consentDateTo)
            }
            if(consentTakerName && consentTakerName.size()>0) { like('consentTakerName',consentTakerName+"%")}
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

}
