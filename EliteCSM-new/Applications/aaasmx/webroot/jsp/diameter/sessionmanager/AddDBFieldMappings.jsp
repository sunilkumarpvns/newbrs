<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.elitecore.core.serverx.sessionx.FieldMapping" %>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData" %>
<%@page import="com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager" %>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData" %>
<%@page import="java.util.List" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>
<%@taglib prefix="ec" uri="/elitetags" %>

<%  		
	String index = request.getParameter("mappingIndexParam");
	String toggleMappingMethodCall ="toggleMappingDiv('"+index+"')";
	String removeCompMethodCall = "removeComponent(table"+index+")";
	String  mappingNameId = "mappingName"+index ;
			
	DiameterSessionManagerBLManager diameterSessionManagerBLManager = new DiameterSessionManagerBLManager();
	List<DiameterSessionManagerData> diameterSessionManagerDataList = diameterSessionManagerBLManager.getDiameterSessionManagerDatas();
	
	String  mappingName = request.getParameter("mappingName") != null ? request.getParameter("mappingName") : StringUtils.EMPTY; 
%>
<table width="98%" cellpadding="0" cellspacing="0" id="table<%=index%>" class="box" style="margin-top: 10px;">
	<tr>
		<td>
			<table width="100%" cellpadding="0" cellspacing="0">
				<tr>
					<td align=left class=tblheader-bold valign=top colspan=2 style="border-style: solid;border-width: 1px;border-color: #D9E6F6;">
						<label class="mappingLabel">Mapping-<%=index%></label> 
					</td>
					<td class="tblheader-bold" align="right" width="15px" style="border-style: solid;border-width: 1px;border-color: #D9E6F6;">
						<img src='<%=request.getContextPath()%>/images/minus.jpg' class='delete' height='15' onclick="<%=removeCompMethodCall%>" />
					</td>
					<td class="tblheader-bold" align="right" width="15px" style="border-style: solid;border-width: 1px;border-color: #D9E6F6;">
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
							<table width="100%" cellpadding="0" cellspacing="0" style="padding-top: 10px;">
								<tr>
									<td class=captiontext valign=top colspan=1 width="18%">
										<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.dbfieldmappingname" /> 
										<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.dbfieldmappingname" header="diameter.sessionmanager.dbfieldmappingname"/>
									</td>
									<td align=left class=labeltext valign=top style="padding-bottom: 10px;">
										<input type="text" name="<%=mappingNameId%>" id="<%=mappingNameId%>" class="mappingNameClass" style="width: 219px" value="<%=mappingName%>" maxlength="20"/>
										<font color="#FF0000"> *</font>
										&nbsp;
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td colspan="3" class="captiontext">
										<table width="97%" id="mappingtbl" cellpadding="0" cellspacing="0" border="0">
											<tr>
												<td align="left"  valign="top">
													<input type="button" name="c_btnAddMapping" onclick='addDbFieldMapping("dbFieldMappingTable<%=index%>","dbFieldMappingTemplate<%=index%>");' id="btnAddMapping" value="Add Mapping" class="light-btn"/>&nbsp;		
													<input type="button" name="c_btnAddMapping" onclick="addSessionManagerMappingTemplate('dbFieldMappingTable<%=index%>','defaultTemplate<%=index%>');" id="btnAddMapping" value="Add Template" class="light-btn" tabindex="18"/>
													<select class="labeltext" name="defaultTemplate" id="defaultTemplate<%=index%>" class="defaultTemplate" tabindex="19">
																<option value="NONE">--Select--</option>
																<option value="Radius">Radius</option>
																<option value="Diameter-NAS">Diameter-NAS</option>
																<option value="Diameter-CC">Diameter-CC</option>
																<% if(diameterSessionManagerDataList != null){
																	for(DiameterSessionManagerData data : diameterSessionManagerDataList)	{%>
																		<optgroup label='<%=data.getName()%>'>
																			<%if(data.getDiameterSessionManagerMappingData() != null){ %>
																				<%for(DiameterSessionManagerMappingData mappingData : data.getDiameterSessionManagerMappingData()){ %>
																					<option value='<%=mappingData.getMappingId()%>'>
																							<%=mappingData.getMappingName()%>
																						</option>
																					<%}%>
																				<%}%>
																		</optgroup>
																	<%}
																} %>
													</select>
												</td>
											</tr>
											<tr>
												<td>
													<table width="100%" cellspacing="0" cellpadding="0" border="0" id="dbFieldMappingTable<%=index%>" class="dbFieldMappingTable">
														<tr>
															<td align="left" class="tblheader" valign="top" width="23.75%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.dbfieldname" />
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.dbfieldname" header="diameter.sessionmanager.dbfieldname"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="23.75%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.refereingattribute" />
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.refereingattribute" header="diameter.sessionmanager.refereingattribute"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="23.75%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.datatype" /> 
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.datatype" header="diameter.sessionmanager.datatype"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="23.75%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.defaultvalue" />
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.defaultvalue" header="diameter.sessionmanager.defaultvalue"/>
															</td>
															<td align="left" class="tblheader" valign="top"width="5%">Remove</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td class=labeltext>&nbsp;</td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<!-- Mapping Table Row template -->
<table style="display:none;" id="dbFieldMappingTemplate<%=index%>">
	<tr>
		<td class="allborder"><input  class="dbFieldName noborder" autocomplete="off" type="text" name="dbFieldName<%=index%>"   maxlength="1000" size="28" style="width:100%" /></td>
		<td class="tblrows"><input class="referringAttribute noborder" autocomplete="off"  type="text" name="referringAttribute<%=index%>"  maxlength="1000" size="28" style="width:100%"/></td>
		<td class="tblrows">
			<select class="dataType" name="dataType<%=index%>" id="dataType<%=index%>" size="1" style="width: 100%;height: 100%;">
				<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
				<option value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</option>
			</select>
		</td>
		<td class="tblrows"><input class="noborder defaultValue" type="text" name="defaultValue<%=index%>"  maxlength="1000" size="30" style="width:100%" /></td>
		<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" /></td>
	</tr>
</table> 