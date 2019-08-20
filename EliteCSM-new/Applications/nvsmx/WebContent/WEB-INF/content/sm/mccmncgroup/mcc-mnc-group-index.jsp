<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags/dialog" prefix="nvx" %>

<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();

%>
<style>
    .subtable td:nth-child(odd) {
        text-align:left;
        font-weight:bold;
        width: 110px;
    }

    .subtable td:nth-child(even) {
        text-align:left;
        width: 110px;
    }

    .collapsing {
        -webkit-transition: none;
        transition: none;
    }
    
</style>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/import.css"/>
<%
    String criteriaJson = (String)request.getAttribute(Attributes.CRITERIA);
%>
<s:form  id="mccmncgroupDataSearch" method="post" enctype="multipart/form-data" cssClass="form-vertical">
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <s:text name="mccmncgroup.search"/>
            </h3>
        </div>

        <div class="panel-body">
            <div class="dataTable-button-groups">
					<span class="btn-group btn-group-sm" >
						<a href="${pageContext.request.contextPath}/sm/mccmncgroup/mcc-mnc-group/new"
                           class="btn btn-default" role="button" id="btnAddGroup">
                            <span class="glyphicon glyphicon-plus" title="Add"></span>
                        </a>
                        <span class="btn-group btn-group-sm" id="btnRemoveMCCMNCGroup"
                              data-toggle="confirmation-singleton"
                              onmousedown="return removeRecords(this,'mccmncgroupDataSearch');"
                              data-href="javascript:removeData('mccmncgroupDataSearch','${pageContext.request.contextPath}/sm/mccmncgroup/mcc-mnc-group/*/destroy');">
                       <button id="btnDeleteMCCMNCGroup" type="button" class="btn btn-default"
                               data-toggle="tooltip" data-placement="right" title="delete"
                               role="button">
                            <span class="glyphicon glyphicon-trash" title="delete"></span>
                       </button>
                     </span>
					</span>

            </div>
            <nv:dataTable
                    id="mccmncgroupData"
                    list="${dataListAsJson}"
                    rows="<%=rows%>"
                    width="100%"
                    showPagination="true"
                    showFilter="true"
                    cssClass="table table-blue">
                <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
                <nv:dataTableColumn title="Name" beanProperty="name" hrefurl="${pageContext.request.contextPath}/sm/mccmncgroup/mcc-mnc-group/$<id>" sortable="true" style="width:100px;" />
                <nv:dataTableColumn title="Brand" beanProperty="brandData.name" tdCssClass="word-break" sortable="true"/>
                <nv:dataTableColumn title="Description" beanProperty="description" tdCssClass="word-break" />
                <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/sm/mccmncgroup/mcc-mnc-group/$<id>/edit" style="width:20px;"/>
                <nv:dataTableColumn title="" 	 icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/sm/mccmncgroup/mcc-mnc-group/$<id>?_method=DELETE" 	 style="width:20px;"  />
            </nv:dataTable>
        </div>
    </div>
</s:form>
<%@include file="/WEB-INF/content/sm/utility/indexPageUtility.jsp"%>