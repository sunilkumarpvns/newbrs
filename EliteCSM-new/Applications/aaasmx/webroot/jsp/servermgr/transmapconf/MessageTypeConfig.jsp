<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>
<%@taglib prefix="ec" uri="/elitetags" %>
<%-- <%@ include file="/jsp/core/includes/common/Header.jsp"%> --%>


<%  		
	String index = request.getParameter("mappingIndexParam"); 
			String defaultMappingMethodCall = "setDefaultMapping("+index+")";
			String reqMappingMethodCall = "addNewMapping('requestTranslationMapTable"+index+"')";
			String respMappingMethodCall ="addNewMapping('responseTranslationMapTable"+index+"')";
			String toggleMappingMethodCall ="toggleMappingDiv('"+index+"')";
			String removeCompMethodCall = "removeComponent(table"+index+")";
	String  mappingNameId = "mappingName"+index ;
			String inMessageId = "inMessageMapping"+index;
			String outMessageId = "outMessageMapping"+index;
			
	String  mappingName = request.getParameter("mappingName") != null ? request.getParameter("mappingName") : StringUtils.EMPTY; 
	String inMessage =    request.getParameter("inMessage") != null ? request.getParameter("inMessage") : StringUtils.EMPTY;;
	String outMessage =   request.getParameter("outMessage") != null ? request.getParameter("outMessage") : StringUtils.EMPTY;;
			
	
			String defaultMapping = request.getParameter("defaultMapping");
			String dummyResponse = request.getParameter("dummyResponse");
			String translationTo = request.getParameter("translationTo");
			String translationFrom = request.getParameter("translationFrom");
			String translationToId = request.getParameter("translationToId");
			String translationFromId = request.getParameter("translationFromId");
			translationTo = translationTo==null?"": "("+translationTo+")";
			translationFrom = translationFrom==null?"": "("+translationFrom+")";
			System.out.println("inMessage :"+inMessage);
			System.out.println("outMessage :"+outMessage);
			System.out.println("Encoding inMessage : "+StringEscapeUtils.escapeHtml(inMessage));	
			System.out.println("Encoding outMessage: "+StringEscapeUtils.escapeHtml(outMessage));	
	
			if(defaultMapping==null || defaultMapping.equals("Y")){
				defaultMapping="checked";
			}else{
				defaultMapping="";
			}
			if(dummyResponse != null && dummyResponse.equals(Boolean.toString(true))){
				dummyResponse="checked";
			} else {
				dummyResponse = ""; 
			}
		%>


