<%@page import="com.elitecore.netvertexsm.web.devicemgmt.form.DeviceManagementForm"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td colspan="2" valign="top"> 

<%
	String navigationBasePath = request.getContextPath();
	DeviceManagementForm deviceManagementForm = (DeviceManagementForm) request.getAttribute("deviceManagementForm");
	// view information url
	String viewDeviceManagement = navigationBasePath+"/deviceMgmt.do?method=view&tacDetailId="+deviceManagementForm.getTacDetailId();
	String editDeviceManagement = navigationBasePath+"/deviceMgmt.do?method=initUpdate&tacDetailId="+deviceManagementForm.getTacDetailId();
	String exportDeviceManagement = navigationBasePath+"/deviceMgmt.do?method=exportCSV&tacDetailIds="+deviceManagementForm.getTacDetailId();
%>                                                    

	<table border="0" width="100%" cellspacing="0" cellpadding="0">
		<tr id=header1>
			<td class="subLinksHeader" width="87%">
				<bean:message key="general.action" />
			</td>
			<td class="subLinksHeader" width="13%">
				<a href="javascript:void(0)" onClick="swapImages()"><img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a>
			</td>
		</tr>
		<tr valign="top">
			<td colspan="2" id="backgr1">
				<div>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="subLinks">
								<a href="<%=editDeviceManagement%>"><bean:message bundle="deviceMgmtResources" key="devicemgmt.update.link"/></a>
							</td>
						</tr>
						<tr>
							<td class="subLinks">
								<a href="<%=exportDeviceManagement%>"><bean:message bundle="deviceMgmtResources" key="devicemgmt.export.link"/></a>
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
				<a href="javascript:void(0)" onClick="swapImages()"><img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a>
			</td>
		</tr>
		<tr valign="top">
			<td colspan="2" id="backgr1">
				<div>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="subLinks">
								<a href="<%=viewDeviceManagement%>"><bean:message bundle="deviceMgmtResources" key="devicemgmt.view.link"/></a>
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