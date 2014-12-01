<%--
  Created by IntelliJ IDEA.
  User: soheil
  Date: 07/11/2014
  Time: 13:25
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>

    <asset:javascript src="jquery/dist/jquery.js"/>
    <asset:javascript src="angular/angular.js"/>
    <asset:javascript src="bootstrap/dist/js/bootstrap.js"/>
    <asset:javascript src="angular-bootstrap/ui-bootstrap-tpls.js"/>

    <asset:javascript src="angular/main.js"/>


    <asset:stylesheet src="starter-template.css"/>
    <asset:stylesheet src="application.css"/>
    <asset:stylesheet src="bootstrap/dist/css/bootstrap.css"/>


</head>

<body ng-app="greenlight">

<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                    aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Project name</a>
        </div>

        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li class="active"><a href="#">Home</a></li>
                <li><a href="#about">About</a></li>
                <li><a href="#contact">Contact</a></li>
            </ul>
        </div>
        <!--/.nav-collapse -->
    </div>
</nav>

<div class="container">


    <div class="starter-template">
        <h1>Greenlight v2</h1>

        <p class="lead">Consent Management System</p>

    </div>

    <div class="row" ng-controller="ExampleController" ng-init="init()">


        <div class="col-xs-12 col-md-12 col-md-12 col-lg-4">
            <p>{{consent.patient | json}}</p>
            <p>{{data | json}}</p>

            <div>
                <form novalidate role="form" class="css-form" name="form">



                    <input type="text" overwrite-email ng-model="today" required/>

                    <div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="input-group">
                                    <input  type="text"
                                            class="form-control"
                                            datepicker-popup="{{format}}"
                                            ng-model="dt"
                                            is-open="opened"
                                            min-date="minDate"
                                            max-date="today"
                                            show-button-bar="false"
                                            ng-required="true"
                                            name="testDate"
                                            close-text="Close"

                                        overwrite-email
                                        required/>
                                    <div class="input-group-addon" ng-click="open($event)" style="cursor: pointer">
                                        <span class="glyphicon glyphicon-calendar"></span>
                                    </div>
                                </div>

                            </div>
                        </div>    </div>




                    <div class="form-group">
                        <label for="nhsNumber">nhsNumber</label>


                        <div class="input-group">

                            <input type="input" class="form-control"
                                   placeholder="Enter NHS number"
                                   id="nhsNumber"
                                   name="nhsNumber"
                                   ng-model="consent.patient.nhsNumber"
                                   ng-trim="true" ng-pattern="nhsNumberPattern" required>

                            <div class="input-group-addon">
                                <span class="glyphicon glyphicon-upload"></span>
                            </div>
                        </div>


                        <span class="help-block validation-text"
                              ng-show="form.nhsNumber.$dirty && form.nhsNumber.$invalid">Not a valid NHS number</span>
                    </div>

                    <button type="submit" class="btn btn-default" ng-disabled="form.$invalid">Submit</button>


                    <div class="form-group">
                        <label for="hospitalNumber">Hospital Number</label>
                        <input type="input" class="form-control" placeholder="Hospital Number"
                               id="hospitalNumber"
                               name="hospitalNumber"
                               ng-model="consent.patient.hospitalNumber"  required>
                    </div>

                    <div class="form-group">
                        <label for="lastName">LastName</label>
                        <input type="input" class="form-control"
                               id="lastName"
                               name="lastName"
                               ng-model="consent.patient.lastName"
                               placeholder="LastName" required>
                    </div>

                    <div class="form-group">
                        <label for="firstName">FirstName</label>
                        <input type="input" class="form-control"
                               id="firstName"
                               name="firstName"
                               ng-model="consent.patient.firstName"
                               placeholder="FirstName" required>
                    </div>

                    <div class="form-group">
                        <label>Date of Birth</label>

                        <div class="row">
                            <div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
                                <select class="form-control"
                                        id="dateOfBirthYear"
                                        name="dateOfBirthYear"
                                        ng-model="consent.patient.dateOfBirth.year">
                                    <option ng-repeat="year in validYears" selected="">{{year}}</option>
                                </select>
                            </div>

                            <div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
                                <select class="form-control"
                                        id="dateOfBirthMonth"
                                        name="dateOfBirthMonth"
                                        ng-model="consent.patient.dateOfBirth.month"></select>
                            </div>

                            <div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
                                <select class="form-control" id="dateOfBirthDay"
                                        name="dateOfBirthDay"
                                        ng-model="consent.patient.dateOfBirth.day"></select>
                            </div>
                        </div>
                    </div>


                    <div class="form-group">
                        <label for="consentTakerName">Consent Taker Name</label>
                        <input type="input" class="form-control"
                               id="consentTakerName"
                               name="consentTakerName"
                               ng-model="consent.consentTakerName"
                               placeholder="Consent Taker Name" required>
                    </div>


                    <div class="form-group">
                        <label>Consent Date</label>

                        <div class="row">
                            <div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
                                <select class="form-control"
                                        id="consentDateYear"
                                        name="consentDateYear"
                                        ng-model="consent.consentDate.year">
                                    <option ng-repeat="year in consentValidYears" selected="">{{year}}</option>
                                </select>
                            </div>

                            <div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
                                <select class="form-control"
                                        id="consentDateMonth"
                                        name="consentDateMonth"
                                        ng-model="consent.patient.consentDate.month"></select>
                            </div>

                            <div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
                                <select class="form-control" id="consentDateDay"
                                        name="consentDateDay"
                                        ng-model="consent.patient.consentDate.day"></select>
                            </div>
                        </div>
                    </div>


                    <div class="form-group">
                        <label for="formId">Form ID</label>

                        <div class="input-group">
                            <div class="input-group-addon" style="">{{consent.consentFormTemplate.prefix}}</div>
                            <input type="input" class="form-control"
                                   id="formId"
                                   name="formId"
                                   ng-model="consent.formId"
                                   ng-pattern="consent.consentFormTemplate.formIdPattern"
                                   placeholder="FormId" required consent-form-id>
                        </div>
                        <span ng-show="form.formId.$error.pattern"
                              class="help-block">Invalid Format, it should be a 5 digit number</span>
                        <span ng-show="form.formId.$pending.myValidator"
                              class="help-block">Checking if this formId is available ...</span>
                        <span ng-show="form.formId.$error.myValidator"
                              class="help-block">This form {{foundFormId}} is already taken!</span>
                    </div>


                    <div class="form-group">
                        <label for="comment">Comment</label>
                        <textarea type="input" class="form-control"
                                  id="comment"
                                  name="comment"
                                  ng-model="consent.comment"
                                  placeholder="Comment"></textarea>
                    </div>


                    <div class="form-group">
                        <label>Consent Form Type</label>
                        <select class="form-control"
                                id="consentFormTemplate"
                                name="consentFormTemplate"
                                ng-model="consent.consentFormTemplate"
                                ng-options="form.name for form in allForms"
                                required>
                        </select>
                    </div>

                    <div class="form-group">
                        <template-questions consent="consent"></template-questions>
                    </div>

                </form>
            </div>

        </div>
    </div>
</div>

</body>

</html>