<%@page import="com.elitecore.corenetvertex.constants.CommunicationProtocol"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData"%>
<%
	String navigationBasePath = request.getContextPath();
    GatewayData navigationGatewayData = (GatewayData)request.getAttribute("gatewayData");
	// view information url
	String viewGateway = navigationBasePath+"/viewGateway.do?gatewayId="+navigationGatewayData.getGatewayId()+"&commProtocolId="+navigationGatewayData.getCommProtocol();
	String viewGatewayAssociation = navigationBasePath+"/viewGateway.do?viewAssociations=true&gatewayId="+navigationGatewayData.getGatewayId()+"&commProtocolId="+navigationGatewayData.getCommProtocol();
	
	// update inforamation details
	String updateBasicDetails = navigationBasePath+"/initEditGatewayBasicDetails.do?gatewayId="+navigationGatewayData.getGatewayId()+"&commProtocolId="+navigationGatewayData.getCommProtocol();
	String updateGateway = navigationBasePath+"/initEditGateway.do?gatewayId="+navigationGatewayData.getGatewayId()+"&commProtocolId="+navigationGatewayData.getCommProtocol();	
%> 
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td colspan="2" valign="top"> 

			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.action" />
					</td>
					<td class="subLinksHeader" width="13%">
						<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"/>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=updateBasicDetails%>" tabindex="13"><bean:message key="general.update.basicdetails.link" /></a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=updateGateway%>" tabindex="13"><bean:message bundle="gatewayResources" key="gateway.update" /></a>
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
						<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"/>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=viewGateway%>" tabindex="14"><bean:message bundle="gatewayResources" key="gateway.view" /></a>
									</td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
				<% if(navigationGatewayData.getCommProtocol().equals(CommunicationProtocol.DIAMETER.id)){ %>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=viewGatewayAssociation%>" tabindex="14"><bean:message bundle="gatewayResources" key="gateway.view.association" /></a>
									</td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
				<%} %>
			</table>
		</td>
  </tr>

</table>

