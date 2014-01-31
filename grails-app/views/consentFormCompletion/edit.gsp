<%@ page import="uk.ac.ox.brc.greenlight.ConsentForm; uk.ac.ox.brc.greenlight.Patient" %>

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

            <g:form id="consentFormCompletion" role="form" action="update" controller="consentFormCompletion" >
                <g:render template="form"/>
                <div class="col-md-12">
                    <button type="submit" class="btn  btn-primary  ">Save</button>
                </div>
            </g:form>
        </div>
    </div>
</div>
</body>
</html>