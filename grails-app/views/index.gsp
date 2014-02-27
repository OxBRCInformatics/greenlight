<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>ORB Consent Form</title>

</head>

<body>

<div id="page-body" role="main">

    <div class="jumbotron">
        <div class="container">
            <h2>ORB <br>Consent Form <br> Management System</h2>
        </div>

        <div class="row">
            <div style="margin: 0 auto;">
                <g:link  controller="attachment" action="create" style="color: #000000;text-decoration:none;">
                    <div class="col-md-4 homepagePanel" style="background-color: #5bc0de;height: 300px;margin-right:2px;">
                        <i class="fa fa-file fa-4x"></i>
                        <h4>Upload scanned<br> consent forms</h4>
                        <p>Upload scanned consent forms. Image formats and PDF are all supported.</p>
                    </div>
                </g:link>
                <g:link  controller="attachment" action="list" style="color: #000000;text-decoration:none;">
                    <div class="col-md-4 homepagePanel" style="background-color: #428bca; height: 300px;margin-right:2px;" >
                        <i class="fa fa-keyboard-o fa-4x"></i>
                        <h4>Annotate<br>  consent forms</h4>
                        <p>ORB consent forms and their items are all supported.
                        Select your uploaded consent form and annotate it's details.</p>
                    </div>
                </g:link>
                <div class="col-md-3 homepagePanel" style="background-color: #5cb85c;height: 300px;">
                    <i class="fa fa-search fa-4x"></i>
                    <h4>Search and use<br> consents form</h4>

                </div>
            </div>
        </div>

    </div>

    <div id="footer">
        <div class="container">
            <p class="text-muted">2013 &copy; BRC</p>
        </div>
    </div>
</div>
</body>
</html>
