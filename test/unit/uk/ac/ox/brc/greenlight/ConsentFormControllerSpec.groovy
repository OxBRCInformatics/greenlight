package uk.ac.ox.brc.greenlight

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by soheil on 01/04/2014.
 */
@TestFor(ConsentFormController)
class ConsentFormControllerSpec extends Specification{

    def setup()
    {
        controller.consentFormService = Mock(ConsentFormService)


    }

    def "Calling export action will return csv file"()
    {
        when:"calling export action"
        controller.export()

        then:"consentFormService exportToCSV method should be called once"
        1 * controller.consentFormService.exportToCSV() >>{ return "header1,header2 \r\n data1,data2" }


        then:"it returns a csv contentType"
        controller.response.contentType == "text/csv;charset=utf-8"
    }

    def "Calling export action will return csv file with a correct Name"()
    {
        when:"calling export action"
        def fileName ="consentForms-"+(new Date()).format("dd-MM-yyyy")
        controller.export()

        then:"consentFormService exportToCSV method should be called once"
        1 * controller.consentFormService.exportToCSV() >>{ return "header1,header2 \r\n data1,data2" }


        then:"it returns a csv file with a correct name"
        controller.response.header("Content-disposition") == "attachment; filename=${fileName}.csv"
    }
}