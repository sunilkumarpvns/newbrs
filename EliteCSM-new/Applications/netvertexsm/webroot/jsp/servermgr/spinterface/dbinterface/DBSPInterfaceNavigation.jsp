<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DatabaseSPInterfaceData"%>
<%@page import="com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData"%>



<%

 	DatabaseSPInterfaceData spInterfaceData = null;
 	if (driverInstanceData != null) {
 		Set dbAuthDriverSet = driverInstanceData.getDatabaseSPInterfaceDriverSet();
 		if (dbAuthDriverSet != null && !dbAuthDriverSet.isEmpty()) {
 			Iterator iterator = dbAuthDriverSet.iterator();
 			if (iterator.hasNext()) {
 				spInterfaceData = (DatabaseSPInterfaceData) iterator.next();
 			}
 		}
 	}
 	// view information url
 	
 	String viewDriverInstance = request.getContextPath()
 			+ "/viewSPInterface.do?driverInstanceId="
 			+ driverInstanceData.getDriverInstanceId();
 	// update inforamation details
 	String updateDBSPRData = request.getContextPath()
 			+ "/initEditSPInterface.do?driverInstanceId="
 			+ driverInstanceData.getDriverInstanceId();
 	

%>                                                    

	                                                   
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td colspan="2" valign="top"> 
			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message bundle="driverResources" key="driver.driverinstance.action" />
					</td>
					<td class="subLinksHeader" width="13%">
						<img src="<%=request.getContextPath()%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"/>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=updateDBSPRData%>" tabindex="11"><bean:message bundle="driverResources" key="spinterface.update" /></a>
									</td>
								</tr>
							
							</table>
						</div>
					</td>
				</tr>			
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message bundle="driverResources" key="driver.driverinstance.view" />
					</td>
					<td class="subLinksHeader" width="13%">
						<img src="<%=request.getContextPath()%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"/>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=viewDriverInstance%>" tabindex="18"><bean:message bundle="driverResources" key="spinterface.view" /></a> 
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
