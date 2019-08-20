<%@page
	import="com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
    DynAuthPolicyInstData dynAuthPolicyInstData =(DynAuthPolicyInstData) request.getAttribute("dynAuthPolicyInstData");
	
	String navigationBasePath = request.getContextPath();	
	String dynAuthPolicySummary = navigationBasePath+"/viewDynAuthServicePolicy.do?dynauthpolicyid="+dynAuthPolicyInstData.getDynAuthPolicyId();
	String dynAuthPolicyAdvanceDetails = navigationBasePath+"/viewDynAuthServicePolicyAdvanceDetails.do?dynauthpolicyid="+dynAuthPolicyInstData.getDynAuthPolicyId();
	String updateBasicDetails = navigationBasePath+"/updateDynAuthServicePolicyBasicDetail.do?dynauthpolicyid="+dynAuthPolicyInstData.getDynAuthPolicyId();
	String viewDynaAuthPolicyHistory = navigationBasePath+"/viewDynAuthServicePolicyHistory.do?dynauthpolicyid="+dynAuthPolicyInstData.getDynAuthPolicyId()+"&auditUid="+dynAuthPolicyInstData.getAuditUId()+"&name="+dynAuthPolicyInstData.getName();
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
									<td class="subLinks"><a href="<%=updateBasicDetails%>"
										class="subLink"><bean:message
												bundle="servicePolicyProperties"
												key="servicepolicy.dynauthpolicy.update.basicdetail" /></a></td>
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
									<td class="subLinks"><a href="<%=dynAuthPolicySummary%>"
										class="subLink"><bean:message
												bundle="servicePolicyProperties"
												key="servicepolicy.dynauthpolicy.viewsummary" /></a></td>
								</tr>
								<tr>
									<td class="subLinks"><a
										href="<%=dynAuthPolicyAdvanceDetails%>" class="subLink"><bean:message
												bundle="servicePolicyProperties"
												key="servicepolicy.dynauthpolicy.viewadvancedetails" /></a></td>
								</tr>
								<tr>
									<td class="subLinks"><a
										href="<%=viewDynaAuthPolicyHistory%>" class="subLink">
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
