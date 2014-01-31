<%@ page import="uk.ac.ox.brc.greenlight.Attachment" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="mainBootstrap">
		<g:set var="entityName" value="${message(code: 'attachedFormImage.label', default: 'Attachment')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-consentForm" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-consentForm" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
						<th><g:message code="consentForm.patientConsent.label" default="Patient Consent" /></th>
						<g:sortableColumn property="scannedForm" title="${message(code: 'attachedFormImage.scannedForm.label', default: 'Scanned Form')}" />
						<g:sortableColumn property="dateOfScan" title="${message(code: 'attachedFormImage.dateOfScan.label', default: 'Date Of Scan')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${consentFormInstanceList}" status="i" var="consentFormInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link action="show" id="${consentFormInstance.id}">${fieldValue(bean: consentFormInstance, field: "attachedFormImage")}</g:link></td>
						<td>${fieldValue(bean: consentFormInstance, field: "scannedForm")}</td>
						<td><g:formatDate date="${consentFormInstance.dateOfScan}" /></td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${consentFormInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
