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
    }

    enum Gender{
        MALE,
        FEMALE,
        OTHER
    }
}
