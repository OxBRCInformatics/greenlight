package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional




@Transactional
class ConsentFormService {



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

    def buildORBConsent()
    {
        def consentForm = new ConsentForm();
        consentForm.responses=new ArrayList<Response>();
        for(i in 0..9)
            consentForm.responses.add(false)

        consentForm.responses=new ArrayList<String>();
        consentForm.responses.add("I have read and understand that patient information sheet(green v1.2 dated 3rd March 2009). My question have bee answered satisfactorily...")
        consentForm.responses.add("I agree to give a sample  of blood and/or other tissue for research.")
        consentForm.responses.add("I agree that further blood and/or tissue samples may be taken, that participation is voluntary and that I....")
        consentForm.responses.add("I understand how the sample will be taken, that participation ...")
        consentForm.responses.add("I understand how the sample will be taken, that participation ...")
        consentForm.responses.add("I understand how the sample will be taken, that participation ...")
        consentForm.responses.add("I understand how the sample will be taken, that participation ...")
        consentForm.responses.add("I understand how the sample will be taken, that participation ...")
        consentForm.responses.add("I understand how the sample will be taken, that participation ...")
        consentForm.responses.add("I understand how the sample will be taken, that participation ...")

        return consentForm;
    }

}
