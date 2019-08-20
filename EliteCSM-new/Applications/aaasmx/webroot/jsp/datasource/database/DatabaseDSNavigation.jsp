<%@page import="com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData"%>
<%@page import="com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">

	<tr>
		<td colspan="2" valign="top">
			<%
	String navigationBasePath = request.getContextPath();
	DatabaseDSData databaseDSData = (DatabaseDSData)request.getAttribute("databaseDSData");

	String updateDatabaseDS              = navigationBasePath+"/updateDatabaseDS.do?databaseId="+databaseDSData.getDatabaseId();	
	String viewDatabaseDS                 = navigationBasePath+"/viewDatabaseDS.do?databaseId="+databaseDSData.getDatabaseId();	
	String viewDatabaseDSHistory          = navigationBasePath+"/viewDatabaseDSHistory.do?auditUid="+databaseDSData.getAuditUId()+"&name="+databaseDSData.getName()+"&databaseId="+databaseDSData.getDatabaseId();	

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
									<td class="subLinks"><a href="<%=updateDatabaseDS%>"><bean:message
												bundle="datasourceResources"
												key="database.datasource.updateDatabaseDatasource" /></a></td>
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
									<td class="subLinks"><a href="<%=viewDatabaseDS%>"><bean:message
												bundle="datasourceResources"
												key="database.datasource.viewDatabaseDatasource" /></a></td>
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
									<td class="subLinks">
										<a href="<%=viewDatabaseDSHistory%>">
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

