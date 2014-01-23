
<%@ page import="uk.ac.ox.brc.greenlight.Patient" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'patient.label', default: 'Patient')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-patient" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-patient" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="givenName" title="${message(code: 'patient.givenName.label', default: 'Given Name')}" />
					
						<g:sortableColumn property="dateOfBirth" title="${message(code: 'patient.dateOfBirth.label', default: 'Date Of Birth')}" />
					
						<g:sortableColumn property="familyName" title="${message(code: 'patient.familyName.label', default: 'Family Name')}" />
					
						<g:sortableColumn property="gender" title="${message(code: 'patient.gender.label', default: 'Gender')}" />
					
						<g:sortableColumn property="hospitalNumber" title="${message(code: 'patient.hospitalNumber.label', default: 'Hospital Number')}" />
					
						<g:sortableColumn property="nhsNumber" title="${message(code: 'patient.nhsNumber.label', default: 'Nhs Number')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${patientInstanceList}" status="i" var="patientInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${patientInstance.id}">${fieldValue(bean: patientInstance, field: "givenName")}</g:link></td>
					
						<td><g:formatDate date="${patientInstance.dateOfBirth}" /></td>
					
						<td>${fieldValue(bean: patientInstance, field: "familyName")}</td>
					
						<td>${fieldValue(bean: patientInstance, field: "gender")}</td>
					
						<td>${fieldValue(bean: patientInstance, field: "hospitalNumber")}</td>
					
						<td>${fieldValue(bean: patientInstance, field: "nhsNumber")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${patientInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
