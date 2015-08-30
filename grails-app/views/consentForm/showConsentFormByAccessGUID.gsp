<%--
  Created by IntelliJ IDEA.
  User: soheil
  Date: 20/08/2015
  Time: 11:53
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>Greenlight Consent Form</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">

    <asset:stylesheet src="bootstrap/docs/assets/css/bootstrap.css"/>
    <asset:javascript src="bootstrap/docs/assets/js/bootstrap.min.js"/>
    <asset:stylesheet src="bootstrap/docs/assets/css/bootstrap-responsive.css"/>


    <link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">

    <!--[if lt IE 9]>
    <!--<script src="${resource(dir: 'bower_components/html5shiv/dist/', file: 'html5shiv.js')}"></script>-->
    <!--<script src="${resource(dir: 'bower_components/respond/dest/', file: 'respond.min.js')}"></script>-->
    <asset:javascript src="html5shiv/dist/html5shiv.js"/>
    <asset:javascript src="respond/dest/respond.min.js"/>
    <![endif]-->

    <style>
/* Sticky footer styles
-------------------------------------------------- */
html {
    position: relative;
    min-height: 100%;
}
body {
    /* Margin bottom by footer height */
    margin-bottom: 60px;
}
.footer {
    position: absolute;
    bottom: 0;
    width: 100%;
    /* Set the fixed height of the footer here */
    height: 60px;
    background-color: #f5f5f5;
}


/* Custom page CSS
-------------------------------------------------- */
/* Not required for template or sticky footer method. */

body > .container {
    padding: 60px 15px 0;
}
.container .text-muted {
    margin: 20px 0;
}

.footer > .container {
    padding-right: 15px;
    padding-left: 15px;
}

code {
    font-size: 80%;
}
    </style>
</head>

<body>

<div role="navigation" class="navbar">
    <div class="navbar-inner">
        <a href="${createLink(uri: '/')}" class="brand">Oxford BioResource Consent Form</a>
    </div>
</div>

<div>
    <div class="span12" style="padding-bottom: 60px;">
        <g:hasErrors>
            <div class="row">
                <div class="span6">
                    <div class="alert alert-danger">Error in processing the request!</div>
                </div>
            </div>
        </g:hasErrors>

        <g:if test="${flash.error}">
            <div class="row">
                <div class="span6">
                    <div class="alert alert-danger">${flash.error}</div>
                </div>
            </div>
        </g:if>
        <g:else>
          <div>
            <div class="row">
                <div class="span4">
                    <strong style="padding-left: 10px;padding-bottom: 10px;">Patient Details</strong>
                    <table class="table table-bordered" style="font-size: 14px;">
                        <tr>
                            <td style="text-align: right"><strong>NHS Number:</strong></td>
                            <td style="text-align: left">${consent?.patient?.nhsNumber}</td>
                        </tr>
                        <tr>
                            <td style="text-align: right;width:120px;"><strong>Hospital Number:</strong></td>
                            <td style="text-align: left">${consent?.patient?.hospitalNumber}</td>
                        </tr>
                        <tr>
                            <td style="text-align: right"><strong>First Name:</strong></td>
                            <td style="text-align: left">${consent?.patient?.givenName}</td>
                        </tr>
                        <tr>
                            <td style="text-align: right"><strong>Last Name:</strong></td>
                            <td style="text-align: left;">${consent?.patient?.familyName}</td>
                        </tr>
                        <tr>
                            <td style="text-align: right"><strong>Date of Birth:</strong></td>
                            <td style="text-align: left">${consent?.patient?.dateOfBirth}</td>
                        </tr>
                    </table>
                </div>
                <div class="span8">
                    <strong style="padding-left: 10px;padding-bottom: 10px;">Consent Details</strong>
                    <table class="table table-bordered" style="font-size: 14px;background-color:${consent?.consentStatus in ["Full consent","Consent with restrictions"] ? '#F1FAED':'#F7EBEB'} ">
                        <tbody>

                            <tr>
                                <td   style="text-align: right;width:150px;"><strong>Consent Date:</strong></td>
                                <td style="text-align: left">${consent?.consentDate}</td>
                            </tr>
                            <tr>
                                <td    style="text-align: right"><strong>Consent Form Id:</strong></td>
                                <td style="text-align: left">${consent?.formID}</td>
                            </tr>
                            <tr>
                                <td  style="text-align: right"><strong>Consent Taker Name:</strong></td>
                                <td style="text-align: left">${consent?.consentTakerName}</td>
                            </tr>
                            <tr>
                                <td    style="text-align: right"><strong>Consent Form:</strong></td>
                                <td style="text-align: left"> ${consent?.consentFormType?.name} ${consent?.consentFormType?.version}</td>
                            </tr>
                            <tr>
                                <td     style="text-align: right; vertical-align: top"><strong>Consent Status</strong></td>
                                <td>
                                    <g:if test="${consent.consentStatus == "Full consent"}">
                                        <span class="label label-success" >Full Consent</span>
                                    </g:if>
                                    <g:elseif test="${consent.consentStatus == "Consent with restrictions"}">
                                        <span class="label label-success">Full Consent with restrictions</span>
                                        <g:if test="${consent?.consentStatusLabels?.size() > 0}">
                                            <br>
                                            <ul style="margin: 0 0 0px 25px !important; ">
                                                <g:each in="${consent?.consentStatusLabels}" var="statusLabel">
                                                    <li style="padding-left:0px;margin-left: 0px;">${statusLabel}</li>
                                                </g:each>
                                            </ul>
                                        </g:if>
                                    </g:elseif>
                                    <g:else>
                                        <span class="label label-important">No Consent</span>
                                    </g:else>
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td>
                                    <a href="${request.forwardURI}?attachment" style="cursor: pointer">
                                        <span class="label label-info">Download Attachment</span>
                                    </a>
                                </td>
                            </tr>

                        </tbody>
                    </table>
                </div>
            </div>
            <div class="row" style="margin-top: 20px;">
                <div class="span12 text-left">
                    <table class="table table-bordered table-striped">
                        <thead>
                            <th></th>
                            <th>Question</th>
                            <th>Answer</th>
                        </thead>
                        <tbody>
                        <g:each in="${consent?.responses}" var="response" status="i">
                            <tr>
                                <td style="text-align: center">${i+1}</td>
                                <td>${response?.question}</td>
                                <td style="text-align: center">${response?.answer}</td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="row" style="margin-bottom: 10px;">
                <div class="span4">
                    <a href="${request.forwardURI}.json" style="cursor: pointer">
                        <span class="label label-info">json</span>
                    </a>
                    <a href="${request.forwardURI}.xml" style="cursor: pointer">
                        <span class="label label-info">xml</span>
                    </a>
                </div>
            </div>
        </div>
        </g:else>
    </div>
</div>
<footer class="footer">
    <div class="container">
        <p class="text-muted">&copy;&nbsp${new Date()[Calendar.YEAR]} &nbspv<g:meta name="app.version"/>  BRC</p>
    </div>
</footer>
</body>
</html>