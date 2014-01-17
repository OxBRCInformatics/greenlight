
<%@ page import="uk.ac.ox.brc.greenlight.ConsentForm" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'consentForm.label', default: 'ConsentForm')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-consentForm" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-consentForm" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list consentForm">
			
				<g:if test="${consentFormInstance?.patientConsent}">
				<li class="fieldcontain">
					<span id="patientConsent-label" class="property-label"><g:message code="consentForm.patientConsent.label" default="Patient Consent" /></span>
					
						<span class="property-value" aria-labelledby="patientConsent-label"><g:link controller="patientConsent" action="show" id="${consentFormInstance?.patientConsent?.id}">${consentFormInstance?.patientConsent?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${consentFormInstance?.scannedForm}">
				
				
				
				<li class="fieldcontain">
					<span id="scannedForm-label" class="property-label"><g:message code="consentForm.scannedForm.label" default="Scanned Form" /></span>
					<img class="Photo" style="width:100px;height:100px;" src="${createLink(controller:'consentForm', action:'viewImage', id:"${consentFormInstance.id}")}" />
				
				</li>
				</g:if>
			
				<g:if test="${consentFormInstance?.dateOfConsent}">
				<li class="fieldcontain">
					<span id="dateOfConsent-label" class="property-label"><g:message code="consentForm.dateOfConsent.label" default="Date Of Consent" /></span>
					
						<span class="property-value" aria-labelledby="dateOfConsent-label"><g:formatDate date="${consentFormInstance?.dateOfConsent}" /></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:consentFormInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${consentFormInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
