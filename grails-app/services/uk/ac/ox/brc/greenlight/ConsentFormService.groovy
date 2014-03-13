package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional




@Transactional
class ConsentFormService {

    def search(params)
    {
        def nhsNumber = params["nhsNumber"];
        def hospitalNumber = params["hospitalNumber"];
        def consentTakerName = params["consentTakerName"];

        def consentDateFromStr = params["consentDateFrom"];
        def consentDateToStr = params["consentDateTo"];

        def consentDateFrom=null
        if(consentDateFromStr && consentDateFromStr.size()>0)
        {
            try
            {
                consentDateFrom= new Date().parse("dd/mm/yyyy",consentDateFromStr)
            }
            catch (Exception ex){}
        }



        def consentDateTo =null
        if (consentDateToStr && consentDateToStr.size()>0)
        {
            try{
                consentDateTo = new Date().parse("dd/mm/yyyy",consentDateToStr)
            }
            catch(Exception ex){}
        }



        def criteria = ConsentForm.createCriteria()
        def results = criteria.list {
            if(consentDateFrom && consentDateTo){between('consentDate', consentDateFrom, consentDateTo)}
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
