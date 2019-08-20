<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%
 int rows = ConfigurationProvider.getInstance().getPageRowSize();
 String criteriaJson = (String)request.getAttribute(Attributes.CRITERIA);
%>

<script type="text/javascript">
var jsonString = <%=criteriaJson%>;
if(isNullOrEmpty(jsonString) == false){
	var criteria = jsonString;
}
</script>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<s:text name="audit.information" />
		</h3>
	</div>

	<div class="panel-body">
		<nv:dataTable 
			id="historyAuditData"
			actionUrl="/searchTable/policydesigner/util/Nvsmx"
			beanType="com.elitecore.nvsmx.commons.model.audit.AuditDataWrapper" 
			rows="15"
			width="100%"
			showPagination="true"
			cssClass="table table-blue"
			subTableUrl="/commons/audit/Audit/viewDetail">

			<nv:dataTableColumn title="Resource" beanProperty="resourceName"  />
			<nv:dataTableColumn title="Log Message" beanProperty="message" />
			<nv:dataTableColumn title="Staff" beanProperty="staffUserName" />
			<nv:dataTableColumn title="TimeStamp" beanProperty="auditDate"  sortable="true"/>
			<nv:dataTableColumn title="Client IP" beanProperty="clientIp" />
			
		</nv:dataTable>
	</div>
</div>