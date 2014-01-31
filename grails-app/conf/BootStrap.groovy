import uk.ac.ox.brc.greenlight.Attachment
import uk.ac.ox.brc.greenlight.LabSample
import uk.ac.ox.brc.greenlight.Patient
import uk.ac.ox.brc.greenlight.PatientConsent

class BootStrap {

    def init = { servletContext ->
        def billy = new Patient(
                givenName: "Billy",
                familyName: "Joel",
                dateOfBirth: new Date("09/05/1949"),
                hospitalNumber: "1001",
                nhsNumber: "123-456-7890",
                consents: []
        ).addToConsents(
                consentTakerName: "Geoff Geoffries",
                consentDate:new Date()
        ).save(failOnError: true)

        def eric = new Patient(
                givenName: "Eric",
                familyName: "Clapton",
                dateOfBirth: new Date("30/03/1945"),
                hospitalNumber: "1001",
                nhsNumber: "123-456-7891",
                consents: []
        ).save(failOnError: true)


        def consentForm1=new Attachment(
                dateOfScan:new Date(),
                scannedForm:[]
        ).save(failOnError: true);


        def consentForm2=new Attachment(
                dateOfScan:new Date(),
                scannedForm:[]
        ).save(failOnError: true);



        def consentForm3=new Attachment(
                dateOfScan:new Date(),
                scannedForm:[],
                patientConsent:billy.consents[0]
        ).save(failOnError: true);

    }
    def destroy = {
    }
}
