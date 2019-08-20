<bean:define id="driverInstanceData" scope="request" name="driverInstanceData" type="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"></bean:define>
<bean:define id="mappingGatewayAuthDriverData"  scope="request" name="mappingGatewayAuthDriverData"  type="com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.MappingGatewayAuthDriverData"></bean:define>
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
						View  <bean:write name="driverInstanceData" property="driverTypeData.name"/>
					</td>
				</tr>   
				
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.localhostid" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="mappingGatewayAuthDriverData" property="localHostId"/>&nbsp;
					</td>
				</tr>
						
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.localhostport" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="mappingGatewayAuthDriverData" property="localHostPort"/></td>
				</tr>
						
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.localhostip" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="mappingGatewayAuthDriverData" property="localHostIp"/>&nbsp;
					</td>
				</tr>
						
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.remotehostid" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="mappingGatewayAuthDriverData" property="remoteHostId"/>&nbsp;
					</td>
				</tr>
						
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.remotehostport" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="mappingGatewayAuthDriverData" property="remoteHostPort"/>&nbsp;
					</td>
				</tr>
						
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.remotehostip" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="mappingGatewayAuthDriverData" property="remoteHostIp"/>&nbsp;
					</td>
				</tr>
								
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.querytimeoutcount" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="mappingGatewayAuthDriverData" property="maxQueryTimeoutCount"/>&nbsp;
					</td>
				</tr>
						
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.mapgwconnpoolsize" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="mappingGatewayAuthDriverData" property="mapGwConnPoolSize"/>&nbsp;
					</td>
				</tr>
						
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.requesttimeout" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="mappingGatewayAuthDriverData" property="requestTimeout"/>&nbsp;
					</td>
				</tr>
						
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.mapgw.statuscheckduration" />
					</td>
					
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="mappingGatewayAuthDriverData" property="statusCheckDuration"/>&nbsp;
					</td>
				</tr>
				
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.useridentityattributes" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="mappingGatewayAuthDriverData" property="userIdentityAttributes"/>&nbsp;
					</td>
				</tr>
				
				<tr>
					<td align="left" class="tblheader-bold" valign="top" colspan="2">
						<bean:message bundle="driverResources" key="driver.mapgsm" />
					</td>
				</tr> 
				
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.mapgw.sendauthinfo" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<logic:equal name="mappingGatewayAuthDriverData" property="sendAuthInfo" value="TRUE">True</logic:equal>  
						<logic:notEqual name="mappingGatewayAuthDriverData" property="sendAuthInfo" value="TRUE">False</logic:notEqual>
					</td>
				</tr>
						
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.mapgw.numberoftriplets" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="mappingGatewayAuthDriverData" property="numberOfTriplets"/>&nbsp;
					</td>
				</tr>
				
				<tr>
					<td align="left" class="tblheader-bold" valign="top" colspan="2">
						<bean:message bundle="driverResources" key="driver.mapprofilefield" />
					</td>
				</tr> 
				<tr>
					<td align="left"  valign="top" colspan="2">
						<table width="100%" id="mappingtbl" cellpadding="0" cellspacing="0" border="0">
							<tr>								
								<td align="left" class="tblheader" valign="top">
									Logical Name
								</td>								
								<td align="left" class="tblheader" valign="top">
									Profile Field
								</td>
								<td align="left" class="tblheader" valign="top">
									<bean:message bundle="driverResources" key="driver.webserviceauthdriver.defaultvalue" />
								</td>
								<td align="left" class="tblheader" valign="top" >
										<bean:message bundle="driverResources" key="driver.webserviceauthdriver.valuemapping" />
								</td>  	  	
							</tr>
												
							<logic:iterate id="obj" name="mappingGatewayAuthDriverData" property="gatewayFieldList" type="com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.GatewayFieldMapData">
								<tr>
									<td class="tblfirstcol">
										<bean:write name="obj" property="nameValuePoolData.name"/>&nbsp; 
									</td>
									<td class="tblrows"> 
										<bean:write name="obj" property="profileField"/>&nbsp;
									</td>
									<td class="tblrows">
										<bean:write name="obj" property="defaultValue"/>&nbsp;
									</td>
									<td class="tblrows">
										<bean:write name="obj" property="valueMapping"/>&nbsp;
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