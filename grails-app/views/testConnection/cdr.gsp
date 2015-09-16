<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Oxford BioResource Consent Form</title>


    <style>
    body.wait *, body.wait
    {
        cursor: progress !important;
    }
    </style>


    <script type="text/javascript">
        var grailsContextPath = "${ createLinkTo(dir: '/')}";
        function findPatientInCDR(){

            $("span#firstName").text("")
            $("span#lastName").text("")
            $("span#dob").text("")
            $("span#status").text("")
            var findPatientURL = grailsContextPath + "testConnection/findPatientInCDR.json"
                $.ajax({
                    type: 'POST',
                    data:{
                           nhsNumber: $('#nhsNumber').val(),
                           hospitalNumber: $('#hospitalNumber').val()
                    },
                    url: findPatientURL,
                    success: function (data) {
                        if(data.success == false) {
                             $("span#status").text(data.log)
                        }else{
                            //found
                            if(data.patient != null){
                                $("span#firstName").text(data.patient.firstName)
                                $("span#lastName").text(data.patient.lastName)
                                $("span#dob").text(data.patient.dateOfBirth)
                                $("span#status").text("Found")
                            }else{
                                $("span#status").text("Not Found")
                            }
                        }

                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        $("span#status").text(thrownError)
                    }
                })
        }


        $(document).ready(function () {
            //add cursor waiting after calling ajax
            $(document).ajaxStart(function ()
            {
                $('body').addClass('wait');
            }).ajaxComplete(function () {
                $('body').removeClass('wait');
            });
        });



    </script>
</head>

<body>

    <div id="page-body" role="main">

        <div class="row">
            <div class="span10 offset1">
                <g:if test="${result.success == false}">
                    <div class="alert alert-error alert-block">
                        <h4>Error!</h4>
                        <p>"${result.log}"</p>
                        <br>
                        <div>Greenlight App URL: ${result?.serverLink}</div>
                    </div>
                </g:if>
                <g:else>
                    <div class="alert alert-success alert-block" style="text-align: left" >
                        <h4>Success</h4>
                        <p>Successfully connected to Mirth CDR</p>
                        <br>
                        <div>Greenlight App URL: ${result?.serverLink}</div>
                    </div>
                </g:else>
            </div>
        </div>

        <div class="row">
            <div class="span3 offset1">
                <form method="POST">
                    <div class="form-group">
                        <label for="nhsNumber">NHS Number</label>
                        <g:textField
                                class="form-control" tabindex="2"
                                name="nhsNumber"  id="nhsNumber"
                                placeholder="NHS Number"/>
                    </div>
                    <div class="form-group" style="margin-top: 15px;">
                        <label for="hospitalNumber">Hospital Number</label>
                        <g:textField
                                class="form-control" tabindex="2"
                                name="hospitalNumber"  id="hospitalNumber"
                                placeholder="Hospital Number"/>
                    </div>
                    <button class="btn btn-primary" onclick="findPatientInCDR();return false;">Search</button>
                </form>
            </div>
            <div class="span5">
                <br>
                <table class="table table-bordered" style="font-size: 14px;">
                    <tr>
                        <td style="width:100px;"><strong>First Name:</strong></td>
                        <td><span id="firstName"></span></td>
                    </tr>
                    <tr>
                        <td style="text-align: left"><strong>Last Name:</strong></td>
                        <td><span id="lastName"></span></td>
                    </tr>
                    <tr>
                        <td style="text-align: left"><strong>Date of Birth:</strong></td>
                        <td><span id="dob"></span></td>
                    </tr>
                    <tr>
                        <td style="width:100px;"><strong>Status:</strong></td>
                        <td><span id="status"></span></td>
                    </tr>
                </table>

            </div>

        </div>

    </div>
</body>
</html>
