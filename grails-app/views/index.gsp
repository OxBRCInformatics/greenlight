<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Oxford BioResource Consent Form</title>

</head>

<body>

<div id="page-body" role="main">

    <div class="jumbotron">
        <div class="container">
            <h2>Oxford BioResource <br>Consent Form <br> Management System</h2>
        </div>

        <table>
            <tr>
                <td width="33%" class="dashboard-panel">
                    <g:link  controller="attachment" action="create">
                        <div class="colored-box">
                            <span class="glyphicon glyphicon-import extra-large" ></span>
                            <h4>Upload scanned<br> consent forms</h4>
                            <p>Upload scanned consent forms. Image formats and PDF are all supported.</p>
                        </div>
                    </g:link>
                </td>
                <td width="33%" class="dashboard-panel">
                    <g:link  controller="attachment" action="unAnnotatedList">
                        <div class="colored-box">
                            <span class="glyphicon glyphicon-edit extra-large"></span>
                            <h4>Annotate<br>  consent forms</h4>
                            <p>Oxford BioResource consent forms and their items.
                            Select your uploaded consent form and annotate it's details.</p>
                        </div>
                    </g:link>
                </td>
                <td width="33%" class="dashboard-panel">
                   <g:link  controller="consentForm" action="cuttingRoom">
                    <div class="colored-box">
                        <span class="glyphicon glyphicon-search extra-large"></span>
                        <h4>Search and use<br> consents form</h4>
                        <p>Search for consent forms by NHS number, MRN, consent dates and more...</p>
                    </div>
                    </g:link>
                </td>
            </tr>
        </table>

    </div>

    <div id="footer">
        <div class="container">
            <p class="text-muted">2014 &copy; BRC</p>
        </div>
    </div>
</div>
</body>
</html>
