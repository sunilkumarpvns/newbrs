<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@page import="com.google.gson.JsonArray"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv"%>

<%
JsonArray auditDataJson = (JsonArray)request.getAttribute(Attributes.VIEW_HISTORY);
Attributes.count = 0;
%>
<div class="panel panel-primary">
	<div class="panel-heading" style="padding: 8px 15px">
		<h3 class="panel-title" style="display: inline;">${auditPageHeadingName} History</h3>
	</div>
	<div class="panel-body">
		<nv:dataTable id="historyAuditData"
			list="<%=auditDataJson.toString()%>" 
			rows="10"
			width="100%"
			showPagination="false" 
			showInfo="false" 
			cssClass="table table-blue"
			subTableUrl="/commons/audit/Audit/viewDetail?pageId=${actualId}">
			<nv:dataTableColumn title="Staff" beanProperty="staffUserName" tdCssClass="text-left text-middle" style="width:100px" />
			<nv:dataTableColumn title="Log Message" beanProperty="message" tdCssClass="text-left text-middle" style="width:100px" />
			<nv:dataTableColumn title="Client IP" beanProperty="clientIp" tdCssClass="text-left text-middle" style="width:100px" />
			<nv:dataTableColumn title="TimeStamp" beanProperty="auditDate" tdCssClass="text-left text-middle" style="width:100px" />
		</nv:dataTable>
		<div class="row">
			<div class="col-xs-12" align="center">
				<button id="btnBack" class="btn btn-primary btn-sm"  value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}${refererUrl}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back" /> </button>
			</div>
		</div>
	</div>
</div>
