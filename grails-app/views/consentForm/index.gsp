
<%@ page import="uk.ac.ox.brc.greenlight.ConsentForm" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'attachedFormImage.label', default: 'ConsentForm')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-patientConsent" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-patientConsent" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<th><g:message code="patientConsent.consentForm.label" default="Consent Form" /></th>
					
						<g:sortableColumn property="consentStatus" title="${message(code: 'attachedFormImage.consentStatus.label', default: 'Consent Status')}" />
					
						<g:sortableColumn property="answers" title="${message(code: 'attachedFormImage.answers.label', default: 'Answers')}" />
					
						<g:sortableColumn property="clinicianName" title="${message(code: 'attachedFormImage.consentTakerName.label', default: 'Clinician Name')}" />
					
						<g:sortableColumn property="consentDate" title="${message(code: 'attachedFormImage.consentDate.label', default: 'Consent Date')}" />
					
						<th><g:message code="patientConsent.patient.label" default="Patient" /></th>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${patientConsentInstanceList}" status="i" var="patientConsentInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${patientConsentInstance.id}">${fieldValue(bean: patientConsentInstance, field: "attachedFormImage")}</g:link></td>
					
						<td>${fieldValue(bean: patientConsentInstance, field: "consentStatus")}</td>
					
						<td>${fieldValue(bean: patientConsentInstance, field: "answers")}</td>
					
						<td>${fieldValue(bean: patientConsentInstance, field: "consentTakerName")}</td>
					
						<td><g:formatDate date="${patientConsentInstance.consentDate}" /></td>
					
						<td>${fieldValue(bean: patientConsentInstance, field: "patient")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${patientConsentInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
