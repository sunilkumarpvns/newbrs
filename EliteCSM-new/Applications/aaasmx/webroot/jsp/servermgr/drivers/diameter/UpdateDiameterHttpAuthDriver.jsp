<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>

<bean:define id="driverInstanceData" scope="request" name="driverInstanceData"
	type="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"></bean:define>
<bean:define id="httpAuthDriverData" scope="request" name="httpAuthDriverData"
	type=" com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverData"></bean:define>
	
<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script>
	var flag=false;
	var responseParameterIndex=false;
	setTitle('<bean:message bundle="driverResources" key="driver.driver"/>');
	
	function validateForm(){
		if(isNull(document.forms[0].driverInstanceName.value)){
			alert('Name must be specified');
		}else if(!isValidName) {
 			alert('Enter Valid Driver Name');
 			document.forms[0].driverInstanceName.focus();
 			return;
 		}else if(isNull(document.forms[0].http_url.value)){
 			alert('Http Url Name must be specified.');
 		}else if(isNull(document.forms[0].http_url.value)){
 			alert('Http Url Name must be specified.');
 		}else if(isNull(document.forms[0].statusCheckDuration.value)){
 			alert('StatusCheckDuration must be specified.');
 		}else if(isNaN(document.forms[0].statusCheckDuration.value)){
 			alert('StatusCheckDuration must be numeric.');
 		}else if(isNull(document.forms[0].maxQueryTimeoutCount.value)){
 			alert('Max query timeout count must be specified.');
 		}else if(isNaN(document.forms[0].maxQueryTimeoutCount.value)){
 			alert('Max query timeout count must be numeric.');
 		}else if(isNull(document.forms[0].expiryDateFormats.value)){
 			alert('Expiry Date Formats must be specified.');
 		}else if(document.getElementById('mappingtbl').getElementsByTagName('tr').length < 2){
	 	 	alert('At least one mapping must be there.');
 		}else if(responseParameterIndex==true && flag==true){
			alert('ResponseParameterIndex must be numeric.');
 		}else{ 			 			
 			if(isValidLogicalNameMapping("mappingtbl","logicalnmVal","responseParamIndex")){
				document.forms[0].action.value = 'Update';
				document.forms[0].submit();
			}
 		}		
	}
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
		setAction("update");
		enableAll();
		setFieldSuggestion();
	});	
	function setColumnsOnUserIdentity(){
		var userIdentityAttributes = document.getElementById("userIdentityAttributes").value;
		retriveDiameterDictionaryAttributes(userIdentityAttributes,"userIdentityAttributes");
	}
	function setResponseParamIndex(obj){
 		if(isNaN(obj.value)){
 			responseParameterIndex=true;
 			flag=true;
 			return;
 		}
 		flag=false;
 	}
	function verifyName() {
		var searchName = document.getElementById("name").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.DRIVER%>',searchName,'update','<%=driverInstanceData.getDriverInstanceId()%>','verifyNameDiv');
	}
	
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="tblheader-bold" height="20%" colspan="2">
						<bean:message bundle="driverResources" key="driver.view" /></td>
				</tr>

				<tr>
					<td align="left" class="tblheader-bold" valign="top" colspan="2">
						<bean:message bundle="driverResources" key="driver.driverinstancedetails" />
					</td>
				</tr>

				<tr>
					<td class="tblfirstcol" width="20%" height="20%">
						<bean:message bundle="driverResources" key="driver.instname" /></td>
					<td class="tblcol" width="30%" height="20%">
						<bean:write name="driverInstanceData" property="name" />&nbsp;</td>
				</tr>

				<tr>
					<td class="tblfirstcol" width="20%" height="20%">
						<bean:message bundle="driverResources" key="driver.instdesc" /></td>
					<td class="tblcol" width="30%" height="20%">
						<bean:write name="driverInstanceData" property="description" />&nbsp;</td>
				</tr>

				<tr>
					<td align="left" class="tblheader-bold" valign="top" colspan="2">
						Update <bean:write name="driverInstanceData" property="driverTypeData.name" />
					</td>
				</tr>

				<tr>
					<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box" colspan="2">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td class="small-gap" colspan="3">&nbsp;</td>
							</tr>
							<tr>
								<td><html:form action="/updateDiameterHttpAuthDriver">
										<html:hidden name="diameterHttpAuthDriverForm" property="action" value="update" />
										<html:hidden name="diameterHttpAuthDriverForm" property="driverInstanceId" />
										<html:hidden name="diameterHttpAuthDriverForm" property="driverRelatedId" />
										<html:hidden property="auditUId" styleId="auditUId" name="diameterHttpAuthDriverForm"/>
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td align="left" class="captiontext" valign="top" width="26%">
													<bean:message bundle="driverResources" key="driver.instname" /> 
														<ec:elitehelp headerBundle="driverResources" 
															text="createdriver.name" header="driver.name"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text tabindex="1" styleId="name" property="driverInstanceName"
													size="30" maxlength="255" style="width:250px"
													onblur="verifyName();" /><font color="#FF0000"> *</font>
													<div id="verifyNameDiv" class="labeltext"></div></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="26%">
													<bean:message bundle="driverResources" key="driver.instdesc" />
														<ec:elitehelp headerBundle="driverResources" 
															text="createdriver.description" header="driver.description"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text tabindex="2" property="driverInstanceDesc"
													style="width:250px" maxlength="60"></html:text></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="26%">
													<bean:message bundle="driverResources" key="driver.httpurl" />
														<ec:elitehelp headerBundle="driverResources" 
															text="httpdriver.url" header="driver.httpurl"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text tabindex="3" styleId="http_url" property="http_url"
													size="30" maxlength="255" style="width:250px" /><font
													color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="driverResources" key="driver.statuscheckduration" />
														<bean:message key="general.seconds" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="httpdriver.statuscheckduration" 
																	header="driver.statuscheckduration"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text tabindex="4" styleId="statusCheckDuration"
													property="statusCheckDuration" size="10" maxlength="10"
													style="width:250px" /><font color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="driverResources" key="driver.querytimeoutcount" />
														<ec:elitehelp headerBundle="driverResources" 
															text="httpdriver.querytimeout" header="driver.querytimeoutcount"/> 
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text tabindex="5" styleId="maxQueryTimeoutCount"
													property="maxQueryTimeoutCount" size="10" maxlength="10"
													style="width:250px" /><font color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="driverResources" key="driver.expirydatepatterns" />
														<ec:elitehelp headerBundle="driverResources" 
															text="httpdriver.expdateformat" header="driver.expirydatepatterns"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text tabindex="6" styleId="expiryDateFormats"
													property="expiryDateFormats" size="20" maxlength="255"
													style="width:250px" /><font color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="driverResources"
														key="driver.useridentityattributes" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="driver.usridentity" header="driver.useridentityattributes"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text tabindex="7" styleId="userIdentityAttributes"
													property="userIdentityAttributes" size="20"
													maxlength="255" onkeyup="setColumnsOnUserIdentity();"
														style="width:250px" /></td> 
											</tr>

											<tr>
												<td class="small-gap" colspan="3">&nbsp;</td>
											</tr>

											<tr>
												<td class="small-gap" colspan="3">&nbsp;</td>
											</tr>

											<tr>
												<td align="left" class="tblheader-bold" valign="top"
													colspan="3"><bean:message bundle="driverResources"
													key="driver.httpresponsemapping" /></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top"
													colspan="2"><input type="button" tabindex="8"
													onclick='addNewRow("dbMappingTable","mappingtbl","logicalnmVal",true);' value=" Add " class="light-btn"
													style="size: 140px"></td>
											</tr>

											<tr>
												<td align="left" class="labeltext" colspan="3" valign="top"
													width="97%">
													<table width="98%" id="mappingtbl" cellpadding="0"
														cellspacing="0" border="0" class="captiontext">
														<tr>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources" key="driver.dia.http.logicalname"/>
																	<ec:elitehelp headerBundle="driverResources" 
																		text="httpdriver.logicalname" header="driver.dia.http.logicalname"/>
															</td>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources" key="driver.dia.http.respparaindex" />
																	<ec:elitehelp headerBundle="driverResources" 
																		text="httpdriver.responseParameterIndex" header="driver.dia.http.respparaindex"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="15%">
																<bean:message bundle="driverResources"
																	key="driver.dia.http.default" /> 
																		<ec:elitehelp headerBundle="driverResources" 
																			text="httpdriver.defaultvalue" header="driver.dia.http.default"/>
															</td>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources" key="driver.dia.http.valuemap" />
																	<ec:elitehelp headerBundle="driverResources" 
																		text="httpdriver.valuemapping" header="driver.dia.http.valuemap"/>
															</td>
															<td align="left" class="tblheader" valign="top"
																width="5%">Remove</td>
														</tr>

														<logic:iterate id="obj" name="httpAuthFieldMapList"
														type="com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverFieldMapData">
														<tr>
															<td class="allborder"><select class="noborder"
																name="logicalnmVal" id="logicalnmVal" style="width: 100%"
																tabindex="10"
																onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
																	<option
																		value='<bean:write name="obj" property="nameValuePoolData.value"/>'>
																		<bean:write name="obj" property="nameValuePoolData.name" />
																	</option>
															</select></td>
															<td class="tblrows"><input class="noborder" type="text"
																name="responseParamIndex" maxlength="10" size="10"
																style="width: 100%" tabindex="10"
																value='<bean:write name="obj" property="responseParamIndex"/>' onchange="setResponseParamIndex(this);" /></td>
															<td class="tblrows"><input class="noborder" type="text"
																name="defaultValue" maxlength="1000" size="28"
																style="width: 100%" tabindex="10"
																value='<bean:write name="obj" property="defaultValue"/>' /></td>
															<td class="tblrows"><input class="noborder" type="text"
																name="valueMapping" maxlength="1000" size="30"
																style="width: 100%" tabindex="10"
																value='<bean:write name="obj" property="valueMapping"/>' /></td>
															<td class="tblrows" align="center" colspan="3"><img
																value="top" src="<%=basePath%>/images/minus.jpg"
																class="delete" style="padding-right: 5px; padding-top: 5px;"
																height="14" tabindex="10" /></td>
														</tr>
													</logic:iterate>
													</table>
												</td>
											</tr>

											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2"><input
													type="button" tabindex="13" name="c_btnUpdate"
													id="c_btnUpdate" value=" Update " class="light-btn"
													onclick="validateForm()"> &nbsp; <input
													type="reset" tabindex="14" name="c_btnDeletePolicy"
													onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?'"
													value="Cancel" class="light-btn"></td>
											</tr>
										</table>
									</html:form></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			
			<!-- Mapping Table Row template -->
<table style="display: none;" id="dbMappingTable">
	<tr>
		<td class="allborder"><select class="noborder"
			name="logicalnmVal" id="logicalnmVal" style="width: 100%"
			tabindex="10"
			onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
		</select></td>
		<td class="tblrows"><input class="noborder" type="text"
			name="responseParamIndex" maxlength="10" size="10" style="width: 100%" onchange="setResponseParamIndex(this);" 
			tabindex="10" /></td>
		<td class="tblrows"><input class="noborder" type="text"
			name="defaultValue" maxlength="1000" size="28" style="width: 100%"
			tabindex="10" /></td>
		<td class="tblrows"><input class="noborder" type="text"
			name="valueMapping" maxlength="1000" size="30" style="width: 100%"
			tabindex="10" /></td>
		<td class="tblrows" align="center" colspan="3"><img value="top"
			src="<%=basePath%>/images/minus.jpg" class="delete"
			style="padding-right: 5px; padding-top: 5px;" height="14"
			tabindex="10" /></td>
	</tr>
</table>