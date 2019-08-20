<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
	<%  RadServicePolicyData radServicePolicyData =	(RadServicePolicyData) request.getAttribute("radServicePolicyData");
	
		String navigationBasePath = request.getContextPath();	
		
		String updateBasicDetails = navigationBasePath+"/updateRadiusServicePolicyBasicDetail.do?radiusPolicyId="+radServicePolicyData.getRadiusPolicyId();
		String updateAuthServiceFlow = navigationBasePath+"/updateRadiusServicePolicyAuthServiceFlow.do?radiusPolicyId="+radServicePolicyData.getRadiusPolicyId();
		String updateAcctServiceFlow = navigationBasePath+"/updateRadiusServicePolicyAcctServiceFlow.do?radiusPolicyId="+radServicePolicyData.getRadiusPolicyId();
		
		String authPolicySummary = navigationBasePath+"/viewRadiusServicePolicy.do?radiusPolicyId="+radServicePolicyData.getRadiusPolicyId();
		String viewRadiusServicePolicyAuthFlow = navigationBasePath+"/viewRadiusServicePolicyAuthFlow.do?radiusPolicyId="+radServicePolicyData.getRadiusPolicyId();
		String viewRadiusServicePolicyAcctFlow = navigationBasePath+"/viewRadiusServicePolicyAcctFlow.do?radiusPolicyId="+radServicePolicyData.getRadiusPolicyId();
		String viewHistory = navigationBasePath+"/viewRadiusServicePolicy.do?radiusPolicyId="+radServicePolicyData.getRadiusPolicyId()+"&auditId="+radServicePolicyData.getAuditUid();
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
								<tr>
									<td class="subLinks">
										<a href="<%=updateAuthServiceFlow%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.update.authserviceflow" />
										</a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=updateAcctServiceFlow%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.update.acctserviceflow" />
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
										<a href="<%=authPolicySummary%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewsummary" />
										</a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewRadiusServicePolicyAuthFlow%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewradiusservicepolicyauthflow" />
										</a>
									</td>
								</tr> 
								<tr>
									<td class="subLinks">
										<a href="<%=viewRadiusServicePolicyAcctFlow%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewradiusservicepolicyacctflow" />
										</a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewHistory%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.viewHistory" />
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
