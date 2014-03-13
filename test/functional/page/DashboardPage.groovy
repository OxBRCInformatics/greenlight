package page

import geb.Page

/**
 * Created by soheil on 13/03/2014.
 */
class DashboardPage extends Page{
    static url = "/"

    static at = {
        url == "/" &&
                title == "ORB Consent Form"
    }

}