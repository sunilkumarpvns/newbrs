<%@ page import="java.util.*" %>







<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
	<bean:define id="radiusClientProfileBean" name="radiusClientProfileData" scope="request" type="com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData" /> 
	<tr>
		<td align="right" valign="top"> 
			<table cellpadding="0" cellspacing="0" border="0" width="100%" >

				<tr> 
		            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="radiusResources" key="radius.clientprofile.viewinformation"/></td>
	            </tr>
	            <tr> 
	            <td class="tblfirstcol" width="31%" height="20%"><bean:message  bundle="radiusResources" key="radius.clientprofile.clientprofilename" /></td>
	            <td class="tblcol" width="70%" height="20%" ><bean:write name="radiusClientProfileBean" property="profileName"/>&nbsp;</td>
	           </tr>
	           <tr> 
	            <td class="tblfirstcol" width="31%" height="20%"><bean:message  bundle="radiusResources" key="radius.clientprofile.description" /></td>
	            <td class="tblcol" width="70%" height="20%" ><bean:write name="radiusClientProfileBean" property="description"/>&nbsp;</td>
	           </tr>
         
               <tr> 
	            <td class="tblfirstcol" width="31%" height="20%"><bean:message bundle="radiusResources" key="radius.clientprofile.clienttypename" /></td>
	            <td class="tblcol" width="70%" height="20%"><bean:write name="radiusClientProfileBean" property="clientTypeData.clientTypeName"/>&nbsp;</td>
	          </tr>
	          
	          <tr> 
	             <td class="tblfirstcol" width="31%" height="20%"><bean:message bundle="radiusResources" key="radius.clientprofile.vendorname" /></td>
	             <td class="tblcol" width="70%" height="20%"><bean:write name="radiusClientProfileBean" property="vendorData.vendorName"/>&nbsp;</td>
	          </tr>
	          <tr>
	           <td class="tblfirstcol" valign="top" width="30%" height="20%"><bean:message bundle="radiusResources" key="radius.clientprofile.supportedvendorlist" />&nbsp;</td>
	           <td class="tblcol" colspan="2">
	              <table>
	              <% int indexId=1; %> 
	              <logic:iterate id="vendordata" name="supportedVendorLstBean" type="com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData">
							<tr>
							<td align="left" class="labeltext" width="5%"><%=indexId%>.</td>
							<td class="labeltext" width="70%" height="20%"><bean:write name="vendordata" property="vendorName" />&nbsp;</td>
							</tr>
				  <%indexId++; %>			
				 </logic:iterate>
				 </table>
	           </td>
              </tr>   				
			</table>
		</td>
	</tr>
</table>
		