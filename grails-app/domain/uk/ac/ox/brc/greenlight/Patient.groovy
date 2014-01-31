package uk.ac.ox.brc.greenlight

class Patient {

    String givenName
    String familyName
    Date dateOfBirth
    String nhsNumber
    String hospitalNumber

    static hasMany = [
            consents: ConsentForm
    ]
    static constraints = {
        nhsNumber matches: '\\d{3}\\-\\d{3}-\\d{4}'
    }

}
