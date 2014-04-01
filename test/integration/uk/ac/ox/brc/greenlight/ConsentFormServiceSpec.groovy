package uk.ac.ox.brc.greenlight

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.spock.IntegrationSpec
import spock.lang.Specification

/**
 * Created by soheil on 28/03/2014.
 */
class ConsentFormServiceSpec extends IntegrationSpec {

    def   consentFormService
    def   consentEvaluationService

    def setup(){
        def attachment= new Attachment(id: 1, fileName: 'a.jpg', dateOfUpload: new Date(),
                attachmentType: Attachment.AttachmentType.IMAGE, content: []).save()

        def template=new ConsentFormTemplate(
                id: 1,
                name: "ORB1",
                templateVersion: "1.1",
                namePrefix: "GNR",
        ).addToQuestions(new Question(name: 'I read1...')
        ).save()


        def patient= new Patient(
            givenName: "Eric",
            familyName: "Clapton",
            dateOfBirth: new Date("30/03/1945"),
            hospitalNumber: "1002",
            nhsNumber: "1234567890",
            consents: []
            ).save()

       def consent = new ConsentForm(
                attachedFormImage: attachment,
                template: template,
                patient: patient,
                consentDate: new Date([year:2014,month:01,date:01]),
                consentTakerName: "Edward",
                formID: "GEN12345",
                formStatus: ConsentForm.FormStatus.NORMAL,
        ).save()

        consent.addToResponses(new Response(answer: Response.ResponseValue.YES))
        consent.addToResponses(new Response(answer: Response.ResponseValue.YES))
        consent.addToResponses(new Response(answer: Response.ResponseValue.YES))
        consent.addToResponses(new Response(answer: Response.ResponseValue.YES))
        consent.save()
    }

    def "Delete action will delete consentForm and its responses"() {

        when:"Delete action is called"
        ConsentForm.count() == 1
        def cons= ConsentForm.first()
        cons.responses.size() == 2
        consentFormService.delete(cons)


        then:"It deletes the consentForm and its responses"
        ConsentForm.count() == 0
        Response.count() == 0
        Patient.count() == 1
    }

    def "Check getConsentFormByFormId for not-available FormId "()
    {
        when:"CheckFormId is called for a non-existing formId"
        def formId = "123"
        def consentId = consentFormService.getConsentFormByFormId(formId);

        then:"then -1 will be returned as not available"
        consentId == -1
    }


    def "Check getConsentFormByFormId for available FormId "()
    {
        when:"CheckFormId is called for a existing formId"
        def formId =ConsentForm.list()[0].formID
        def actualConsentId = ConsentForm.list()[0].id
        def consentId = consentFormService.getConsentFormByFormId(formId);

        then:"then the actual consent id should be returned"
        consentId != -1
        consentId == actualConsentId
    }


    def "Check getConsentFormByFormId for general FormId ends with 00000"()
    {
        when:"CheckFormId is called for a general FormId"
        def formId = "GEN00000"
        def consentId = consentFormService.getConsentFormByFormId(formId);

        then:"then it returns -1"
        consentId == -1
    }



    def "exportToCSV returns CSV content with Headers"()
    {
        when:"exportToCSV is called"
        String csv = consentFormService.exportToCSV()
        csv.readLines().size() != 0
        def headers=csv.readLines()[0].tokenize(",")

        then:"the first row is header"
        headers.size() == 13
        headers[0] == "consentId"
        headers[1] == "consentDate"
        headers[2] == "consentformID"
        headers[3] == "consentTakerName"
        headers[4] == "formStatus"
        headers[5] == "patientNHS"
        headers[6] == "patientMRN"
        headers[7] == "patientName"
        headers[8] == "patientSurName"
        headers[9] =="patientDateOfBirth"
        headers[10] == "templateName"
        headers[11] == "consentResult"
        headers[12] == "responses"
    }


    def "exportToCSV returns consent in CSV format"()
    {
        when:"exportToCSV is called"
        String csv = consentFormService.exportToCSV()
        List<ConsentForm> consents = ConsentForm.list()


        then:"it returns contents in csv format"
        csv.readLines().size() == consents.size() + 1 //1 for Header row
        csv.readLines().eachWithIndex { line, index ->
            //the first line is Header
            if(index == 0)
                return;

            def values = line.tokenize(",")
            values.size() != 0
            def consent =  consents[index-1]
            assert consent.id.toString() == values[0]
            assert consent.consentDate.format("dd-MM-yyyy") == values[1]
            assert consent.formID.toString() == values[2]
            assert consent.consentTakerName == values[3]
            assert consent.formStatus.toString() == values[4]
            assert consent.patient.nhsNumber.toString() == values[5]
            assert consent.patient.hospitalNumber.toString() == values[6]
            assert consent.patient.givenName.toString() == values[7]
            assert consent.patient.familyName.toString() == values[8]
            assert consent.patient.dateOfBirth.format("dd-MM-yyyy") == values[9]
            assert consent.template.namePrefix.toString() == values[10]

            ConsentStatus status=  consentEvaluationService.getConsentStatus(consent)
            assert status.toString() == values[11]

            def resString = ""
            consent.responses.each { response->
                resString += response.answer.toString() +"|"
            }
            assert resString == values[12]
        }
    }
}