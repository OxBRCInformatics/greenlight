package page

import geb.Page

/**
 * Created by soheil on 28/01/14.
 */
class AttachmentUploadPage extends Page{

    static url = "attachment/upload"

    static at = {
        url == "attachment/upload" &&
                title == "Consent Forms Upload"
    }
    static content = {

        uploadButton{$("button[id='btnUpload'][type='submit']")}
        fileSelectorButton{$("input[id='scannedForm']")}
        uploadedFilesTable(required: false) {$("table[id='uploadedFilesTable']")}
    }
}
