<%@page import="com.elitecore.netvertexsm.web.locationconfig.area.form.AreaMgmtForm"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td colspan="2" valign="top"> 

<%
 	String navigationBasePath = request.getContextPath();
   	AreaMgmtForm areaMgmtForm = (AreaMgmtForm)request.getAttribute("areaMgmtForm");
   	String viewAreaManagement = navigationBasePath+"/areaManagement.do?method=view&areaId="+areaMgmtForm.getAreaId();
   	String editAreaManagement = navigationBasePath+"/areaManagement.do?method=initUpdate&areaId="+areaMgmtForm.getAreaId();
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
								<a href="<%=editAreaManagement%>"><bean:message bundle="locationMasterResources" key="area.management.update.link"/></a>
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
								<a href="<%=viewAreaManagement%>"><bean:message bundle="locationMasterResources" key="area.management.view.link"/></a>
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