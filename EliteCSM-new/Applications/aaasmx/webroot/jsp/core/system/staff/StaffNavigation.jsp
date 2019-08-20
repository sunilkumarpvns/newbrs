<%@ page
	import="com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
	String navigationBasePath = request.getContextPath();
	IStaffData staffData =(IStaffData) request.getAttribute("staffData");
	
	String updateStaff              = navigationBasePath+"/updateStaff.do?staffid="+staffData.getStaffId();
	String updateAccessGroup	    = navigationBasePath+"/updateAccessGroup.do?staffid="+staffData.getStaffId();
	String changeUserName      		= navigationBasePath+"/changeUserName.do?staffid="+staffData.getStaffId();
	String changeStaffPassword      = navigationBasePath+"/changeStaffPassword.do?staffid="+staffData.getStaffId();
	String viewStaff                = navigationBasePath+"/viewStaff.do?staffid="+staffData.getStaffId();
	String viewStaffAdvanceDetail   = navigationBasePath+"/viewStaffAdvanceDetail.do?staffid="+staffData.getStaffId();
	String viewStaffHistory  		= navigationBasePath+"/viewStaffHistory.do?staffid="+staffData.getStaffId()+"&auditUid="+staffData.getAuditUId()+"&name="+staffData.getName();

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
									<td class="subLinks"><a href="<%=updateStaff%>"><bean:message
											bundle="StaffResources"	key="staff.updatestaff" /></a></td>
								</tr>

								<tr>
									<td class="subLinks"><a href="<%=updateAccessGroup%>"><bean:message
											bundle="StaffResources"	key="staff.updateaccessgroup" /></a></td>
								</tr>

								<tr>
									<td class="subLinks"><a href="<%=changeUserName%>"><bean:message
											bundle="StaffResources"	key="staff.changeusername" /></a></td>
								</tr>

								<tr>
									<td class="subLinks"><a href="<%=changeStaffPassword%>"><bean:message
											bundle="StaffResources"	key="staff.changepassword" /></a></td>
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
									<td class="subLinks"><a href="<%=viewStaff%>"><bean:message
												bundle="StaffResources" key="staff.viewstaff" /></a></td>
								</tr>
								<tr>
									<td class="subLinks"><a href="<%=viewStaffAdvanceDetail%>"><bean:message
												bundle="StaffResources" key="staff.viewstaffadvancedetail" /></a></td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewStaffHistory%>">
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

