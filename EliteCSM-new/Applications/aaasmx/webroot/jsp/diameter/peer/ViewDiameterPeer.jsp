 <%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <bean:define id="diameterPeerDataBean" name="diameterPeerData" scope="request" type="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData" />
    <tr> 
    
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" >
	          <tr> 
	            <td class="tblheader-bold" colspan="5" height="20%">
	              <bean:message bundle="diameterResources" key="diameterpeer.viewpeer"/>
	            </td>
	          </tr>
	           <tr> 
	          </tr>
	        
	          <tr> 
	            <td class="tblfirstcol" width="25%" ><bean:message bundle="diameterResources" key="diameterpeer.name" /></td>
	            <td class="tblcol" width="25%" ><bean:write name="diameterPeerDataBean" property="name"/>&nbsp;</td>
	            <td class="tblfirstcol" width="25%" ><bean:message bundle="diameterResources" key="diameterpeer.peerid" /></td>
	            <td class="tblcol" width="25%" ><bean:write name="diameterPeerDataBean" property="peerId"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="25%" ><bean:message bundle="diameterResources" key="diameterpeer.hostidentity" /></td>
	            <td class="tblcol" width="25%" colspan="3"><bean:write name="diameterPeerDataBean" property="hostIdentity"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="25%" ><bean:message bundle="diameterResources" key="diameterpeer.realmname" /></td>
	            <td class="tblcol" width="25%" colspan="3"><bean:write name="diameterPeerDataBean" property="realmName"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="25%" ><bean:message bundle="diameterResources" key="diameterpeer.remoteaddress" /></td>
	            <td class="tblcol" width="25%" colspan="3"><bean:write name="diameterPeerDataBean" property="remoteAddress"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="25%" ><bean:message bundle="diameterResources" key="diameterpeer.localaddress" /></td>
	            <td class="tblcol" width="25%" colspan="3"><bean:write name="diameterPeerDataBean" property="localAddress"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="25%" ><bean:message bundle="diameterResources" key="diameterpeer.requesttimeout" /></td>
	            <td class="tblcol" width="25%" colspan="3"><bean:write name="diameterPeerDataBean" property="requestTimeout"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="25%" ><bean:message bundle="diameterResources" key="diameterpeer.retransmissioncount" /></td>
	            <td class="tblcol" width="25%" colspan="3"><bean:write name="diameterPeerDataBean" property="retransmissionCount"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="25%" ><bean:message bundle="diameterResources" key="routingconf.imsibasedrouting.secondarypeer" /></td>
	            <td class="tblcol" width="25%" colspan="3"><bean:write name="diameterPeerDataBean" property="secondaryPeerName"/>&nbsp;</td>
	          </tr>
	           <tr> 
	            <td class="tblfirstcol" width="25%" ><bean:message bundle="diameterResources" key="diameterpeer.uriformat" /></td>
	            <td class="tblcol" width="25%" colspan="3"><bean:write name="diameterPeerDataBean" property="diameterURIFormat"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="25%" ><bean:message bundle="diameterResources" key="diameterpeer.profilename" /></td>
	            <td class="tblcol" width="25%" colspan="3">
	           		 <logic:notEmpty name="diameterPeerDataBean" property="diameterPeerProfileData.profileName">
					    <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="diameterPeerDataBean" property="peerProfileId"/>','<bean:write name="diameterPeerDataBean" property="diameterPeerProfileData.profileName"/>','<%=EliteViewCommonConstant.DIAMETER_PEER_PROFILE%>');">
					     	<bean:write name="diameterPeerDataBean" property="diameterPeerProfileData.profileName"/>
					   </span>
					</logic:notEmpty>
	            </td>
	          </tr>
		</table>
		</td>
    </tr>
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>   
   </tr>
</table>


