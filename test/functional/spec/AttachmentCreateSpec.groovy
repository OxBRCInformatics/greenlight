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


    def setup()
    {
        to LoginPage
        username = "admin"
        password = "password"
        submitButton.click()
        at DashboardPage
    }


    def "Check if upload button is shown properly"()
    {
        when:"In attachment page"
        to AttachmentUploadPage


        then:"uploaded button and file selector are both displayed"
        at AttachmentUploadPage
        uploadButton.displayed
        fileSelectorButton.displayed
    }

}