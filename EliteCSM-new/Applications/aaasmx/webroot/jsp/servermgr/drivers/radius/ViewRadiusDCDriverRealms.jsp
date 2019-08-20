<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverRealmsData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverVendorData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverPeerData"%>
<%@page import="com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
   
   
   <bean:define id="diameterChargingDriverConfigBean" name="diameterChargingDriverData" scope="request" type="com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverData" />
  
    <tr> 
      <td valign="top" align="right" colspan="2"> 
    	 <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="driverResources" key="realmsmapconf.view"/></td>
          </tr>
          </table>
       </td>
     </tr>
     <tr>
     	<td colspan="2">
     	<%int index=1; %>
 		<logic:iterate id="realmsInstDataBean"  name="diameterChargingDriverConfigBean" property="driverRealmsRelSet" type="DiameterChargingDriverRealmsData">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader" colspan="2" height="20%">Realm-<%=index%></td>
          </tr>
          
          <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1> <bean:message bundle="driverResources" key="driver.dc.realmname"/></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="realmsInstDataBean" property="realmName"/>&nbsp;</td>
         </tr>
         <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1> <bean:message bundle="driverResources" key="driver.dc.routingaction"/></td>
            <%if(realmsInstDataBean.getRoutingAction() == 0){%>
            	<td class="tblcol" width="80%" height="20%" >Local&nbsp;</td>
            <%}%>
            <%if(realmsInstDataBean.getRoutingAction() == 1){%>
            	<td class="tblcol" width="80%" height="20%" >Relay&nbsp;</td>
            <%}%>
            <%if(realmsInstDataBean.getRoutingAction() == 2){%>
            	<td class="tblcol" width="80%" height="20%" >Proxy&nbsp;</td>
            <%}%>
            <%if(realmsInstDataBean.getRoutingAction() == 3){%>
            	<td class="tblcol" width="80%" height="20%" >Redirect&nbsp;</td>
            <%}%>
          </tr>
          <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1> <bean:message bundle="driverResources" key="driver.dc.authapplicationid"/></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="realmsInstDataBean" property="authApplicationId"/>&nbsp;</td>
          </tr>
          <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1> <bean:message bundle="driverResources" key="driver.dc.acctapplicationid"/></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="realmsInstDataBean" property="acctApplicationId"/>&nbsp;</td>
          </tr>
          <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1> <bean:message bundle="driverResources" key="driver.dc.vendorparams"/></td>
            <td class="tblcol" width="70%" height="20%" >
            	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
            		<tr>
            			<td class="tblheader" height="20%"><bean:message key="general.serialnumber"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="driverResources" key="driver.dc.vendor.vendorid"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="driverResources" key="driver.dc.vendor.authappid"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="driverResources" key="driver.dc.vendor.acctappid"/></td>
            		</tr>
            		<% int tempIndexVendor = 1; %>
            		<%if(realmsInstDataBean.getRealmVendorRelSet()!=null && !realmsInstDataBean.getRealmVendorRelSet().isEmpty()){ %>
            		<logic:iterate id="vendorMappingInstDetailDataBean"  name="realmsInstDataBean" property="realmVendorRelSet">
            		<tr>
            			<td class="tblfirstcol" height="20%"><%=tempIndexVendor%></td>
						<td align=left class=tblrows valign=top><bean:write name="vendorMappingInstDetailDataBean" property="vendorId"/>&nbsp;</td>
						<td align=left class=tblrows valign=top><bean:write name="vendorMappingInstDetailDataBean" property="authApplicationId"/>&nbsp;</td>
						<td align=left class=tblrows valign=top><bean:write name="vendorMappingInstDetailDataBean" property="acctApplicationId"/>&nbsp;</td>
            		</tr>
            		<%tempIndexVendor++; %>
            		
            		</logic:iterate>
            		<%}else{ %>
            			<tr>
				           	<td class="tblfirstcol" colspan="4" align="center" valign=top>No Records Found.</td>
				        </tr>
            		<%} %>
            	</table>
            	
            </td>
          </tr>
          <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1> <bean:message bundle="driverResources" key="driver.dc.peerparams"/></td>
            <td class="tblcol" width="70%" height="20%" >
            	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
            		<tr>
            			<td class="tblheader" height="20%"><bean:message key="general.serialnumber"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="driverResources" key="driver.dc.peer.name"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="driverResources" key="driver.dc.peer.communicationport"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="driverResources" key="driver.dc.peer.attemptconnection"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="driverResources" key="driver.dc.peer.watchdoginterval"/></td>
            		</tr>
            		<% int tempIndexPeer = 1; %>
            		<%if(realmsInstDataBean.getRealmPeerRelSet()!=null && !realmsInstDataBean.getRealmPeerRelSet().isEmpty()){ %>
            		<logic:iterate id="peerMappingInstDetailDataBean"  name="realmsInstDataBean" property="realmPeerRelSet" type="DiameterChargingDriverPeerData">
            		<tr>
            			<td class="tblfirstcol" height="20%"><%=tempIndexVendor%></td>
						<td align=left class=tblcol valign=top><bean:write name="peerMappingInstDetailDataBean" property="name"/>&nbsp;</td>
						<td align=left class=tblcol valign=top><bean:write name="peerMappingInstDetailDataBean" property="communicationPort"/>&nbsp;</td>
						<td align=left class=tblcol valign=top><bean:write name="peerMappingInstDetailDataBean" property="attemptConnection"/>&nbsp;</td>
						<td align=left class=tblcol valign=top><bean:write name="peerMappingInstDetailDataBean" property="watchDogInterval"/>&nbsp;</td>
            		</tr>
            		<%tempIndexVendor++; %>
            		
            		</logic:iterate>
            		<%}else{ %>
	     				<tr>
			            	<td class="tblfirstcol" colspan="8" align="center" valign=top>No Records Found.</td>
			            </tr>
            		<%} %>
            	</table>
            	
            </td>
          </tr>
    	<tr>
			<td class="small-gap" colspan="2">&nbsp;</td>
   		</tr>
		</table>
		<%index++;%>
		</logic:iterate>
	</td>
</tr>
</table>