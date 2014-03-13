<div class="row">
    <div class="span12">
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
                                        <g:link action="create" class="btn btn-primary" controller="consentFormCompletion" params="[attachmentId:"${attachment.id}"]">Enter details</g:link>
                                        <g:link action="show" id="${attachment.id}" class="btn btn-success" style="text-decoration:none;">View</g:link>
                                        <g:remoteLink action="delete" controller="attachment" class="btn  btn-danger" id="${attachment.id}" onSuccess="deleteRow(data,textStatus)">Delete</g:remoteLink>
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
