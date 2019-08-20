
<%@page	import="com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData"%>
<%@page import="com.elitecore.elitesm.util.constants.BaseConstant"%>





<%@ page import="com.elitecore.elitesm.web.servermgr.server.forms.ViewNetServerLiveClientsDetailsForm"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="java.text.SimpleDateFormat"%>
<script type="text/javascript">

	function popupClientProfile(val) {	
		
		var elementid = "#"+val;
		document.getElementById(val).style.visibility = "visible";		
		$( elementid ).dialog({
			modal: false,
			autoOpen: false,		
			height: "auto",
			width: 500
		});	
		$(elementid).dialog("open");
		window.pageYOffset=position;
	}

</script>
<%
    ViewNetServerLiveClientsDetailsForm netServerLiveClientsDetailsForm = (ViewNetServerLiveClientsDetailsForm)request.getAttribute("viewNetServerLiveClientsDetailsForm");
	String dateFormat = ConfigManager.get(ConfigConstant.DATE_FORMAT);
	List activeClientList = (List)request.getAttribute("activeClientList");
	List unsupportedVendorClientList = (List)request.getAttribute("unsupportedVendorClientList");
	List licenseExceededClientList = (List)request.getAttribute("licenseExceededClientList");
	NetServerInstanceData serverInstanceData = (NetServerInstanceData)request.getAttribute("netServerInstanceData");
	
%>

