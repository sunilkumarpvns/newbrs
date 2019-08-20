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
        <h3 class="panel-title"><s:text name="account.search"/>&nbsp;<s:text name='generic.common.header' /> </h3>
    </div>

    <div class="panel-body">
      
       <nv:dataTable
                id="AccountData"
                list="${dataListAsJson}"
                rows="<%=rows%>"
                width="100%"
                showPagination="true"
                showFilter="true"
                showInfo="true"
                cssClass="table table-blue">
            <nv:dataTableColumn title="Account Name" 	beanProperty="name" hrefurl="${pageContext.request.contextPath}/pd/account/account/$<id>" sortable="true"  />
            <nv:dataTableColumn title="Account Manager" beanProperty="accountManager"/>
            <nv:dataTableColumn title="Status" 			beanProperty="Status"/>
        </nv:dataTable>
    </div>
</div>
