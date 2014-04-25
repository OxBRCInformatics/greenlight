<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Studies Description</title>

</head>

<body>

<div class="container">

    <g:form role="form" controller="study" action="updateStudy">
    <div class="row">
        <div class="span12">
            <h3>Cut-Up room Text</h3>

                <g:textArea name="description" value="${description}" style="width:50%;" rows="10">
                </g:textArea>
        </div>


        <div class="span12">
            <button id='btnUpdate' type="submit" class="btn btn-primary">Update</button>
            <g:if test="${flash.message}">
                <div class="alert alert-success studyAlert" >${flash.message}</div>
            </g:if>
             <g:if test="${flash.error}">
                <div class="alert alert-error studyAlert" >${flash.error}</div>
            </g:if>
    </g:form>





    </div>
</div>

</body>
</html>




