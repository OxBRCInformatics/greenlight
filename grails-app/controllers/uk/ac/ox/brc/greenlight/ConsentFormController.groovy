package uk.ac.ox.brc.greenlight

class ConsentFormController {

    def consentFormService

    def find()
    {
        def result= consentFormService.search(params);
        render view:"search", model:[consentForms:result]
    }



    def checkConsent()
    {
        def result= consentFormService.checkConsent(params);
        render view:"cuttingRoom", model:[result:result.consented,consentForm:result.consentForm, searchInput:params["searchInput"]]
    }

}