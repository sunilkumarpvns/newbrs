



<%@ page
	import="com.elitecore.elitesm.web.servermgr.server.forms.ListUserfileDatasourceForm"%>

<%
	String localBasePath = request.getContextPath();
	String netserverid=(String)request.getParameter("netserverid");
	ListUserfileDatasourceForm listUserfileDatasourceForm=(ListUserfileDatasourceForm)request.getAttribute("listUserfileDatasourceForm");
	
	
	
%>
<script type="text/javascript">

function selectFile() 
{
	var filename=document.listUserfileDatasourceForm.selectedFileName.value;
	location.href="<%=localBasePath%>/listUserfileAccountInformation.do?netserverid=<%=netserverid%>&selectedFileName="+filename;
}

</script>

<html:form action="/listUserfileDatasource">
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td class="small-gap" colspan="3">&nbsp;</td>
		</tr>
		<table width="100%" cols="2" id="listTable" type="tbl-list" border="0"
			cellpadding="0" cellspacing="0">
			<tr>
				<td class="table-header" width="24%" colspan="1" align="left">
					<bean:message bundle="servermgrResources"
						key="servermgr.userfile.userfiledatasourcelist" />
				</td>
				<td align="left" class="blue-text" valign="middle" width="100%"
					colspan="1">&nbsp;</td>
			</tr>
			<%
			String []userfiles=listUserfileDatasourceForm.getUserfiles();
		%>

			<%if(userfiles != null && userfiles.length > 0){%>
			<tr>
				<td align="middle" class="labeltext" width="10%"></br>
					&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources"
						key="servermgr.userfile.selectuserfiles" /></td>
				<td align="left"></br> <html:select
						name="listUserfileDatasourceForm" styleId="selectedFileName"
						property="selectedFileName">
						<% for(int i=0;i<userfiles.length;i++) { %>
						<html:option value="<%=userfiles[i]%>"><%=userfiles[i]%></html:option>
						<%} %>
					</html:select></td>
			</tr>
			<tr>
				<td align="right" width="10%" height="20%"></br>
					&nbsp;&nbsp;&nbsp;&nbsp; <input type="button" value="  Select   "
					class="light-btn" onclick="selectFile()" /></td>
				<td align="left"></br> &nbsp; <input type="button" value="Cancel"
					class="light-btn" /></td>
			</tr>
			<%}else{%>
			<tr>
				<td align="middle" class="labeltext" width="50%"></br>
					&nbsp;&nbsp;&nbsp;&nbsp; <bean:message bundle="servermgrResources"
						key="servermgr.userfile.alertMessage" /></td>
			</tr>
			<%}%>
		</table>
		</html:form>