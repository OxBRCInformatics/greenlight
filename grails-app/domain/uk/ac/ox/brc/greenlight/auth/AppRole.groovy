package uk.ac.ox.brc.greenlight.auth
import org.codehaus.groovy.grails.plugins.orm.auditable.Stamp

@Stamp
class AppRole {

	String authority

	static auditable =  [ignore:['version','lastUpdated','lastUpdatedBy','createdBy','dateCreated']]


	static mapping = {
//		cache true
	}

	static constraints = {
		authority blank: false, unique: true
	}
}
