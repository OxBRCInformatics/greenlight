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


    def export (){
        def csvString = consentFormService.exportToCSV()
        def fileName ="consentForms-"+(new Date()).format("yyyy-MMM-dd")
        response.setHeader("Content-disposition", "attachment; filename=${fileName}.csv");
        render(contentType: "text/csv;charset=utf-8", text: csvString.toString());
    }
}
