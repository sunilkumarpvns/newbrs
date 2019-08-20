<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<bean:define id="driverInstanceData" scope="request" name="driverInstanceData" type="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"></bean:define>
<bean:define id="hssAuthDriverData" scope="request" name="hssAuthDriverData" type="com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverData"></bean:define>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
    	<td> 
    		<table width="100%" border="0" cellspacing="0" cellpadding="0">
        		<tr> 
          			<td class="tblheader-bold" height="20%" colspan="2">
          				<bean:message bundle="driverResources" key="driver.view"/>
          			</td>
        		</tr>  
        		
        		<tr>
					<td align="left" class="tblheader-bold" valign="top" colspan="2">
					<bean:message bundle="driverResources" key="driver.driverinstancedetails" /></td>
				</tr>  					
        		
        		<tr>
					<td class="tblfirstcol" width="20%" height="20%" >
						<bean:message bundle="driverResources" key="driver.instname" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="driverInstanceData" property="name"/>&nbsp;
					</td>
				</tr>   
			     
			   	<tr>
					<td class="tblfirstcol" width="20%" height="20%">
						<bean:message bundle="driverResources" key="driver.instdesc" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="driverInstanceData" property="description"/>&nbsp;
					</td> 
				</tr>
				
				<tr>
					<td align="left" class="tblheader-bold" valign="top" colspan="2">
						View   <bean:write name="driverInstanceData" property="driverTypeData.name"/>
					</td>
				</tr>  
				<tr>
					<td class="tblfirstcol" width="20%" height="20%">
						<bean:message bundle="driverResources" key="driver.hssdriver.applicationid" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="hssAuthDriverData" property="applicationid"/>&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="20%" height="20%">
						<bean:message bundle="driverResources" key="driver.hssdriver.commandcode" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="hssAuthDriverData" property="commandCode"/>&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="20%" height="20%">
						<bean:message bundle="driverResources" key="driver.hssdriver.requesttimeout" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="hssAuthDriverData" property="requesttimeout"/>&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="20%" height="20%">
						<bean:message bundle="driverResources" key="driver.hssdriver.useridentityattribute" /> 
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="hssAuthDriverData" property="userIdentityAttributes"/>&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="20%" height="20%">
						<bean:message bundle="driverResources" key="driver.hhsdriver.nooftriplets" /> 
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="hssAuthDriverData" property="noOfTriplets"/>&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="20%" height="20%">
						<bean:message bundle="driverResources" key="driver.hhsdriver.additionalattributes" /> 
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="hssAuthDriverData" property="additionalAttributes"/>&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="20%" height="20%">
						<bean:message bundle="driverResources" key="driver.hhspeerconfiguration" /> 
					</td>
					<td width="30%" height="20%" >
						<table width="100%" cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td class="tblheader-bold" width="20%" height="20%">
									PeerName 
								</td>
								<td class="tblheader-bold" width="30%" height="20%" >
									Weightage&nbsp;
								</td>
							</tr>
							<logic:iterate id="peerData" property="diameterPeerRelDataList" name="hssAuthDriverData" type="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerRelData">
							<tr>
								<td class="tblfirstcol" width="20%" height="20%">
									<logic:iterate id="peerDataList" name="diameterPeerDataList" type="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData">
										<logic:equal value="<%=peerData.getPeerUUID().toString()%>" name="peerDataList" property="peerUUID">
						            		<span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="peerDataList" property="peerUUID"/>','<%=peerDataList.getName() %>','<%=EliteViewCommonConstant.DIAMETER_PEER%>');">
						            			<%=peerDataList.getName() %>
						            		</span>
										</logic:equal>
									</logic:iterate>
								</td>
								<td class="tblcol" width="30%" height="20%" >
									<%=peerData.getWeightage()%>
								</td>
							</tr>
							</logic:iterate>
						</table>
					</td>
				</tr>
			</table> 
		</td>
	</tr>
</table>