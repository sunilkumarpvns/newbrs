<%@ page import="com.elitecore.netvertexsm.web.RoutingTable.network.form.NetworkManagementForm"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td colspan="2" valign="top"> 

<%
 	String navigationBasePath = request.getContextPath();
  	NetworkManagementForm mccMNCManagmentForm = (NetworkManagementForm) request.getAttribute("networkManagementForm");
  	// view information url
  	String viewNetwork = navigationBasePath+"/networkManagement.do?method=view&networkID="+mccMNCManagmentForm.getNetworkID();
  	String editNetwok = navigationBasePath+"/networkManagement.do?method=initUpdate&networkID="+mccMNCManagmentForm.getNetworkID();
 %>                                                    

	<table border="0" width="100%" cellspacing="0" cellpadding="0">
		<tr id=header1>
			<td class="subLinksHeader" width="87%">
				<bean:message key="general.action" />
			</td>
			<td class="subLinksHeader" width="13%">
				<a href="javascript:void(0)" onClick="swapImages()"><img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a>
			</td>
		</tr>
		<tr valign="top">
			<td colspan="2" id="backgr1">
				<div>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="subLinks">
								<a href="<%=editNetwok%>"><bean:message bundle="routingMgmtResources" key="network.update.link"/></a>
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
				<a href="javascript:void(0)" onClick="swapImages()"><img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a>
			</td>
		</tr>
		<tr valign="top">
			<td colspan="2" id="backgr1">
				<div>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="subLinks">
								<a href="<%=viewNetwork%>"><bean:message bundle="routingMgmtResources" key="network.view.link"/></a>
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