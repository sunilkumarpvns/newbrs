<%@page import="com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@page import="com.elitecore.elitesm.util.constants.SessionManagerConstant"%>

<%
	ISessionManagerInstanceData sessionManager = (ISessionManagerInstanceData)request.getAttribute("sessionManagerInstanceData");
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">	
	<tr>
		<td valign="top" align="right">
		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			height="15%">
			<tr>
				<td class="tblheader-bold" colspan="4" height="20%">
					<bean:message bundle="sessionmanagerResources" key="sessionmanager.viewsummary" />
				</td>
			</tr>
			<tr>
				<td class="tblfirstcol" width="23%" height="20%"><bean:message key="general.name" /></td>
				<td class="tblcol" width="30%" height="20%" colspan="3"><%=sessionManager.getName()%>&nbsp;</td>
			</tr>
			<tr>
				<td class="tblfirstcol" width="23%" height="20%"><bean:message key="general.description" /></td>
				<td class="tblcol" width="30%" height="20%" colspan="3"><%=EliteUtility.formatDescription(sessionManager.getDescription())%> &nbsp;</td>
			</tr>
		</table>
		</td>
	</tr>
</table>