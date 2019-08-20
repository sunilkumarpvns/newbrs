<bean:define id="driverInstanceData" scope="request" name="driverInstanceData" type="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"></bean:define>
<bean:define id="httpAuthDriverData"  scope="request" name="httpAuthDriverData"  type=" com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverData"></bean:define>
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
						<bean:message bundle="driverResources" key="driver.httpurl" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="httpAuthDriverData" property="http_url"/>&nbsp;
					</td>
				</tr>
						
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.statuscheckduration" /><bean:message key="general.seconds" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="httpAuthDriverData" property="statusCheckDuration"/></td>
				</tr>
						
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.querytimeoutcount" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="httpAuthDriverData" property="maxQueryTimeoutCount"/>&nbsp;
					</td>
				</tr>
						
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.expirydatepatterns" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="httpAuthDriverData" property="expiryDateFormat"/>&nbsp;
					</td>
				</tr>
						
				<tr>
					<td align="left" class="tblfirstcol" valign="top">
						<bean:message bundle="driverResources" key="driver.useridentityattributes" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="httpAuthDriverData" property="userIdentityAttributes"/>&nbsp;
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
									Response Parameter Index 
								</td>
								<td align="left" class="tblheader" valign="top">
									<bean:message bundle="driverResources" key="driver.webserviceauthdriver.defaultvalue" />
								</td>
								<td align="left" class="tblheader" valign="top" >
										<bean:message bundle="driverResources" key="driver.webserviceauthdriver.valuemapping" />
								</td>  	  	
							</tr>
												
							<logic:iterate id="obj" name="httpAuthDriverData" property="httpFieldMapList" type=" com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverFieldMapData">
								<tr>
									<td class="tblfirstcol">
										<bean:write name="obj" property="nameValuePoolData.name"/>&nbsp; 
									</td>
									<td class="tblrows"> 
										<bean:write name="obj" property="responseParamIndex"/>&nbsp;
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