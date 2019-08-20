<%@page import="com.elitecore.elitesm.web.plugins.forms.UpdateUniversalDiameterPluginForm"%>
<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/plugin/universal-diameter-plugin-view.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/plugin/plugin-button.css" />

<%
	UpdateUniversalDiameterPluginForm updateUniversalDiameterPluginForm = (UpdateUniversalDiameterPluginForm)request.getAttribute("updateUniversalDiameterPluginForm");
%>
 
 <html:form action="/updateUniversalDiameterPlugin" styleId="uniAuthPlugin">
 	 <html:hidden name="updateUniversalDiameterPluginForm" property="universalPluginPolicyJson" styleId="universalPluginPolicyJson" />
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
			<td class="tblfirstcol" width="20%" height="20%" >
				<bean:message bundle="pluginResources" key="plugin.instname" />
			</td>
			<td class="tblcol" width="30%" height="20%" >
				<bean:write name="updateUniversalDiameterPluginForm" property="pluginName" />
			</td>
		 </tr>   
	 	 <tr>
			<td class="tblfirstcol" width="20%" height="20%">
				<bean:message bundle="pluginResources" key="plugin.instdesc" />
			</td>
			<td class="tblcol" width="30%" height="20%" >
				<bean:write name="updateUniversalDiameterPluginForm" property="description"/>
			</td>
		</tr>
		<tr>
			<td class="tblfirstcol" width="20%" height="20%">
				<bean:message bundle="pluginResources" key="plugin.status" />
			</td>
			<td class="tblcol" width="30%" height="20%" valign="middle">
				<logic:equal name="updateUniversalDiameterPluginForm" property="status" value="1">
				    <img src="<%=basePath%>/images/active.jpg"/>&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.active" />
				</logic:equal>
				<logic:equal name="updateUniversalDiameterPluginForm" property="status" value="0">
				   <img src="<%=basePath%>/images/deactive.jpg" />&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.inactive" />
				</logic:equal>
			</td>
		</tr>
		 <tr>
			<td class="tblheader-bold" width="20%" height="20%" colspan="2">
				<bean:message bundle="pluginResources" key="plugin.universalpluginpolicies" />
			</td>
		</tr>
		
		<% if (updateUniversalDiameterPluginForm.getUniversalPluginPolicyJson() != null) { %>
		
		<tr>
			<td class="tblfirstcol" height="20%" colspan="2">
				<table width="100%" cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td>
							 <div class="plugin-box">
				                <table width="100%" cellspacing="0" cellpadding="0" border="0">
				                	<tr>
				                		<td>
											<div class="table-header plugin-table-header-css captiontext" style="background-color: #D9E6F6;line-height: 15px;">
												<bean:message bundle="pluginResources" key="plugin.acct.universalinpolicy.list" />
											</div>
											<div class="captiontext"  style="padding: 5px;">
												<table width="100%" cellspacing="0" cellpadding="0" border="0" id="plugin-mapping-table">
													<tbody class="parent sortableClass ui-sortable">
													</tbody>
												</table>
											</div>
										</td>
				                	</tr>
				                 </table>
				              </div><!-- /.plugin-box -->
						 </td>
					</tr>
					<tr>
						<td>
							 <div class="plugin-box">
				                <table width="100%" cellspacing="0" cellpadding="0" border="0">
				                	<tr>
				                		<td>
											<div class="table-header plugin-table-header-css captiontext" style="background-color: #D9E6F6;line-height: 15px;">
												<bean:message bundle="pluginResources" key="plugin.acct.universaloutpolicy.list" />
											</div>
											<div class="captiontext"  style="padding: 5px;">
												<table width="100%" cellspacing="0" cellpadding="0" border="0" id="plugin-post-mapping-table">
													<tbody class="parent sortableClass ui-sortable">
													</tbody>
												</table>
											</div>
										</td>
				                	</tr>
				                 </table>
				              </div><!-- /.plugin-box -->
						</td>
					</tr>
					<tr>
						<td>
							&nbsp;
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<%
			}
		%>
	</table>
</html:form>

<!-- External include files list -->
<%@include file="/jsp/servermgr/plugins/universalplugin/ViewUniversalInPlugin.jsp" %>
<%@include file="/jsp/servermgr/plugins/universalplugin/ViewUniversalOutPlugin.jsp" %>

<script type="text/javascript">
	
	/* Convert JSON format Data to Universal Plugin List */
	initializedPlugins($('#universalPluginPolicyJson').val());

</script>
