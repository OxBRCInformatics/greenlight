package uk.ac.ox.brc.greenlight
import groovy.sql.Sql
import grails.transaction.Transactional

import java.sql.Connection
import java.sql.DriverManager

@Transactional
class DemographicService {

    def grailsApplication

    def findPatient(nhsNumber) {

        if(!nhsNumber){
            return null
        }

        def conString = grailsApplication.config.epds.conString
        def sql = Sql.newInstance(conString.url, conString.username, conString.password, 'sun.jdbc.odbc.JdbcOdbcDriver')
        sql.firstRow("select ACTIVE_MRN,GIVENNAME,FAMILYNAME,DOB,SEX from PMI.VW_COSD_STAGING_DB where NHSNUMBER=:nhsNum",[nhsNum:nhsNumber])
         //[[ORIGINAL_MRN:XXX, ACTIVE_MRN:xxxx, NHSNUMBER:9446457610, TITLE:MR, GIVENNAME:ABC, FAMILYNAME:AA, SEX:1, DOB:2010-05-17 00:00:00.0, DOD:null, ADDRESS_LINE1:ABCBCBCBC, ADDRESS_LINE3:AAAA, ADDRESS_LINE4:OXFORD, ADDRESS_LINE5:null, POSTCODE:OX3Q, ETHNICGROUP:C, GP_GMPCODE:G8703318, GP_PRACTICE_CODE:K84615]]
    }


	/*
	we can check if ODBC driver is supported
	http://stackoverflow.com/questions/14229072/removal-of-jdbc-odbc-bridge-in-java-8
 	*/
	def checkODBC(){
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
	}


	def checkOracle(nhsNumber){
		Connection conn = DriverManager.getConnection('jdbc:oracle:thin:@oxnetepdsprod02.oxnet.nhs.uk:1521:EPDSDEV',"username","password");
		Sql sql = new Sql(conn);
		sql.firstRow("select ACTIVE_MRN,GIVENNAME,FAMILYNAME,DOB,SEX from PMI.VW_COSD_STAGING_DB where NHSNUMBER=:nhsNum",[nhsNum:nhsNumber])
	}
}