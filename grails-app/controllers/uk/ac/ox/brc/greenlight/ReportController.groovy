package uk.ac.ox.brc.greenlight

class ReportController {

	def databaseCleanupService

	def databaseStatusReports(){
		def  dbReport
		try {
			dbReport = databaseCleanupService.databaseStatusReports()
		}
		catch (Exception exception) {
			render exception.message
			return
		}
 		render view:"databaseStatusReports", model:[dbReport:dbReport]
	}
}