package uk.ac.ox.brc.greenlight.auth

import org.apache.commons.lang.builder.HashCodeBuilder
import org.codehaus.groovy.grails.plugins.orm.auditable.Stamp


@Stamp
class UserRole implements Serializable {

	AppUser appUser
	AppRole appRole

	static auditable =  [ignore:['version','lastUpdated','lastUpdatedBy','createdBy','dateCreated']]

	boolean equals(other) {
		if (!(other instanceof UserRole)) {
			return false
		}

		other.appUser?.id == appUser?.id &&
			other.appRole?.id == appRole?.id
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (appUser) builder.append(appUser.id)
		if (appRole) builder.append(appRole.id)
		builder.toHashCode()
	}

	static UserRole get(long userId, long roleId) {
		find 'from UserRole where appUser.id=:userId and appRole.id=:roleId',
			[userId: userId, roleId: roleId]
	}

	static UserRole create(AppUser user, AppRole role, boolean flush = false) {
		new UserRole(appUser: user, appRole: role).save(flush: flush, insert: true)
	}

	static boolean remove(AppUser user, AppRole role, boolean flush = false) {
		UserRole instance = UserRole.findByAppUserAndAppRole(user, role)
		if (!instance) {
			return false
		}

		instance.delete(flush: flush)
		true
	}

	static void removeAll(AppUser user) {
		executeUpdate 'DELETE FROM UserRole WHERE appUser=:user', [user: user]
	}

	static void removeAll(AppRole role) {
		executeUpdate 'DELETE FROM UserRole WHERE appRole=:role', [role: role]
	}

	static mapping = {
		id composite: ['appRole', 'appUser']
		version false
	}
}
