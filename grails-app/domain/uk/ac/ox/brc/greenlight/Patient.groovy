package uk.ac.ox.brc.greenlight

class Patient {

    String givenName
    String familyName
    Date dateOfBirth
    String nhsNumber
    String hospitalNumber

    // static auditable = true

    static hasMany = [
            consents: ConsentForm
    ]
    static constraints = {
        nhsNumber nullable: true, matches: '\\d{3}\\-\\d{3}-\\d{4}'
        givenName nullable: true
        familyName nullable: true
        dateOfBirth nullable: true
        hospitalNumber nullable: true
    }

}
