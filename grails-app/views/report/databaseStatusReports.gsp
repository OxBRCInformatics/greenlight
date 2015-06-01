<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Database Status Reports</title>
</head>

<body>

<div class="container">

    <div class="row">
        <div class="span12 PageMainPanel">

            <div class="panel panel-primary">
                <div class="panel-heading">
                    <h3 class="panel-title">Database Status Reports</h3>
                </div>

                <div class="panel-body">
                    <div class="span12">
                        <div class="panel panel-primary PageMainPanel">
                            <g:if test="${dbReport}">
                                <label style="font-weight: bold; font-size: 16px;"> Consent Count: ${dbReport?.ConsentFormCount}</label>


                                <br>
                                <label style="font-weight: bold; font-size: 16px;"> Consent Forms with Blank Elements </label>
                                <div class="consentSearchTable">
                                     <div class="table-responsive">
                                        <table class="table  table-hover table-bordered table-condensed">
                                            <thead>
                                                <tr>
                                                    <th>FormID</th>
                                                    <th>NHS Number</th>
                                                    <th>Last name</th>
                                                    <th>First name</th>
                                                    <th>Hospital Number</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <g:each in="${dbReport?.consentFormsWithEmptyFields}" var="consent" status="index">
                                                    <tr>
                                                        <td>${consent[0]}</td>
                                                        <td>${consent[1]}</td>
                                                        <td>${consent[2]}</td>
                                                        <td>${consent[3]}</td>
                                                        <td>${consent[4]}</td>
                                                    </tr>
                                                </g:each>
                                        </tbody>
                                        </table>
                                     </div>
                                </div>

                                <br>
                                <label style="font-weight: bold; font-size: 16px;"> Consent Forms with Generic NHS Numbers </label>
                                <div class="consentSearchTable">
                                    <div class="table-responsive">
                                        <table class="table  table-hover table-bordered table-condensed">
                                            <thead>
                                            <tr>
                                                <th>FormID</th>
                                                <th>NHS Number</th>
                                                <th>Last name</th>
                                                <th>First name</th>
                                                <th>Hospital Number</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <g:each in="${dbReport?.consentFormWithGenericIDs}" var="consent" status="index">
                                                <tr>
                                                    <td>${consent[0]}</td>
                                                    <td>${consent[1]}</td>
                                                    <td>${consent[2]}</td>
                                                    <td>${consent[3]}</td>
                                                    <td>${consent[4]}</td>
                                                </tr>
                                            </g:each>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>


                                <br>
                                    <label style="font-weight: bold; font-size: 16px;"> NHS Number with more that one DOB </label>
                                    <div class="consentSearchTable">
                                        <div class="table-responsive">
                                            <table class="table  table-hover table-bordered table-condensed">
                                                <thead>
                                                <tr>
                                                     <th style="width: 20%">NHS Number</th>
                                                     <th style="width: 30%">DOBs</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <g:each in="${dbReport?.nhsNumberWithMoreThanOneDOB?.keySet()}" var="nhsNumber" status="index">
                                                    <tr>
                                                        <td>${nhsNumber}</td>
                                                        <td>${dbReport?.nhsNumberWithMoreThanOneDOB[nhsNumber]}</td>
                                                    </tr>
                                                </g:each>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>

                                <br>
                                    <label style="font-weight: bold; font-size: 16px;"> Hospital Number with more that one DOB </label>
                                    <div class="consentSearchTable">
                                        <div class="table-responsive">
                                            <table  class=" table  table-hover table-bordered table-condensed">
                                                <thead>
                                                <tr>
                                                    <th style="width: 20%">Hospital Number</th>
                                                    <th style="width: 30%">DOBs</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <g:each in="${dbReport?.hospitalNumberWithMoreThanOneDOB?.keySet()}" var="hospitalNumber" status="index">
                                                    <tr>
                                                        <td>${hospitalNumber}</td>
                                                        <td>${dbReport?.hospitalNumberWithMoreThanOneDOB[hospitalNumber]}</td>
                                                    </tr>
                                                </g:each>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                            </g:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>




