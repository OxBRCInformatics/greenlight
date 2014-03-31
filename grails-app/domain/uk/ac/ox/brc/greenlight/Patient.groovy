package uk.ac.ox.brc.greenlight

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
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
        nhsNumber nullable: true, matches: '\\d{10}'
        givenName nullable: true
        familyName nullable: true
        dateOfBirth nullable: true
        hospitalNumber nullable: true
		consents nullable: true
    }

}
