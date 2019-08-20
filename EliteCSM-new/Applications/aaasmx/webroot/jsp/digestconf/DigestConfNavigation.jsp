

<%@page
	import="com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData"%>

<%@page
	import="com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData"%><table
	width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
	String navigationBasePath = request.getContextPath();
    DigestConfigInstanceData digestConfigInstanceData=(DigestConfigInstanceData)request.getAttribute("digestConfData");
	// view information url
	String viewDigestConfig=navigationBasePath+"/viewDigestConf.do?digestConfId="+digestConfigInstanceData.getDigestConfId();
	
	// update inforamation details
	String updateDigestConf=navigationBasePath+"/updateDigestConf.do?digestConfId="+digestConfigInstanceData.getDigestConfId();
	String viewDriverHistory = navigationBasePath+"/viewDigestConfHistory.do?digestConfId="+digestConfigInstanceData.getDigestConfId()+"&auditUid="+digestConfigInstanceData.getAuditUId()+"&name="+digestConfigInstanceData.getName();;	

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
									<td class="subLinks"><a href="<%=updateDigestConf%>"><bean:message
												bundle="digestconfResources" key="digestconf.update" /></a></td>
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
									<td class="subLinks"><a href="<%=viewDigestConfig%>"><bean:message
												bundle="digestconfResources" key="digestconf.view" /></a></td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewDriverHistory%>">
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

