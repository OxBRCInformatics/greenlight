package uk.ac.ox.brc.greenlight

class Patient {

    String givenName
    String familyName
    Gender gender
    Date dateOfBirth

    String nhsNumber
    String hospitalNumber

    static hasMany = [
            consents: PatientConsent,
            labSamples: LabSample
    ]
    static constraints = {
        nhsNumber matches: '\\d{3}\\-\\d{3}-\\d{4}'
    }

     enum Gender {
        MALE("Male"), FEMALE("Female"),OTHER("Other");
        final String value;
        Gender(String value) { this.value = value; }
        String toString() { value; }
        String getKey() { name(); }
    }

}
