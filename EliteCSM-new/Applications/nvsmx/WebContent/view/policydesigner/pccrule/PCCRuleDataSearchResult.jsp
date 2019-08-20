<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<%
	int rows = ConfigurationProvider.getInstance().getPageRowSize();
%> 
<script>
var criteria = ${criteriaJson};
function renderServiceType(data, type, thisBean){
    if(data == undefined || data == ""){
        return "";
	}
    return "<a href='${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/view?serviceTypeId="+thisBean.dataServiceTypeData.id+"'>"+data+"</a>";
}
</script>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<s:text name="pccrule.search" />
		</h3>
	</div>
	<div class="panel-body">
		<nv:dataTable
			id="PCCRuleDataSearch"
			actionUrl="/genericSearch/policydesigner/pccrule/PccRule/searchData	"
			beanType="com.elitecore.nvsmx.policydesigner.model.pkg.pccrule.PccRuleWrapper"
			rows="<%=rows%>"
			width="100%"
			showPagination="true"
			cssClass="table table-blue">
			
			<nv:dataTableColumn title="PCCRule" beanProperty="name"
				tdCssClass="text-left text-middle word-break"
				hrefurl="${pageContext.request.contextPath}/policydesigner/pccrule/PccRule/view?pccRuleId=id"
				tdStyle="width:200px" />
			<nv:dataTableColumn title="Service"
				beanProperty="dataServiceTypeData.name"
				tdCssClass="text-left text-middle word-break" tdStyle="width:100px"
				renderFunction="renderServiceType"/>
			<nv:dataTableColumn title="Type"
				beanProperty="type"
				tdCssClass="text-left text-middle word-break" tdStyle="width:100px" />
			<nv:dataTableColumn title="Monitoring Key"
				beanProperty="monitoringKey"
				tdCssClass="text-left text-middle word-break" tdStyle="width:200px" />
		</nv:dataTable>
	</div>
</div>