<%@page
	import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData"%>
<%@page
	import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData"%>
<%@page
	import="com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
    DiameterPolicyData diameterPolicyInstData = (DiameterPolicyData) request.getAttribute("diameterPolicyData");
	
	String navigationBasePath = request.getContextPath();	
	
	String basicDetails = navigationBasePath+"/initUpdateDiameterPolicy.do?diameterPolicyId="+diameterPolicyInstData.getDiameterPolicyId();
	
	String viewBasicDetails = navigationBasePath+"/initViewDiameterPolicy.do?diameterPolicyId="+diameterPolicyInstData.getDiameterPolicyId();
	
	String viewDetails = navigationBasePath+"/viewDiameterDetails.do?diameterPolicyId="+diameterPolicyInstData.getDiameterPolicyId();
	
	String viewClientHistory = navigationBasePath+"/viewDiameterPolicyHistory.do?diameterPolicyId="+diameterPolicyInstData.getDiameterPolicyId()+"&auditUid="+diameterPolicyInstData.getAuditUId()+"&name="+diameterPolicyInstData.getName();	
 
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
												key="servicepolicy.eappolicy.update.basic.details" /></a></td>
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
												key="servicepolicy.eappolicy.details" /></a></td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewClientHistory%>">
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
