<%@page
	import="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
				    DiameterRoutingConfData diameterRoutingConfigData = (DiameterRoutingConfData) request.getAttribute("diameterRoutingConfData");
					
					String navigationBasePath = request.getContextPath();	
					
					String basicRoutingDetails = navigationBasePath+"/initUpdateDiameterRoutingConfig.do?routingConfigId="+diameterRoutingConfigData.getRoutingConfigId();
					
					String peerDetails = navigationBasePath+"/initUpdateDiameterRoutingConfPeer.do?routingConfigId="+diameterRoutingConfigData.getRoutingConfigId();
					
					String viewRoutingTable = navigationBasePath+"/viewDiameterRoutingTable.do?routingConfigId="+diameterRoutingConfigData.getRoutingConfigId();										
					
					String viewRoutingConfHistory = navigationBasePath+"/viewDiameterRoutingConfBasicDetailHistory.do?routingConfigId="+diameterRoutingConfigData.getRoutingConfigId()+"&auditUid="+diameterRoutingConfigData.getAuditUId()+"&name="+diameterRoutingConfigData.getName();	

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
									<td class="subLinks"><a class="subLink"
										href="<%=basicRoutingDetails%>"><bean:message
												bundle="diameterResources"
												key="routingconf.update.basic.details" /></a></td>
								</tr>
								<tr>
									<td class="subLinks"><a class="subLink"
										href="<%=peerDetails%>"><bean:message
												bundle="diameterResources" key="routingconf.update.peer" /></a></td>
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
									<td class="subLinks"><a href="<%=viewRoutingTable%>"
										class="subLink"><bean:message bundle="diameterResources"
												key="routingconf.view.viewbasic" /></a></td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewRoutingConfHistory%>">
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