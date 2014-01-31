<%@ page import="uk.ac.ox.brc.greenlight.ConsentFormTemplate" %>



<div class="fieldcontain ${hasErrors(bean: consentFormTemplateInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="consentFormTemplate.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${consentFormTemplateInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: consentFormTemplateInstance, field: 'questions', 'error')} ">
	<label for="questions">
		<g:message code="consentFormTemplate.questions.label" default="Questions" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${consentFormTemplateInstance?.questions?}" var="q">
    <li><g:link controller="question" action="show" id="${q.id}">${q?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="question" action="create" params="['consentFormTemplate.id': consentFormTemplateInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'question.label', default: 'Question')])}</g:link>
</li>
</ul>

</div>

