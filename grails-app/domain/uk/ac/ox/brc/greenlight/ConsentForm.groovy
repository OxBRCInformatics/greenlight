package uk.ac.ox.brc.greenlight

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class ConsentForm {

    Attachment attachedFormImage
    ConsentFormTemplate template

    Date consentDate
    String consentTakerName
    String formID
    FormStatus formStatus = FormStatus.NORMAL

    List<Response> responses
    String comment

    //static auditable = true

    static belongsTo = [
            patient: Patient
    ]

    static hasMany = [
            responses:Response
    ]

   static mapping = {
        responses cascade: 'all-delete-orphan'
   }

    static constraints = {
        attachedFormImage nullable: true //remove this later :)
        formID matches: '[a-zA-Z]{3}\\d{5}'
        template nullable: true
        consentDate nullable: true
        consentTakerName nullable: true
        patient nullable: true
        comment nullable: true
    }

    enum FormStatus {
        NORMAL("Normal"),SPOILED("Spoiled"), DECLINED("Declined");
        final String value;
        FormStatus(String value) { this.value = value; }
        String toString() { value; }
        String getKey() { name(); }
    }


}