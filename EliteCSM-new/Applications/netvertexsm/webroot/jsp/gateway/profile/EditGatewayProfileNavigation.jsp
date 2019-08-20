
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData"%>


<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td colspan="2" valign="top"> 


<%
	String navigationBasePath = request.getContextPath();
    GatewayProfileData gatewayProfile = (GatewayProfileData)request.getAttribute("gatewayProfileData");
    String profileFieldMap = "fieldMap";
	// view information url
	String viewGatewayProfile = navigationBasePath+"/viewGatewayProfile.do?profileId="+gatewayProfile.getProfileId();
	String updateprofile = navigationBasePath+"/initEditGatewayProfile.do?profileId="+gatewayProfile.getProfileId();
	String updateprofilefieldmap = navigationBasePath+"/initEditGatewayProfile.do?profileId="+gatewayProfile.getProfileId()+"&profileName="+profileFieldMap;
%>                                                    

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
										<a href="<%=updateprofile%>" tabindex="20"><bean:message bundle="gatewayResources" key="gateway.profile.update" />
									</td>
								</tr>
								<%--
								<tr>
									<td class="subLinks">
										<a href="<%=updateprofilefieldmap%>"><bean:message bundle="gatewayResources" key="gateway.profile.gatewaymapping" />
									</td>
								</tr>
								--%>
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
										<a href="<%=viewGatewayProfile%>" tabindex="21"><bean:message bundle="gatewayResources" key="gateway.profile.view" /></a>
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

