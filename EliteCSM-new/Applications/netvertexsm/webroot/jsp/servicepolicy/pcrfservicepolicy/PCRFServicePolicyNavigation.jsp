
<%@page import="com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicyData"%>

<%
	String navigationBasePath = request.getContextPath();
    PCRFServicePolicyData navigationPolicyData = (PCRFServicePolicyData)request.getAttribute("pcrfPolicyData");
	// view information url
	String viewPCRFPolicy = navigationBasePath+"/viewPCRFPolicy.do?pcrfPolicyId="+navigationPolicyData.getPcrfPolicyId();
	String viewAdvanceDetail = navigationBasePath+"/viewPCRFPolicy.do?pcrfPolicyId="+navigationPolicyData.getPcrfPolicyId();
	
	// update inforamation details
	String updateDatabaseDS= navigationBasePath+"/initEditPCRFServicePolicy.do?pcrfPolicyId="+navigationPolicyData.getPcrfPolicyId();
		
%> 

<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%"><bean:message key="general.action" /></td>
					<td class="subLinksHeader" width="13%">
						<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"/>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=updateDatabaseDS%>" tabindex="23" ><bean:message bundle="servicePolicyProperties" key="servicepolicy.update" /></a>
									</td>
								</tr>

								
							</table>
						</div>
					</td>
				</tr>
				<tr id=header1>
					<td class="subLinksHeader" width="87%"><bean:message key="general.view" /></td>
					<td class="subLinksHeader" width="13%">
						<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"/>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=viewPCRFPolicy%>" tabindex="24"><bean:message bundle="servicePolicyProperties" key="servicepolicy.view" /></a>
									</td>
								</tr>
								
							</table>
						</div>
					</td>
				</tr>
</table>
		
