<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DatabaseSPInterfaceData"%>
<%@page import="com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData"%>



<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td colspan="2" valign="top"> 


<%
 	String navigationBasePath = request.getContextPath();
 	DriverInstanceData driverInstance = (DriverInstanceData) request.getAttribute("driverInstanceData");
 	if(driverInstance == null) {
		DriverBLManager driverBLManager = new DriverBLManager();
		driverInstance = new DriverInstanceData();
		driverInstance.setDriverInstanceId(Long.parseLong(request.getParameter("id")));
		driverInstance = driverBLManager.getDriverInstanceData(driverInstanceData);
	}
 	DatabaseSPInterfaceData spInterfaceData = null;
 	if (driverInstance != null) {
 		Set dbAuthDriverSet = driverInstance.getDatabaseSPInterfaceDriverSet();
 		if (dbAuthDriverSet != null && !dbAuthDriverSet.isEmpty()) {
 			Iterator iterator = dbAuthDriverSet.iterator();
 			if (iterator.hasNext()) {
 				spInterfaceData = (DatabaseSPInterfaceData) iterator.next();
 			}
 		}
 	}

 	// view information url
 	
 	String viewDriverInstance = navigationBasePath
 			+ "/viewSPInterface.do?driverInstanceId="
 			+ driverInstance.getDriverInstanceId();

 	// update inforamation details
 	String updateDBSPInterfaceData = navigationBasePath
 			+ "/initEditSPInterface.do?driverInstanceId="
 			+ driverInstance.getDriverInstanceId();
 	String viewDriverAdvanceDetail = navigationBasePath
 			+ "/viewSPInterfaceAdvanceDetail.do?driverInstanceId="
 			+ driverInstance.getDriverInstanceId();
 	
 	String uploadAddFile=null;
 	String uploadUpdateFile=null;
 	String uploadFileSummary=null;
 	if(spInterfaceData!=null){
 	// Upload file details to add subscribers
 	 uploadAddFile = navigationBasePath
 			+ "/jsp/servermgr/spinterface/dbinterface/spmgmt/UploadCSVForSubProfileContainer.jsp?id="
 			+ driverInstance.getDriverInstanceId()
 			+ "&dbDriverId=" 
 			+ spInterfaceData.getDatabaseSpInterfaceId()
 			+ "&operationType=ADD";
 	
    // Upload file details to update subscribers
 	 uploadUpdateFile = navigationBasePath
 			+ "/jsp/servermgr/spinterface/dbinterface/spmgmt/UploadCSVForSubProfileContainer.jsp?id="
 			+ driverInstance.getDriverInstanceId()
 			+ "&dbDriverId=" 
 			+ spInterfaceData.getDatabaseSpInterfaceId()
 			+ "&operationType=UPDATE";
    
 	// Upload file Summary 
 	 uploadFileSummary = navigationBasePath
 			+ "/subProfileUploadSummary.do?driverInstanceId="
 			+ driverInstance.getDriverInstanceId()
 			+ "&dbDriverId=" 
 			+ spInterfaceData.getDatabaseSpInterfaceId()
 			+ "&operationType=Summary";
 	}
%>                                                    

	                                                   

			<table border="0" width="97%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message bundle="driverResources" key="driver.driverinstance.action" />
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
										<a href="<%=updateDBSPInterfaceData%>" tabindex="9"><bean:message bundle="driverResources" key="driver.driverinstance.update.link" /></a>
									</td>
								</tr>
								<%
									if (spInterfaceData != null) {

										String updateFieldName = navigationBasePath
												+ "/updateDatabaseSubscriberProfileField.do?driverInstanceId="
												+ driverInstanceData.getDriverInstanceId()
												+ "&dbAuthId=" + spInterfaceData.getDatabaseSpInterfaceId()
												+ "&checkForOutcome=";
										String addSubscriberProfileData = navigationBasePath
												+ "/addDatabaseSubscriberProfileData.do?driverInstanceId="
												+ driverInstanceData.getDriverInstanceId()
												+ "&dbAuthId=" + spInterfaceData.getDatabaseSpInterfaceId()
												+ "&firstFieldData=&secondFieldData=";
										String searchSubscriberProfileData = navigationBasePath
												+ "/searchDatabaseSubscriberProfileData.do?driverInstanceId="
												+ driverInstanceData.getDriverInstanceId()
												+ "&dbAuthId=" + spInterfaceData.getDatabaseSpInterfaceId();
										String updateValuePool = navigationBasePath
												+ "/updateDatabaseSubscriberProfileValuePool.do?driverInstanceId="
												+ driverInstanceData.getDriverInstanceId()
												+ "&dbAuthId=" + spInterfaceData.getDatabaseSpInterfaceId()
												+ "&checkForOutcome=forValuePool";
								%>

								<tr>
									<td class="subLinks">
										<a href="<%=updateFieldName%>"><bean:message bundle="driverResources" key="subscriberprofile.database.updatefieldname" /></a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=updateValuePool%>"><bean:message bundle="driverResources" key="subscriberprofile.database.updatevaluepool" /></a>
									</td>
								</tr>
									<tr>
									<td class="subLinks">
										<bean:message bundle="driverResources" key="subscriberprofile.database.title" /> 
									</td>
									</tr>
									<tr>
									<td class="subLinks">&nbsp;-&nbsp;
										<a href="<%=addSubscriberProfileData%>"><bean:message bundle="driverResources" key="subscriberprofile.database.adddata" /></a>
									</td>
									</tr>
									<tr>
									<td class="subLinks">&nbsp;-&nbsp;
										<a href="<%=searchSubscriberProfileData%>"><bean:message bundle="driverResources" key="subscriberprofile.database.searchdata" /></a>
									</td>
									</tr>
									<tr>
									<td class="subLinks">&nbsp;-&nbsp;
										<a href="<%=uploadAddFile%>">Add Subscriber by CSV</a>
									</td>
									</tr>	
									<tr>
									<td class="subLinks">&nbsp;-&nbsp;
										<a href="<%=uploadUpdateFile%>">Update Subscriber by CSV</a>
									</td>
									</tr>
								<%
									}
								%>
								
							</table>
						</div>
					</td>
				</tr>			
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message bundle="driverResources" key="driver.driverinstance.view" />
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
										<a href="<%=viewDriverInstance%>" tabindex="10"><bean:message bundle="driverResources" key="driver.driverinstance.view.link" /></a> 
									</td>
								</tr>
								<%if(uploadFileSummary!=null){%>
								<tr>
									<td class="subLinks">
										<a href="<%=uploadFileSummary%>" tabindex="10">File Upload Summary</a> 
									</td>
								</tr>
								<%} %>
								<tr>
									<td class="subLinks">
										<a href="#" onclick="javascript:show()" tabindex="11"><bean:message bundle="driverResources" key="driver.driverinstance.view.advancedetail" /></a>
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
