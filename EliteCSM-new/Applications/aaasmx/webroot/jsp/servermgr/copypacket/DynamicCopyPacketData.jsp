<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean"
	prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic"
	prefix="logic"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested"
	prefix="nested"%>
<%@ taglib uri="/elitecore" prefix="elitecore"%>
<%@taglib prefix="ec" uri="/elitetags" %>


<%
String basePath = request.getContextPath();	
String index = request.getParameter("mappingIndexParam");
	String defaultMappingMethodCall = "setDefaultMapping(" + index + ")";
	String reqMappingMethodCall = "addNewMapping('requestMapping" + index + "')";
	String respMappingMethodCall = "addNewMapping('responseMapping" + index + "')";
	String toggleMappingMethodCall = "toggleMappingDiv('" + index + "')";
	String removeCompMethodCall = "removeComponent(table" + index + ")";
	String mappingNameId = "mappingName" + index;
	String inExpressionId = "inExpression" + index;

	String mappingName = request.getParameter("mappingName") != null ? request
			.getParameter("mappingName") : StringUtils.EMPTY;
	String inExpression = request.getParameter("inExpression") != null ? request
			.getParameter("inExpression") : StringUtils.EMPTY;

	String defaultMapping = request.getParameter("defaultMapping");
	String dummyResponse = request.getParameter("dummyResponse");
	String translationTo = request.getParameter("translationTo");
	String translationFrom = request.getParameter("translationFrom");
	String translationToId = request.getParameter("translationToId");
	String translationFromId = request
			.getParameter("translationFromId");
	translationTo = translationTo == null ? "" : "(" + translationTo + ")";
	translationFrom = translationFrom == null ? "" : "(" + translationFrom + ")";
	System.out.println("inExpression :" + inExpression);
	System.out.println("Encoding inMessage : " + StringEscapeUtils.escapeHtml(inExpression));
	if (defaultMapping != null
			&& defaultMapping.equals(Boolean.toString(true))) {
		defaultMapping = "checked";
	} else {
		defaultMapping = "";
	}
	 if (dummyResponse != null
			&& dummyResponse.equals(Boolean.toString(true))) {
		dummyResponse = "checked";
	} else {
		dummyResponse = "";
	} 
%>



