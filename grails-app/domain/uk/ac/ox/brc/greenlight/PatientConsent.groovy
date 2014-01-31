package uk.ac.ox.brc.greenlight

import java.util.Date;
class PatientConsent {

    ConsentForm consentForm
    StudyForm studyForm

    Date consentDate
    String clinicianName


    List<PatientConsentOptIn> patientConsentOptIns


    static belongsTo = [
            patient: Patient
    ]

    static hasMany = [
            patientConsentOptIns:PatientConsentOptIn
    ]

    static constraints = {
        consentForm nullable: true //remove this later :)
        consentStatus nullable: true
    }

}