<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionConfData"%>



<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td colspan="2" valign="top"> 


<%
	String navigationBasePath = request.getContextPath();
	SessionConfData navigationSessionInstanceData = (SessionConfData)request.getAttribute("sessionConfData");
	// view information url
	String viewSessionConfiguration = navigationBasePath+"/viewSessionConf.do";
	// update inforamation details
	String updateSessionConfiguration = navigationBasePath+"/initEditSessionConf.do";	
	//String viewSessionInstanceAdvanceDetail = navigationBasePath+"/viewSessionInstanceAdvanceDetail.do?gxSMInstanceId="+navigationSessionInstanceData.getGxSMInstanceId();
%>                                                    

	                                                   

			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message bundle="sessionMgrResources" key="session.gx.action" />
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
										<a href="<%=updateSessionConfiguration%>" tabindex="21"><bean:message bundle="sessionMgrResources" key="session.updatesessionconf" /></a>
									</td>
								</tr>
							</table>
						</div>
					</td>
				</tr>			
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message bundle="sessionMgrResources" key="session.gx.view" />
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
										<a href="<%=viewSessionConfiguration%>" tabindex="23"><bean:message bundle="sessionMgrResources" key="session.viewsessionconf" /></a> 
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
