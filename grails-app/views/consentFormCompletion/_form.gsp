<%@ page import="uk.ac.ox.brc.greenlight.ConsentFormTemplate; uk.ac.ox.brc.greenlight.ConsentFormTemplateService; uk.ac.ox.brc.greenlight.ConsentForm; uk.ac.ox.brc.greenlight.ConsentForm" %>
<%@ page import="uk.ac.ox.brc.greenlight.Patient" %>
<%@ page import="uk.ac.ox.brc.greenlight.Attachment" %>




<g:hiddenField name="commandInstance.attachmentId" value="${commandInstance?.attachment?.id}"></g:hiddenField>
<g:hiddenField name="commandInstance.patient.id" value="${commandInstance?.patient?.id}"></g:hiddenField>
<g:hiddenField name="commandInstance.consentForm.id" id="commandInstance.consentForm.id" value="${commandInstance?.consentForm?.id}"></g:hiddenField>



<g:set var="responses" value="${commandInstance?.consentForm?.responses}"></g:set>



<div class="span12">
    <div class="row-fluid">

        <div class="span4">
            <div class="form-group">
                <label for="commandInstance.patient.nhsNumber" class="required">                <span  class="bootstrapTooltip" data-toggle="tooltip" data-original-title="Load patient demographic" onclick="loadDemographic()"><i class="icon-refresh  icon-green"  style="cursor: pointer"></i>  </span>
                    NHS Number</label>
                <g:textField
                        class="form-control  ${hasErrors(bean: patient, field: 'nhsNumber', 'invalidInput')}"
                        id="commandInstance.patient.nhsNumber" name="commandInstance.patient.nhsNumber"
                        value="${commandInstance?.patient?.nhsNumber}"
                        placeholder="NHS number like 1234567890"  />
            </div>

            <div class="form-group">
                <label for="commandInstance.patient.hospitalNumber" class="required">Hospital Number</label>
                <g:textField
                        class="form-control  ${hasErrors(bean: patient, field: 'hospitalNumber', 'invalidInput')}"
                        id="commandInstance.patient.hospitalNumber" name="commandInstance.patient.hospitalNumber"
                        value="${commandInstance?.patient?.hospitalNumber}"
                        placeholder="Hospital Number"  />
            </div>

            <div class="form-group">
                <label for="commandInstance.patient.familyName" class="required">Last Name</label>
                <g:textField
                        class="form-control  ${hasErrors(bean: patient, field: 'familyName', 'invalidInput')}"
                        id="commandInstance.patient.familyName" name="commandInstance.patient.familyName"
                        value="${commandInstance?.patient?.familyName}"
                        placeholder="Last Name" />
            </div>
        </div>

        <div class="span4">

            <div class="form-group">
                <label for="commandInstance.patient.givenName" class="required">First Name</label>
                <g:textField
                        class="form-control ${hasErrors(bean: patient, field: 'givenName', 'invalidInput')}"
                        name="commandInstance.patient.givenName" value="${commandInstance?.patient?.givenName}"
                        placeholder="First Name" />
            </div>

            <div class="form-group">
                <label for="commandInstance.patient.dateOfBirth">Date of Birth</label>
                <g:datePicker class="form-control" id="commandInstance.patient.dateOfBirth"
                              name="commandInstance.patient.dateOfBirth"
                              relativeYears="${dateOfBirthMax}"
                              value="${commandInstance?.patient?.dateOfBirth}"
                              placeholder="Date of Birth"
                              precision="day" />
            </div>

            <div class="form-group">
                <label for="commandInstance.consentForm.consentTakerName" class="required">Consent Taker Name</label>
                <g:textField name="commandInstance.consentForm.consentTakerName"
                             id="commandInstance.consentForm.consentTakerName"
                             class="form-control  ${hasErrors(bean: consentForm, field: 'consentTakerName', 'invalidInput')}"
                             value="${commandInstance?.consentForm?.consentTakerName}"
                             placeholder="Enter Consent Taker's Name" />
            </div>
        </div>

        <div class="span4">
            <div class="form-group">
                <label for="commandInstance.consentForm.consentDate">Consent Date</label>
                <g:datePicker class="form-control"
                              id="commandInstance.consentForm.ConsentDate"
                              name="commandInstance.consentForm.consentDate"
                              relativeYears="${consentDateMax}"
                              value="${commandInstance?.consentForm?.consentDate}"
                              placeholder="Consent Date"
                              precision="day" />
            </div>

            <div class="form-group">
                <label for="commandInstance.consentForm.formID" class="required">Form Id<span
                        id="templatePrefix"></span></label>

                <g:textField
                        class="form-control  ${hasErrors(bean: consentForm, field: 'formID', 'invalidInput')}"
                        id="commandInstance.consentForm.formID" name="commandInstance.consentForm.formID"
                        value="${commandInstance?.consentForm?.formID}"
                        placeholder="Consent Form Id like GEN12345"
                         />

            </div>


            <div class="form-group">
                <label for="commandInstance.consentForm.comment">Comment</label>
                <g:textArea
                        class="form-control  ${hasErrors(bean: consentForm, field: 'comment', 'invalidInput')}"
                        id="commandInstance.consentForm.comment" name="commandInstance.consentForm.comment"
                        value="${commandInstance?.consentForm?.comment}"
                        placeholder="Comment"/>
            </div>
        </div>

    </div>
