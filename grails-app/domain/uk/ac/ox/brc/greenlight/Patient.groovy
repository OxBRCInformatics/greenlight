package uk.ac.ox.brc.greenlight

import groovy.transform.EqualsAndHashCode
import org.codehaus.groovy.grails.plugins.orm.auditable.Stamp

@EqualsAndHashCode
@Stamp
class Patient {

    String givenName
    String familyName
    Date dateOfBirth
    String nhsNumber
    String hospitalNumber

	static auditable =  [ignore:['version','lastUpdated','lastUpdatedBy','createdBy','dateCreated']]

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
