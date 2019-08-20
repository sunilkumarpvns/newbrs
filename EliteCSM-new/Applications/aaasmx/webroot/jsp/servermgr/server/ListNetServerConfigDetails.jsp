<%@page import="com.elitecore.elitesm.util.constants.PluginConstants"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData"%>
<%@ page import="com.elitecore.elitesm.web.servermgr.server.forms.ListNetServerConfigurationForm"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>

<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<%
	String localBasePath = request.getContextPath();
	ListNetServerConfigurationForm listNetServerConfigurationForm = (ListNetServerConfigurationForm)request.getAttribute("listnetServerConfigurationForm");
	List configInstanceList = ((ListNetServerConfigurationForm)request.getAttribute("listnetServerConfigurationForm")).getConfigInstanceList();
	int iIndex = 0;
%>

<html:form action="/listNetServerConfiguration">
	<html:hidden name="listnetServerConfigurationForm" styleId="action" property="action" />
	<html:hidden name="listnetServerConfigurationForm" styleId="netServerId" property="netServerId" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
					<tr>
						<td class="tblheader-bold" colspan="3">
							<bean:message bundle="servermgrResources" key="servermgr.view.updateserverconfiguration" />
						</td>
					</tr>
					<tr>
						<td colspan="3">
							<table width="100%" cols="8" id="listTable" type="tbl-list" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="right" class="tblheader" valign="top" width="5%">
										<bean:message bundle="servermgrResources" key="servermgr.serialnumber" />
									</td>
									<td align="left" class="tblheader" valign="top" width="20%">
										<bean:message bundle="servermgrResources" key="servermgr.name" />
									</td>
									<td align="left" class="tblheader" valign="top" width="25%">
										<bean:message bundle="servermgrResources" key="servermgr.description" />
									</td>
									<td align="center" class="tblheader" valign="top" width="5%">
										<bean:message key="general.edit" />
									</td>
								</tr>
								<%
    								if(configInstanceList != null && configInstanceList.size() > 0){
								%>
								<logic:iterate id="netConfigInstanceData" name="listnetServerConfigurationForm" property="configInstanceList" type="com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationInstanceData">
									<bean:define id="netConfigurationData" name="netConfigInstanceData" property="netConfiguration" type="com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationData"></bean:define>
									<tr>
										<td align="right" class="tblfirstcol" valign="top"><%=(iIndex+1) %></td>
										<td align="left" class="tblrows" valign="top"><bean:write name="netConfigurationData" property="name" /></td>
										<td align="left" class="tblrows"><bean:write name="netConfigurationData" property="displayName" /></td>
										<td align="center" class="tblcol">
											<a href="<%=localBasePath%>/updateNetServerConfiguration.do?confInstanceId=<bean:write name="netConfigInstanceData" property="configInstanceId"/>&netServerId=<%=listNetServerConfigurationForm.getNetServerId()%>">
												<img src="<%=localBasePath%>/images/edit.jpg" border="0" />
											</a>
										</td>
									</tr>
									<% iIndex += 1; %>
								</logic:iterate>
								<%
   									 }else{
								%>
								<tr>
									<td align="center" class="tblfirstcol" colspan="8">
										<bean:message bundle="servermgrResources" key="servermgr.norecordsfound" />
									</td>
								</tr>
								<%   }  %>
							</table>
						</td>
					</tr>
					<tr>
						<td colspan="1">&nbsp;</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>