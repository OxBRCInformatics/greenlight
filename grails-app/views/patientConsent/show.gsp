<%@ page import="uk.ac.ox.brc.greenlight.PatientConsent; uk.ac.ox.brc.greenlight.Patient" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="mainBootstrap">
    <title></title>
</head>

<body>

<div class="col-md-12 ">
    <div class="panel panel-primary PageMainPanel">
        <div class="panel-heading">Consent Form</div>

        <div class="panel-body">
            %{--<g:hasErrors>--}%
            %{--<g:eachError><p>${it}</p></g:eachError>--}%
            %{--</g:hasErrors>--}%
            <div class="col-md-12">
                <g:if test="${flash.created}">
                    <div class="alert alert-success">${flash.created}</div>
                </g:if>
                <g:elseif test="${flash.error}">
                    <div class="alert alert-danger">${flash.error}</div>
                </g:elseif>
            </div>
            <g:form role="form" >
                <g:render template="form"/>
                <div class="col-md-12">


                    <g:link   action="delete" controller="patientConsent" id="${patientConsent?.id}"  onclick="return confirm('Are you sure?');" >
                        <button type="button" class="btn  btn-danger btn-sm" style="width:50px">Delete</button>
                    </g:link>

                    <g:link   action="edit" controller="patientConsent" id="${patientConsent?.id}" >
                        <button type="button" class="btn  btn-primary btn-sm" style="width:50px">Edit</button>
                    </g:link>


                </div>
            </g:form>
        </div>
    </div>
</div>
</body>
</html>