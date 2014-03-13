
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Consent Forms</title>

</head>

<body>

<div class="container">

    <div class="row">
        <div class="col-md-12 PageMainPanel">

            <div class="panel panel-primary" >
                <div class="panel-heading">

                    <h3 class="panel-title">Search Consent Form</h3>
                </div>
                <div class="panel-body">
                    <div class="col-md-12">
                        <div class="panel panel-primary PageMainPanel">

                           <g:form role="form" action="find" controller="ConsentForm" params="">
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="col-md-4 ">
                                            <div class="form-group">
                                                <label for="nhsNumber">NHS Number</label>
                                                <g:textField  class="form-control" tabindex="1"
                                                              id="nhsNumber" name="nhsNumber"
                                                              value="${params.nhsNumber}"
                                                              placeholder="NHS number like 1234567890"/>
                                            </div>



                                            <div class="form-group">
                                                <label for="consentDateFrom">Consent Date From</label>
                                                <br>
                                                <g:datePicker class="form-control" id="consentDateFrom" tabindex="4"
                                                              name="consentDateFrom"
                                                              value="${params.consentDateFrom}"
                                                              precision="day"/>
                                            </div>




                                            <div class="form-group">
                                                <input type="submit"  class="btn btn-primary"  value="Search" tabindex="6">
                                            </div>
                                        </div>
                                        <div class="col-md-4 ">


                                            <div class="form-group">
                                                <label for="hospitalNumber">Hospital Number</label>
                                                <g:textField
                                                        class="form-control" tabindex="2"
                                                        name="hospitalNumber"  id="hospitalNumber"
                                                        value="${params.hospitalNumber}"
                                                        placeholder="Hospital Number"/>
                                            </div>


                                            <div class="form-group">
                                                <label for="consentDateTo">Consent Date To</label>
                                                <br>
                                                <g:datePicker class="form-control" id="consentDateTo" tabindex="5"
                                                              name="consentDateTo"
                                                              value="${params.consentDateTo}"
                                                              precision="day"/>
                                            </div>





                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="consentTakerName">Consent Taker</label>
                                                <g:textField
                                                        class="form-control" tabindex="3"
                                                        name="consentTakerName"  id="consentTakerName"
                                                        value="${params.consentTakerName}"
                                                        placeholder="Consent Taker Name"/>

                                            </div>


                                        </div>
                                    </div>

                                </div>
                           </g:form>

                            <div style="padding: 2px;">
                                <div class="table-responsive">
                                    <table class="table  table-hover table-bordered">
                                        <thead>
                                        <tr>
                                            <th>NHS Number</th>
                                            <th>Hospital Number</th>
                                            <th>Form Status</th>
                                            <th>Form Type</th>
                                            <th>Upload Date</th>
                                            <th>Consent Date</th>
                                            <th>Consent Taker</th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                        <tbody>
                                            <g:each in="${consentForms}" var="consentForm" status="index">
                                                <tr>

                                                    <td>${consentForm?.patient?.nhsNumber}</td>
                                                    <td>${consentForm?.patient?.hospitalNumber}</td>
                                                    <td>${consentForm?.formStatus}</td>
                                                    <td>${consentForm?.template?.namePrefix}</td>
                                                    <td>
                                                        <g:formatDate format="yyyy-MM-dd HH:mm" date="${consentForm?.attachedFormImage?.dateOfUpload}"/>
                                                    </td>

                                                    <td>
                                                        <g:formatDate format="yyyy-MM-dd" date="${consentForm?.consentDate}"/>
                                                    </td>

                                                    <td>${consentForm?.consentTakerName}</td>

                                                    <td>
                                                        <g:link action="show" id="${consentForm?.id}" controller="consentFormCompletion" target="blank">
                                                            <button type="button" class="btn btn-success btn-sm" style="width:50px">View</button>
                                                        </g:link>
                                                    </td>

                                                </tr>
                                            </g:each>
                                    </tbody>
                                    </table>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
         </div>
    </div>
</div>

              </body>
             </html>




