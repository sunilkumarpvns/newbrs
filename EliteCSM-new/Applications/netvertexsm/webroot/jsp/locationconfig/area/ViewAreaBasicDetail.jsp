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
			  					  			  				  	
			  </table>
			</td>
		</tr>		
</table>