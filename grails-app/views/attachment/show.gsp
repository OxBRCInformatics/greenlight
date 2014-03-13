<%@ page import="uk.ac.ox.brc.greenlight.Attachment" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
</head>

<body>
<div class="row ">
    <div class="span12">
        <h1>Consent Form Detail</h1>
        <small>Date of Upload: <g:formatDate date="${attachment.dateOfUpload}" type="day"></g:formatDate></small>

        <div class="row">
            <div class="span12">
            <g:if test="${attachment?.attachmentType == Attachment.AttachmentType.IMAGE}">
                <img id="scannedForm" style="margin: 4px; width: 100%;height: 800px;" class="Photo" src="${createLink(controller: 'attachment', action: 'viewContent', id: "${attachment.id}")}"/>
            </g:if>
            <g:elseif test="${attachment?.attachmentType == Attachment.AttachmentType.PDF}">
                <g:render template="/attachment/pdfViewer" model="[attachmentId: attachment.id]"></g:render>
            </g:elseif>
            </div>
        </div>
    </div>
</div>

</body>
</html>
