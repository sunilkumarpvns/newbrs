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
        <h3 class="panel-title"><s:text name="ratecard.search"/>&nbsp;<s:text name='generic.common.header' /> </h3>
    </div>

    <div class="panel-body">
       <nv:dataTable
                id="rateCardData"
                list="${dataListAsJson}"
                rows="<%=rows%>"
                width="100%"
                showFilter="true"
                showPagination="true"
                cssClass="table table-blue">
            <nv:dataTableColumn  title="Name" 					beanProperty="name" hrefurl="${pageContext.request.contextPath}/pd/ratecard/rate-card/$<id>" sortable="true"  />
            <nv:dataTableColumn  title="Description" 			beanProperty="description" />
         <%--    <nv:dataTableColumn  title="Rate Upload Format" 	beanProperty="rateFileFormatData.name" /> --%>
             <nv:dataTableColumn title="Rate UOM" 				beanProperty="rateUom" />
             <nv:dataTableColumn title="Pulse UOM" 				beanProperty="pulseUom" />
            <nv:dataTableColumn  title="Accounting Effect"   	beanProperty="accountEffect"/>
           
        </nv:dataTable>
    </div>
</div>

