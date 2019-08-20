<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.*"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.util.constants.AccountFieldConstants"%>
<%@ page import="java.util.List"%>
<%
	String basePath = request.getContextPath();
	List dbfeildMapList = (List)session.getAttribute("dbfeildMapList");	

%>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script language="javascript">
	$(document).ready(function() {
	var logicalNameData =eval(('<bean:write name="logicalNameData"/>').replace(new RegExp("&quot;", 'g'),"\""));
	/* set Logical Data JsonObject */
	setLogicalNameData(logicalNameData);
	
	/* set Logical Name drop down for default value */ 
	setLogicalnameDropDown("mappingtbl","logicalnmVal",true);
	
	/* bind click event of delete image */
	$('#mappingtbl td img.delete').live('click',function() {
		 $(this).parent().parent().remove(); 
		 setLogicalnameDropDown("mappingtbl","logicalnmVal",true); /* set Logical Name drop down after remove row */
	});
	setFieldSuggestion();
});		
 	function validateForm(){	
	
 		if(document.forms[0].databaseId.value == 0){
 			alert('Select atleast one datasource value ');
 		}else if(isNull(document.forms[0].tableName.value)){
 			alert('Table Name must be specified.');
 		}else if(isNull(document.forms[0].querytimeout.value)){
 			document.forms[0].querytimeout.focus();
 			alert('DB Query Timeout must be specified.');
 		}else if(!isPositiveNumber(document.forms[0].querytimeout.value)){
 			document.forms[0].querytimeout.focus();
 			alert('DB Query Timeout must be zero or positive number.');
 		}else if(isNull(document.forms[0].timeoutcount.value)){
 			document.forms[0].timeoutcount.focus();
 			alert('Maximum Query Timeout Count must be specified.');
 		}else if(!isPositiveNumber(document.forms[0].timeoutcount.value)){
 			document.forms[0].timeoutcount.focus();
 			alert('Maximum Query Timeout Count must be zero or positive number.');
 		}else if(document.getElementById('mappingtbl').getElementsByTagName('tr').length < 1){
 	 	 	alert('At least one mapping must be there.');
 		}else if(isNull(document.forms[0].profileLookupColumn.value)){
 	 		alert('Profile Lookup Column must be specified.');
 	 		document.forms[0].profileLookupColumn.focus();
 		}else{ 			 
 			if(isValidLogicalNameMapping("mappingtbl", "logicalnmVal", "dbfieldVal")){
 				document.forms[0].action.value = 'create';
 		 		document.forms[0].submit();
 			}
 		}		
	}
 	
	function setColumnsOnUserIdentity(){
		var userIdentityAttributes = document.getElementById("userIdentityAttributes").value;
		retriveDiameterDictionaryAttributes(userIdentityAttributes,"userIdentityAttributes");
	}
	function setFieldSuggestion(){
		$(retriveTableFields(document.getElementById("databaseId").value,document.getElementById("tableName").value,"dbfieldVal"));	
		$(retriveTableFields(document.getElementById("databaseId").value,document.getElementById("tableName").value,"profileLookupColumn"));	
	}
	function  setAutoCompleteData(dbFieldObject){
		$(dbFieldObject).autocomplete({
			source : $( "#dbfieldVal" ).autocomplete( "option", "source" ),
		});
	}
	
	function setAutoCompleteDataOnProfileLookup(profileLookupColumnObj){
		$(profileLookupColumnObj).autocomplete({
			source : $( "#profileLookupColumn" ).autocomplete( "option", "source" ),
		});
	}
	setTitle('<bean:message bundle="driverResources" key="driver.driver"/>');
	
	
</script>



