<%@page import="com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
	<%
		RadiusPolicyGroup radiusPolicyGroup =	(RadiusPolicyGroup) request.getAttribute("radiusPolicyGroup");
			
		String navigationBasePath = request.getContextPath();	
		
		String updateBasicDetails = navigationBasePath+"/updateRadiusPolicyGroup.do?policyId="+radiusPolicyGroup.getPolicyId();
		String radiusPolicySummary = navigationBasePath+"/viewRadiusPolicyGroup.do?policyId="+radiusPolicyGroup.getPolicyId();
			 	String viewRadiusPolicyGroupHistory = navigationBasePath+"/viewRadiusPolicyGroupHistory.do?policyId="+radiusPolicyGroup.getPolicyId()+"&auditUid="+radiusPolicyGroup.getAuditUId()+"&name="+radiusPolicyGroup.getPolicyName();
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
										<a href="<%=radiusPolicySummary%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewsummary" />
										</a>
									</td>
								</tr>
								 <tr>
									<td class="subLinks">
										<a href="<%=viewRadiusPolicyGroupHistory%>" class="subLink">
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
