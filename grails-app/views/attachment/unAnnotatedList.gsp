
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Consent Forms</title>


    <script type="text/javascript">

        var editor; // use a global for the submit and return data rendering in the examples

        $(document).ready(function() {

            $('#example').dataTable( {
                "sDom": "<'row'<'span6'l><'span6'>r>t<'row span10'<ip>>",
                "oTableTools": {"sRowSelect": "single"},
                "aLengthMenu": [[5, 10, 15, 25, 50, 100 , -1], [5, 10, 15, 25, 50, 100, "All"]],
                "iDisplayLength" : 10,
                "sPaginationType": "two_button",
                "bProcessing": true,
                "bServerSide": true,
                "sAjaxSource": "listUnAnnotatedAttachments.json",
                "sServerMethod": "POST",
                "aoColumns": [
                    { "mData": "dateOfUpload"          },
                    { "mData": "fileName" },
                    {"mData":"id","bVisible":    false},
                    {  "mData": "id",
                        "bSortable":false,
                        "fnRender": function (oObj) {
                            return "<a class='btn btn-primary btn-small' href=/consentFormCompletion/create?attachmentId=" + oObj.aData.id + '>' + 'Enter Details' + '</a>' +
                                    "<a style='margin-left: 2px;' class='btn  btn-danger btn-small'"+ " onclick='return deleteRow("+oObj.aData.id +","+ oObj.iDataRow + ")' "+" href=/attachment/delete/" + oObj.aData.id + '>' + 'Delete' + '</a>'

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


                    <h3 class="panel-title">Un-Annotated Consent Forms</h3>

                 </div>


                <div class="panel-body">
                    <div class="addformButton">
                        <g:link action="create" class="btn btn-primary btn-sm" >Add Forms</g:link>
                    </div>

                    <div class="table-responsive">
                        <div style="width:90%; margin-left:5px;">

                            <table class="table table-striped table-bordered" id="example" border="0" cellpadding="0" cellspacing="0" width="100%">
                                <thead>
                                <tr>
                                    <th width="30%">Upload Date</th>
                                    <th width="12%">File Name</th>
                                    <th width="0%">id</th>
                                    <th width="12%">Action</th>
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


<g:javascript>

    function deleteRow(attachmentId,iDataRow)
    {
        if(confirm("Are you sure?"))
        {
            $.ajax({
                type: 'POST',
                url: "/attachment/delete/"+attachmentId,
                async:false,
                success: function (data) {

                    if(data == "Can not delete it as it's annotated")
                        alert("Can not delete it as it's annotated")
                    else
                    {
                        var table = $('#example').DataTable();
                        table.fnDeleteRow(iDataRow);
                    }
                },
                error: function (xhr, ajaxOptions, thrownError) {
                    alert(thrownError);
                }
            })

        }
        return false;
    }

</g:javascript>

</body>
</html>




