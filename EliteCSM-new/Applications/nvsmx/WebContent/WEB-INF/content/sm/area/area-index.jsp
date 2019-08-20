<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<%@ taglib prefix="nv" uri="http://www.elitecore.com/nvsmx/tags" %>
<%@ page import="com.elitecore.nvsmx.system.ConfigurationProvider" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 8/11/17
  Time: 12:03 PM
  To change this template use File | Settings | File Templates.
--%>
<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>
<script>
    function regionRender(data, type, thisBean){
        console.log(thisBean);
        return "<a href='${pageContext.request.contextPath}/sm/region/region/"+thisBean.cityData.regionData.id+"'>"+data+"</a>";
    }
    function cityRender(data, type, thisBean){
        return "<a href='${pageContext.request.contextPath}/sm/city/city/"+thisBean.cityData.id+"'>"+data+"</a>";
    }
</script>
<s:form  id="areaDataSearch" method="post" enctype="multipart/form-data" cssClass="form-vertical">
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="area.search" />
        </h3>
    </div>

    <div class="panel-body">
        <div class="dataTable-button-groups">
            <span class="btn-group btn-group-sm" >
                <a href="${pageContext.request.contextPath}/sm/area/area/new" class="btn btn-default" role="button" id="btnAdd">
                    <span class="glyphicon glyphicon-plus" title="Add"></span>
                </a>
                    <span class="btn-group btn-group-sm" id="btnRemoveArea"
                    data-toggle="confirmation-singleton"
                    onmousedown="return removeRecords(this,'areaDataSearch');"
                    data-href="javascript:removeData('areaDataSearch','${pageContext.request.contextPath}/sm/area/area/*/destroy');">
                    <button id="btnDeleteCity" type="button" class="btn btn-default"
                    data-toggle="tooltip" data-placement="right" title="delete"
                    role="button">
                    <span class="glyphicon glyphicon-trash" title="delete"></span>
                    </button>
                    </span>
            </span>
        </div>

        <nv:dataTable
                id="areaDatas"
                list="${dataListAsJson}"
                rows="<%=rows%>"
                width="100%"
                showPagination="true"
                showFilter="true"
                cssClass="table table-blue">
            <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll'/>"  beanProperty="id"  style="width:5px !important;" />
            <nv:dataTableColumn title="Name" 		 beanProperty="name" hrefurl="${pageContext.request.contextPath}/sm/area/area/$<id>" sortable="true"  />
            <nv:dataTableColumn title="City" 		 beanProperty="cityData.name"   renderFunction="cityRender"/>
            <nv:dataTableColumn title="Region" 		 beanProperty="cityData.regionData.name"  renderFunction="regionRender"/>
            <nv:dataTableColumn title="Country"      beanProperty="cityData.regionData.countryData.name"   />
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/sm/area/area/$<id>/edit" style="width:20px;border-right:0px;"/>
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/sm/area/area/$<id>?_method=DELETE" 	 style="width:20px;"  />
        </nv:dataTable>

    </div>
</div>
</s:form>
<%@include file="/WEB-INF/content/sm/utility/indexPageUtility.jsp"%>