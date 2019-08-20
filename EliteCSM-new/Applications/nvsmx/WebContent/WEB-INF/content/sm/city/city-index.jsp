<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<%@ taglib prefix="nv" uri="http://www.elitecore.com/nvsmx/tags" %>
<%@ page import="com.elitecore.nvsmx.system.ConfigurationProvider" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 6/11/17
  Time: 3:09 PM
  To change this template use File | Settings | File Templates.
--%>
<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>
<script>
    function regionRender(data, type, thisBean){
        return "<a href='${pageContext.request.contextPath}/sm/region/region/"+thisBean.regionData.id+"'>"+data+"</a>";
    }
</script>
<s:form  id="cityDataSearch" method="post" enctype="multipart/form-data" cssClass="form-vertical">
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="city.search" />
        </h3>
    </div>

    <div class="panel-body">
        <div class="dataTable-button-groups">
            <span class="btn-group btn-group-sm" >
                <a href="${pageContext.request.contextPath}/sm/city/city/new" class="btn btn-default" role="button" id="btnAdd">
                    <span class="glyphicon glyphicon-plus" title="Add"></span>
                </a>
                    <span class="btn-group btn-group-sm" id="btnRemoveCity"
                    data-toggle="confirmation-singleton"
                    onmousedown="return removeRecords(this,'cityDataSearch');"
                    data-href="javascript:removeData('cityDataSearch','${pageContext.request.contextPath}/sm/city/city/*/destroy');">
                    <button id="btnDeleteCity" type="button" class="btn btn-default"
                    data-toggle="tooltip" data-placement="right" title="delete"
                    role="button">
                    <span class="glyphicon glyphicon-trash" title="delete"></span>
                    </button>
                    </span>
            </span>
        </div>

        <nv:dataTable
                id="cityDatas"
                list="${dataListAsJson}"
                rows="<%=rows%>"
                width="100%"
                showPagination="true"
                showFilter="true"
                cssClass="table table-blue">
            <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
            <nv:dataTableColumn title="Name" 		 beanProperty="name" hrefurl="${pageContext.request.contextPath}/sm/city/city/$<id>" sortable="true"  />
            <nv:dataTableColumn title="Region" 		 beanProperty="regionData.name" renderFunction="regionRender"/>
            <nv:dataTableColumn title="Country"      beanProperty="regionData.countryData.name" />
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:javascript:updateCity(id)" style="width:20px;border-right:0px;"/>
            <nv:dataTableColumn title="" 	 icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/sm/city/city/$<id>?_method=DELETE" 	 style="width:20px;"  />
        </nv:dataTable>

    </div>
</div>
</s:form>
<script>
    var data = ${dataListAsJson};
</script>
<%@include file="city-edit.jsp"%>
<%@include file="/WEB-INF/content/sm/utility/indexPageUtility.jsp"%>