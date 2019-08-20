
<%@ page import = "com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData" %>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr> 
<td colspan="2" valign="top"> 
 
<%
	String navigationBasePath = request.getContextPath();
	IStaffData staffData =(IStaffData) request.getAttribute("staffData");
	IStaffData loggedInUserData =(IStaffData) request.getAttribute("loggedInUserData");
	
	String updateStaff              = navigationBasePath+"/updateStaff.do?staffid="+staffData.getStaffId();
	String updateAccessGroup	    = navigationBasePath+"/updateAccessGroup.do?staffid="+staffData.getStaffId();
	String resetStaffPassword	    = navigationBasePath+"/changeStaffPassword.do?staffid="+staffData.getStaffId();
	String viewStaff                = navigationBasePath+"/viewStaff.do?staffid="+staffData.getStaffId();
%>

			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id="header1">
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.action" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" onClick="STB('UpdateRadiusPolicy');swapImages()"><img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=updateStaff%>"><bean:message key="staff.updatestaff" /></a>
									</td>
								</tr>

								<tr>
									<td class="subLinks">
										<a href="<%=updateAccessGroup%>"><bean:message key="staff.updateaccessgroup"/></a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<% if(loggedInUserData != null) {
											if(loggedInUserData.getStaffId() == 1){
										%>
											<a href="<%=resetStaffPassword%>"><bean:message key="staff.resetpassword"/></a>
										<%}else{%>
											<a href="<%=resetStaffPassword%>" style="pointer-events: none;cursor: default"><bean:message key="staff.resetpassword"/></a>
										<%}}%>
									</td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.view" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" onClick="STB('ViewRadiusPolicy');swapImages()"><img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=viewStaff%>"><bean:message key="staff.viewstaff" /></a>
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

