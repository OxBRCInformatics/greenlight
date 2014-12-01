package uk.ac.ox.brc.greenlight.auth

class AppRole {

	String authority

	static mapping = {
//		cache true
	}

	static constraints = {
		authority blank: false, unique: true
	}
}
