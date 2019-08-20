<table width="100%" border="0" cellspacing="0" cellpadding="0">
  					<tr> 
    						<td> 
    						  <table width="100%" border="0" cellspacing="0" cellpadding="0">
        						<tr> 
          						<td class="tblheader-bold" height="20%" colspan="2">
          						<bean:message bundle="driverResources" key="driver.view"/></td>
        						</tr>  
        						 <tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="2">
							<bean:message bundle="driverResources" key="driver.driverinstancedetails" /></td>
						 </tr>  					
        						 <tr>
							<td class="tblfirstcol" width="20%" height="20%" >
							<bean:message bundle="driverResources" key="driver.instname" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateRadiusMappingGWAuthDriverForm" property="driverInstanceName"/></td>
						 </tr>   
			    
			   			 <tr>
							<td class="tblfirstcol" width="20%" height="20%">
							<bean:message bundle="driverResources" key="driver.instdesc" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateRadiusMappingGWAuthDriverForm" property="driverDesp"/></td>
												
						</tr>
				
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="2">
							<bean:message bundle="driverResources" key="driver.driverdetails" /></td>
						</tr>   
			       
				
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.localhostid" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateRadiusMappingGWAuthDriverForm" property="localHostId"/></td>
						</tr>
						
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.localhostport" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateRadiusMappingGWAuthDriverForm" property="localHostPort"/></td>
						</tr>
						
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.localhostip" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateRadiusMappingGWAuthDriverForm" property="localHostIp"/></td>
						</tr>
						
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.remotehostid" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateRadiusMappingGWAuthDriverForm" property="remoteHostId"/></td>
						</tr>
						
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.remotehostport" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateRadiusMappingGWAuthDriverForm" property="remoteHostPort"/></td>
						</tr>
						
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.remotehostip" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateRadiusMappingGWAuthDriverForm" property="remoteHostIp"/></td>
						</tr>
								
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.querytimeoutcount" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateRadiusMappingGWAuthDriverForm" property="maxQueryTimeoutCount"/></td>
						</tr>
						
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.mapgwconnpoolsize" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateRadiusMappingGWAuthDriverForm" property="mapGwConnPoolSize"/></td>
						</tr>
						
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.requesttimeout" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateRadiusMappingGWAuthDriverForm" property="requestTimeout"/></td>
						</tr>
						
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.mapgw.statuscheckduration" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateRadiusMappingGWAuthDriverForm" property="statusCheckDuration"/></td>
						</tr>
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.useridentityattributes" />
						    </td>
							<td class="tblcol" width="30%" height="20%" >
							   <bean:write name="updateRadiusMappingGWAuthDriverForm" property="userIdentityAttributes"/>&nbsp;
							</td>
						</tr>
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="3">
								<bean:message bundle="driverResources" key="driver.mapgsm" />
							</td>					
						</tr>
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.mapgw.sendauthinfo" />
						    </td>
							<td class="tblcol" width="30%" height="20%" >
								<logic:equal name="updateRadiusMappingGWAuthDriverForm" property="sendAuthInfo" value="TRUE">True</logic:equal>  
							   	<logic:notEqual name="updateRadiusMappingGWAuthDriverForm" property="sendAuthInfo" value="TRUE">False</logic:notEqual>
							</td>
						</tr>
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.mapgw.numberoftriplets" />
						    </td>
							<td class="tblcol" width="30%" height="20%" >
							   <bean:write name="updateRadiusMappingGWAuthDriverForm" property="numberOfTriplets"/>&nbsp;
							</td>
						</tr>								
				</table>
		</td>
	</tr>
</table>