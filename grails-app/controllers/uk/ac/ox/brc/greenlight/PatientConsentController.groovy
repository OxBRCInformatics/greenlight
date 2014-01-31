package uk.ac.ox.brc.greenlight

import grails.converters.JSON

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class PatientConsentController {

    def patientConsentService

    def index() {
    }

    def show() {
        def patientConsent = PatientConsent.get(params?.id);
        if (!patientConsent) {
            redirect(controller: 'ConsentForm', action: 'list')
        }
        [patientConsent: patientConsent, patient: patientConsent?.patient, consentForm: patientConsent?.consentForm]
    }

    def edit() {
        def patientConsent = PatientConsent.get(params?.id);
        if (!patientConsent) {
            redirect(controller: 'ConsentForm', action: 'list')
        }
        [patientConsent: patientConsent, patient: patientConsent?.patient, consentForm: patientConsent?.consentForm]
    }

    def update() {
        //Build Patient Object
        def patient = Patient.get(params.patient.id);
        patient.properties = params.patient;

        def patientConsent = PatientConsent.get(params.patientConsent.id)
        bindData(patientConsent, params.patientConsent, [exclude: ['questions']]);


        patientConsent.answers.eachWithIndex() { answer, i ->
            patientConsent.answers[i] = false;
            if (params["patientConsentAnswers.${i}"])
                patientConsent.answers[i] = true;
        }

        //Load the consent Form
        def consentForm = ConsentForm.get(params.consentForm.id);

        patientConsent.patient = patient;
        patientConsent.consentForm = consentForm;

        patient.validate()
        patientConsent.validate()

        if (patient.hasErrors() ||
                patientConsent.hasErrors()) {
            flash.error = "Error in input"
            render model: [patientConsent: patientConsent, patient: patient, consentForm: consentForm], view: 'edit'
            return
        }

        def result = patientConsentService.save(patient, patientConsent)
        if (result) {
            flash.created = "Patient Consent Form ${patientConsent.id} Updated"
            redirect action: 'show', params: [id: patientConsent.id]
            return
        }

        flash.error = "Error in saving Patient Consent"
        render model: [patientConsent: patientConsent, patient: patient, consentForm: consentForm], view: 'edit'
        return
    }

    def save() {
        //Build Patient Object
        def patient = new Patient();
        patient.properties = params.patient;

        def patientConsent = patientConsentService.buildORBConsent();
        bindData(patientConsent, params.patientConsent, [exclude: ['questions']]);

        patientConsent.answers.eachWithIndex() { answer, i ->
            patientConsent.answers[i] = false;
            if (params["patientConsentAnswers.${i}"])
                patientConsent.answers[i] = true;
        }

        //Load the consent Form
        def consentForm = ConsentForm.get(params.consentForm.id);


        patientConsent.patient = patient;
        patientConsent.consentForm = consentForm;

        patient.validate()
        patientConsent.validate()

        if (patient.hasErrors() ||
                patientConsent.hasErrors()) {
            flash.error = "Error in input"
            render model: [patientConsent: patientConsent, patient: patient, consentForm: consentForm], view: 'create'
            return
        }

        def result = patientConsentService.save(patient, patientConsent)
        if (result) {
            flash.created = "Patient Consent Form ${patientConsent.id} Created"
            redirect controller: 'consentForm', action: 'list'
        }

        flash.error = "Error in saving Patient Consent"
        render model: [patientConsent: patientConsent, patient: patient, consentForm: consentForm], view: 'create'
        return
    }

    def delete() {
        def patientConsent = PatientConsent.get(params.id);
        def result = patientConsentService.delete(patientConsent)
        if (result) {
            redirect action: "list", controller: "consentForm"
            return
        }
        else
        {
            //needs to Notfound page
        }
    }

    def create() {
        def consentForm = ConsentForm.get(params.consentFormId);
        if (!consentForm) {
            render 'not found';
            return
        }
        /*
        if(consentForm.patientConsent)
        {
            redirect action:'show', id:params.id
            return
        }*/
        def patientConsent = patientConsentService.buildORBConsent()
        def patient = new Patient();
        [patientConsent: patientConsent, patient: patient, consentForm: consentForm]
    }
}