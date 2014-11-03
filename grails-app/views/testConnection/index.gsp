<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Oxford BioResource Consent Form</title>

</head>

<body>

    <div id="page-body" role="main">

        <div class="row">
            <div class="span10 offset1">

                <g:if test="${result.errors!=null}">
                    <div class="alert alert-error alert-block">
                        <h4>Error!</h4>
                        <p>"${result.errors}"</p>
                    </div>
                </g:if>
                <g:else>
                    <div class="alert alert-success alert-block" style="text-align: left" >
                        <h4>Success</h4>
                        <p>Successfully connected to Demographic Database</p>
                    </div>
                </g:else>

            </div>
        </div>

    </div>
</body>
</html>
