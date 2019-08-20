<%@page import="com.elitecore.elitesm.web.plugins.forms.QuotaManagementPluginForm"%>
<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>

<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" />
<script type="text/javascript"  src="<%=request.getContextPath()%>/js/plugin/quota-mgt-plugin-view.js"></script>
<%
	QuotaManagementPluginForm quotaManagementPluginForm = (QuotaManagementPluginForm)request.getAttribute("quotaManagementPluginForm");
%>
 
 
	 <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >            						  	
	  	 <tr> 
			<td class="tblheader-bold" height="20%" colspan="2">
				<bean:message bundle="pluginResources" key="plugin.view"/>
			</td>
		</tr>  
	     <tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="2">
			<bean:message bundle="pluginResources" key="plugin.plugininstancedetails" /></td>
		 </tr>  					
	   	 <tr>
			<td class="tblfirstcol" width="13%" height="20%" >
				<bean:message bundle="pluginResources" key="plugin.instname" />
			</td>
			<td class="tblcol" width="30%" height="20%" >
				<bean:write name="quotaManagementPluginForm" property="pluginName" />&nbsp;
			</td>
		 </tr>   
	 	 <tr>
			<td class="tblfirstcol" width="13%" height="20%">
				<bean:message bundle="pluginResources" key="plugin.instdesc" />
			</td>
			<td class="tblcol" width="30%" height="20%" >
				<bean:write name="quotaManagementPluginForm" property="description"/>&nbsp;
			</td>
		</tr>
		<tr>
			<td class="tblfirstcol" width="13%" height="20%">
				<bean:message bundle="pluginResources" key="plugin.status" />
			</td>
			<td class="tblcol" width="30%" height="20%" valign="middle">
				<logic:equal name="quotaManagementPluginForm" property="status" value="1">
				    <img src="<%=basePath%>/images/active.jpg" class="status_class" />&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.active" />
				</logic:equal>
				<logic:equal name="quotaManagementPluginForm" property="status" value="0">
				   <img src="<%=basePath%>/images/deactive.jpg" class="status_class"  />&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.inactive" />
				</logic:equal>
			</td>
		</tr>
		<% if (quotaManagementPluginForm.getQuotaMgtJson() != null) { %>
		<tr> 
			<td class="tblheader-bold" height="20%" colspan="2">
				<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.title" />
			</td>
		</tr>  
		<tr>
			<td  class="tblfirstcol" height="20%" colspan="2">
				<table width="100%" cellspacing="0" cellpadding="0" border="0" id="plugin-mapping-table">
					<tbody>
					</tbody>
				</table>
			</td>
		</tr>
		
		<%} %>
	</table>
	
	<!-- External include files list -->
	<%@include file="/jsp/servermgr/plugins/quotamanagement/ViewQuotaMgtPlugin.jsp" %>
	
	<script type="text/javascript">
	
		/* Convert JSON format Data to Quota Management Plugin List */
		initializedQuotaMgtPlugins(JSON.stringify(<%=quotaManagementPluginForm.getQuotaMgtJson()%>));

	</script>
	
