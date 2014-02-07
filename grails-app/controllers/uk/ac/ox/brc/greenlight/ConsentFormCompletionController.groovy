package uk.ac.ox.brc.greenlight

import org.codehaus.groovy.grails.plugins.web.api.ControllersApi

class ConsentFormCompletionController {

        def consentFormService

        def index() {
        }

        def create() {
            def attachment = Attachment.get(params?.attachmentId);
            if (!attachment)
                redirect  action:'notFound'

            ConsentFormCommand commandInstance =new ConsentFormCommand();
            commandInstance.attachment = attachment
            commandInstance.consentForm=new ConsentForm(formStatus: ConsentForm.FormStatus.NORMAL);
            respond  commandInstance, model:[commandInstance: commandInstance]
        }

        def save() {

            def commandObj=new ConsentFormCommand();

            commandObj.patient = new Patient();
            commandObj.patient.properties = params.commandInstance.patient;

            //Build Consent Form Object
            commandObj.consentForm =new ConsentForm();
            bindData(commandObj.consentForm, params.commandInstance.consentForm, [exclude: ['responses']]);
            commandObj.consentForm.responses = [];

            //Load Selected Consent Template
            commandObj.template = ConsentFormTemplate.get(params.commandInstance.consentFormTemplateId);

            //Load Attachment
            commandObj.attachment = Attachment.get(params.commandInstance.attachmentId);

            commandObj.consentForm.attachedFormImage = commandObj.attachment;
            commandObj.consentForm.patient = commandObj.patient;
            commandObj.consentForm.template = commandObj.template;


            for (int i = 0; i < params.int('questionsSize');i ++ ) {
                Response.ResponseValue answer = params["responses.$i"]
                Question question =  commandObj.template?.questions[i];

                def response = new Response(answer: answer,question:question)
                commandObj.consentForm.addToResponses(response)
            }

        commandObj.patient.validate()
        commandObj.consentForm.validate()

        if (commandObj.patient.hasErrors() || commandObj.consentForm.hasErrors()) {
            flash.error = "Error in input"
            render view: 'create',  model:[commandInstance: commandObj]
            return
        }

        def result = consentFormService.save(commandObj.patient, commandObj.consentForm)
        if (result)
            flash.created = "Patient Consent Form ${commandObj.consentForm.id} Created"
        else
            flash.error = "Error in saving Patient Consent"

        redirect controller:'attachment',  action:'list'
    }

        def show() {
            def consentForm = ConsentForm.get(params?.id);
            if (!consentForm) {
                redirect(controller: 'ConsentForm', action: 'list')
            }

            def command =new ConsentFormCommand();
            command.consentForm = consentForm;
            command.patient = consentForm.patient;
            command.attachment = consentForm.attachedFormImage;
            command.template= consentForm.template;
            command.questions= consentForm.template.questions;
            command.responses= consentForm.responses;

            respond command , model:[commandInstance: command]
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
               notFound()
            }
        }

        def edit() {
            def consentForm = ConsentForm.get(params?.id);
            if (!consentForm) {
                redirect(controller: 'ConsentForm', action: 'list')
            }

            def commandInstance =new ConsentFormCommand([
                    attachment: consentForm.attachedFormImage,
                    consentForm:consentForm,
                    patient: consentForm.patient,
                    template: consentForm.template

            ]);

            respond  commandInstance, model:[commandInstance: commandInstance]
        }

        def update() {


           def commandObj=new ConsentFormCommand();

            //Load patient object
            commandObj.patient = Patient.get(params.commandInstance.patient.id)
            commandObj.patient.properties = params.commandInstance.patient;

            //Load Consent Form Object
            commandObj.consentForm =ConsentForm.get(params.commandInstance.consentForm.id)
            bindData(commandObj.consentForm, params.commandInstance.consentForm, [exclude: ['responses']]);
            commandObj.consentForm.responses = [];

            //Load Selected Consent Template
            commandObj.template = ConsentFormTemplate.get(params.commandInstance.consentFormTemplateId);

            //Load Attachment
            commandObj.attachment = Attachment.get(params.commandInstance.attachmentId);


            commandObj.consentForm.attachedFormImage = commandObj.attachment;
            commandObj.consentForm.patient = commandObj.patient;
            commandObj.consentForm.template = commandObj.template;


            for (int i = 0; i < params.int('questionsSize');i ++ ) {
                Response.ResponseValue answer = params["responses.$i"]
                Question question =  commandObj.template?.questions[i];

                def response = new Response(answer: answer,question:question)
                commandObj.consentForm.addToResponses(response)
            }

            commandObj.patient.validate()
            commandObj.consentForm.validate()

            if (commandObj.patient.hasErrors() || commandObj.consentForm.hasErrors()) {
                flash.error = "Error in input"
                render view: 'create',  model:[commandInstance: commandObj]
                return
            }

            def result = consentFormService.save(commandObj.patient, commandObj.consentForm)
            if (result)
                flash.created = "Patient Consent Form ${commandObj.consentForm.id} Created"
            else
                flash.error = "Error in saving Patient Consent"

            redirect controller:'attachment',  action:'list'

        }



        def notFound()
        {
            render 'Not Found!'
        }
    }

class ConsentFormCommand
{
    Patient patient
    ConsentForm consentForm
    Attachment attachment
    ConsentFormTemplate template
    List<Question> questions
    List<Response> responses
}