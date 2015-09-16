package uk.ac.ox.brc.greenlight

import com.sun.jna.Structure
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.spock.IntegrationSpec
import groovy.mock.interceptor.MockFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Soheil on 13/03/2014.
 */


class ConsentFormSearchSpec extends  IntegrationSpec{

    def consentFormController= new ConsentFormController()



    def setup() {
        def attachment= new Attachment(id: 1, fileName: 'a.jpg', dateOfUpload: new Date(), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flash: true)

        def template=new ConsentFormTemplate(
                id: 1,
                name: "ORB1",
                templateVersion: "1.1",
                namePrefix: "GNR",
        ).addToQuestions(new Question(name: 'I read1...')
        ).save()

        def consent1 = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
                attachedFormImage: attachment,
                template: template,
                consentDate: new Date([year:2014,month:01,date:01]),
                consentTakerName: "Edmin",
                formID: "GEN12345",
                formStatus: ConsentForm.FormStatus.NORMAL
        ).save();


        new Patient(
                givenName: "Eric",
                familyName: "Clapton",
                dateOfBirth: new Date("30/03/1945"),
                hospitalNumber: "1002",
                nhsNumber: "1234567890",
                consents: []
        ).addToConsents(consent1).save()




        def consent2 = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
                attachedFormImage: attachment,
                template: template,
                consentDate: new Date([year:2014,month:01,date:20]),
                consentTakerName: "Adam",
                formID: "GEN12369",
                formStatus: ConsentForm.FormStatus.NORMAL
        ).save();

        new Patient(
                givenName: "Andy",
                familyName: "Morrison",
                dateOfBirth: new Date("30/03/1945"),
                hospitalNumber: "1001",
                nhsNumber: "0987654321",
                consents: []
        ).addToConsents(consent2).save()
    }


    private def InitControllerParams()
    {
        consentFormController.params['nhsNumber'] = ""
        consentFormController.params['hospitalNumber'] = ""
        consentFormController.params['consentTakerName'] = ""
        consentFormController.params['consentDateFrom'] = ""
        consentFormController.params['consentDateTo'] = ""
        consentFormController.params['formIdFrom'] = ""
        consentFormController.params['formIdTo'] = ""
    }


    @Unroll
    void "Test if consentFormSearch return correct values #consentDateFrom #consentDateTo"()
    {

        setup:"Initialize the params for search"
        InitControllerParams();

        when: "Find is called by a single params for nhsNumber"
        consentFormController.params['nhsNumber'] = nhsNumber
        consentFormController.params['hospitalNumber'] = hospitalNumber
        consentFormController.params['consentDateFrom'] = consentDateFrom
        consentFormController.params['consentDateTo'] = consentDateTo

        consentFormController.find()
        def model =  consentFormController.modelAndView.model


        then: "A model is generated containing the found consent form"
        model.consentForms.size()==resultSize


        where:
        nhsNumber   | hospitalNumber |consentDateFrom                       | consentDateTo                        | resultSize
        "1234567890"| ""             | new Date("01/01/2018")               | new Date("01/01/2018")               |    0
        ""          | ""             | new Date("01/01/2014")               | new Date([year:2014,month:1,date:2]) |    1
        "1234567890"| "9999"         | null                                 | null                                 |    0
        "1234567890"| "1002"         | null                                 | null                                 |    1
        ""          | ""             | new Date([year:2014,month:1,date:1]) | new Date([year:2014,month:5,date:5]) |    2
        "1234567890"| ""             | new Date("01/01/2015")               | new Date("20/01/2014")               |    0

    }


    void "Test if consentFormSearch finds consents by NHSNumber correctly"()
    {
        setup:"Initialize the params for search"
        InitControllerParams();

        when: "Find is called by a single params for nhsNumber"
        consentFormController.params['nhsNumber'] = "1234567890"
        consentFormController.find()
        def patient = Patient.findByNhsNumber("1234567890");
        def model =  consentFormController.modelAndView.model


        then: "A model is generated containing the found consent form"
        model.consentForms.size()==1
        model.consentForms[0].patient.givenName == patient.givenName
    }

    void "Test if consentFormSearch finds consents by hospitalNumber correctly"()
    {

        setup:"Initialize the params for search"
        InitControllerParams();

        when: "Find is called by a single params for hospitalNumber"
        consentFormController.params['hospitalNumber'] = "1001"
        consentFormController.find()
        def patient = Patient.findByHospitalNumber("1001");
        def model =  consentFormController.modelAndView.model


        then: "A consentForm should be returned having the right patient"
        model.consentForms.size()==1
        model.consentForms[0].patient.givenName == patient.givenName
    }


    void "Test if consentFormSearch finds consents by consentDateFrom & consentFormTo correctly"()
    {
        setup:"Initialize the params for search"
        InitControllerParams();

        when: "Find is called by a single params for consentDateFrom-To"
        consentFormController.params['consentDateFrom'] = new Date([year:2014,month:1,date:1])
        consentFormController.params['consentDateTo'] = new Date([year:2014,month:2,date:1])
        consentFormController.find()
        def model =  consentFormController.modelAndView.model


        then: "A consent form should be returned"
        model.consentForms.size()==1
    }

    @Unroll
    void "Test if consentFormSearch finds consents correctly between two consentFormDates from:#from to:#to"()
    {
        setup:"Initialize the params for search"
        InitControllerParams();

        when: "Find is called by a single params for consentDateFrom-To"
        consentFormController.params['consentDateFrom'] = from
        consentFormController.params['consentDateTo'] = to
        consentFormController.find()
        def model =  consentFormController.modelAndView.model


        then: "correct consent form should be returned"
        model.consentForms.size() == result

        where:
        from                                  |   to                                    |  result
        new Date([year:2014,month:1,date:1])  |    new Date([year:2014,month:2,date:1]) |    2
        new Date([year:2014,month:1,date:5])  |    new Date([year:2014,month:2,date:1]) |    1
        null                                  |    new Date([year:2014,month:2,date:1]) |    2
        new Date([year:2014,month:1,date:1])  |    null                                 |    2
        null                                  |    null                                 |    0

    }


    @Unroll
    void "Test if consentFormSearch finds consents by formId  from:#from  to:#to"()
    {
        setup:"Initialize the params for search"
        InitControllerParams();

        when: "Find is called by a single params for formIdFrom-formIdTo"
        consentFormController.params['formIdFrom'] = from
        consentFormController.params['formIdTo'] = to
        consentFormController.find()
        def model =  consentFormController.modelAndView.model



        then: "A consent form should be returned"
        model.consentForms.size()==size

        where:
        from         | to           | size
        "GEN12345"   | "GEN12370"   | 2
        "GEN12345"   | "GEN12345"   | 1
        "GEN12345"   | "GEN12365"   | 1
        "GEN12370"   | "GEN12345"   | 2
        "GEN12345"   | ""           | 1
        ""           | "GEN12345"   | 2
    }


    void "Test if consentFormSearch returns corrects records if FormIdfrom>FormIdTo"()
    {
        setup:"Initialize the params for search"
        InitControllerParams();

        when: "Find is called by a single params for formIdFrom-formIdTo"
        consentFormController.params['formIdFrom'] = "GEN12390"
        consentFormController.params['formIdTo'] = "GEN12345"
        consentFormController.params['nhsNumber'] = "1234567890"

        consentFormController.find()
        def model =  consentFormController.modelAndView.model


        then: "no result should be returned"
        model.consentForms.size()==1
    }


    void "Test if consentFormSearch ignores date search where consentDateFrom>consentDateTo"()
    {

        setup:"Initialize the params for search"
        InitControllerParams();

        when: "Find is called by a single params for consentDateFrom-To"
        consentFormController.params['consentDateFrom'] = new Date("01/01/2014")
        consentFormController.params['consentDateTo'] = new Date("01/02/2013")
        consentFormController.find()
        def model =  consentFormController.modelAndView.model


        then: "consentDates should be ignored in the search"
        model.consentForms.size()==2
    }


}
