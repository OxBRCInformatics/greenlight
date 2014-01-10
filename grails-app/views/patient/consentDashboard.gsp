<%--
  Created by IntelliJ IDEA.
  User: rb
  Date: 10/01/2014
  Time: 09:02
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <r:require modules="bootstrap"/>
    <r:layoutResources />
</head>

<body>

<table class="table table-striped">
    <thead>
        <tr>
            <th>Sample ID</th>
            <th>Patient name</th>
            <th>NHS #</th>
            <th>Hospital #</th>
            <th>Consent?</th>
        </tr>
    </thead>
    <tbody>
        <g:each in="${samples}" var="sample">
            <g:if test="${sample.patient.consents.empty}">
            <tr class="alert-danger">
            </g:if>
            <g:else>
            <tr>
            </g:else>
                <td>${sample.id}</td>
                <td>${sample.patient.givenName} ${sample.patient.familyName}</td>
                <td>${sample.patient.nhsNumber}</td>
                <td>${sample.patient.hospitalNumber}</td>
                <td>${!sample.patient.consents.empty}</td>
            </tr>
        </g:each>
    </tbody>
</table>

<r:layoutResources />
</body>
</html>