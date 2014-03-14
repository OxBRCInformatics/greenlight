package spec

import geb.spock.GebReportingSpec
import org.openqa.selenium.Dimension
import page.AttachmentUploadPage
import page.DashboardPage
import page.authentication.LoginPage

/**
 * Created by soheil on 28/01/14.
 */
class AttachmentCreateSpec extends GebReportingSpec {

//
//    def setup()
//    {
//        to LoginPage
//        username = "admin"
//        password = "password"
//        submitButton.click()
//        //at DashboardPage
//    }
//
//
//    def "Check if upload button is shown properly"()
//    {
//        when:"In attachment page"
//        to AttachmentUploadPage
//
//
//        then:"uploaded button and file selector are both displayed"
//        uploadButton.displayed
//        fileSelectorButton.displayed
//    }

//    def "Check if no file is selected and upload is clicked, file table is still empty"()
//    {
//        when: "In consent Form create page and click on upload"
//        to AttachmentUploadPage
//        uploadButton.click()
//
//        then:"Should be in Upload page with empty file table"
//        at AttachmentUploadPage
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