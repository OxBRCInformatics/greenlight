<%@ page import="uk.ac.ox.brc.greenlight.ConsentForm; uk.ac.ox.brc.greenlight.Patient" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Consent Form</title>
</head>

<body>

<div class="span12 ">
    <div class="panel panel-primary PageMainPanel">
        <div class="panel-heading">Consent Form</div>

        <div class="panel-body">
            <g:hasErrors>
            <g:eachError><p>${it}</p></g:eachError>
            </g:hasErrors>
                <g:if test="${flash.created}">
                    <div class="alert alert-success">${flash.created}</div>
                </g:if>
                <g:elseif test="${flash.error}">
                    <g:if test="${flash.annotatedBefore}">
                        <div class="alert alert-danger">
                            This <a href="${flash.annotatedBeforeLink}">form</a> is annotated!
                        </div>
                    </g:if>
                    <g:else>
                        <div class="alert alert-danger">${flash.error}</div>
                    </g:else>
                </g:elseif>

            <g:set var="dateOfBirthMax" value="[-100..0]" />
            <g:set var="consentDateMax" value="[-100..0]" />

            <g:form role="form" action="save" controller="consentFormCompletion" >
                <g:render template="form"/>

                <button type="submit" class="btn  btn-primary">Save</button>

            </g:form>
        </div>
    </div>
</div>
</body>
</html>