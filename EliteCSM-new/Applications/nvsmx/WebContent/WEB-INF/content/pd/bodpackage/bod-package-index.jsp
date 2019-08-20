<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags/dialog" prefix="nvx" %>
<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();

%>
<s:form id="bodPackageSearchForm" method="post" enctype="multipart/form-data" cssClass="form-vertical">
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <s:text name="bod.package.search"/>
            </h3>
        </div>

        <div class="panel-body">
            <div class="dataTable-button-groups">
				<span class="btn-group btn-group-sm">
						<a href="${pageContext.request.contextPath}/pd/bodpackage/bod-package/new" class="btn btn-default"
                           role="button" id="btnAddGroup">
                            <span class="glyphicon glyphicon-plus" title="Add"></span>
                        </a>
                     <span class="btn-group btn-group-sm" id="btnRemoveCity"
                           data-toggle="confirmation-singleton"
                           onmousedown="return removeRecords(this,'bodPackageSearchForm');"
                           data-href="javascript:removeData('bodPackageSearchForm','${pageContext.request.contextPath}/pd/bodpackage/bod-package/*/destroy');">
                           <button id="btnDeleteCity" type="button" class="btn btn-default"
                                   data-toggle="tooltip" data-placement="right" title="delete"
                                   role="button">
                            <span class="glyphicon glyphicon-trash" title="delete"/></button>
                    </span>
					</span>
            </div>
            <nv:dataTable
                    id="bodPackageData"
                    list="${dataListAsJson}"
                    rows="<%=rows%>"
                    width="100%"
                    showPagination="true"
                    cssClass="table table-blue" showFilter="true">
                <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />" beanProperty="id"
                                    style="width:5px !important;"/>
                <nv:dataTableColumn title="Name" beanProperty="Name"
                                    hrefurl="${pageContext.request.contextPath}/pd/bodpackage/bod-package/$<id>"
                                    sortable="true"/>
                <nv:dataTableColumn title="Status" beanProperty="Status" sortable="true"/>
                <nv:dataTableColumn title="Mode" beanProperty="Mode" sortable="true"/>
                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>"
                                    hrefurl="edit:${pageContext.request.contextPath}/pd/bodpackage/bod-package/$<id>/edit"
                                    style="width:20px;border-right:0px;"/>
                <nv:dataTableColumn title="" disableWhen="Mode==LIVE2"
                                    icon="<span class='glyphicon glyphicon-trash'></span>"
                                    hrefurl="delete:${pageContext.request.contextPath}/pd/bodpackage/bod-package/$<id>?_method=DELETE"
                                    style="width:20px;"/>
            </nv:dataTable>

        </div>
    </div>
</s:form>
<%@include file="/WEB-INF/content/sm/utility/indexPageUtility.jsp" %>
<script type="text/javascript">
    function isAnyEntitySelected(formId) {

        if (isNullOrEmpty(formId)) {
            checkBoxes = $("input[name='ids']");
        } else {
            checkBoxes = $("form[id='" + formId + "']").find("input[name='ids']");
        }
        var modes;

        if (isNullOrEmpty(formId)) {
            modes = $("input[name='modes']");
        } else {
            modes = $("form[id='" + formId + "']").find("input[name='modes']");
        }
        var resultCode=false;
        var resultMessage;
        var checkBoxes;
        $(checkBoxes).each(function (index) {
            if ($(this).prop("checked")) {
                resultCode = true;
                if ($(modes[index]).val() == "LIVE2") {
                    resultCode = "WARNING";
                    resultMessage = "LIVE2 BoD can not be deleted";
                    return false;
                }
            }
        });
        return {"resultCode":resultCode,"resultMessage":resultMessage};
    }
</script>