package pages.consnetForm
import geb.Page



class ConsnetFormListPage extends Page {
    static url = "consentForm/list"

    static at = {
        url == "consentForm/list" &&
                title == "Consent Forms"
    }
    static content = {
        fileSelectorButton{$("input[id='scannedForm']")}
        addFormsButton{$("button[id='btnAddForms'][type='button']")}
    }
}