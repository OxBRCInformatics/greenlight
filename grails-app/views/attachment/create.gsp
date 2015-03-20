<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Consent Forms Upload</title>
    <meta name="layout" content="main">
</head>

<body>


<div class="container">

    <div class="row">
        <div class="span12" style="margin-top:5px">
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">Consent Form</h3>
                    </div>
                    <div class="panel-body">
                        <div class="row " >
                            <div class="span8">
                                <div class="panel panel-primary">
                                    <div class="panel-body">
                                        <g:form action="save" enctype="multipart/form-data" encoding="multipart/form-data">

                                                <h5>Please select files:</h5>

                                                <table>
                                                    <tr>
                                                        <td style="vertical-align:top"><input type="checkbox" name="singleConsentPerPDFFile" id="singleConsentPerPDFFile"/></td>
                                                        <td>
                                                            &nbspMulti-part Consent Form <br>
                                                            <span style="font-size: 11px;font-weight: bold;">Each uploaded PDF file might contain several pages which all belong to ONE consent<br> (e.g. GEL consent forms).</span>
                                                        </td>
                                                    </tr>
                                                </table>
                                            <br>

                                                <p>
                                                    <input type="file" id="scannedForms" name="scannedForms" multiple="true" style="margin:3px" accept="'image/png', 'image/jpeg', 'image/jpg', 'image/gif','application/pdf'"/>

                                                    <button id='btnUpload' type="submit" class="btn btn-primary" style="margin:3px;">Upload</button>
                                                </p>

                                        </g:form>
                                    </div>
                                </div>
                            </div>
                        </div>


                        <g:render template="form"/>


                    </div>
                </div>
            </div>
        </div>
</div>

</body>
</html>