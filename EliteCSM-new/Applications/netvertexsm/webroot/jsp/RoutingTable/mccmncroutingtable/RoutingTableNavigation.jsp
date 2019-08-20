<%@ page import="com.elitecore.netvertexsm.web.RoutingTable.mccmncroutingtable.form.RoutingTableManagementForm"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td colspan="2" valign="top"> 

<%
	String navigationBasePath = request.getContextPath();
	RoutingTableManagementForm routingTableManagementForm = (RoutingTableManagementForm) request.getAttribute("routingTableManagementForm");
	// view information url
	String viewRoutingTable = navigationBasePath+"/routingTableManagement.do?method=view&routingTableId="+routingTableManagementForm.getRoutingTableId();
	String editRoutingTable = navigationBasePath+"/routingTableManagement.do?method=initUpdate&routingTableId="+routingTableManagementForm.getRoutingTableId();
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
								<a href="<%=editRoutingTable%>"><bean:message bundle="routingMgmtResources" key="routingtable.update.link"/></a>
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
								<a href="<%=viewRoutingTable%>"><bean:message bundle="routingMgmtResources" key="routingtable.view.link"/></a>
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