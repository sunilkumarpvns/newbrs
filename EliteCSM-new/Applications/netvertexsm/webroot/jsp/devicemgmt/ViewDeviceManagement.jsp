<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<bean:define id="tacDetailBean" name="tacDetailData" scope="request" type="com.elitecore.netvertexsm.datamanager.devicemgmt.data.TACDetailData" />

    <tr>
		<td valign="top" align="right"> 
			<table cellpadding="0" cellspacing="0" border="0" width="97%">
			 	  <tr>
					<td class="tblheader-bold" colspan="2"   valign="top" ><bean:message  bundle="deviceMgmtResources" key="devicemgmt.view.title"/> </td>
				  </tr>			
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="deviceMgmtResources" key="devicemgmt.tac" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%"> <bean:write name="tacDetailBean" property="tac"/>&nbsp;</td> 
				  </tr>
			   	 			
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="deviceMgmtResources" key="devicemgmt.brand" /></td> 
					<td align="left" class="tblrows" valign="top" > <bean:write name="tacDetailBean" property="brand"/>&nbsp;</td> 
				  </tr>
	
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="deviceMgmtResources" key="devicemgmt.model" /></td> 
					<td align="left" class="tblrows" valign="top"> <bean:write name="tacDetailBean" property="model"/>&nbsp;</td>
				  </tr>
				  
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="deviceMgmtResources" key="devicemgmt.hardwaretype" /></td> 
					<td align="left" class="tblrows" valign="top"> <bean:write name="tacDetailBean" property="hardwareType"/>&nbsp;</td> 
				  </tr>
				  	
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="deviceMgmtResources" key="devicemgmt.operatingsystem" /></td> 
					<td align="left" class="tblrows" valign="top"> <bean:write name="tacDetailBean" property="operatingSystem"/>&nbsp;</td>  
				  </tr>	

				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="deviceMgmtResources" key="devicemgmt.year" /></td> 
					<td align="left" class="tblrows" valign="top"> <bean:write name="tacDetailBean" property="year"/>&nbsp;</td>				  
				  </tr>	
				  
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="deviceMgmtResources" key="devicemgmt.additionalinfo" /></td> 
					<td align="left" class="tblrows" valign="top"> <bean:write name="tacDetailBean" property="additionalInfo"/>&nbsp;</td>				  
				  </tr>	
				  <tr>
					<td class="tbllabelcol" width="20%" height="20%" ><bean:message key="general.createddate" /></td>
					<td class="tblcol" width="30%" height="20%" ><%=EliteUtility.dateToString(tacDetailBean.getCreatedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>					
				</tr>
				<tr>
					<td class="tbllabelcol" width="20%" height="20%" ><bean:message key="general.lastmodifieddate" /></td>
					<td class="tblcol" width="30%" height="20%" ><%=EliteUtility.dateToString(tacDetailBean.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>					
				</tr>	
			</table>
		</td>
	</tr>
</table>