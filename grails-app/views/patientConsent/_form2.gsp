<%@ page import="uk.ac.ox.brc.greenlight.PatientConsent" %>
 <%@ page import="uk.ac.ox.brc.greenlight.Patient" %>
 <%@ page import="uk.ac.ox.brc.greenlight.ConsentForm" %>
 
 


<div class="fieldcontain ${hasErrors(bean: patientInstance, field: 'givenName', 'error')} ">
	<label for="givenName">
		<g:message code="patientInstance.givenName.label" default="Given Name" />
		
	</label>
	<g:textField name="patientInstance.givenName" value="${patientInstance?.givenName}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: patientInstance, field: 'familyName', 'error')} ">
	<label for="familyName">
		<g:message code="patient.familyName.label" default="Family Name" />
		
	</label>
	<g:textField name="patientInstance.familyName" value="${patientInstance?.familyName}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: patientInstance, field: 'gender', 'error')} required">
	<label for="gender">
		<g:message code="patient.gender.label" default="Gender" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="patientInstance.gender" from="${uk.ac.ox.brc.greenlight.Patient$Gender?.values()}" keys="${uk.ac.ox.brc.greenlight.Patient$Gender.values()*.name()}" required="" value="${patientInstance?.gender?.name()}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: patientInstance, field: 'dateOfBirth', 'error')} required">
	<label for="dateOfBirth">
		<g:message code="patient.dateOfBirth.label" default="Date Of Birth" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="patientInstance.dateOfBirth" precision="day"  value="${patientInstance?.dateOfBirth}"  />
</div>
<div class="fieldcontain ${hasErrors(bean: patientInstance, field: 'hospitalNumber', 'error')} ">
	<label for="hospitalNumber">
		<g:message code="patient.hospitalNumber.label" default="Hospital Number" />
		
	</label>
	<g:textField name="patientInstance.hospitalNumber" value="${patientInstance?.hospitalNumber}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer1', 'error')} ">
	<label for="answer1">
		<g:message code="patientConsent.answer1.label" default="Q1" />
		
	</label>
	<g:checkBox name="patientConsentInstance.answer1" value="${patientConsentInstance?.answer1}" />
	I agree to have the tissue to be used for..........
</div>
<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer2', 'error')} ">
	<label for="answer2">
		<g:message code="patientConsent.answer2.label" default="Q2" />
		
	</label>
	<g:checkBox name="patientConsentInstance.answer2" value="${patientConsentInstance?.answer2}" />
I agree to have the tissue to be used for..........</div>
<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer3', 'error')} ">
	<label for="answer3">
		<g:message code="patientConsent.answer3.label" default="Q3" />
		
	</label>
	<g:checkBox name="patientConsentInstance.answer3" value="${patientConsentInstance?.answer3}" />
I agree to have the tissue to be used for..........</div>
<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer4', 'error')} ">
	<label for="answer4">
		<g:message code="patientConsent.answer4.label" default="Q4" />
		
	</label>
	<g:checkBox name="patientConsentInstance.answer4" value="${patientConsentInstance?.answer4}" />
I agree to have the tissue to be used for..........</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer5', 'error')} ">
	<label for="answer5">
		<g:message code="patientConsent.answer5.label" default="Q5" />
		
	</label>
	<g:checkBox name="patientConsentInstance.answer5" value="${patientConsentInstance?.answer5}" />
I agree to have the tissue to be used for..........</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer6', 'error')} ">
	<label for="answer6">
		<g:message code="patientConsent.answer6.label" default="Q6" />
		
	</label>
	<g:checkBox name="patientConsentInstance.answer6" value="${patientConsentInstance?.answer6}" />
I agree to have the tissue to be used for..........</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer7', 'error')} ">
	<label for="answer7">
		<g:message code="patientConsent.answer7.label" default="Q7" />
		
	</label>
	<g:checkBox name="patientConsentInstance.answer7" value="${patientConsentInstance?.answer7}" />
I agree to have the tissue to be used for..........</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer8', 'error')} ">
	<label for="answer8">
		<g:message code="patientConsent.answer8.label" default="Q8" />
		
	</label>
	<g:checkBox name="patientConsentInstance.answer8" value="${patientConsentInstance?.answer8}" />
I agree to have the tissue to be used for..........</div>

<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer9', 'error')} ">
	<label for="answer9">
		<g:message code="patientConsent.answer9.label" default="Q9" />
		
	</label>
	<g:checkBox name="patientConsentInstance.answer9" value="${patientConsentInstance?.answer9}" />
I agree to have the tissue to be used for..........</div>



<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'answer10', 'error')} ">
	<label for="answer10">
		<g:message code="patientConsent.answer10.label" default="Q10" />
		
	</label>
	<g:checkBox name="patientConsentInstance.answer10" value="${patientConsentInstance?.answer10}" />
I agree to have the tissue to be used for..........</div>



<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'clinicianName', 'error')} ">
	<label for="clinicianName">
		<g:message code="patientConsent.clinicianName.label" default="Clinician Name" />
		
	</label>
	<g:textField name="patientConsentInstance.clinicianName" value="${patientConsentInstance?.clinicianName}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'consentStatus', 'error')} required">
	<label for="consentStatus">
		<g:message code="patientConsent.consentStatus.label" default="Consent Status" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="patientConsentInstance.consentStatus" from="${uk.ac.ox.brc.greenlight.PatientConsent$ConsentStatus?.values()}" keys="${uk.ac.ox.brc.greenlight.PatientConsent$ConsentStatus.values()*.name()}" required="" value="${patientConsentInstance?.consentStatus?.name()}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: patientConsentInstance, field: 'date', 'error')} required">
	<label for="date">
		<g:message code="patientConsent.date.label" default="Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="patientConsentInstance.date" precision="day"  value="${patientConsentInstance?.date}"  />
</div>
<div class="fieldcontain ${hasErrors(bean: consentFormInstance, field: 'scannedForm', 'error')} required">
	<label for="scannedForm">
		<g:message code="consentForm.scannedForm.label" default="Scanned Form" />
		<span class="required-indicator">*</span>
	</label>
	<input type="file" id="scannedForm" name="consentFormInstance.scannedForm" />
</div>
<div class="fieldcontain ${hasErrors(bean: consentFormInstance, field: 'dateOfConsent', 'error')} required">
	<label for="dateOfConsent">
		<g:message code="consentForm.dateOfConsent.label" default="Date Of Scan" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="consentFormInstance.dateOfConsent" precision="day"  value="${consentFormInstance?.dateOfConsent}"  />
</div>

