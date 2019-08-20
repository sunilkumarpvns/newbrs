<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<%@page
		import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.*"%>

	<tr>
		<td colspan="2" valign="top">
			<%
	String navigationBasePath = request.getContextPath();
	DriverInstanceData driverInstance = (DriverInstanceData)session.getAttribute("driverInstance");
	DBAuthDriverData dbAuthDriverData = null;
	if(driverInstance!=null){
		Set dbAuthDriverSet = driverInstance.getDbdetail();
		if(dbAuthDriverSet!=null && !dbAuthDriverSet.isEmpty()){
			Iterator iterator = dbAuthDriverSet.iterator();
			if(iterator.hasNext()){
				dbAuthDriverData = (DBAuthDriverData)iterator.next();
			}
		}
	}
	
	String updateDriverInstance  = navigationBasePath+"/initupdateDriverInstance.do?driverInstanceId="+driverInstance.getDriverInstanceId();	
	String viewDriverInstance = navigationBasePath+"/viewDriverInstance.do?action=view&driverInstanceId="+driverInstance.getDriverInstanceId();	
	String viewDriverHistory = navigationBasePath+"/viewRadiusDBAcctDriverHistory.do?driverInstanceId="+driverInstance.getDriverInstanceId()+"&auditUid="+driverInstance.getAuditUId()+"&name="+driverInstance.getName();;	
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
									<td class="subLinks"><a href="<%=updateDriverInstance%>"><bean:message
												bundle="driverResources" key="driver.updatedriver" /></a></td>
								</tr>
								<%if(dbAuthDriverData!=null){
									
									String updateFieldName =navigationBasePath+"/updateDatabaseSubscriberProfileField.do?driverInstanceId="+driverInstance.getDriverInstanceId()+"&dbAuthId="+dbAuthDriverData.getDbAuthId()+"&checkForOutcome=";
									String addSubscriberProfileData =navigationBasePath+"/addDatabaseSubscriberProfileData.do?driverInstanceId="+driverInstance.getDriverInstanceId()+"&dbAuthId="+dbAuthDriverData.getDbAuthId()+"&firstFieldData=&secondFieldData=";
									String searchSubscriberProfileData =navigationBasePath+"/searchDatabaseSubscriberProfileData.do?driverInstanceId="+driverInstance.getDriverInstanceId()+"&dbAuthId="+dbAuthDriverData.getDbAuthId();
									String updateValuePool = navigationBasePath+"/updateDatabaseSubscriberProfileValuePool.do?driverInstanceId="+driverInstance.getDriverInstanceId()+"&dbAuthId="+dbAuthDriverData.getDbAuthId()+"&checkForOutcome=forValuePool";
									%>
								<tr>
									<td class="subLinks"><bean:message
											bundle="driverResources"
											key="subscriberprofile.database.title" /></td>
								</tr>
								<tr>
									<td class="subLinks">&nbsp;-&nbsp; <a
										href="<%=updateFieldName%>"><bean:message
												bundle="driverResources"
												key="subscriberprofile.database.updatefieldname" /></a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">&nbsp;-&nbsp; <a
										href="<%=addSubscriberProfileData%>"><bean:message
												bundle="driverResources"
												key="subscriberprofile.database.adddata" /></a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">&nbsp;-&nbsp; <a
										href="<%=searchSubscriberProfileData%>"><bean:message
												bundle="driverResources"
												key="subscriberprofile.database.searchdata" /></a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">&nbsp;-&nbsp; <a
										href="<%=updateValuePool%>"><bean:message
												bundle="driverResources"
												key="subscriberprofile.database.updatevaluepool" /></a>
									</td>
								</tr>
								<%}%>
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
									<td class="subLinks">
										<a href="<%=viewDriverInstance%>">
											<bean:message bundle="driverResources" key="driver.viewdriver" />
										</a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewDriverHistory%>">
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

