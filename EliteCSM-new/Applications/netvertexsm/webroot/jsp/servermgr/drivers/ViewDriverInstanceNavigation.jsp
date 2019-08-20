<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td colspan="2" valign="top"> 


<%
 	String navigationBasePath = request.getContextPath();
 	DriverInstanceData driverInstance = (DriverInstanceData) request.getAttribute("driverInstanceData");
 	

 	String viewDriverInstance = navigationBasePath+ "/viewDriverInstance.do?driverInstanceId="+ driverInstance.getDriverInstanceId();

 	// update inforamation details
 	String updateDriverBasicDetails = navigationBasePath+ "/initEditDriverBasicDetails.do?driverInstanceId="+ driverInstance.getDriverInstanceId();
 	String updateDriverInstance = navigationBasePath+ "/initEditDriverInstance.do?driverInstanceId="+ driverInstance.getDriverInstanceId();
 	String viewDriverAdvanceDetail = navigationBasePath+ "/viewDriverAdvanceDetail.do?driverInstanceId="+ driverInstance.getDriverInstanceId();
%>                                                    

	                                                   

			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message bundle="driverResources" key="driver.driverinstance.action" />
					</td>
					<td class="subLinksHeader" width="13%">
						<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow">
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=updateDriverBasicDetails%>" tabindex="41" ><bean:message key="general.update.basicdetails.link" /></a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=updateDriverInstance%>" tabindex="42"><bean:message bundle="driverResources" key="driver.driverinstance.update.link" /></a>
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
						<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow">
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=viewDriverInstance%>" tabindex="43"><bean:message bundle="driverResources" key="driver.driverinstance.view.link" /></a> 
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
