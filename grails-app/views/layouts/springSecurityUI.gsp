<%@ page import="org.codehaus.groovy.grails.plugins.PluginManagerHolder"%>
<%@ page import="org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils"%>
<%@ page import="grails.plugins.springsecurity.SecurityConfigType"%>
<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><g:layoutTitle default='Security Management Console'/></title>

<meta name="viewport" content="width=device-width, initial-scale=1.0">

 %{--Do not remove this as Geb functional test, fails after logging in spring security--}%
<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon"/>

    <r:require modules="bootstrap"/>
    <r:require modules="customCSS"/>
    <g:javascript library="application"/>

<s2ui:resources module='spring-security-ui' />

<g:layoutHead/>

<%-- tab icons --%>
    <style>
    .icon_role {
        background-image: url('${fam.icon(name: 'lock')}');
    }
    .icon_users {
        background-image: url('${fam.icon(name: 'group')}');
    }
    .icon_user {
        background-image: url('${fam.icon(name: 'user')}');
    }
    .icon_error {
        background-image: url('${fam.icon(name: 'exclamation')}');
    }
    .icon_info {
        background-image: url('${fam.icon(name: 'information')}');
    }
    .icon, .ui-tabs .ui-tabs-nav li a.icon {
        background-repeat: no-repeat;
        padding-left: 24px;
        background-position: 4px center;
    }
    </style>

    <!--[if lt IE 8]>
        <link href="${resource(dir:'bower_components/bootstrap-ie7/css',file:'bootstrap-ie7.css')}" rel="stylesheet">
    <![endif]-->
</head>


<body>

<div role="navigation" class="navbar">
    <div class="navbar-inner">
        <a href="${createLink(uri: '/')}" class="brand">ORB Consent Form</a>

        <ul class="nav">
            <li class="active"><a href="${createLink(uri: '/')}">Home</a></li>
            <li><g:link  controller="attachment" action="list">Consent Forms</g:link></li>
            <li><g:link  controller="attachment" action="create">Upload Files</g:link></li>

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

    </div>
</div><!--/.nav -->


<div class="container" style="margin-top: 50px;">

    <div id='s2ui_header_body'>

        <div id='s2ui_header_title'>
            Spring Security Management Console
        </div>

        <span id='s2ui_login_link_container'>

            <nobr>
                <div id='loginLinkContainer'>
                    <sec:ifLoggedIn>
                        Logged in as <sec:username/> (<g:link controller='logout'>Logout</g:link>)
                    </sec:ifLoggedIn>
                    <sec:ifNotLoggedIn>
                        <a href='#' id='loginLink'>Login</a>
                    </sec:ifNotLoggedIn>

                    <sec:ifSwitched>
                        <a href='${request.contextPath}/j_spring_security_exit_user'>
                            Resume as <sec:switchedUserOriginalUsername/>
                        </a>
                    </sec:ifSwitched>
                </div>
            </nobr>

        </span>
    </div>

    <div id="s2ui_main">
        <div id="s2ui_content">
            <s2ui:layoutResources module='spring-security-ui' />
            <g:layoutBody/>

        </div>
    </div>
</div>


<script type="text/javascript">
    $(function()
    {
        $('.bootstrapTooltip').tooltip()

        jQuery.validator.addMethod("nhsNumber", function(value, element) {
            return this.optional(element) || /^\d\d\d-\d\d\d-\d\d\d\d$/.test(value);
        }, "NHS-Number must be in xxx-xxx-xxxxx format")



        jQuery.validator.addMethod("ShouldNotSelected", function(value, element){
            return 'null' != value;
        }, "Please select one option.");

    });
</script>
<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>


<r:layoutResources />

<g:render template='/includes/ajaxLogin' plugin='spring-security-ui'/>

<s2ui:showFlash/>
</body>
</html>