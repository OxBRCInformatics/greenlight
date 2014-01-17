import uk.ac.ox.brc.greenlight.ConsentForm
import uk.ac.ox.brc.greenlight.LabSample
import uk.ac.ox.brc.greenlight.Patient
import uk.ac.ox.brc.greenlight.PatientConsent

class BootStrap {

    def init = { servletContext ->
        def billy = new Patient(
                givenName: "Billy",
                familyName: "Joel",
                dateOfBirth: new Date("09/05/1949"),
                gender: Patient.Gender.MALE,
                hospitalNumber: "1001",
                nhsNumber: "nhs2",
                consents: [],
        ).addToConsents(
                clinicianName: "Geoff Geoffries",
				date:new Date(),

        ).addToLabSamples(
                sampleID: "SAMPLE_GEOFF_001",
                dateOfProcessing: new Date("01/10/2013")
        ).addToLabSamples(
                sampleID: "SAMPLE_GEOFF_002",
                dateOfProcessing: new Date() //today
        ).save(failOnError: true)

        def eric = new Patient(
                givenName: "Eric",
                familyName: "Clapton",
                dateOfBirth: new Date("30/03/1945"),
                gender: Patient.Gender.MALE,
                hospitalNumber: "1001",
                nhsNumber: "nhs2",
                consents: [],
        ).addToLabSamples(
                sampleID: "SAMPLE_ERIC_001",
                dateOfProcessing: new Date() //today
        ).save(failOnError: true)

    }
    def destroy = {
    }
}
