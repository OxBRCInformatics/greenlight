package specs.consentForm

import geb.spock.GebReportingSpec
import org.openqa.selenium.Dimension
import pages.consnetForm.ConsentFormCreatePage
import pages.consnetForm.ConsentFormUploadPage
import pages.consnetForm.ConsnetFormListPage

/**
 * Created by soheil on 28/01/14.
 */
class ConsentFormCreateSpec extends GebReportingSpec {


    def "Check if upload button is shown properly"()
    {
        when:"In consent form create page"
        to ConsentFormCreatePage


        then:"uploaded button and file selector are both displayed"
        uploadButton.displayed
        //fileSelectButton.displayed
    }

//    def "Check if no file is selected and upload is clicked, file table is empty"()
//    {
//        when: "In consent Form create page and click on upload"
//        to ConsentFormCreatePage
//        uploadButton.click()
//
//        then:"Should be in Upload page with empty file table"
//        at ConsentFormUploadPage
//        !uploadedFilesTable.present
//    }
//
//
//    def "Check if a non-Image file is selected for upload, it does not upload the file"()
//    {
//     when:"In consent form create and select a non-image file to upload"
//        to ConsentFormCreatePage
//        //fileSelectorButton="c:\\1.png"
//        uploadButton.click()
//
//      then: "Should be in Upload page and the file be displayed, by correct name"
//        at ConsentFormUploadPage
//        fileSelectorButton.displayed
//        uploadButton.displayed
//    }
}