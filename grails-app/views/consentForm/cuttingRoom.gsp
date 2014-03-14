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
    <meta name="layout" content="main">

</head>

<body>

<div class="centered well well-small grayBackground" >
    <div class="row">
        <g:form controller="consentForm" name="consentForm" action="checkConsent">
              <div class="span6 offset3">
                    <g:textField name="searchInput" id="searchInput" class="form-control boldInput" ></g:textField>
            </div>
        </g:form>
    </div>

    <div class="row">
        <div class="span6 offset3">

            <g:if test="${result!=null}">

                 <g:if test="${consentForm!=null}">
                     <g:if test="${result}">
                        <div class="alert alert-success">
                            <div class="boldMessage">Ref: ${searchInput}</div>
                            <div class="boldMessage">is Consented.</div>
                        </div>
                    </g:if>
                    <g:else>
                        <div class="alert alert-danger">
                            <div class="boldMessage">Ref: ${searchInput}</div>
                            <div class="boldMessage">is Not Consented!</div>
                        </div>
                    </g:else>
                 </g:if>
                <g:else>
                    <div class="alert alert-danger">
                        <div class="boldMessage" >Ref: ${searchInput}</div>
                        <div class="boldMessage">Not Found!</div>
                    </div>
                </g:else>
            </g:if>
            <g:else>
                <div class="alert alert-info">
                    <div class="cuttingRoomInfoMessage" >Please enter NHS number or MRN</div>
                </div>
            </g:else>
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

</body>
</html>