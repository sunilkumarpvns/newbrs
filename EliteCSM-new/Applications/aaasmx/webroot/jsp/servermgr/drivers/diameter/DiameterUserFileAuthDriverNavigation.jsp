
<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.drivers.userfiledriver.data.UserFileAuthDriverData"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<%@page
		import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.*"%>

	<tr>
		<td colspan="2" valign="top">
			<%
	String navigationBasePath = request.getContextPath();
	UserFileAuthDriverData driverInstance = (UserFileAuthDriverData)session.getAttribute("userfiledata");
	DriverInstanceData driverInstData = (DriverInstanceData)session.getAttribute("driverInstance");
	
	String updateDriverInstance  = navigationBasePath+"/initupdateDriverInstance.do?driverInstanceId="+driverInstance.getDriverInstanceId();	
	String viewDriverInstance = navigationBasePath+"/viewDriverInstance.do?action=view&driverInstanceId="+driverInstance.getDriverInstanceId();	
	String viewDiameterUserFileDriverHistory = navigationBasePath+"/viewDiameterUserFileAuthDriverHistory.do?driverInstanceId="+driverInstData.getDriverInstanceId()+"&auditUid="+driverInstData.getAuditUId()+"&name="+driverInstData.getName();;	

%>

			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%"><bean:message
							key="general.action" /></td>
					<td class="subLinksHeader" width="13%"><a
						href="javascript:void(0)"
						onClick="STB('UpdateDiameterPolicy');swapImages()"><img
							src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg"
							border="0" name="arrow"></a></td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks"><a href="<%=updateDriverInstance%>">
											<bean:message bundle="driverResources"
												key="driver.updatedriver" />
									</a></td>
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
						onClick="STB('ViewDiameterPolicy');swapImages()"><img
							src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg"
							border="0" name="arrow"></a></td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks"><a href="<%=viewDriverInstance%>">
											<bean:message bundle="driverResources"
												key="driver.viewdriver" />
									</a></td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewDiameterUserFileDriverHistory%>">
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

