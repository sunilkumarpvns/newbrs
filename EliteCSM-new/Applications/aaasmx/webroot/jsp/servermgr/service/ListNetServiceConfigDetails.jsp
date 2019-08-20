<%@ page import="java.util.List"%>
<%@ page
	import="com.elitecore.elitesm.web.servermgr.service.forms.ListNetServiceConfigurationForm"%>
<%@ page
	import="com.elitecore.elitesm.datamanager.servermgr.data.NetServiceInstanceData"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>

<%@ include file="/jsp/core/includes/common/Header.jsp"%>






<%
	ListNetServiceConfigurationForm listNetServiceConfigurationForm = ((ListNetServiceConfigurationForm)request.getAttribute("listNetServiceConfigurationForm"));
	List configInstanceList = listNetServiceConfigurationForm.getConfigInstanceList();
    //List driverConfigInstanceList = listNetServiceConfigurationForm.getDriverConfigInstanceList();    
    //List netDriverTypeList = (List)request.getAttribute("lstNetDriverType");    
    INetServiceInstanceData netServiceInstanceDataObject =(INetServiceInstanceData)request.getAttribute("netServiceInstanceData");
	int iIndex = 0;
%>

<html:form action="/listNetServiceConfiguration">
	<html:hidden name="listNetServiceConfigurationForm" styleId="action"
		property="action" />
	<html:hidden name="listNetServiceConfigurationForm"
		styleId="netServiceId" property="netServiceId" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					height="15%">
					<tr>
						<td class="tblheader-bold" colspan="3"><bean:message
								bundle="servermgrResources"
								key="servermgr.view.updateserviceconfiguration" />&nbsp;[<%=netServiceInstanceDataObject.getName()%>]</td>
					</tr>
					<tr>
						<td colspan="3">
							<table width="100%" cols="8" id="listTable" type="tbl-list"
								border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="right" class="tblheader" valign="top" width="5%"><bean:message
											bundle="servermgrResources" key="servermgr.serialnumber" /></td>
									<td align="left" class="tblheader" valign="top" width="20%"><bean:message
											bundle="servermgrResources" key="servermgr.name" /></td>
									<td align="left" class="tblheader" valign="top" width="25%"><bean:message
											bundle="servermgrResources" key="servermgr.description" /></td>
									<td align="center" class="tblheader" valign="top" width="5%"><bean:message
											key="general.edit" /></td>
								</tr>
								<%
    if(configInstanceList != null && configInstanceList.size() > 0){
%>
								<logic:iterate id="netConfigInstanceData"
									name="listNetServiceConfigurationForm"
									property="configInstanceList"
									type="com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationInstanceData">
									<bean:define id="netConfigurationData"
										name="netConfigInstanceData" property="netConfiguration"
										type="com.elitecore.elitesm.datamanager.servermgr.data.INetConfigurationData"></bean:define>
									<tr>
										<td align="right" class="tblfirstcol" valign="top"><%=(iIndex+1) %></td>
										<td align="left" class="tblrows" valign="top"><bean:write
												name="netConfigurationData" property="name" /></td>
										<td align="left" class="tblrows"><bean:write
												name="netConfigurationData" property="displayName" /></td>
										<td align="center" class="tblcol"><a
											href="<%=basePath%>/updateServiceConfiguration.do?confInstanceId=<bean:write name="netConfigInstanceData" property="configInstanceId"/>&netServiceId=<%=listNetServiceConfigurationForm.getNetServiceId()%>">
												<img src="<%=basePath%>/images/edit.jpg" border="0" />
										</a></td>
									</tr>
									<% iIndex += 1; %>
								</logic:iterate>
								<%
    }else{
%>
								<tr>
									<td align="center" class="tblfirstcol" colspan="8"><bean:message
											bundle="servermgrResources" key="servermgr.norecordsfound" /></td>
								</tr>
								<%
    }
%>
							</table>
						</td>
					</tr>



					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>

				</table>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
	</table>
</html:form>
