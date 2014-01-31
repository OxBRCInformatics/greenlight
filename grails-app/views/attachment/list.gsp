
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="mainBootstrap">
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
                            <th>Assigned</th>
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
                                <td> Yes</td>
                                <td>${attachment?.consentForm?.patient.nhsNumber}</td>
                                <td style="  text-align: left;">
                                        <g:link action="show" id="${attachment?.consentForm?.id}" controller="consentFormCompletion">
                                            <button type="button" class="btn btn-success btn-sm" style="width:50px">View</button>
                                        </g:link>
                                </td>
                            </g:if>
                            <g:else>
                                <td>No</td>
                                <td></td>
                                <td style="width:150px;text-align: left; ">
                                    <p>

                                <g:link action="create" class="linkButton" controller="consentFormCompletion" params="[attachmentId:"${attachment.id}"]"  >

                                        <button type="button" class="btn  btn-primary btn-sm " style="width:50px" >Assign</button>
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


                                 <ul class="pagination" style="margin: 0px;">

                                     <g:set var="max" value="${params.max}"   />



                                     <g:if test="${params.offset!='0'}">
                                        <li><a href="#">&laquo;</a></li>
                                     </g:if>
                                     <g:each var="offsetId" in="${ (0..<'100'.toInteger()/'10'.toInteger()) }">

                                         <li  ><a href="#"> ${offsetId+1} </a></li>

                                         %{--<li class="${1==1? 'disabled' :'active'}"><a href="#">1</a></li>--}%

                                     </g:each>
                                     %{--<li><a href="#">&raquo;</a></li>--}%
                                 </ul>



                             </td>

                         </tr>

                        </tfoot>
                    </table>
                        <div class="pagination" style="align-content: center;">
                            <g:paginate total="25" />
                        </div>

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




