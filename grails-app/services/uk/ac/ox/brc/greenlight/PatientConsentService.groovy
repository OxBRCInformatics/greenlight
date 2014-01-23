package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional




@Transactional
class PatientConsentService {



    def save(Patient patient,PatientConsent patientConsent) {
        try
        {
            patient.save()
            patientConsent.save(flush: true)
            return true
        }
        catch(Exception ex)
        {
            return false
        }
    }



    def delete(PatientConsent patientConsent) {
        try
        {
            patientConsent.delete(flush: true)
            return true
        }
        catch(Exception ex)
        {
            return false
        }
    }

    def buildORBConsent()
    {
        def patientConsent = new PatientConsent();
        patientConsent.answers=new ArrayList<Boolean>();
        for(i in 0..9)
            patientConsent.answers.add(false)

        patientConsent.questions=new ArrayList<String>();
        patientConsent.questions.add("I have read and understand that patient information sheet(green v1.2 dated 3rd March 2009). My question have bee answered satisfactorily...")
        patientConsent.questions.add("I agree to give a sample  of blood and/or other tissue for research.")
        patientConsent.questions.add("I agree that further blood and/or tissue samples may be taken, that participation is voluntary and that I....")
        patientConsent.questions.add("I understand how the sample will be taken, that participation ...")
        patientConsent.questions.add("I understand how the sample will be taken, that participation ...")
        patientConsent.questions.add("I understand how the sample will be taken, that participation ...")
        patientConsent.questions.add("I understand how the sample will be taken, that participation ...")
        patientConsent.questions.add("I understand how the sample will be taken, that participation ...")
        patientConsent.questions.add("I understand how the sample will be taken, that participation ...")
        patientConsent.questions.add("I understand how the sample will be taken, that participation ...")

        return patientConsent;
    }

}
