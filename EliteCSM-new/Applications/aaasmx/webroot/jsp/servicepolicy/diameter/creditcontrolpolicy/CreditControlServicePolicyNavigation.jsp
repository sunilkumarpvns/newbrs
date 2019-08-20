<%@page
	import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData"%>

<%@page
	import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData"%><table
	width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
    CreditControlPolicyData ccPolicyInstData = (CreditControlPolicyData) request.getAttribute("ccPolicyData");
	
	String navigationBasePath = request.getContextPath();	
	
	String basicDetails = navigationBasePath+"/initUpdateCcpolicy.do?policyId="+ccPolicyInstData.getPolicyId()+"&pageAction=update";
	
	String viewBasicDetails = navigationBasePath+"/initViewCcpolicy.do?policyId="+ccPolicyInstData.getPolicyId()+"&pageAction=view";
		
	String viewHistory = navigationBasePath+"/viewCcpolicyHistory.do?policyId="+ccPolicyInstData.getPolicyId()+"&auditUid="+ccPolicyInstData.getAuditUId()+"&name="+ccPolicyInstData.getName();
	
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
									<td class="subLinks"><a href="<%=basicDetails%>"
										class="subLink"><bean:message
												bundle="servicePolicyProperties"
												key="servicepolicy.ccpolicy.update" /></a></td>
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
												key="servicepolicy.ccpolicy.view" /></a></td>
								</tr>
								<tr>
									<td class="subLinks"><a
										href="<%=viewHistory%>" class="subLink">
											<bean:message key="view.history" /></a></td>
								</tr>
							</table>
						</div>
					</td>
				</tr>


			</table>
		</td>
	</tr>

</table>
