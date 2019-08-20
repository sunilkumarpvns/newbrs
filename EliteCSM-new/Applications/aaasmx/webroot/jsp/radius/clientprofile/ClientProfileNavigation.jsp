

<%@page
	import="com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
	String navigationBasePath = request.getContextPath();
    RadiusClientProfileData clientProfileData=(RadiusClientProfileData)request.getAttribute("radiusClientProfileData");
	// view information url
	String viewBasicClientProfileDetails=navigationBasePath+"/viewClientProfile.do?profileId="+clientProfileData.getProfileId()+"&viewType=basic";
	String viewOtherDetails=navigationBasePath+"/viewClientProfile.do?profileId="+clientProfileData.getProfileId()+"&viewType=other";
	// update inforamation details
	String updateBasicDetails=navigationBasePath+"/updateClientProfile.do?profileId="+clientProfileData.getProfileId()+"&viewType=basic";
	String updateOtherDetails=navigationBasePath+"/updateClientProfile.do?profileId="+clientProfileData.getProfileId()+"&viewType=other";
	String viewClientHistory = navigationBasePath+"/viewClientProfileHistory.do?profileId="+clientProfileData.getProfileId()+"&auditUid="+clientProfileData.getAuditUId()+"&name="+clientProfileData.getProfileName()+"&viewType=other";	

%>

			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%"><bean:message
							key="general.action" /></td>
					<td class="subLinksHeader" width="13%"><a
						href="javascript:void(0)"
						onClick="STB('UpdateRadiusPolicy');swapImages()"><img
							src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg"
							border="0" name="arrow"></a></td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks"><a href="<%=updateBasicDetails%>"><bean:message
												bundle="radiusResources"
												key="radius.clientprofile.updatebasicdetails" /></a></td>
								</tr>

								<tr>
									<td class="subLinks"><a href="<%=updateOtherDetails%>"><bean:message
												bundle="radiusResources"
												key="radius.clientprofile.updateadvancedetails" /></a></td>
								</tr>

							</table>
						</div>
					</td>
				</tr>
				<tr id=header1>
					<td class="subLinksHeader" width="87%"><bean:message
							key="general.view" /></td>
					<td class="subLinksHeader" width="13%"><a
						href="javascript:void(0)"
						onClick="STB('ViewRadiusPolicy');swapImages()"><img
							src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg"
							border="0" name="arrow"></a></td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks"><a
										href="<%=viewBasicClientProfileDetails%>"><bean:message
												bundle="radiusResources"
												key="radius.clientprofile.viewbasicdetails" /></a></td>
								</tr>
								<tr>
									<td class="subLinks"><a href="<%=viewOtherDetails%>"><bean:message
												bundle="radiusResources"
												key="radius.clientprofile.viewotherdetails" /></a></td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewClientHistory%>">
											<bean:message bundle="datasourceResources" key="database.datasource.viewDatabaseDatasourceHistory" />
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

