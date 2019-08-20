<%@page import="java.util.Set"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapDetailData"%>
<%@page import="com.elitecore.elitesm.web.servermgr.copypacket.forms.ViewCopyPacketMappingConfigForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Copy Packet Mapping</title>

<script type="text/javascript">
$(document).ready(function(){
	$("#defaultMapping").hide();
	$("#dummyresponse").hide();
	
	
});

function toggleTable(tableId) {
	
	
    var lTable = document.getElementById(tableId);
    if(tableId=="defaultMapping"){
	    var ldefault = document.getElementById("defaultMappingImage");
	   	lTable.style.display = (lTable.style.display == "table") ? "none" : "table";
	    if(lTable.style.display=="table"){
	    	ldefault.src="images/bottom-level.jpg";
	    }else{
	    	ldefault.src="images/top-level.jpg";
	    }
    }
    if(tableId=="dummyresponse"){
	    var ldummy = document.getElementById("dummyresponseImage");
	   	lTable.style.display = (lTable.style.display == "table") ? "none" : "table";
	    if(lTable.style.display=="table"){
	    	ldummy.src="images/bottom-level.jpg";
	    }else{
	    	ldummy.src="images/top-level.jpg";
	    }
    }
    
}
function mappingToggleTable(tableId) {
	
	var lImage = document.getElementById("mappingImage"+tableId);
   	var lTable = document.getElementById("Mapping"+tableId);
    lTable.style.display = (lTable.style.display == "table") ? "none" : "table";
    if(lTable.style.display=="table"){
       	lImage.src="images/bottom-level.jpg";
    }else{
    	lImage.src="images/top-level.jpg";
    }
} 

