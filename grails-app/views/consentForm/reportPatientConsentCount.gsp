<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Consent Forms Report</title>
</head>

<body>

<div class="container">

    <div class="row">
        <div class="span12 PageMainPanel">

            <div class="panel panel-primary">
                <div class="panel-heading">

                    <h3 class="panel-title">Participants consented to more than one type of Consent Form</h3>
                </div>

                <div class="panel-body">
                    <div class="span12">
                        <div class="panel panel-primary PageMainPanel">
                            <g:form role="form" controller="ConsentForm" params="">
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="span12 ">

                                                 <g:actionSubmit class="btn btn-primary" value="View" tabindex="6"
                                                                action="searchPatientConsentCount" param=""></g:actionSubmit>

                                                 <g:actionSubmit class="btn btn-primary" value="Export"
                                                                tabindex="6"
                                                                action="exportPatientConsentCount"></g:actionSubmit>
                                         </div>
                                    </div>
                                </div>
                            </g:form>

                            <g:if test="${patients}">
                                <label style="font-weight: bold"> Count: ${patients?.size()}</label>
                            </g:if>
                            <div class="consentSearchTable">
                                <div class="table-responsive">
                                    <table class="table  table-hover table-bordered table-condensed">
                                        <thead>
                                        <tr>
                                            <th>NHS Number</th>
                                            <th>Hospital Number</th>
                                            <th>First name</th>
                                            <th>Last name</th>
                                            <th>DOB</th>
                                            <th>Consent Forms</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <g:each in="${patients}" var="patient" status="index">
                                            <tr>
                                                <td>${patient?.nhsNumber}</td>
                                                <td>${patient?.hospitalNumber}</td>
                                                <td>${patient?.givenName}</td>
                                                <td>${patient?.familyName}</td>
                                                <td>
                                                    <g:formatDate format="yyyy-MM-dd" date="${patient?.dateOfBirth}"/>
                                                </td>
                                                <td>${patient?.consentsString}</td>
                                            </tr>
                                        </g:each>
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>




