
<%@page
	import="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
    DiameterPeerData diameterPeerData = (DiameterPeerData) request.getAttribute("diameterPeerData");
	String navigationBasePath = request.getContextPath();	
	String viewClientHistory = navigationBasePath+"/viewDiameterPeerHistory.do?peerUUID="+diameterPeerData.getPeerUUID()+"&auditUid="+diameterPeerData.getAuditUId()+"&name="+diameterPeerData.getName();	

%>
			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%"><bean:message
							key="general.action" /></td>
					<td class="subLinksHeader" width="13%"><a
						href="javascript:void(0)"><img
							src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg"
							border="0" name="arrow"></a></td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks"><a href="javascript:void(0)"
										onclick="editDetail()" class="subLink"><bean:message
												bundle="diameterResources" key="diameterpeer.updatepeer" /></a></td>
								</tr>

							</table>
						</div>
					</td>
				</tr>

				<tr id=header1>
					<td class="subLinksHeader" width="87%"><bean:message
							key="general.view" /></td>
					<td class="subLinksHeader" width="13%"><a
						href="javascript:void(0)"><img
							src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg"
							border="0" name="arrow"></a></td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks"><a href="javascript:void(0)"
										onclick="viewDetail()" class="subLink"><bean:message
												bundle="diameterResources" key="diameterpeer.viewpeer" /></a></td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewClientHistory%>">
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

<script language="javascript">
   function editDetail()
   {
	   location.href='<%=basePath%>/initUpdateDiameterPeer.do?peerUUID='+'<%=diameterPeerData.getPeerUUID()%>';
   }
   
   function viewDetail()
   {
	   location.href='<%=basePath%>/viewDiameterPeer.do?peerUUID='+'<%=diameterPeerData.getPeerUUID()%>';
   }
   
</script>
