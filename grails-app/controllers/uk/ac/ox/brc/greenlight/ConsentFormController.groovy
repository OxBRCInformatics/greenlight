package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional

@Transactional(readOnly = true)
class ConsentFormController {

    def consentFormService

    def index() {
    }

    def show() {
        def consentForm = ConsentForm.get(params?.id);
        if (!consentForm) {
            redirect(controller: 'ConsentForm', action: 'list')
        }
        [consentForm: consentForm, patient: consentForm?.patient, attachment: consentForm?.attachedFormImage]
    }

    def edit() {
        def consentForm = ConsentForm.get(params?.id);
        if (!consentForm) {
            redirect(controller: 'ConsentForm', action: 'list')
        }
        [consentForm: consentForm, patient: consentForm?.patient, attachment: consentForm?.attachedFormImage]
    }

    def update() {
        //Build Patient Object
        def patient = Patient.get(params.patient.id);
        patient.properties = params.patient;

        def consentForm = ConsentForm.get(params.consentForm.id)
        bindData(consentForm, params.consentForm, [exclude: ['questions']]);


        consentForm.answers.eachWithIndex() { answer, i ->
            consentForm.answers[i] = false;
            if (params["consentFormAnswers.${i}"])
                consentForm.answers[i] = true;
        }

        //Load the consent Form
        def attachment = Attachment.get(params.attachment.id);

        consentForm.patient = patient;
        consentForm.attachedFormImage = attachment;

        patient.validate()
        consentForm.validate()

        if (patient.hasErrors() ||
                consentForm.hasErrors()) {
            flash.error = "Error in input"
            render model: [consentForm: consentForm, patient: patient, attachment: attachment], view: 'edit'
            return
        }

        def result = consentFormService.save(patient, consentForm)
        if (result) {
            flash.created = "Patient Consent Form ${consentForm.id} Updated"
            redirect action: 'show', params: [id: consentForm.id]
            return
        }

        flash.error = "Error in saving Patient Consent"
        render model: [consentForm: consentForm, patient: patient, attachment: attachment], view: 'edit'
        return
    }

    def save() {
        //Build Patient Object
        def patient = new Patient();
        patient.properties = params.patient;

        def consentForm = consentFormService.buildORBConsent();
        bindData(consentForm, params.consentForm, [exclude: ['questions']]);

        consentForm.answers.eachWithIndex() { answer, i ->
            consentForm.answers[i] = false;
            if (params["consentFormAnswers.${i}"])
                consentForm.answers[i] = true;
        }

        //Load the consent Form
        def attachment = Attachment.get(params.attachment.id);


        consentForm.patient = patient;
        consentForm.attachment = attachment;

        patient.validate()
        consentForm.validate()

        if (patient.hasErrors() ||
                consentForm.hasErrors()) {
            flash.error = "Error in input"
            render model: [consentForm: consentForm, patient: patient, attachment: attachment], view: 'create'
            return
        }

        def result = consentFormService.save(patient, consentForm)
        if (result) {
            flash.created = "Patient Consent Form ${consentForm.id} Created"
            redirect controller: 'attachment', action: 'list'
        }

        flash.error = "Error in saving Patient Consent"
        render model: [consentForm: consentForm, patient: patient, attachment: attachment], view: 'create'
        return
    }

    def delete() {
        def consentForm = ConsentForm.get(params.id);
        def result = consentFormService.delete(consentForm)
        if (result) {
            redirect action: "list", controller: "attachment"
            return
        }
        else
        {
            //needs to Notfound page
        }
    }

    def create() {
        def attachment = Attachment.get(params.attachmentId);
        if (!attachment) {
            render 'not found';
            return
        }

        def consentForm = consentFormService.buildORBConsent()
        def patient = new Patient();
        [consentForm: consentForm, patient: patient, attachment: attachment]
    }
}