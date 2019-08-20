<%@ page
	import="com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolData"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
	String navigationBasePath = request.getContextPath();
	IIPPoolData ipPoolData = (IIPPoolData) request.getAttribute("ipPoolData");
	
	String updateIPPoolMasterDetails = navigationBasePath+"/initUpdateIPPool.do?action=initUpdate&ipPoolId="+ipPoolData.getIpPoolId();
	String removeIPAddress 			 = navigationBasePath+"/removeIPAddress.do?ipPoolId="+ipPoolData.getIpPoolId();
	String addIPAddress              = navigationBasePath+"/addIPAddress.do?ipPoolId="+ipPoolData.getIpPoolId();  
	String searchIPAddress           = navigationBasePath+"/searchIPAddress.do?ipPoolId="+ipPoolData.getIpPoolId();  
	String changeIPPoolStatus		 = navigationBasePath+"/initChangeStatus.do?action=changeStatus&ipPoolId="+ipPoolData.getIpPoolId()+"&name="+ipPoolData.getName()+"&auditUid="+ipPoolData.getAuditUId();  
	String viewIPPool				 = navigationBasePath+"/viewIPPool.do?ipPoolId="+ipPoolData.getIpPoolId(); 
	String viewIPPoolHistory 		 = navigationBasePath+"/viewIPPoolHistory.do?ipPoolId="+ipPoolData.getIpPoolId()+"&auditUid="+ipPoolData.getAuditUId()+"&name="+ipPoolData.getIpPoolId();	

%>
			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%"><bean:message key="general.action" /></td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" onClick="STB('UpdateRadiusPolicy');swapImages()">
						<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=updateIPPoolMasterDetails%>"><bean:message bundle="ippoolResources" key="ippool.updatedetails" /></a>
									</td>
								</tr>

								<tr>
									<td class="subLinks">
										<a href="<%=searchIPAddress%>"><bean:message bundle="ippoolResources" key="ippool.searchipaddress" /></a>
									</td>
								</tr>

								<tr>
									<td class="subLinks">
										<a href="<%=changeIPPoolStatus%>"><bean:message bundle="ippoolResources" key="ippool.changestatus" /></a>
									</td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
				<tr id=header1>
					<td class="subLinksHeader" width="87%"><bean:message key="general.view" /></td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" onClick="STB('ViewRadiusPolicy');swapImages()">
						<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=viewIPPool%>"><bean:message bundle="ippoolResources" key="ippool.viewippool" /></a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewIPPoolHistory%>">
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

