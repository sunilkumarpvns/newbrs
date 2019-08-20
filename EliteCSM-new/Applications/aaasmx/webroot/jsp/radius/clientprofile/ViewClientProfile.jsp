<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>








 
<table width="100%" border="0" cellspacing="0" cellpadding="0">
   
    <bean:define id="radiusClientProfileBean" name="radiusClientProfileData" scope="request" type="com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData" />
    <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="radiusResources" key="radius.clientprofile.viewinformation"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="31%" height="20%"><bean:message  bundle="radiusResources" key="radius.clientprofile.clientprofilename" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="radiusClientProfileBean" property="profileName"/></td>
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
            <td class="tblfirstcol" width="31%" height="20%"><bean:message bundle="radiusResources" key="radius.clientprofile.dnslist" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="radiusClientProfileBean" property="dnsList"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tblfirstcol" width="31%" height="20%"><bean:message bundle="radiusResources" key="radius.clientprofile.useridentity" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="radiusClientProfileBean" property="userIdentities"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tblfirstcol" width="31%" height="20%"><bean:message bundle="radiusResources" key="radius.clientprofile.prepaidstandard" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="radiusClientProfileBean" property="prepaidStandard"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tblfirstcol" width="31%" height="20%"><bean:message bundle="radiusResources" key="radius.clientprofile.clientpolicy" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="radiusClientProfileBean" property="clientPolicy"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tblfirstcol" width="31%" height="20%"><bean:message bundle="radiusResources" key="radius.clientprofile.hotlinepolicy" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="radiusClientProfileBean" property="hotlinePolicy"/>&nbsp;</td>
          </tr>
          
          <%-- tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="radiusResources" key="radius.clientprofile.framedpool" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="radiusClientProfileBean" property="framedPool"/>&nbsp;</td>
          </tr--%>
          
          <tr> 
            <td class="tblfirstcol" width="31%" height="20%"><bean:message bundle="radiusResources" key="radius.clientprofile.dhcpaddress" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="radiusClientProfileBean" property="dhcpAddress"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tblfirstcol" width="31%" height="20%"><bean:message bundle="radiusResources" key="radius.clientprofile.haaddress" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="radiusClientProfileBean" property="haAddress"/>&nbsp;</td>
          </tr>
          <tr>
			<td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="radiusResources" key="radius.clientprofile.supportedvendorlist" /></td>
		  </tr>
		 
		 <logic:iterate id="vendordata" name="supportedVendorLstBean" type="com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData">
			<tr>
			  <td class="tblcol" width="70%" height="20%"><bean:write name="vendordata" property="vendorName"/>&nbsp;</td>
			</tr>
		</logic:iterate>
			

		</table>
		</td>
    </tr>
</table>
