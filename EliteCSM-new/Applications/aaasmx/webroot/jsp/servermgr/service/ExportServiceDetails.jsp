<%@ include file="/jsp/core/includes/common/Header.jsp"%>







<html:form action="/exportNetServiceDetail">
	<html:hidden name="exportNetServiceConfigurationForm"
		styleId="netServiceId" property="netServiceId" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					height="15%">
					<tr>
						<td class="tblheader-bold" colspan="2"><bean:message
								bundle="servermgrResources" key="servermgr.exportservicedetails" /></td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="btns-td" valign="middle"><input type="button"
				name="c_btnSynchronize"
				onclick="javascript:location.href='<%=basePath%>/jsp/servermgr/ServiceContainer.jsp'"
				id="c_btnSynchronize" value=" Export " class="light-btn"> <input
				type="reset" name="c_btnDeletePolicy"
				onclick="javascript:location.href='<%=basePath%>/viewNetServiceInstance.do?netserviceid=<bean:write name="netServiceInstanceBean" property="netServiceId"/>'"
				value="Cancel" class="light-btn"></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
	</table>
</html:form>

