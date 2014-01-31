<%@ page import="uk.ac.ox.brc.greenlight.ConsentFormTemplate; uk.ac.ox.brc.greenlight.ConsentFormTemplateService; uk.ac.ox.brc.greenlight.ConsentForm; uk.ac.ox.brc.greenlight.ConsentForm" %>
<%@ page import="uk.ac.ox.brc.greenlight.Patient" %>
<%@ page import="uk.ac.ox.brc.greenlight.Attachment" %>




<g:hiddenField name="commandInstance.attachmentId" value="${commandInstance?.attachment?.id}"></g:hiddenField>
<g:hiddenField name="commandInstance.patient.id" value="${commandInstance?.patient?.id}"></g:hiddenField>
<g:hiddenField name="commandInstance.consentForm.id" value="${commandInstance?.consentForm?.id}"></g:hiddenField>



<g:set var="responses" value="${commandInstance?.consentForm?.responses}"></g:set>



<div class="col-md-12">
    <div class="panel panel-primary PageMainPanel">
        <div class="panel-body">

            <div class="col-md-6 ">
                <div class="form-group">
                    <div class="form-group">
                        <label for="commandInstance.patient.nhsNumber" class="required">NHS Number</label>
                        <g:textField
                                class="form-control  ${hasErrors(bean: patient, field: 'nhsNumber', 'invalidInput')}"
                                id="commandInstance.patient.nhsNumber" name="commandInstance.patient.nhsNumber"
                                value="${commandInstance?.patient?.nhsNumber}"
                                placeholder="NHS number  NNN-NNN-NNNN"/>
                    </div>

                    <label for="commandInstance.patient.givenName" class="required">Given Name</label>
                    <g:textField
                            class="form-control ${hasErrors(bean: patient, field: 'givenName', 'invalidInput')}"
                            name="commandInstance.patient.givenName" value="${commandInstance?.patient?.givenName}"
                            placeholder="Given Name"/>

                </div>
                <div class="form-group">
                    <label for="commandInstance.patient.familyName" class="required">Family Name</label>
                    <g:textField
                            class="form-control  ${hasErrors(bean: patient, field: 'familyName', 'invalidInput')}"
                            id="commandInstance.patient.familyName" name="commandInstance.patient.familyName"
                            value="${commandInstance?.patient?.familyName}"
                            placeholder="Family Name"/>
                </div>
                <div class="form-group">
                    <label for="commandInstance.consentForm.consentTakerName" class="required">Consent Taker Name</label>
                    <g:textField name="commandInstance.consentForm.consentTakerName" id="commandInstance.consentForm.consentTakerName"
                                 class="form-control  ${hasErrors(bean: consentForm, field: 'consentTakerName', 'invalidInput')}"
                                 value="${commandInstance?.consentForm?.consentTakerName}"
                                 placeholder="Enter Consent Taker's Name"/>
                </div>
                <div class="form-group">
                    <label for="commandInstance.patient.dateOfBirth">Date of Birth</label>
                    <g:datePicker class="form-control" id="commandInstance.patient.dateOfBirth" name="commandInstance.patient.dateOfBirth"
                                  value="${commandInstance?.patient?.dateOfBirth}"
                                  placeholder="Date of Birth"
                                  precision="day"/>
                </div>

            </div>
            <div class="col-md-6 ">
                <div class="form-group">
                    <label for="commandInstance.patient.hospitalNumber" class="required">Hospital Number</label>
                    <g:textField
                            class="form-control  ${hasErrors(bean: patient, field: 'hospitalNumber', 'invalidInput')}"
                            id="commandInstance.patient.hospitalNumber" name="commandInstance.patient.hospitalNumber"
                            value="${commandInstance?.patient?.hospitalNumber}"
                            placeholder="Hospital Number"/>
                </div>
                <div class="form-group">
                    <label for="commandInstance.consentForm.formID" class="required">Form Id</label>
                    <g:textField
                            class="form-control  ${hasErrors(bean: consentForm, field: 'formID', 'invalidInput')}"
                            id="commandInstance.consentForm.formID" name="commandInstance.consentForm.formID"
                            value="${commandInstance?.consentForm?.formID}"
                            placeholder="Consent Form Id"/>
                </div>
                <div class="form-group">
                    <label for="commandInstance.consentForm.ConsentDate">Consent Date</label>
                    <g:datePicker class="form-control"
                                  id="commandInstance.consentForm.ConsentDate"
                                  name="commandInstance.consentForm.consentDate"
                                  value="${commandInstance?.consentForm?.consentDate}"
                                  placeholder="Consent Date"
                                  precision="day"/>
                </div>

            </div>

        </div>
    </div>
