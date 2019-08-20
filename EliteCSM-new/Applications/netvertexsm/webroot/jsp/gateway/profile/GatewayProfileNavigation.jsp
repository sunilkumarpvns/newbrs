
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
	String updateRadiusProfile = navigationBasePath+"/editRadiusGatewayProfile.do?profileId="+gatewayProfile.getProfileId();
	String managePacketMap = navigationBasePath+"/manageMappingOrder.do?profileId="+gatewayProfile.getProfileId();
	String updateprofilefieldmap = navigationBasePath+"/editDiameterGatewayProfile.do?profileId="+gatewayProfile.getProfileId()+"&profileName="+profileFieldMap;
	String viewAssociations = navigationBasePath+"/viewGatewayProfile.do?viewAssociations=true&profileId="+gatewayProfile.getProfileId();
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
										<a href="<%=updateprofile%>"><bean:message bundle="gatewayResources" key="gateway.profile.update" /></a>
									</td>
								</tr>
								<!-- Update Cisco/Radius/Diameter Page -->
    							<bean:define id="gatewayProfileBean" name="gatewayProfileData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData" />
								<logic:notEmpty name="gatewayProfileBean" property="radiusProfileData">
    								<tr>
										<td class="subLinks">
											<a href="<%=updateRadiusProfile%>"> Update Radius Profile</a>
										</td>
									</tr>
    							</logic:notEmpty>
    							<logic:notEmpty name="gatewayProfileBean" property="diameterProfileData">
    								<tr>
										<td class="subLinks">
											<a href="<%=updateprofilefieldmap%>">Update Diameter Profile</a>
										</td>
									</tr>
    							</logic:notEmpty>
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
										<a href="<%=viewGatewayProfile%>"><bean:message bundle="gatewayResources" key="gateway.profile.view" /></a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewAssociations%>"><bean:message key="general.view.associations" /></a>
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

