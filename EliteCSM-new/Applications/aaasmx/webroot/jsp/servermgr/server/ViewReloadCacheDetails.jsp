




<%@ page import="com.elitecore.elitesm.web.servermgr.server.forms.ViewReloadCacheDetailsForm"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="java.text.SimpleDateFormat"%>


<%
    
    ViewReloadCacheDetailsForm viewReloadCacheDetailsForm = (ViewReloadCacheDetailsForm)request.getAttribute("viewReloadCacheDetailsForm");
	String dateFormat = ConfigManager.get(ConfigConstant.DATE_FORMAT);
	int iIndex = 0;
%>

<html:form action="/viewReloadCacheDetails">
	<html:hidden name="viewReloadCacheDetailsForm" styleId="netServerId" property="netServerId" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0"
		height="15%" align="right">

		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					height="15%">
					<tr>
						<td>
							&nbsp;
						</td>
					</tr>
						<tr>
							<td class="tblheader-bold" colspan="5" height="20%">
								<bean:message bundle="servermgrResources"
									key="servermgr.viewcachedetails.basicdetails" />
							</td>
						</tr>
					<logic:equal name="viewReloadCacheDetailsForm" property="errorCode" value="0">
						<tr>
							<td align="left" class="tblheader" valign="top" width="5%">
								<bean:message bundle="servermgrResources"
									key="servermgr.serialnumber" />
							</td>
							<td align="left" class="tblheader" valign="top" width="20%">
								<bean:message bundle="servermgrResources"
									key="servermgr.viewcachedetails.name" />
							</td>
							<td align="left" class="tblheader" valign="top" width="35%">
								<bean:message bundle="servermgrResources"
									key="servermgr.viewcachedetails.source" />
							</td>
							<td align="left" class="tblheader" valign="top" width="20%">
								<bean:message bundle="servermgrResources"
									key="servermgr.viewcachedetails.lastupdatedtime" />
							</td>
							<td align="left" class="tblheader" valign="top" width="20%">
								<bean:message bundle="servermgrResources"
									key="servermgr.viewcachedetails.lastreloadattempttime" />
							</td>
					    </tr>
					    
					    <logic:iterate id="cacheDetailBean" name="lstCacheDetails" type="com.elitecore.elitesm.web.servermgr.server.forms.CacheDetailBean">
							<%
							iIndex++;
							%>
							<tr>
								<td align="left" class="tblfirstcol" valign="top">
									<%=iIndex%>
								</td>
								<td align="left" class="tblrows" valign="top">
									<bean:write name="cacheDetailBean" property="name" />&nbsp;
								</td>
								<td align="left" class="tblrows" valign="top">
									<bean:write name="cacheDetailBean" property="source" />&nbsp;
								</td>
								<td align="left" class="tblrows" valign="top">
									<bean:write name="cacheDetailBean" property="lastUpdatedTime" />&nbsp;
								</td>
								<td align="left" class="tblrows" valign="top">
									<bean:write name="cacheDetailBean" property="lastReloadAttemptTime" />&nbsp;
								</td>
							</tr>
						</logic:iterate>
					</logic:equal>
					<%if(iIndex==0){ %>
					<tr>
						<td  align="center" class="tblfirstcol" colspan="5">
							No Records Found.
						</td>
					</tr>
					<%}%>
				</table>
			</td>
		</tr>

		<tr>
			<td>
				&nbsp;
			</td>
		</tr>

		<logic:notEqual name="viewReloadCacheDetailsForm"
			property="errorCode" value="0">

			<tr>
				<td class="blue-text-bold">
					<bean:message bundle="servermgrResources"
						key="servermgr.connectionfailure" />
					<br>
					<bean:message bundle="servermgrResources"
						key="servermgr.admininterfaceip" />
					:
					<bean:write name="netServerInstanceData" property="adminHost" />
					<br>
					<bean:message bundle="servermgrResources"
						key="servermgr.admininterfaceport" />
					:
					<bean:write name="netServerInstanceData" property="adminPort" />
					&nbsp;
				</td>
			</tr>
			<tr>
				<td>
					&nbsp;
				</td>
			</tr>
		</logic:notEqual>

	</table>
</html:form>
