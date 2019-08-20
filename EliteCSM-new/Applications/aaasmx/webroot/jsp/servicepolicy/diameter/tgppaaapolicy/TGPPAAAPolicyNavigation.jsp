<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.form.TGPPAAAPolicyForm"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
	<%
	 	
	 	TGPPAAAPolicyData tgppAAAPolicyData = (TGPPAAAPolicyData)request.getAttribute("tgppAAAPolicyData");
		String navigationBasePath = request.getContextPath();	
		
		String updateBasicDetails = navigationBasePath+"/updateTGPPAAAServicePolicyBasicDetail.do?tgppAAAPolicyId="+tgppAAAPolicyData.getTgppAAAPolicyId();
		String updateCommandCodeFlow = navigationBasePath+"/updateTGPPAAAServicePolicyCommandCodeFlow.do?tgppAAAPolicyId="+tgppAAAPolicyData.getTgppAAAPolicyId();
		
		String viewBasicSummary = navigationBasePath+"/viewTGPPAAAPolicy.do?tgppAAAPolicyId="+tgppAAAPolicyData.getTgppAAAPolicyId()+"&viewAdvancedDetails=true";
		String viewCommandCodeFlow = navigationBasePath+"/updateTGPPAAAServicePolicyCommandCodeFlow.do?tgppAAAPolicyId="+tgppAAAPolicyData.getTgppAAAPolicyId()+"&isView=true";
		String viewHistory = navigationBasePath+"/viewTGPPAAAPolicy.do?tgppAAAPolicyId="+tgppAAAPolicyData.getTgppAAAPolicyId()+"&viewAdvancedDetails=true&auditId="+tgppAAAPolicyData.getAuditUid()+"&name="+tgppAAAPolicyData.getName();
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
											<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.updatebasicdetail" />
									 	</a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=updateCommandCodeFlow%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.updatecommandcodeflow" />
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
										<a href="<%=viewBasicSummary%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.viewdetails" />
										</a>
									</td>
								</tr>
								 <tr>
									<td class="subLinks">
										<a href="<%=viewCommandCodeFlow%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.viewcommandcodeflow" />
										</a>
									</td>
								</tr> 
								<tr>
									<td class="subLinks">
										<a href="<%=viewHistory%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.viewhistory" />
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
