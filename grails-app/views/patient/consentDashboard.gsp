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
            <th>Patient name</th>
            <th>NHS #</th>
            <th>Hospital #</th>
            <th>Consent?</th>
        </tr>
    </thead>
    <tbody>
        <g:each in="${patients}" var="patient">
            <g:if test="${patient.consents.empty}">
            <tr class="alert-danger">
            </g:if>
            <g:else>
            <tr>
            </g:else>
                <td>${patient.givenName} ${patient.familyName}</td>
                <td>${patient.nhsNumber}</td>
                <td>${patient.hospitalNumber}</td>
                <td>${!patient.consents.empty}</td>
            </tr>
        </g:each>
    </tbody>
</table>
<r:layoutResources />
</body>
</html>