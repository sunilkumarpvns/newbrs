<%@page
	import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
    NASPolicyInstData nasPolicyInstData = (NASPolicyInstData) request.getAttribute("nasPolicyInstData");
	
	String navigationBasePath = request.getContextPath();	
	
	String basicDetails = navigationBasePath+"/updateNASServicePolicyBasicDetail.do?nasPolicyId="+nasPolicyInstData.getNasPolicyId();
	
	String authParamDetails = navigationBasePath+"/updateNASServicePolicyAuthenticationParamDetail.do?nasPolicyId="+nasPolicyInstData.getNasPolicyId();
	
	String authorizeParamDetails = navigationBasePath+"/updateNASServicePolicyAuthorizationParamDetail.do?nasPolicyId="+nasPolicyInstData.getNasPolicyId();
	
	String rfc4372CUIParamDetails = navigationBasePath+"/updateNASServicePolicyRFC4372CUIParamDetail.do?nasPolicyId="+nasPolicyInstData.getNasPolicyId();
	
	String acctParamDetails = navigationBasePath+"/updateNASServicePolicyAccountingParams.do?nasPolicyId="+nasPolicyInstData.getNasPolicyId();
	
	String responseAttributeDetails = navigationBasePath+"/updateResponseAttributeDetails.do?nasPolicyId="+nasPolicyInstData.getNasPolicyId();
	
	String viewNASServicePolicyDetail = navigationBasePath+"/viewNASServicePolicyDetail.do?nasPolicyId="+nasPolicyInstData.getNasPolicyId();	

	String viewHistory = navigationBasePath+"/viewNASServicePolicyHistory.do?nasPolicyId="+nasPolicyInstData.getNasPolicyId()+"&auditUid="+nasPolicyInstData.getAuditUId()+"&name="+nasPolicyInstData.getName();	

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
												key="servicepolicy.naspolicy.update.basic.details" /></a></td>
								</tr>

								<tr>
									<td class="subLinks"><a href="<%=authParamDetails%>"
										class="subLink"><bean:message
												bundle="servicePolicyProperties"
												key="servicepolicy.naspolicy.update.authparam.details" /></a></td>
								</tr>

								<tr>
									<td class="subLinks"><a href="<%=authorizeParamDetails%>"
										class="subLink"><bean:message
												bundle="servicePolicyProperties"
												key="servicepolicy.naspolicy.update.authorizeparam.details" /></a></td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=rfc4372CUIParamDetails%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.update.rfc4372cui.details" />
										</a>
									</td>
								</tr>
								

								<tr>
									<td class="subLinks"><a href="<%=acctParamDetails%>"
										class="subLink"><bean:message
												bundle="servicePolicyProperties"
												key="servicepolicy.naspolicy.update.acctparam.details" /></a></td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=responseAttributeDetails%>" class="subLink">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.update.responseattribute" />
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
									<td class="subLinks"><a
										href="<%=viewNASServicePolicyDetail%>" class="subLink"><bean:message
												bundle="servicePolicyProperties"
												key="servicepolicy.naspolicy.view" /></a></td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewHistory%>">
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
