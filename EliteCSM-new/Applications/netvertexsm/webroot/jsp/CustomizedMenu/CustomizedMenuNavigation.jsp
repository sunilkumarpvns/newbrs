<%@page import="com.elitecore.netvertexsm.web.customizedmenu.CustomizedMenuForm"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td colspan="2" valign="top"> 

<%
	String navigationBasePath = request.getContextPath();
	CustomizedMenuForm customizedMenuForm = (CustomizedMenuForm) request.getAttribute("customizedMenuForm");
%>      
<script type="text/javascript">
function updateMenu() {
	document.getElementById("updateMenu").href = '<%=navigationBasePath%>/customizedMenumgmt.do?method=initUpdate&title='+encodeURI('<%=customizedMenuForm.getTitle()%>');
}
function viewMenu(){
	document.getElementById("viewMenu").href = '<%=navigationBasePath%>/customizedMenumgmt.do?method=view&title='+encodeURI('<%=customizedMenuForm.getTitle()%>');
}
</script>	                                              

	<table border="0" width="100%" cellspacing="0" cellpadding="0">
		<tr id=header1>
			<td class="subLinksHeader" width="87%">
				<bean:message key="general.action" />
			</td>
			<td class="subLinksHeader" width="13%">
				<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow">
			</td>
		</tr>
		<tr valign="top">
			<td colspan="2" id="backgr1">
				<div>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="subLinks">
								<a href="#" id="updateMenu" onclick="updateMenu();"><bean:message bundle="customizedMenuResources" key="csmmenumgmt.update.link"/></a>
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
				<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow">
			</td>
		</tr>
		<tr valign="top">
			<td colspan="2" id="backgr1">
				<div>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="subLinks">
								<a href="#" id="viewMenu" onclick="viewMenu();"><bean:message bundle="customizedMenuResources" key="csmmenumgmt.view.link"/></a>
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