</script>
</head>
<body>
	<table width="100%">
		<bean:define id="copyPacketTranslationConfBean" name="copyPacketMappingConfData" scope="request" type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData" />
			<td width="85%">
					<tr>
						<td>
							<table cellpadding="0" cellspacing="0" id="table3" border="0" width="100%">
								<tr>
									<td cellpadding="0" cellspacing="0" border="0" width="100%"
										class="box">
										<table cellpadding="0" cellspacing="0" border="0" width="100%" >
											<tr>
												<td class="tblheader-bold" colspan="3" height="20%">
													<bean:message bundle="servermgrResources" key="copypacket.configdetail"/>
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top" colspan="4">&nbsp;</td>
											</tr>
											<%int index =0; %>
											<logic:iterate id="mappingData" name="copyPacketTranslationConfBean" property="copyPacketTransMapData" type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapData">
											<%index++;
											if(index==1){
											%>
											<tr>
												<td align=left class=tblheader-bold valign=top colspan=2>Mapping <%=index%></td>
												<td class="tblheader-bold" align="right" width="15px" >
													<img id="mappingImage<%=index%>" onclick="mappingToggleTable('<%=index%>')" src="images/bottom-level.jpg" />
												</td>
											</tr>
											<tr>
												<td colspan="4">
													<table width="100%" id="Mapping<%=index%>" align="right" border="0" style="display:table ">
											<%}else{%>
												<tr>
												
												<td align=left class=tblheader-bold valign=top colspan=2>Mapping <%=index%></td>
												<td class="tblheader-bold" align="right" width="15px">
													<img id="mappingImage<%=index%>" onclick="mappingToggleTable(<%=index%>)" src="images/top-level.jpg" />
												</td> 
											</tr>
											
											<tr>
											<td colspan="4">
													<table width="100%" id="Mapping<%=index%>" align="right" border="0" style="display:none ">
											<%} %>		
														<tr>
															<td>
																<table cellpadding="0" width="97%" align="right" border="0">
																	<tr>
																		<td  align=left class='labeltext' valign=top width="30%"><bean:message bundle="servermgrResources" key="copypacket.viewmappingconfig.mappingname"/></td>
																		<td class="labeltext"><bean:write name="mappingData" property="mappingName"/></td>
																	</tr>
																	<tr>
																		<td valign="top" class="labeltext" width="30%"><bean:message bundle="servermgrResources" key="copypacket.viewmappingconfig.inexpression"/></td>
																		<td class="labeltext"><bean:write name="mappingData" property="inExpression"/></td>
																	</tr>
																	<tr>
																		<td valign="top" class="labeltext" width="30%"><bean:message bundle="servermgrResources" key="copypacket.defaultmapping"/></td>
																		<td class="labeltext"><bean:write name="mappingData" property="isDefaultMapping"/></td>
																	</tr>
																	<tr>
																		<td valign="top" class="labeltext" width="30%"><bean:message bundle="servermgrResources" key="copypacket.dummyresponse"/></td>
																		<td class="labeltext"><bean:write name="mappingData" property="dummyResponse"/></td>
																	</tr>
																</table>
															</td>
														</tr>
														<logic:equal name="mappingData" property="isDefaultMapping" value="false">
														<tr>
															<td colspan="2" align="right">
																<table cellpadding="0" cellspacing="0" class="box" width="100%">	
																	<tr>
																		<td class="table-header"><bean:message bundle="servermgrResources" key="copypacket.viewmappingconfig.requestparameter"/></td>
																	</tr>
																	<tr>
																		<td class="labeltext" style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
																	</tr>
																	<tr>
																	<!-- Adding Header For Request -->
																		<td>
																			<table id="example" class="labelText" cellspacing="0" width="100%">
																				<tr>
																					<td>
																						<table cellpadding="0" cellspacing="0" class="box" width="100%">
																							<tr>
																								<td align="left" class="tblheader" valign="top"
																									width="10%"><bean:message bundle="servermgrResources" key="copypacket.parameter.operation"/></td>
																								<td align=left class=tblheader valign=top
																									width="15%"><bean:message bundle="servermgrResources" key="copypacket.parameter.checkexpression"/></td>
																								<td align=left class=tblheader valign=top
																									width="25%"><bean:message bundle="servermgrResources" key="copypacket.parameter.destinationexpression"/></td>
																								<td align=left class=tblheader valign=top
																									width="25%"><bean:message bundle="servermgrResources" key="copypacket.parameter.sourceexpression"/></td>
																								<td align=left class=tblheader valign=top
																									width="10%"><bean:message bundle="servermgrResources" key="copypacket.parameter.defaultvalue"/></td>
																								<td align=left class=tblheader valign=top
																									width="10%" ><bean:message bundle="servermgrResources" key="copypacket.valuemapping"/></td>
																							</tr>
																							<%int reqcount=0; %>
																							<logic:iterate id="mappingDetailData" name="mappingData" property="copyPacketTransMapDetail" type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapDetailData">
																								<logic:equal value="TMI0001"  name="mappingDetailData" property="mappingTypeId">
																									<%reqcount++; %>	
																								<tr>
																									<td class="tblrows" width="10%"><bean:write name="mappingDetailData" property="operation"/>&nbsp;</td>
																									<td class="tblrows" width="15%"><bean:write name="mappingDetailData" property="checkExpression"/>&nbsp;</td>
																									<td class="tblrows" width="25%"><bean:write name="mappingDetailData" property="destinationExpression"/>&nbsp;</td>
																									<td class="tblrows" width="25%"><bean:write name="mappingDetailData" property="sourceExpression"/>&nbsp;</td>
																									<td class="tblrows" width="10%"><bean:write name="mappingDetailData" property="defaultValue"/>&nbsp;</td>
																									<td class="tblrows" width="10%" style="border-right: none"><bean:write name="mappingDetailData" property="valueMapping"/>&nbsp;</td>
																								</tr>
																								</logic:equal>
																							</logic:iterate> 
																							<%if(reqcount==0){ %>
																								<tr><td colspan="6" align="center">No Record Found</td></tr>
																							<%}%>
																						</table>
																					</td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td class="labeltext" style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
														</tr>
														<tr>
															<td colspan="2" align="right">
																<table cellpadding="0" cellspacing="0" class="box" width="100%">
																	<tr>
																		<td class="table-header"><bean:message bundle="servermgrResources" key="copypacket.viewmappingconfig.responseparameter"/></td>
																	</tr>
																	<tr>
																		<td class="labeltext"
																			style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
																	</tr>
																	<tr>
																		<td>
																			<table class="labelText" cellspacing="0" width="100%">
																				<tr>
																					<td>
																						<table cellpadding="0" cellspacing="0" class="box" width="100%" border="0">
																							<tr>
																								<td align=left class=tblheader valign=top width="10%" ><bean:message bundle="servermgrResources" key="copypacket.parameter.operation"/></td>
																								<td align=left class=tblheader valign=top width="15%"><bean:message bundle="servermgrResources" key="copypacket.parameter.checkexpression"/></td>
																								<td align=left class=tblheader valign=top width="25%"><bean:message bundle="servermgrResources" key="copypacket.parameter.destinationexpression"/></td>
																								<td align=left class=tblheader valign=top width="25%"><bean:message bundle="servermgrResources" key="copypacket.parameter.sourceexpression"/></td>
																								<td align=left class=tblheader valign=top width="10%"><bean:message bundle="servermgrResources" key="copypacket.parameter.defaultvalue"/></td>
																								<td align=left class=tblheader valign=top width="10%"><bean:message bundle="servermgrResources" key="copypacket.valuemapping"/></td>
																							</tr>
																							<%int rescount =0; %>
																							<logic:iterate id="mappingDetailData" name="mappingData" property="copyPacketTransMapDetail" type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapDetailData">
																								<logic:equal value="TMI0002"  name="mappingDetailData" property="mappingTypeId">
																								<%rescount++; %>	
																									<tr>
																										<td class="tblrows" width="10%"><bean:write name="mappingDetailData" property="operation"/>&nbsp;</td>
																										<td class="tblrows" width="15%"><bean:write name="mappingDetailData" property="checkExpression"/> &nbsp;</td>
																										<td class="tblrows" width="25%"><bean:write name="mappingDetailData" property="destinationExpression"/> &nbsp;</td>
																										<td class="tblrows" width="25%"><bean:write name="mappingDetailData" property="sourceExpression"/>&nbsp;</td>
																										<td class="tblrows" width="10%"><bean:write name="mappingDetailData" property="defaultValue"/>&nbsp;</td>
																										<td class="tblrows" width="10%" style="border-right: none"><bean:write name="mappingDetailData" property="valueMapping"/>&nbsp;</td>
																									</tr>
																								</logic:equal>
																								</logic:iterate>
																								<%if(rescount==0){ %>
																									<tr><td colspan="6" align="center">No Record Found</td></tr>
																								<%} %>
																						</table>
																					</td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														</logic:equal>
													</table>
												</td>
											</tr>
											</logic:iterate> 
											<tr>
												<td align=left class=tblheader-bold valign=top colspan=2>
													<bean:message bundle="servermgrResources" key="copypacket.defaultmapping"/>
												</td>
												<td class="tblheader-bold" align="right" width="15px" >
													<img id="defaultMappingImage" onclick="toggleTable('defaultMapping')" src="images/top-level.jpg" />
												</td>
											</tr>
											
											
											<tr>
												<td colspan="4">
													
													<table width="100%" align="right" border="0" id="defaultMapping">
													<% int countDefault = 0; %>
												
													<logic:iterate id="defaultmapping" name="copyPacketTranslationConfBean" property="copyPacketTransMapData" type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapData">
													<%if(countDefault==0){%> 
													
													<logic:equal name="defaultmapping" property="isDefaultMapping" value="true" parameter="copyPacketTranslationConfBean">
													
													<tr>
															<td colspan="2" align="right">
																<table cellpadding="0" cellspacing="0" class="box" width="100%">	
																	<tr>
																		<td class="table-header"><bean:message bundle="servermgrResources" key="copypacket.viewmappingconfig.requestparameter"/></td>
																	</tr>
																	<tr>
																		<td class="labeltext" style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
																	</tr>
																	<tr>
																	<!-- Adding Header For Request -->
																		<td>
																			<table id="example" class="labelText" cellspacing="0" width="100%">
																				<tr>
																					<td>
																						<table cellpadding="0" cellspacing="0" class="box" width="100%">
																							<tr>
																								<td align="left" class="tblheader" valign="top"
																									width="10%"><bean:message bundle="servermgrResources" key="copypacket.parameter.operation"/></td>
																								<td align=left class=tblheader valign=top
																									width="15%"><bean:message bundle="servermgrResources" key="copypacket.parameter.checkexpression"/></td>
																								<td align=left class=tblheader valign=top
																									width="25%"><bean:message bundle="servermgrResources" key="copypacket.parameter.destinationexpression"/></td>
																								<td align=left class=tblheader valign=top
																									width="25%"><bean:message bundle="servermgrResources" key="copypacket.parameter.sourceexpression"/></td>
																								<td align=left class=tblheader valign=top
																									width="10%"><bean:message bundle="servermgrResources" key="copypacket.parameter.defaultvalue"/></td>
																								<td align=left class=tblheader valign=top
																									width="10%" ><bean:message bundle="servermgrResources" key="copypacket.valuemapping"/></td>
																							</tr>
																							<%int defreqcount = 0; %>
																							<logic:iterate id="mappingDetailData" name="defaultmapping" property="copyPacketTransMapDetail" type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapDetailData">
																								<logic:equal value="TMI0001"  name="mappingDetailData" property="mappingTypeId">
																								<%defreqcount++; %>	
																								<tr>
																									<td class="tblrows" width="10%"><bean:write name="mappingDetailData" property="operation"/>&nbsp;</td>
																									<td class="tblrows" width="15%"><bean:write name="mappingDetailData" property="checkExpression"/>&nbsp;</td>
																									<td class="tblrows" width="25%"><bean:write name="mappingDetailData" property="destinationExpression"/>&nbsp;</td>
																									<td class="tblrows" width="25%"><bean:write name="mappingDetailData" property="sourceExpression"/>&nbsp;</td>
																									<td class="tblrows" width="10%"><bean:write name="mappingDetailData" property="defaultValue"/>&nbsp;</td>
																									<td class="tblrows" width="10%" style="border-right: none"><bean:write name="mappingDetailData" property="valueMapping"/>&nbsp;</td>
																								</tr>
																								</logic:equal>
																							</logic:iterate>
																							<%if(defreqcount==0){ %>
																									<tr><td colspan="6" align="center">No Record Found</td></tr>
																							<%} %>
																						</table>
																					</td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																</table>
															</td>
															
														</tr>
														<tr>
															<td class="labeltext" style="padding-right: 10px; padding-left: 10px; padding-top: 2px; padding: bottom:2px;"></td>
														</tr>
														<tr>
															<td colspan="2" align="right">
																<table cellpadding="0" cellspacing="0" class="box" width="100%">
																	<tr>
																		<td class="table-header"><bean:message bundle="servermgrResources" key="copypacket.viewmappingconfig.responseparameter"/></td>
																	</tr>
																	<tr>
																		<td class="labeltext"
																			style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
																	</tr>
																	<tr>
																		<td>
																			<table class="labelText" cellspacing="0" width="100%">
																				<tr>
																					<td>
																						<table cellpadding="0" cellspacing="0" class="box" width="100%" border="0">
																							<tr>
																								<td align=left class=tblheader valign=top width="10%" ><bean:message bundle="servermgrResources" key="copypacket.parameter.operation"/></td>
																								<td align=left class=tblheader valign=top width="15%"><bean:message bundle="servermgrResources" key="copypacket.parameter.checkexpression"/></td>
																								<td align=left class=tblheader valign=top width="25%"><bean:message bundle="servermgrResources" key="copypacket.parameter.destinationexpression"/></td>
																								<td align=left class=tblheader valign=top width="25%"><bean:message bundle="servermgrResources" key="copypacket.parameter.sourceexpression"/></td>
																								<td align=left class=tblheader valign=top width="10%"><bean:message bundle="servermgrResources" key="copypacket.parameter.defaultvalue"/></td>
																								<td align=left class=tblheader valign=top width="10%"><bean:message bundle="servermgrResources" key="copypacket.valuemapping"/></td>
																							</tr>
																							<%int defrescount =0; %>
																							<logic:iterate id="mappingDetailData" name="defaultmapping" property="copyPacketTransMapDetail" type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapDetailData">
																								<logic:equal value="TMI0002"  name="mappingDetailData" property="mappingTypeId">
																								<% defrescount++; %>	
																									<tr>
																										<td class="tblrows" width="10%"><bean:write name="mappingDetailData" property="operation"/>&nbsp;</td>
																										<td class="tblrows" width="15%"><bean:write name="mappingDetailData" property="checkExpression"/> &nbsp;</td>
																										<td class="tblrows" width="25%"><bean:write name="mappingDetailData" property="destinationExpression"/> &nbsp;</td>
																										<td class="tblrows" width="25%"><bean:write name="mappingDetailData" property="sourceExpression"/>&nbsp;</td>
																										<td class="tblrows" width="10%"><bean:write name="mappingDetailData" property="defaultValue"/>&nbsp;</td>
																										<td class="tblrows" width="10%" style="border-right: none"><bean:write name="mappingDetailData" property="valueMapping"/>&nbsp;</td>
																									</tr>
																								</logic:equal>
																							</logic:iterate>
																							<%if(defrescount==0){ %>
																									<tr><td colspan="6" align="center">No Record Found</td></tr>
																							<%}%>
																						</table>
																					</td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<%countDefault++; %>
														</logic:equal>
														<% }%>
														</logic:iterate>
														
													</table>
												</td>
											</tr>
											
											<tr>
												<td align=left class=tblheader-bold valign=top colspan=2>
													<bean:message bundle="servermgrResources" key="copypacket.dummyesponseparameters"/>
												</td>
												<td class="tblheader-bold" align="right" width="15px" >
													<img id="dummyresponseImage" src="images/top-level.jpg" onclick="toggleTable('dummyresponse')" />
												</td>
											</tr>
											
											<tr>
												<td class="labeltext" style="padding-right: 10px; padding-left: 10px; padding-top: 4px; padding: bottom:4px;"></td>
											</tr>
											<tr>
												<td style="padding-right: 25px;padding-bottom: 2px">
													<table align="right" id="dummyresponse" cellpadding="0" cellspacing="0" class="box" width="95%" border="0" >
														<tr>
															<td align=left class=tblheader valign=top width="50%" >
																<bean:message bundle="servermgrResources" key="copypacket.dummyesponseparameters.outfield"/>
															</td>
															<td align=left class=tblheader valign=top width="50%">
																<bean:message bundle="servermgrResources" key="copypacket.dummyesponseparameters.value"/>
															</td>
															
														</tr>
														<logic:iterate id="dummyResponse" name="copyPacketTranslationConfBean" property="dummyParameterData" type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketDummyResponseParameterData">
														<tr>
															<td class="tblrows" width="50%"><bean:write name="dummyResponse" property="outField"/>  &nbsp;</td>
															<td class="tblrows" style="border-right: none"><bean:write name="dummyResponse" property="value"/> &nbsp;</td>
															
														</tr>
								
														</logic:iterate> 
														<logic:empty name="copyPacketTranslationConfBean" property="dummyParameterData" >
															<td colspan="2" align="center">No Records Found</td>
														</logic:empty> 
													</table>
												</td>
											</tr>
											
											
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</td>
			</tr>
		</table>
	</body>
</html>