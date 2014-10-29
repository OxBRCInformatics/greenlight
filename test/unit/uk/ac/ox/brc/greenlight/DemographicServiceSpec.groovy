package uk.ac.ox.brc.greenlight

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(DemographicService)
class DemographicServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "checkPatient"() {
        when:"nhsNumber is passed"
        def result = service.findPatient("9446457610")

        then:"it returns a patient demographics"
        result["ACTIVE_MRN"] == "10221601"
        result["GIVENNAME"] == "THIRTYFIVECHARACTERLONGFORENAM"
        result["FAMILYNAME"] == "FEATHERSTONEHAUGH-SMYTHE-WILLI"
        result["SEX"] == "1"
        result["DOB"].toString() == "2010-05-17 00:00:00.0"
 }
}
