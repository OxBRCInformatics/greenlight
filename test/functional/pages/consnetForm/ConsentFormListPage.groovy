package pages.consnetForm
import geb.Page



class ConsnetFormListPage extends Page {
    static url = "attachedFormImage/list"

    static at = {
        url == "attachedFormImage/list" &&
                title == "Consent Forms"
    }
    static content = {
        fileSelectorButton{$("input[id='scannedForm']")}
        addFormsButton{$("button[id='btnAddForms'][type='button']")}
    }
}