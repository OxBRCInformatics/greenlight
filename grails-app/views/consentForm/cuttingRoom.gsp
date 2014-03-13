<%--
  Created by IntelliJ IDEA.
  User: soheil
  Date: 13/03/2014
  Time: 10:20
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Consent Check</title>
    <r:require modules="bootstrap"/>
    <r:require modules="customCSS"/>
    <g:javascript library="jquery"></g:javascript>
    <r:layoutResources />

</head>

<body>



<div class="container">
    <div class="row">
        <div class="col-md-12"><br></div>
    </div>
    <div class="row">
        <g:form controller="consentForm" name="consentForm" action="checkConsent">
              <div class="col-md-10">
                <div class="form-group">
                    <g:textField name="searchInput" id="searchInput" class="form-control "></g:textField>
                </div>
            </div>
        </g:form>
    </div>

    <div class="row">
        <div class="col-md-10">

            <g:if test="${result!=null}">

                 <g:if test="${consentForm!=null}">
                     <g:if test="${result}">
                        <div class="alert alert-success">
                            <div class="boldMessage">Reference: ${searchInput}</div>
                            <div class="boldMessage">is Consented.</div>
                        </div>
                    </g:if>
                    <g:else>
                        <div class="alert alert-danger">
                            <div class="boldMessage">Reference: ${searchInput}</div>
                            <div class="boldMessage">is Not Consented!</div>
                        </div>
                    </g:else>
                 </g:if>
                <g:else>
                    <div class="alert alert-warning">
                        <div class="boldMessage" >Reference: ${searchInput}</div>
                        <div class="boldMessage">Not Found!</div>
                    </div>
                </g:else>
            </g:if>
        </div>
    </div>

</div>



<g:javascript type="text/javascript">
    $(function () {
        $("#searchInput").focus();

        $("#searchInput").keypress(function(e) {
            if(e.which == 10 || e.which == 13) {
                if($("#searchInput").val()=="")
                    return;
                this.form.submit();
            }
          }
        );

    });
</g:javascript>
<r:layoutResources />

</body>
</html>