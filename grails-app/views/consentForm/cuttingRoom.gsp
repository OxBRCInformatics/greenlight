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

    <g:if test="${flash.message}">
    <div class="row">
        <div class="span6 offset3">
                <div class="alert alert-info alert-block">
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                    <p>${flash.message}</p>
                </div>
        </div>
    </div>
    </g:if>

    <div class="row">
        <div class="span6 offset3">
            <div class="alert alert-info">
                <div class="cuttingRoomInfoMessage" >Please enter NHS number or MRN</div>
            </div>
            <g:form controller="consentForm" name="consentForm" action="checkConsent">
                <g:textField name="searchInput" id="searchInput" class="form-control boldInput" ></g:textField>
            </g:form>
        </div>
    </div>
    <div class="row">
        <div class="span6 offset3">

            <g:if test="${errormsg}">
                <div class="alert alert-error alert-block">
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                    <h4>Error!</h4>
                    <p>${errormsg}</p>
                </div>
            </g:if>

            <g:if test="${searchInput}">
                <g:if test="${consents}">
                    Search for <span>${searchInput}:</span>
                    <table>
                        <thead>
                            <tr>
                                <th>Consent Form</th>
                                <th>Date consented</th>
                                <th>Consent status</th>
                                <th>Restrictions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <g:each var="consent" in="${consents}">
                            <tr>
                                <td>${consent.form.name} <small>(version: ${consent.form.version})</small></td>
                                <td><g:formatDate format="dd-MM-yyyy" date="${consent.lastCompleted}"/></td>
                                <g:if test="${consent.consentStatus == uk.ac.ox.brc.greenlight.ConsentStatus.FULL_CONSENT.name()}">
                                    <td class="alert alert-info alert-block">${consent.consentStatus}</td>
                                </g:if>
                                <g:else>
                                    <td class="alert alert-danger alert-block">${consent.consentStatus}</td>
                                </g:else>
                            </tr>
                            </g:each>
                        </tbody>
                    </table>
                </g:if>
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

</body>
</html>