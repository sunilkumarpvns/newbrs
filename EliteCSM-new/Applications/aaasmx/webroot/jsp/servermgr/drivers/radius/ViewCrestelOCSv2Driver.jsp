 <%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<bean:define id="driverInstanceData" scope="request" name="driverInstanceData" type="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"></bean:define>
<bean:define id="crestelChargingDriverData" scope="request" name="crestelChargingDriverData" type=" com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data.CrestelChargingDriverData"></bean:define>
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
					<td class="tblfirstcol" width="20%" height="20%" >
						<bean:message bundle="driverResources" key="driver.translation.crestelcharging.transmapconf"/>
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<logic:notEmpty name="crestelChargingDriverData" property="translationMappingConfData.name">
						    <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="crestelChargingDriverData" property="transMapConfId"/>','<bean:write name="crestelChargingDriverData" property="translationMappingConfData.name"/>','<%=EliteViewCommonConstant.TRANSLATION_MAPPING%>');">
						    	<bean:write name="crestelChargingDriverData" property="translationMappingConfData.name"/>
						    </span>
						</logic:notEmpty>
					</td>
				</tr>   
			     
			   	<tr>
					<td class="tblfirstcol" width="20%" height="20%">
						<bean:message bundle="driverResources" key="driver.crestel.charging.noofinstance" />
					</td>
					<td class="tblcol" width="30%" height="20%" >
						<bean:write name="crestelChargingDriverData" property="instanceNumber"/>&nbsp;
					</td>
				</tr> 
				
				<tr>
					<td align="left" class="tblheader-bold" valign="top" colspan="4">
					<bean:message bundle="driverResources" key="driver.translation.crestelcharging.jndiprops" /></td>
				</tr>
	
				<tr>
					<td align="left"  valign="top" colspan="2">
						<table width="100%" id="mappingtbl" cellpadding="0" cellspacing="0" border="0">
							<tr>								
								<td align="left" class="tblheader" valign="top">
									<bean:message bundle="driverResources" key="driver.translation.rating.property" />
								</td>								
								<td align="left" class="tblheader" valign="top">
									<bean:message bundle="driverResources" key="driver.translation.rating.value" />
								</td>
							</tr>
												
							<logic:iterate id="obj" name="crestelChargingDriverData" property="jndiPropValMapList" type="  com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data.CrestelChargingDriverPropsData">
									<tr>
									<td class="tblfirstcol">
										<bean:write name="obj" property="name"/>&nbsp; 
									</td>
									<td class="tblrows"> 
										<bean:write name="obj" property="value"/>&nbsp;
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