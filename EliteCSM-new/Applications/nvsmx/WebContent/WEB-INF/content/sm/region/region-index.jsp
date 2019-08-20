<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<%@ taglib prefix="nv" uri="http://www.elitecore.com/nvsmx/tags" %>
<%@ page import="com.elitecore.nvsmx.system.ConfigurationProvider" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 31/10/17
  Time: 3:58 PM
  To change this template use File | Settings | File Templates.
--%>
<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();


%>
<s:form  id="regionDataSearch" method="post" enctype="multipart/form-data" cssClass="form-vertical">
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="region.search"/>
        </h3>
    </div>

    <div class="panel-body">
        <div class="dataTable-button-groups">
                <span class="btn-group btn-group-sm" >
                    <a href="${pageContext.request.contextPath}/sm/region/region/new" class="btn btn-default" role="button" id="btnAdd">
                        <span class="glyphicon glyphicon-plus" title="Add"></span>
                    </a>
                        <span class="btn-group btn-group-sm" id="btnRemoveRegion"
                        data-toggle="confirmation-singleton"
                        onmousedown="return removeRecords(this,'regionDataSearch');"
                        data-href="javascript:removeData('regionDataSearch','${pageContext.request.contextPath}/sm/region/region/*/destroy');">
                        <button id="btnDeleteRegion" type="button" class="btn btn-default"
                        data-toggle="tooltip" data-placement="right" title="delete"
                        role="button">
                        <span class="glyphicon glyphicon-trash" title="delete"></span>
                        </button>
                        </span>
                </span>
        </div>

        <nv:dataTable
                id="regionData"
                list="${dataListAsJson}"
                rows="<%=rows%>"
                width="100%"
                showPagination="true"
                showFilter="true"
                cssClass="table table-blue">
            <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
            <nv:dataTableColumn title="Name" 		 beanProperty="name" hrefurl="${pageContext.request.contextPath}/sm/region/region/$<id>"  />
            <nv:dataTableColumn title="Country" 		 beanProperty="countryData.name" style="width:50%;" />
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:javascript:updateRegionName(id)" style="width:20px;border-right:0px;"/>
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/sm/region/region/$<id>?_method=DELETE" 	 style="width:20px;"  />
        </nv:dataTable>

    </div>
</div>
</s:form>
<script>
    var data = ${dataListAsJson};
</script>

<%@include file="region-edit.jsp"%>
<%@include file="/WEB-INF/content/sm/utility/indexPageUtility.jsp"%>
