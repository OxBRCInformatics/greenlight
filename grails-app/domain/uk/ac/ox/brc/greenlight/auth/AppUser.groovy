package uk.ac.ox.brc.greenlight.auth
import org.codehaus.groovy.grails.plugins.orm.auditable.Stamp

@Stamp
class AppUser {

	transient springSecurityService

	String username
	String password
	boolean enabled
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired


	static auditable =  [ignore:['version','lastUpdated','lastUpdatedBy','createdBy','dateCreated']]


	static constraints = {
		username blank: false, unique: true
		password blank: false
	}

	static mapping = {
		password column: '`password`'
	}

	Set<AppRole> getAuthorities() {
		UserRole.findAllByAppUser(this).collect { it.appRole } as Set
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService.encodePassword(password)
	}
}
