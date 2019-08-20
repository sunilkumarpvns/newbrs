<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<% 
	String localBasePath = request.getContextPath();
	String[] fileGroups = (String[])request.getAttribute("fileGroups");
	
%>

<script>
function validateCreate(){
	if(document.forms[0].configurationFile.value == ''){
	    alert('Upload file is a compulsory field. Please Click on Browse to upload file.');
	}else{
		document.forms[0].submit();
	}
}
</script>

<html:form action="/uploadRadiusDictionary"
	enctype="multipart/form-data">
	<html:hidden name="uploadNetServerDictionaryForm" styleId="netServerId"
		property="netServerId" />
	<html:hidden name="uploadNetServerDictionaryForm" styleId="action"
		property="action" value="upload" />


	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					height="15%">
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td class="tblheader-bold" colspan="2"><bean:message
								bundle="servermgrResources" key="servermgr.uploaddictionary" /></td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>

					</tr>
					<%if(fileGroups!=null && fileGroups.length>0) %>
					<tr>

						<td align="center" class="labeltext" valign="top" width="10%">Select
							Dictionary Group</td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<%for (int i=0;i<fileGroups.length;i++){%> <html:radio
								property="fileGroup" value="<%=fileGroups[i]%>"><%=fileGroups[i]%></html:radio>
							<%} %>
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<td align="center" class="labeltext" valign="top" width="10%"><bean:message
								bundle="servermgrResources" key="servermgr.uploaddictionary" /></td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<input type="file" name="configurationFile" size="35" /><font
							color="#FF0000"> *</font>
						</td>
					</tr>

				</table>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="btns-td" valign="middle"><input type="button"
				name="c_btnSynchronize" onclick="validateCreate()"
				id="c_btnSynchronize" value=" Upload " class="light-btn"> <input
				type="reset" name="c_btnDeletePolicy"
				onclick="javascript:location.href='<%=localBasePath%>/manageRadiusDictionary.do?netServerId=<bean:write name="uploadNetServerDictionaryForm" property="netServerId"/>'"
				value="Cancel" class="light-btn"></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
	</table>
</html:form>

<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
