
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.BaseConstant"%>
<jsp:directive.page import="com.elitecore.netvertexsm.web.core.system.profilemanagement.ProfileManager"/>
<jsp:directive.page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"/>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">

			
			
			
			

			<%
			            String navigationBasePath = request.getContextPath();
			            INetServerInstanceData netServerInstanceData = (INetServerInstanceData) request.getAttribute("netServerInstanceData");

			   
			            String updateServerBasicDetails = navigationBasePath + "/updateNetServerInstanceBasicDetail.do?netserverid=" + netServerInstanceData.getNetServerId();
			            String changeAdminInterfaceDetails = navigationBasePath + "/updateNetServerAdminInterfaceDetail.do?netserverid=" + netServerInstanceData.getNetServerId();
			            String synchronizeConfiguration = navigationBasePath + "/updateNetServerSynchronizeConfigDetail.do?netserverid=" + netServerInstanceData.getNetServerId();
			            String importServerDetails = navigationBasePath + "/importNetServerDetail.do?netserverid=" + netServerInstanceData.getNetServerId();
			            String exportServerDetails = navigationBasePath + "/exportNetServerDetail.do?netserverid=" + netServerInstanceData.getNetServerId();
			            String addService = navigationBasePath + "/addNetServerServiceInstance.do?netserverid=" + netServerInstanceData.getNetServerId();
			            String removeService = navigationBasePath + "/removeNetServerServiceInstance.do?netserverid=" + netServerInstanceData.getNetServerId();
			            String updateServerConfiguration = navigationBasePath + "/listNetServerConfiguration.do?netserverid=" + netServerInstanceData.getNetServerId();
			            String services = navigationBasePath + "/viewNetServerServices.do?netserverid=" + netServerInstanceData.getNetServerId();
			            String liveServerDetails = navigationBasePath + "/viewNetServerLiveDetails.do?netServerId=" + netServerInstanceData.getNetServerId();
			            String reloadCacheDetails = navigationBasePath + "/viewReloadCacheDetails.do?netServerId=" + netServerInstanceData.getNetServerId();
			            String server = navigationBasePath + "/viewNetServerInstance.do?netserverid=" + netServerInstanceData.getNetServerId();
			            String listUserfileDatasource = navigationBasePath + "/listUserfileDatasource.do?netserverid=" + netServerInstanceData.getNetServerId();

			            String graph = navigationBasePath + "/viewGraph.do?referenceid=" + netServerInstanceData.getNetServerId();
			            String networktree = navigationBasePath + "/viewNetworkTree.do?netServerId=" + netServerInstanceData.getNetServerId();
			            String reloadConfiguration = navigationBasePath + "/netServerReloadConfiguration.do?netserverid=" + netServerInstanceData.getNetServerId();
			            String reloadCache = navigationBasePath + "/netServerReloadCache.do?netserverid=" + netServerInstanceData.getNetServerId();
			            String reloadPolicy = navigationBasePath + "/netServerReloadPolicy.do?netserverid=" + netServerInstanceData.getNetServerId();
			            String startStopServer = navigationBasePath + "/initNetServerStartStop.do?netServerId=" + netServerInstanceData.getNetServerId();
			            String synchronizeDictionaryAction = navigationBasePath + "/updateNetDictionarySynchronize.do?netServerId=" + netServerInstanceData.getNetServerId();
			            String manageDictionaryAction = navigationBasePath + "/manageRadiusDictionary.do?netServerId=" + netServerInstanceData.getNetServerId();
			            String liveServerClientsDetails = navigationBasePath + "/viewNetServerLiveClientsDetails.do?netServerId=" + netServerInstanceData.getNetServerId();
			            String intNetServerLicense = navigationBasePath + "/initNetServerLicense.do?netServerId=" + netServerInstanceData.getNetServerId();
			            String viewsupportedrfc = navigationBasePath + "/viewSupportedRFC.do?netServerId=" + netServerInstanceData.getNetServerId();
			            //String viewLogReport = navigationBasePath + "/viewLogReport.do?netServerId=" + netServerInstanceData.getNetServerId();
			            String syncServerInstanceVersion = navigationBasePath + "/synchronizeServerInstanceVersion.do?netServerInstanceId=" + netServerInstanceData.getNetServerId() + "&action=init";
			            String initSearchFile = navigationBasePath+"/initSearchFile.do?netserverid="+netServerInstanceData.getNetServerId();
						String searchSendPacket=navigationBasePath+"/searchSendPacket.do?netserverid="+netServerInstanceData.getNetServerId();
						String searchRadiusSendPacket=navigationBasePath+"/searchRadiusSendPacket.do?netserverid="+netServerInstanceData.getNetServerId();
			            
						String serverLogList =navigationBasePath+"/initListNetServerLogs.do?method=search&netServerId=" + netServerInstanceData.getNetServerId();
			            String viewPolicyDetail = navigationBasePath + "/viewPolicyStatusAction.do?netServerId=" + netServerInstanceData.getNetServerId();
			            
			%>
