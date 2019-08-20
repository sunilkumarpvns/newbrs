<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerData"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
	String navigationBasePath 		 = request.getContextPath();
	String updateAlertListener       = navigationBasePath+"/updateAlertListener.do?listenerId="+alertListenerData.getListenerId();
	String viewAlertListener		 = navigationBasePath+"/viewAlertListener.do?listenerId="+alertListenerData.getListenerId();
	String viewHistory  			 = navigationBasePath+"/viewAlertListenerHistory.do?listenerId="+alertListenerData.getListenerId()+"&auditUid="+alertListenerData.getAuditUId()+"&name="+alertListenerData.getName();

%>

			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header>
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
									<td class="subLinks"><a href="<%=updateAlertListener%>"><bean:message
												bundle="servermgrResources"
												key="servermgr.alert.updatealertlistener" /></a></td>
								</tr>

							</table>
						</div>
					</td>
				</tr>
				<tr id=header>
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
									<td class="subLinks"><a href="<%=viewAlertListener%>"><bean:message
												bundle="servermgrResources"
												key="servermgr.alert.viewalertlistener" /></a></td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
				<tr>
					<td class="subLinks">
						<a href="<%=viewHistory%>" class="subLink">
							<bean:message key="view.history" />
						</a>
					</td>
				</tr>
			</table>
		</td>
	</tr>

</table>

