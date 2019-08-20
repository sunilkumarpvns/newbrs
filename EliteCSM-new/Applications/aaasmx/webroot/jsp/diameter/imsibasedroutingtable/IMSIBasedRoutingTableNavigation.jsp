<%@page import="com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
			IMSIBasedRoutingTableData imsiBasedRoutingTableDataObj = (IMSIBasedRoutingTableData) request.getAttribute("imsiBasedRoutingTableData");
			String navigationBasePath = request.getContextPath();	
	
			String basicDetails=navigationBasePath+"/initUpdateIMSIBasedRouting.do?routingTableId="+imsiBasedRoutingTableDataObj.getRoutingTableId();
			String viewBasicDetails=navigationBasePath+"/initViewIMSIBasedRouting.do?routingTableId="+imsiBasedRoutingTableDataObj.getRoutingTableId();
			String viewHistory = navigationBasePath+"/viewIMSIBasedRoutingHistory.do?routingTableId="+imsiBasedRoutingTableDataObj.getRoutingTableId()+"&auditUid="+imsiBasedRoutingTableDataObj.getAuditUId()+"&name="+imsiBasedRoutingTableDataObj.getRoutingTableName();	

		%>
			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.action" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" onClick="STB('UpdateRadiusPolicy');swapImages()">
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
										<a href="<%=basicDetails%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.update.basic.details" />
										</a>
									</td>
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
									<td class="subLinks"><a href="<%=viewBasicDetails%>"
										class="subLink"><bean:message
												bundle="servicePolicyProperties"
												key="servicepolicy.eappolicy.viewbasic" /></a></td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewHistory%>" class="subLink">
											<bean:message bundle="diameterResources" key="imsibasedroutingtable.viewhistory" />
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
