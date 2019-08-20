<%@page import="com.elitecore.corenetvertex.constants.ConversionType"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData" %>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GroovyScriptData" %>
<script>
	$(document).ready(function(){
		
		if($(".radiusToPCRFMapping").size() == 0){
			$("#radiusToPCRF").after("<tr><td colspan='5' align='middle' class='tblfirstcol'>No Record Found</td></tr>");
		}
		
		if($(".PCRFToRadiusMapping").size() == 0){
			$("#PCRFToRadius").after("<tr><td colspan='5' align='middle' class='tblfirstcol'>No Record Found</td></tr>");
		}		

		if($(".groovyScriptsData").size() == 0){
			$("#groovyScript").after("<tr><td colspan='5' align='middle' class='tblfirstcol'>No Record Found</td></tr>");
		}						
	});
</script>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td valign="top" align="right"> 
		<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
		<tr>
			<td align="right" class="labeltext" valign="top" class="box" > 
				<table cellpadding="0" cellspacing="0" border="0" width="100%" >
					<tr>
						<td class=tblheader-bold colspan="5">Radius Info</td>
					</tr>
					<tr>
					<td colspan="5" >
					<table cellpadding="0" cellspacing="0" border="0" width="100%" >
	          		<tr>
       					<td class="tbllabelcol" width="30%" height="20%" ><bean:message  bundle="gatewayResources" key="gateway.radius.timeout" /></td>
       					<td class="tblrows" width="20%"   ><bean:write name="gatewayProfileBean" property="radiusProfileData.timeout"/>&nbsp;</td>
       					<td class="tbllabelcol" width="30%" height="20%" ><bean:message  bundle="gatewayResources" key="gateway.radius.maxreqtimeout" /></td>
       					<td class="tblrows" width="20%"   ><bean:write name="gatewayProfileBean" property="radiusProfileData.maxRequestTimeout"/>&nbsp;</td>       					
		   			</tr>

		   			<tr>
       					<td class="tbllabelcol" width="30%" height="20%" ><bean:message  bundle="gatewayResources" key="gateway.radius.retrycount" /></td>
       					<td class="tblrows" width="20%"  ><bean:write name="gatewayProfileBean" property="radiusProfileData.retryCount"/>&nbsp;</td>
       					<td class="tbllabelcol" width="30%" height="20%" ><bean:message  bundle="gatewayResources" key="gateway.radius.statuscheckduration" /></td>
       					<td class="tblrows" width="20%"  ><bean:write name="gatewayProfileBean" property="radiusProfileData.statusCheckDuration"/>&nbsp;</td>       					
		   			</tr>

		   			<tr>
       					<td class="tbllabelcol" width="30%" height="20%" ><bean:message  bundle="gatewayResources" key="gateway.radius.icmppingenabled" /></td>
       					<td class="tblrows" width="20%" ><bean:write name="gatewayProfileBean" property="radiusProfileData.icmpPingEnabled"/></td>
       					<td class="tbllabelcol" width="30%" height="20%" ><bean:message  bundle="gatewayResources" key="gateway.profile.supportedvendorlist" /></td>
       					<td class="tblrows" width="20%" ><bean:write name="gatewayProfileBean" property="radiusProfileData.supportedVendorList"/>&nbsp;</td>       					
		   			</tr>
		   			<tr>
		   			</tr>
		   			<tr>
       					<td class="tbllabelcol" width="30%" height="20%" ><bean:message  bundle="gatewayResources" key="gateway.profile.sendacctresponse" /></td>
       					<td class="tblrows" >
       						<bean:write name="gatewayProfileBean" property="radiusProfileData.sendAccountingResponse"/>
       					</td>
						<td class="tbllabelcol" width="30%" height="20%" ><bean:message  bundle="gatewayResources" key="gateway.radius.interiminterval" /></td>
						<td class="tblrows" colspan="4">
							<bean:write name="gatewayProfileBean" property="radiusProfileData.interimInterval"/>
						</td>
		   			</tr>
		   			</table>
		   			</td>
		   			</tr>
					<tr class="small-gap">	<td colspan="5">&nbsp;</td> </tr>		   			
		   				<tr class="small-gap"><td>&nbsp;</td></tr>		
							
						 	<tr>
								<td class=tblheader-bold colspan="5"><bean:message bundle="gatewayResources" key="pccrulemapping"/></td>
							</tr>
							<tr id="pccRuleMapping">
								<td class="tblheaderfirstcol" valign="top" width="20%"><bean:message bundle="gatewayResources" key="gateway.profile.name" /></td>
								<td class="tblheaderlastcol" valign="top" width="20%" colspan="3"><bean:message bundle="gatewayResources" key="gateway.profile.accessnetworktype" /></td>
							</tr>
							<logic:notEmpty    name="gatewayProfileRuleMappingList">
							<logic:iterate id="pccRuleMappingBean" name="gatewayProfileRuleMappingList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileRuleMappingData">
									<tr>
										<td class='tblfirstcol'>
										<a href="<%=basePath%>/pccRuleManagement.do?method=view&ruleMappingId=<bean:write name="pccRuleMappingBean" property="ruleMappingId"/>" >
										<bean:write name="pccRuleMappingBean" property="ruleMappingData.name" />&nbsp;
										</a>
										</td>
										<td class='tblrows' colspan="3"><bean:write name="pccRuleMappingBean" property="accessNetworkType" />&nbsp;</td>
									</tr>
							</logic:iterate>	
							</logic:notEmpty>
						   <logic:empty   name="gatewayProfileRuleMappingList">
						   <tr>
								<td colspan="4" class="tblfirstcol" align="center" > <bean:message bundle="datasourceResources" key="database.datasource.norecordfound"/> </td>
							</tr>
						  </logic:empty>
					<!-- GATEWAY TO PCRF START -->
					<tr class="small-gap">	<td colspan="5">&nbsp;</td> </tr>					
					<tr><td class=tblheader-bold colspan="5" ><bean:message bundle="gatewayResources" key="gateway.profile.radius.radiustopcrfpacketmapping" /></td></tr>
					<tr id="radiusToPCRF">
						<td  class="tblheader" valign="top" width="20%"><bean:message bundle="gatewayResources" key="mapping.name" /></td>
						<td  class="tblheader" valign="top" width="20%" colspan="3"><bean:message bundle="gatewayResources" key="mapping.condition" /></td>
					</tr>
					
					<logic:iterate id="packetMapData" name="diameterPacketMapList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData">
						<logic:equal name="packetMapData" property="packetMappingData.type" value="<%=ConversionType.GATEWAY_TO_PCRF.getConversionType()%>">
							<tr class="radiusToPCRFMapping">
	                    		<td class='tblfirstcol'> 
	                    		<a href="<%=basePath%>/viewPacketMapping.do?mappingId=<bean:write name="packetMapData" property="packetMappingData.packetMapId"/>">
	                    			<bean:write name="packetMapData" property="packetMappingData.name"/>&nbsp;
	                    		</a>	
	                    		</td>
	                       		<td class='tblrows' colspan="3"><bean:write name="packetMapData" property="condition"/>&nbsp;</td>
							</tr>
						</logic:equal>
					</logic:iterate>
					<!-- GATEWAY TO PCRF STOP -->
					<!-- PCRF -TO- RADIUS START -->
					<tr class="small-gap">	<td colspan="5">&nbsp;</td> </tr>					
					<tr><td class=tblheader-bold colspan="5" ><bean:message bundle="gatewayResources" key="gateway.profile.radius.pcrftoradiuspacketmapping" /></td></tr>
					<tr id="PCRFToRadius">
						<td  class="tblheader" valign="top" width="20%"><bean:message bundle="gatewayResources" key="mapping.name" /></td>
						<td  class="tblheader" valign="top" width="20%" colspan="3"><bean:message bundle="gatewayResources" key="mapping.condition" /></td>
					</tr>
					
					<logic:iterate id="packetMapData" name="diameterPacketMapList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData">
						<logic:equal name="packetMapData" property="packetMappingData.type" value="<%=ConversionType.PCRF_TO_GATEWAY.getConversionType()%>">
							<tr class="PCRFToRadiusMapping">
							<td class='tblfirstcol'> 
							   <a href="<%=basePath%>/viewPacketMapping.do?mappingId=<bean:write name="packetMapData" property="packetMappingData.packetMapId"/>"> 
	                    		<bean:write name="packetMapData" property="packetMappingData.name"/>&nbsp;
	                    		</a>
	                    		</td>
	                       		<td class='tblrows' colspan="3"><bean:write name="packetMapData" property="condition"/>&nbsp;</td>
							</tr>
						</logic:equal>
					</logic:iterate>
					<!-- PCRF TO RADIUS STOP -->
					<!-- GROOVY SCRIPT DATA START -->
					<tr class="small-gap">	<td colspan="5">&nbsp;</td> </tr>	
					<tr>
					<td colspan="5">
						<table width="100%">									
						<tr><td class=tblheader-bold colspan="5" ><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript" /></td></tr>
						<tr id="groovyScript">
							<td  class="tblheader" valign="top" width="10%"><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript.ordernumber" /></td>
							<td  class="tblheader" valign="top" width="30%"><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript.name" /></td>
							<td  class="tblheader" valign="top" width="60%" colspan="2"><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript.argument" /></td>
						</tr>
						
						<logic:iterate id="groovyScriptData" name="groovyScriptsDataList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GroovyScriptData">						
						<tr class="groovyScriptsData">
		                  	<td class='tblfirstcol'> <bean:write name="groovyScriptData" property="orderNumber"/>&nbsp;</td>
		                    <td class='tblrows' ><bean:write name="groovyScriptData" property="scriptName"/>&nbsp;</td>
		                    <td class='tblrows' colspan="2"><bean:write name="groovyScriptData" property="argument"/>&nbsp;</td>
						</tr>										
						</logic:iterate>		
						</table>
					</td>
					</tr>			
					<!-- GROOVY SCRIPT DATA END -->
	        	</table>
	    	</td>
	 	</tr>
		</table>
		</td>
	</tr>
</table>

	

	           