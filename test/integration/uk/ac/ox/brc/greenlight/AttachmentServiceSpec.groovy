package uk.ac.ox.brc.greenlight

import grails.test.spock.IntegrationSpec
import spock.lang.Specification

/**
 * Created by soheil on 31/03/2014.
 */
class AttachmentServiceSpec extends IntegrationSpec {

    def service = new AttachmentService()

    def "GetAllAttachments"() {
        when:""
        def r = service.getAllAttachments()
        then:""
        r.size() == 2
    }
}
