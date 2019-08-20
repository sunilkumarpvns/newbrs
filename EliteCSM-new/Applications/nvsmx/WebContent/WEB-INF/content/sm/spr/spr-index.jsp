<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 13/9/17
  Time: 5:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>

<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();

%>

<s:form id="sprSearchForm" method="post" enctype="multipart/form-data" cssClass="form-vertical">
<div class="panel panel-primary">
<div class="panel-heading">
<h3 class="panel-title"><s:text name="spr.search"/> </h3>
</div>
    <div class="panel-body">
        <div class="dataTable-button-groups">
            <span class="btn-group btn-group-sm" >
                <a href="${pageContext.request.contextPath}/sm/spr/spr/new" class="btn btn-default" role="button" id="btnAdd">
                    <span class="glyphicon glyphicon-plus" title="Add"></span>
                </a>
                  <span class="btn-group btn-group-sm" id="btnRemoveSPR"
                        data-toggle="confirmation-singleton"
                        onmousedown="return removeRecords(this,'sprSearchForm');"
                        data-href="javascript:removeData('sprSearchForm','${pageContext.request.contextPath}/sm/spr/spr/*/destroy');">
                       <button id="btnDeleteSPR" type="button" class="btn btn-default"
                               data-toggle="tooltip" data-placement="right" title="delete"
                               role="button">
                            <span class="glyphicon glyphicon-trash" title="delete"></span>
                       </button>
                  </span>
            </span>
        </div>

        <nv:dataTable
                id="sprDataList"
                list="${dataListAsJson}"
                rows="<%=rows%>"
                width="100%"
                showPagination="true"
                showFilter="true"
                cssClass="table table-blue">
            <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' class='selectAll'/>"  beanProperty="id"  style="width:5px !important;" />
            <nv:dataTableColumn title="Name" beanProperty="name" hrefurl="${pageContext.request.contextPath}/sm/spr/spr/$<id>" sortable="true"  />
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/sm/spr/spr/$<id>/edit" style="width:20px;border-right:0px;"/>
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/sm/spr/spr/$<id>?_method=DELETE" 	 style="width:20px;"  />
        </nv:dataTable>
    </div>

</div>
</s:form>
<%@include file="/WEB-INF/content/sm/utility/indexPageUtility.jsp"%>