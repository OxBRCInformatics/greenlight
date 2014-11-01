<%@ page import="grails.plugin.springsecurity.SpringSecurityUtils"%>
<%@ page import="grails.plugin.springsecurity.SecurityConfigType"%>
<!DOCTYPE html>



<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><g:layoutTitle default="Grails"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    %{--Do not remove this as Geb functional test, fails after logging in spring security--}%
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">

    %{--<link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">--}%
    %{--<link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">--}%
    %{--<link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">--}%
    %{--<link rel="stylesheet" href="${resource(dir: 'css', file: 'mobile.css')}" type="text/css">--}%

    <r:require modules="bootstrap"/>
    <r:require modules="customCSS"/>
    <g:javascript library="jquery"></g:javascript>
    <g:javascript library="jqueryFormValidator"></g:javascript>
    <g:javascript library="dataTables"></g:javascript>

    <!--[if lt IE 9]>
    <script src="${resource(dir:'bower_components/html5shiv/dist/',file:'html5shiv.js')}"></script>
    <script src="${resource(dir:'bower_components/respond/dest/',file:'respond.min.js')}"></script>
    <![endif]-->

    <r:layoutResources />
    <g:layoutHead/>
</head>
%{--<body style="margin: 0 auto">--}%
%{--<div id="grailsLogo" role="banner"><a href="http://grails.org"><img src="${resource(dir: 'images', file: 'grails_logo.png')}" alt="Grails"/></a></div>--}%
%{--<g:layoutBody/>--}%
%{--<div class="footer" role="contentinfo"></div>--}%
%{--<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>--}%
%{--<r:layoutResources />--}%
%{--</body>--}%

<body>


<div role="navigation" class="navbar">
    <div class="navbar-inner">
        <a href="${createLink(uri: '/')}" class="brand">Oxford BioResource Consent Form</a>

        <ul class="nav">
            <li class="active"><a href="${createLink(uri: '/')}">Home</a></li>

            <li class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#">Consent Forms <b class="caret"></b></a>
                <ul class="dropdown-menu">
                    <li><g:link  controller="attachment" action="unAnnotatedList">Un-Annotated Consent Forms</g:link></li>
                    <li><g:link  controller="attachment" action="annotatedList">Annotated Consent Forms</g:link></li>
                 </ul>
            </li>
            <li><g:link  controller="attachment" action="create">Upload Files</g:link></li>
            <li><g:link  controller="consentForm" action="search">Search</g:link></li>
            <li><g:link  controller="consentForm" action="cuttingRoom">Cut-Up Room</g:link></li>
            <li><g:link  controller="study" action="index">Cut-Up room Text</g:link></li>

            <sec:ifAnyGranted roles="ROLE_ADMIN">
                <!-- Admin menu -->
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#"> Administration <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a><b>Roles</b></a></li>
                        <li><g:link controller="role" action='search'><g:message code="spring.security.ui.search"/></g:link></li>
                        <li><g:link controller="role" action='create'><g:message code="spring.security.ui.create"/></g:link></li>
                        <li><a><b>Users</b></a></li>
                        <li><g:link controller="user" action='search'><g:message code="spring.security.ui.search"/></g:link></li>
                        <li><g:link controller="user" action='create'><g:message code="spring.security.ui.create"/></g:link></li>
                        <li class="divider"></li>
                        <g:if test='${SpringSecurityUtils.securityConfig.securityConfigType == SecurityConfigType.Requestmap}'>
                            <li><a><b><g:message code="spring.security.ui.menu.requestmaps"/></b></a></li>
                            <li><g:link controller="requestmap" action='search'><g:message code="spring.security.ui.search"/></g:link></li>
                            <li><g:link controller="requestmap" action='create'><g:message code="spring.security.ui.create"/></g:link></li>
                        </g:if>
                        <g:if test='${SpringSecurityUtils.securityConfig.rememberMe.persistent}'>
                            <li><a><b><g:message code="spring.security.ui.menu.persistentLogins"/></b></a></li>
                            <li><g:link controller="persistentLogin" action='search'><g:message code="spring.security.ui.search"/></g:link></li>
                        </g:if>
                        <li><a><b><g:message code="spring.security.ui.menu.registrationCode"/></b></a></li>
                        <li><g:link controller="registrationCode" action='search'><g:message code="spring.security.ui.search"/></g:link></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#"> <g:message code="spring.security.ui.menu.appinfo"/> <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><g:link action='config' controller='securityInfo'><g:message code='spring.security.ui.menu.appinfo.config'/></g:link></li>
                        <li><g:link action='mappings' controller='securityInfo'><g:message code='spring.security.ui.menu.appinfo.mappings'/></g:link></li>
                        <li><g:link action='currentAuth' controller='securityInfo'><g:message code='spring.security.ui.menu.appinfo.auth'/></g:link></li>
                        <li><g:link action='usercache' controller='securityInfo'><g:message code='spring.security.ui.menu.appinfo.usercache'/></g:link></li>
                        <li><g:link action='filterChain' controller='securityInfo'><g:message code='spring.security.ui.menu.appinfo.filters'/></g:link></li>
                        <li><g:link action='logoutHandler' controller='securityInfo'><g:message code='spring.security.ui.menu.appinfo.logout'/></g:link></li>
                        <li><g:link action='voters' controller='securityInfo'><g:message code='spring.security.ui.menu.appinfo.voters'/></g:link></li>
                        <li><g:link action='providers' controller='securityInfo'><g:message code='spring.security.ui.menu.appinfo.providers'/></g:link></li>
                    </ul>
                </li>
            </sec:ifAnyGranted>
        </ul>
        <ul class="nav">
            <sec:ifLoggedIn>
                <li><g:link data-placement="bottom" data-original-title="Logout" rel="tooltip" controller="logout"> Logout </g:link></li>
            </sec:ifLoggedIn>
            <sec:ifNotLoggedIn>
                <li><g:link data-placement="bottom" data-original-title="Login" rel="tooltip" controller="login"> Login </g:link></li>
            </sec:ifNotLoggedIn>
        </ul>


    </div><!--/.nav-collapse -->
</div><!--/.nav -->

<div class="container" style="margin-top: 50px;">
    <g:layoutBody/>
</div>


<script type="text/javascript">
    $(function()
    {
        $('.bootstrapTooltip').tooltip()

        $.validator.addMethod(
                "regex",
                function(value, element, regexp) {
                    var re = new RegExp(regexp);
                    return this.optional(element) || re.test(value);
                },
                "Not a valid format."
        );

        $.validator.addMethod("formElementSelected", function(value, element){
            return value > -1;
        }, "Please select an item.");

    });

</script>

<r:layoutResources />
</body>
</html>
