<%@page import="com.elitecore.core.serverx.sessionx.FieldMapping"%>
<%@page import="com.elitecore.aaa.diameter.conf.DiameterConcurrencyConfigurable.MandateryFieldConstants"%>
<%@page import="com.elitecore.aaa.diameter.conf.DiameterConcurrencyConfigurable.SessionOverrideActions"%>
<%@page import="com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData"%>
<%@page import="com.elitecore.elitesm.web.diameter.diameterconcurrency.form.DiameterConcurrencyForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.aaa.diameter.conf.DiameterConcurrencyConfigurable.DBFailureActions" %>

<%
	DiameterConcurrencyForm diameterConcurrencyForm = (DiameterConcurrencyForm)request.getAttribute("diameterConcurrencyForm");
	List<IDatabaseDSData> lstdatasource = (List<IDatabaseDSData>) request.getAttribute("lstDatasource");
	String basePath = request.getContextPath();
    String statusVal=(String)request.getParameter("status");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<script language="javascript" src="<%=request.getContextPath()%>/js/diameter/diameter-concurrency.js"></script>

<script>

var isValidName;



$(document).ready(function(){
	$('.additionalMappingTable td img.delete').live('click',function() {
		$(this).parent().parent().remove();
	});	
	setColumnsOnTextFields();
});

function validateName(val)
{
	var test1 = /(^[A-Za-z0-9-_]*$)/;
	var regexp =new RegExp(test1);
	if(regexp.test(val)){
		return true;
	}//04/19/2011
	return false;
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DIAMETER_CONCURRENCY%>',searchName,'create','','verifyNameDiv');
}

