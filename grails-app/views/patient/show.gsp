
<%@ page import="uk.ac.ox.brc.greenlight.Patient" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'patient.label', default: 'Patient')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-patient" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-patient" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list patient">
			
				<g:if test="${patientInstance?.givenName}">
				<li class="fieldcontain">
					<span id="givenName-label" class="property-label"><g:message code="patient.givenName.label" default="Given Name" /></span>
					
						<span class="property-value" aria-labelledby="givenName-label"><g:fieldValue bean="${patientInstance}" field="givenName"/></span>
					
				</li>
				</g:if>
			

			
				<g:if test="${patientInstance?.dateOfBirth}">
				<li class="fieldcontain">
					<span id="dateOfBirth-label" class="property-label"><g:message code="patient.dateOfBirth.label" default="Date Of Birth" /></span>
					
						<span class="property-value" aria-labelledby="dateOfBirth-label"><g:formatDate date="${patientInstance?.dateOfBirth}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientInstance?.familyName}">
				<li class="fieldcontain">
					<span id="familyName-label" class="property-label"><g:message code="patient.familyName.label" default="Family Name" /></span>
					
						<span class="property-value" aria-labelledby="familyName-label"><g:fieldValue bean="${patientInstance}" field="familyName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientInstance?.gender}">
				<li class="fieldcontain">
					<span id="gender-label" class="property-label"><g:message code="patient.gender.label" default="Gender" /></span>
					
						<span class="property-value" aria-labelledby="gender-label"><g:fieldValue bean="${patientInstance}" field="gender"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${patientInstance?.hospitalNumber}">
				<li class="fieldcontain">
					<span id="hospitalNumber-label" class="property-label"><g:message code="patient.hospitalNumber.label" default="Hospital Number" /></span>
					
						<span class="property-value" aria-labelledby="hospitalNumber-label"><g:fieldValue bean="${patientInstance}" field="hospitalNumber"/></span>
					
				</li>
				</g:if>
			

			
				<g:if test="${patientInstance?.nhsNumber}">
				<li class="fieldcontain">
					<span id="nhsNumber-label" class="property-label"><g:message code="patient.nhsNumber.label" default="Nhs Number" /></span>
					
						<span class="property-value" aria-labelledby="nhsNumber-label"><g:fieldValue bean="${patientInstance}" field="nhsNumber"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:patientInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${patientInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('Are you sure?');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
