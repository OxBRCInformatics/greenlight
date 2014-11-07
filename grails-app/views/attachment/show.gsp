<%@ page import="uk.ac.ox.brc.greenlight.Attachment" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Consent Form</title>
</head>

<body>
<div class="row ">
    <div class="span12">
        <h1>Consent Form Detail</h1>
        <small>Date of Upload: <g:formatDate date="${attachment.dateOfUpload}" type="day"></g:formatDate></small>
        <img id="scannedForm" width="100%" class="Photo" src="${resource(dir:'attachments', file:attachment?.id + '.jpg')}"/>
    </div>
</div>

</body>
</html>
