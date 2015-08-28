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


	public boolean equals(Object obj) {
		if (obj instanceof Patient) {
			if (givenName?.toLowerCase()  == obj?.givenName?.toLowerCase()   &&
				familyName?.toLowerCase() == obj?.familyName?.toLowerCase() &&
				dateOfBirth?.compareTo(obj?.dateOfBirth) == 0 &&
				nhsNumber == obj?.nhsNumber   &&
				hospitalNumber == obj?.hospitalNumber)
				return true
		}
		return false
	}


}
