package pages.consnetForm
import geb.Page


class ConsentFormCreatePage extends Page{
    static url = "attachedFormImage/create"

    static at = {
        url == "attachedFormImage/create" &&
                title == "Consent Forms Upload"
    }
    static content = {
        uploadButton{$("button[id='btnUpload'][type='submit']")}
        fileSelectorButton{$("input[id='scannedForm']")}
    }
}