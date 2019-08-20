<%@page import="com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">

	<tr>
		<td colspan="2" valign="top">
			<%
				String navigationBasePath = request.getContextPath();
				PluginInstData pluginInstData =(PluginInstData)session.getAttribute("pluginInstance");
		
				String updatePluginInstance  = navigationBasePath+"/initUpdatePluginInstance.do?pluginInstanceId="+pluginInstData.getPluginInstanceId();	
				String viewPluginInstance = navigationBasePath+"/viewPluginInstance.do?action=view&pluginInstanceId="+pluginInstData.getPluginInstanceId();	
				String viewPluginHistory = navigationBasePath+"/viewRadiusGroovyPluginHistory.do?pluginInstanceId="+pluginInstData.getPluginInstanceId()+"&auditUid="+pluginInstData.getAuditUId()+"&name="+pluginInstData.getName();;	
			%>

			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.action" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" onClick="STB('UpdateRadiusPolicy');swapImages()">
							<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow" />
						</a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=updatePluginInstance%>">
											<bean:message bundle="pluginResources" key="plugin.updateplugin" />
										</a>
									</td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.view" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" onClick="STB('ViewRadiusPolicy');swapImages()">
							<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow">
						</a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=viewPluginInstance%>">
											<bean:message bundle="pluginResources" key="plugin.viewplugin" />
										</a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewPluginHistory%>">
											<bean:message bundle="pluginResources" key="plugin.history" />
										</a>
									</td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>

</table>

