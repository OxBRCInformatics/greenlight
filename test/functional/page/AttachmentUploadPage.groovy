package page

import geb.Page

/**
 * Created by soheil on 28/01/14.
 */
class AttachmentUploadPage extends Page{

    static url = "attachment/create"

    static at = {
        url == "attachment/create" &&
                title == "Consent Forms Upload"
    }
    static content = {

        uploadButton{$("button[id='btnUpload'][type='submit']")}
        fileSelectorButton{$("input[id='scannedForms']")}
    }
}
