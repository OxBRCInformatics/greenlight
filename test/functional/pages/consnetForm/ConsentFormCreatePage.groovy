package pages.consnetForm
import geb.Page


class ConsentFormCreatePage extends Page{
    static url = "consentForm/create"

    static at = {
        url == "consentForm/create" &&
                title == "Consent Forms Upload"
    }
    static content = {
        uploadButton{$("button[id='btnUpload'][type='submit']")}
        fileSelectorButton{$("input[id='scannedForm']")}
    }
}