setTitle('<bean:message bundle="diameterResources" key="diameter.diameterconcurrency"/>');
</script>
<html>
<body onload="document.diameterConcurrencyForm.name.focus();">
	<html:form action="/createDiameterConcurrency">
		<html:hidden name="diameterConcurrencyForm" styleId="action" property="action" value="create" />
		<html:hidden name="diameterConcurrencyForm" styleId="lstFieldMapping" property="lstFieldMapping" />
		<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
			<tr>
				<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
				<td>
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
								<table cellpadding="0" cellspacing="0" border="0" width="100%">
									<tr>
										<td class="table-header">
											<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.create" />
										</td>
									</tr>
									<tr>
										<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
									</tr>
									<tr>
										<td colspan="3">
											<table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" cellSpacing="0" cellPadding="0" border="0">
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="diameterResources"  key="diameter.diameterconcurrency.name" /> 
														<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.name" header="diameter.diameterconcurrency.name"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="50%">
														<html:text styleId="name" tabindex="1" name="diameterConcurrencyForm" property="name" onblur="verifyName();" size="30" styleClass="flatfields" style="font-family: Verdana; width:250px " maxlength="64"  />
														<font color="#FF0000"> *</font>	
														<div id="verifyNameDiv" class="labeltext"></div>
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top" width="12%">
														<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.description" />
														<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.description" header="diameter.diameterconcurrency.description"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="2">
														<html:textarea styleId="description" name="diameterConcurrencyForm" property="description" cols="50" rows="4" style="width:250px" tabindex="2" />
													</td>
												</tr>
												
												<tr>
													<td align="left" class="captiontext" valign="top" width="12%">
														<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.datasource" />
														<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.datasource" header="diameter.diameterconcurrency.datasource"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="3">
															<%if (lstdatasource == null) {%> 
																<bean:define id="lstDatasource"	name="diameterConcurrencyForm" property="lstDatasource"></bean:define> 
															<%} %> 
							   								<html:select name="diameterConcurrencyForm" styleId="databaseDsId" property="databaseDsId" size="1" style="width:250px" tabindex="4" onchange="setColumnsOnTextFields();">
																<html:option value="0">
																	<bean:message bundle="sessionmanagerResources" key="sessionmanager.select" />
																</html:option>
																<html:options collection="lstDatasource" property="databaseId" labelProperty="name" /> 
															</html:select>
															<font color="#FF0000"> *</font>	
													</td>
												</tr>
												
												<tr>
													<td align="left" class="captiontext" valign="top" width="12%">
														<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.tablename" />
														<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.tablename" header="diameter.diameterconcurrency.tablename"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="2">
														<html:text styleId="tableName" name="diameterConcurrencyForm" property="tableName" style="width:250px" tabindex="5" maxlength="30" />
														<font color="#FF0000"> *</font>	
													</td>
												</tr>
												
												<tr>
													<td align="left" class="captiontext" valign="top" width="12%">
														<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.starttime" />
														<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.starttime" header="diameter.diameterconcurrency.starttime"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="2">
														<html:text styleId="startTimeField" name="diameterConcurrencyForm" property="startTimeField" styleClass="startTimeField" style="width:250px" tabindex="5" maxlength="30" />
														<font color="#FF0000"> *</font>	
													</td>
												</tr>
												
												<tr>
													<td align="left" class="captiontext" valign="top" width="12%">
														<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.lastupdatetime" />
														<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.lastupdatetime" header="diameter.diameterconcurrency.lastupdatetime"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="2">
														<html:text styleId="lastUpdateTimeField" name="diameterConcurrencyForm" property="lastUpdateTimeField" styleClass="lastUpdateTimeField" style="width:250px" tabindex="5" maxlength="30" />
														<font color="#FF0000"> *</font>	
													</td>
												</tr>
												
												<tr>
													<td align="left" class="captiontext" valign="top" width="12%">
														<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.concurrencyidentityfield" />
														<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.concurrencyidentityfield" header="diameter.diameterconcurrency.concurrencyidentityfield"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="2">
														<html:text styleId="concurrencyIdentityField" name="diameterConcurrencyForm" property="concurrencyIdentityField"  styleClass="concurrencyIdentityField" style="width:250px" tabindex="5" maxlength="30" />
														<font color="#FF0000"> *</font>	
													</td>
												</tr>
												
												<tr>
													<td align="left" class="captiontext" valign="top" width="12%">
														<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.dbfailureaction" />
														<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.dbfailureaction" header="diameter.diameterconcurrency.dbfailureaction"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="2">
														<html:select name="diameterConcurrencyForm" styleId="dbFailureAction" property="dbFailureAction" style="width:250px" tabindex="6" >
															<logic:iterate id="dbFailureActionInst"  collection="<%=DBFailureActions.values() %>"> 
																<%String displayText=((DBFailureActions)dbFailureActionInst).name(); %>
																<html:option value="<%=displayText%>"><%=displayText%></html:option>
									 						</logic:iterate>
														</html:select>
													</td>
												</tr>
												<tr>
													<td class="tblheader-bold" colspan="3">
														<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.sessionoverideproperties" />
													</td>
												</tr>
												
												<tr>
													<td align="left" class="captiontext" valign="top" width="12%">
														<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.action" />
														<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.action" header="diameter.diameterconcurrency.action"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="2">
														<html:select name="diameterConcurrencyForm" styleId="sessionOverrideAction" property="sessionOverrideAction" style="width:250px" tabindex="6" >
															<logic:iterate id="sessionOverideActionInst"  collection="<%=SessionOverrideActions.values() %>"> 
																<%String valueText=((SessionOverrideActions)sessionOverideActionInst).value; %>
																<html:option value="<%=valueText%>"><%=((SessionOverrideActions)sessionOverideActionInst).value%></html:option>
									 						</logic:iterate>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td align="left" class="captiontext" valign="top" width="12%">
														<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.sessionoverridefields" />
														<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.sessionoverridefields" header="diameter.diameterconcurrency.sessionoverridefields"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="2">
														<html:text styleId="sessionOverrideFields" property="sessionOverrideFields" maxlength="300" style="width:250px;"/>
													</td>
												</tr>
												
												
												<tr>
													<td class="tblheader-bold" colspan="3">
														<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.mandatorydbfieldmapping" />
													</td>
												</tr>
												
												<tr>
													<td colspan="3" class="captiontext">
														<table cellspacing="0" cellpadding="0" border="0" width="95%" class="madatoryMappingsTable">
															<tr>
																<td class="tblheader" width="18%">
																	<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.fields" />
																	<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.fields" header="diameter.diameterconcurrency.fields"/>
																</td>
																<td class="tblheader" width="17%">
																	<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.dbfieldname" />
																	<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.dbfieldname" header="diameter.diameterconcurrency.dbfieldname"/>
																</td>
																<td class="tblheader" width="17%">
																	<bean:message bundle="diameterResources" key="diameter.diameterconurrency.referringattribute" />
																	<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconurrency.referringattribute" header="diameter.diameterconurrency.referringattribute"/>
																</td>
																<td class="tblheader" width="17%">
																	<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.datatype" />
																	<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.datatype" header="diameter.diameterconcurrency.datatype"/>
																</td>
																<td class="tblheader" width="16%">
																	<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.defaultvalue" />
																	<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.defaultvalue" header="diameter.diameterconcurrency.defaultvalue"/>
																</td>
																<td class="tblheader" width="10%">
																	<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.includeinasr"/>
																	<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.includeinasr" header="diameter.diameterconcurrency.includeinasr"/>
																</td>
															</tr>
															<tr>
																<td class="labeltext allborder" width="18%">
																	<%=MandateryFieldConstants.APPLICATION_ID.value %>
																	<input type="hidden" value="<%=MandateryFieldConstants.APPLICATION_ID.value %>" name="field"/>
																</td>
																<td class="tblrows" width="17%">
																	<input name="dbFieldName"  class="dbFieldName concurrency-textbox" style="width:100%;" value="APPLICATION_ID"/>
																</td>
																<td class="tblrows" width="17%">
																	<input name="referringAttribute" class="referringAttribute concurrency-textbox" style="width:100%;" value="21067:65539"/>
																</td>
																<td class="tblrows" width="17%">
																	<input type="hidden" name="dataType" class="dataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" />
																	<select disabled="disabled" class="concurrency-textbox" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width:100%;">
																		<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
																	</select>	
																</td>
																<td class="tblrows" width="16%">
																	<input name="defaultValue" class="defaultValue concurrency-textbox" style="width:100%;"/>
																</td>
																<td class="labeltext tblrows" width="10%" align="center">
																	<input type="checkbox" name="includeInASR" class="includeInASR" onclick="changeIncludeInASRValue(this);" value="true" checked="checked"/>
																</td>
															</tr>
															<tr>
																<td class="labeltext allborder" width="18%">
																	<%=MandateryFieldConstants.SESSION_ID.value %>
																	<input type="hidden" value="<%=MandateryFieldConstants.SESSION_ID.value %>" name="field"/>
																</td>
																<td class="tblrows" width="17%">
																	<input name="dbFieldName"  class="dbFieldName concurrency-textbox" style="width:100%;" value="SESSION_ID"/>
																</td>
																<td class="tblrows" width="17%">
																	<input name="referringAttribute" class="referringAttribute concurrency-textbox" style="width:100%;" value="0:263"/>
																</td>
																<td class="tblrows" width="17%">
																	<input type="hidden" name="dataType" class="dataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" />
																	<select disabled="disabled" class="concurrency-textbox" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width:100%;">
																		<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
																	</select>	
																</td>
																<td class="tblrows" width="16%">
																	<input name="defaultValue" class="defaultValue concurrency-textbox" style="width:100%;"/>
																</td>
																<td class="labeltext tblrows" width="10%" align="center">
																	<input type="checkbox" name="includeInASR" class="includeInASR" onclick="changeIncludeInASRValue(this);" value="true" checked="checked"/>
																</td>
															</tr>
															<tr>
																<td class="labeltext allborder" width="18%">
																	<%=MandateryFieldConstants.PDP_TYPE.value %>
																	<input type="hidden" value="<%=MandateryFieldConstants.PDP_TYPE.value %>" name="field"/>
																</td>
																<td class="tblrows" width="17%">
																	<input name="dbFieldName"  class="dbFieldName concurrency-textbox" style="width:100%;" value="PDP_TYPE"/>
																</td>
																<td class="tblrows" width="17%">
																	<input name="referringAttribute" class="referringAttribute concurrency-textbox" style="width:100%;" value="0:6"/>
																</td>
																<td class="tblrows" width="17%">
																	<input type="hidden" name="dataType" class="dataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" />
																	<select disabled="disabled" class="concurrency-textbox" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width:100%;">
																		<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
																	</select>	
																</td>
																<td class="tblrows" width="16%">
																	<input name="defaultValue" class="defaultValue concurrency-textbox" style="width:100%;"/>
																</td>
																<td class="labeltext tblrows" width="10%" align="center">
																	<input type="checkbox" name="includeInASR" class="includeInASR" onclick="changeIncludeInASRValue(this);" value="false"/>
																</td>
															</tr>
															<tr>
																<td class="labeltext allborder" width="18%">
																	<%=MandateryFieldConstants.INDIVIDUAL_ID.value %>
																	<input type="hidden" value="<%=MandateryFieldConstants.INDIVIDUAL_ID.value %>" name="field"/>
																</td>
																<td class="tblrows" width="17%">
																	<input name="dbFieldName"  class="dbFieldName concurrency-textbox" style="width:100%;" value="SUBSCRIBER_ID"/>
																</td>
																<td class="tblrows" width="17%">
																	<input name="referringAttribute" class="referringAttribute concurrency-textbox" style="width:100%;" value="0:1"/>
																</td>
																<td class="tblrows" width="17%">
																	<input type="hidden" name="dataType" class="dataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" />
																	<select disabled="disabled" class="concurrency-textbox" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width:100%;">
																		<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
																	</select>	
																</td>
																<td class="tblrows" width="16%">
																	<input name="defaultValue" class="defaultValue concurrency-textbox" style="width:100%;"/>
																</td>
																<td class="labeltext tblrows" width="10%" align="center">
																	<input type="checkbox" name="includeInASR" class="includeInASR" onclick="changeIncludeInASRValue(this);" value="false"/>
																</td>
															</tr>
															<tr>
																<td class="labeltext allborder" width="18%">
																	<%=MandateryFieldConstants.PEER_ID.value %>
																	<input type="hidden" value="<%=MandateryFieldConstants.PEER_ID.value %>" name="field"/>
																</td>
																<td class="tblrows" width="17%">
																	<input name="dbFieldName"  class="dbFieldName concurrency-textbox" style="width:100%;" value="PEER_ID"/>
																</td>
																<td class="tblrows" width="17%">
																	<input name="referringAttribute" class="referringAttribute concurrency-textbox" style="width:100%;" value="21067:65621"/>
																</td>
																<td class="tblrows" width="17%">
																	<input type="hidden" name="dataType" class="dataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" />
																	<select disabled="disabled" class="concurrency-textbox" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width:100%;">
																		<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
																	</select>	
																</td>
																<td class="tblrows" width="16%">
																	<input name="defaultValue" class="defaultValue concurrency-textbox" style="width:100%;"/>
																</td>
																<td class="labeltext tblrows" width="10%" align="center">
																	<input type="checkbox" name="includeInASR" class="includeInASR" onclick="changeIncludeInASRValue(this);" value="true" checked="checked"/>
																</td>
															</tr>
															<tr>
																<td class="labeltext allborder" width="18%">
																	<%=MandateryFieldConstants.GROUP_ID.value %>
																	<input type="hidden" value="<%=MandateryFieldConstants.GROUP_ID.value %>" name="field"/>
																</td>
																<td class="tblrows" width="17%">
																	<input name="dbFieldName"  class="dbFieldName concurrency-textbox" style="width:100%;" value="GROUPNAME"/>
																</td>
																<td class="tblrows" width="17%">
																	<input name="referringAttribute" class="referringAttribute concurrency-textbox" style="width:100%;" value="21067:65557"/>
																</td>
																<td class="tblrows" width="17%">
																	<input type="hidden" name="dataType" class="dataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" />
																	<select disabled="disabled" class="concurrency-textbox" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width:100%;">
																		<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
																	</select>	
																</td>
																<td class="tblrows" width="16%">
																	<input name="defaultValue" class="defaultValue concurrency-textbox" style="width:100%;"/>
																</td>
																<td class="labeltext tblrows" width="10%" align="center">
																	<input type="checkbox" name="includeInASR" class="includeInASR" value="false" onclick="changeIncludeInASRValue(this);"/>
																</td>
															</tr>
														</table>
													</td>
												</tr>
												
												<tr>
													<td class="tblheader-bold" colspan="3">
														<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.additionaldbfieldmapping" />
													</td>
												</tr>
												
												<tr>
													<td colspan="3" class="captiontext">
														<table cellspacing="0" cellpadding="0" border="0" width="100%">
															<tr>
																<td>
																	<input type="button" onclick="addAdditionalMappingRow('additionalMappingTable','templateMappingTable');" value=" Add " class="light-btn broadcast-com-btn" style="size: 140px" tabindex="3"> 
																</td>
															</tr>
															<tr>
																<td>
																	<table cellspacing="0" cellpadding="0" border="0" width="95%" id="additionalMappingTable" class="additionalMappingTable">
																		<tr>
																			<td class="tblheader" width="19%">
																				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.dbfieldname" />
																				<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.dbfieldname" header="diameter.diameterconcurrency.dbfieldname"/>
																			</td>
																			<td class="tblheader" width="20%">
																				<bean:message bundle="diameterResources" key="diameter.diameterconurrency.referringattribute" />
																				<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconurrency.referringattribute" header="diameter.diameterconurrency.referringattribute"/>
																			</td>
																			<td class="tblheader" width="20%">
																				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.datatype" />
																				<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.datatype" header="diameter.diameterconcurrency.datatype"/>
																			</td>
																			<td class="tblheader" width="20%">
																				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.defaultvalue" />
																				<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.defaultvalue" header="diameter.diameterconcurrency.defaultvalue"/>
																			</td>
																			<td class="tblheader" width="15%" align="center">
																				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.includeinasr"/>
																				<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.includeinasr" header="diameter.diameterconcurrency.includeinasr"/>
																			</td>
																			<td class="tblheader" width="8%" align="center">
																				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.remove"/>
																			</td>
																		</tr>
																</table>
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td colspan="3">&nbsp;</td>
												</tr>
												<tr>
													<td>&nbsp;</td>
													<td align="left" valign="middle" colspan="3">&nbsp;&nbsp;
														<input type="button" tabindex="11" name="c_btnCreate" onclick="customValidate();" value="   Create   " class="light-btn">&nbsp;&nbsp; 
														<input type="reset" tabindex="12" name="c_btnDeletePolicy" onclick="javascript:location.href='searchDiameterConcurrency.do'" value=" Cancel " class="light-btn">
													</td>
												</tr>
												<tr>
													<td colspan="3">&nbsp;</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
					</table>
				</td>
			</tr>
		</table>
	</html:form>
</body>
</html>
<table style="display: none;" id="templateMappingTable">
	<tr>
		<td class="allborder" width="19%">
			<input name="dbFieldName"  class="dbFieldName concurrency-textbox" style="width:100%;"/>
		</td>
		<td class="tblrows" width="20%">
			<input name="referringAttribute" class="referringAttribute concurrency-textbox" style="width:100%;"/>
		</td>
		<td class="tblrows" width="20%">
			<input type="hidden" name="dataType" class="dataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" />
			<select disabled="disabled" class="concurrency-textbox" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width:100%;">
				<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
			</select>	
		</td>
		<td class="tblrows" width="20%">
			<input name="defaultValue" class="defaultValue concurrency-textbox" style="width:100%;"/>
		</td>
		<td class="labeltext tblrows" width="15%" align="center">
			<input type="checkbox" name="includeInASR" class="includeInASR" value="false" onclick="changeIncludeInASRValue(this);">
		</td>
		<td class="labeltext tblrows" width="15%" align="center">
			<img src='<%=request.getContextPath()%>/images/minus.jpg' class='delete' height='15' />&nbsp;
		</td>
	</tr>
</table>