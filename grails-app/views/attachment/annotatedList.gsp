
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Consent Forms</title>


    <script type="text/javascript">

        var editor; // use a global for the submit and return data rendering in the examples

        $(document).ready(function() {

            var grailsContextPath = "${ createLinkTo(dir: '/')}";

            $('#example').dataTable( {
                "sDom": "<'row'<'span7'l><'span7'>r>t<'row span10'<ip>>",
                "oTableTools": {"sRowSelect": "single"},
                "aLengthMenu": [[5, 10, 15, 25, 50, 100 , -1], [5, 10, 15, 25, 50, 100, "All"]],
                "iDisplayLength" : 10,
                "sPaginationType": "two_button",
                "bProcessing": true,
                "bServerSide": true,
                "sAjaxSource": "lisAnnotatedAttachments.json",
                "sServerMethod": "POST",
                "aoColumns": [
                    { "mData": "consentDate" },
                    { "mData": "formStatus" },
                    { "mData": "consentStatus" },
                    { "mData": "template.namePrefix" },
                    { "mData": "formID" },
                    { "mData": "patient.nhsNumber" },
                    {"mData":"id","bVisible":    false},
                    {  "mData": "id",
                        "bSortable":false,
                        "fnRender": function (oObj) {
                            return "<a class='btn btn-success btn-small' href="+grailsContextPath+"consentFormCompletion/show/" + oObj.aData.id + '>' + 'View' + '</a>'
                        }
                    }
                        ]
            } );
        } );

    </script>


</head>

<body>

<div class="container">

    <div class="row">
        <div class="col-md-12 PageMainPanel">

            <div class="panel panel-primary" >
                <div class="panel-heading">


                    <h3 class="panel-title">Annotated Consent Forms</h3>

                 </div>


                <div class="panel-body">
                    <div class="addformButton">
                        <g:link action="export" controller="ConsentForm" class="btn btn-primary btn-sm" >Export to CSV</g:link>
                    </div>

                    <div class="table-responsive">
                        <div style="width:90%; margin-left:5px;">

                            <table class="table table-striped table-bordered" id="example" border="0" cellpadding="0" cellspacing="0" width="100%">
                                <thead>
                                <tr>
                                    <th width="12%">Consent Date</th>
                                    <th width="15%">Form Status</th>
                                    <th width="20%">Consent Status</th>
                                    <th width="10%">Form Type</th>
                                    <th width="12%">Form Id</th>
                                    <th width="20%">NHS#</th>

                                    <th width="0%">id</th>

                                    <th width="10%">Action</th>
                                </tr>
                                </thead>
                            </table>

                        </div>


                    </div>
                </div>
            </div>

        </div>
    </div>

</div>



</body>
</html>




