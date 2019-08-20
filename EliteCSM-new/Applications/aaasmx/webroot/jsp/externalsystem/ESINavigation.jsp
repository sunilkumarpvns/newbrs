<%@page import="com.elitecore.elitesm.datamanager.externalsystem.data.*"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">

	<tr>
		<td colspan="2" valign="top">
			<%																						
	String navigationBasePath = request.getContextPath();
	ESITypeAndInstanceData esiTypeInstance1 = (ESITypeAndInstanceData)session.getAttribute("esiTypeInstance");
	String updateESI = navigationBasePath+"/initupdateESI.do?esiInstanceId="+esiTypeInstance1.getEsiInstanceId();	
	String viewESI = navigationBasePath+"/viewESI.do?esiInstanceId="+esiTypeInstance1.getEsiInstanceId();	
	String viewESIHistory = navigationBasePath+"/viewESIHistory.do?esiInstanceId="+esiTypeInstance1.getEsiInstanceId()+"&auditUid="+esiTypeInstance1.getAuditUId()+"&name="+esiTypeInstance1.getName();

%>

			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
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
									<td class="subLinks"><a href="<%=updateESI%>"><bean:message
												bundle="externalsystemResources" key="esi.update" /></a></td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
				<tr id=header1>
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
									<td class="subLinks">
										<a href="<%=viewESI%>">
											<bean:message bundle="externalsystemResources" key="esi.view" />
										</a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewESIHistory%>">
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