<script>
	
	function openLogs() {
    	window.open('<%=serverLogList%>','_blank','width=900px,height=600px,scrollbars=yes,location=no,menubar=no,titlebar=yes');
	}
	function popupNetworkTree(){
		var url = '<%=networktree%>';
		var win1 = window.open(url,'Network Tree','top=100, left=100, height=450, width=800, scrollbars=yes, status');
		win1.focus();
	}
</script>

			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%"><bean:message key="general.action" /></td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)"><img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg"	border="0" name="arrow"/></a>
					</td>
				</tr>
				
				
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
							
								<% if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.UPDATE_NET_SERVER_INSTANCE_BASIC_DETAIL_ACTION)){ %>
								<tr>
									<td class="subLinks">
										<a href="<%=updateServerBasicDetails%>"><bean:message bundle="servermgrResources" key="servermgr.updatebasicdetails"/></a>
									</td>
								</tr>
								<%}%>
								
								<% if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.ADD_NET_SERVER_SERVICE_INSTANCE_ACTION)){ %>
									<tr>
									<td class="subLinks">
										<a href="<%=addService%>"><bean:message bundle="servermgrResources" key="servermgr.addservice" /></a>
									</td>
								</tr>
								<%}%>
								
								<% if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.REMOVE_NET_SERVER_SERVICE_INSTANCE_ACTION)){ %>
								<tr>
									<td class="subLinks">
										<a href="<%=removeService%>"><bean:message bundle="servermgrResources" key="servermgr.removeservice" /></a>
									</td>
								</tr>
								<%}%>
								
								<% if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.UPDATE_NET_SERVER_ADMIN_INTERFACE_DETAIL_ACTION)){ %>
								<tr>
									<td class="subLinks">
										<a href="<%=changeAdminInterfaceDetails%>"><bean:message bundle="servermgrResources" key="servermgr.changeadmininterfacedetails" /></a>
									</td>
								</tr>
								<%}%>
							
							  <% if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.SYNCHRONIZE_BACK_NET_SERVER_CONFIGURATION_ACTION)
								       ||ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.SYNCHRONIZE_NET_SERVER_CONFIGURATION_ACTION) 
								       ||ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.LIST_USERFILE_DATASOURCE_ACTION)
								       ||ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.DOWNLOAD_LICENSE_PUBLIC_KEY_ACTION)
								       ||ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.UPLOAD_LICENSE_ACTION)
								       ||ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.SIGNAL_SOFT_RESTART_ACTION)
								       ||ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.SIGNAL_SERVER_RELOAD_CONFIGURATION_ACTION)
								       ||ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.NET_SERVER_RESTART_ACTION)
								       ||ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.START_NET_SERVER_ACTION)
								       ||ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.SIGNAL_SERVER_SHUTDOWN_ACTION)
								    ){ %>
							
								<tr>
									<td class="sublinks"><bean:message bundle="servermgrResources" key="servermgr.managelivenetserver" /></td>
								</tr>
								
							 <%} %>
								<% if(   (ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.SYNCHRONIZE_BACK_NET_SERVER_CONFIGURATION_ACTION))
								       ||(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.SYNCHRONIZE_NET_SERVER_CONFIGURATION_ACTION))){ %>
								<tr>
									<td class="subLinks">&nbsp;-&nbsp;
										<a href="<%=synchronizeConfiguration%>"><bean:message bundle="servermgrResources" key="servermgr.synchronizeconfiguration" /></a>
									</td>
								</tr>
								<%}%>
								
								<% if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.SYNCHRONIZE_SERVER_VERSION_ACTION)){ %>
								<tr>
									<td class="subLinks">&nbsp;-&nbsp;
										<a href="<%=syncServerInstanceVersion%>"><bean:message bundle="servermgrResources" key="servermgr.synchronize.version" /></a>
									</td>
								</tr>
								<%} %>
								
								<%if(true){ %>
								
								<tr>
									<td class="sublinks">&nbsp;-&nbsp;
										<a href="<%=synchronizeDictionaryAction%>" /><bean:message bundle="servermgrResources" key="servermgr.synchronizedictionary" /></a>
									</td>
								</tr>     
								
								<%} %>
								
								
								<%
								 if (netServerInstanceData.getNetServerType().getNetServerTypeId().equalsIgnoreCase(BaseConstant.NETVERTEX_SERVER)) {
								%>
								
								<% if( (ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.DOWNLOAD_LICENSE_PUBLIC_KEY_ACTION))
								       ||(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.UPLOAD_LICENSE_ACTION))){ %>
								<tr>
									<td class="sublinks">&nbsp;-&nbsp;
										<a href="<%=intNetServerLicense%>" /><bean:message bundle="servermgrResources" key="servermgr.license" /></a>
									</td>
								</tr>
								<%}%>
								
								
							<%}%>
							
							<% if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.SIGNAL_SERVER_RELOAD_CONFIGURATION_ACTION)){ %>
						
								<tr>
									<td class="sublinks">&nbsp;-&nbsp;
										<a href="<%=reloadConfiguration%>" /><bean:message bundle="servermgrResources" key="servermgr.reloadConfiguration" /></a>
									</td>
								</tr>
							<%}%>
								
						      <% if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.SIGNAL_SERVER_RELOAD_CACHE_ACTION)){ %>
								<tr>
									<td class="sublinks">&nbsp;-&nbsp;
										<a href="<%=reloadCache%>" /><bean:message bundle="servermgrResources" key="servermgr.reloadCache" /></a>
									</td>
								</tr>
								<tr>
									<td class="sublinks">&nbsp;-&nbsp;
										<a href="<%=reloadPolicy%>" /><bean:message bundle="servermgrResources" key="servermgr.reloadPolicy" /></a>
									</td>
								</tr>
								<%}%>
								
								<% if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.NET_SERVER_RESTART_ACTION) ||
								      ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.START_NET_SERVER_ACTION) ||
								      ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.SIGNAL_SERVER_SHUTDOWN_ACTION)){ %>
								<tr>
									<td class="sublinks">&nbsp;-&nbsp;
										<a href="<%=startStopServer%>" /><bean:message bundle="servermgrResources" key="servermgr.startstopserver" /></a>
									</td>
								</tr>
								<%}%>
								
								<% if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.LIST_SERVICE_CONFIGURATION_ACTION)){ %>
								<tr>
									<td class="subLinks">
										<a href="<%=updateServerConfiguration%>"><bean:message bundle="servermgrResources" key="servermgr.view.updateserverconfiguration" /></a>
									</td>
								</tr>
								<%}%>
								
								<%if(netServerInstanceData.getVersion().equals("Development Version")){%>
								<tr>
									<td class="sublinks"><bean:message bundle="servermgrResources" key="servermgr.sendpacket" /></td>
								</tr>
								
									<tr>
										<td class="sublinks">&nbsp;-&nbsp;
											<a href="<%=searchSendPacket%>" /><bean:message bundle="servermgrResources" key="servermgr.sendpacket.diameter" /></a>
										</td>
									</tr>
									
									<tr>
										<td class="sublinks">&nbsp;-&nbsp;
											<a href="<%=searchRadiusSendPacket%>" /><bean:message bundle="servermgrResources" key="servermgr.sendpacket.radius" /></a>
										</td>
									</tr>
								<%}%>
								
							</table>
						</div>
					</td>
				</tr>
				
				<tr id=header1>
					<td class="subLinksHeader" width="87%"><bean:message key="general.view" /></td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" onClick="STB('ViewRadiusPolicy');swapImages()"><img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a>
					</td>
				</tr>
				
				
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=server%>"><bean:message bundle="servermgrResources" key="servermgr.view.server" /></a>
									</td>
								</tr>
							
							<% if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.VIEW_CONFIGURED_SERVICES_ACTION)){ %>
								<tr>
									<td class="subLinks">
										<a href="<%=services%>"><bean:message bundle="servermgrResources" key="servermgr.services" /></a>
									</td>
								</tr>
							<%} %>
							
							<% if(  ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.VIEW_SERVER_DETAIL_ACTION)
							      || ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.VIEW_SUPPORTED_RFC_ACTION)
							      || ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.VIEW_SERVER_CLIENTS_DETAIL_ACTION)
							      || ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.VIEW_GRAPH_ACTION)
							      ){ %>
							
								<tr>
									<td class="subLinks"><bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver" /></td>
								</tr>
							
							<%}%>
							
							
								<% if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.VIEW_SERVER_DETAIL_ACTION)){ %>
								<tr>
									<td class="subLinks">&nbsp;-&nbsp;
										<a href="<%=liveServerDetails%>"><bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver.basicdetails" /></a>
									</td>
								</tr>
								<%}%>
								
 								<% if(ProfileManager.getSubMoudleActionStatus(request,response,"User Trace Logs")){ %>
							        <tr>
							         <td class="sublinks">&nbsp;-&nbsp; 
							          <a href="#" onclick="openLogs()" />User Trace Logs</a>
							         </td>
							        </tr>
       							 <%}%>	
       							 <tr>
									<td class="subLinks">&nbsp;-&nbsp;
									<a href="<%=viewPolicyDetail%>" ><bean:message bundle="servermgrResources" key="servermgr.viewpolicystatus" /></a>
									</td>
								</tr> 								
								
								 							
								<%if (netServerInstanceData.getNetServerType().getNetServerTypeId().equalsIgnoreCase(BaseConstant.NETVERTEX_SERVER)) {%>
								
								<tr>
									<td class="sublinks">
									<a href="javascript:void(0);" onclick="popupNetworkTree()"  ><bean:message bundle="servermgrResources" key="servermgr.networktree" /></a> 
									</td>
								</tr><%}%>  
								
							
        
						</table>
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
