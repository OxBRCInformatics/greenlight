package page
import geb.Page



class AttachmentListPage extends Page {
    static url = "attachment/list"

    static at = {
        url == "attachment/list" &&
        title == "Consent Forms"
    }

    static content = {

        addFormButton{$("a[href='/attachment/create']")}
    }
}