</div>

<div class="col-md-12">
    <div class="panel panel-primary PageMainPanel">
        <div class="panel-body">
            <div class="col-md-8">

                <g:if test="${commandInstance?.attachment}">
                    <img id="commandInstance.attachment" style="margin: 4px; width:100%;height:100%;"
                     class="Photo"
                     src="${createLink(controller: 'attachment', action: 'viewContent', id: "${commandInstance?.attachment?.id}")}"/>
                </g:if>
            </div>
            <div class="col-md-4">
                <div class="form-group">
                <label >Consent Form Type</label>
                %{--<select id="consentFormTemplate"  name="consentFormTemplate">--}%
                <g:select id="commandInstance.consentFormTemplate"
                    style="font-size:12px;"
                          name="commandInstance.consentFormTemplateId" class="form-control"
                          value="${commandInstance?.consentForm?.template?.id}"
                          from="${ConsentFormTemplate.list()}"
                          optionKey="id"
                          optionValue="${name}"
                          noSelection="${[null: 'Select one ...']}"
                          onchange="  ${
                              remoteFunction(
                                  action: 'getQuestions',
                                  controller: 'ConsentFormTemplate',
                                  params:'\'templateId=\' + this.value',
                                  update: [success: 'questionList', failure: ''],
                                  onSuccess: "FixQuestionStyle()"
                          )}"></g:select>
</div>
                <div class="form-group">
                   <label for="commandInstance.consentForm.formStatus">Form Status</label>
                   <g:select id="commandInstance?.consentForm.formStatus"
                             name="commandInstance.consentForm.formStatus"
                             class="form-control"
                             value="${commandInstance?.consentForm?.formStatus}"
                             from="${ConsentForm?.FormStatus?.values()}"
                             optionKey="key"
                             optionValue="value"
                             noSelection="${[null: 'Select one ...']}"
                   />
                </div>


                <div class="form-group" id="questionList">
                    <g:if test="${commandInstance?.consentForm}">
                        <g:render  template="/consentFormTemplate/getQuestions"
                                   model="[questions:commandInstance?.template?.questions,
                                           responses:commandInstance?.consentForm?.responses]" >
                        </g:render>
                    </g:if>
                </div>

            </div>
        </div>
    </div>
</div>


<g:javascript>

    function FixQuestionStyle()
    {
        $('.bootstrapTooltip').tooltip()
    }


    function applyFormValidation()
    {
        $('form').validate({
            rules: {
                'commandInstance.patient.givenName':{
                    required:true
                },
                'commandInstance.patient.familyName':{
                    required:true
                },
                'commandInstance.consentForm.consentTakerName':{
                    required:true
                },

                'commandInstance.consentForm.formID':{
                    required:true
                },
                'commandInstance.patient.hospitalNumber':
                {
                    required:true
                },
                'commandInstance.patient.nhsNumber':{
                    nhsNumber: true,
                    required: true
                },
                'commandInstance.consentFormTemplate':
                {
                    ShouldNotSelected: true
                }
            },
            highlight: function(element) {
                $(element).closest('.form-group').addClass('has-error');
            },
            unhighlight: function(element) {
                $(element).closest('.form-group').removeClass('has-error');
            }
        });

    }

    $(function(){
        applyFormValidation();
    });

</g:javascript>