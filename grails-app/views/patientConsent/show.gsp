
<%@ page import="uk.ac.ox.brc.greenlight.PatientConsent" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'patientConsent.label', default: 'PatientConsent')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-patientConsent" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-patientConsent" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list patientConsent">
			
				<g:if test="${patientConsentInstance?.consentForm}">
				<li class="fieldcontain">
					<span id="consentForm-label" class="property-label"><g:message code="patientConsent.consentForm.label" default="Consent Form" /></span>
					
						<span class="property-value" aria-labelledby="consentForm-label"><g:link controller="consentForm" action="show" id="${patientConsentInstance?.consentForm?.id}">${patientConsentInstance?.consentForm?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientConsentInstance?.answer1}">
				<li class="fieldcontain">
					<span id="answer1-label" class="property-label"><g:message code="patientConsent.answer1.label" default="Answer1" /></span>
					
						<span class="property-value" aria-labelledby="answer1-label"><g:formatBoolean boolean="${patientConsentInstance?.answer1}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientConsentInstance?.answer10}">
				<li class="fieldcontain">
					<span id="answer10-label" class="property-label"><g:message code="patientConsent.answer10.label" default="Answer10" /></span>
					
						<span class="property-value" aria-labelledby="answer10-label"><g:formatBoolean boolean="${patientConsentInstance?.answer10}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientConsentInstance?.answer2}">
				<li class="fieldcontain">
					<span id="answer2-label" class="property-label"><g:message code="patientConsent.answer2.label" default="Answer2" /></span>
					
						<span class="property-value" aria-labelledby="answer2-label"><g:formatBoolean boolean="${patientConsentInstance?.answer2}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientConsentInstance?.answer3}">
				<li class="fieldcontain">
					<span id="answer3-label" class="property-label"><g:message code="patientConsent.answer3.label" default="Answer3" /></span>
					
						<span class="property-value" aria-labelledby="answer3-label"><g:formatBoolean boolean="${patientConsentInstance?.answer3}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientConsentInstance?.answer4}">
				<li class="fieldcontain">
					<span id="answer4-label" class="property-label"><g:message code="patientConsent.answer4.label" default="Answer4" /></span>
					
						<span class="property-value" aria-labelledby="answer4-label"><g:formatBoolean boolean="${patientConsentInstance?.answer4}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientConsentInstance?.answer5}">
				<li class="fieldcontain">
					<span id="answer5-label" class="property-label"><g:message code="patientConsent.answer5.label" default="Answer5" /></span>
					
						<span class="property-value" aria-labelledby="answer5-label"><g:formatBoolean boolean="${patientConsentInstance?.answer5}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientConsentInstance?.answer6}">
				<li class="fieldcontain">
					<span id="answer6-label" class="property-label"><g:message code="patientConsent.answer6.label" default="Answer6" /></span>
					
						<span class="property-value" aria-labelledby="answer6-label"><g:formatBoolean boolean="${patientConsentInstance?.answer6}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientConsentInstance?.answer7}">
				<li class="fieldcontain">
					<span id="answer7-label" class="property-label"><g:message code="patientConsent.answer7.label" default="Answer7" /></span>
					
						<span class="property-value" aria-labelledby="answer7-label"><g:formatBoolean boolean="${patientConsentInstance?.answer7}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientConsentInstance?.answer8}">
				<li class="fieldcontain">
					<span id="answer8-label" class="property-label"><g:message code="patientConsent.answer8.label" default="Answer8" /></span>
					
						<span class="property-value" aria-labelledby="answer8-label"><g:formatBoolean boolean="${patientConsentInstance?.answer8}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientConsentInstance?.answer9}">
				<li class="fieldcontain">
					<span id="answer9-label" class="property-label"><g:message code="patientConsent.answer9.label" default="Answer9" /></span>
					
						<span class="property-value" aria-labelledby="answer9-label"><g:formatBoolean boolean="${patientConsentInstance?.answer9}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientConsentInstance?.clinicianName}">
				<li class="fieldcontain">
					<span id="clinicianName-label" class="property-label"><g:message code="patientConsent.clinicianName.label" default="Clinician Name" /></span>
					
						<span class="property-value" aria-labelledby="clinicianName-label"><g:fieldValue bean="${patientConsentInstance}" field="clinicianName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientConsentInstance?.consentStatus}">
				<li class="fieldcontain">
					<span id="consentStatus-label" class="property-label"><g:message code="patientConsent.consentStatus.label" default="Consent Status" /></span>
					
						<span class="property-value" aria-labelledby="consentStatus-label"><g:fieldValue bean="${patientConsentInstance}" field="consentStatus"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientConsentInstance?.date}">
				<li class="fieldcontain">
					<span id="date-label" class="property-label"><g:message code="patientConsent.date.label" default="Date" /></span>
					
						<span class="property-value" aria-labelledby="date-label"><g:formatDate date="${patientConsentInstance?.date}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientConsentInstance?.patient}">
				<li class="fieldcontain">
					<span id="patient-label" class="property-label"><g:message code="patientConsent.patient.label" default="Patient" /></span>
					
						<span class="property-value" aria-labelledby="patient-label"><g:link controller="patient" action="show" id="${patientConsentInstance?.patient?.id}">${patientConsentInstance?.patient?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:patientConsentInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${patientConsentInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
