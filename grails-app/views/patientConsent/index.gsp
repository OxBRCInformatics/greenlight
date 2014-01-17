
<%@ page import="uk.ac.ox.brc.greenlight.PatientConsent" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'patientConsent.label', default: 'PatientConsent')}" />
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
					
						<g:sortableColumn property="answer1" title="${message(code: 'patientConsent.answer1.label', default: 'Answer1')}" />
					
						<g:sortableColumn property="answer10" title="${message(code: 'patientConsent.answer10.label', default: 'Answer10')}" />
					
						<g:sortableColumn property="answer2" title="${message(code: 'patientConsent.answer2.label', default: 'Answer2')}" />
					
						<g:sortableColumn property="answer3" title="${message(code: 'patientConsent.answer3.label', default: 'Answer3')}" />
					
						<g:sortableColumn property="answer4" title="${message(code: 'patientConsent.answer4.label', default: 'Answer4')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${patientConsentInstanceList}" status="i" var="patientConsentInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${patientConsentInstance.id}">${fieldValue(bean: patientConsentInstance, field: "consentForm")}</g:link></td>
					
						<td><g:formatBoolean boolean="${patientConsentInstance.answer1}" /></td>
					
						<td><g:formatBoolean boolean="${patientConsentInstance.answer10}" /></td>
					
						<td><g:formatBoolean boolean="${patientConsentInstance.answer2}" /></td>
					
						<td><g:formatBoolean boolean="${patientConsentInstance.answer3}" /></td>
					
						<td><g:formatBoolean boolean="${patientConsentInstance.answer4}" /></td>
					
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
