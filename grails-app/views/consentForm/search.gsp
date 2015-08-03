
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Consent Forms</title>

</head>

<body>

<div class="container">

    <div class="row">
        <div class="span12 PageMainPanel">

            <div class="panel panel-primary" >
                <div class="panel-heading">

                    <h3 class="panel-title">Search Consent Form</h3>
                </div>
                <div class="panel-body">
                    <div class="span12">
                        <div class="panel panel-primary PageMainPanel">

                           <g:form role="form" action="find" controller="ConsentForm" params="">
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="span4 ">
                                            <div class="form-group">
                                                <label for="nhsNumber">NHS Number</label>
                                                <g:textField  class="form-control" tabindex="1"
                                                              id="nhsNumber" name="nhsNumber"
                                                              value="${params.nhsNumber}"
                                                              placeholder="NHS number like 1234567890"/>
                                            </div>



                                            <div class="form-group">
                                                <label for="consentDateFrom">Consent Date From</label>
                                                <br>
                                                <g:datePicker class="form-control" id="consentDateFrom" tabindex="4"
                                                              name="consentDateFrom"
                                                              value="${params.consentDateFrom}"
                                                              precision="day" default="none" noSelection="['':'']"/>
                                            </div>


                                            <div class="form-group">
                                                <label for="formIdFrom">Consent Form Id From</label>
                                                <br>
                                                <g:textField  class="form-control" tabindex="7"
                                                              id="formIdFrom" name="formIdFrom"
                                                              value="${params.formIdFrom}"
                                                              placeholder="Form Id like GEN12345"/>
                                            </div>



                                            <div class="form-group">
                                                %{--<input type="submit"  class="btn btn-primary"  value="Search" tabindex="6">--}%

                                                <g:actionSubmit class="btn btn-primary" value="Search" tabindex="9"
                                                                action="find" param=""></g:actionSubmit>

                                                <g:actionSubmit class="btn btn-primary" value="Export" tabindex="10"
                                                                action="findAndExport"></g:actionSubmit>

                                            </div>
                                        </div>
                                        <div class="span4 ">


                                            <div class="form-group">
                                                <label for="hospitalNumber">Hospital Number</label>
                                                <g:textField
                                                        class="form-control" tabindex="2"
                                                        name="hospitalNumber"  id="hospitalNumber"
                                                        value="${params.hospitalNumber}"
                                                        placeholder="Hospital Number"/>
                                            </div>


                                            <div class="form-group">
                                                <label for="consentDateTo">Consent Date To</label>
                                                <br>
                                                <g:datePicker class="form-control" id="consentDateTo" tabindex="5"
                                                              name="consentDateTo"
                                                              value="${params.consentDateTo}"
                                                              precision="day" default="none" noSelection="['':'']"/>
                                            </div>


                                            <div class="form-group">
                                                <label for="formIdTo">Consent Form Id To</label>
                                                <br>
                                                <g:textField  class="form-control" tabindex="8"
                                                              id="formIdTo" name="formIdTo"
                                                              value="${params.formIdTo}"
                                                              placeholder="Form Id like GEN6789"/>
                                            </div>





                                        </div>
                                        <div class="span4">
                                            <div class="form-group">
                                                <label for="consentTakerName">Consent Taker</label>
                                                <g:textField
                                                        class="form-control" tabindex="3"
                                                        name="consentTakerName"  id="consentTakerName"
                                                        value="${params.consentTakerName}"
                                                        placeholder="Consent Taker Name"/>

                                            </div>
                                            <div class="form-group">
                                                <label for="comment">Comment</label>
                                                <g:textArea rows="6" cols="40"
                                                        class="form-control" tabindex="6"
                                                        name="comment"  id="comment"
                                                        value="${params.comment}"
                                                        placeholder="Comment"/>

                                            </div>
                                        </div>
                                    </div>

                                </div>
                           </g:form>

                            <div class="consentSearchTable">
                                <div class="table-responsive">
                                    <table class="table  table-hover table-bordered">
                                        <thead >
                                        <tr>
                                            <th style="text-align: center">NHS Number</th>
                                            <th style="text-align: center">Hospital Number</th>
                                            <th style="text-align: center">Form Status</th>
                                            <th style="text-align: center">Form Id</th>
                                            <th style="text-align: center">Upload Date</th>
                                            <th style="text-align: center">Consent Date</th>
                                            <th style="text-align: center">Consent Taker</th>
                                            <th style="text-align: center">Comment</th>
                                            <th style="text-align: center"></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <g:each in="${consentForms}" var="consentForm" status="index">
                                            <tr>
                                                <td style="width:10%;">${consentForm?.patient?.nhsNumber}</td>
                                                <td style="width:10%">${consentForm?.patient?.hospitalNumber}</td>
                                                <td style="width:10%">${consentForm?.formStatus}</td>
                                                <td style="width:7%">${consentForm?.formID}</td>
                                                <td style="width:15%;font-size: 12px;">
                                                    <g:formatDate format="yyyy-MM-dd HH:mm"
                                                                  date="${consentForm?.attachedFormImage?.dateOfUpload}"/>
                                                </td>

                                                <td style="width:10%;font-size: 12px;">
                                                    <g:formatDate format="yyyy-MM-dd"
                                                                  date="${consentForm?.consentDate}"/>
                                                </td>

                                                <td style="width:10%">${consentForm?.consentTakerName}</td>
                                                %{--<td style="cursor: pointer" title="${consentForm?.comment}">${consentForm?.comment?.split(/\s+/).size()>10 ? consentForm?.comment?.split(/\s+/).toList().subList(0,9).join(' ')+"...." : consentForm?.comment}</td>--}%

                                                <td style="width:10%" style="cursor: pointer" title="${consentForm?.comment}">
                                                    <div class="comment more">${consentForm?.comment?.trim()}</div>
                                                </td>

                                                <td style="width:8%">
                                                    <g:link action="show" id="${consentForm?.id}"
                                                            class="btn btn-success btn-sm"
                                                            controller="consentFormCompletion"
                                                            target="blank">View</g:link>
                                                </td>

                                            </tr>
                                        </g:each>
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
         </div>
    </div>
</div>

<script type="text/javascript">

    //based on https://github.com/viralpatel/jquery.shorten
    $(document).ready(function () {
        var showChar = 40;
        var ellipsestext = "...";
        var moretext = "more";
        var lesstext = "less";
        $('.more').each(function () {
            var content = $(this).html();
            if (content.length > showChar) {
                var c = content.substr(0, showChar);
                var h = content.substr(showChar - 1, content.length - showChar);
                var html = c + '<span class="moreelipses">' + ellipsestext + '</span><span class="morecontent"><span>' + h + '</span>&nbsp;&nbsp;<a href="" class="morelink">' + moretext + '</a></span>';
                $(this).html(html);
            }
        });
        $(".morelink").click(function () {
            if ($(this).hasClass("less")) {
                $(this).removeClass("less");
                $(this).html(moretext);
            } else {
                $(this).addClass("less");
                $(this).html(lesstext);
            }
            $(this).parent().prev().toggle();
            $(this).prev().toggle();
            return false;
        });

    });
</script>

</body>

</html>




