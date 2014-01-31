<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Consent Forms Upload</title>
    <meta name="layout" content="mainBootstrap">
</head>

<body>


<div class="container">

    <div class="row">
        <div class="col-md-12" style="margin-top:5px">
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">Consent Form</h3>
                    </div>
                    <div class="panel-body">
                        <div class="row " >
                            <div class="col-md-6">
                                <div class="panel panel-primary">
                                    <div class="panel-body">
                                        <g:form action="upload"  enctype="multipart/form-data">
                                            <div class="fieldcontain ${hasErrors(bean: attachment, field: 'scannedForm', 'error')}     ">
                                                <h5>Please select files:</h5>

                                                <p>
                                                    <input type="file" id="scannedForm" name="scannedForm" multiple="true" style="margin:3px" accept="'image/png', 'image/jpeg', 'image/jpg', 'image/gif'"/>

                                                    <button id='btnUpload' type="submit" class="btn btn-primary" style="margin:3px;">Upload</button>
                                                </p>
                                            </div>
                                        </g:form>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                <g:if test="${attachments?.size()}">
                                    <div class="panel panel-default" >
                                        <div class="panel-heading">Upload Result</div>
                                        <div class="panel-body">
                                            <table class="table able-bordered" id="uploadedFilesTable">
                                                <thead>
                                                <tr>
                                                    <th>No.</th>
                                                    <th>Date of Scan</th>
                                                    <th>Preview</th>
                                                </tr>
                                                </thead>
                                                <tbody>

                                                <g:each in="${attachments}" var="attachment" status="i">
                                                    <tr id="consentFormRow${attachment.id}">
                                                        <td>${i+1}</td>
                                                        <td>"${attachment.dateOfScan}"</td>
                                                        <td>
                                                            <p>
                                                                <g:link action="show" id="${attachment.id}" target="_blank" class="linkButton">
                                                                    <button type="button" class="btn btn-success">View</button>
                                                                </g:link>
                                                                <g:remoteLink action="delete" controller="attachment" id="${attachment.id}" onSuccess="deleteRow(data,textStatus)">
                                                                    <button type="button" class="btn  btn-danger"  onclick="return confirm('Are you sure?')">Delete</button>
                                                                </g:remoteLink>
                                                            </p>
                                                        </td>
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



    <g:javascript>

        function deleteRow(data,textStatus)
        {
            var element=$("#consentFormRow"+data.id);
            element.fadeOut(300, function(){ $(this).remove();});
        }

    </g:javascript>


</body>
</html>