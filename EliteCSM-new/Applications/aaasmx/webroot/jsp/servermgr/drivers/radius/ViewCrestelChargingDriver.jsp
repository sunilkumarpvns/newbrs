 <%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
	<tr> 
    	<td class="tblheader-bold" height="20%" colspan="2"><bean:message bundle="driverResources" key="driver.view"/></td>
    </tr>
      
    <tr>
		<td align="left" class="tblheader-bold" valign="top" colspan="2">
			<bean:message bundle="driverResources" key="driver.driverinstancedetails" />
		</td>
	</tr>
	  					
    <tr>
		<td class="tblfirstcol" width="20%" height="20%" >
			<bean:message bundle="driverResources" key="driver.instname" />
		</td>
		<td class="tblcol" width="30%" height="20%" ><bean:write name="updateCrestelChargingDriverForm" property="driverInstanceName"/>
		</td>
	</tr>
	   
	<tr>
		<td class="tblfirstcol" width="20%" height="20%">
			<bean:message bundle="driverResources" key="driver.instdesc" />
		</td>
		<td class="tblcol" width="30%" height="20%" >
			<bean:write name="updateCrestelChargingDriverForm" property="driverInstanceDesp"/>&nbsp;
		</td>												
	</tr>
						
	<tr>
		<td align="left" class="tblheader-bold" valign="top" colspan="2">
			<bean:message bundle="driverResources" key="driver.driverdetails" />
		</td>
	</tr>
								 
	<tr>
		<td class="tblfirstcol" width="20%" height="20%">
			<bean:message bundle="driverResources" key="driver.translation.crestelcharging.transmapconf" />
		</td>
		<td class="tblcol" width="30%" height="20%" >
			<logic:notEmpty name="updateCrestelChargingDriverForm" property="selectedTranslationMappingConfData.name">
			    <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="updateCrestelChargingDriverForm" property="translationMapConfigId"/>','<bean:write name="updateCrestelChargingDriverForm" property="selectedTranslationMappingConfData.name"/>','<%=EliteViewCommonConstant.TRANSLATION_MAPPING%>');">
			    	<bean:write name="updateCrestelChargingDriverForm" property="selectedTranslationMappingConfData.name"/>
			    </span>
			</logic:notEmpty>
		</td>
	</tr>
								
	<tr>
		<td class="tblfirstcol" width="20%" height="20%">
			<bean:message bundle="driverResources" key="driver.crestel.charging.noofinstance" />
		</td>
		<td class="tblcol" width="30%" height="20%" >
			<bean:write name="updateCrestelChargingDriverForm" property="instanceNumber" />&nbsp;
		</td>
	</tr>
	
</table>