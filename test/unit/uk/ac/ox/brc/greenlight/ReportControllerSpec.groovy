package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by soheil on 01/06/2015.
 */
@TestFor(ReportController)
class ReportControllerSpec extends Specification {


	def setup() {
		controller.databaseCleanupService = Mock(DatabaseCleanupService)
	}

	void "dataCleansingReports returns database status reports"() {

		given:
		def reports = [
				"consentCount": 2,
				"patientCount": 3
		]
		when:
		controller.databaseStatusReports()

		then:
		1 * controller.databaseCleanupService.databaseStatusReports() >> { return reports }
		controller.modelAndView.model.dbReport == reports
		controller.modelAndView.viewName == "/report/databaseStatusReports"
	}
}