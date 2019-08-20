<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<%
	int rows = ConfigurationProvider.getInstance().getPageRowSize();
	
%> 
<script>
var criteria = ${criteriaJson};


/*
* This function is used to provided custom rendering of name column of search page based
* on Emergency or Data packages
* * */
function nameRenderingFunction(data, type, thisBean){
    if (thisBean.pkgData.type == '<s:property value="@com.elitecore.corenetvertex.pkg.PkgType@EMERGENCY.name()" />') {
        return "<a href='${pageContext.request.contextPath}/policydesigner/emergencypkgqos/EmergencyPkgQos/view?qosProfileId="+thisBean.id+"'>"+data+"</a>";
    } else {
        return "<a href='${pageContext.request.contextPath}/policydesigner/qos/QosProfile/view?qosProfileId="+thisBean.id+"'>"+data+"</a>";
    }
}

</script>
<div class="panel panel-primary">
		<div class="panel-heading">
			<h3 class="panel-title">
				<s:text name="qos.search" />
			</h3>
		</div>
		<div class="panel-body">
		<nv:dataTable 
			id="QoSProfileSearchResult"
			actionUrl="/genericSearch/policydesigner/qos/QosProfile/searchData"
			beanType="com.elitecore.corenetvertex.pkg.qos.QosProfileData"
			rows="<%=rows %>"
			width="100%"
			showPagination="true" 
			showInfo="true"
			cssClass="table table-blue">
			<nv:dataTableColumn title="QoS Profile" beanProperty="name"
								tdCssClass="text-left text-middle word-break" renderFunction="nameRenderingFunction"
								tdStyle="width:200px" />
			<nv:dataTableColumn title="Package" beanProperty="pkgData.name"
				tdCssClass="text-left text-middle word-break"
				tdStyle="width:200px" />
			<nv:dataTableColumn title="Quota Profile" beanProperty="quotaProfile.name"
				tdCssClass="text-left text-middle word-break"
				tdStyle="width:200px" />
		</nv:dataTable>
	</div>
</div>