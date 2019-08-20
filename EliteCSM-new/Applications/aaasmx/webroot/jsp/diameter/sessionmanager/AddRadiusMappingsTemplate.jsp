<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.SessionManagerFieldMappingData" %>
<%@page import="com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.Set" %>
<%@page import="com.elitecore.core.serverx.sessionx.FieldMapping" %>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>
<%@taglib prefix="ec" uri="/elitetags" %>

<%  		
	String index = request.getParameter("mappingIndexParam"); 
	String defaultTemplateId = request.getParameter("defaultTemplateValue");

	String toggleMappingMethodCall ="toggleMappingDiv('"+index+"')";
	String removeCompMethodCall = "removeComponent(table"+index+")";
	String  mappingNameId = "mappingName"+index ;
	
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
									<td align=left class=labeltext valign=top >
										<input type="text" name="<%=mappingNameId%>" id="<%=mappingNameId%>" class="mappingNameClass" style="width: 219px" value="" maxlength="27"/>
										<font color="#FF0000"> *</font>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="captiontext">
										<table width="97%" id="mappingtbl" cellpadding="0" cellspacing="0" border="0">
											<tr>
												<td>
													<table width="100%" cellspacing="0" cellpadding="0" border="0" id="dbFieldMappingTable<%=index%>" class="dbFieldMappingTable">
														<tr>
															<td align="left" class="tblheader" valign="top" width="19%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.fields" />
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.fields" header="diameter.sessionmanager.fields"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="19%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.dbfieldname" />
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.dbfieldname" header="diameter.sessionmanager.dbfieldname"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="19%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.refereingattribute" />
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.refereingattribute" header="diameter.sessionmanager.refereingattribute"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="19%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.datatype" /> 
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.datatype" header="diameter.sessionmanager.datatype"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="19%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.defaultvalue" />
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.defaultvalue" header="diameter.sessionmanager.defaultvalue"/>
															</td>
														</tr>
														<tr>
															<td class="allborder">
																<input class="noborder" class="fields" type="text" name="fields<%=index%>" id="fields<%=index%>" value="Session ID" maxlength="1000" size="28" style="width:100%" />
															</td>
															<td class="tblrows"><input class="noborder" class="dbFieldName" type="text" name="dbFieldName<%=index%>" id="dbFieldName<%=index%>" value="ACCT_SESSION_ID" maxlength="1000" size="28" style="width:100%" onkeyup="setDbFieldsMapping(this);"/></td>
															<td class="tblrows"><input class="noborder" class="referringAttribute" type="text" name="referringAttribute<%=index%>"  id="referringAttribute<%=index%>" value="0:44" maxlength="1000" size="28" style="width:100%"  onkeyup="setReferringAttributes(this);" /></td>
															<td>
																<select name="dataType<%=index%>" id="dataType<%=index%>" size="1" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width: 100%;color: #6E6E6E;" disabled="disabled">
																	<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
																	<option value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</option>
																</select>
															</td>
															<td class="tblrows"><input class="noborder" type="text" name="defaultValue<%=index%>" id="defaultValue<%=index%>" maxlength="1000" size="30" style="width:100%" /></td>
														</tr>
														<tr>
															<td class="allborder">
																<input class="noborder" class="fields" type="text" name="fields<%=index%>" id="fields<%=index%>" value="PDP Type" maxlength="1000" size="28" style="width:100%" />
															</td>
															<td class="tblrows"><input class="noborder" class="dbFieldName" type="text" name="dbFieldName<%=index%>" id="dbFieldName<%=index%>" value="NAS_PORT_TYPE" maxlength="1000" size="28" style="width:100%"  onkeyup="setDbFieldsMapping(this);"/></td>
															<td class="tblrows"><input class="noborder" class="referringAttribute" type="text" name="referringAttribute<%=index%>"  id="referringAttribute<%=index%>" value="0:61" maxlength="1000" size="28" style="width:100%" onkeyup="setReferringAttributes(this);" /></td>
															<td>
																<select name="dataType<%=index%>" id="dataType<%=index%>" size="1" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width: 100%;color: #6E6E6E;" disabled="disabled" >
																	<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
																	<option value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</option>
																</select>
															</td>
															<td class="tblrows"><input class="noborder" type="text" name="defaultValue<%=index%>" id="defaultValue<%=index%>" maxlength="1000" size="30" style="width:100%" /></td>
														</tr>
														<tr>
															<td class="allborder">
																<input class="noborder" class="fields" type="text" name="fields<%=index%>" id="fields<%=index%>" value="Session Timeout" maxlength="1000" size="28" style="width:100%" />
															</td>
															<td class="tblrows"><input class="noborder" class="dbFieldName" type="text" name="dbFieldName<%=index%>" id="dbFieldName<%=index%>" value="SESSION_TIMEOUT" maxlength="1000" size="28" style="width:100%"  onkeyup="setDbFieldsMapping(this);"/></td>
															<td class="tblrows"><input class="noborder" class="referringAttribute" type="text" name="referringAttribute<%=index%>"  id="referringAttribute<%=index%>" value="$RES(0:27)" maxlength="1000" size="28" style="width:100%" onkeyup="setReferringAttributes(this);" /></td>
															<td>
																<select name="dataType<%=index%>" id="dataType<%=index%>" size="1" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width: 100%;color: #6E6E6E;" disabled="disabled" >
																	<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
																	<option value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</option>
																</select>
															</td>
															<td class="tblrows"><input class="noborder" type="text" name="defaultValue<%=index%>" id="defaultValue<%=index%>" maxlength="1000" size="30" style="width:100%" /></td>
														</tr>
														<tr>
															<td class="allborder">
																<input class="noborder" class="fields" type="text" name="fields<%=index%>" id="fields<%=index%>" value="AAA ID" maxlength="1000" size="28" style="width:100%"  />
															</td>
															<td class="tblrows"><input class="noborder" class="dbFieldName" type="text" name="dbFieldName<%=index%>" id="dbFieldName<%=index%>" value="AAA_ID" maxlength="1000" size="28" style="width:100%"  onkeyup="setDbFieldsMapping(this);"/></td>
															<td class="tblrows"><input class="noborder" class="referringAttribute" type="text" name="referringAttribute<%=index%>"  id="referringAttribute<%=index%>" value="21067:143" maxlength="1000" size="28" style="width:100%"  onkeyup="setReferringAttributes(this);" /></td>
															<td>
																<select name="dataType<%=index%>" id="dataType<%=index%>" size="1" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width: 100%;color: #6E6E6E;" disabled="disabled" >
																	<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
																	<option value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</option>
																</select>
															</td>
															<td class="tblrows"><input class="noborder" type="text" name="defaultValue<%=index%>" id="defaultValue<%=index%>" maxlength="1000" size="30" style="width:100%" /></td>
														</tr>
														<tr>
															<td class="allborder">
																<input class="noborder" class="fields" type="text" name="fields<%=index%>" id="fields<%=index%>" value="NAS ID" maxlength="1000" size="28" style="width:100%"  />
															</td>
															<td class="tblrows"><input class="noborder" class="dbFieldName" type="text" name="dbFieldName<%=index%>" id="dbFieldName<%=index%>" value="NAS_IDENTIFIER" maxlength="1000" size="28" style="width:100%"  onkeyup="setDbFieldsMapping(this);"/></td>
															<td class="tblrows"><input class="noborder" class="referringAttribute" type="text" name="referringAttribute<%=index%>"  id="referringAttribute<%=index%>" value="0:32" maxlength="1000" size="28" style="width:100%" onkeyup="setReferringAttributes(this);" /></td>
															<td>
																<select name="dataType<%=index%>" id="dataType<%=index%>" size="1" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width: 100%;color: #6E6E6E;" disabled="disabled" >
																	<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
																	<option value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</option>
																</select>
															</td>
															<td class="tblrows"><input class="noborder" type="text" name="defaultValue<%=index%>" id="defaultValue<%=index%>" maxlength="1000" size="30" style="width:100%" /></td>
														</tr>
														<tr>
															<td class="allborder">
																<input class="noborder" class="fields" type="text" name="fields<%=index%>" id="fields<%=index%>" value="User Identity" maxlength="1000" size="28" style="width:100%"  />
															</td>
															<td class="tblrows"><input class="noborder" class="dbFieldName" type="text" name="dbFieldName<%=index%>" id="dbFieldName<%=index%>" value="USER_NAME" maxlength="1000" size="28" style="width:100%"  onkeyup="setDbFieldsMapping(this);"/></td>
															<td class="tblrows"><input class="noborder" class="referringAttribute" type="text" name="referringAttribute<%=index%>"  id="referringAttribute<%=index%>" value="0:1" maxlength="1000" size="28" style="width:100%"  onkeyup="setReferringAttributes(this);" /></td>
															<td>
																<select name="dataType<%=index%>" id="dataType<%=index%>" size="1" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width: 100%;color: #6E6E6E;" disabled="disabled" >
																	<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
																	<option value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</option>
																</select>
															</td>
															<td class="tblrows"><input class="noborder" type="text" name="defaultValue<%=index%>" id="defaultValue<%=index%>" maxlength="1000" size="30" style="width:100%"/></td>
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
		<td class="allborder"><input class="noborder" class="dbFieldName" type="text" name="dbFieldName<%=index%>" id="dbFieldName<%=index%>"  maxlength="1000" size="28" style="width:100%"  onkeyup="setDbFieldsMapping(this);"/></td>
		<td class="tblrows"><input class="noborder" class="referringAttribute" type="text" name="referringAttribute<%=index%>"  id="referringAttribute<%=index%>" maxlength="1000" size="28" style="width:100%"  onkeyup="setReferringAttributes(this);" /></td>
		<td>
			<select name="dataType<%=index%>" id="dataType<%=index%>" size="1" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width: 100%;color: #6E6E6E;" disabled="disabled">
				<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
				<option value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</option>
			</select>
		</td>
		<td class="tblrows"><input class="noborder" type="text" name="defaultValue<%=index%>" id="defaultValue<%=index%>" maxlength="1000" size="30" style="width:100%" /></td>
		<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" /></td>
	</tr>
</table> 