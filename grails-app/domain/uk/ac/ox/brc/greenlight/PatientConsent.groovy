package uk.ac.ox.brc.greenlight

import java.util.Date;
class PatientConsent {

    ConsentForm consentForm
    ConsentStatus consentStatus
    Date consentDate
    String clinicianName

    List<Boolean> answers
    List<String> questions


    static belongsTo = [
            patient: Patient
    ]

    static hasMany = [
            answers:Boolean,
            questions:String
    ]

    static constraints = {
        consentForm nullable: true //remove this later :)
        consentStatus nullable: true
    }

    enum ConsentStatus {
        UNKOWN("Unknown"), ALL_CONSENTED("All Consented"),NOT_CONSENTED("Non Consented");
        final String value;
        ConsentStatus(String value) { this.value = value; }
        String toString() { value; }
        String getKey() { name(); }
    }


}