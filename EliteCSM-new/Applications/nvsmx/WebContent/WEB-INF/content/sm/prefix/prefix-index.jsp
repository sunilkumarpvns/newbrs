<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider" %>
<%@ page import="com.elitecore.nvsmx.system.constants.NVSMXCommonConstants" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s" %>

<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>
<script>
    function networkRender(data, type, thisBean) {
        return "<a href='${pageContext.request.contextPath}/sm/network/network/" + thisBean.networkData.id + "'>" + data + "</a>"
    }

    function getSelectedValues() {
        var selectedData = false;
        var selectedDatas = document.getElementsByName("ids");
        for (var i = 0; i < selectedDatas.length; i++) {
            if (selectedDatas[i].checked == true) {
                selectedData = true;
            }
        }
        return selectedData;
    }

    function importPrefix() {
        if ($("#import").is(":visible")) {
            $("#import").hide();
            $("#import").addClass("col-xs-7 collapse");
        }
        else {
            $("#import").show();
            $("#import").addClass("col-xs-7 collapse in");
            $("#export").hide();
            $("#export").addClass("col-xs-7 collapse");
        }
    }

    function exportPrefix(obj) {
        var selectVar = getSelectedValues();
        if (selectVar == false) {
            if ($("#export").is(":visible")) {
                $("#export").hide();
                $("#export").addClass("col-xs-7 collapse");
            }
            else {
                $("#export").show();
                $("#export").addClass("col-xs-7 collapse in");
                $("#import").hide();
                $("#import").addClass("col-xs-7 collapse");
            }
        } else {
            var flag = false;
            flag = deleteConfirmMsg(obj, "Export selected Prefix ?");
            if (flag == true) {
                exportData();
            }
            return flag;
        }
    }

    function exportData() {
        document.forms["prefixDataSearch"].action = "${pageContext.request.contextPath}/sm/prefix/prefix/*/export";
        document.forms["prefixDataSearch"].submit();
    }

    function exportUsingText() {
        var data = document.getElementById("exportDataInput").value;
        var dataColumn = document.getElementById("exportDataSelect").value;
        document.forms["prefixDataSearch"].action = "${pageContext.request.contextPath}/sm/prefix/prefix/*/exportUsingText";
        document.forms["prefixDataSearch"].submit();
    }

    function exportAll() {
        document.forms["prefixDataSearch"].action = "${pageContext.request.contextPath}/sm/prefix/prefix/*/exportAll";
        document.forms["prefixDataSearch"].submit();
        showProgressBar();
    }

    function importFunc() {
        if ($("#uploadedFile").val().length == 0) {
            addWarning(".popup", "At least select file for Import");
            return false;
        }
        else if (!($("#uploadedFile").val().split('.').pop() == "csv" || $("#uploadedFile").val().split('.').pop() == "txt")) {
            addWarning(".popup", "Please select CSV/TXT format file");
            return false;
        }
        ;
        document.forms["prefixDataSearch"].action = "${pageContext.request.contextPath}/sm/prefix/prefix/*/importPrefix";
        document.forms["prefixDataSearch"].submit();

    }
    function showProgressBar() {

        var pleaseWait = $('#pleaseWaitDialog');
        $(pleaseWait).modal({backdrop: 'static', keyboard: false});
        showPleaseWait = function () {
            pleaseWait.modal('show');
            timer = setInterval(isExportCompleted, 2000);

        };

        hidePleaseWait = function () {
            pleaseWait.modal('hide');
        };
        showPleaseWait();
    }
    function isExportCompleted() {
        var flag = '<%=request.getServletContext().getAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING)%>';
        if (flag == "false") {
            $('#pleaseWaitDialog').modal('hide');
            $("#exportPkgSuccess").modal('show');
            clearTimeout(timer);
        }
    }

    $(function () {
        $('input[id=importPrefix]').change(function () {
            $('#uploadedFile').val($(this).val());
        });

    });
</script>

