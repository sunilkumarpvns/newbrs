<%@page
	import="com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyData"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
    CGPolicyData cgPolicyInstData = (CGPolicyData) request.getAttribute("cgPolicyData");
	
	String navigationBasePath = request.getContextPath();	
	
	String basicDetails = navigationBasePath+"/initUpdateCGPolicy.do?policyId="+cgPolicyInstData.getPolicyId();
	
	String viewBasicDetails = navigationBasePath+"/initViewCGPolicy.do?policyId="+cgPolicyInstData.getPolicyId();
	
	String viewDetails = navigationBasePath+"/viewCGDetails.do?policyId="+cgPolicyInstData.getPolicyId();
		
	String viewHistory = navigationBasePath+"/viewCGDetailsHistory.do?policyId="+cgPolicyInstData.getPolicyId()+"&auditUid="+cgPolicyInstData.getAuditUId()+"&name="+cgPolicyInstData.getName();

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
										href="<%=basicDetails%>"><bean:message
												bundle="servicePolicyProperties"
												key="servicepolicy.cgpolicy.update.basic.details" /></a></td>
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
												key="servicepolicy.cgpolicy.viewbasic" /></a></td>
								</tr>
							</table>
						</div>
					</td>
				</tr>

				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks"><a href="<%=viewDetails%>"
										class="subLink"><bean:message
												bundle="servicePolicyProperties"
												key="servicepolicy.cgpolicy.details" /></a></td>
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