<table width="100%" cellpadding="0" cellspacing="0" id="table<%=index%>">
	<tr>
		<td>
			<table width="100%" cellpadding="0" cellspacing="0">
				<tr>
					<td align=left class=tblheader-bold valign=top colspan=2>
						<label class="mappingLabel">Mapping-<%=index%></label> 
					</td>
					<td class="tblheader-bold" align="right" width="15px">
						<img src='<%=request.getContextPath()%>/images/minus.jpg' class='delete' height='15' onclick="<%=removeCompMethodCall%>" />
					</td>
					<td class="tblheader-bold" align="right" width="15px">
						<img alt="bottom" id="toggleImageElement<%=index%>" onclick="<%=toggleMappingMethodCall%>" src="<%=request.getContextPath()%>/images/top-level.jpg" />
					</td>
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
											key="translationmapconf.mappingname" /> 
												<ec:elitehelp headerBundle="servermgrResources" 
													text="translationmapconf.mappingname" 
														header="translationmapconf.mappingname"/>
									</td>
									<td align=left class=labeltext valign=top >
										<input type="text" name="<%=mappingNameId%>" id="<%=mappingNameId%>" class="mappingNameClass" style="width: 219px" value="<%=mappingName%>" maxlength="64"/>
										<font color="#FF0000"> *</font>
									</td>
								</tr>
								<tr>
									<td class=captiontext valign=top colspan=1 width="30%">
										<bean:message bundle="servermgrResources" 
											key="translationmapconf.inmessage" /> <%=translationFrom %>
												<ec:elitehelp headerBundle="servermgrResources" 
													text="translationmapconf.inmessage" 
														header="translationmapconf.inmessage"/>
									</td>
									<td align=left class=labeltext valign=top colspan=1>
										<input name="<%=inMessageId%>" id="<%=inMessageId%>" type="text" class="mappingInMessage" value="<%=StringEscapeUtils.escapeHtml(inMessage)%>" style="width: 219px" maxlength="200">
										<font color="#FF0000"> *</font>
									</td>
									<td align=left class=labeltext valign=top colspan=2>
										<input type="checkbox" <%=dummyResponse %> name="dummyResponseChkBox<%=index%>" id="dummyResponseChkBox<%=index%>" /> 
										<bean:message bundle="servermgrResources" key="translationmapconf.dummyresponse" /> 
									</td>

								</tr>
								<tr>
									<td class=captiontext valign=top colspan=1>
										<bean:message bundle="servermgrResources" 
											key="translationmapconf.outmessage" /> 
												<%=translationTo %> 
													<ec:elitehelp headerBundle="servermgrResources" 
														text="translationmapconf.outmessage" 
															header="translationmapconf.outmessage"/>
									</td>
									<td align=left class=labeltext valign=top colspan=1>
										<input name="<%=outMessageId%>" id="<%=outMessageId%>" type="text" class="mappingOutMessage" value="<%=StringEscapeUtils.escapeHtml(outMessage)%>" style="width: 219px" maxlength="200"></td>
									<td align=left class=labeltext valign=top colspan=2>
										<input type="checkbox" <%=defaultMapping%> onclick="<%=defaultMappingMethodCall%>" name="defaultMappingChkBox<%=index%>" id="defaultMappingChkBox<%=index%>" value="Y" /> 
										<bean:message bundle="servermgrResources" key="translationmapconf.defaultmapping" /> 
									</td>
								</tr>
								<tr>
									<td colspan="4">
										<table width="100%">
											<tr>
												<td width="100%">
													<div id="div<%=index%>" style="display: none;">
														<table width="100%" cellpadding="0" cellspacing="0">
															<tr>
																<td class=labeltext valign=top colspan='4' align=right>
																	<table border="0" width="95%" cellpadding="0" cellspacing="0">
																		<tr>
																			<td class=labeltext>&nbsp;</td>
																		</tr>
																		<tr>
																			<td align=left class=labeltext valign=top colspan=4 width=100%>
																				<table cellpadding=0 cellspacing=0 class="box" width=100%>
																					<tr>
																						<td class=table-header>Request Parameters</td>
																					</tr>
																					<tr>
																						<td class=labeltext>&nbsp;</td>
																					</tr>
																					<tr class="captiontext">
																						<td class="captiontext"><input type=button class=light-btn onclick="<%=reqMappingMethodCall%>" value="Add New Mapping" /></td>
																					</tr>
																					<tr>
																						<td class=labeltext>&nbsp;</td>
																					</tr>
																					<tr>
																						<td class=captiontext>
																							<table id="requestTranslationMapTable<%=index%>" cellpadding=0 cellspacing=0 border=0 width=100%>
																								<tr>
																									<td align=left class=tblheader valign=top>
																										<bean:message bundle="servermgrResources" key="translationmapconf.checkexpression" />
																											<ec:elitehelp headerBundle="servermgrResources" 
																												text="transmapping.checkexp" 
																													header="translationmapconf.checkexpression"/>
																									</td>
																									<td align=left class=tblheader valign=top>
																										<bean:message bundle="servermgrResources" 
																											key="translationmapconf.mappingexression" />
																												<ec:elitehelp headerBundle="servermgrResources" 
																													text="transmapping.mappingexp" 
																														header="translationmapconf.mappingexression"/>
																									</td>
																									<td align=left class=tblheader valign=top>
																										<bean:message bundle="servermgrResources" 
																											key="translationmapconf.defaultvalue" />
																												<ec:elitehelp headerBundle="servermgrResources" 
																													text="transmapping.defaultvalue" 
																														header="translationmapconf.defaultvalue"/>
																									</td>
																									<td align=left class=tblheader valign=top>
																										<bean:message bundle="servermgrResources" 
																											key="translationmapconf.valuemapping" />
																												<ec:elitehelp headerBundle="servermgrResources" 
																													text="transmapping.valuemapping" 
																														header="translationmapconf.valuemapping"/> 
																									</td>
																									<td align=left class=tblheader valign=top width="5%">Remove</td>
																								</tr>
																							</table>
																						</td>
																					</tr>
																					<tr>
																						<td class=labeltext>&nbsp;</td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td class=labeltext>&nbsp;</td>
																		</tr>
																		<tr>
																			<td colspan=4>
																				<table cellpadding=0 cellspacing=0 class=box width=100%>
																					<tr>
																						<td class=table-header>Response Parameters</td>
																					</tr>
																					<tr>
																						<td class=labeltext>&nbsp;</td>
																					</tr>
																					<tr>
																						<td class=captiontext>
																							<input type=button class=light-btn onclick="<%=respMappingMethodCall%>" value="Add New Mapping" />
																						</td>
																					</tr>
																					<tr>
																						<td class=labeltext>&nbsp;</td>
																					</tr>
																					<tr>
																						<td class=labeltext>
																							<table id="responseTranslationMapTable<%=index%>" cellpadding=0 cellspacing=0 border=0 width=100%>
																								<tr>
																									<td align=left class=tblheader valign=top>
																										<bean:message bundle="servermgrResources" 
																											key="translationmapconf.checkexpression" />
																												<ec:elitehelp headerBundle="servermgrResources" 
																													text="transmapping.checkexp" 
																														header="translationmapconf.checkexpression"/>
																									</td>
																									<td align=left class=tblheader valign=top>
																										<bean:message bundle="servermgrResources" 
																											key="translationmapconf.mappingexression" />
																												<ec:elitehelp headerBundle="servermgrResources" 
																													text="transmapping.mappingexp" 
																														header="translationmapconf.mappingexression"/>
																									</td>
																									<td align=left class=tblheader valign=top>
																										<bean:message bundle="servermgrResources" 
																											key="translationmapconf.defaultvalue" />
																												<ec:elitehelp headerBundle="servermgrResources" 
																													text="transmapping.defaultvalue" 
																														header="translationmapconf.defaultvalue"/>
																									</td>
																									<td align=left class=tblheader valign=top>
																										<bean:message bundle="servermgrResources" 
																											key="translationmapconf.valuemapping" />
																												<ec:elitehelp headerBundle="servermgrResources" 
																													text="transmapping.valuemapping" 
																														header="translationmapconf.valuemapping"/>
																									<td align=left class=tblheader valign=top width="5%">Remove</td>
																								</tr>
																							</table>
																						</td>
																					</tr>
																					<tr>
																						<td class=labeltext>&nbsp;</td>
																					</tr>
																				</table>
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