<table width="100%" cellpadding="0" cellspacing="0" id="table<%=index%>">
	<tr>
		<td>
			<table width="100%" cellpadding="0" cellspacing="0">
				<tr>
					<td align=left class=tblheader-bold valign=top colspan=2><label
						class="mappingLabel">Mapping-<%=index%></label></td>
					<td class="tblheader-bold" align="right" width="15px"><img
						src='<%=request.getContextPath()%>/images/minus.jpg'
						class='delete' height='15' onclick="<%=removeCompMethodCall%>" />
					</td>
					<td class="tblheader-bold" align="right" width="15px"><img
						alt="bottom" id="toggleImageElement<%=index%>"
						onclick="<%=toggleMappingMethodCall%>"
						src="<%=request.getContextPath()%>/images/top-level.jpg" /></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<table width="100%" cellpadding="0" cellspacing="0">
				<tr>
					<td width="100%">
						<div id="toggleDivElement<%=index%>">
							<table width="100%" cellpadding="0" cellspacing="0">
								<tr>
									<td class=captiontext valign=top colspan=1 width="30%">
										<bean:message bundle="servermgrResources" 
											key="copypacket.mappingname" />
												<ec:elitehelp headerBundle="servermgrResources" 
													text="copypacket.mappingname" 
														header="copypacket.mappingname"/>	
									</td>

									<td align=left class=labeltext valign=top><input
										type="text" name="<%=mappingNameId%>" id="<%=mappingNameId%>"
										class="mappingNameClass" style="width: 219px"
										value="<%=mappingName%>" maxlength="64" /> <font
										color="#FF0000"> *</font></td>
									<td align=left class=labeltext valign=top colspan=2><input
										type="checkbox" <%=dummyResponse%>
										name="dummyResponseChkBox<%=index%>"
										id="dummyResponseChkBox<%=index%>" /> <bean:message
											bundle="servermgrResources" key="copypacket.dummyresponse" />
									</td>
								</tr>
								<tr>
									<td class=captiontext valign=top colspan=1 width="30%">
										<bean:message bundle="servermgrResources" 
											key="copypacket.inexpression" />
												<ec:elitehelp headerBundle="servermgrResources" 
													text="copypacket.inexpression" 
														header="copypacket.inexpression"/>
															<%=translationFrom%> 
									</td>
									<td align=left class=labeltext valign=top colspan=1><textarea
										name="<%=inExpressionId%>" id="<%=inExpressionId%>"
										type="text" class="mappingInMessage autoSuggest" cols="1" rows="1" 
										style="min-width:219px; min-height:25px; height:25px;" maxlength="200" > <%=StringEscapeUtils.escapeHtml(inExpression)%></textarea><font
										color="#FF0000"> *</font></td>
									<td align=left class=labeltext valign=top colspan=2><input
										type="checkbox" <%=defaultMapping%>
										onclick="<%=defaultMappingMethodCall%>"
										name="defaultMappingChkBox<%=index%>"
										id="defaultMappingChkBox<%=index%>" value="true" class="defaultMappingCSS" /> <bean:message
											bundle="servermgrResources" key="copypacket.defaultmapping" />
									</td>

								</tr>

								<tr>
									<td colspan="7">
										<table width="100%">
											<tr>
												<td width="100%">
													<div id="div<%=index%>" style="display: none;">
														<table width="100%" cellpadding="0" cellspacing="0">
															<tr>
																<td class=labeltext valign=top colspan='4' align=right>
																	<table border="0" width="100%" cellpadding="0"
																		cellspacing="0">
																		<tr>
																			<td class=labeltext>&nbsp;</td>
																		</tr>

																		<tr>
																			<td align="right" class="labeltext" valign="top"
																				colspan="4">
																				<table cellpadding="0" cellspacing="0" class="box"
																					width="100%">
																					<tr>
																						<td class="table-header">Request Parameters</td>
																					</tr>
																					<tr>
																						<td class="labeltext"
																							style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
																					</tr>
																					<tr>
																						<td class="labeltext"><input type="button"
																							class="light-btn" value="Add New Mapping"
																							tabindex="4"
																							onclick="addNewMapping('requestMapping<%=index%>');" /></td>

																					</tr>
																					<tr>
																						<td class="labeltext"
																							style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
																					</tr>

																					<tr>
																						<td class="labeltext">
																							<table id="requestMapping<%=index%>"
																								cellpadding="0" cellspacing="0" border="0"
																								width="100%">
																								<thead width="100%">
																									<tr>
																										<td align=left class=tblheader valign=top" width="11%">
																											<bean:message bundle="servermgrResources"
																												key="copypacket.parameter.operation" /> 
																													<ec:elitehelp headerBundle="servermgrResources" 
																														text="copypacket.parameter.operation" 
																															header="copypacket.parameter.operation"/>
																										</td>
																										<td align=left class=tblheader valign=top" width="16%">
																											<bean:message bundle="servermgrResources"
																												key="copypacket.parameter.checkexpression" />
																													<ec:elitehelp headerBundle="servermgrResources" 
																														text="copypacket.parameter.checkexpression" 
																															header="copypacket.parameter.checkexpression"/>
																										</td>
																										<td align=left class=tblheader valign=top" width="16%">
																											<bean:message bundle="servermgrResources"
																												key="copypacket.parameter.destinationexpression" />
																													<ec:elitehelp headerBundle="servermgrResources" 
																														text="copypacket.parameter.destinationexpression" 
																															header="copypacket.parameter.destinationexpression"/>
																										</td>
																										<td align=left class=tblheader valign=top width="15%">
																											<bean:message bundle="servermgrResources"
																												key="copypacket.parameter.sourceexpression" />
																													<ec:elitehelp headerBundle="servermgrResources" 
																														text="copypacket.parameter.sourceexpression" 
																															header="copypacket.parameter.sourceexpression"/>
																										</td>
																										<td align=left class=tblheader valign=top width="15%">
																											<bean:message bundle="servermgrResources"
																												key="copypacket.parameter.defaultvalue" />
																													<ec:elitehelp headerBundle="servermgrResources" 
																														text="copypacket.parameter.defaultvalue" 
																															header="copypacket.parameter.defaultvalue"/>
																										</td>
																										<td align=left class=tblheader valign=top width="15%">
																											<bean:message bundle="servermgrResources"
																												key="copypacket.parameter.valuemapping" />
																													<ec:elitehelp headerBundle="servermgrResources" 
																														text="copypacket.parameter.valuemapping" 
																															header="copypacket.parameter.valuemapping"/>
																										</td>
																										<td align="left" class="tblheader"
																											valign="top" width="4%">Remove</td>
																										<td align="left" class="tblheader"
																											valign="top" width="4%">Expand</td>
																										<td align="left" class="tblheader"
																											valign="top" width="4%">Order</td>
																									</tr>
																								</thead>
																								<tbody></tbody>

																							</table>
																						</td>
																					</tr>
																					<tr>
																						<td class="labeltext"
																							style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
																					</tr>
																				</table>
																			</td>
																		</tr>

																		<tr>
																			<td class="labeltext">&nbsp;</td>
																		</tr>
																		<tr>
																			<td colspan="4" align="right">
																				<table cellpadding="0" cellspacing="0" class="box"
																					width="100%">
																					<tr>
																						<td class="table-header">Response Parameter 
																					</tr>
																					<tr>
																						<td class="labeltext"
																							style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
																					</tr>
																					<tr>
																						<td class="labeltext"><input type="button"
																							class="light-btn" value="Add New Mapping"
																							tabindex="4"
																							onclick="addNewMapping('responseMapping<%=index%>');" /></td>

																					</tr>

																					<tr>
																						<td class="labeltext"
																							style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
																					</tr>
																					<tr>
																						<td class="labeltext">
																							<table id="responseMapping<%=index%>"
																								cellpadding="0" cellspacing="0" border="0"
																								width="100%">
																								<thead width="100%">
																									<tr>
																										<td align=left class=tblheader valign=top width="11%">
																											<bean:message bundle="servermgrResources"
																												key="copypacket.parameter.operation" /> 
																													<ec:elitehelp headerBundle="servermgrResources" 
																														text="copypacket.parameter.operation" 
																															header="copypacket.parameter.operation"/>
																										</td>
																										<td align=left class=tblheader valign=top width="16%">
																											<bean:message bundle="servermgrResources"
																												key="copypacket.parameter.checkexpression" />
																													<ec:elitehelp headerBundle="servermgrResources" 
																														text="copypacket.parameter.checkexpression" 
																															header="copypacket.parameter.checkexpression"/>
																										</td>
																										<td align=left class=tblheader valign=top width="16%">
																											<bean:message bundle="servermgrResources"
																												key="copypacket.parameter.destinationexpression" />
																													<ec:elitehelp headerBundle="servermgrResources" 
																														text="copypacket.parameter.destinationexpression" 
																															header="copypacket.parameter.destinationexpression"/>
																										</td>
																										<td align=left class=tblheader valign=top width="15%">
																											<bean:message bundle="servermgrResources"
																												key="copypacket.parameter.sourceexpression" />
																													<ec:elitehelp headerBundle="servermgrResources" 
																														text="copypacket.parameter.sourceexpression" 
																															header="copypacket.parameter.sourceexpression"/>
																										</td>
																										<td align=left class=tblheader valign=top width="15%">
																											<bean:message bundle="servermgrResources"
																												key="copypacket.parameter.defaultvalue" />
																													<ec:elitehelp headerBundle="servermgrResources" 
																														text="copypacket.parameter.defaultvalue" 
																															header="copypacket.parameter.defaultvalue"/>
																										</td>
																										<td align=left class=tblheader valign=topwidth="15%">
																											<bean:message bundle="servermgrResources"
																												key="copypacket.parameter.valuemapping" />
																													<ec:elitehelp headerBundle="servermgrResources" 
																														text="copypacket.parameter.valuemapping" 
																															header="copypacket.parameter.valuemapping"/>
																										</td>
																										<td align="left" class="tblheader"
																											valign="top" width="4%">Remove</td>
																										<td align="left" class="tblheader"
																											valign="top" width="4%">Expand</td>
																										<td align="left" class="tblheader"
																											valign="top" width="4%">Order</td>
																									</tr>
																								</thead>
																								<tbody></tbody>

																							</table>
																						</td>
																					</tr>
																					<tr>
																						<td class="labeltext"
																							style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
																					</tr>
																				</table>

																			</td>
																		</tr>
																	</table>
																	</div>
																</td>
															</tr>
															<tr>
																<td class="labeltext"
																	style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
															</tr>
														</table>
												</td>
											</tr>
										</table>
										</div>
									</td>
								</tr>
							</table>
					</td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
</table>
</td>
</tr>
<tr>
	<td class=labeltext>&nbsp;</td>
</tr>
</table>

<script>
setDefaultMapping('<%=index%>');
</script>