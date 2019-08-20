<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
	com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyData radiusPolicyData =
	(com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyData) request.getAttribute("radiusPolicyData");
	
	String navigationBasePath = request.getContextPath();	
	String viewRadiusPolicy = navigationBasePath+"/viewRadiusPolicy.do?radiusPolicyId="+radiusPolicyData.getRadiusPolicyId();
	String updateRadiusPolicy = navigationBasePath+"/updateRadiusPolicy.do?radiusPolicyId="+radiusPolicyData.getRadiusPolicyId();
	String viewRadiusPolicyHistory = navigationBasePath+"/viewRadiusPolicyHistory.do?radiusPolicyId="+radiusPolicyData.getRadiusPolicyId()+"&auditUid="+radiusPolicyData.getAuditUId()+"&name="+radiusPolicyData.getName();;	

	
	/* String updateActivityStatus = navigationBasePath+"/updateRadiusPolicyStatus.do?radiusPolicyId="+radiusPolicyData.getRadiusPolicyId();
	String updateReplyItems = navigationBasePath+"/updateRadiusPolicyReplyItem.do?radiusPolicyId="+radiusPolicyData.getRadiusPolicyId();
	String updateReplaceItems = navigationBasePath+"/updateRadiusPolicyReplaceItem.do?radiusPolicyId="+radiusPolicyData.getRadiusPolicyId();
	String updateRejectItems = navigationBasePath+"/updateRadiusPolicyRejectItem.do?radiusPolicyId="+radiusPolicyData.getRadiusPolicyId();
	String updateCheckItems = navigationBasePath+"/updateRadiusPolicyCheckItem.do?radiusPolicyId="+radiusPolicyData.getRadiusPolicyId();
	
	String viewReplyItems = navigationBasePath+"/viewRadiusPolicyReplyItem.do?radiusPolicyId="+radiusPolicyData.getRadiusPolicyId();
	String viewReplaceItems = navigationBasePath+"/viewRadiusPolicyReplaceItem.do?radiusPolicyId="+radiusPolicyData.getRadiusPolicyId();
	String viewRejectItems = navigationBasePath+"/viewRadiusPolicyRejectItem.do?radiusPolicyId="+radiusPolicyData.getRadiusPolicyId();
	String viewCheckItems = navigationBasePath+"/viewRadiusPolicyCheckItem.do?radiusPolicyId="+radiusPolicyData.getRadiusPolicyId(); */


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
									<td class="subLinks"><a href="<%=updateRadiusPolicy%>"><bean:message
												bundle="radiusResources" key="radiuspolicy.update" /></a></td>
								</tr>

								<%-- <tr> 
                  <td class="subLinks"><a href="<%=updateActivityStatus%>" ><bean:message bundle="radiusResources" key="radiuspolicy.update.activitystatus" /></a></td>
                </tr>
                <tr> 
                  <td class="subLinks"><a href="<%=updateCheckItems%>" ><bean:message bundle="radiusResources" key="radiuspolicy.update.checkitems" /></a></td>                
                </tr>
				 <tr> 
                  <td class="subLinks"><a href="<%=updateReplyItems%>" ><bean:message bundle="radiusResources" key="radiuspolicy.update.replyitems" /></a></td>                
                 </tr>
			  	<tr> 
                  <td class="subLinks"><a href="<%=updateRejectItems%>" ><bean:message bundle="radiusResources" key="radiuspolicy.update.rejectitems" /></a></td>
                </tr>	 --%>
								<%--                <tr> --%>
								<%--                  <td class="subLinks"><a href="<%=updateReplaceItems%>" ><bean:message bundle="radiusResources" key="radiuspolicy.update.replaceitems" /></a></td>                --%>
								<%--                </tr>--%>
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
									<td class="subLinks"><a href="<%=viewRadiusPolicy%>"><bean:message
												bundle="radiusResources" key="radiuspolicy.view" /></a></td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewRadiusPolicyHistory%>">
											<bean:message bundle="datasourceResources" key="database.datasource.viewDatabaseDatasourceHistory" />
										</a>
									</td>
								</tr>
								<%-- <tr> 
                  <td class="subLinks"><a href="<%=viewCheckItems%>" ><bean:message bundle="radiusResources" key="radiuspolicy.view.checkitems" /></a></td>                
                </tr>
				 <tr> 
                  <td class="subLinks"><a href="<%=viewReplyItems%>" ><bean:message bundle="radiusResources" key="radiuspolicy.view.replyitems" /></a></td>                
                 </tr>
			  	<tr> 
                  <td class="subLinks"><a href="<%=viewRejectItems%>" ><bean:message bundle="radiusResources" key="radiuspolicy.view.rejectitems" /></a></td>
                </tr>	 --%>
								<%--                <tr> --%>
								<%--                  <td class="subLinks"><a href="<%=viewReplaceItems%>" ><bean:message bundle="radiusResources" key="radiuspolicy.view.replaceitems" /></a></td>                --%>
								<%--                </tr>--%>
							</table>
						</div>
					</td>
				</tr>




			</table>
		</td>
	</tr>

</table>
