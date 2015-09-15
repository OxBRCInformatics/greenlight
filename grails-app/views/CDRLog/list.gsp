<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Consent Forms</title>

<style>
body.wait *, body.wait
{
    cursor: progress !important;
}
</style>

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

        function loadTable(grailsContextPath){
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
                                return  "<div class='more'>&bull; " +  oObj.aData.attemptsLog.replace(/\n{2}/g, '<br><br> &bull; ') + "</div>"
                            }else if(oObj.aData.attemptsLog==null) {
                                return  ""
                            }else{
                                return  "<div class='more'>&bull; " +  oObj.aData.attemptsLog + "</div>"
                            }
                        }
                    },
                    { "mData": "attemptsCount"},
                    { "mData": "id","bVisible":false }
                ],
                "fnDrawCallback": function (nRow) {
                    applyComment();
                },

                "fnServerParams": function (aoData) {
                    aoData.push({
                        "name": "nhsNumber",
                        "value": $("#nhsNumber").val()
                    })
                    aoData.push({
                        "name": "hospitalNumber",
                        "value": $("#hospitalNumber").val()
                    })

                    aoData.push({
                        "name": "consentFormId",
                        "value": $("#consentFormId").val()
                    })

                    aoData.push({
                        "name": "consentAccessGUID",
                        "value": $("#consentAccessGUID").val()
                    })


                    aoData.push({
                        "name": "persistedInCDR",
                        "value": $("#persistedInCDR").val()
                    })
                }

            });
        }

        $(document).ready(function () {
            var grailsContextPath = "${ createLinkTo(dir: '/')}";
            loadTable(grailsContextPath)

            //add cursor waiting after calling ajax
            $(document).ajaxStart(function ()
            {
                $('body').addClass('wait');
            }).ajaxComplete(function () {
                $('body').removeClass('wait');
            });
         });

        function reLoad(){
            $('#example').dataTable().fnDestroy();
            var grailsContextPath = "${ createLinkTo(dir: '/')}";
            loadTable(grailsContextPath)
        }

    </script>

</head>

<body>

<div class="container">
    <div class="row">
        <div class="col-md-12 PageMainPanel">
            <div class="panel panel-primary">
                <h3 class="panel-title">CDR Log records</h3>

                <br>
                <br>
                <div class="panel-heading">
                </div>
                <div class="panel-body">
                        <div class="panel-body">
                            <form method="POST">
                                <div class="row">
                                <div class="span4">
                                    <div class="form-group">
                                        <label for="nhsNumber">NHS Number</label>
                                        <g:textField  class="form-control" tabindex="1"
                                                      id="nhsNumber" name="nhsNumber"
                                                      value="${params.nhsNumber}"
                                                      placeholder="NHS number"/>
                                    </div>
                                    <div class="form-group">
                                        <label for="hospitalNumber">Hospital Number</label>
                                        <g:textField class="form-control" tabindex="2"
                                                name="hospitalNumber"  id="hospitalNumber"
                                                value="${params.hospitalNumber}"
                                                placeholder="Hospital number"/>
                                    </div>
                                    <div class="form-group">
                                        <label for="persistedInCDR">Saved In CDR</label>
                                        <g:select class="form-control" tabindex="5"
                                                  name="persistedInCDR"  id="persistedInCDR"
                                                  value="${params.savedInCDR}"
                                                  from="${[' ','Yes', 'No']}"
                                                  keys="${[' ','true', 'false']}">
                                        </g:select>
                                    </div>

                                    <div class="form-group">
                                        <button  tabindex="6" class="btn btn-primary" value="Search" onclick="reLoad();return false;">Search</button>
                                        <button  tabindex="7" class="btn btn-primary"  type="reset">Reset</button>
                                    </div>
                                </div>
                                <div class="span4 ">
                                    <div class="form-group">
                                        <label for="consentFormId">Consent Form Id</label>
                                        <g:textField  class="form-control" tabindex="3"
                                                      id="consentFormId" name="consentFormId"
                                                      value="${params.consentFormId}"
                                                      placeholder="FormId"/>
                                    </div>
                                    <div class="form-group">
                                        <label for="consentAccessGUID">Consent Access GUID</label>
                                        <g:textField  class="form-control" tabindex="4"
                                                      id="consentAccessGUID" name="consentAccessGUID"
                                                      value="${params.consentAccessGUID}"
                                                      placeholder="AccessGUID"/>
                                    </div>
                                </div>
                                <div class="span4 ">

                                    <span class="label label-primary">Consent Forms Status</span> <br>
                                    <span style="font-style: italic">Total : ${consentsStatus.total}</span> <br>
                                    <span style="font-style: italic">Normal : ${consentsStatus.normal}</span><br>
                                    <span style="font-style: italic">Saved In CDR : ${consentsStatus.persistedInCDR}</span><br>
                                </div>

                            </div>
                            </form>
                        </div>
                    <div style="width: 100%;">
                        <div class="dataTables_scroll" style="padding: 10px;">
                            <table class="table table-striped table-bordered" id="example" width="100%">
                                <thead>
                                <tr>
                                    <th width="8px"></th>
                                    <th width="20px">Date</th>
                                    <th>Action</th>
                                    <th width="5px">Saved</th>
                                    <th>Consent</th>
                                    <th>NHS</th>
                                    <th >MRN</th>
                                    <th>Date/Time Saved in CDR</th>
                                    <th width="30px">Consent Date</th>
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




