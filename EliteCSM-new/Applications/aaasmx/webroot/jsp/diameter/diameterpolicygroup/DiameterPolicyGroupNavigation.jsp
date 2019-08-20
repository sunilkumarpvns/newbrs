<%@page import="com.elitecore.elitesm.datamanager.diameter.diameterpolicygroup.data.DiameterPolicyGroup"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
	<%
		DiameterPolicyGroup diameterPolicyGroup = (DiameterPolicyGroup)request.getAttribute("diameterPolicyGroup");
			
		String navigationBasePath = request.getContextPath();	
		
		String updateBasicDetails = navigationBasePath+"/updateDiameterPolicyGroup.do?policyId="+diameterPolicyGroup.getPolicyId();
		String diameterPolicySummary = navigationBasePath+"/viewDiameterPolicyGroup.do?policyId="+diameterPolicyGroup.getPolicyId();
		String viewDiameterPolicyGroupHistory = navigationBasePath+"/viewDiameterPolicyGroupHistory.do?policyId="+diameterPolicyGroup.getPolicyId()+"&auditUid="+diameterPolicyGroup.getAuditUId()+"&name="+diameterPolicyGroup.getPolicyName();
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
										<a href="<%=diameterPolicySummary%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewsummary" />
										</a>
									</td>
								</tr>
								 <tr>
									<td class="subLinks">
										<a href="<%=viewDiameterPolicyGroupHistory%>" class="subLink">
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
