package uk.ac.ox.brc.greenlight

import grails.converters.JSON


class ConsentFormCompletionController {

    def consentFormService
    def demographicService
	def GELBarcodeParserService

    def index() {
    }

	def parseGELBarcode(){
		def barcodeStr = params["GELBarcode"]
		def GELParticipant = GELBarcodeParserService.parseGELBarcodeString(barcodeStr)
		def model =[GELParticipant:GELParticipant]
		respond model
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


		//it is already annotated
		if(attachment?.consentForm?.id){
			//this form is annotated before by another user
			flash.error = true
			flash.annotatedBefore = true
			flash.annotatedBeforeLink = createLink([controller: "consentFormCompletion",action: "show",id:attachment?.consentForm?.id])
		}

        respond commandInstance, model: [commandInstance: commandInstance]
    }

    def save() {

        def commandObj = new ConsentFormCommand();
        commandObj.buildObject(params);

        commandObj.patient.validate()
        commandObj.consentForm.validate()

        //check if formId is used before
        //this is checked in ClientSide as well
        def consentId = consentFormService.getConsentFormByFormId(commandObj.consentForm.formID);
        if ( (consentId  != -1 && commandObj.consentForm.id == null) ||
             (consentId != -1 && commandObj.consentForm.id != consentId) )
        {
            flash.error = "Form Id already used!"
            render view: 'create', model: [commandInstance: commandObj]
            return
        }



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

		//if it is in Save mode Not Edit(Update)
		if(!commandObj?.consentForm?.id){
			//if this attachment is already attached to a consentForm, so do not save the consentForm
			//this may happen when two users simultaneously work on the same attachment and one of them saves the
			//annotated form first
			if (commandObj?.attachment?.consentForm){
				//this form is annotated before by another user
				flash.error = true
				flash.annotatedBefore = true
				flash.annotatedBeforeLink = createLink([controller: "consentFormCompletion",action: "show",id:commandObj?.attachment?.consentForm?.id])
				render view: 'create', model: [commandInstance: commandObj]
				return
			}
		}
        def result = consentFormService.save(commandObj.patient, commandObj.consentForm)
        if (result)
            flash.created = "Patient Consent Form ${commandObj.consentForm.id} Created"
        else
            flash.error = "Error in saving Patient Consent"

        redirect controller: 'attachment', action: 'annotatedList'
    }

    def show() {
        def consentForm = ConsentForm.get(params?.id);

        if(request.xhr)
        {
           render consentForm as JSON
           return
        }

        if (!consentForm) {
            redirect(controller: 'attachment', action: 'annotatedList')
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
            redirect action: "annotatedList", controller: "attachment"
            return
        } else {
            redirect(action:'notFound')
            return
        }
    }

    def edit() {
        def consentForm = ConsentForm.get(params?.id);
        if (!consentForm) {
            redirect(controller: 'ConsentForm', action: 'annotatedList')
        }

        def commandInstance = new ConsentFormCommand([
                attachment: consentForm?.attachedFormImage,
                consentForm: consentForm,
                patient: consentForm?.patient,
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

        redirect controller: 'attachment', action: 'annotatedList'

    }


    def notFound() {
        respond "Not found"
        return
    }


    def checkFormId()
    {
        def formId = params["id"]
        //default value , so if it can not find the consent formId, it returns -1 as ID
        def model=[consentFormId:-1]

        if(formId != null){
            model.consentFormId = consentFormService.getConsentFormByFormId(formId)
        }
        respond model
    }

    def findDemographic(){
        def nhsNumber = params["nhsNumber"]
        def patient = demographicService.findPatient(nhsNumber)

		if(patient){
			patient.DOB_day   = patient.DOB[Calendar.DATE];
			patient.DOB_month = patient.DOB[Calendar.MONTH];
			patient.DOB_year  = patient.DOB[Calendar.YEAR];
		}
		def model =[patient:patient]
        respond model
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


        def birthYear = params.commandInstance?.patient.dateOfBirth_year;
        def birthMonth = params.commandInstance?.patient.dateOfBirth_month;
        def birthDay = params.commandInstance?.patient.dateOfBirth_day;
        patient.dateOfBirth = Date.parse("yyyy/MM/dd",birthYear+"/"+birthMonth+"/"+birthDay);



        //Build Consent Form Object
        consentForm = ConsentForm.get(params.commandInstance.consentForm.id)
        if (!consentForm)
            consentForm = new ConsentForm();
        consentForm.properties = params.commandInstance.consentForm

		//Grails assigns null to an empty textArea when it does not contain any text
		if(!consentForm?.comment)
			consentForm.comment = ""

        def consentDateYear = params.commandInstance.consentForm.consentDate_year;
        def consentDateMonth = params.commandInstance.consentForm.consentDate_month;
        def consentDateDay = params.commandInstance.consentForm.consentDate_day;
        consentForm.consentDate = Date.parse("yyyy/MM/dd",consentDateYear+"/"+consentDateMonth+"/"+consentDateDay);

        //if it is in update mode, delete all old responses
        if(consentForm.responses){
            consentForm.responses.clear()
        }
        else{
            consentForm.responses = [];
        }

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