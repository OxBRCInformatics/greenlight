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
</head>

<body>

<table>
    <thead>
        <tr>
            <th>Patient name</th>
            <th>Hospital #</th>
            <th>Consent?</th>
        </tr>
    </thead>
    <tbody>
        <g:each in="${patients}" var="patient">
            <tr>
                <td>${patient.givenName} ${patient.familyName}</td>
                <td>${patient.hospitalNumber}</td>
                <td>${patient.consents.length}</td>
            </tr>
        </g:each>
    </tbody>
</table>
</body>
</html>