<%@page import="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterPeerGroupData"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterPeerGroupRelData"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <bean:define id="diameterRoutingTableBean" name="diameterRoutingConfData" scope="request" type="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData" />
   <tr> 
      <td valign="top" align="right" colspan="2"> 
    	 <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="100%">
              <bean:message bundle="diameterResources" key="routingconf.failureactions"/>
            </td>
          </tr>
          <tr>
          	<td colspan="4" width="100%">
          		<table class="box" width="100%">
          		<tr>
          			<td class="tblheader" width="33%">Error Code</td>
	  				<td class="tblheader" width="33%">Failure Action</td>
	 				<td class="tblheader" width="33%">Failure Argument</td>
	 			</tr>
	 			<logic:iterate id="diameterRoutingConfigFailureParam"  name="diameterRoutingTableBean" property="diameterRoutingConfigFailureParamSet" type="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfigFailureParam">
	 				<tr>
	 					<td class="tblrows" width="33%"><bean:write name="diameterRoutingConfigFailureParam" property="errorCodes"/>&nbsp;</td>
	 					<td class="tblrows" width="33%">
	 						<logic:iterate id="obj" property='failureActionMap' name='diameterRoutingConfForm' >
	 							<logic:equal value='<%=diameterRoutingConfigFailureParam.getFailureAction().toString()%>' property='key' name='obj'>
	 								<bean:write name="obj" property="value"/>
	 							</logic:equal>
	 						</logic:iterate>&nbsp;	 
	 					</td>	
	  					<td class="tblrows" width="33%"><bean:write name="diameterRoutingConfigFailureParam" property="failureArgument"/>&nbsp;</td>
	 				</tr>
	 			</logic:iterate>
	 			<logic:empty name="diameterRoutingTableBean" property="diameterRoutingConfigFailureParamSet">
	 				<tr><td colspan="3" align="center">No Failure Action Configure.</td></tr>
	 			</logic:empty>	
	 			</table>
	 		</td>			
	  	</tr>
          <%-- <tr> 
            <td class="tblfirstcol" width="22%">
              <bean:message bundle="diameterResources" key="routingconf.protocolfailure"/>
            </td>
            <td class="tblcol" width="28%">
              <logic:equal name="diameterRoutingTableBean" property="protocolFailureAction" value="1">
		          <bean:message bundle="diameterResources" key="routingconf.drop"/>
		      </logic:equal>
		      <logic:equal name="diameterRoutingTableBean" property="protocolFailureAction" value="2">
		          <bean:message bundle="diameterResources" key="routingconf.failover"/>
		      </logic:equal>
		      <logic:equal name="diameterRoutingTableBean" property="protocolFailureAction" value="3">
		          <bean:message bundle="diameterResources" key="routingconf.redirect"/>
		      </logic:equal>
		      <logic:equal name="diameterRoutingTableBean" property="protocolFailureAction" value="4">
		          <bean:message bundle="diameterResources" key="routingconf.passthrough"/>
		      </logic:equal>
            </td>
            <td class="tblfirstcol" width="22%">
              <bean:message bundle="diameterResources" key="routingconf.protocolfailurearg"/>
            </td>
            <td class="tblcol" width="28%">
              <bean:write name="diameterRoutingTableBean" property="protocolFailureArguments"/>&nbsp;
            </td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="22%">
              <bean:message bundle="diameterResources" key="routingconf.transientfailure"/>
            </td>
            <td class="tblcol" width="28%">
              <logic:equal name="diameterRoutingTableBean" property="transientFailureAction" value="1">
		          <bean:message bundle="diameterResources" key="routingconf.drop"/>
		      </logic:equal>
		      <logic:equal name="diameterRoutingTableBean" property="transientFailureAction" value="2">
		          <bean:message bundle="diameterResources" key="routingconf.failover"/>
		      </logic:equal>
		      <logic:equal name="diameterRoutingTableBean" property="transientFailureAction" value="3">
		          <bean:message bundle="diameterResources" key="routingconf.redirect"/>
		      </logic:equal>
		      <logic:equal name="diameterRoutingTableBean" property="transientFailureAction" value="4">
		          <bean:message bundle="diameterResources" key="routingconf.passthrough"/>
		      </logic:equal>
            </td>
            <td class="tblfirstcol" width="22%">
              <bean:message bundle="diameterResources" key="routingconf.transientfailurearg"/>
            </td>
            <td class="tblcol" width="28%">
              <bean:write name="diameterRoutingTableBean" property="transientFailureArguments"/>&nbsp;
            </td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="22%">
              <bean:message bundle="diameterResources" key="routingconf.permanentfailure"/>
            </td>
            <td class="tblcol" width="28%">
              <logic:equal name="diameterRoutingTableBean" property="permanentFailureAction" value="1">
		          <bean:message bundle="diameterResources" key="routingconf.drop"/>
		      </logic:equal>
		      <logic:equal name="diameterRoutingTableBean" property="permanentFailureAction" value="2">
		          <bean:message bundle="diameterResources" key="routingconf.failover"/>
		      </logic:equal>
		      <logic:equal name="diameterRoutingTableBean" property="permanentFailureAction" value="3">
		          <bean:message bundle="diameterResources" key="routingconf.redirect"/>
		      </logic:equal>
		      <logic:equal name="diameterRoutingTableBean" property="permanentFailureAction" value="4">
		          <bean:message bundle="diameterResources" key="routingconf.passthrough"/>
		      </logic:equal>
            </td>
            <td class="tblfirstcol" width="22%">
              <bean:message bundle="diameterResources" key="routingconf.permanentfailurearg"/>
            </td>
            <td class="tblcol" width="28%">
              <bean:write name="diameterRoutingTableBean" property="permanentFailureArguments"/>&nbsp;
            </td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="22%">
              <bean:message bundle="diameterResources" key="routingconf.timeoutaction"/>
            </td>
            <td class="tblcol" width="28%">
              <logic:equal name="diameterRoutingTableBean" property="timeOutAction" value="1">
		          <bean:message bundle="diameterResources" key="routingconf.drop"/>
		      </logic:equal>
		      <logic:equal name="diameterRoutingTableBean" property="timeOutAction" value="2">
		          <bean:message bundle="diameterResources" key="routingconf.failover"/>
		      </logic:equal>
		      <logic:equal name="diameterRoutingTableBean" property="timeOutAction" value="3">
		          <bean:message bundle="diameterResources" key="routingconf.redirect"/>
		      </logic:equal>
		      <logic:equal name="diameterRoutingTableBean" property="timeOutAction" value="4">
		          <bean:message bundle="diameterResources" key="routingconf.passthrough"/>
		      </logic:equal>
            </td>
            <td class="tblfirstcol" width="22%">
              <bean:message bundle="diameterResources" key="routingconf.timeoutactionarg"/>
            </td>
            <td class="tblcol" width="28%">
              <bean:write name="diameterRoutingTableBean" property="timeOutArguments"/>&nbsp;
            </td>
          </tr> --%>
         </table>
       </td>
     </tr>
    <tr> 
      <td valign="top" align="right" colspan="2"> 
    	 <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="diameterResources" key="routingconf.peer.groups"/></td>
          </tr>
          </table>
       </td>
     </tr>
     <tr>
     	<td colspan="2">
     	<%int index=1; %>
 		<logic:iterate id="diameterPeerGroupDataBean"  name="diameterRoutingTableBean" property="diameterPeerGroupDataSet" type="DiameterPeerGroupData">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader" colspan="2" height="20%">Peer Group-<%=index%></td>
          </tr>
          
          <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1> <bean:message bundle="diameterResources" key="routingconf.ruleset"/></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="diameterPeerGroupDataBean" property="ruleset"/>&nbsp;</td>
         </tr>
          <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1> <bean:message bundle="diameterResources" key="routingconf.peergroup.peer"/></td>
            <td class="tblcol" width="70%" height="20%" >
            	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
            		<tr>
            			<td class="tblheader" height="20%"><bean:message key="general.serialnumber"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="diameterResources" key="routingconf.peergroup.peer"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="diameterResources" key="routingconf.peergroup.loadfactor"/></td>
            		</tr>
            		<% int tempIndexPeer = 1; %>
            		<%if(diameterPeerGroupDataBean.getDiameterPeerGroupRelDataSet()!=null && !diameterPeerGroupDataBean.getDiameterPeerGroupRelDataSet().isEmpty()){ %>
            		<logic:iterate id="peerMappingInstDetailDataBean"  name="diameterPeerGroupDataBean" property="diameterPeerGroupRelDataSet">
            		<tr>
            			<td class="tblfirstcol" height="20%"><%=tempIndexPeer%></td>
						<td align=left class=tblrows valign=top><bean:write name="peerMappingInstDetailDataBean" property="diameterPeerData.name"/>&nbsp;</td>
						<td align=left class=tblrows valign=top><bean:write name="peerMappingInstDetailDataBean" property="loadFector"/>&nbsp;</td>
            		</tr>
            		<%tempIndexPeer++; %>
            		
            		</logic:iterate>
            		<%if(tempIndexPeer==1){%>
            				<tr>
				            	<td class="tblfirstcol" colspan="5" align="center" valign=top>No Records Found.</td>
				            </tr>
            		<% }%>
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