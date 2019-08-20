<%@ include file="/jsp/core/includes/common/Header.jsp"%>






<script>
function validateCreate(){
	if(document.forms[0].configurationFile.value == ''){
	    alert('Import file is a compulsory field Please import file.');
	}else{
		document.forms[0].submit();
	}
}
</script>

<html:form action="/importNetServiceConfigurationDetail"
	enctype="multipart/form-data">
	<html:hidden name="importNetServiceConfigurationForm"
		styleId="netServiceId" property="netServiceId" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					height="15%">
					<tr>
						<td class="tblheader-bold" colspan="2"><bean:message
								bundle="servermgrResources" key="servermgr.importservicedetails" /></td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<td align="center" class="labeltext" valign="top" width="10%"><bean:message
								bundle="servermgrResources" key="servermgr.importsource" /></td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<input type="file" name="configurationFile" size="40" /><font
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
				id="c_btnSynchronize" value=" Import " class="light-btn"> <input
				type="reset" name="c_btnDeletePolicy"
				onclick="javascript:location.href='<%=basePath%>/viewNetServiceInstance.do?netserviceid=<bean:write name="netServiceInstanceBean" property="netServiceId"/>'"
				value="Cancel" class="light-btn"></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
	</table>

</html:form>