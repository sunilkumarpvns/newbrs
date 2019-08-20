<%@page import="com.elitecore.corenetvertex.constants.UMStandard"%>
<%@page import="com.elitecore.corenetvertex.constants.GatewayComponent"%>
<%@page import="com.elitecore.corenetvertex.constants.ConversionType"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GroovyScriptData"%>
<script>
	$(document)
			.ready(
					function() {
						if ($(".radiusToPCRFMapping").size() == 0) {
							$("#radiusToPCRF")
									.after(
											"<tr><td colspan='5' align='middle' class='tblfirstcol'>No Records Found</td></tr>");
						}

						if ($(".PCRFToRadiusMapping").size() == 0) {
							$("#PCRFToRadius")
									.after(
											"<tr><td colspan='5' align='middle' class='tblfirstcol'>No Records Found</td></tr>");
						}
						if($(".groovyScriptsData").size() == 0){
							$("#groovyScript").after("<tr><td colspan='5' align='middle' class='tblfirstcol'>No Record Found</td></tr>");
						}						
					});
</script>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td valign="top" align="right">
			<table cellpadding="0" cellspacing="0" border="0" width="97%"
				align="right">
				<tr>
					<td align="left" class="labeltext" valign="top" class="box">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">							
							<tr>
								<td class=tblheader-bold colspan="5">Diameter Info</td>
							</tr>
							<tr>
								<td class="tbllabelcol" width="28%" height="30%">
									<bean:message bundle="gatewayResources" key="gateway.diameter.timeout" />
								</td>
								<td class="tblcol" width="23%" height="20%">
									<bean:write name="gatewayProfileBean" property="diameterProfileData.timeout" />&nbsp;
								</td>
								<td class="tbllabelcol" width="28%" height="20%">
									<bean:message bundle="gatewayResources" key="gateway.diameterattribute.ceravps" />
								</td>
								<td class="tblcol" width="19%" height="20%" ><bean:write
										name="gatewayProfileBean"
										property="diameterProfileData.cerAvps" />&nbsp;</td>										
							</tr>

							<logic:equal value="true" name="gatewayProfileBean"
								property="diameterProfileData.sendDPRCloseEvent">
								<tr>
									<td class="tbllabelcol"   width="28%"  height="20%"><bean:message
											bundle="gatewayResources"
											key="gateway.diameterattribute.sessioncleanupon" /></td>
									<td class="tblcol"    height="20%" colspan="4"
										id="standard"><bean:message bundle="gatewayResources"
											key="gateway.diameterattribute.sessioncleanupcer" /></td>
								</tr>
							</logic:equal>

							<logic:equal value="true" name="gatewayProfileBean"
								property="diameterProfileData.sendDPRCloseEvent">
								<tr>
									<td class="tbllabelcol"  width="28%"  height="20%"><bean:message
											bundle="gatewayResources"
											key="gateway.diameterattribute.sessioncleanupon" /></td>
									<td class="tblcol"    height="20%" colspan="4"
										id="standard"><bean:message bundle="gatewayResources"
											key="gateway.diameterattribute.sessioncleanupdpr" /></td>
								</tr>
							</logic:equal>

							<tr>

							</tr>
							<tr>
								<td class="tbllabelcol"  width="28%"  height="20%"><bean:message
										bundle="gatewayResources"
										key="gateway.diameterattribute.dpravps" /></td>
								<td class="tblcol"  width="23%" height="20%" ><bean:write
										name="gatewayProfileBean"
										property="diameterProfileData.dprAvps" />&nbsp;</td>
								<td class="tbllabelcol"  width="28%"  height="20%"><bean:message
										bundle="gatewayResources"
										key="gateway.diameterattribute.dwravps" /></td>
								<td class="tblcol"  width="19%"  height="20%" ><bean:write
										name="gatewayProfileBean"
										property="diameterProfileData.dwrAvps" />&nbsp;</td>										
							</tr>
							<tr class="small-gap"><td>&nbsp;</td> </tr>							
							<tr>
								<td class=tblheader-bold colspan="5"><bean:message
										bundle="gatewayResources"
										key="gateway.diameterattribute.connectionparameters" /></td>
							</tr>
							<tr>
								<td class="tbllabelcol" width="28%" height="20%"><bean:message
										bundle="gatewayResources"
										key="gateway.diameterattribute.senddprcloseevent" /></td>
								<td class="tblcol" width="23%"   height="20%" ><bean:write
										name="gatewayProfileBean"
										property="diameterProfileData.sendDPRCloseEvent" />&nbsp;</td>
								<td class="tbllabelcol" width="28%" height="20%"><bean:message
										bundle="gatewayResources"
										key="gateway.diameterattribute.socketreceivebuffersize" /></td>
								<td class="tblcol"  width="19%"  height="20%"><bean:write
										name="gatewayProfileBean"
										property="diameterProfileData.socketReceiveBufferSize" />&nbsp;</td>										
							</tr>
							<tr>
								<td class="tbllabelcol" width="28%" height="20%"><bean:message
										bundle="gatewayResources"
										key="gateway.diameterattribute.socketsendbuffersize" /></td>
								<td class="tblcol" width="23%" height="20%"><bean:write
										name="gatewayProfileBean"
										property="diameterProfileData.socketSendBufferSize" />&nbsp;</td>
								<td class="tbllabelcol" width="28%" height="20%"><bean:message
										bundle="gatewayResources"
										key="gateway.diameterattribute.tcpnagleAlgorithm" /></td>
								<td class="tblcol" width="19%" height="20%" ><bean:write
										name="gatewayProfileBean"
										property="diameterProfileData.tcpNagleAlgorithm" />&nbsp;</td>										
							</tr>
							<logic:equal name="gatewayProfileBean" property="diameterProfileData.isDWGatewayLevel" value="true">
								<tr>
									<td class="tbllabelcol"  width="30%"  height="20%"><bean:message
											bundle="gatewayResources"
											key="gateway.diameterattribute.dwrinterval" /></td>
									<td class="tblcol" width="20%" height="20%" colspan="4"><bean:write
											name="gatewayProfileBean"
											property="diameterProfileData.dwInterval" />&nbsp;</td>
								</tr>
							</logic:equal>	
						
							<tr>
								<td class="tbllabelcol" width="28%" height="20%"><bean:message
										bundle="gatewayResources"
										key="gateway.diameterattribute.dwrduration.initconnection" /></td>
								<td class="tblcol" width="23%" height="20%" ><bean:write
										name="gatewayProfileBean"
										property="diameterProfileData.initConnectionDuration" />&nbsp;</td>
								<td class="tbllabelcol" width="28%" height="20%"></td>
								<td class="tblcol" width="19%" height="20%"></td>										
							</tr>  
						
							<tr class="small-gap"><td>&nbsp;</td> </tr>		
							<tr>
								<td class=tblheader-bold colspan="5"><bean:message
										bundle="gatewayResources"
										key="gateway.diameterattribute.applicationparameters" /></td>
							</tr>	
							<tr>
								<td class="tbllabelcol" height="30%" width="28%"><bean:message
										bundle="gatewayResources" key="gateway.pccprovision" /></td>
								<td class="tblcol" height="30%" width="23%">
										<logic:equal name="gatewayProfileBean"	property="diameterProfileData.pccProvision" value="0">
											All On Network Entry
										</logic:equal> 
										<logic:equal name="gatewayProfileBean" property="diameterProfileData.pccProvision" value="1">
											First On Network Entry
										</logic:equal> 
										<logic:equal name="gatewayProfileBean" property="diameterProfileData.pccProvision" value="2">
											None On Network Entry	
										</logic:equal>
								</td>
								<td class="tbllabelcol" width="28%" height="20%">
									<bean:message bundle="gatewayResources" key="gateway.profile.multichargingruleenabled" />
								</td>
								<td class="tblcol" width="19%" height="20%" ><bean:write
										name="gatewayProfileBean"
										property="diameterProfileData.multiChargingRuleEnabled" />&nbsp;</td>								
							</tr>	

							<tr>
								<td class="tbllabelcol" width="28%" height="20%"><bean:message
										bundle="gatewayResources"
										key="gateway.profile.supportedvendorlist" /></td>
								<td class="tblcol" width="23%" height="20%"><bean:write
										name="gatewayProfileBean"
										property="diameterProfileData.supportedVendorList" />&nbsp;</td>
								<td class="tbllabelcol" width="28%" height="20%"><bean:message
										bundle="gatewayResources"
										key="gateway.profile.supportedstandardlist" /></td>
								<logic:equal value="1" name="gatewayProfileBean"
									property="diameterProfileData.supportedStandard">
									<td class="tblrows" width="19%" height="20%" id="standard">Release-7</td>
								</logic:equal>
								<logic:equal value="2" name="gatewayProfileBean"
									property="diameterProfileData.supportedStandard">
									<td class="tblcol" width="19%" height="20%" id="standard">Release-8</td>
								</logic:equal>
								<logic:equal value="3" name="gatewayProfileBean"
									property="diameterProfileData.supportedStandard">
									<td class="tblcol" width="19%" height="20%" id="standard">Release-9</td>
								</logic:equal>
								<logic:equal value="4" name="gatewayProfileBean"
									property="diameterProfileData.supportedStandard">
									<td class="tblrows" width="19%" height="20%" id="standard">CISCOSCE</td>
								</logic:equal>
								<logic:equal value="0" name="gatewayProfileBean"
									property="diameterProfileData.supportedStandard">
									<td class="tblcol" width="19%" height="20%" id="standard">&nbsp;</td>
								</logic:equal>										
							</tr>
							
							<tr>
								<td class="tbllabelcol" width="28%" height="20%"><bean:message
											bundle="gatewayResources"
											key="gateway.diameter.profile.umstandards" />
								</td>
								<td class="tblcol" height="20%">
								<bean:write	name="gatewayProfileBean" property="diameterProfileData.umStandard" />&nbsp;
								</td>
								<td class="tbllabelcol" width="28%" height="20%">&nbsp;</td>
								<td class="tblcol" height="20%">&nbsp;</td>															
							</tr>
							
							<tr>
									<td class="tbllabelcol" width="28%" height="20%"><bean:message
											bundle="gatewayResources"
											key="gateway.diameterattribute.customgxappid" /></td>
									<td class="tblcol" height="20%">&nbsp;
										<logic:equal name="gatewayProfileBean" property="diameterProfileData.isCustomGxAppId" value="true">									
											<bean:write	name="gatewayProfileBean" property="diameterProfileData.gxApplicationId" />&nbsp;									
										</logic:equal>
									</td>
									
									<td class="tbllabelcol" width="28%" height="20%"><bean:message
											bundle="gatewayResources"
											key="gateway.diameterattribute.customgyappid" /></td>
									<td class="tblcol" height="20%">&nbsp;									
										<logic:equal name="gatewayProfileBean" property="diameterProfileData.isCustomGyAppId" value="true">
											<bean:write	name="gatewayProfileBean" property="diameterProfileData.gyApplicationId" />&nbsp;									
										</logic:equal>
									</td>
							</tr>
							<tr>
									<td class="tbllabelcol" width="28%" height="20%"><bean:message
											bundle="gatewayResources"
											key="gateway.diameterattribute.customrxappid" /></td>
									<td class="tblcol" height="20%">&nbsp;									
									<logic:equal name="gatewayProfileBean" property="diameterProfileData.isCustomRxAppId" value="true">
										<bean:write	name="gatewayProfileBean" property="diameterProfileData.rxApplicationId" />&nbsp;									
									</logic:equal>
									</td>
									<td class="tbllabelcol" width="28%" height="20%"><bean:message
											bundle="gatewayResources"
											key="gateway.diameterattribute.customs9appid" /></td>
									<td class="tblcol" height="20%">&nbsp;									
									<logic:equal name="gatewayProfileBean" property="diameterProfileData.isCustomS9AppId" value="true">
										<bean:write	name="gatewayProfileBean" property="diameterProfileData.s9ApplicationId" />&nbsp;									
									</logic:equal>
									</td>
							</tr>
						  <tr>
									<td class="tbllabelcol" width="28%" height="20%"><bean:message
											bundle="gatewayResources"
											key="gateway.diameterattribute.customsyappid" /></td>
									<td class="tblcol" height="20%">&nbsp;									
									<logic:equal name="gatewayProfileBean" property="diameterProfileData.isCustomSyAppId" value="true">
										<bean:write	name="gatewayProfileBean" property="diameterProfileData.syApplicationId" />&nbsp;									
									</logic:equal>
									</td>
									<%if((gatewayProfileData.getGatewayType().equalsIgnoreCase(GatewayComponent.APPLICATION_FUNCTION.value)) || (gatewayProfileData.getGatewayType().equalsIgnoreCase(GatewayComponent.DRA.value) )){ %>
									<td class="tbllabelcol" width="28%" height="20%"><bean:message bundle="gatewayResources" key="gateway.diameter.profile.sessionlookupkey" />&nbsp;</td>
									<td class="tblcol" height="20%"><bean:write	name="gatewayProfileBean" property="diameterProfileData.sessionLookUpKey" />&nbsp;</td>
									<%}else{%>
									<td class="tbllabelcol" width="28%" height="20%">&nbsp;</td>
									<td class="tblcol" height="20%">&nbsp;</td>
									<%} %>
							</tr>
 						
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
						  
							<tr  class="small-gap">	<td colspan="5" >&nbsp;</td>
							</tr>													
						 	
						 	<tr>
								<td class=tblheader-bold colspan="5"><bean:message bundle="gatewayResources" key="gateway.profile.diameter.diametertopcrfpacketmapping" /></td>
							</tr>
							<tr id="radiusToPCRF">
								<td class="tblheader" valign="top" width="20%"><bean:message bundle="gatewayResources" key="gateway.profile.name" /></td>
								<td class="tblheader" valign="top" width="20%" colspan="3"><bean:message bundle="gatewayResources" key="mapping.condition" /></td>
							</tr>	
							<logic:iterate id="packetMapData" name="diameterPacketMapList"
								type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData">
								<logic:equal name="packetMapData" property="packetMappingData.type" value="<%=ConversionType.GATEWAY_TO_PCRF.getConversionType()%>">
									<tr class="radiusToPCRFMapping">
										<td class='tblfirstcol'>
										<a href="<%=basePath%>/viewPacketMapping.do?mappingId=<bean:write name="packetMapData" property="packetMappingData.packetMapId"/>">
										<bean:write name="packetMapData" property="packetMappingData.name" />&nbsp;
										</a>
										</td>
										<td class='tblrows' colspan="3"><bean:write name="packetMapData" property="condition" />&nbsp;</td>
									</tr>
								</logic:equal>
							</logic:iterate>	

							<tr>
								<td class=tblheader-bold colspan="5"><bean:message bundle="gatewayResources" key="gateway.profile.diameter.pcrftodiameterpacketmapping" /></td>
							</tr>
							<tr id="PCRFToRadius">
								<td class="tblheader" valign="top" width="20%"><bean:message bundle="gatewayResources" key="gateway.profile.name" /></td>
								<td class="tblheader" valign="top" width="20%" colspan="3"><bean:message bundle="gatewayResources" key="mapping.condition" /></td>
							</tr>

							<logic:iterate id="packetMapData" name="diameterPacketMapList"
								type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData">
								<logic:equal name="packetMapData" property="packetMappingData.type" value="<%=ConversionType.PCRF_TO_GATEWAY.getConversionType()%>">
									<tr class="PCRFToRadiusMapping">
										<td class='tblfirstcol'>
										<a href="<%=basePath%>/viewPacketMapping.do?mappingId=<bean:write name="packetMapData" property="packetMappingData.packetMapId"/>">
										<bean:write name="packetMapData" property="packetMappingData.name" />&nbsp;
										</a>
										</td>
										<td class='tblrows' colspan="3"><bean:write name="packetMapData" property="condition" />&nbsp;</td>
									</tr>
								</logic:equal>
							</logic:iterate>	
					<!-- GROOVY SCRIPT DATA START -->
						<tr class="small-gap">	<td colspan="5">&nbsp;</td> </tr>
						<tr>
						<td colspan="5">
						<table width="100%">					
						<tr><td class=tblheader-bold colspan="5" ><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript" /></td></tr>
						<tr id="groovyScript">
							<td  class="tblheader" valign="top" ><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript.ordernumber" /></td>
							<td  class="tblheader" valign="top" width="30%"  ><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript.name" /></td>
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



