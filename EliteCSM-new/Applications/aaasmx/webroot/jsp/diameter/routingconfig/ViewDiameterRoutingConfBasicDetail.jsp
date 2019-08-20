<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<%@page import="com.elitecore.aaa.core.util.constant.CommonConstants"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<%@page import="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData"%>
<%@page import="com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions"%>

<%
	DiameterRoutingConfData viewDiameterRoutingConfigData = (DiameterRoutingConfData)request.getAttribute("diameterRoutingConfData");
%>


<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <bean:define id="diameterRoutingConfBean" name="diameterRoutingConfData" scope="request" type="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData" />
    <tr> 
    
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="8" height="20%">
            <bean:message bundle="diameterResources" key="routingconf.viewsummary"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="22%" height="20%"><bean:message key="general.name" /></td>
            <td class="tblcol" width="28%" height="20%" ><bean:write name="diameterRoutingConfBean" property="name"/></td>
            
            <td class="tblfirstcol" width="22%" height="20%"><bean:message bundle="diameterResources" key="routingconf.tablename"/></td>
		     <td class="tblcol" width="28%" height="20%">
			     <logic:notEmpty name="diameterRoutingConfBean" property="diameterRoutingTableData.routingTableName">
				    <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="diameterRoutingConfBean" property="diameterRoutingTableData.routingTableId"/>','<bean:write name="diameterRoutingConfBean" property="diameterRoutingTableData.routingTableName"/>','<%=EliteViewCommonConstant.DIAMETER_ROUTING_TABLE%>');">
				    	<bean:write name="diameterRoutingConfBean" property="diameterRoutingTableData.routingTableName"/>
				    </span>
				</logic:notEmpty>
		     </td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="22%" height="20%"><bean:message bundle="diameterResources" key="general.description" /></td>
            <td class="tblcol" width="78%" height="20%" colspan="3"><bean:write name="diameterRoutingConfBean" property="description"/>&nbsp;</td>
          </tr>
           <tr> 
            <td class="tblfirstcol" width="22%" height="20%"><bean:message bundle="diameterResources" key="routingconf.realmname"/>&nbsp;</td>
            <td class="tblcol" width="28%" height="20%"><bean:write name="diameterRoutingConfBean" property="realmName"/>&nbsp;</td>
            <td class="tblfirstcol" width="22%" height="20%"><bean:message bundle="diameterResources" key="routingconf.appids"/>&nbsp;</td>
            <td class="tblcol" width="28%" height="20%"><bean:write name="diameterRoutingConfBean" property="appIds"/>&nbsp;</td>
          </tr>         
          <tr> 
            <td class="tblfirstcol" width="22%" height="20%"><bean:message bundle="diameterResources" key="routingconf.originhost"/>&nbsp;</td>
            <td class="tblcol" width="28%" height="20%"><bean:write name="diameterRoutingConfBean" property="originHost"/>&nbsp;</td>
            <td class="tblfirstcol" width="22%" height="20%"><bean:message bundle="diameterResources" key="routingconf.originrealm"/>&nbsp;</td>
            <td class="tblcol" width="28%" height="20%"><bean:write name="diameterRoutingConfBean" property="originRealm"/>&nbsp;</td>
          </tr>          
          <tr> 
            <td class="tblfirstcol" width="22%" height="20%"><bean:message bundle="diameterResources" key="routingconf.ruleset"/>&nbsp;</td>
            <td class="tblcol" width="28%" height="20%"><bean:write name="diameterRoutingConfBean" property="ruleset"/>&nbsp;</td>
            <td class="tblfirstcol" width="22%" height="20%"><bean:message bundle="diameterResources" key="routingconf.transmapconf"/>&nbsp;</td>
            <%if(viewDiameterRoutingConfigData.getTransMapConfId() != null){%>            	
            	<td class="tblcol" width="28%" height="20%">
	            	<logic:notEmpty name="diameterRoutingConfBean" property="translationMappingConfData.name">
					    <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="diameterRoutingConfBean" property="translationMappingConfData.translationMapConfigId"/>','<bean:write name="diameterRoutingConfBean" property="translationMappingConfData.name"/>','<%=EliteViewCommonConstant.TRANSLATION_MAPPING%>');">
					    	<bean:write name="diameterRoutingConfBean" property="translationMappingConfData.name"/>
					    </span>
					</logic:notEmpty>
					&nbsp;
            	</td>
            <%}else if(viewDiameterRoutingConfigData.getCopyPacketMapId()!=null){%>
            	<td class="tblcol" width="28%" height="20%">
            		<logic:notEmpty name="diameterRoutingConfBean" property="copyPacketMappingConfData.name">
					    <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="diameterRoutingConfBean" property="copyPacketMappingConfData.copyPacketTransConfId"/>','<bean:write name="diameterRoutingConfBean" property="copyPacketMappingConfData.name"/>','<%=EliteViewCommonConstant.COPY_PACKET_CONFIG%>');">
					    	<bean:write name="diameterRoutingConfBean" property="copyPacketMappingConfData.name"/>
					    </span>
					</logic:notEmpty>
					&nbsp;
            	</td>
            <%}else{%>
            	<td class="tblcol" width="28%" height="20%">&nbsp;</td>            
            <% }%>
          </tr>
          <tr>
          	 <td class="tblfirstcol" width="22%" height="20%"><bean:message bundle="diameterResources" key="routingconf.routingaction"/></td>
          	 <td class="tblcol" width="28%" height="20%">
          	 	<bean:write name="diameterRoutingConfBean" property="routingActionName" /> &nbsp;
          	 </td>
		    <td class="tblfirstcol" width="22%" height="20%"><bean:message bundle="diameterResources" key="routingconf.statefulrouting"/></td>
            <td class="tblcol" width="28%" height="20%" >
            	<logic:equal name="diameterRoutingConfBean" property="statefulRouting" value="1"> 
            		Enabled
            	</logic:equal>
            	<logic:notEqual name="diameterRoutingConfBean" property="statefulRouting" value="1">
            		Disabled
            	</logic:notEqual>
            	
            </td>
		 </tr>

		 <tr>
		 	<td class="tblfirstcol" width="22%" height="20%"><bean:message bundle="diameterResources" key="routingconf.transactiontimeout"/></td>
            <td class="tblcol" width="28%" height="20%" ><bean:write name="diameterRoutingConfBean" property="transactionTimeout"/>&nbsp;</td>
		 	
		 	
		 	 <td class="tblfirstcol" width="22%" height="20%"><bean:message bundle="diameterResources" key="routingconf.attachedredirection"/></td>
            <td class="tblcol" width="28%" height="20%" >
            	<logic:equal name="diameterRoutingConfBean" property="attachedRedirection" value="true"> 
            		Enabled
            	</logic:equal>
            	<logic:notEqual name="diameterRoutingConfBean" property="attachedRedirection" value="true">
            		Disabled
            	</logic:notEqual>
            	
            </td>
		 </tr>	
		 <logic:notEmpty property="subsciberMode" name="diameterRoutingConfBean">
			 <logic:equal value="<%=CommonConstants.IMSI_MSISDN %>" property="subsciberMode" name="diameterRoutingConfBean">
			  <tr>
			 	<td class="tblfirstcol" width="22%" height="20%">
			 		<bean:message bundle="diameterResources" key="diameterrouting.subscriber.routing1"/>
			 	</td>
	            <td class="tblcol" width="28%" height="20%" > 
		            <logic:notEmpty name="diameterRoutingConfBean" property="imsiBasedRoutingTableData">
					    <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="diameterRoutingConfBean" property="imsiBasedRoutingTableData.routingTableId"/>','<bean:write name="diameterRoutingConfBean" property="imsiBasedRoutingTableData.routingTableName"/>','<%=EliteViewCommonConstant.IMSI_BASED_ROUTING_TABLE%>');">
					    	<bean:write name="diameterRoutingConfBean" property="imsiBasedRoutingTableData.routingTableName"/>
					    </span>
		            </logic:notEmpty>
	           		 &nbsp;
	           </td>
	            <td class="tblfirstcol" width="22%" height="20%"><bean:message bundle="diameterResources" key="diameterrouting.subscriber.routing2"/></td>
	            <td class="tblcol" width="28%" height="20%"> 
	            <logic:notEmpty name="diameterRoutingConfBean" property="msisdnBasedRoutingTableData">
	            	<logic:notEmpty name="diameterRoutingConfBean" property="msisdnBasedRoutingTableData">
					    <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="diameterRoutingConfBean" property="msisdnBasedRoutingTableData.routingTableId"/>','<bean:write name="diameterRoutingConfBean" property="msisdnBasedRoutingTableData.routingTableName"/>','<%=EliteViewCommonConstant.MSISDN_BASED_ROUTING_TABLE%>');">
					    	<bean:write name="diameterRoutingConfBean" property="msisdnBasedRoutingTableData.routingTableName"/>
					    </span>
		            </logic:notEmpty>
	            	
	            </logic:notEmpty>
	            &nbsp;</td>
			 </tr>	
			 </logic:equal>
		  
			  <logic:equal value="<%=CommonConstants.MSISDN_IMSI %>" property="subsciberMode" name="diameterRoutingConfBean">
			  <tr>
			 	 <td class="tblfirstcol" width="22%" height="20%">
			 	 	<bean:message bundle="diameterResources" key="diameterrouting.subscriber.routing1"/></td>
	             <td class="tblcol" width="28%" height="20%"> 
	            <logic:notEmpty name="diameterRoutingConfBean" property="msisdnBasedRoutingTableData">
	           		 <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="diameterRoutingConfBean" property="msisdnBasedRoutingTableData.routingTableId"/>','<bean:write name="diameterRoutingConfBean" property="msisdnBasedRoutingTableData.routingTableName"/>','<%=EliteViewCommonConstant.MSISDN_BASED_ROUTING_TABLE%>');">
					  	<bean:write name="diameterRoutingConfBean" property="msisdnBasedRoutingTableData.routingTableName"/>
					 </span>
	            </logic:notEmpty>
	            &nbsp;</td>
			 	<td class="tblfirstcol" width="22%" height="20%">
			 		<bean:message bundle="diameterResources" key="diameterrouting.subscriber.routing2"/></td>
	            <td class="tblcol" width="28%" height="20%" > 
	            <logic:notEmpty name="diameterRoutingConfBean" property="imsiBasedRoutingTableData">
	           		 <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="diameterRoutingConfBean" property="imsiBasedRoutingTableData.routingTableId"/>','<bean:write name="diameterRoutingConfBean" property="imsiBasedRoutingTableData.routingTableName"/>','<%=EliteViewCommonConstant.IMSI_BASED_ROUTING_TABLE%>');">
					   	<bean:write name="diameterRoutingConfBean" property="imsiBasedRoutingTableData.routingTableName"/>
					  </span>
	            </logic:notEmpty>
	            &nbsp;</td>
			 </tr>	
			 </logic:equal>
		 </logic:notEmpty>
		 
		 <logic:empty property="subsciberMode" name="diameterRoutingConfBean">
		  <tr>
			   <td class="tblfirstcol" width="22%" height="20%">
			 	 	<bean:message bundle="diameterResources" key="diameterrouting.subscriber.routing1"/></td>
	             <td class="tblcol" width="28%" height="20%"> 
	            	&nbsp;
	            </td>
			 	<td class="tblfirstcol" width="22%" height="20%">
			 		<bean:message bundle="diameterResources" key="diameterrouting.subscriber.routing2"/></td>
	            <td class="tblcol" width="28%" height="20%" > 
	           		 &nbsp;
	            </td>
			 </tr>	
		 </logic:empty>
		 
		</table>
		</td>
    </tr>
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
   </tr>
</table>