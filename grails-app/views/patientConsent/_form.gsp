<%@ page import="uk.ac.ox.brc.greenlight.PatientConsent" %>



<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'consentForm', 'error')} ">
	<label for="consentForm">
		<g:message code="patientConsent.consentForm.label" default="Consent Form" />
		
	</label>
	<g:select id="consentForm" name="consentForm.id" from="${uk.ac.ox.brc.greenlight.ConsentForm.list()}" optionKey="id" value="${patientConsentInstance?.consentForm?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer1', 'error')} ">
	<label for="answer1">
		<g:message code="patientConsent.answer1.label" default="Answer1" />
		
	</label>
	<g:checkBox name="answer1" value="${patientConsentInstance?.answer1}" />
</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer10', 'error')} ">
	<label for="answer10">
		<g:message code="patientConsent.answer10.label" default="Answer10" />
		
	</label>
	<g:checkBox name="answer10" value="${patientConsentInstance?.answer10}" />
</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer2', 'error')} ">
	<label for="answer2">
		<g:message code="patientConsent.answer2.label" default="Answer2" />
		
	</label>
	<g:checkBox name="answer2" value="${patientConsentInstance?.answer2}" />
</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer3', 'error')} ">
	<label for="answer3">
		<g:message code="patientConsent.answer3.label" default="Answer3" />
		
	</label>
	<g:checkBox name="answer3" value="${patientConsentInstance?.answer3}" />
</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer4', 'error')} ">
	<label for="answer4">
		<g:message code="patientConsent.answer4.label" default="Answer4" />
		
	</label>
	<g:checkBox name="answer4" value="${patientConsentInstance?.answer4}" />
</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer5', 'error')} ">
	<label for="answer5">
		<g:message code="patientConsent.answer5.label" default="Answer5" />
		
	</label>
	<g:checkBox name="answer5" value="${patientConsentInstance?.answer5}" />
</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer6', 'error')} ">
	<label for="answer6">
		<g:message code="patientConsent.answer6.label" default="Answer6" />
		
	</label>
	<g:checkBox name="answer6" value="${patientConsentInstance?.answer6}" />
</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer7', 'error')} ">
	<label for="answer7">
		<g:message code="patientConsent.answer7.label" default="Answer7" />
		
	</label>
	<g:checkBox name="answer7" value="${patientConsentInstance?.answer7}" />
</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer8', 'error')} ">
	<label for="answer8">
		<g:message code="patientConsent.answer8.label" default="Answer8" />
		
	</label>
	<g:checkBox name="answer8" value="${patientConsentInstance?.answer8}" />
</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer9', 'error')} ">
	<label for="answer9">
		<g:message code="patientConsent.answer9.label" default="Answer9" />
		
	</label>
	<g:checkBox name="answer9" value="${patientConsentInstance?.answer9}" />
</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'clinicianName', 'error')} ">
	<label for="clinicianName">
		<g:message code="patientConsent.clinicianName.label" default="Clinician Name" />
		
	</label>
	<g:textField name="clinicianName" value="${patientConsentInstance?.clinicianName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'consentStatus', 'error')} required">
	<label for="consentStatus">
		<g:message code="patientConsent.consentStatus.label" default="Consent Status" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="consentStatus" from="${uk.ac.ox.brc.greenlight.PatientConsent$ConsentStatus?.values()}" keys="${uk.ac.ox.brc.greenlight.PatientConsent$ConsentStatus.values()*.name()}" required="" value="${patientConsentInstance?.consentStatus?.name()}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'date', 'error')} required">
	<label for="date">
		<g:message code="patientConsent.date.label" default="Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="date" precision="day"  value="${patientConsentInstance?.date}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'patient', 'error')} required">
	<label for="patient">
		<g:message code="patientConsent.patient.label" default="Patient" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="patient" name="patient.id" from="${uk.ac.ox.brc.greenlight.Patient.list()}" optionKey="id" required="" value="${patientConsentInstance?.patient?.id}" class="many-to-one"/>
</div>

