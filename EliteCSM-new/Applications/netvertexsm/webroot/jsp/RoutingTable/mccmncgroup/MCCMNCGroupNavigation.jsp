<%@ page import="com.elitecore.netvertexsm.web.RoutingTable.mccmncgroup.form.MCCMNCGroupManagementForm"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td colspan="2" valign="top"> 

<%
	String navigationBasePath = request.getContextPath();
	MCCMNCGroupManagementForm mccMNCGroupManagmentForm = (MCCMNCGroupManagementForm) request.getAttribute("mccMNCGroupManagmentForm");
	// view information url
	String viewMccMncGroup = navigationBasePath+"/mccmncGroupManagement.do?method=view&mccmncGroupId="+mccMNCGroupManagmentForm.getMccmncGroupId();
	String editMCCMNCGroup = navigationBasePath+"/mccmncGroupManagement.do?method=initUpdate&mccmncGroupId="+mccMNCGroupManagmentForm.getMccmncGroupId();
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
								<a href="<%=editMCCMNCGroup%>"><bean:message bundle="routingMgmtResources" key="mccmncgroup.update.link"/></a>
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
								<a href="<%=viewMccMncGroup%>"><bean:message bundle="routingMgmtResources" key="mccmncgroup.view.link"/></a>
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