<%@page import="com.elitecore.netvertexsm.web.bitemplate.form.BITemplateForm"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td colspan="2" valign="top"> 

<%
	String navigationBasePath = request.getContextPath();
	BITemplateForm navigationBITemp = (BITemplateForm) request.getAttribute("biTemplateForm");
	// view information url
	String viewBITemplate = navigationBasePath+"/viewBITemplate.do?id="+navigationBITemp.getBiId();
	// update inforamation details
	String editBITemplate = navigationBasePath+"/initEditBITemplate.do?id="+navigationBITemp.getBiId();
	// Upload file details
	String uploadFile = navigationBasePath+"/uploadFile.do?id="+navigationBITemp.getBiId()+"&type=load";
%>                                                    

	<table border="0" width="100%" cellspacing="0" cellpadding="0">
		<tr id=header1>
			<td class="subLinksHeader" width="87%">
				<bean:message key="general.action" />
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
								<a href="<%=editBITemplate%>">Update BI/CEA Template</a>
							</td>
						</tr>
						<tr>
							<td class="subLinks">
								<a href="<%=uploadFile%>">Upload CSV File</a>
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
				<a href="javascript:void(0)" onClick="STB('ViewRadiusPolicy');swapImages()"><img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a>
			</td>
		</tr>
		<tr valign="top">
			<td colspan="2" id="backgr1">
				<div>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="subLinks">
								<a href="<%=viewBITemplate%>">View BI/CEA Template</a>
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