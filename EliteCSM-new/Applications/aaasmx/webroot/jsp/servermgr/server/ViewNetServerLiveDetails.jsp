




<%@ page import="com.elitecore.elitesm.web.servermgr.server.forms.ViewNetServerLiveDetailsForm"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.core.util.mbean.data.live.EliteNetServerDetails"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.core.services.data.LiveServiceSummary"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="com.elitecore.core.commons.scheduler.EliteTaskData"%>



<%
    EliteNetServerDetails liveDetailsBean = (EliteNetServerDetails)request.getAttribute("eliteLiveServerDetails");
    ViewNetServerLiveDetailsForm netServerLiveDetailsForm = (ViewNetServerLiveDetailsForm)request.getAttribute("viewNetServerLiveDetailsForm");
   
    List lstServiceDetails = liveDetailsBean.getServiceSummaryList();
    List lstServerInternalTasks = liveDetailsBean.getInternalTasks();
	String dateFormat = ConfigManager.get(ConfigConstant.DATE_FORMAT);
%>

<html:form action="/viewNetServerLiveDetails">
	<html:hidden name="viewNetServerLiveDetailsForm" styleId="netServerId" property="netServerId" />
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
						<td class="tblheader-bold" colspan="4">
							<bean:message bundle="servermgrResources"
								key="servermgr.liveserverdetails" />
						</td>
					</tr>
					<logic:equal name="viewNetServerLiveDetailsForm"
						property="errorCode" value="0">
						<tr>
							<td colspan="1" align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="servermgrResources"
									key="servermgr.servername" />
							</td>
							<td colspan="3" align="left" class="tblcol" valign="top">
								<bean:write name="eliteLiveServerDetails" property="name" />
								&nbsp;
							</td>
						</tr>
						<tr>
							<td align="left" class="tblfirstcol" valign="top" width="20%">
								<bean:message bundle="servermgrResources"
									key="servermgr.serveridentification" />
							</td>
							<td align="left" class="tblcol" valign="top" width="30%">
								<bean:write name="eliteLiveServerDetails"
									property="identification" />
								&nbsp;
							</td>
							<td align="left" class="tblfirstcol" valign="top" width="20%">
								<bean:message bundle="servermgrResources"
									key="servermgr.serverversion" />
							</td>
							<td align="left" class="tblcol" valign="top" width="30%">
								<bean:write name="eliteLiveServerDetails" property="version" />
								&nbsp;
							</td>
						</tr>
						
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="servermgrResources"
									key="servermgr.server.serverstartupdatetime" />
							</td>
							<td align="left" class="tblcol" valign="top">
								<%=EliteUtility.dateToString(liveDetailsBean.getServerStartUpTime(), ConfigManager.get(ConfigConstant.DATE_FORMAT))%>
								&nbsp;
							</td>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="servermgrResources"
									key="servermgr.server.softstartdatetime" />
							</td>
							<td align="left" class="tblcol" valign="top">
								<%=EliteUtility.dateToString(liveDetailsBean.getSoftRestartTime(), ConfigManager.get(ConfigConstant.DATE_FORMAT))%>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="servermgrResources"
									key="servermgr.server.cachereloaddatetime" />
							</td>
							<td align="left" class="tblcol" valign="top">
								<%=EliteUtility.dateToString(liveDetailsBean.getCacheReloadTime(), ConfigManager.get(ConfigConstant.DATE_FORMAT))%>
								&nbsp;
							</td>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="servermgrResources"
									key="servermgr.server.serverreloaddatetime" />
							</td>
							<td align="left" class="tblcol" valign="top">
								<%=EliteUtility.dateToString(liveDetailsBean.getServerReloadTime(), ConfigManager.get(ConfigConstant.DATE_FORMAT))%>
								&nbsp;
							</td>
						</tr>
					</logic:equal>

				</table>
			</td>
		</tr>

		<tr>
			<td>
				&nbsp;
			</td>
		</tr>

		<logic:equal name="viewNetServerLiveDetailsForm" property="errorCode"
			value="0">
			<tr>
				<td valign="top" align="right">
					<table width="100%" border="0" cellpadding="0" cellspacing="0"
						height="15%">
						<tr>
							<td class="tblheader-bold" colspan="7" height="20%">
								<bean:message bundle="servermgrResources"
									key="servermgr.configureservices" />
							</td>
						</tr>
						<tr>
							<td align="left" class="tblheader" valign="top" width="5%">
								<bean:message bundle="servermgrResources"
									key="servermgr.serialnumber" />
							</td>
							<td align="left" class="tblheader" valign="top" width="25%">
								<bean:message bundle="servermgrResources"
									key="servermgr.instancename" />
							</td>
							<td align="left" class="tblheader" valign="top" width="20%">
								<bean:message bundle="servermgrResources"
									key="servermgr.serviceip" /> -
							    <bean:message bundle="servermgrResources"
									key="servermgr.serviceport" />
							</td>
							<td align="left" class="tblheader" valign="top" width="15%">
								<bean:message bundle="servermgrResources"
									key="servermgr.server.serverstartupdatetime" />
							</td>
							<td align="left" class="tblheader" valign="top" width="10%">
								<bean:message bundle="servermgrResources" key="servermgr.status" />
							</td>
							<td align="left" class="tblheader" valign="top" width="20%">
								<bean:message bundle="servermgrResources" key="servermgr.remark" />
							</td>
						</tr>

						<bean:define id="listServiceDetails" name="eliteLiveServerDetails"
							property="serviceSummaryList"></bean:define>
						<%
						int iIndex = 0;
						String remarks=null;
						%>
						<logic:iterate id="serviceData" name="listServiceDetails"
							type="com.elitecore.core.services.data.LiveServiceSummary">
							<%
							            LiveServiceSummary liveServiceSummary = (LiveServiceSummary) lstServiceDetails.get(iIndex);
							            Date startupTime = liveServiceSummary.getServiceStartupTime();
							            String ipAddress = liveServiceSummary.getSocketAddress();
							            remarks = liveServiceSummary.getRemarks();
							            if(remarks==null){
							            	remarks = "-";
							            }
							            		            
							%>
							<%
							iIndex++;
							%>
							<tr>
								<td align="left" class="tblfirstcol" valign="top">
									<%=iIndex%>
								</td>
								<td align="left" class="tblrows" valign="top">
									<bean:write name="serviceData" property="instanceName" />
									&nbsp;
								</td>
								<td align="left" class="tblrows" valign="top">
									<%=ipAddress%> 
									&nbsp;
								</td>

								<!-- 						  <td align="left" class="tblrows"><bean:write name="serviceData" property="serviceStartupTime"/>&nbsp;</td> -->
								<td align="left" class="tblrows" valign="top">
									<%=EliteUtility.dateToString(startupTime, dateFormat)%>
								</td>
								<td align="left" valign="top" class="tblrows">
									<bean:write name="serviceData" property="status" />
									&nbsp;
								</td>
								<td align="left" class="tblrows" valign="top">
									<%=remarks%>
									&nbsp;
								</td>
							</tr>

						</logic:iterate>
						<tr>
							<td align="left" class="small-text-grey" colspan="6">Note: ** in status field represents service started with last successful configuration , verify your current configuration or contact system administrator. </td>
						</tr>
					</table>
			<tr>
				<td>
					&nbsp;
				</td>
			</tr>
			<%if(lstServerInternalTasks!=null){ %>
			<tr>

				<td valign="top" align="right">

					<table width="100%" border="0" cellpadding="0" cellspacing="0"
						height="15%">

						<tr>
							<td class="tblheader-bold" colspan="5">
								<bean:message bundle="servermgrResources"
									key="servermgr.server.internaltasks" />
							</td>
						</tr>

						<tr>
							<td align="left" class="tblheader" valign="top" width="5%">
								<bean:message bundle="servermgrResources"
									key="servermgr.serialnumber" />
							</td>
							<td align="left" class="tblheader" valign="top" width="35%">
								<bean:message bundle="servermgrResources"
									key="servermgr.taskname" />
							</td>
							<td align="left" class="tblheader" valign="top" width="20%">
								<bean:message bundle="servermgrResources"
									key="servermgr.lastexecutiontime" />
							</td>
							<td align="left" class="tblheader" valign="top" width="20%">
								<bean:message bundle="servermgrResources"
									key="servermgr.nextexecutiontime" />
							</td>
						</tr>
						<%
						iIndex = 0;
						%>
						<bean:define id="lstInternalTasks" name="eliteLiveServerDetails"
							property="internalTasks"></bean:define>
						<logic:iterate id="internalTask" name="lstInternalTasks"
							type="com.elitecore.core.commons.scheduler.EliteTaskData">

							<%
							            EliteTaskData taskData = (EliteTaskData) lstServerInternalTasks.get(iIndex);
							            Date lastTime = taskData.getLastExecutionTime();
							            Date nextTime = taskData.getNextExecutionTime();
							            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
							%>
							<%
							iIndex++;
							%>
							<tr>
								<td align="left" class="tblfirstcol">
									<%=iIndex%>
								</td>
								<td align="left" class="tblrows">
									<bean:write name="internalTask" property="taskName" />
									&nbsp;
								</td>
								<td align="left" class="tblrows">
									<%=EliteUtility.dateToString(lastTime, dateFormat)%>
								</td>
								<td align="left" class="tblcol">
									<%=EliteUtility.dateToString(nextTime, dateFormat)%>
								</td>
							</tr>
						</logic:iterate>
					</table>
				</td>
			</tr>
			<%} %>
		

<%iIndex =0; %>
<tr>
				<td>
					&nbsp;
				</td>
			</tr>

			
</logic:equal>




	
		
		<logic:notEqual name="viewNetServerLiveDetailsForm"
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
