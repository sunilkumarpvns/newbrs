<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags/dialog" prefix="nvx" %>

<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();

%>
<style>
    .subtable td:nth-child(odd) {
        text-align: left;
        font-weight: bold;
        width: 110px;
    }

    .subtable td:nth-child(even) {
        text-align: left;
        width: 110px;
    }

    .collapsing {
        -webkit-transition: none;
        transition: none;
    }

</style>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/import.css"/>

<s:form id="deviceDataSearch" method="post" enctype="multipart/form-data" cssClass="form-vertical">
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <s:text name="device.search"/>
            </h3>
        </div>

        <div class="panel-body">
            <div class="modal" id="sampleCSV" tabindex="-1" role="dialog">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal"
                                    aria-label="Close" onclick="hideSampleCSV()">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <h4 class="modal-title set-title">
                                <s:text name="device.sampleCSV"/>
                            </h4>
                        </div>
                        <s:form id="sampleCSVFrom">
                            <div class="modal-body">
                                <div class="row">
                                    <s:text name="device.sampleCSV.value"/>
                                </div>
                            </div>
                            <div class="modal-footer" style="text-align: center;">
                                <button type="button" class="btn btn-sm btn-primary" data-dismiss="modal" onclick="hideSampleCSV()"><s:text name="button.close"></s:text> </button>
                            </div>
                        </s:form>
                    </div>
                </div>
            </div>
            <div class="dataTable-button-groups">
                    <span class="btn-group btn-group-sm">
                        <a href="${pageContext.request.contextPath}/sm/device/device/new" class="btn btn-default"
                           role="button" id="btnAddGroup">
                            <span class="glyphicon glyphicon-plus" title="Add"></span>
                        </a>
                          <span class="btn-group btn-group-sm" id="btnRemoveDevice"
                                data-toggle="confirmation-singleton"
                                onmousedown="return removeRecords(this,'deviceDataSearch');"
                                data-href="javascript:removeData('deviceDataSearch','${pageContext.request.contextPath}/sm/device/device/*/destroy');">
                                <button id="btnDeleteDevice" type="button" class="btn btn-default"
                                data-toggle="tooltip" data-placement="right" title="delete"
                                role="button">
                                <span class="glyphicon glyphicon-trash" title="delete"></span>
                                </button>
                          </span>
                        <span class="btn-group btn-group-sm" id="btnImportGroup">
                            <button id="btnImport" type="button" class="btn btn-default" data-toggle="collapse"
                                    data-target="#import" aria-expanded="false" aria-controls="import"
                                    data-placement="right" title="Import CSV" role="button">
                                <span class="glyphicon glyphicon-import" title="Import CSV"></span>
                            </button>
                        </span>
                        <span class="btn-group btn-group-sm" id="btnExportPkg" data-toggle="confirmation-singleton"
                              onmousedown="return exportDevice(this);" data-href="javascript:exportData();">
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
                                <li><a href="javascript:exportAll();"><s:text name="export.all"/></a></li>
                                <li><a href="javascript:showSampleCSV();"><s:text name="sample.csv"/></a></li>
                            </ul>
                        </span>
                    </span>
                <div class="collapse" id="import">
                    <div class="input-append form-group">
                        <div class="input-group" style="width: 80%">
                            <input id="uploadedFileText" class="form-control" type="text">
                            <a class="input-group-addon" onclick="$('input[id=uploadedFile]').click();" style="text-decoration:none;cursor : pointer;">Browse</a>
                        </div>
                        <button type="button" class="btn btn-sm btn-primary" role="button" onclick="return uploadFile();">Import</button>
                    </div>
                    <s:file id="uploadedFile" type="file" name="uploadedFile" cssStyle="display:none;"/>
                </div>
            </div>


            <nv:dataTable
                    id="deviceData"
                    list="${dataListAsJson}"
                    rows="<%=rows%>"
                    width="100%"
                    showPagination="true"
                    showFilter="true"
                    cssClass="table table-blue">
                <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll'/>" beanProperty="id"
                                    style="width:5px !important;"/>
                <nv:dataTableColumn title="TAC" beanProperty="Tac"
                                    hrefurl="${pageContext.request.contextPath}/sm/device/device/$<id>" sortable="true"
                                    tdCssClass="word-break"
                                    tdStyle="min-width: 25%; !important"/>
                <nv:dataTableColumn title="Brand" beanProperty="Brand" sortable="true" tdCssClass="word-break"
                                    tdStyle="min-width: 15%"/>
                <nv:dataTableColumn title="Model" beanProperty="Device Model" tdCssClass="word-break"
                                    tdStyle="min-width: 15%"/>
                <nv:dataTableColumn title="Hardware Type" beanProperty="Hardware Type" tdCssClass="word-break"
                                    tdStyle="min-width: 15%"/>
                <nv:dataTableColumn title="Operating System" beanProperty="OS" tdCssClass="word-break"
                                    tdStyle="min-width: 15%"/>
                <nv:dataTableColumn title="Year" beanProperty="Year" tdCssClass="word-break" tdStyle="min-width: 15%"/>
                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>"
                                    hrefurl="edit:${pageContext.request.contextPath}/sm/device/device/$<id>/edit"
                                    style="width:20px;"/>
                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"
                                    hrefurl="delete:${pageContext.request.contextPath}/sm/device/device/$<id>?_method=DELETE"
                                    style="width:20px;"/>
            </nv:dataTable>
        </div>
    </div>
</s:form>
<%@include file="/WEB-INF/content/sm/utility/indexPageUtility.jsp"%>
<script type="text/javascript">

    $(function () {
        $('input[id=uploadedFile]').change(function () {
            $('#uploadedFileText').val($(this).val());
        });

    });

    function uploadFile() {
        if ($("#uploadedFile").val().length == 0) {
            addWarning(".popup", "<s:property value="getText('device.upload.error')"/>");
            return false;
        };
        document.forms["deviceDataSearch"].action = "device/*/upload";
        document.forms["deviceDataSearch"].submit();
    }

    function exportDevice(obj) {
        var selectVar = getSelectedValues();
        if (selectVar == false) {
            return addWarning(".popup", "At least select one device for export");
        } else {
            var flag = false;
            flag = deleteConfirmMsg(obj, "Export selected devices?");
            if (flag == true) {
                exportData();
            }
            return flag;
        }
    }

    function exportData() {
        document.forms["deviceDataSearch"].action = "device/*/export";
        document.forms["deviceDataSearch"].submit();
    }

    function exportAll() {
        document.forms["deviceDataSearch"].action = "device/*/exportAll";
        document.forms["deviceDataSearch"].submit();
        showProgressBar();
    }

    function getSelectedValues() {
        var selectedData = false;
        var selectedDatas = document.getElementsByName("ids");
        for (var i = 0; i < selectedDatas.length; i++) {
            if (selectedDatas[i].name == 'ids') {
                if (selectedDatas[i].checked == true) {
                    selectedData = true;
                }
            }
        }
        return selectedData;
    }

    function hideSampleCSV() {
        $("#sampleCSV").hide();
    }
    
    function showSampleCSV() {
        $("#sampleCSV").show();
    }

</script>