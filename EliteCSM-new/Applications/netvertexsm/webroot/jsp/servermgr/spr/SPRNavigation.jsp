<%@page import="com.elitecore.netvertexsm.web.servermgr.spr.form.SPRForm"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td colspan="2" valign="top"> 

<%
	String navigationBasePath = request.getContextPath();
	SPRForm sprForm = (SPRForm) request.getAttribute("sprForm");
	String viewSPR = navigationBasePath+"/sprData.do?method=view&id="+sprForm.getSprId();
	String editSPR = navigationBasePath+"/sprData.do?method=initUpdate&id="+sprForm.getSprId();
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
								<a href="<%=editSPR%>"><bean:message bundle="sprResources" key="spr.update.link"/></a>
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
								<a href="<%=viewSPR%>"><bean:message bundle="sprResources" key="spr.view.link"/></a>
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