
<%@page
	import="com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
 	String navigationBasePath = request.getContextPath();	
	String viewClientHistory = navigationBasePath+"/viewDiameterPeerProfileHistory.do?peerProfileId="+diameterPeerProfileData.getPeerProfileId()+"&auditUid="+diameterPeerProfileData.getAuditUId()+"&name="+diameterPeerProfileData.getProfileName();	
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
												bundle="diameterResources"
												key="diameterpeerprofile.updatepeerprofile" /></a></td>
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
												bundle="diameterResources"
												key="diameterpeerprofile.viewpeerprofile" /></a></td>
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
	   location.href='<%=basePath%>/initUpdateDiameterPeerProfile.do?peerProfileId='+'<%=diameterPeerProfileData.getPeerProfileId()%>';
   }
   
   function viewDetail()
   {
	   location.href='<%=basePath%>/viewDiameterPeerProfile.do?peerProfileId='+'<%=diameterPeerProfileData.getPeerProfileId()%>';
   }
   
</script>
