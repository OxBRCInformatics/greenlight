<%@ page import="uk.ac.ox.brc.greenlight.ConsentFormTemplate; uk.ac.ox.brc.greenlight.ConsentForm; uk.ac.ox.brc.greenlight.Patient" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title></title>
</head>

<body>

<div class="col-md-12 ">
    <div class="panel panel-primary PageMainPanel">
        <div class="panel-heading">Consent Form</div>

        <div class="panel-body">
            <g:hasErrors>
            <g:eachError><p>${it}</p></g:eachError>
            </g:hasErrors>
            <div class="col-md-12">
                <g:if test="${flash.created}">
                    <div class="alert alert-success">${flash.created}</div>
                </g:if>
                <g:elseif test="${flash.error}">
                    <div class="alert alert-danger">${flash.error}</div>
                </g:elseif>
            </div>

            <g:form role="form" action="save" controller="consentFormCompletion" >

                <g:hiddenField name="commandInstance.attachment.id" value="${commandInstance?.attachment?.id}"></g:hiddenField>
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
                                            id="commandInstance.patient.nhsNumber" name="commandInstance.patient.hospitalNumber"
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

                                <label >Consent Form Type</label>
                                %{--<select id="consentFormTemplate"  name="consentFormTemplate">--}%
                                <g:select id="commandInstance.consentFormTemplate" name="commandInstance.consentFormTemplate" class="form-control"
                                          style="font-size:12px;"
                                          value="${commandInstance?.consentForm?.template?.id}"
                                          from="${ConsentFormTemplate.list()}"
                                          optionKey="id"
                                          optionValue="${name}"

                                ></g:select>

                                <div class="form-group">
                                    <label for="commandInstance.consentForm.formStatus">Form Status</label>
                                    <g:select id="commandInstance?.consentForm.formStatus"
                                              name="commandInstance.consentForm.formStatus"
                                              class="form-control"
                                              value="${commandInstance?.consentForm?.formStatus}"
                                              from="${ConsentForm?.FormStatus?.values()}"
                                              optionKey="key" optionValue="value"/>
                                </div>


                                <ul class="list-group">
                                    <g:each in="${commandInstance?.questions}" var="question" status="index">

                                        <li class="list-group-item">
                                            <span class="label label-primary" style="margin-right: 3px;clear:both;" >${index+1}</span>
                                            &nbsp;  ${commandInstance?.responses[index]?.answer}
                                        </li>
                                    </g:each>
                                </ul>


                                </div>
                            </div>
                        </div>
                    </div>


                        <g:link   action="delete" controller="consentFormCompletion" id="${commandInstance?.consentForm?.id}"  onclick="return confirm('Are you sure?');" >
                            <button type="button" class="btn  btn-danger btn-sm" style="width:50px">Delete</button>
                        </g:link>

                        <g:link   action="edit" controller="consentFormCompletion" id="${commandInstance?.consentForm?.id}" >
                            <button type="button" class="btn  btn-primary btn-sm" style="width:50px">Edit</button>
                        </g:link>

                </div>

            </g:form>
        </div>
    </div>
</div>
</body>
</html>