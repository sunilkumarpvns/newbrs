<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>





<% 
	String localBasePath = request.getContextPath();
%>

<html:form action="/exportNetServerDetail">
	<html:hidden name="exportNetServerConfigurationForm"
		styleId="netServerId" property="netServerId" />

	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header"><bean:message
											bundle="servermgrResources"
											key="servermgr.exportservicedetails" /></td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle"><input type="button"
										name="c_btnSynchronize"
										onclick="javascript:location.href='<%=localBasePath%>/jsp/servermgr/ServerContainer.jsp'"
										id="c_btnSynchronize" value=" Export " class="light-btn">
										<input type="reset" name="c_btnDeletePolicy" value="Cancel"
										class="light-btn"></td>
								</tr>
								<tr>
									<td>&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>

</html:form>


