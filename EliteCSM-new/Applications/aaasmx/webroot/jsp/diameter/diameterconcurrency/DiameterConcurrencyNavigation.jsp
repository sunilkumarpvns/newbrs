<%@page import="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.diameterpolicygroup.data.DiameterPolicyGroup"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
	<%
		DiameterConcurrencyData diameterConcurrencyData = (DiameterConcurrencyData)request.getAttribute("diameterConcurrencyData");
			
		String navigationBasePath = request.getContextPath();	
		
		String updateBasicDetails = navigationBasePath+"/updateDiameterConcurrency.do?diaConConfigId="+diameterConcurrencyData.getDiaConConfigId();
		String diameterConcurrencySummary = navigationBasePath+"/viewDiameterConcurrency.do?diaConConfigId="+diameterConcurrencyData.getDiaConConfigId();
		String ViewDiameterConcurrencyHistory = navigationBasePath+"/viewDiameterConcurrencyHistory.do?diaConConfigId="+diameterConcurrencyData.getDiaConConfigId()+"&auditUid="+diameterConcurrencyData.getAuditUId()+"&name="+diameterConcurrencyData.getName();
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
										<a href="<%=updateBasicDetails%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.update.basicdetail" />
										</a>
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
						<a href="javascript:void(0)" onClick="STB('ViewRadiusPolicy');swapImages()">
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
										<a href="<%=diameterConcurrencySummary%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewsummary" />
										</a>
									</td>
								</tr>
								 <tr>
									<td class="subLinks">
										<a href="<%=ViewDiameterConcurrencyHistory%>" class="subLink">
											<bean:message key="view.history" />
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
