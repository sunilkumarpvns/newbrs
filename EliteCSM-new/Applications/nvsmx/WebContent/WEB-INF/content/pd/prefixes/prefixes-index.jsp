<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>

<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="prefixes.search"/> </h3>
    </div>
    <div class="panel-body">
        <div class="row">
            <div class="col-xs-5">
				<span class="btn-group btn-group-sm" >
                     <a href="${pageContext.request.contextPath}/pd/prefixes/prefixes/new?masterPrefix=DEFAULT_PREFIX_LIST_MASTER_ID" class="btn btn-default" role="button" id="btnAddPrefixes">
                       <span class="glyphicon glyphicon-plus" title="Add"></span>
                     </a>
				</span>
			</div>
        </div>
       <nv:dataTable
                id="PrefixesData"
                list="${dataListAsJson}"
                rows="<%=rows%>"
                width="100%"
                showPagination="true"
                cssClass="table table-blue"> 
            <nv:dataTableColumn title="<input type='checkbox' name='selectAll' id='selectAll'/>" beanProperty="id"  style="width:5px !important;"/>
            <nv:dataTableColumn title="Prefix Name" 		    beanProperty="name" hrefurl="${pageContext.request.contextPath}/pd/prefixes/prefixes/$<id>?masterPrefix=DEFAULT_PREFIX_LIST_MASTER_ID" sortable="true" tdCssClass="word-break" />
            <nv:dataTableColumn title="Description" 	beanProperty="description"  sortable="true"/>
            <nv:dataTableColumn title="Country Code" 		beanProperty="countryCode" sortable="true" tdCssClass="word-break"/>
            <nv:dataTableColumn title="Area Code" 		beanProperty="areaCode" sortable="true" tdCssClass="word-break"/>
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/pd/prefixes/prefixes/$<id>/edit?masterPrefix=DEFAULT_PREFIX_LIST_MASTER_ID" style="width:20px;border-right:0px;"/>
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/pd/prefixes/prefixes/$<id>?_method=DELETE&masterPrefix=DEFAULT_PREFIX_LIST_MASTER_ID" style="width:20px;"  />
        </nv:dataTable>
    </div>
</div>