<s:form id="prefixDataSearch" method="post" enctype="multipart/form-data" cssClass="form-vertical">
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title"><s:text name="prefix.search"/></h3>
        </div>
        <div class="panel-body">
            <div class="dataTable-button-groups">
                <div class="col-xs-5">
				<span class="btn-group btn-group-sm">
                     <a href="${pageContext.request.contextPath}/sm/prefix/prefix/new" class="btn btn-default"
                        role="button" id="btnAddprefix">
                       <span class="glyphicon glyphicon-plus" title="Add"></span>
                     </a>
                    <span class="btn-group btn-group-sm" id="btnRemovePrefix"
                          data-toggle="confirmation-singleton"
                          onmousedown="return removeRecords(this,'prefixDataSearch');"
                          data-href="javascript:removeData('prefixDataSearch','${pageContext.request.contextPath}/sm/prefix/prefix/*/destroy');">
                                <button id="btnDeletePrefix" type="button" class="btn btn-default"
                                        data-toggle="tooltip" data-placement="right" title="delete"
                                        role="button">
                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                </button>
                          </span>
                    <span class="btn-group btn-group-sm" id="btnImportPrefix" onclick="return importPrefix(this)">
							<button id="btnImport" type="button" class="btn btn-default" data-toggle="collapse"
                                    aria-controls="import" data-placement="right" title="import" role="button">
								<span class="glyphicon glyphicon-import" title="Import"></span>
							</button>
						</span>
						<span class="btn-group btn-group-sm" id="btnExportPrefix" data-toggle="confirmation-singleton"
                              onclick="return exportPrefix(this);"
                              data-href="javascript:exportData();">
							<button id="btnExport" type="button" class="btn btn-default" data-toggle="tooltip"
                                    data-placement="right" title="export" role="button">
								<span class="glyphicon glyphicon-export" title="export"></span>
							</button>
						</span>
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"
                            aria-haspopup="true" data-target="#exportAll" aria-expanded="false">
							<span class="caret"></span>
							<span class="sr-only">Toggle Dropdown</span>
						</button>
		  				<span id="exportAll">
		 					<ul class="dropdown-menu">
								<li><a href="javascript:exportAll();">Export All</a></li>
							</ul>
						</span>
				</span>
                </div>
                <div class="col-xs-7 collapse" id="import">
                    <div class="input-append form-group">
                        <div class="col-xs-10">
                            <div class="input-group">
                                <input id="uploadedFile" class="form-control" type="text">
                                <a class="input-group-addon" onclick="$('input[id=importPrefix]').click();"
                                   style="text-decoration:none;cursor : pointer;">Browse</a>
                            </div>
                        </div>
                        <div class="col-xs-2">
                            <button type="button" class="btn btn-sm btn-primary" role="button"
                                    onclick="return importFunc();">Import
                            </button>
                        </div>
                    </div>
                    <s:file id="importPrefix" type="file" name="importedFile" cssStyle="display:none;"/>
                    <s:hidden name="importAction" id="userAction"/>
                </div>
                <div class="col-xs-7 collapse" id="export">
                    <div class="input-append form-group">
                        <div class="col-xs-10">
                            <div class="input-group">
                                <select id="exportDataSelect" name="exportDataSelect"
                                        style="border-radius: 3px;height: 28px;">
                                    <option value="prefix">Prefix</option>
                                    <option value="country">Country</option>
                                    <option value="operator">Operator</option>
                                    <option value="network">Network</option>
                                </select>
                                <input type="text" id="exportDataInput" name="exportDataInput"
                                       style="border-radius: 6px;height: 28px;width: 220px">
                            </div>
                        </div>
                        <div class="col-xs-2">
                            <button type="submit" onclick="exportUsingText()" class="btn btn-sm btn-primary"
                                    value="Export">Export
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <nv:dataTable
                    id="prefixData"
                    list="${dataListAsJson}"
                    rows="<%=rows%>"
                    width="100%"
                    showPagination="true"
                    showFilter="true"
                    cssClass="table table-blue">
                <nv:dataTableColumn title="<input type='checkbox' name='selectAll' id='selectAll'/>" beanProperty="id"
                                    style="width:5px !important;"/>
                <nv:dataTableColumn title="Prefix" beanProperty="prefix"
                                    hrefurl="${pageContext.request.contextPath}/sm/prefix/prefix/$<id>" sortable="true"
                                    tdCssClass="word-break"/>
                <nv:dataTableColumn title="Country" beanProperty="countryData.name" sortable="true"
                                    tdCssClass="word-break"/>
                <nv:dataTableColumn title="Operator" beanProperty="operatorData.name" sortable="true"
                                    tdCssClass="word-break"/>
                <nv:dataTableColumn title="Network" beanProperty="networkData.name" sortable="true"
                                    tdCssClass="word-break" renderFunction="networkRender"/>
                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>"
                                    hrefurl="edit:${pageContext.request.contextPath}/sm/prefix/prefix/$<id>/edit"
                                    style="width:20px;border-right:0px;"/>
                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"
                                    hrefurl="delete:${pageContext.request.contextPath}/sm/prefix/prefix/$<id>?_method=DELETE"
                                    style="width:20px;"/>
            </nv:dataTable>
        </div>
    </div>
    <div class="modal fade" id="pleaseWaitDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
         aria-hidden="true" align="center">
        <div class="modal-dialog" style="margin:200px;">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title set-title">Exporting Prefixes...</h4>
                </div>
                <div class="modal-body">
                    <div class="progress">
                        <div class="progress-bar progress-bar-success progress-bar-striped active" role="progressbar"
                             aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 1000%">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- Success Message for exportAll -->
    <div class="modal fade" id="exportPkgSuccess" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
         aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                    <h4 class="modal-title set-title">Success</h4>
                </div>
                <div class="modal-body">
                    <h5>Your download will begin shortly</h5>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" data-dismiss="modal">Ok</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div>
</s:form>

<%@include file="/WEB-INF/content/sm/utility/indexPageUtility.jsp" %>