<%@ page import="uk.ac.ox.brc.greenlight.ConsentForm" %>



<div class="fieldcontain ${hasErrors(bean: consentFormInstance, field: 'patientConsent', 'error')} ">
	<label for="patientConsent">
		<g:message code="consentForm.patientConsent.label" default="Patient Consent" />

	</label>
	<g:select id="patientConsent" name="patientConsent.id" from="${uk.ac.ox.brc.greenlight.PatientConsent.list()}" optionKey="id" value="${consentFormInstance?.patientConsent?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: consentFormInstance, field: 'scannedForm', 'error')} required">
	<label for="scannedForm">
		<g:message code="consentForm.scannedForm.label" default="Scanned Form" />
		<span class="required-indicator">*</span>
	</label>
	<input type="file" id="scannedForm" name="scannedForm" />
</div>

<div class="fieldcontain ${hasErrors(bean: consentFormInstance, field: 'dateOfScan', 'error')} required">
	<label for="dateOfScan">
		<g:message code="consentForm.dateOfScan.label" default="Date Of Scan" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="dateOfScan" precision="day"  value="${consentFormInstance?.dateOfScan}"  />
</div>

