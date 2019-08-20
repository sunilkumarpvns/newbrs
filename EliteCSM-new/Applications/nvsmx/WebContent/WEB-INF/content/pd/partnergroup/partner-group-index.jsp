<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags/dialog" prefix="nvx" %>

<%
    int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="pg.search"/> </h3>
    </div>

    <div class="panel-body">
       <div class="row">
            <div class="col-xs-5">
					<span class="btn-group btn-group-sm" >
                        <a href="${pageContext.request.contextPath}/pd/partnergroup/partner-group/new" class="btn btn-default" role="button" id="btnPartnerGroup">
                            <span class="glyphicon glyphicon-plus" title="Add"></span>
                        </a>
					</span>
            </div>

        </div>

       <nv:dataTable
                id="PartnerGroup"
                list="${dataListAsJson}"
                rows="<%=rows%>"
                width="100%"
                showPagination="true"
                cssClass="table table-blue">
            <nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="id"  style="width:5px !important;" />
            <nv:dataTableColumn title="Name" 		    beanProperty="name" hrefurl="${pageContext.request.contextPath}/pd/partnergroup/partner-group/$<id>" sortable="true"  />
            <nv:dataTableColumn title="Description"   beanProperty="description" />
            <nv:dataTableColumn title="LOB" 		beanProperty="lobData.name" sortable="true"/>
            <nv:dataTableColumn title="Status"   beanProperty="Status" sortable="true"/>
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/pd/partnergroup/partner-group/$<id>/edit" style="width:20px;border-right:0px;"/>
            <nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/pd/partnergroup/partner-group/$<id>?_method=DELETE" 	 style="width:20px;"  />
        </nv:dataTable>
    </div>
</div>
