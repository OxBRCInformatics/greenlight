package uk.ac.ox.brc.greenlight

class PatientController {

    def index(){
        redirect(action: "consentDashboard")
    }
    def consentDashboard() {
        [patients: Patient.all]
    }
}
