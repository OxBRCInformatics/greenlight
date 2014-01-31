
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="mainBootstrap">
    <title>Consent Forms</title>

</head>

<body>

<div class="container">

    <div class="row">
        <div class="col-md-12 PageMainPanel">

            <div class="panel panel-primary" >
                <div class="panel-heading">

                    <h3 class="panel-title">Uploaded Consent Form</h3>
                    </div>


                <div class="panel-body">
                    <table class="table  table-hover table-bordered table-responsive" >
                        <thead>
                        <tr>
                            <th>No.</th>
                            <th>Form ID</th>
                            <th>Scan Date</th>
                            <th>Assigned</th>
                            <th>NHS Number</th>
                            <th style="width:50px; "></th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${consentForms}" var="consentForm" status="index">

                            <g:set var="assigned" value="${consentForm.patientConsent!=null}"></g:set>


                            <g:if test="${assigned==true}" >
                                <tr class="success" id="consentFormRow${consentForm.id}" >
                            </g:if>
                            <g:else>
                                <tr id="consentFormRow${consentForm.id}"  >
                            </g:else>

                            <td>${index+1}</td>
                            <td>${consentForm?.id}</td>
                            <td><g:formatDate format="yyyy-MM-dd HH:mm" date="${consentForm?.dateOfScan}"/></td>
                            <g:if test="${consentForm?.patientConsent!=null}">
                                <td> Yes</td>
                                <td>${consentForm.patientConsent.patient.nhsNumber}</td>
                                <td style="  text-align: left;">
                                        <g:link action="show" id="${consentForm?.patientConsent?.id}" controller="patientConsent">
                                            <button type="button" class="btn btn-success btn-sm" style="width:50px">View</button>
                                        </g:link>
                                </td>
                            </g:if>
                            <g:else>
                                <td>No</td>
                                <td></td>
                                <td style="width:150px;text-align: left; ">
                                    <p>

                                <g:link action="create" class="linkButton" controller="patientConsent" params="[consentFormId:"${consentForm.id}"]"  >

                                        <button type="button" class="btn  btn-primary btn-sm " style="width:50px" >Fill</button>
                                </g:link>

                                        <g:remoteLink  class="btn  btn-danger btn-sm " controller="consentForm" action="delete" id="${consentForm.id}"  before="if(!confirm('Are you sure?')) return false" onSuccess="deleteRow(data,textStatus)">
                                        Delete
                                        </g:remoteLink>

                                    </p>

                                </td>
                            </g:else>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                    <g:link action="create">
                        <button type="button" class="btn  btn-primary btn-sm" id="btnAddForms" >Add Forms</button>
                    </g:link>
                </div>
            </div>

        </div>
    </div>

</div>

<g:javascript>

    function deleteRow(data,textStatus)
    {
        var element=$("#consentFormRow"+data.id);
        element.fadeOut(300, function(){ $(this).remove();});
    }

</g:javascript>



</body>
</html>