<html:form action="/createDiameterDBAuthDriver">

	<html:hidden name="createDiameterDBAuthDriverForm" property="action" />
	<html:hidden name="createDiameterDBAuthDriverForm"
		property="driverInstanceName" />
	<html:hidden name="createDiameterDBAuthDriverForm"
		property="driverDesp" />
	<html:hidden name="createDiameterDBAuthDriverForm"
		property="driverRelatedId" />
	<html:hidden property="itemIndex" />

	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header"><bean:message
											bundle="driverResources" key="driver.createdbauthDriver" /></td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td>
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td align="left" class="tblheader-bold" valign="top"
													colspan="3"><bean:message bundle="driverResources"
														key="driver.dbauthdriver.details" /></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="30%">
													<bean:message bundle="driverResources"
														key="driver.dbauthdriver.ds" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="dbauthdriver.ds" header="driver.dbauthdriver.ds"/> 
												</td>
												<td align="left" class="labeltext" valign="top" width="70%">
													<html:select tabindex="1" property="databaseId" onchange="setFieldSuggestion()"
														styleId="databaseId" style="width:130px">
														<html:option value="0">Select</html:option>
														<html:optionsCollection
															name="createDiameterDBAuthDriverForm"
															property="databaseDSList" label="name" value="databaseId" />
													</html:select><font color="#FF0000"> *</font>
												</td>
											</tr>



											<tr>
												<td align="left" class="captiontext" valign="top" width="30%">
													<bean:message bundle="driverResources"
														key="driver.tablename" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="dbauthdriver.tblname" header="driver.tablename"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="70%">
													<html:text tabindex="2" styleId="tableName"
														property="tableName" size="30" maxlength="128"
														style="width:250px" /><font color="#FF0000"> *</font>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="30%">
													<bean:message bundle="driverResources"
														key="driver.querytimeout" /> 
															<bean:message
																key="general.seconds" /> 
																	<ec:elitehelp headerBundle="driverResources" 
																		text="dbauthdriver.dbquerytimeout" header="driver.querytimeout"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="70%">
													<html:text tabindex="3" styleId="querytimeout"
														property="dbQueryTimeout" size="20" maxlength="10"
														style="width:250px" /><font color="#FF0000"> *</font>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="30%">
													<bean:message bundle="driverResources"
														key="driver.querytimeoutcount" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="dbauthdriver.maxquerytimeout" header="driver.querytimeoutcount"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="70%">
													<html:text tabindex="4" styleId="timeoutcount"
														property="maxQueryTimeoutCount" size="20" maxlength="10"
														style="width:250px" /><font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="30%">
													<bean:message bundle="driverResources" key="driver.profilelookupcolumn" />
													<ec:elitehelp headerBundle="driverResources" text="dbauthdriver.profilelookup" header="driver.profilelookupcolumn"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="70%">
													<html:text styleId="profileLookupColumn" property="profileLookupColumn" size="30" maxlength="30" style="width:250px" tabindex="5" onfocus="setAutoCompleteDataOnProfileLookup(this);" />
													<font color="#FF0000">*</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="30%">
													<bean:message bundle="driverResources"
														key="driver.useridentityattributes" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="driver.usridentity" header="driver.useridentityattributes"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="70%">
													<input tabindex="5" type="text"
													name="userIdentityAttributes" id="userIdentityAttributes"
													size="60" maxlength="256" autocomplete="off"
													onkeyup="setColumnsOnUserIdentity();" style="width: 250px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="tblheader-bold" valign="top"
													colspan="3"><bean:message bundle="driverResources"
														key="driver.fieldmap" /></td>
											</tr>
											<tr>
												<td class="captiontext" valign="top" colspan="2"><input
													type="button" tabindex="6" onclick='addNewRow("dbMappingTable","mappingtbl","logicalnmVal",true);' value=" Add "
													class="light-btn" style="size: 140px"></td>
											</tr>

										</table>
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td align="left" class="captiontext" colspan="3"
													valign="top" width="97%">
													<table width="98%" id="mappingtbl" cellpadding="0"
														cellspacing="0" border="0">
														<tr>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources" key="driver.logicalname" />
																	<ec:elitehelp headerBundle="driverResources" 
																		text="dbauthdriver.logicalname" header="driver.logicalname"/>
															</td>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources" key="driver.dbfield" />
																	<ec:elitehelp headerBundle="driverResources" 
																		text="dbauthdriver.dbattribute" header="driver.dbfield"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="15%">
																<bean:message bundle="driverResources"
																	key="driver.defaultvalue" />
																		<ec:elitehelp headerBundle="driverResources" 
																			text="dbauthdriver.defaultvalue" header="driver.defaultvalue"/>
															</td>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources" key="driver.valuemapping" />
																	<ec:elitehelp headerBundle="driverResources" 
																		text="dbauthdriver.valuemapping" header="driver.valuemapping"/>
															</td>
															<td align="left" class="tblheader" valign="top"
																width="5%">Remove</td>
														</tr>
														<logic:iterate id="obj" name="defaultMapping" type=" com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthFieldMapData">
														<tr>
															<td class="allborder">
																<select class="noborder"  name="logicalnmVal"  id="logicalnmVal" style="width:100%" tabindex="10" onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
																	<option value='<bean:write name="obj" property="nameValuePoolData.value"/>'><bean:write name="obj" property="nameValuePoolData.name"/> </option>
																</select>
															</td>
															<td class="tblrows"><input class="noborder" type="text" name="dbfieldVal"  id="dbfieldVal"  maxlength="1000" size="28" style="width:100%" onfocus="setAutoCompleteData(this);" tabindex="10" value='<bean:write name="obj" property="dbField"/>' /></td>
															<td class="tblrows"><input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width:100%" tabindex="10"/></td>
															<td class="tblrows"><input class="noborder" type="text" name="valueMapping" maxlength="1000" size="30" style="width:100%" tabindex="10"/></td>
															<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="10"/></td>
														</tr>
														</logic:iterate>
													</table>
												</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2"><input
													type="button" tabindex="7" name="c_btnCreate"
													id="c_btnCreate2" value=" Create " class="light-btn"
													onclick="validateForm()"> <input tabindex="8"
													type="reset" name="c_btnDeletePolicy"
													onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?'"
													value="Cancel" class="light-btn"></td>
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
<!-- Mapping Table Row template -->
	<table style="display:none;" id="dbMappingTable">
		<tr>
			<td class="allborder">
				<select class="noborder"  name="logicalnmVal"  id="logicalnmVal" style="width:100%" tabindex="10" onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
				</select>
			</td>
			<td class="tblrows"><input class="noborder" type="text" name="dbfieldVal" id="dbfieldVal"  maxlength="1000" size="28" style="width:100%" tabindex="10" onfocus="setAutoCompleteData(this);"/></td>
			<td class="tblrows"><input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width:100%" tabindex="10"/></td>
			<td class="tblrows"><input class="noborder" type="text" name="valueMapping" maxlength="1000" size="30" style="width:100%" tabindex="10"/></td>
			<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="10"/></td>
  		</tr>
	</table>