 <%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerRelationWithPeerGroup"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  	<tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" >
	          <tr> 
	            <td class="tblheader-bold" colspan="5" height="20%">
	              <bean:message bundle="diameterResources" key="diameterpeergroup.view"/>
	            </td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="25%" ><bean:message bundle="diameterResources" key="diameterpeergroup.name" /></td>
	            <td class="tblcol" width="*" ><bean:write name="diameterPeerGroup" property="peerGroupName"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="25%" ><bean:message bundle="diameterResources" key="diameterpeergroup.description" /></td>
	            <td class="tblcol" width="*" colspan="3"><bean:write name="diameterPeerGroup" property="description"/>&nbsp;</td>
	          </tr>
	          
				<tr>
					<td class="tblfirstcol" width="25%"><span>Peer Group</span></td>
					<td class="tblcol" width="*" colspan="3">
						<%
							int peerIndex = 0;
						%> 
						<logic:iterate id="peers" name="diameterPeerGroup" property="peerList" type="com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerRelationWithPeerGroup">
						<%=++peerIndex%>.
						<logic:notEmpty name="peers" property="peerName">
							<span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="peers" property="peerId"/>','<bean:write name="peers" property="peerName"/>','<%=EliteViewCommonConstant.DIAMETER_PEER%>');"><bean:write name="peers" property="peerName" /></span>
						</logic:notEmpty> -W- <bean:write name="peers" property="weightage" />
						<br/>
						</logic:iterate> 
						<%
 							if (peerIndex == 0) {
 						%> No Peers Configured <%}%>
					</td>
				</tr>
	          <tr> 
	            <td class="tblfirstcol" width="25%" ><bean:message bundle="diameterResources" key="diameterpeergroup.stateful" /></td>
	            <td class="tblcol" width="*" colspan="3"><bean:write name="diameterPeerGroup" property="stateful"/>&nbsp;</td>
	          </tr>
	          
	          <tr> 
	            <td class="tblfirstcol" width="25%" ><bean:message bundle="diameterResources" key="diameterpeergroup.transactiontimeout" /></td>
	            <td class="tblcol" width="*" colspan="3"><bean:write name="diameterPeerGroup" property="transactionTimeout"/>&nbsp;</td>
	          </tr>
	          
	          <tr> 
	            <td class="tblfirstcol" width="25%" ><bean:message bundle="diameterResources" key="diameterpeergroup.georedunduntgroup" /></td>
	            <td class="tblcol" width="*" colspan="3">
	            	<logic:empty name="diameterPeerGroup" property="geoRedunduntGroup">
	            	 	<bean:message bundle="diameterResources" key="diameterpeergroup.none.configuration" /> &nbsp;
	            	</logic:empty>
	            	<logic:notEmpty name="diameterPeerGroup" property="geoRedunduntGroup">
	             		<span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="diameterPeerGroup" property="geoRedunduntGroup"/>','<bean:write name="diameterPeerGroupForm" property="geoRedunduntGroupName"/>','<%=EliteViewCommonConstant.DIAMETER_PEER_GROUP%>');"><bean:write name="diameterPeerGroupForm" property="geoRedunduntGroupName"/></span>
	            	</logic:notEmpty>
	            </td>
	          </tr>
	          
			</table>
		</td>
    </tr>
</table>


