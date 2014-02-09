
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Consent Forms</title>

</head>

<body>

<div class="container">

    <div class="row">
        <div class="col-md-12 PageMainPanel">

            <div class="panel panel-primary" >
                <div class="panel-heading">

                    <h3 class="panel-title">Uploaded Consent Form</h3>
                    </div>


                <div class="panel-body">
                    <div class="table-responsive">
                    <table class="table  table-hover table-bordered " >
                        <thead>
                        <tr>

                            <th>Upload Date</th>
                            <th>Form Status</th>
                            <th>NHS#</th>
                            <th style="width:50px; "></th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${attachments}" var="attachment" status="index">

                            <g:set var="assigned" value="${attachment?.consentForm!=null}"></g:set>


                            <g:if test="${assigned==true}" >
                                <tr class="success" id="consentFormRow${attachment.id}"   >
                            </g:if>
                            <g:else>
                                <tr id="consentFormRow${attachment.id}"   >
                            </g:else>


                            <td><g:formatDate format="yyyy-MM-dd HH:mm" date="${attachment?.dateOfUpload}"/></td>
                            <g:if test="${attachment?.consentForm!=null}">
                                <td>${attachment?.consentForm?.formStatus}</td>
                                <td>${attachment?.consentForm?.patient.nhsNumber}</td>
                                <td style="  text-align: left;">
                                        <g:link action="show" id="${attachment?.consentForm?.id}" controller="consentFormCompletion">
                                            <button type="button" class="btn btn-success btn-sm" style="width:50px">View</button>
                                        </g:link>
                                </td>
                            </g:if>
                            <g:else>
                                <td></td>
                                <td></td>
                                <td style="width:200px;text-align: left; ">
                                    <p>

                                <g:link action="create" class="linkButton" style="text-decoration:none;" controller="consentFormCompletion" params="[attachmentId:"${attachment.id}"]"  >

                                        <button type="button" class="btn  btn-primary btn-sm "   >Enter Details</button>
                                </g:link>

                                        <g:remoteLink  class="btn  btn-danger btn-sm " controller="attachment" action="delete" id="${attachment.id}"  before="if(!confirm('Are you sure?')) return false" onSuccess="deleteRow(data,textStatus)">
                                        Delete
                                        </g:remoteLink>

                                    </p>

                                </td>
                            </g:else>
                            </tr>
                        </g:each>

                        </tbody>
                        <tfoot>
                         <tr>
                             <td colspan="8" style="text-align: center;">
                             </td>
                         </tr>

                        </tfoot>
                    </table>

                    </div>
                    <g:link action="create">
                        <button type="button" class="btn  btn-primary btn-sm" id="btnAddForms" >Add Forms</button>
                    </g:link>
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




