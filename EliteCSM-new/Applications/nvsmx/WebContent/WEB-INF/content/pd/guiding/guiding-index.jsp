<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>

 <%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();


%>
 
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="guiding.search"/> </h3>
    </div>
    <div class="panel-body">
        <div class="dataTable-button-groups">
			<span class="btn-group btn-group-sm" >
	               	<a href="${pageContext.request.contextPath}/pd/guiding/guiding/new" class="btn btn-default" role="button" id="btnAddGuiding">
	                   	<span class="glyphicon glyphicon-plus" title="Add"></span>
	                </a>
			</span>
        </div>
       	<nv:dataTable
                id="GuidingData"
                list="${dataListAsJson}"
                rows="<%=rows%>"  
                width="100%"
                showPagination="true"
                showFilter="true"
                cssClass="table table-blue"> 
             <nv:dataTableColumn title="<input type='checkbox' name='selectAll' id='selectAll'/>" beanProperty="id"  style="width:5px !important;"/>
             <nv:dataTableColumn title="Guiding Name" beanProperty="guidingName" hrefurl="${pageContext.request.contextPath}/pd/guiding/guiding/$<id>" sortable="true" tdCssClass="word-break" />
             <nv:dataTableColumn title="Lob Name" 	beanProperty="lobData.name" sortable="true"/>
             <nv:dataTableColumn title="Service Name" 	beanProperty="serviceData.name" sortable="true"/>
             <nv:dataTableColumn title="Account Identifier Type" 	beanProperty="accountIdentifierType" sortable="true"/>
              <nv:dataTableColumn title="Account Identifier Value" 		    beanProperty="accountIdentifierValue" sortable="true"/>
             <nv:dataTableColumn title="Account Number" 	beanProperty="accountData.name" sortable="true"/>
             <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/pd/guiding/guiding/$<id>/edit" style="width:20px;border-right:0px;"/>
             <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/pd/guiding/guiding/$<id>?_method=DELETE" style="width:20px;"  />
        </nv:dataTable>
    </div>
</div>

