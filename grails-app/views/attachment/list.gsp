
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Consent Forms</title>

</head>

<body>

<div class="container">

    <div class="row">
        <div class="span12">
               <h3>Uploaded Consent Form</h3>

                    <div class="table-responsive">
                        <g:link action="create" class="btn btn-primary btn-sm" >Add Forms</g:link>
                    <table class="table  table-hover table-bordered " >
                        <thead>
                        <tr>
                            <th>Upload Date</th>
                            <th>Form Status</th>
                            <th>Form Type</th>
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


                            <td>
                                <g:formatDate format="yyyy-MM-dd HH:mm" date="${attachment?.dateOfUpload}"/>
                            </td>
                            <g:if test="${attachment?.consentForm!=null}">
                                <td>${attachment?.consentForm?.formStatus}</td>
                                <td>${attachment?.consentForm?.template?.namePrefix}</td>
                                <td>${attachment?.consentForm?.patient.nhsNumber}</td>
                                <td style="  text-align: left;">
                                        <g:link action="show" class="btn btn-success" id="${attachment?.consentForm?.id}" controller="consentFormCompletion">View</g:link>
                                </td>
                            </g:if>
                            <g:else>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td style="width:200px;text-align: left; ">
                                    <g:link action="create" class="btn btn-primary" style="text-decoration:none;" controller="consentFormCompletion" params="[attachmentId: attachment.id]">Enter Details</g:link>
                                    <g:remoteLink  class="btn  btn-danger btn-sm " controller="attachment" action="delete" id="${attachment.id}"  before="if(!confirm('Are you sure?')) return false" onSuccess="deleteRow(data,textStatus)">Delete</g:remoteLink>
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




