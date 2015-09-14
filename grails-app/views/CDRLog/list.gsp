<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Consent Forms</title>


    <script type="text/javascript">


        //based on https://github.com/viralpatel/jquery.shorten
        function applyComment() {
            var showChar = 100;
            var ellipsestext = "...";
            var moretext = "more";
            var lesstext = "less";
            $('.more').each(function () {
                var content = $(this).html();
                if (content.length > showChar) {
                    var c = content.substr(0, showChar);
                    var h = content.substr(showChar - 1, content.length - showChar + 1);
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
        }

        var grailsContextPath = "${ createLinkTo(dir: '/')}";

        function reSendCDRLog(id,rowIndex){
            var resendURL = grailsContextPath + "CDRLog/resendCDRLogRecordToCDR/"+ id + ".json"
            if(confirm("Are you sure?"))
            {
                $.ajax({
                    type: 'POST',
                    url: resendURL,
                    success: function (data) {
                        if(data.success == false) {
                            alert(data.log)
                        }
                        $("#example").DataTable().fnDraw()
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        alert(thrownError)
                        $("#example").DataTable().fnDraw()
                    }
                })
            }
            return false;
        }

        function markAsPersisted(id,rowIndex){
            var resendURL = grailsContextPath + "CDRLog/markCDRLogRecordAsPersisted/"+ id + ".json"
            if(confirm("It will mark the record as Saved in CDR, are you sure?"))
            {
                $.ajax({
                    type: 'POST',
                    url: resendURL,
                    success: function (data) {
                        if(data.success == false) {
                            alert(data.log)
                        }
                        $("#example").DataTable().fnDraw()
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        alert(thrownError);
                        $("#example").DataTable().fnDraw();
                    }
                })
            }
            return false;
        }

        function unMarkCDRLogRecordIfPersisted(id,rowIndex){
            var resendURL = grailsContextPath + "CDRLog/unMarkCDRLogRecordIfPersisted/"+ id + ".json"
            if(confirm("It will mark the record as UnSaved in CDR, are you sure?"))
            {
                $.ajax({
                    type: 'POST',
                    url: resendURL,
                    async:false,
                    success: function (data) {
                        if(data.success == false) {
                            alert(data.log)
                        }
                        $("#example").DataTable().fnDraw()
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        alert(thrownError);
                        $("#example").DataTable().fnDraw();
                    }
                })
            }
            return false;
        }

        $(document).ready(function () {
            var grailsContextPath = "${ createLinkTo(dir: '/')}";
            $('#example').dataTable({
                "sDom": "<'row'<'span12'l><'span12'>r>t<'row span15'<ip>>",
                "oTableTools": {"sRowSelect": "single"},
                "aLengthMenu": [
                    [5, 10, 15, 25, 50, 100 , -1],
                    [5, 10, 15, 25, 50, 100, "All"]
                ],
                "iDisplayLength" : 10,
                "sPaginationType": "full_numbers",//"two_button",
                "bProcessing": true,
                "bServerSide": true,
                "sAjaxSource": "fetchRecords.json",
                "sServerMethod": "POST",
                "aaSorting": [[1,'desc']], // sort it by default for First column which is actionDate
                "sScrollX": "100%",
                "aoColumns": [
                    { "mData": "id",
                        "bSortable":false,
                        "fnRender": function (oObj,index) {
                            var reSendParam = oObj.aData.id + ',' + index
                            var buttons = ""
                            if(oObj.aData.persistedInCDR == "No"){
                                //reTry
                                buttons = buttons + "<span class='icon-repeat' title='reTry' style='cursor: pointer;' onclick='reSendCDRLog("+reSendParam +")'></span>";
                                //markAsPersisted
                                buttons = buttons + "<span class='icon-ok' title='Mark as Saved' style='cursor: pointer;' onclick='markAsPersisted("+reSendParam +")'></span>";
                            }else{
                                buttons = buttons + "<span class='icon-remove' title='Mark as UnSaved' style='cursor: pointer;' onclick='unMarkCDRLogRecordIfPersisted("+reSendParam +")'></span>";
                            }
                            return buttons
                        }
                    },
                    { "mData": "actionDate"},
                    { "mData": "action" },
                    { "mData": "persistedInCDR"},
                    { "mData": "consentAccessGUID",
                        "fnRender" : function(oObj) {
                            if(oObj.aData.consentId)
                                return "<a href="+grailsContextPath+"consentFormCompletion/show/" + oObj.aData.consentId + '>' +  oObj.aData.consentAccessGUID + '</a>'
                            else
                                return oObj.aData.consentAccessGUID
                        }
                    },
                    { "mData": "nhsNumber"},
                    { "mData": "hospitalNumber"},
                    { "mData": "dateTimePersistedInCDR"},
                    { "mData": "consentDate" },
                    { "mData": "consentFormId" },
                    { "mData": "consentStatus" },
                    { "mData": "resultDetail",
                        "fnRender": function (oObj) {
                            return "<div class='more'>"+ oObj.aData.resultDetail    + "</div>"
                        }
                    },
                    { "mData": "connectionError"},
                    { "mData": "attemptsLog",
                        "fnRender" : function(oObj) {
                            if(oObj.aData.attemptsLog!=null && oObj.aData.attemptsLog!=undefined) {
                                return  "<div class='more'>" +  oObj.aData.attemptsLog.replace(/\n/g, '<br>') + "</div>"
                            }else if(oObj.aData.attemptsLog==null) {
                                return  ""
                            }else{
                                return  "<div class='more'>" +  oObj.aData.attemptsLog + "</div>"
                            }
                        }
                    },
                    { "mData": "attemptsCount"},
                    { "mData": "id","bVisible":false }
                ],
                "fnDrawCallback": function (nRow) {
                    applyComment();
                }
            });
         });

    </script>

</head>

<body>

<div class="container">
    <div class="row">
        <div class="col-md-12 PageMainPanel">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <h3 class="panel-title">CDR Log records</h3>
                </div>

                <div class="panel-body">
                    <div style="width: 100%;">
                        <div class="dataTables_scroll" style="padding: 10px;">
                            <table class="table table-striped table-bordered" id="example" width="100%">
                                <thead>
                                <tr>
                                    <th></th>
                                    <th>Date</th>
                                    <th>Action</th>
                                    <th>Saved</th>
                                    <th>Consent</th>
                                    <th>NHS</th>
                                    <th>MRN</th>
                                    <th>Date/Time Saved in CDR</th>
                                    <th>Consent Date</th>
                                    <th>Consent FormId</th>
                                    <th>Consent Status</th>
                                    <th>Detail</th>
                                    <th>Con Error</th>
                                    <th>Attempts Log</th>
                                    <th>Attempts Count</th>
                                    <th width="0%">id</th>
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
</div>
</body>
</html>




