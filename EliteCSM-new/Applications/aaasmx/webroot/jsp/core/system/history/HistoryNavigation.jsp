<%@page import="com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData"%>
<%@page import="com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">

	<tr>
		<td colspan="2" valign="top">
			<%
			String navigationBasePath = request.getContextPath();


	/* String updateDatabaseDS              = navigationBasePath+"/updateDatabaseDS.do?databaseId="+databaseDSData.getDatabaseId();	
	String viewDatabaseDS                 = navigationBasePath+"/viewDatabaseDS.do?databaseId="+databaseDSData.getDatabaseId();	
	String viewDatabaseDSHistory          = navigationBasePath+"/viewDatabaseHistory.do?auditUid="+databaseDSData.getAuditUId()+"&name="+databaseDSData.getName();	
 */
 String exportToPDF="";
 String exportToCSV="";
 String backButton="";
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
									<td class="subLinks">
										<a href="<%=exportToPDF%>">
											<bean:message bundle="datasourceResources" key="database.datasource.exporttopdf" />
										</a>
									</td>
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
										<a href="<%=exportToCSV%>">
											<bean:message bundle="datasourceResources" key="database.datasource.exporttocsv" />
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
									<td class="subLinks" style="cursor: pointer;">
										<a onclick="history.go(-1);">
											<bean:message bundle="datasourceResources" key="database.datasource.backbutton" />
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

