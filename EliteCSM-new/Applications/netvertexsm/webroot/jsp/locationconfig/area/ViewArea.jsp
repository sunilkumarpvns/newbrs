<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.locationconfig.area.data.AreaData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.locationconfig.area.data.LacData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.locationconfig.area.data.CallingStationInfoData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.locationconfig.area.data.WiFiCallingStationInfoData" %>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<bean:define id="areaBean" name="areaData" scope="request" type="com.elitecore.netvertexsm.datamanager.locationconfig.area.data.AreaData" />
	
	 <tr>
		<td valign="top" align="right"> 
			<table cellpadding="0" cellspacing="0" border="0" width="97%">
			
	 			<tr>
					<td class="tblheader-bold"  valign="top" colspan="2"  ><bean:message  bundle="locationMasterResources" key="area.management.title"/> </td>
				</tr>			
		 	    <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="area.name" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%"> <bean:write name="areaBean" property="area"/>&nbsp;</td>					
			  	</tr>
		 	    <tr>		 	    	 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="city.country.name" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%">
						<logic:notEmpty name="areaBean" property="cityData.region.countryData"> 
							<bean:write name="areaBean" property="cityData.region.countryData.name"/>
						</logic:notEmpty>
						&nbsp;
					</td>					
			  	</tr>
		 	    <tr>		 	    	 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="area.region" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%">
						<logic:notEmpty name="areaBean" property="cityData.region">
							<a href="<%=basePath%>/regionManagement.do?method=view&regionId=<bean:write name="areaBean" property="cityData.region.regionId"/>" > 
								<bean:write name="areaBean" property="cityData.region.regionName"/>
							</a>
						</logic:notEmpty>
						&nbsp;
					</td>					
			  	</tr>
		 	    <tr>		 	    	 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="city.title" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%">
					<logic:notEmpty name="areaBean" property="cityData">
						<a href="<%=basePath%>/cityManagement.do?method=view&cityId=<bean:write name="areaBean" property="cityData.cityId"/>" > 
							<bean:write name="areaBean" property="cityData.cityName"/>
						</a>
					</logic:notEmpty>
					&nbsp;</td>
										
			  	</tr>
			  	
		 	    <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="area.param1" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%"> <bean:write name="areaBean" property="param1"/>&nbsp;</td>					
			  	</tr>

		 	    <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="area.param2" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%"> <bean:write name="areaBean" property="param2"/>&nbsp;</td>					
			  	</tr>
		 	    <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="area.param3" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%"> <bean:write name="areaBean" property="param3"/>&nbsp;</td>					
			  	</tr>
			  				  	
		 	    <tr>		 	    	 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="area.network" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%">
						<logic:notEmpty name="areaBean" property="networkData">
							<a href="<%=basePath%>/networkManagement.do?method=view&networkID=<bean:write name="areaBean" property="networkData.networkID"/>">											            
								<bean:write name="areaBean" property="networkData.networkName"/>
							</a>
						</logic:notEmpty>
						<logic:empty name="areaBean" property="networkData">-</logic:empty>						 						
						&nbsp;
					</td>					
			  	</tr>
			  					  	
	 			<tr>
					<td class="tblheader-bold"  valign="top" colspan="2"  ><bean:message  bundle="locationMasterResources" key="area.3gpp.lcac.info"/> </td>
				</tr>			  			  			  					  			  			  					  			  			  					  			  

				<tr>
					<td colspan="2" >
					<table cellpadding="0" cellspacing="0" border="0" width="100%" >
			        <logic:notEmpty name="areaBean" property="lacDataSet">
						<tr class="L">
							<td align="center" class="tblheaderfirstcol" valign="top" width="15%"><bean:message bundle="locationMasterResources" key="area.lacs" /></td>
							<td align="center" class="tblheader" valign="top" width="25%"><bean:message bundle="locationMasterResources" key="area.cis" />	</td>
							<td align="center" class="tblheader" valign="top" width="25%"><bean:message bundle="locationMasterResources" key="area.sacs" />	</td>
							<td align="center" class="tblheader" valign="top" width="25%"><bean:message bundle="locationMasterResources" key="area.racs" />	</td>							
						</tr>					
						<logic:iterate  id="lacDataBean" name="areaBean" property="lacDataSet" type="com.elitecore.netvertexsm.datamanager.locationconfig.area.data.LacData" >
						<tr>
							<td align="center" class="tblfirstcol" valign="top" width="15%"><bean:write name="lacDataBean" property="lacCode"/>&nbsp;</td>
							<td align="center" class="tblrows" valign="top" width="25%"><bean:write name="lacDataBean" property="strCellIds"/>&nbsp;</td>
							<td align="center" class="tblrows" valign="top" width="25%"><bean:write name="lacDataBean" property="strSacs"/>&nbsp;</td>
							<td align="center" class="tblrows" valign="top" width="25%"><bean:write name="lacDataBean" property="strRacs"/>&nbsp;</td>												
						</tr>
						</logic:iterate>
					</logic:notEmpty>
					<logic:empty name="areaBean" property="lacDataSet">	
						<tr>
							<td align="center" class="tblfirstcol" colspan="4" ><bean:message bundle="datasourceResources" key="database.datasource.norecordfound"/></td>
						</tr>						
					</logic:empty>
					</table>
					</td>
				</tr>
	 			<tr>
					<td class="tblheader-bold"  valign="top" colspan="2"  ><bean:message  bundle="locationMasterResources" key="area.wifi.ap.info"/> </td>
				</tr>					
				<tr>		 	    	 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="area.called.staion.ids" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%">
						<logic:notEmpty name="areaBean" property="callingStationInfoData">
							<bean:write name="areaBean" property="callingStationInfoData.callingStaionIds"/>
						</logic:notEmpty>
						<logic:empty name="areaBean" property="callingStationInfoData.callingStaionIds">-</logic:empty> 
						&nbsp;
					</td>					
			  	</tr>
		 	    <tr>		 	    	 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="area.wifi.ap.ssids" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%">
						<logic:notEmpty name="areaBean" property="wifiCallingStationInfoData"> 
							<bean:write name="areaBean" property="wifiCallingStationInfoData.ssids"/>
						</logic:notEmpty>
						<logic:empty name="areaBean" property="wifiCallingStationInfoData.ssids">-</logic:empty>
						&nbsp;
					</td>					
			  	</tr>			  	
		  	</table>
		</td>
	</tr>		
</table>