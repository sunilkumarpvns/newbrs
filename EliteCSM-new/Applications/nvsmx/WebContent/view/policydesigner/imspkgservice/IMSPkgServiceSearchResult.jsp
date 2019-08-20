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
				<s:text name="ims.service.search"></s:text>
			</h3>
		</div>
		<div class="panel-body">
		<nv:dataTable 
			id="IMSPkgServices" 
			actionUrl="/genericSearch/policydesigner/imspkgservice/IMSPkgService/searchData"
			beanType="com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData"
			rows="<%=rows %>"
			width="100%"
			showPagination="true" 
			showInfo="true"
			cssClass="table table-blue">
			<nv:dataTableColumn title="IMS Package Service" beanProperty="name"
				hrefurl="${pageContext.request.contextPath}/policydesigner/ims/IMSPkg/view?pkgId=imsPkgData.id"
				tdCssClass="text-left text-middle word-break"
				tdStyle="width:200px" />
			<nv:dataTableColumn title="IMS Package" beanProperty="imsPkgData.name"
				tdCssClass="text-left text-middle word-break"
				tdStyle="width:200px" />
			<nv:dataTableColumn title="Media Type" beanProperty="mediaTypeData.name"
				tdCssClass="text-left text-middle word-break" />
			<nv:dataTableColumn title="AF Application ID" beanProperty="afApplicationId"
				tdCssClass="text-left text-middle word-break" />
			<nv:dataTableColumn title="Action" beanProperty="action"
				tdCssClass="text-left text-middle word-break" />
			<nv:dataTableColumn title="" sortable="false"
				icon="<span class='glyphicon glyphicon-pencil'></span>"
				style="width:20px;" tdStyle="width:20px;" tdCssClass="text-middle"
				hrefurl="edit:${pageContext.request.contextPath}/policydesigner/imspkgservice/IMSPkgService/init?imsPkgServiceId=id" />
			<nv:dataTableColumn title=""
				icon="<span class='glyphicon glyphicon-trash'></span>"
				hrefurl="delete:${pageContext.request.contextPath}/policydesigner/imspkgservice/IMSPkgService/delete?imsPkgServiceId=id"
				style="width:20px;" tdStyle="width:20px;" tdCssClass="text-middle" />
		</nv:dataTable>
	</div>
</div>