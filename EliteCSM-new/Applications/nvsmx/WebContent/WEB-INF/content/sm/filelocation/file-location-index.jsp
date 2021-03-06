<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>

<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();

%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="filelocation.search"/> </h3>
    </div>
    <div class="panel-body">
        <div class="dataTable-button-groups">
				<span class="btn-group btn-group-sm" >
                	<a href="${pageContext.request.contextPath}/sm/filelocation/file-location/new" class="btn btn-default" role="button" id="btnAddFileLocation">
                    	<span class="glyphicon glyphicon-plus" title="Add"></span>
                    </a>
				</span>
        </div>
       	<nv:dataTable
                id="FileLocationData"
                list="${dataListAsJson}"
                rows="<%=rows%>"
                width="100%"
                showPagination="true"
                showFilter="true"
                cssClass="table table-blue"> 
            <nv:dataTableColumn title="<input type='checkbox' name='selectAll' id='selectAll'/>" beanProperty="id"  style="width:5px !important;"/>
            <nv:dataTableColumn title="Name" 		    beanProperty="name" hrefurl="${pageContext.request.contextPath}/sm/filelocation/file-location/$<id>" sortable="true" tdCssClass="word-break" />
            <nv:dataTableColumn title="Description" 	beanProperty="description" sortable="true"/>
            <nv:dataTableColumn title="Input path" 		beanProperty="inputPath" tdCssClass="word-break" sortable="true"/>
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/sm/filelocation/file-location/$<id>/edit" style="width:20px;border-right:0px;"/>
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/sm/filelocation/file-location/$<id>?_method=DELETE" style="width:20px;"  />
        </nv:dataTable>
    </div>
</div>

