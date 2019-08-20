<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="com.elitecore.netvertexsm.web.gateway.attrmapping.form.PacketMappingForm"%>
<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>




<style>
.allborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px} 
.topbottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 1px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.noborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.plcKey { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
</style>



<table cellpadding="0" cellspacing="0" border="0" width="100%">
  <tr>
    <td valign="top" align="right">
    <html:form action="/pccRuleManagement.do?method=update">	
<bean:define id="packetMappingDataBean" name="ruleMappingData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData" />
		<table cellpadding="0" cellspacing="0" border="0" width="97%">
	    	<tr>
				<td valign="top" align="right"> 
					<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
							<tr>
								<td align="right" class="labeltext" valign="top" class="box">
									<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td style="margin-left: 2.0em;" colspan="5" class="tblheader-bold">View PCC Rule Mapping </td>
											</tr>
											<tr>
												<td class="tbllabelcol" width="20%" height="20%"><bean:message bundle="gatewayResources" key="mapping.name" /></td>
												<td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="packetMappingDataBean" property="name" />&nbsp;</td>
											</tr>
											<tr>
												<td class="tbllabelcol" width="20%" height="20%"><bean:message bundle="gatewayResources" key="mapping.description" /></td>
												<td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="packetMappingDataBean" property="description" />&nbsp;</td>
											</tr>
									</table>
								</td>
							</tr>
							<tr class="small-gap">
								<td colspan="5">&nbsp;</td>
							</tr>
							
							<tr>
								<td colspan="5">
									<table width="100%" style="table-layout: fixed;">
											<tr>
												<td class=tblheader-bold colspan="4">Static Mapping</td>
											</tr>
										   <tr id="staticPCCRuleMappingHeader">
												<td class="tblheaderfirstcol" width="15%" height="25%">Attribute</td>
												<td class="tblheader" width="40%" height="30%">Policy Key</td>
												<td class="tblheader" width="25%" height="25%">Default Value</td>
												<td class="tblheaderlastcol" width="20%" height="20%">Value	Mapping</td>
											</tr>
											<logic:iterate id="pccRuleMap" name="pccRuleMapList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData">
													<logic:equal value="STATIC" name="pccRuleMap" property="type">
																<tr class="staticPCCRuleMapping">
																	<td class="tblfirstcol" style="width: 50px; vertical-align: top;word-wrap:break-word" >
																		<bean:write name="pccRuleMap" property="attribute" />&nbsp;
																	</td>
																	<td class="tblrows" style="vertical-align: top;word-wrap:break-word" width="20%";>
																			<bean:write name="pccRuleMap" property="policyKey" />
																	</td>
																	<%if(pccRuleMap.getDefaultValue() == null || pccRuleMap.getDefaultValue().trim().equalsIgnoreCase("null")){%>
																		<td class="tblrows">&nbsp;</td>
																	<%}else{%>
																	<td class="tblrows" style="vertical-align: top;word-wrap:break-word">
																		<bean:write name="pccRuleMap" property="defaultValue" />&nbsp;
																	</td>
																	<%}if (pccRuleMap.getValueMapping() == null|| pccRuleMap.getValueMapping().trim().equalsIgnoreCase("null")) {%>
																	<td class="tblrows">&nbsp;</td>
																	<%} else {%>
																	<td class="tblrows" style="vertical-align: top;word-wrap:break-word">
																		<bean:write name="pccRuleMap" property="valueMapping" />&nbsp;
																	</td>
																	<%}%>
																</tr>
															</logic:equal>
												</logic:iterate>
											</table>
										</td>
									</tr>
									<tr>
										<td colspan="5">
											<table width="100%" style="table-layout: fixed;">
											<tr>
												<td class=tblheader-bold colspan="4">Dynamic Mapping</td>
											</tr>
														<tr id="dynamicPCCRuleMappingHeader">
															<td class="tblheaderfirstcol" width="15%" height="25%">Attribute</td>
															<td class="tblheader" width="40%" height="30%">Policy Key</td>
															<td class="tblheader" width="25%" height="25%">Default Value</td>
															<td class="tblheaderlastcol" width="20%" height="20%">Value Mapping</td>
														</tr>
														<logic:iterate id="pccRuleMap" name="pccRuleMapList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData">
															<logic:equal value="DYNAMIC" name="pccRuleMap" property="type">
																<tr class="dynamicPCCRuleMapping">
																	<td class="tblfirstcol" style="vertical-align: top;word-wrap:break-word;">
																		<bean:write name="pccRuleMap" property="attribute" />&nbsp;
																	</td>
																	<%if(pccRuleMap.getPolicyKey()!= null){%>
												            		<td class="tblrows" style="vertical-align: top;word-wrap:break-word;" width="20%";>
																		<%=StringEscapeUtils.escapeHtml(pccRuleMap.getPolicyKey().trim())%>&nbsp;
																	</td>
																	<%}else{%>
												            			<td class="tblrows" >&nbsp;</td>
												                	<%}if (pccRuleMap.getDefaultValue() == null || pccRuleMap.getDefaultValue().trim().equalsIgnoreCase("null")){%>
																	<td class="tblrows">&nbsp;</td>
																	<%}else{%>
																	<td class="tblrows" style="vertical-align: top;word-wrap:break-word;">
																		<bean:write name="pccRuleMap" property="defaultValue" />&nbsp;
																	</td>
																	<%}if(pccRuleMap.getValueMapping()==null ||pccRuleMap.getValueMapping().trim().equalsIgnoreCase("null")){%>
																	<td class="tblrows">&nbsp;</td>
																	<%}else{%>
																	<td class="tblrows" style="vertical-align: top;word-wrap:break-word;">
																			<bean:write name="pccRuleMap" property="valueMapping" />&nbsp;
																	</td>
																	<%}%>
																</tr>
															</logic:equal>
														</logic:iterate>
													</table>
												</td>
											</tr>
					</table> 
			</td>
		</tr>	
	</table>
	</html:form> 
	</td>
	</tr>
	</table>