</div>

<div class="row">
    <div class="span12">
        <div class="row">
            <div class="span8">

                <g:if test="${commandInstance?.attachment}">

                        <div  id="scrollerDiv">
                            <a href="${createLink(action: 'show', controller: 'attachment', id: commandInstance?.attachment?.id)}" target="_blank">
                                <img id="scannedForm" style="margin: 4px; width: 100%;height: 100%" class="Photo" src="${resource(dir:'attachments', file: commandInstance?.attachment?.id + '.jpg')}" />
                            </a>
                        </div>

                </g:if>
            </div>
            <div class="span4">
                <div class="form-group">
                    <label>Consent Form Type</label>
                    %{--<select id="consentFormTemplate"  name="consentFormTemplate">--}%
                    <g:select id="commandInstance.consentFormTemplate"
                              style="font-size:12px;width: 250px;"
                              name="commandInstance.consentFormTemplateId" class="form-control"
                              value="${commandInstance?.consentForm?.template?.id}"
                              from="${ConsentFormTemplate.list()}"
                              optionKey="id"
                              optionValue="${name}"
                              noSelection="${['-1': 'Select one ...']}"
                              onchange="${
                                  remoteFunction(
                                          action: 'getQuestions',
                                          controller: 'ConsentFormTemplate',
                                          params: '\'templateId=\' + this.value',
                                          update: [success: 'questionList', failure: ''],
                                          onSuccess: "formTemplateChanged()"
                                  )}"></g:select>
                </div>

                <div class="form-group">
                    <label for="commandInstance.consentForm.formStatus">Form Status</label>
                    <g:select id="commandInstance.consentForm.formStatus"
                              name="commandInstance.consentForm.formStatus"
                              class="form-control"
                              value="${commandInstance?.consentForm?.formStatus}"
                              from="${ConsentForm?.FormStatus?.values()}"
                              optionKey="key"
                              optionValue="value"/>
                </div>


                <div class="form-group" id="questionList">
                    <g:if test="${commandInstance?.consentForm}">
                        <g:render template="/consentFormTemplate/getQuestions"
                                  model="[questions: commandInstance?.template?.questions,
                                          responses: commandInstance?.consentForm?.responses]">
                        </g:render>
                    </g:if>
                </div>

            </div>
        </div>
    </div>
</div>


