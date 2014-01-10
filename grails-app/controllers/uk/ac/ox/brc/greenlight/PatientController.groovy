package uk.ac.ox.brc.greenlight

class PatientController {

    def index() {
        [patients: Patient.all]
    }
}
