 <%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<%@ page import="java.util.List" %>
<%
	out.append("");
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<bean:define id="diameterChargingDriverBean" name="diameterChargingDriverData" scope="request" type="com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverData"/>
				<bean:define id="driverInstanceBean" name="driverInstanceData" scope="request" type="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"/>
  					<tr> 
    						<td> 
    						  <table width="100%" border="0" cellspacing="0" cellpadding="0">
        						<tr> 
          						<td class="tblheader-bold" height="20%" colspan="5">
          						<bean:message bundle="driverResources" key="driver.view"/></td>
        						</tr>  
        						 <tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="5">
							<bean:message bundle="driverResources" key="driver.driverinstancedetails" /></td>
						 </tr>  					
        				<tr>
							<td class="tblfirstcol" width="20%" height="20%" >
							<bean:message bundle="driverResources" key="driver.instname" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="driverInstanceBean" property="name"/></td>
						 </tr>   
			    
			   			 <tr>
							<td class="tblfirstcol" width="20%" height="20%">
							<bean:message bundle="driverResources" key="driver.instdesc" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="driverInstanceBean" property="description"/></td>
												
						</tr>
				
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="5">
							<bean:message bundle="driverResources" key="driver.driverdetails" /></td>
						</tr>   
						
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.dc.transmapconf" /></td>
							<td class="tblcol" width="30%" height="20%" >
								<logic:notEmpty name="diameterChargingDriverBean" property="translationMappingConfData.name">
								    <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="diameterChargingDriverBean" property="transMapConfId"/>','<bean:write name="diameterChargingDriverBean" property="translationMappingConfData.name"/>','<%=EliteViewCommonConstant.TRANSLATION_MAPPING%>');">
								    	<bean:write name="diameterChargingDriverBean" property="translationMappingConfData.name"/>
								    </span>
								</logic:notEmpty>
							</td>
						</tr>	
						
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.dc.disconnecturl" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="diameterChargingDriverBean" property="disConnectUrl"/></td>
						</tr>
			</table>
		</td>
	</tr>
</table>