<g:javascript>

    function formTemplateChanged()
    {
       $('.bootstrapTooltip').tooltip()

       var tempId= $("select[id='commandInstance.consentFormTemplate']").find(":selected").val()

       if(tempId==-1)
        {
            blankForm();
            return;
        }
        var link= "${createLink(action: 'show', controller: 'ConsentFormTemplate')}";
        $.ajax({
           type: 'POST',
           url: link+"/"+tempId+".json",
           success: function (data) {
                   $('#templatePrefix').html(" ("+data.namePrefix+")");
                   var placeholder= "Consent Form Id like "+data.namePrefix+"12345";
                   $("input[id='commandInstance.consentForm.formID']").attr('placeholder',placeholder);
                   //$("input[id='commandInstance.consentForm.formID']").val(data.namePrefix);

                   $("input[id='commandInstance.consentForm.formID']").rules('remove');
                   var rule = new RegExp("^"+data.namePrefix+"\\d{5}$");
                   $("input[id='commandInstance.consentForm.formID']").rules('add',{
                            required: true,
                            regex:rule ,
                            checkDuplicateFormId:true
                   });

            }
        });
    }

    function blankForm()
    {
        $('#templatePrefix').html(" ");
        $("input[id='commandInstance.consentForm.formID']").attr('placeholder',"Consent Form Id like GEN12345");
        $("input[id='commandInstance.consentForm.formID']").val('')

        $("input[id='commandInstance.consentForm.formID']").rules('remove');
        var rule = new RegExp("^[a-zA-Z]{3}\\d{5}$");
        $("input[id='commandInstance.consentForm.formID']").rules('add',{required: true,regex:rule });
    }

    function applyFormValidation()
    {
        $('form').validate({
            rules: {
                'commandInstance.patient.nhsNumber':{
                    regex: /^\d{10}$/,
                    required:true
                },
                'commandInstance.consentForm.formID':{
                    required:true,
                    regex:/^[a-zA-Z]{3}\d{5}$/,
                    checkDuplicateFormId:true
                }
            },

            highlight: function(element) {
                $(element).closest('.form-group').addClass('has-error');
            },
            unhighlight: function(element) {
                $(element).closest('.form-group').removeClass('has-error');
            }
        });

          $("select[id='commandInstance.consentFormTemplate']").rules("add",{
                     required: true,
                     formElementSelected: true
                });

    }


       function getConsentIdByFormId()
    {
        var ajaxLink= "${createLink(action:'checkFormId', controller:'ConsentFormCompletion')}";
        var returnedConsentId
        $.ajax({
            type: 'POST',
            url: ajaxLink+"/"+value+".json",
            async:false,
            success: function (data) {
                    debugger;
                    returnedConsentId = data.consentFormId
                    return
                    }
                })

        return returnedConsentId;
    }


    function loadDemographic(){
     var ajaxLink= "${createLink(action:'findDemographic', controller:'ConsentFormCompletion')}";
        var patient
        $.ajax({
            type: 'POST',
            url: ajaxLink+".json?nhsNumber="+$("input[id='commandInstance.patient.nhsNumber']").val(),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            async:true,
            success: function (data) {
                    patient = data.patient

                    if(patient){
                        $("input[id='commandInstance.patient.hospitalNumber']").val(patient.ACTIVE_MRN)
                        $("input[id='commandInstance.patient.givenName']").val(patient.GIVENNAME)
                        $("input[id='commandInstance.patient.familyName']").val(patient.FAMILYNAME)
                        $("select[id='commandInstance.patient.dateOfBirth_day']").val(patient.DOB_day)
                        //as in GSP calender, months are considered from [1..12], we should add +1 and year-1900 as it is originally a Timestamp
                        $("select[id='commandInstance.patient.dateOfBirth_month']").val(patient.DOB_month+1)
                        $("select[id='commandInstance.patient.dateOfBirth_year']").val(patient.DOB_year-1900)

                        //As it is found, now set focus to Consent Taker field
                        $("input[id='commandInstance.consentForm.consentTakerName']").focus()
                        return
                        }
                    },
            error:function(err){

            }

                })

                if(patient){
                    return true;
                    }
                 else{
                    return false;
                    }

    }

    function parseGELBarcode(){
     var ajaxLink= "${createLink(action:'parseGELBarcode', controller:'ConsentFormCompletion')}";
        var GELParticipant
        $.ajax({
            type: 'POST',
            url: ajaxLink+".json?GELBarcode="+$("input[id='commandInstance.patient.nhsNumber']").val(),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            async:true,
            success: function (data) {

                    GELParticipant = data.GELParticipant

                    if(GELParticipant.success == true){
                        $("input[id='commandInstance.patient.nhsNumber']").val(GELParticipant.result.NHSNumber)
                        $("input[id='commandInstance.patient.hospitalNumber']").val(GELParticipant.result.participantDetails.hospitalNumber)
                        $("input[id='commandInstance.patient.givenName']").val(GELParticipant.result.participantDetails.forenames)
                        $("input[id='commandInstance.patient.familyName']").val(GELParticipant.result.participantDetails.surname)
                        $("select[id='commandInstance.patient.dateOfBirth_day']").val(GELParticipant.result.participantDetails.dobDate)
                        $("select[id='commandInstance.patient.dateOfBirth_month']").val(GELParticipant.result.participantDetails.dobMonth)
                        $("select[id='commandInstance.patient.dateOfBirth_year']").val(GELParticipant.result.participantDetails.dobYear)

                        $("input[id='commandInstance.consentForm.nhsNumber']").focus()
                        $("input[id='commandInstance.consentForm.consentTakerName']").focus()
                        return true
                    }
                    else{
                        alert(GELParticipant.error)
                        $("input[id='commandInstance.consentForm.nhsNumber']").focus()
                        return false
                       }
                    },
            error:function(err){
                    return false
            }

          })
    }


   function leaveNHSNumber(){
        //check if it is GELBarcode
         var nhsNumberValue = $("input[id='commandInstance.patient.nhsNumber']").val()
         if(nhsNumberValue.length > 10 && (nhsNumberValue.indexOf("|")>-1 || nhsNumberValue.indexOf("~")>-1) && nhsNumberValue.indexOf("8018") == 0){
            if(parseGELBarcode() == true){
                loadDemographic()
                $("input[id='commandInstance.patient.consentTakerName']").focus()
            }

         }else{
            loadDemographic();
            $("input[id='commandInstance.patient.consentTakerName']").focus();
         }
   }

    $(function(){

        //set default focus on NHSNumber
        $("input[id='commandInstance.patient.nhsNumber']").focus()

         $("input[id='commandInstance.patient.nhsNumber']").blur(function(){
                leaveNHSNumber();
          });


        $("input[id='commandInstance.patient.nhsNumber']").keypress(function(e) {
              //Enter key
              if (e.which == 13) {
               leaveNHSNumber();
                return false;
              }
            });

        //Disable enter for Form Submission
         $("form").keypress(function(e) {
              //Enter key
              if (e.which == 13) {
                return false;
              }
            });




        $.validator.addMethod(
                    "checkDuplicateFormId",
                        function(value, element) {
                            var actualConsentId   =$("input[id='commandInstance.consentForm.id']").val()
                            var consentFormLink = "${createLink(action:'show', controller:'ConsentFormCompletion')}";
                            var returnedConsentId = ""
                            var ajaxLink= "${createLink(action:'checkFormId', controller:'ConsentFormCompletion')}";
                            var returnedConsentId
                            $.ajax({
                                type: 'POST',
                                url: ajaxLink+"/"+value+".json",
                                async:false,
                                success: function (data) {
                                        returnedConsentId = data.consentFormId
                                        return
                                        }
                                    })

                            consentFormLink = consentFormLink + "/"+returnedConsentId;
                            //add this element and use it again in error message
                            $(element).data('consentFormLink',consentFormLink);


                            //FormId is NOT used before
                            if(returnedConsentId == -1)
                                    return  true;

                            //FormId is used before
                            //and now it is a new consentForm
                            if(returnedConsentId != -1 && actualConsentId == null)
                                    return false;

                            //FormId is used before
                            //and now it is an edit mode for a consentForm
                            if(returnedConsentId != -1 && returnedConsentId != actualConsentId)
                                     return false

                            return true
                          } ,
                           function(params, element) {
                               return "<a target='blank' href='"+$(element).data('consentFormLink')+"'>Form Id<a/> already used!";}
                         );

       applyFormValidation();






 });


    //As we need to change css of an image, we need to wait and act after it is loaded
    //as $() jquery runs when DOM fully loaded, but
    //$(window).load() executes when all other things are loaded as well, such as the images
    $(window).load(function() {
        if( $("#scrollerDiv img").height() > 1000){
            $("#scrollerDiv").css( "overflow", "scroll" );
            $("#scrollerDiv").css( "max-height", "700px" );
        }
    });

</g:javascript>
