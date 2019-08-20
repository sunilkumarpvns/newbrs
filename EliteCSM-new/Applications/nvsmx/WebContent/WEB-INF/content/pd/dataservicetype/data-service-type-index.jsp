<%--
  User: jaidiptrivedi
--%>
<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider" %>
<%@ page import="com.elitecore.nvsmx.system.constants.NVSMXCommonConstants" %>
<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>

<script type="text/javascript">

    function isAnyDataServiceTypeSelected() {
        var allValues = document.getElementsByName("ids");
        for (var i = 0; i < allValues.length; i++) {
            if (allValues[i].checked == true) {
                return true;
            }
        }
        return false;
    }

    function isAllDataServiceTypeSelected() {
        var allValues = document.getElementsByName("ids");

        for (var i = 0; i < allValues.length; i++) {
            if (allValues[i].checked == true) {
                if (allValues[i].value == 'DATA_SERVICE_TYPE_1') {
                    return true;
                }
            }
        }
        return false;
    }

    function removeRecords(obj) {
        if (isAnyDataServiceTypeSelected() == false) {
            return addWarning(".popup", "At least select one Data Service Type for delete");
        }
        if (isAllDataServiceTypeSelected()) {
            return addWarning(".popup", "All Service can not be deleted");
        }

        var flag = false;
        flag = deleteConfirmMsg(obj, "Delete selected services?");
        if (flag == true) {
            removeData();
        }
        return flag;

    }

    function removeData() {
        document.forms["dataServiceTypeIndex"].action = "${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/delete";
        document.forms["dataServiceTypeIndex"].submit();
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
            clearTimeout(timer);
        }
    }


    function exportService(obj) {
        var selectVar = isAnyDataServiceTypeSelected();
        if (selectVar == false) {
            return addWarning(".popup", "At least select one Data Service Type for export");
        }
        var flag = false;
        flag = deleteConfirmMsg(obj, "Export selected Services ?");
        if (flag == true) {
            exportData();
        }
        return flag;
    }

    function exportData() {
        document.forms["dataServiceTypeIndex"].action = "data-service-type/*/export";
        document.forms["dataServiceTypeIndex"].submit();
    }

    function exportAll() {
        document.forms["dataServiceTypeIndex"].action = "data-service-type/*/exportAll";
        document.forms["dataServiceTypeIndex"].submit();
        showProgressBar();
    }


    function importService() {
        if ($("#uploadedFile").val().length == 0) {
            addWarning(".popup", "At least configure file for Import");
            return false;
        }
        ;
        document.forms["dataServiceTypeIndex"].action = "data-service-type/*/importDataServiceType";
        document.forms["dataServiceTypeIndex"].submit();

    }

    $(function () {
        $('input[id=importServices]').change(function () {
            $('#uploadedFile').val($(this).val());
        });

    });
</script>


<s:form id="dataServiceTypeIndex" method="post" enctype="multipart/form-data" cssClass="form-vertical">
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <s:text name="dataservicetype.search"/>
            </h3>
        </div>

        <div class="panel-body">
            <div class="dataTable-button-groups">
                <span class="btn-group btn-group-sm">
                    <a href="${pageContext.request.contextPath}/pd/dataservicetype/data-service-type/new"
                       class="btn btn-default" role="button" id="btnAddGroup">
                        <span class="glyphicon glyphicon-plus" title="<s:text name="button.add"/>"></span>
                    </a>
                    <span class="btn-group btn-group-sm" onclick="return removeRecords(this);" onmousedown="return removeRecords(this);"
                          data-href="javascript:removeData();">
                        <button id="btnDelete" type="button" disabled class="btn btn-default" data-toggle="tooltip"
                                data-placement="right" title="<s:text name="button.delete"/>" role="button">
                            <span class="glyphicon glyphicon-trash" title="<s:text name="button.delete"/>"></span>
                        </button>
				    </span>
                    <span class="btn-group btn-group-sm" id="btnImportGroup">
						<button id="btnImport" type="button" class="btn btn-default" data-toggle="collapse"
                                data-target="#import" aria-expanded="false" aria-controls="import"
                                data-placement="right" title="<s:text name="button.import"/>" role="button">
							<span class="glyphicon glyphicon-import" title="<s:text name="button.import"/>"></span>
						</button>
					</span>
                    <span class="btn-group btn-group-sm" id="btnExportService" data-toggle="confirmation-singleton"
                          onclick="return exportService(this);" onmousedown="return exportService(this);"
                          data-href="javascript:exportData();">
                        <button id="btnExport" type="button" class="btn btn-default" data-toggle="tooltip"
                                data-placement="right" title="<s:text name="button.export"/>" role="button">
                            <span class="glyphicon glyphicon-export" title="<s:text name="button.export"/>"></span>
                        </button>
                    </span>
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"
                            aria-haspopup="true" data-target="#exportAll" aria-expanded="false">
                        <span class="caret"></span>
                        <span class="sr-only">Toggle Dropdown</span>
                    </button>
                    <span id="exportAll">
                        <ul class="dropdown-menu">
                            <li><a href="javascript:exportAll();"><s:text name="export.all"/>l</a></li>
                        </ul>
                    </span>
                </span>
            </div>
            <div class="col-xs-7 collapse" id="import">
                <div class="input-append form-group">
                    <div class="col-xs-10">
                        <div class="input-group">
                            <input id="uploadedFile" class="form-control" type="text">
                            <a class="input-group-addon" onclick="$('input[id=importServices]').click();"
                               style="text-decoration:none;cursor : pointer;">Browse</a>
                        </div>
                    </div>
                    <div class="col-xs-2">
                        <button type="button" class="btn btn-sm btn-primary" role="button"
                                onclick="return importService();">Import
                        </button>
                    </div>
                </div>
                <s:file id="importServices" type="file" name="importedFile" cssStyle="display:none;"/>
                <s:hidden name="importAction" id="userAction"/>
            </div>
            <nv:dataTable
                    id="dataServiceTypeData"
                    list="${dataListAsJson}"
                    rows="<%=rows%>"
                    showPagination="true"
                    showFilter="true"
                    cssClass="table table-blue"
                    width="100%">

                <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />" beanProperty="id"
                                    style="width:5px !important;"/>
                <nv:dataTableColumn title="Name" beanProperty="name"
                                    hrefurl="${pageContext.request.contextPath}/pd/dataservicetype/data-service-type/$<id>"
                                    sortable="true" style="width:200px;"/>
                <nv:dataTableColumn title="Service Identifier" beanProperty="serviceIdentifier"/>
                <nv:dataTableColumn title="Description" beanProperty="description"/>
                <nv:dataTableColumn icon="<span class='glyphicon glyphicon-pencil'></span>"
                                    hrefurl="edit:${pageContext.request.contextPath}/pd/dataservicetype/data-service-type/$<id>/edit"
                                    style="width:20px;border-right:0px;"/>
                <nv:dataTableColumn disableWhen="id==DATA_SERVICE_TYPE_1"
                                    icon="<span class='glyphicon glyphicon-trash'></span>"
                                    hrefurl="delete:${pageContext.request.contextPath}/pd/dataservicetype/data-service-type/$<id>?_method=DELETE"
                                    style="width:20px;"/>

            </nv:dataTable>
        </div>
    </div>
    <div class="modal fade" id="pleaseWaitDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
         aria-hidden="true" align="center">
        <div class="modal-dialog" style="margin:200px;">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title set-title">Exporting Packages...</h4>
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
</s:form>