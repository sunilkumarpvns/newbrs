<%
	request.getContextPath(); 
%>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<bean:define id="cityBean" name="cityData" scope="request" type="com.elitecore.netvertexsm.datamanager.locationconfig.city.data.CityData" />
	 <tr>
		<td valign="top" align="right"> 
			<table cellpadding="0" cellspacing="0" border="0" width="97%">
 			<tr>
				<td class="tblheader-bold"  valign="top" colspan="2"  ><bean:message  bundle="locationMasterResources" key="city.view.title"/> </td>
			</tr>			
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="city.name" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%"> <bean:write name="cityBean" property="cityName"/>&nbsp;</td>					
				  </tr>
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="region.name" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%"> 
					<a href="<%=basePath%>/regionMgmt.do?method=view&regionId=<bean:write name="cityBean" property="region.regionId"/>" tabindex="7">
					<bean:write name="cityBean" property="region.regionName" />
					</a>&nbsp;	&nbsp;
					</td>					
				  </tr>
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="region.country.name" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%"> <bean:write name="cityBean" property="region.countryData.name"/>&nbsp;</td>					
				  </tr>
			     <tr><td>&nbsp;</td></tr>
			</table>
		</td>
	</tr>
	<tr><td>&nbsp;</td>
	</tr>
</table>