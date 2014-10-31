package uk.ac.ox.brc.greenlight
import groovy.sql.Sql
import grails.transaction.Transactional

import java.sql.Connection
import java.sql.DriverManager

@Transactional
class DemographicService {

	def grailsApplication

	/*
	 * Uses Oracle driver and connects Oracle 11.2.0 directly
	 * we downloaded ojdbc6.jar and saved in in lib directory as the library source for Oracle Connectivity
	 */
	def findPatient(nhsNumber) {

		if (!nhsNumber) {
			return null
		}

		try {
			def conString  = grailsApplication.config.epds.conString
			Connection con = DriverManager.getConnection(conString.url, conString.username, conString.password);
			def sql = new Sql(con);
			def row = sql.firstRow("select ACTIVE_MRN,GIVENNAME,FAMILYNAME,DOB,SEX from PMI.VW_COSD_STAGING_DB where NHSNUMBER=:nhsNum", [nhsNum: nhsNumber])
			//as DOB is a Timestamp, its toString() will return the actual date in string format
			//its month value is between [0-11]
			def date  =  new Date().parse("yyyy-MM-dd",row.DOB.toString())
			row.DOB = new Date(date[Calendar.YEAR],date[Calendar.MONTH],date[Calendar.DATE])
			row
			//[[ORIGINAL_MRN:XXX, ACTIVE_MRN:xxxx, NHSNUMBER:9446457610, TITLE:MR, GIVENNAME:ABC, FAMILYNAME:AA, SEX:1, DOB:2010-05-17 00:00:00.0, DOD:null, ADDRESS_LINE1:ABCBCBCBC, ADDRESS_LINE3:AAAA, ADDRESS_LINE4:OXFORD, ADDRESS_LINE5:null, POSTCODE:OX3Q, ETHNICGROUP:C, GP_GMPCODE:G8703318, GP_PRACTICE_CODE:K84615]]
		}
		catch(Exception ex){
			log.error(ex)
			return null
		}
	}
}