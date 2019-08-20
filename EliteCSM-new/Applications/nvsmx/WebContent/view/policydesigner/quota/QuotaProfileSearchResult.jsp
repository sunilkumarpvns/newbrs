<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<%
	int rows = ConfigurationProvider.getInstance().getPageRowSize();
	
%> 

<script>
var criteria = ${criteriaJson}; 
</script>
<div class="panel panel-primary">
		<div class="panel-heading">
			<h3 class="panel-title">
				<s:text name="quota.search"/>
			</h3>
		</div>
		<div class="panel-body">
		<nv:dataTable 
			id="QuotaProileSearchResult" 
			actionUrl="/genericSearch/policydesigner/quota/QuotaProfile/searchData"
			beanType="com.elitecore.corenetvertex.pkg.quota.QuotaProfileData"
			rows="<%=rows %>"
			width="100%"
			showPagination="true" 
			showInfo="true"
			cssClass="table table-blue">
			<nv:dataTableColumn title="Quota Profile" beanProperty="name"
				tdCssClass="text-left text-middle word-break"
				hrefurl="${pageContext.request.contextPath}/policydesigner/quota/QuotaProfile/view?quotaProfileId=id"
				tdStyle="width:200px" />
			<nv:dataTableColumn title="Package" beanProperty="pkgData.name"
				tdCssClass="text-left text-middle word-break"
				tdStyle="width:200px" />
			<nv:dataTableColumn title="Description" beanProperty="description"
				tdCssClass="text-left text-middle word-break"
				tdStyle="width:200px" />
		</nv:dataTable>
	</div>
</div>