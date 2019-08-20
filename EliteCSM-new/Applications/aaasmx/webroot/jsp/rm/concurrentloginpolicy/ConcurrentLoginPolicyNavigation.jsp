<%@ page
	import="com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyData"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
	String navigationBasePath = request.getContextPath();
	IConcurrentLoginPolicyData concurrentLoginPolicyData =(IConcurrentLoginPolicyData) request.getAttribute("concurrentLoginPolicyData");
	
	String updateBasicDetails          = navigationBasePath+"/updateConcurrentLoginPolicyBasicDetail.do?concurrentLoginPolicyId="+concurrentLoginPolicyData.getConcurrentLoginId();
	String updateActivityStatus        = navigationBasePath+"/updateConcurrentLoginPolicyStatus.do?concurrentLoginPolicyId="+concurrentLoginPolicyData.getConcurrentLoginId();
	String updateAttributeDetail     = navigationBasePath+"/updateConcLoginPolicyAttributeDetail.do?concurrentLoginPolicyId="+concurrentLoginPolicyData.getConcurrentLoginId();
	String viewConcLoginPolicySummary  = navigationBasePath+"/viewConcurrentLoginPolicy.do?concurrentLoginId="+concurrentLoginPolicyData.getConcurrentLoginId();
	String viewAttributeDetail       = navigationBasePath+"/viewConcLoginPolicyAttributeDetail.do?concurrentLoginPolicyId="+concurrentLoginPolicyData.getConcurrentLoginId();
	String viewConcurrentPolicyHistory   = navigationBasePath+"/viewConcurrentLoginPolicyHistory.do?concurrentLoginPolicyId="+concurrentLoginPolicyData.getConcurrentLoginId()+"&auditUid="+concurrentLoginPolicyData.getAuditUId()+"&name="+concurrentLoginPolicyData.getName();

%>

			<table border="0" width="100%" cellspacing="0" cellpadding="0">

				<tr id=header1>
					<td class="subLinksHeader" width="87%"><bean:message
							key="general.update" /></td>
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
									<td class="subLinks"><a href="<%=updateBasicDetails%>"><bean:message
												bundle="radiusResources"
												key="concurrentloginpolicy.update.basicdetails" /></a></td>
								</tr>

								<tr>
									<td class="subLinks"><a href="<%=updateActivityStatus%>"><bean:message
												bundle="radiusResources"
												key="concurrentloginpolicy.update.activitystatus" /></a></td>
								</tr>

								<logic:equal name="concurrentLoginPolicyData"
									property="concurrentLoginPolicyModeId" value="SMS0150">
									<tr>
										<td class="subLinks"><a href="<%=updateAttributeDetail%>"><bean:message
													bundle="radiusResources"
													key="concurrentloginpolicy.update.attributedetail" /></a></td>
									</tr>
								</logic:equal>
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
										href="<%=viewConcLoginPolicySummary%>"><bean:message
												bundle="radiusResources"
												key="concurrentloginpolicy.view.summary" /></a></td>
								</tr>
								<logic:equal name="concurrentLoginPolicyData"
									property="concurrentLoginPolicyModeId" value="SMS0150">
									<tr>
										<td class="subLinks"><a href="<%=viewAttributeDetail%>"><bean:message
													bundle="radiusResources"
													key="concurrentloginpolicy.view.detail.attribute" /></a></td>
									</tr>
								</logic:equal>
								<tr>
									<td class="subLinks">
										<a href="<%=viewConcurrentPolicyHistory%>">
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
