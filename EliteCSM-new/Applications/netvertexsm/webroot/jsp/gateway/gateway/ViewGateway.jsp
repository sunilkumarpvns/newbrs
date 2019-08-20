<%@page import="com.elitecore.corenetvertex.constants.CommunicationProtocol"%>
<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData"%>
<%@ page import="java.util.*" %>


<%
	GatewayData gatewayData = (GatewayData) request.getAttribute("gatewayData");
	String commProId = gatewayData.getCommProtocol();
	if(commProId!=null) {
%>
<script type="text/javascript">
	
</script>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<bean:define id="gatewayBean" name="gatewayData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData" />
    <tr>
		<td valign="top" align="left"> 
		<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
		      <tr> 
	            <td class="tblheader-bold" width="100%" colspan="2" height="20%"><bean:message bundle="gatewayResources" key="gateway.view" /></td>
	          </tr>
	          <tr> 
	            <td class="tbllabelcol"  height="20%"><bean:message bundle="gatewayResources" key="gateway.creategateway" /></td>
	            <td class="tblcol"  height="20%"><bean:write name="gatewayBean" property="gatewayName"/>&nbsp;</td>
	          </tr>	          
	          <tr> 
	            <td class="tbllabelcol"  height="20%"><bean:message bundle="gatewayResources" key="gateway.ipaddress" /></td>
	            <td class="tblcol"  height="20%">
	            	<logic:notEmpty name="gatewayBean" property="connectionUrl">
	            		<bean:write name="gatewayBean" property="connectionUrl"/>
	            	</logic:notEmpty>	            	
	            	<logic:empty name="gatewayBean" property="connectionUrl">-</logic:empty>
	            	&nbsp;
	            </td>
	          </tr>
	          <tr> 
	            <td class="tbllabelcol"  height="20%"><bean:message  bundle="gatewayResources" key="gateway.description" /></td>
	            <td class="tblcol"  height="20%" ><bean:write name="gatewayBean" property="description"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tbllabelcol"  height="20%"><bean:message  bundle="gatewayResources" key="gateway.commprotocol" /></td>
	            <td class="tblcol"  height="20%" ><%=CommunicationProtocol.fromId(gatewayData.getCommProtocol()).name%>&nbsp;</td>
	          </tr>
	          <logic:notEmpty name="gatewayBean" property="policyEnforcementMethodName">
	          <tr> 
	            <td class="tbllabelcol"  height="20%"><bean:message  bundle="gatewayResources" key="gateway.policy.enforcement.method" /></td>
	            <td class="tblcol"  height="20%" ><bean:write name="gatewayBean" property="policyEnforcementMethodName"/>&nbsp;</td>
	          </tr>
	          </logic:notEmpty>
			  <tr> 
	             <td class="tbllabelcol"  height="20%"><bean:message bundle="gatewayResources" key="gateway.profile" /></td>
	             <td class="tblcol"  height="20%" >
	             <a href="<%=basePath%>/viewGatewayProfile.do?profileId=<bean:write name="gatewayBean" property="gatewayProfileData.profileId"/>">
	             <bean:write name="gatewayBean" property="gatewayProfileData.profileName"/>&nbsp;
	             </a>
	             </td>
	          </tr>
	          <tr> 
	             <td class="tbllabelcol"  height="20%"><bean:message bundle="gatewayResources" key="gateway.location" /></td>
	             <td class="tblcol"  height="20%">
		             <logic:notEmpty name="gatewayBean" property="gatewayLocationData">	             
		             	<logic:notEmpty name="gatewayBean" property="gatewayLocationData.locationName">
		             		<bean:write name="gatewayBean" property="gatewayLocationData.locationName"/>
		             	</logic:notEmpty>
		             </logic:notEmpty>
		             <logic:equal name="gatewayBean" property="gatewayLocationData" value="">-</logic:equal>
	             	&nbsp;
	             </td>
	          </tr>
	          <tr> 
	             <td class="tbllabelcol"  height="20%"><bean:message bundle="gatewayResources" key="gateway.area" /></td>
	             <td class="tblcol"  height="20%">	             			             	           
		             	<logic:notEmpty name="gatewayBean" property="areaName">
		             		<bean:write name="gatewayBean" property="areaName"/>
		             	</logic:notEmpty>														            		             
		             	<logic:equal name="gatewayBean" property="areaName" value="">-</logic:equal>
	             	 	&nbsp;	             	
	             </td>
	          </tr>	
	          <tr> 
	             <td class="tbllabelcol"  height="20%"><bean:message  key="general.createddate" /></td>
	             <td class="tblcol"  height="20%"><%=EliteUtility.dateToString(gatewayBean.getCreatedDate(),ConfigManager.get(ConfigConstant.DATE_FORMAT))%>&nbsp;</td>
	          </tr>          
	           <tr> 
	             <td class="tbllabelcol"  height="20%"><bean:message  key="general.lastmodifieddate" /></td>
	             <td class="tblcol"  height="20%"><%=EliteUtility.dateToString(gatewayBean.getModifiedDate(),ConfigManager.get(ConfigConstant.DATE_FORMAT))%>&nbsp;</td>
	          </tr>
		</table>
		</td>
    </tr>
    <%if(gatewayData.getCommProtocol().equalsIgnoreCase(CommunicationProtocol.RADIUS.id)  && gatewayData.getRadiusGatewayData()!=null) {	%>		 
     <tr>
		<td valign="top" align="right">	
				<table cellpadding="0" cellspacing="0" border="0" width="97%" >	
						  <tr> 
				            <td class="tblheader-bold" width="100%" height="20%" colspan="2"><bean:message  bundle="gatewayResources" key="gateway.radius.info" /></td>
				          </tr>	
				          <tr> 
				            <td class="tbllabelcol"   width="39%"  height="20%"><bean:message  bundle="gatewayResources" key="gateway.radius.sharedsecret" /></td>
				            <td class="tblcol"   height="20%" ><bean:write name="gatewayBean" property="radiusGatewayData.sharedSecret"/>&nbsp;</td>
				          </tr>
				         
			              <tr> 
				            <td class="tbllabelcol"  width="39%"  height="20%"><bean:message bundle="gatewayResources" key="gateway.radius.minlocalport" /></td>
				            <td class="tblcol"    height="20%"><bean:write name="gatewayBean" property="radiusGatewayData.minLocalPort"/>&nbsp;</td>
				          </tr>
			              
	 			</table>
		</td>
	</tr>
    <%}else if(gatewayData.getCommProtocol().equalsIgnoreCase(CommunicationProtocol.DIAMETER.id)&& gatewayData.getDiameterGatewayData()!=null) {	%>		 
     <tr>
		<td valign="top" align="right">	
				<table cellpadding="0" cellspacing="0" border="0" width="97%" >	
						  <tr> 
				            <td class="tblheader-bold" width="100%" height="20%" colspan="2"><bean:message  bundle="gatewayResources" key="gateway.diameter.info" /></td>
				          </tr>	
				          <tr> 
				            <td class="tbllabelcol"    height="20%"><bean:message  bundle="gatewayResources" key="gateway.diameter.hostidentity" /></td>
				            <td class="tblcol"   height="20%" >
				            	<logic:notEmpty name="gatewayBean" property="diameterGatewayData.hostId"><bean:write name="gatewayBean" property="diameterGatewayData.hostId"/></logic:notEmpty>
				            	<logic:empty name="gatewayBean" property="diameterGatewayData.hostId">-</logic:empty>				            	
				            	&nbsp;
				            </td>
				          </tr>				          
				          <tr> 
				            <td class="tbllabelcol" width="39%"   height="20%"><bean:message  bundle="gatewayResources" key="gateway.diameter.realm" /></td>
				            <td class="tblcol"   height="20%" ><bean:write name="gatewayBean" property="diameterGatewayData.realm"/>&nbsp;</td>
				          </tr>
				          <tr> 
	             			<td class="tbllabelcol" width="39%"  height="20%"><bean:message bundle="gatewayResources" key="gateway.localaddress" /></td>
	             			<td class="tblcol" height="20%"><bean:write name="gatewayBean" property="diameterGatewayData.localAddress"/>&nbsp;</td>
	          			  </tr>
				          <tr> 
	             			<td class="tbllabelcol" width="39%"  height="20%"><bean:message bundle="gatewayResources" key="gateway.request.timeout" /></td>
	             			<td class="tblcol" height="20%">
								<logic:empty name="gatewayBean" property="diameterGatewayData.requestTimeout">0</logic:empty>
								<logic:notEmpty name="gatewayBean" property="diameterGatewayData.requestTimeout"><bean:write name="gatewayBean" property="diameterGatewayData.requestTimeout"/>&nbsp;</logic:notEmpty>	             					             					             					             				
	             			</td>
	          			  </tr>
				          <tr> 
	             			<td class="tbllabelcol" width="39%"  height="20%"><bean:message bundle="gatewayResources" key="gateway.retransmission.count" /></td>
	             			<td class="tblcol" height="20%">
								<logic:empty name="gatewayBean" property="diameterGatewayData.retransmissionCount">0</logic:empty>
								<logic:notEmpty name="gatewayBean" property="diameterGatewayData.retransmissionCount"><bean:write name="gatewayBean" property="diameterGatewayData.retransmissionCount"/>&nbsp;</logic:notEmpty>	             					             					             					             					             			
	             			</td>
	          			  </tr>
	          			   <tr> 
	             			<td class="tbllabelcol" width="39%"  height="20%"><bean:message bundle="gatewayResources" key="gateway.alternatehost" /></td>
	             			<td class="tblcol" height="20%">
	             				<logic:notEmpty name="gatewayBean" property="diameterGatewayData.alternateHostData">
	             						<a href="<%=basePath%>/viewGateway.do?gatewayId=<bean:write name="gatewayBean" property="diameterGatewayData.alternateHostData.gatewayId"/>&commProtocolId=<bean:write name="gatewayBean" property="commProtocol" />" tabindex="12">	             
		             					<bean:write name="gatewayBean" property="diameterGatewayData.alternateHostData.gatewayName"/>
		             			</logic:notEmpty>
		            			 <logic:equal name="gatewayBean" property="diameterGatewayData.alternateHostData" value="">-</logic:equal>	             					             					             					             					             			
	             			</td>
	          			  </tr>
	 			</table>
		</td>
	</tr>
	<%} %>
</table>
<%} %>
