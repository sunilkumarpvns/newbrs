<%@page import="com.elitecore.corenetvertex.constants.CommunicationProtocol"%>
<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterProfileData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData"%>
<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterPacketMapData"%>
<%@ page import="java.util.*,com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData" %>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData"%>

<%
	GatewayProfileData gatewayProfileData = (GatewayProfileData)request.getAttribute("gatewayProfileData");
%>


<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData"%><style> 
.light-btn {  border:medium none; font-family: Arial; font-size: 12px; color: #FFFFFF; background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg'); font-weight: bold}
.rightborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 1px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.bottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.allborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.noborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
</style> 

<script type="text/javascript">

	function viewAdvanceGatewayProfile(){
		document.getElementById("head").style.display = "block";
		document.getElementById("<%=gatewayProfileData.getCommProtocolId()%>").style.display = "block";
	}
	
</script>


<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right"> 
<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
	<bean:define id="gatewayProfileBean" name="gatewayProfileData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData" />		
	<tr>
		<td align="right" class="labeltext" valign="top" class="box" > 
			<table cellpadding="0" cellspacing="0" border="0" width="97%" >	
	          <tr> 
				<td class=tblheader-bold colspan="5">
					<bean:message bundle="gatewayResources" key="gateway.profile.view" />			
				</td>	
			  </tr>
	          <tr> 
	            <td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.profile" /></td>
	            <td class="tblcol" width="20%" height="20%"><bean:write name="gatewayProfileBean" property="profileName"/>&nbsp;</td>
	            <td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.gatewaytype" /></td>
	            <td class="tblcol" width="20%" height="20%" ><bean:write name="gatewayProfileBean" property="gatewayType"/>&nbsp;</td>
	          </tr>	          
	           <tr> 
	            <td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.commprotocol" /></td>
	            <td class="tblcol" width="20%" height="20%" ><%=CommunicationProtocol.fromId(gatewayProfileData.getCommProtocolId()).name%>&nbsp;</td>
	            <td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.profile.vendor" /></td>
	            <td class="tblcol" width="20%" height="20%" ><bean:write name="gatewayProfileBean" property="vendorData.vendorName"/>&nbsp;</td>
	          </tr>	         
	          <tr> 
	            <td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.profile.firmware" /></td>
	            <td class="tblcol" width="20%" height="20%" ><bean:write name="gatewayProfileBean" property="firmware"/>&nbsp;</td>
	            <td class="tbllabelcol" width="20%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.profile.maxthroughput" /></td>
	            <td class="tblcol" width="20%" height="20%" ><bean:write name="gatewayProfileBean" property="maxThroughput"/>&nbsp;</td>
	          </tr>
	          <tr>
	          	<td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.profile.bufferbw" /></td>
	            <td class="tblcol" width="20%" height="20%"><bean:write name="gatewayProfileBean" property="bufferBW"/>&nbsp;</td>
	             <td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.profile.maxipcansession" /></td>
	            <td class="tblcol" width="20%" height="20%"><bean:write name="gatewayProfileBean" property="maxIPCANSession"/>&nbsp;</td>
	          </tr>
	          <tr>
	          	<td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.profile.usagereportingtime" /></td>
	            <td class="tblcol" width="20%" height="20%"><bean:write name="gatewayProfileBean" property="usageReportingTime"/>&nbsp;</td>	            
	          	<td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.profile.revalidationmode" /></td>
	            <td class="tblcol" width="20%" height="20%"><bean:write name="gatewayProfileBean" property="revalidationMode"/>&nbsp;</td>	            
	          </tr>
	           <tr>
	          	<td class="tbllabelcol" width="30%" height="20%"><bean:message key="general.lastmodifieddate" /></td>
	            <td class="tblcol" width="20%" height="20%"><%=EliteUtility.dateToString(gatewayProfileBean.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>	            
	          	<td class="tbllabelcol" width="30%" height="20%"><bean:message key="general.createddate" /></td>
	            <td class="tblcol" width="20%" height="20%"><%=EliteUtility.dateToString(gatewayProfileBean.getCreatedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>	      
	          </tr>
	        </table>
		</td>
	</tr>
</table>
		</td>
    </tr>
</table>


				

<%-- <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right">	
			
<%	
		if(gatewayProfileData.getRadiusProfileData() != null && gatewayProfileData.getCommProtocolId().equalsIgnoreCase(GatewayConstants.RADIUS_GATEWAY_ID)) {
		List<GatewayProfilePacketMapData> packetMapList = (List<GatewayProfilePacketMapData>) request.getAttribute("diameterPacketMapList");
		List<PCCRuleMappingData> pccRuleMappingDataList = (List<PCCRuleMappingData>)request.getAttribute("pccRuleMapList");
%>
<div id="head" style="display: none; margin-left: 1.70em;" class="tblheader-bold"><div style="padding-right: 88%">RADIUS Info</div></div>	 
<table id="DTY002" style="display: none;" cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
	<bean:define id="gatewayProfileBean" name="gatewayProfileData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData" />
	 
	<tr>
       <td class="tblfirstcol" width="30%" height="20%" ><bean:message  bundle="gatewayResources" key="gateway.profile.supportedvendorlist" /></td>
       <td class="tblrows" colspan="5"><bean:write name="gatewayProfileBean" property="radiusProfileData.supportedVendorList"/>&nbsp;</td>
	</tr>
	<tr>
       <td class="tblfirstcol" width="30%" height="20%" ><bean:message  bundle="gatewayResources" key="gateway.profile.sendacctresponse" /></td>
       <td class="tblrows" colspan="5">
       		<%if(gatewayProfileBean.getRadiusProfileData().getSendAccountingResponse().equalsIgnoreCase("1")){%>
       			True&nbsp;
       		<%}else{%>
       			False&nbsp;
       		<%}%>
       </td>
	</tr>
	
    <tr height="15px"></tr>
    <tr>
		<td class="tblheader-bold" width="20%" height="20%">Condition</td>
		<td class="tblheader-bold" width="20%" height="20%" >Name</td>
		<td class="tblheader-bold" width="20%" height="20%" >Packet Type</td>
		<td class="tblheader-bold" width="20%" height="20%" >Type</td>		
	</tr>
	
<%for(GatewayProfilePacketMapData packetMapData : packetMapList) {%>
	<tr>
       <td class="tblfirstcol"><%=(packetMapData.getCondition() != null) ? packetMapData.getCondition() : "" %>&nbsp;</td>
       <td class="tblrows"><%=packetMapData.getPacketMappingData().getName() %>&nbsp;</td>
       <td class="tblrows"><%=packetMapData.getPacketMappingData().getPacketType() %>&nbsp;</td>
       <td class="tblrows"><%=packetMapData.getPacketMappingData().getType() %>&nbsp;</td>
	</tr>
<%}%>

<tr height="15px"></tr>
    <tr>
		<td class="tblheader-bold" width="20%" height="20%">Attribute</td>
		<td class="tblheader-bold" width="20%" height="20%" >Policy Key</td>
		<td class="tblheader-bold" width="20%" height="20%" >Default Value</td>
		<td class="tblheader-bold" width="20%" height="20%" >Value Mapping</td>		
	</tr>
	
	<%if(pccRuleMappingDataList != null && pccRuleMappingDataList.size() > 0){%>
		<%for(PCCRuleMappingData pccRuleMappingData : pccRuleMappingDataList) {%>
			<tr>
		       <td class="tblfirstcol"><%=(pccRuleMappingData.getAttribute() != null) ? pccRuleMappingData.getAttribute() : ""%>&nbsp;</td>
		       <td class="tblrows"><%=(pccRuleMappingData.getPolicyKey() != null) ? pccRuleMappingData.getPolicyKey() : ""%>&nbsp;</td>
		       <td class="tblrows"><%=(pccRuleMappingData.getDefaultValue() != null) ? pccRuleMappingData.getDefaultValue() : ""%>&nbsp;</td>
		       <td class="tblrows"><%=(pccRuleMappingData.getValueMapping() != null) ? pccRuleMappingData.getValueMapping() : ""%>&nbsp;</td>
			</tr>
		<%}%>
	<%}else{%>
		<tr>
			<td align="center" class="tblfirstcol" colspan="5">No Records Found.</td>
		</tr>	
	<%}%>		
</table>
      
<%
	} if(gatewayProfileData.getDiameterProfileData() != null &&  gatewayProfileData.getCommProtocolId().equalsIgnoreCase(GatewayConstants.DIAMETER_GATEWAY_ID)) {
		List<GatewayProfilePacketMapData> packetMapList = (List<GatewayProfilePacketMapData>) request.getAttribute("diameterPacketMapList");
		int standard = gatewayProfileData.getDiameterProfileData().getSupportedStandard();
		
		
%>
<div id="head" style="display: none; margin-left: 1.70em;" class="tblheader-bold"><div style="padding-right: 88%"><bean:message bundle="gatewayResources" key="gateway.diameter.info" /></div></div>	 
<table id="DTY003" style="display: none;" cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
	<bean:define id="gatewayProfileBean" name="gatewayProfileData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData" /> 
	<tr> 
		<td class="tblfirstcol"  height="20%" width="30%"><bean:message  bundle="gatewayResources" key="gateway.pccprovision" /></td>
	    <td class="tblrows"  height="20%" width="70%" colspan="4">
	    	<logic:equal name="gatewayProfileBean" property="diameterProfileData.pccProvision" value="0">
				All On Network Entry
			</logic:equal>
			<logic:equal name="gatewayProfileBean" property="diameterProfileData.pccProvision" value="1">
				First On Network Entry
			</logic:equal>
			<logic:equal name="gatewayProfileBean" property="diameterProfileData.pccProvision" value="2">
				None On Network Entry
			</logic:equal>
	     </td>
	</tr>
	
	<logic:equal name="gatewayProfileBean" property="diameterProfileData.isCustomGxAppId" value="true">
	<tr>
		<td class="tblfirstcol" width="30%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.diameterattribute.customgxappid" /></td>
		<td class="tblrows" width="70%" height="20%" colspan="4"><bean:write name="gatewayProfileBean" property="diameterProfileData.gxApplicationId"/>&nbsp;</td>
	</tr>
	</logic:equal>
	
	<logic:equal name="gatewayProfileBean" property="diameterProfileData.isCustomGyAppId" value="true">
	<tr>
		<td class="tblfirstcol" width="30%" height="20%"><bean:message  bundle="gatewayResources" key="gateway.diameterattribute.customgyappid" /></td>
		<td class="tblrows" width="70%" height="20%" colspan="4"><bean:write name="gatewayProfileBean" property="diameterProfileData.gyApplicationId"/>&nbsp;</td>
	</tr>
	</logic:equal>
	
	<logic:equal name="gatewayProfileBean" property="diameterProfileData.isCustomRxAppId" value="true">
	<tr>
		<td class="tblfirstcol"  height="20%"><bean:message  bundle="gatewayResources" key="gateway.diameterattribute.customrxappid" /></td>
		<td class="tblrows" width="70%" height="20%" colspan="4"><bean:write name="gatewayProfileBean" property="diameterProfileData.rxApplicationId"/>&nbsp;</td>
	</tr>
	</logic:equal>
				       			               
	<logic:equal name="gatewayProfileBean" property="diameterProfileData.isDWGatewayLevel" value="true">
	<tr>
		<td class="tblfirstcol"  height="20%"><bean:message  bundle="gatewayResources" key="gateway.diameterattribute.dwrinterval" /></td>
		<td class="tblrows" width="70%" height="20%" colspan="4"><bean:write name="gatewayProfileBean" property="diameterProfileData.dwInterval"/>&nbsp;</td>
	</tr>
	</logic:equal>
	
	<tr> 
		<td class="tblfirstcol"  height="20%"><bean:message bundle="gatewayResources" key="gateway.diameter.timeout" /></td>
		<td class="tblrows"  width="70%" height="20%" colspan="4"><bean:write name="gatewayProfileBean" property="diameterProfileData.timeout"/>&nbsp;</td>
	</tr>
	
	<tr> 
		<td class="tblfirstcol"  height="20%"><bean:message bundle="gatewayResources" key="gateway.diameter.retranscnt" /></td>
		<td class="tblrows"  width="70%" height="20%" colspan="4"><bean:write name="gatewayProfileBean" property="diameterProfileData.retransmissionCnt"/>&nbsp;</td>
	</tr>
	
	<tr> 
		<td class="tblfirstcol"  height="20%"><bean:message bundle="gatewayResources" key="gateway.diameter.tlsenable" /></td>
		<td class="tblrows" width="70%" height="20%" colspan="4"><bean:write name="gatewayProfileBean" property="diameterProfileData.tlsEnable"/>&nbsp;</td>
	</tr>	
	
	<tr>
       <td class="tblfirstcol" width="30%" height="20%" ><bean:message  bundle="gatewayResources" key="gateway.profile.supportedvendorlist" /></td>
       <td class="tblrows" width="70%" height="20%" colspan="5" ><bean:write name="gatewayProfileBean" property="diameterProfileData.supportedVendorList"/>&nbsp;</td>
	</tr>
	<tr>
       <td class="tblfirstcol" width="30%" height="20%" ><bean:message  bundle="gatewayResources" key="gateway.profile.multichargingruleenabled" /></td>
       <td class="tblrows" width="70%" height="20%" colspan="4" ><bean:write name="gatewayProfileBean" property="diameterProfileData.multiChargingRuleEnabled"/>&nbsp;</td>
	</tr>
	<tr>
       <td class="tblfirstcol" width="30%" height="20%" ><bean:message  bundle="gatewayResources" key="gateway.profile.supportedstandardlist" /></td>
       <%if(standard == 1){ %>
	       <td class="tblrows" width="70%" height="20%" colspan="4" id="standard">Release-7</td>
       <%}else if(standard == 2){ %>
          <td class="tblrows" width="70%" height="20%" colspan="4" id="standard">Release-8</td>
       <%}else if(standard == 3){ %>
    	   <td class="tblrows" width="70%" height="20%" colspan="4" id="standard">Release-9</td>
       <%}else if(standard == 4){ %>
           <td class="tblrows" width="70%" height="20%" colspan="4" id="standard">CISCOSCE</td>
       <%}%>
	</tr>
    <tr height="15px"></tr>
    <tr>
		<td class="tblheader-bold" width="20%" height="20%">Condition</td>
		<td class="tblheader-bold" width="20%" height="20%" >Name</td>
		<td class="tblheader-bold" width="20%" height="20%" >Packet Type</td>
		<td class="tblheader-bold" width="20%" height="20%" >Type</td>		
	</tr>
    
<%		for(GatewayProfilePacketMapData packetMapData : packetMapList) {		 %>
    
	<tr>
       <td class="tblfirstcol" width="20%" height="20%"><%=(packetMapData.getCondition() != null) ? packetMapData.getCondition() : "" %>&nbsp;</td>
       <td class="tblrows" width="20%" height="20%"><%=packetMapData.getPacketMappingData().getName() %>&nbsp;</td>
       <td class="tblrows" width="20%" height="20%"><%=packetMapData.getPacketMappingData().getPacketType() %>&nbsp;</td>
       <td class="tblrows" width="20%" height="20%"><%=packetMapData.getPacketMappingData().getType() %>&nbsp;</td>
	</tr>
	
<%		}				 %>		
</table>
<%	}	 %>
		</td>
    </tr>
</table> --%>
