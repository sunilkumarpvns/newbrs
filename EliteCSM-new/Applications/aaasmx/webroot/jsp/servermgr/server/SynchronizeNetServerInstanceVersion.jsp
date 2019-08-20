<%@ include file="/jsp/core/includes/common/Header.jsp"%>





<%@ page import="org.apache.struts.util.MessageResources"%>



<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<bean:define id="netServerInstanceBean" name="netServerInstanceData"
		scope="request"
		type="com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData" />
	<tr>
		<logic:equal name="synchronizeNetServerVersionForm" scope="request"
			property="netServerStatus" value="true">
			<tr>
				<td valign="top" align="right">
					<table width="100%" border="0" cellspacing="0" cellpadding="0"
						height="15%">
						<tr>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td class="tblheader-bold" colspan="2"><bean:message
									bundle="servermgrResources" key="servermgr.synchronize.version" /></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="10%"><bean:message
									bundle="servermgrResources" key="servermgr.admininterfaceip" /></td>
							<td align="left" class="labeltext" valign="top" width="32%"><bean:write
									name="netServerInstanceBean" property="adminHost" />
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="10%"><bean:message
									bundle="servermgrResources" key="servermgr.admininterfaceport" /></td>
							<td align="left" class="labeltext" valign="top" width="32%"><bean:write
									name="netServerInstanceBean" property="adminPort" />
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="10%"><bean:message
									bundle="servermgrResources"
									key="servermgr.synchronize.serverversion" /></td>
							<td align="left" class="labeltext" valign="top" width="32%"><bean:write
									name="netServerInstanceBean" property="version" />
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="10%"><bean:message
									bundle="servermgrResources"
									key="servermgr.synchronize.Liveversion" /></td>
							<td align="left" class="labeltext" valign="top" width="32%"><bean:write
									name="synchronizeNetServerVersionForm"
									property="liveServerVersion" />
						</tr>
						<tr>
							<td colspan="5" style="">&nbsp;</td>
						</tr>
					</table>
				</td>
			</tr>
			<logic:equal name="synchronizeNetServerVersionForm" scope="request"
				property="syncServerVesionStatus" value="false">
				<bean:define id="netServerInstanceBean" name="netServerInstanceData"
					scope="request"
					type="com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData" />
				<tr>
					<td valign="top" align="right">
						<table width="97%" border="0" cellspacing="0" cellpadding="0"
							height="15%">
							<tr>
								<logic:equal name="synchronizeNetServerVersionForm"
									scope="request" property="syncServerVesionStatus" value="true">
									<td id="notes" class="small-text-grey" height="25%" colspan="5">
										<div id="notediv" style="hight: 25%">
											<!-- 	&nbsp;Note:- Server Version and Live Server Version is Synchronized.  -->
											<div>
									</td>
								</logic:equal>
								<logic:equal name="synchronizeNetServerVersionForm"
									scope="request" property="syncServerVesionStatus" value="false">
									<td id="notes" class="small-text-grey" height="25%" colspan="5">
										<div id="notediv" style="hight: 25%">
											<font color="red">&nbsp;Note:- Please Synchronize
												Server Version for before attempting Synchronize
												Configuration.</font>
											<div>
									</td>
								</logic:equal>
							</tr>
							<tr>
								<td style="" colspan="5">&nbsp;</td>
							</tr>

							<html:form action="/synchronizeServerInstanceVersion.do">
								<html:hidden styleId="action" property="action" value="upgrade" />
								<html:hidden styleId="netServerInstanceId"
									property="netServerInstanceId" />
								<html:hidden styleId="upgradeServerVersion"
									property="upgradeServerVersion" />
								<tr>
									<td class="btns-td" align="left" colspan="5"><input
										type="submit" name="c_btnSynchronize" id="c_btnSynchronize"
										value=" Synchronize Version " class="light-btn"> <input
										type="reset" name="c_btnDeletePolicy"
										onclick="javascript:location.href='<%=request.getContextPath()%>/viewNetServerInstance.do?netserverid=<bean:write name="netServerInstanceBean" property="netServerId"/>'"
										value="Cancel" class="light-btn"></td>
								</tr>
							</html:form>

							<tr>
								<td style="" colspan="5">&nbsp;</td>
							</tr>
							</td>
							</tr>
						</table>
			</logic:equal>
		</logic:equal>

		<logic:equal name="synchronizeNetServerVersionForm" scope="request"
			property="netServerStatus" value="false">
			<tr>
				<td valign="top" align="right">
					<table width="100%" border="0" cellspacing="0" cellpadding="0"
						height="15%">
						<tr>
							<td colspan="5">&nbsp;</td>
						</tr>
						<tr>
							<td class="blue-text-bold" colspan="3"><bean:message
									bundle="servermgrResources" key="servermgr.connectionfailure" />
								<br> <bean:message bundle="servermgrResources"
									key="servermgr.admininterfaceip" /> :<bean:write
									name="netServerInstanceData" property="adminHost" /> <br>
								<bean:message bundle="servermgrResources"
									key="servermgr.admininterfaceport" /> : <bean:write
									name="netServerInstanceData" property="adminPort" /> &nbsp;</td>
						</tr>
						<tr>
							<td colspan="5">&nbsp;</td>
						</tr>
					</table>
				</td>
			</tr>
		</logic:equal>

	</tr>
</table>
