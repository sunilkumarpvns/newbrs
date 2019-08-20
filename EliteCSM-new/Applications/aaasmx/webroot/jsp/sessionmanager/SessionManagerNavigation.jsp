<%@page
	import="com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">

	<tr>
		<td colspan="2" valign="top">
			<%																						
	String navigationBasePath = request.getContextPath();
	ISessionManagerInstanceData sessionManagerInstanceData = (ISessionManagerInstanceData)request.getAttribute("sessionManagerInstanceData");
	//String updateSessionManager = navigationBasePath+"/initUpdateSessionManagerBasicDetail.do?sminstanceid="+sessionManagerInstanceData.getSmInstanceId();
	String searchASM = navigationBasePath+"/initSearchASM.do?sminstanceid="+sessionManagerInstanceData.getSmInstanceId();

	String updateBasicDetails = navigationBasePath+"/updateSessionManagerBasicDetail.do?sminstanceid="+sessionManagerInstanceData.getSmInstanceId(); 
    String updateSessionManagerdetails=navigationBasePath+"/updateSessionManagerDetail.do?sminstanceid="+sessionManagerInstanceData.getSmInstanceId();
    
    String viewSessionManagerSummary = navigationBasePath+"/viewSessionManager.do?sminstanceid="+sessionManagerInstanceData.getSmInstanceId();
    String viewSessionManagerDetail = navigationBasePath+"/viewSessionManagerDetail.do?sminstanceid="+sessionManagerInstanceData.getSmInstanceId();
    String viewSessionManagerHistoryDetail = navigationBasePath+"/viewSessionManagerHistory.do?sminstanceid="+sessionManagerInstanceData.getSmInstanceId()+"&auditUid="+sessionManagerInstanceData.getAuditUId()+"&name="+sessionManagerInstanceData.getName();;	
    
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
									<td class="subLinks"><a href="<%=updateBasicDetails%>"><bean:message
												bundle="sessionmanagerResources"
												key="sessionmanager.updatebasicdetails" /></a></td>
								</tr>

								<tr>
									<td class="subLinks">
										<!--
									    <a href="#"><bean:message bundle="sessionmanagerResources" key="sessionmanager.updatesessionmanagerdetails" /></a>
									    --> <a href="<%=updateSessionManagerdetails%>"><bean:message
												bundle="sessionmanagerResources"
												key="sessionmanager.updatesessionmanagerdetails" /></a>

									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=searchASM%>">
											<bean:message bundle="sessionmanagerResources" key="sessionmanager.searchasm" />
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
										href="<%=viewSessionManagerSummary%>"><bean:message
												bundle="sessionmanagerResources"
												key="sessionmanager.viewsummary" /></a></td>
								</tr>
								<tr>
									<td class="subLinks"><a
										href="<%=viewSessionManagerDetail%>"><bean:message
												bundle="sessionmanagerResources"
												key="sessionmanager.viewsessionmanager" /></a></td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewSessionManagerHistoryDetail%>">
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

