
<%@ taglib uri="/WEB-INF/tld/elite_netvertex.tld" prefix="sm"%>
<%@ page import="com.elitecore.corenetvertex.constants.ProtocolType"%>
<%@ page import="com.elitecore.corenetvertex.constants.ConversionType"%>
<%@ page import="com.elitecore.corenetvertex.constants.PacketType"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.AttributeMappingData"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.netvertexsm.web.gateway.attrmapping.form.PacketMappingForm"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=basePath%>/css/displaytag.css" >


<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyType"%><style>
.allborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px} 
.topbottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 1px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.noborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.plcKey { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
</style>

<%
	PacketMappingForm packetMappingForm = (PacketMappingForm)request.getAttribute("packetMappingForm");
	//PacketMappingData packetMapData = (PacketMappingData)request.getAttribute("packetMappingData");
	
 
	Map<String,String> commProtocolTypes= new HashMap<String,String>();
	commProtocolTypes.put("CCR","Credit-Control-Request");
	commProtocolTypes.put("CCA","Credit-Control-Response");
	commProtocolTypes.put("RAR","Re-Auth-Request");
	commProtocolTypes.put("RAA","Re-Auth-Response");
	commProtocolTypes.put("ASR","Abort-Session-Request");
	commProtocolTypes.put("ASA","Abort-Session-Response");	
	commProtocolTypes.put("AAR","Authenticate-Authorize-Request");
	commProtocolTypes.put("AAA","Authenticate-Authorize-Response");
	commProtocolTypes.put("STR","Session-Termination-Request");
	commProtocolTypes.put("STA","Session-Termination-Response");
	commProtocolTypes.put("ACR","Accounting-Request");
	commProtocolTypes.put("COA","Change-Of-Authorization");
	commProtocolTypes.put("DCR","Disconnect-Request");
	commProtocolTypes.put("AR","Access-Request");
	commProtocolTypes.put("AA","Access-Accept");
	
	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
%>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
  	<tr>
   <td valign="top" align="right"> 
		<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
		<bean:define id="packetMappingDataBean" name="packetMappingData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData" />
		<tr>
			<td align="right" class="labeltext" valign="top" class="box" > 
				<table cellpadding="0" cellspacing="0" border="0" width="100%" >	
				  <tr>
				  	<td style="margin-left: 2.0em;" colspan="5" class="tblheader-bold"><bean:message bundle="gatewayResources" key="mapping.view" /></td>
				  </tr>
		          <tr> 
		            <td class="tbllabelcol" width="20%" height="20%"><bean:message bundle="gatewayResources" key="mapping.name"/></td>
		            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="packetMappingDataBean" property="name"/>&nbsp;</td>
		          </tr>

		          <tr> 
		            <td class="tbllabelcol" width="20%" height="20%"><bean:message bundle="gatewayResources" key="mapping.description"/></td>
		            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="packetMappingDataBean" property="description"/>&nbsp;</td>
		          </tr>
				
		          <tr> 
		            <td class="tbllabelcol" width="20%" height="20%"><bean:message  bundle="gatewayResources" key="mapping.commprotocol" /></td>
		            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="packetMappingDataBean" property="commProtocol"/>&nbsp;</td>
		          </tr>
		          <tr> 
		            <td class="tbllabelcol" width="20%" height="20%"><bean:message  bundle="gatewayResources" key="mapping.conversationtype" /></td>
		            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="packetMappingDataBean" property="type"/>&nbsp;</td>
		          </tr>
		          <tr> 
		            <td class="tbllabelcol" width="20%" height="20%"><bean:message  bundle="gatewayResources" key="mapping.packettype" /></td>
		            <td class="tblcol" width="70%" height="20%" colspan="3"><%=commProtocolTypes.get(packetMappingDataBean.getPacketType())%>&nbsp;</td>
		          </tr>	
		          <tr> 
	             	<td class="tbllabelcol"  height="20%"><bean:message  key="general.createddate" /></td>
	             	<td class="tblcol"  height="20%"><%=EliteUtility.dateToString(packetMappingDataBean.getCreatedDate(),ConfigManager.get(ConfigConstant.DATE_FORMAT))%>&nbsp;</td>
	         	 </tr>
		         <tr> 
	            	<td class="tbllabelcol"  height="20%"><bean:message  key="general.lastmodifieddate" /></td>
	             	<td class="tblcol"  height="20%"><%=EliteUtility.dateToString(packetMappingDataBean.getModifiedDate(),ConfigManager.get(ConfigConstant.DATE_FORMAT))%>&nbsp;</td>
	         	 </tr>
		          	          	                           	          	
		        </table>		        
			</td>
		</tr>
		<tr>
			<td align="right" class="labeltext" valign="top" class="box" > 
				<table cellpadding="0" cellspacing="0" border="0" width="100%" >
		  <tr> 
			<td style="margin-left: 2.0em;" class="tblheader-bold" colspan="8">Mapping</td>
		  </tr>                                      
		  <tr id='gatewaytopcrf'> 
		  	<td valign="middle" colspan="8">  
				<table cellpadding="0" id="gatewayToPCRFTable" cellspacing="0" border="0" width="100%">
					
					<tr>
						<logic:equal name="packetMappingDataBean" property="type" value="PCRF TO GATEWAY">
						<td align="center" class="tblheader" valign="top" width="20%">Attribute</td>
						<td align="center" class="tblheader" valign="top" width="20%">Policy Key</td>
						</logic:equal>					
						<logic:equal name="packetMappingDataBean" property="type" value="GATEWAY TO PCRF">							
							<td align="center" class="tblheader" valign="top" width="20%">Policy Key</td>
							<td align="center" class="tblheader" valign="top" width="20%">Attribute</td>
						</logic:equal>											
						<td align="center" class="tblheader" valign="top" width="20%">Default Value</td>
						<td align="center" class="tblheader" valign="top" width="30%">Value Mapping</td>						
					</tr>
					
			<logic:iterate id="mappingData" scope="request" name="packetMappingForm"  property="attributeMappings" type="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.AttributeMappingData">
				<tr>
				<logic:equal name="packetMappingDataBean" property="type" value="PCRF TO GATEWAY">
				<%if(mappingData.getAttribute() != null){%>				
            		<td class="allborder"> <%=StringEscapeUtils.escapeHtml(mappingData.getAttribute())%> </td>
            	<%}else{%>
            		<td class="allborder">&nbsp;</td>	
            	<%}if(mappingData.getPolicyKey() != null){%>
            		<td class="tblrows" ><%=StringEscapeUtils.escapeHtml(mappingData.getPolicyKey())%></td>
            	<%}else{%>
            		<td class="tblrows" >&nbsp;</td>
                	<%}%>
                </logic:equal>					
				<logic:equal name="packetMappingDataBean" property="type" value="GATEWAY TO PCRF">
					<%if(mappingData.getPolicyKey() != null){%>
            			<td class="allborder" ><%=StringEscapeUtils.escapeHtml(mappingData.getPolicyKey())%></td>
            		<%}else{%>
            			<td class="tblrows" >&nbsp;</td>
                	<%}if(mappingData.getAttribute() != null){%>				
            			<td class="tblrows"> <%=StringEscapeUtils.escapeHtml(mappingData.getAttribute())%> </td>
            		<%}else{%>
            			<td class="allborder">&nbsp;</td>	
            		<%}%>
				</logic:equal>					
				<%if(mappingData.getDefaultValue()!=null) { %>            		
            		<td class="tblrows" ><%=StringEscapeUtils.escapeHtml(mappingData.getDefaultValue())%></td>
                <%}else{%>            		
					<td class="tblrows" >&nbsp;</td>
                <%}if(mappingData.getValueMapping()!=null) { %>
            		<td class="tblrows" ><%=StringEscapeUtils.escapeHtml(mappingData.getValueMapping())%></td>
                <%}else{				 %>            		
					<td class="tblrows" >&nbsp;</td>
                <%}%>					            		
      			</tr>		
			</logic:iterate>					
				</table>
			</td> 
		  </tr>		  		  		    		  
		</table> 
			</td>
		</tr>	
		<tr><td>&nbsp;</td></tr>	
		<tr>
			<td align="right" class="labeltext" valign="top" class="box" > 
				<table cellpadding="0" cellspacing="0" border="0" width="100%" >				
				  <tr>				  	
				  	<td style="margin-left: 2.0em;" colspan="5" class="tblheader-bold"><bean:message  bundle="gatewayResources" key="gateway.profile.associated" /></td>
				  </tr>
		          <tr>
						<td> 
							<display:table   id="gatewayProfileDataBean" name="configuredGatewayProfileDataList" cellpadding="0" cellspacing="0" requestURI="/viewPacketMapping.do?mappingId=${packetMappingData.packetMapId}"  pagesize="<%=pageSize%>"  style="width:100%;margin:0px 0 0px 0">										
			  				<display:column  headerClass="tblheaderfirstcol" style="border:1px solid rgb(204, 204, 204);border-top:0px; width:2%" title='Sr#' >
			  					&nbsp;&nbsp;&nbsp;&nbsp;<%=pageContext.getAttribute("gatewayProfileDataBean_rowNum")%>
			  				</display:column>					    				
			  				<display:column headerClass="tblheader" style="border:1px solid rgb(204, 204, 204);padding-left:5px;border-left:0px;border-top:0px;width:20%" title="Name">
			  				<a href="<%=basePath%>/viewGatewayProfile.do?profileId=<bean:write name="gatewayProfileDataBean" property="profileId"/>"> 
											<bean:write name="gatewayProfileDataBean" property="profileName"/>&nbsp;
							</a>
							</display:column>
			  				<display:column headerClass="tblheaderlastcol" style="border:1px solid rgb(204, 204, 204);padding-left:5px;border-left:0px;border-top:0px;width:30%" title='<bean:message  bundle="gatewayResources" key="Description"/>'>
			  								<bean:write name="gatewayProfileDataBean" property="description"/>&nbsp;
							</display:column>
			  				<display:setProperty name="paging.banner.first"  	value='<span class="pagelinks"> [ First / Prev ] {0} [ <a href="{3}">Next</a> / <a href="{4}">Last</a> ] </span>'></display:setProperty>
			  				<display:setProperty name="paging.banner.last"  	value='<span class="pagelinks"> [ <a href="{1}">First</a> / <a href="{2}">Prev</a> ] {0} [ Next / Last ] </span>'></display:setProperty>
			  				<display:setProperty name="paging.banner.full"  	value='<span class="pagelinks"> [ <a href="{1}">First</a> / <a href="{2}">Prev</a> ] {0} [ <a href="{3}">Next</a> / <a href="{4}">Last</a> ]</span>'></display:setProperty>					    				
			  				<display:setProperty name="paging.banner.placement"  	value="bottom"></display:setProperty>
			  				<display:setProperty name="basic.empty.showtable" 		value="true"></display:setProperty>
			  				<display:setProperty name="paging.banner.item_name" 	value="Gateway Profile"></display:setProperty>
			  				<display:setProperty name="paging.banner.items_name" 	value="Gateway Profiles"></display:setProperty>
			  				<display:setProperty name="paging.banner.no_items_found" value='<tr><td colspan="{0}" class="table-gray" align="center"></td></tr>'></display:setProperty>
			  				<display:setProperty name="basic.msg.empty_list_row" value='<tr><td colspan="{0}" class="table-gray" style="text-align:center;background-color: #D9E6F6;" align="center">No Record Found</td></tr>'></display:setProperty>    				
			  				<display:setProperty name="paging.banner.onepage" value='<span class="pagelinks" style="width:59.6%"></span>'></display:setProperty>			  									    									    									    
						    </display:table>
						</td>
					</tr>
		        </table>			
			</td>
		</tr>		
	</table>
			</td>
	    </tr>
	</table>

<%@include file="/jsp/core/includes/common/Footer.jsp" %>