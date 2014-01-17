package uk.ac.ox.brc.greenlight

class PatientController {

    def index(){
        redirect(action: "consentDashboard")
    }
    def consentDashboard() {
        [patients: Patient.all, samples: LabSample.findAll("from LabSample l where l.dateOfProcessing > current_date()")]
    }
}
