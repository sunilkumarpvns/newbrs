<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
<td colspan="2" valign="top">
<%
	String navigationBasePath = request.getContextPath();
	DiameterSessionManagerData diameterSessionManagerData = (DiameterSessionManagerData)request.getAttribute("diameterSessionManagerData");
	
	String updateDiameterSessionManagerInstance  = navigationBasePath+"/initUpdateDiameterSessionManager.do?sessionManagerId="+diameterSessionManagerData.getSessionManagerId();	
	String updateDiameterSessionManagerScenario  = navigationBasePath+"/initUpdateDiameterSessionManagerScenario.do?sessionManagerId="+diameterSessionManagerData.getSessionManagerId();	
	String viewDiameterSessionManagerInstanceBasicDetails = navigationBasePath+"/viewDiameterSessionManager.do?action=view&sessionManagerId="+diameterSessionManagerData.getSessionManagerId();	
	String viewDiameterSessionManagerScenarioMapping = navigationBasePath+"/viewDiameterSessionManagerScenario.do?action=view&sessionManagerId="+diameterSessionManagerData.getSessionManagerId();	
	String searchDiameterASM = navigationBasePath+"/initSearchDiameterASM.do?sessionManagerId="+diameterSessionManagerData.getSessionManagerId();
	String viewDiameterSessionManagerHistory = navigationBasePath+"/viewDiameterSessionManagerHistory.do?sessionManagerId="+diameterSessionManagerData.getSessionManagerId()+"&auditUid="+diameterSessionManagerData.getAuditUId()+"&name="+diameterSessionManagerData.getName();;	

%>

			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.action" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" onClick="STB('UpdateDiameterPolicy');swapImages()">
							<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"/>
						</a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=updateDiameterSessionManagerInstance%>">
											<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.updatesessionmanager.basicdetails" />
										</a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=updateDiameterSessionManagerScenario%>">
											<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.updatesessionmanager.scenario" />
										</a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=searchDiameterASM%>">
											<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.searchactivesession" />
										</a>
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
						<a href="javascript:void(0)" onClick="STB('ViewDiameterPolicy');swapImages()">
							<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow">
						</a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=viewDiameterSessionManagerInstanceBasicDetails%>">
											<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.viewsessionmanagerdetails" />
										</a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewDiameterSessionManagerScenarioMapping%>">
											<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.viewsessionmanagerscenariomapping" />
										</a>
									</td>
								</tr> 
								<tr>
									<td class="subLinks">
										<a href="<%=viewDiameterSessionManagerHistory%>">
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

