<div class="row">
    <div class="col-md-12">
        <g:if test="${attachments?.size()}">
            <div class="panel panel-default" >
                <div class="panel-heading">Upload Result</div>
                <div class="panel-body">

                    <div class="table-responsive">
                        <table class="table able-bordered" id="uploadedFilesTable">
                            <thead>
                            <tr>
                                <th>Upload Date</th>
                                <th>File Name</th>
                                <th>Preview</th>
                            </tr>
                            </thead>
                            <tbody>

                            <g:each in="${attachments}" var="attachment" status="i">
                                <tr id="consentFormRow${attachment.id}">

                                    <td>"${attachment?.dateOfUpload}"</td>
                                    <td>"${attachment?.fileName}"</td>
                                    <td>
                                        <p>
                                            <g:link action="create" class="linkButton" style="text-decoration:none;" controller="consentFormCompletion" params="[attachmentId:"${attachment.id}"]"  >
                                                <button type="button" class="btn  btn-primary" >Enter details</button>
                                            </g:link>
                                            <g:link action="show" id="${attachment.id}"   class="linkButton" style="text-decoration:none;">
                                                <button type="button" class="btn btn-success">View</button>
                                            </g:link>
                                            <g:remoteLink action="delete" controller="attachment" style="text-decoration:none;" id="${attachment.id}" onSuccess="deleteRow(data,textStatus)">
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
            </div>
        </g:if>
    </div>
</div>


<g:javascript>

    function deleteRow(data,textStatus)
    {
        var element=$("#consentFormRow"+data.id);
        element.fadeOut(300, function(){ $(this).remove();});
    }

</g:javascript>
