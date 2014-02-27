package uk.ac.ox.brc.greenlight

import grails.converters.JSON


class ConsentFormCompletionController {

    def consentFormService

    def index() {
    }

    def create() {
        def attachment = Attachment.get(params?.attachmentId);
        if (!attachment) {
            respond null
            return
        }

        ConsentFormCommand commandInstance = new ConsentFormCommand([
                attachment: attachment,
                consentForm: new ConsentForm()
        ]);

        respond commandInstance, model: [commandInstance: commandInstance]
    }

    def save() {

        def commandObj = new ConsentFormCommand();
        commandObj.buildObject(params);

        commandObj.patient.validate()
        commandObj.consentForm.validate()

        if (commandObj.patient.hasErrors() || commandObj.consentForm.hasErrors()) {
            flash.error = "Error in input"

            commandObj.patient.errors.each { error ->
                flash.error += "\n" + error
            }
            commandObj.consentForm.errors.each { error ->
                flash.error += "\n" + error
            }

            render view: 'create', model: [commandInstance: commandObj]
            return
        }

        def result = consentFormService.save(commandObj.patient, commandObj.consentForm)
        if (result)
            flash.created = "Patient Consent Form ${commandObj.consentForm.id} Created"
        else
            flash.error = "Error in saving Patient Consent"

        redirect controller: 'attachment', action: 'list'
    }

    def show() {
        def consentForm = ConsentForm.get(params?.id);

        if(request.xhr)
        {
           render consentForm as JSON
           return
        }

        if (!consentForm) {
            redirect(controller: 'attachment', action: 'list')
            return;
        }
        def command = new ConsentFormCommand([
                consentForm: consentForm,
                patient: consentForm.patient,
                attachment: consentForm.attachedFormImage,
                template: consentForm?.template,
                questions: consentForm?.template?.questions,
                responses: consentForm?.responses
        ]);

        respond command, model: [commandInstance: command,
                questions: consentForm?.template?.questions,
                responses: consentForm?.responses]
    }

    def delete() {
        def consentForm = ConsentForm.get(params.id);
        def result = consentFormService.delete(consentForm)
        if (result) {
            redirect action: "list", controller: "attachment"
            return
        } else {
            redirect(action:'notFound')
            return
        }
    }

    def edit() {
        def consentForm = ConsentForm.get(params?.id);
        if (!consentForm) {
            redirect(controller: 'ConsentForm', action: 'list')
        }

        def commandInstance = new ConsentFormCommand([
                attachment: consentForm.attachedFormImage,
                consentForm: consentForm,
                patient: consentForm.patient,
                template: consentForm?.template
        ]);

        respond commandInstance, model: [commandInstance: commandInstance]
    }

    def update() {


        def commandObj = new ConsentFormCommand();
        commandObj.buildObject(params);

        commandObj.patient.validate()
        commandObj.consentForm.validate()

        if (commandObj.patient.hasErrors() || commandObj.consentForm.hasErrors()) {
            flash.error = "Error in input"
            render view: 'create', model: [commandInstance: commandObj]
            return
        }

        def result = consentFormService.save(commandObj.patient, commandObj.consentForm)
        if (result)
            flash.created = "Patient Consent Form ${commandObj.consentForm.id} Created"
        else
            flash.error = "Error in saving Patient Consent"

        redirect controller: 'attachment', action: 'list'

    }


    def notFound() {
        respond "Not found"
        return
    }


}

class ConsentFormCommand {
    Patient patient
    ConsentForm consentForm
    Attachment attachment
    ConsentFormTemplate template
    List<Question> questions
    List<Response> responses

    def buildObject(params) {
        patient = Patient.get(params.commandInstance.patient.id)
        if (!patient)
            patient = new Patient();
        patient.properties = params.commandInstance?.patient;

        //Build Consent Form Object
        consentForm = ConsentForm.get(params.commandInstance.consentForm.id)
        if (!consentForm)
            consentForm = new ConsentForm();
        consentForm.properties = params.commandInstance.consentForm
        consentForm.responses = [];

        //Load Selected Consent Template
        if (params.commandInstance.consentFormTemplateId != null && params.commandInstance.consentFormTemplateId != "-1") {
            template = ConsentFormTemplate.get(params.commandInstance.consentFormTemplateId)
        }

        //Load Attachment
        attachment = Attachment.get(params.commandInstance.attachmentId);
        consentForm.attachedFormImage = attachment;
        consentForm.patient = patient;
        consentForm.template = template;


        for (int i = 0; i < params.int('questionsSize'); i++) {
            Response.ResponseValue answer = params["responses.$i"]
            Question question = template?.questions[i];

            def response = new Response(answer: answer, question: question)
            consentForm.addToResponses(response)
        }
    }
}