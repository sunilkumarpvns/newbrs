<%
	request.getContextPath(); 
%>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<bean:define id="regionBean" name="regionData" scope="request" type="com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData" />
	 <tr>
		<td valign="top" align="right"> 
			<table cellpadding="0" cellspacing="0" border="0" width="97%" >
 			<tr>
				<td class="tblheader-bold"  valign="top" colspan="2"  ><bean:message  bundle="locationMasterResources" key="region.view.title"/> </td>
			</tr>			
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="region.name" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%"> <bean:write name="regionBean" property="regionName"/>&nbsp;</td>					
				  </tr>
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="region.country.name" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%"> <bean:write name="regionBean" property="countryData.name"/>&nbsp;</td>					
				  </tr>			   	  
			</table>
		</td>
	</tr>
	<tr><td>&nbsp;</td>
	</tr>
</table>