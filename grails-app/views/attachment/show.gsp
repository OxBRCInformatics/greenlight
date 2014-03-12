<%@ page import="uk.ac.ox.brc.greenlight.Attachment" %>
<!DOCTYPE html>
<html>
	<head>
        <meta name="layout" content="main">
	</head>
	<body>
    <div class="col-md-12 "  >
        <div class="panel panel-primary PageMainPanel" >
            <div class="panel-heading">Consent Form Detail</div>
            <div class="panel-body">
                <div class="row">
                    <div class="col-md-12">
                        Date of Upload:
<g:formatDate date="${attachment.dateOfUpload}" type="day"></g:formatDate>

</div>


<g:if test="${attachment?.attachmentType == Attachment.AttachmentType.IMAGE}">
    <div class="col-md-12">
        <img id="scannedForm" style="margin: 4px; width: 100%;" class="Photo"
             src="${createLink(controller: 'attachment', action: 'viewContent', id: "${attachment.id}")}"/>
        %{--<embed  id="scannedForm"   style="margin: 4px; width: 100%;height: 800px;" class="Photo"   src="${createLink(controller:'attachment', action:'viewContent', id:"${attachment.id}")}" />--}%
        %{--<object   type="application/pdf"   style="margin: 4px; width: 100%;height: 600px"  data="${createLink(controller:'attachment', action:'viewContent', id:"${attachment.id}")}" />--}%
    </div>
</g:if>
<g:elseif test="${attachment?.attachmentType == Attachment.AttachmentType.PDF}">

    <div class="col-md-12">

        <g:render  template="/attachment/pdfViewer"         model="[attachmentId:attachment.id]" >
        </g:render>
    </div>
 </g:elseif>
    </div>

</div>
</div>
</div>



    </body>
</html>
