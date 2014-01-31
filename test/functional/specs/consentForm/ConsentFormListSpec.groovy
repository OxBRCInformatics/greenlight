package specs.consentForm

import geb.spock.GebReportingSpec
import pages.consnetForm.ConsentFormCreatePage
import pages.consnetForm.ConsnetFormListPage


class ConsentFormListSpec extends GebReportingSpec {

    def "Be in Consent Form page and add a new form"()
    {
        when: "Be on consent Form page and click on add Form"
        to ConsnetFormListPage
        addFormsButton.click()

        then:"Click on Add Form"
        at ConsentFormCreatePage

    }
}

