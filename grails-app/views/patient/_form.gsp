<%@ page import="uk.ac.ox.brc.greenlight.Patient" %>



<div class="fieldcontain ${hasErrors(bean: patientInstance, field: 'givenName', 'error')} ">
	<label for="givenName">
		<g:message code="patient.givenName.label" default="Given Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="givenName" required="" value="${patientInstance?.givenName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: patientInstance, field: 'familyName', 'error')} ">
    <label for="familyName">
        <g:message code="patient.familyName.label" default="Family Name" />

    </label>
    <g:textField name="familyName" value="${patientInstance?.familyName}"/>
</div>
 

<div class="fieldcontain ${hasErrors(bean: patientInstance, field: 'dateOfBirth', 'error')}  ">
	<label for="dateOfBirth">
		<g:message code="patient.dateOfBirth.label" default="Date Of Birth" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="dateOfBirth" precision="day"  value="${patientInstance?.dateOfBirth}"  />
</div>


<div class="fieldcontain ${hasErrors(bean: patientInstance, field: 'gender', 'error')}  ">
	<label for="gender">
		<g:message code="patient.gender.label" default="Gender" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="gender"
              from="${uk.ac.ox.brc.greenlight.Patient$Gender?.values()}"
              keys="${uk.ac.ox.brc.greenlight.Patient$Gender.values()*.name()}"
              required=""
              value="${patientInstance?.gender?.name()}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: patientInstance, field: 'hospitalNumber', 'error')} ">
	<label for="hospitalNumber">
		<g:message code="patient.hospitalNumber.label" default="Hospital Number" />
		
	</label>
	<g:textField name="hospitalNumber" value="${patientInstance?.hospitalNumber}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: patientInstance, field: 'nhsNumber', 'error')} ">
	<label for="nhsNumber">
		<g:message code="patient.nhsNumber.label" default="Nhs Number" />
		
	</label>
	<g:textField name="nhsNumber" value="${patientInstance?.nhsNumber}"/>
</div>