<html:form action="/viewNetServerLiveClientsDetails">
	<html:hidden name="viewNetServerLiveClientsDetailsForm" styleId="netServerId" property="netServerId" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right">

    	<tr>
			<td>
				&nbsp;
			</td>
		</tr>

		<logic:equal name="viewNetServerLiveClientsDetailsForm" property="errorCode" value="0">
			<tr>
				<td valign="top" align="right">
					<table width="100%" border="0" cellpadding="0" cellspacing="0"
						height="15%">
						<tr>
							<td class="tblheader-bold" colspan="5" height="20%">
								<bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver.clients.active" />
							</td>
						</tr>
						<tr>
							<td align="left" class="tblheader" valign="top" width="5%">
								<bean:message bundle="servermgrResources" key="servermgr.serialnumber" />
							</td>
							<td align="left" class="tblheader" valign="top" width="15%">
								<bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver.clients.clientip" />
							</td>
								<td align="left" class="tblheader" valign="top" width="15%">
								<bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver.clients.sharedsecret" />
							</td>
							<td align="left" class="tblheader" valign="top" width="15%">
								<bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver.clients.reqexptime" />
							</td>
							<td align="left" class="tblheader" valign="top" width="15%">
								<bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver.clients.profilename" />
							</td>
						</tr>
						<%
						int iIndex = 0;
						if(activeClientList!=null && activeClientList.size()>0){
						%>

						<logic:iterate id="clientData" name="activeClientList" type="com.elitecore.elitesm.web.servermgr.server.forms.ClientDetailBean">
							<%
							iIndex++;
							%>
							<tr>
								<td align="left" class="tblfirstcol">
									<%=iIndex%>
								</td>
								<td align="left" class="tblrows">
									<bean:write name="clientData" property="clientIP" />
									&nbsp;
								</td>

								<td align="left" class="tblrows">
									<bean:write name="clientData" property="sharedSecret" />
									&nbsp;
								</td>
								<td align="left" class="tblrows">
									<bean:write name="clientData" property="requestExpiryTime" />
									&nbsp;
								</td>
								<td align="left" class="tblrows">
									<% String divName = "activeClient"+ iIndex; %>
									<a style="cursor: pointer;"   onclick="popupClientProfile('<%=divName%>')"><bean:write name="clientData" property="clientProfileData.profileName" /></a>
									&nbsp;
									<div id="<%=divName%>" style="display: none;" title="Client Profile Detail">
									<table width="100%" class="box" cellpadding="0" cellspacing="0" border="0">
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.clientprofilename" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.profileName"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.description" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.description"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.useridentity" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.userIdentities"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.prepaidstandard" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.prepaidStandard"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.dnslist" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.dnsList"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.clientpolicy" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.clientPolicy"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.hotlinepolicy" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.hotlinePolicy"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.dhcpaddress" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.dhcpAddress"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.haaddress" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.haAddress"/>&nbsp;</td>
										</tr>
									</table>
									</div>
								</td>
							</tr>


						</logic:iterate>
						<%}else{ %>
						<tr>
							<td align="center" class="tblfirstcol" colspan="9">

								No Records
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
			<%
			iIndex = 0;
		    if(unsupportedVendorClientList!=null && unsupportedVendorClientList.size()>0){
			%>

			<tr>
				<td valign="top" align="right">
					<table width="100%" border="0" cellpadding="0" cellspacing="0"
						height="15%">
						<tr>
							<td class="tblheader-bold" colspan="9" height="20%">
								<bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver.clients.unsupported" />
							</td>
						</tr>
						<tr>
							<td align="left" class="tblheader" valign="top" width="5%">
								<bean:message bundle="servermgrResources" key="servermgr.serialnumber" />
							</td>
							<td align="left" class="tblheader" valign="top" width="15%">
								<bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver.clients.clientip" />
							</td>
								<td align="left" class="tblheader" valign="top" width="15%">
								<bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver.clients.sharedsecret" />
							</td>
							<td align="left" class="tblheader" valign="top" width="15%">
								<bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver.clients.reqexptime" />
							</td>
							<td align="left" class="tblheader" valign="top" width="15%">
								<bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver.clients.profilename" />
							</td>
						</tr>

						<logic:iterate id="clientData" name="unsupportedVendorClientList" type="com.elitecore.elitesm.web.servermgr.server.forms.ClientDetailBean">
							<%
							iIndex++;
							%>
							<tr>
								<td align="left" class="tblfirstcol">
									<%=iIndex%>
								</td>
								<td align="left" class="tblrows">
									<bean:write name="clientData" property="clientIP" />
									&nbsp;
								</td>

								<td align="left" class="tblrows">
									<bean:write name="clientData" property="sharedSecret" />
									&nbsp;
								</td>
								<td align="left" class="tblrows">
									<bean:write name="clientData" property="requestExpiryTime" />
									&nbsp;
								</td>
								<td align="left" class="tblrows">
									<% String divName = "unsupportedVendorClient"+ iIndex; %>
									<a style="cursor: pointer;"   onclick="popupClientProfile('<%=divName%>')"><bean:write name="clientData" property="clientProfileData.profileName" /></a>
									&nbsp;
									<div id="<%=divName%>" style="display: none;" title="Client Profile Detail">
									<table width="100%" class="box" cellpadding="0" cellspacing="0" border="0">
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.clientprofilename" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.profileName"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.description" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.description"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.useridentity" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.userIdentities"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.prepaidstandard" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.prepaidStandard"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.dnslist" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.dnsList"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.clientpolicy" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.clientPolicy"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.hotlinepolicy" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.hotlinePolicy"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.dhcpaddress" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.dhcpAddress"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.haaddress" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.haAddress"/>&nbsp;</td>
										</tr>
									</table>
									</div>
								</td>

							</tr>

						</logic:iterate>
						
						
						
				
						
					</table>
				</td>
			</tr>
			<%}%>
			<tr>
				<td>
					&nbsp;
				</td>
			</tr>
			<%
			 iIndex = 0;
			 if(licenseExceededClientList!=null && licenseExceededClientList.size()>0){
			%>


			<tr>
				<td valign="top" align="right">
					<table width="100%" border="0" cellpadding="0" cellspacing="0"
						height="15%">
						<tr>
							<td class="tblheader-bold" colspan="9" height="20%">
								<bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver.clients.licenseexcedded" />
							</td>
						</tr>
						<tr>
							<td align="left" class="tblheader" valign="top" width="5%">
								<bean:message bundle="servermgrResources" key="servermgr.serialnumber" />
							</td>
							<td align="left" class="tblheader" valign="top" width="15%">
								<bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver.clients.clientip" />
							</td>
								<td align="left" class="tblheader" valign="top" width="15%">
								<bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver.clients.sharedsecret" />
							</td>
							<td align="left" class="tblheader" valign="top" width="15%">
								<bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver.clients.reqexptime" />
							</td>
							<td align="left" class="tblheader" valign="top" width="15%">
								<bean:message bundle="servermgrResources" key="servermgr.viewlivenetserver.clients.profilename" />
							</td>
						</tr>
						<tr>
						<logic:iterate id="clientData" name="licenseExceededClientList"
							type="com.elitecore.elitesm.web.servermgr.server.forms.ClientDetailBean">
							<%
							iIndex++;
							%>
								<td align="left" class="tblfirstcol">
									<%=iIndex%>
								</td>
								<td align="left" class="tblrows">
									<bean:write name="clientData" property="clientIP" />
									&nbsp;
								</td>

								<td align="left" class="tblrows">
									<bean:write name="clientData" property="sharedSecret" />
									&nbsp;
								</td>
								<td align="left" class="tblrows">
									<bean:write name="clientData" property="requestExpiryTime" />
									&nbsp;
								</td>
								<td align="left" class="tblrows">
									<% String divName = "licenseExceededClient"+ iIndex; %>
									<a style="cursor: pointer;"   onclick="popupClientProfile('<%=divName%>')"><bean:write name="clientData" property="clientProfileData.profileName" /></a>
									&nbsp;
									<div id="<%=divName%>" style="display: none;" title="Client Profile Detail">
									<table width="100%" class="box" cellpadding="0" cellspacing="0" border="0">
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.clientprofilename" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.profileName"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.description" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.description"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.useridentity" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.userIdentities"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.prepaidstandard" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.prepaidStandard"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.dnslist" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.dnsList"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.clientpolicy" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.clientPolicy"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.hotlinepolicy" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.hotlinePolicy"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.dhcpaddress" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.dhcpAddress"/>&nbsp;</td>
										</tr>
										<tr>
											<td class="tblfirstcol"><bean:message bundle="radiusResources" key="radius.clientprofile.haaddress" /></td>
											<td class="tblrows"><bean:write name="clientData" property="clientProfileData.haAddress"/>&nbsp;</td>
										</tr>
									</table>
									</div>
								</td>

							</tr>

						</logic:iterate>
						
						 
					</table>
				</td>
			</tr>
			<%}%>
			<tr>
				<td>
					&nbsp;
				</td>
			</tr>
			<%iIndex = 0; %>
			<tr>
				<td>
					&nbsp;
				</td>
			</tr>


		</logic:equal>

		<logic:notEqual name="viewNetServerLiveClientsDetailsForm"
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
