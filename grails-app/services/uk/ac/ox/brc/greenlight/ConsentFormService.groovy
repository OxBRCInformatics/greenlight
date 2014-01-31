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

}
