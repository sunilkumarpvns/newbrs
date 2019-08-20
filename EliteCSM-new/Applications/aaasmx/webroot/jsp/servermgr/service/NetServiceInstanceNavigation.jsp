<%@ page
	import="com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData"%>
<%@ page import="com.elitecore.elitesm.util.constants.RadiusConstant"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page
	import="com.elitecore.elitesm.web.core.system.profilemanagement.ProfileManager"%>





<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
	String navigationBasePath = request.getContextPath();
	INetServiceInstanceData netServiceInstanceData = (INetServiceInstanceData)request.getAttribute("netServiceInstanceData");

	//String importServiceDetails            = navigationBasePath+"/importNetServiceDetail.do?netserviceid="+netServiceInstanceData.getNetServiceId();
	//String exportServiceDetails            = navigationBasePath+"/exportNetServiceDetail.do?netserviceid="+netServiceInstanceData.getNetServiceId();
	String updateServiceConfiguration      = navigationBasePath+"/listNetServiceConfiguration.do?netserviceid="+netServiceInstanceData.getNetServiceId();
	String viewService					   = navigationBasePath+"/viewNetServiceInstance.do?netserviceid="+netServiceInstanceData.getNetServiceId();
	String viewServer                      = navigationBasePath+"/viewNetServerInstance.do?netserverid="+netServiceInstanceData.getNetServerId();
	String updateServiceBasicDetails       = navigationBasePath+"/updateNetServiceInstanceBasicDetail.do?netserviceid="+netServiceInstanceData.getNetServiceId();
	String viewServiceGraph       = navigationBasePath+"/initViewServiceGraph.do?netserviceid="+netServiceInstanceData.getNetServiceId();
	
%>


			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%"><bean:message
							key="general.action" /></td>
					<td class="subLinksHeader" width="13%"><a
						href="javascript:void(0)"><img
							src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg"
							border="0" name="arrow"></a></td>
				</tr>

				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<% if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.UPDATE_SERVICE_BASIC_DETAIL_ACTION)){ %>
								<tr>
									<td class="subLinks"><a
										href="<%=updateServiceBasicDetails%>"><bean:message
												bundle="servermgrResources"
												key="servermgr.updateservicebasicdetails" /></a></td>
								</tr>
								<%}%>


								<%--- if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.IMPORT_SERVICE_ACTION)){%>
			<tr> 
              <td class="subLinks">
              		<a href="<%=importServiceDetails%>"><bean:message bundle="servermgrResources" key="servermger.view.importservicedetails"/></a>
              </td>
            </tr>
       <%}--%>

								<%-- if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.EXPORT_SERVICE_ACTION)){ %>
			<tr> 
              <td class="subLinks">
              		<a href="<%=exportServiceDetails%>" ><bean:message bundle="servermgrResources" key="servermgr.view.expoertservicedetails"/></a>
              </td>
            </tr>
		<%}--%>

								<% if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.UPDATE_SERVICE_CONFIGURATION_ACTION)){ %>
								<tr>
									<td class="subLinks"><a
										href="<%=updateServiceConfiguration%>"><bean:message
												bundle="servermgrResources"
												key="servermgr.view.updateserviceconfiguration" /></a></td>
								</tr>
								<%}%>


							</table>
						</div>
					</td>
				</tr>



				<tr id=header2>
					<td class="subLinksHeader" width="87%"><bean:message
							key="general.view" /></td>
					<td class="subLinksHeader" width="13%"><a
						href="javascript:void(0)"><img
							src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg"
							border="0" name="arrow"></a></td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">

								<%---- ACL remain --%>
								<tr>
									<td class="subLinks"><a href="<%=viewService %>"><bean:message
												bundle="servermgrResources" key="servermgr.view.service" /></a>
									</td>
								</tr>

								<tr>
									<td class="subLinks"><a href="<%=viewServiceGraph %>"><bean:message
												bundle="servermgrResources"
												key="servermgr.view.servicegraph" /></a></td>
								</tr>
								<% if(ProfileManager.getSubMoudleActionStatus(request,response,ConfigConstant.VIEW_SERVER_ACTION)){ %>
								<tr>
									<td class="subLinks"><a href="<%=viewServer %>"><bean:message
												bundle="servermgrResources" key="servermgr.view.server" /></a></td>
								</tr>
								<%}%>


							</table>

						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
