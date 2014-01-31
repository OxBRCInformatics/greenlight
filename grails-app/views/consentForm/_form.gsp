<%@ page import="uk.ac.ox.brc.greenlight.ConsentFormTemplateService; uk.ac.ox.brc.greenlight.ConsentForm; uk.ac.ox.brc.greenlight.ConsentForm" %>
<%@ page import="uk.ac.ox.brc.greenlight.Patient" %>
<%@ page import="uk.ac.ox.brc.greenlight.Attachment" %>

<div class="col-md-12">
    <div class="panel panel-primary PageMainPanel">
        <div class="panel-body">
            <div class="col-md-4 ">
                <div class="form-group">
                    <g:hiddenField name="patient.id" value="${patient?.id}"></g:hiddenField>
                    <g:hiddenField name="consentForm.id" value="${consentForm?.id}"></g:hiddenField>
                    <g:hiddenField name="consentForm.id" value="${consentForm?.id}"></g:hiddenField>


                    <div class="form-group">
                        <label for="patient.nhsNumber" class="required">NHS Number</label>
                        <g:textField
                                class="form-control  ${hasErrors(bean: patient, field: 'nhsNumber', 'invalidInput')}"
                                id="patient.nhsNumber" name="patient.nhsNumber"
                                value="${patient?.nhsNumber}"
                                placeholder="NHS number  NNN-NNN-NNNN"/>
                    </div>

                    <label for="patient.givenName" class="required">Given Name</label>
                    <g:textField
                            class="form-control ${hasErrors(bean: patient, field: 'givenName', 'invalidInput')}"
                            name="patient.givenName" value="${patient?.givenName}"
                            placeholder="Given Name"/>

                </div>
                <div class="form-group">
                    <label for="patient.familyName" class="required">Family Name</label>
                    <g:textField
                            class="form-control  ${hasErrors(bean: patient, field: 'familyName', 'invalidInput')}"
                            id="patient.familyName" name="patient.familyName"
                            value="${patient?.familyName}"
                            placeholder="Family Name"/>
                </div>
                <div class="form-group">
                    <label for="consentForm.consentTakerName" class="required">Clinician Name</label>
                    <g:textField name="consentForm.consentTakerName" id="consentForm.consentTakerName"
                                 class="form-control  ${hasErrors(bean: consentForm, field: 'consentTakerName', 'invalidInput')}"
                                 value="${consentForm?.consentTakerName}"
                                 placeholder="Enter Consent Taker's Name"/>
                </div>
                <div class="form-group">
                    <label for="dateOfBirth">Date of Birth</label>
                    <g:datePicker class="form-control" id="dateOfBirth" name="patient.dateOfBirth"
                                  value="${patient?.dateOfBirth}"
                                  placeholder="Date of Birth"
                                  precision="day"/>
                </div>
                <div class="form-group">
                    <label for="patient.hospitalNumber" class="required">Hospital Number</label>
                    <g:textField
                            class="form-control  ${hasErrors(bean: patient, field: 'hospitalNumber', 'invalidInput')}"
                            id="patient.nhsNumber" name="patient.hospitalNumber"
                            value="${patient?.hospitalNumber}"
                            placeholder="Hospital Number"/>
                </div>
                <div class="form-group">
                    <label for="ConsentDate">Consent Date</label>
                    <g:datePicker class="form-control" id="ConsentDate"
                                  name="consentForm.consentDate"
                                  value="${consentForm?.consentDate}"
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
            <div class="col-md-6">
                <g:hiddenField name="consentForm.id" value="${consentForm?.id}"></g:hiddenField>
                <img id="consentForm.scannedForm" style="margin: 4px; width:100%;height:100%;"
                     class="Photo"
                     src="${createLink(controller: 'attachment', action: 'viewImage', id: "${consentForm?.id}")}"/>
            </div>
            <div class="col-md-6">

                <label for="ConsentForm.template" class="required">Consent Form Type</label>
                <g:select id="" name="ConsentForm?.template"
                          from="${ConsentFormTemplateService.getAll()}"
                          optionKey="id"
                          optionValue="name" >
                </g:select>

                <g:if test="${!consentForm?.responses?.isEmpty()}">
                    <ul class="list-group">
                        <g:each in="${consentForm?.responses}" var="answer" status="index">
                            <li class="list-group-item">
                                <g:checkBox type="checkbox"
                                            name="consentFormAnswers.${index}"
                                            checked="${answer}" >
                                </g:checkBox>
                                <g:if test="${!consentForm?.responses?.isEmpty()}">${consentForm?.responses[index]}</g:if>
                            </li>
                        </g:each></ul>
                </g:if>
            </div>
        </div>
    </div>
</div>

