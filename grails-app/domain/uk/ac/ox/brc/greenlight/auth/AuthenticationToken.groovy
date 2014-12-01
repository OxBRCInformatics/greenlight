package uk.ac.ox.brc.greenlight.auth

class AuthenticationToken {
	String tokenValue
	String username

	//dateCreated & lastUpdated will be save automatically by GORM
	//http://grails.org/doc/latest/ref/Database%20Mapping/autoTimestamp.html
	Date dateCreated
	Date lastTimeUpdated
}
