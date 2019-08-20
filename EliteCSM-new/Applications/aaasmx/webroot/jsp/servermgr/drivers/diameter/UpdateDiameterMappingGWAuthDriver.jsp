<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>

<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<bean:define id="driverInstanceData" scope="request" name="driverInstanceData" type="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"></bean:define>
<bean:define id="mappingGatewayAuthDriverData" scope="request"
	name="mappingGatewayAuthDriverData"
	type="com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.MappingGatewayAuthDriverData"></bean:define>

<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script>
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


	var profileFieldArray = new Array();
	var profileFieldArrayIndex = 0;
	<logic:iterate id="obj" name="diameterMappingGWAuthDriverForm" property="profileFieldList">
		profileFieldArray[profileFieldArrayIndex++] = '<bean:write name="obj" property="value"/>';
	</logic:iterate>

	function setProfileField (profileField){
	$(profileField).autocomplete({
	    source: profileFieldArray
	});
	} 
	var isValidName;
	function verifyName() {
		var searchName = document.getElementById("name").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.DRIVER%>',searchName,'update','<%=driverInstanceData.getDriverInstanceId()%>','verifyNameDiv');
}
	
function setColumnsOnUserIdentity(){
	var userIdentityAttributes = document.getElementById("userIdentityAttributes").value;
	retriveDiameterDictionaryAttributes(userIdentityAttributes,"userIdentityAttributes");
}

function validateForm(){	
	if(isNull(document.forms[0].driverInstanceName.value)){
		alert('Driver name must be specified.');
	}else if(!isValidName) {
			alert('Enter Valid Driver Name');
			document.forms[0].driverinstname.focus();
			return;
	}else if(document.forms[0].localHostId.value == 0){
		alert('Local Host Id must be specified.');
	}else if(isNull(document.forms[0].localHostPort.value)){
		alert('Local Host Port must be specified.');
	}else if(isNull(document.forms[0].localHostIp.value)){
		alert('Local Host Ip must be specified.');
	}else if(isNull(document.forms[0].remoteHostId.value)){
		alert('Remote Host Id must be specified.');
	}else if(isNull(document.forms[0].remoteHostPort.value)){
		alert('Remote Host Port must be specified.');
	}else if(isNull(document.forms[0].remoteHostIp.value)){
		alert('Remote Host Ip must be specified.');
	}else if(isNull(document.forms[0].maxQueryTimeoutCount.value)){
		alert('Max Query Timeout must be specified.');
	}else if(isNaN(document.forms[0].maxQueryTimeoutCount.value)){
		alert('Max Query timeout count must be numeric.');
	}else if(document.forms[0].maxQueryTimeoutCount.value < 0){
		alert('Max Query timeout count must be positive.');
	}else if(document.forms[0].maxQueryTimeoutCount.value > 10000){
		alert('Max Query timeout count must be below 10000.');
	}else if(isNull(document.forms[0].mapGwConnPoolSize.value)){
		alert('Connection Pool Size must be specified.');
	}else if(isNaN(document.forms[0].mapGwConnPoolSize.value)){
		alert('Connection Pool Size must be numeric.');
	}else if(document.forms[0].mapGwConnPoolSize.value < 0){
		alert('Connection Pool Size must be positive.');
	}else if(document.forms[0].mapGwConnPoolSize.value > 500){
		alert('Connection Pool Size must be below 500.');
	}else if(isNull(document.forms[0].requestTimeout.value)){
		alert('Request Timeout must be specified.');
	}else if(isNaN(document.forms[0].requestTimeout.value)){
		alert('Request Timeout must be numeric.');
	}else if(document.forms[0].requestTimeout.value < 0){
		alert('Request Timeout must be positive.');
	}else if(document.forms[0].requestTimeout.value > 3000){
		alert('Request Timeout must be below 3000.');
	}else if(isNull(document.forms[0].statusCheckDuration.value)){
		alert('Status Check Duration must be specified.');
	}else if(isNaN(document.forms[0].statusCheckDuration.value)){
		alert('Status Check Duration must be numeric.');
	}else if(document.forms[0].statusCheckDuration.value < 0){
		alert('Status Check Duration must be positive.');
	}else if(document.forms[0].statusCheckDuration.value > 600){
		alert('Status Check Duration must be below 600.');
	}else{ 			 			
		if(isValidLogicalNameMapping("mappingtbl","logicalnmVal","profileField")){
			document.forms[0].action.value = 'Update';
			document.forms[0].submit();
		}
	}		
}	
</script>


<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">

				<tr>
					<td align="left" class="tblheader-bold" valign="top" colspan="2">
						<bean:message bundle="driverResources"
							key="driver.driverinstancedetails" />
					</td>
				</tr>

				<tr>
					<td class="tblfirstcol" width="20%" height="20%"><bean:message
							bundle="driverResources" key="driver.instname" /></td>
					<td class="tblcol" width="30%" height="20%"><bean:write
							name="driverInstanceData" property="name" />&nbsp;</td>
				</tr>

				<tr>
					<td class="tblfirstcol" width="20%" height="20%"><bean:message
							bundle="driverResources" key="driver.instdesc" /></td>
					<td class="tblcol" width="30%" height="20%"><bean:write
							name="driverInstanceData" property="description" />&nbsp;</td>
				</tr>

				<tr>
					<td align="left" class="tblheader-bold" valign="top" colspan="2">
						Update <bean:write name="driverInstanceData"
							property="driverTypeData.name" />
					</td>
				</tr>

				<tr>
					<td colspan="2"><html:form
							action="/updateDiameterMappingGWAuthDriver">
							<html:hidden name="diameterMappingGWAuthDriverForm"
								property="action" value="update" />
							<html:hidden name="diameterMappingGWAuthDriverForm"
								property="driverInstanceId" />
							<html:hidden property="auditUId" styleId="auditUId"  name="diameterMappingGWAuthDriverForm"/>
							<table cellpadding="0" cellspacing="0" border="0" width="100%"
								height="30%">
								<tr>
									<td align="left" class="captiontext" valign="top" width="23%">
										<bean:message bundle="driverResources" key="driver.name" /> 
											<ec:elitehelp headerBundle="driverResources" 
												text="createdriver.name" header="driver.name"/>
									</td>
									<td align="left" class="labeltext" valign="top" nowrap="nowrap">
										<html:text tabindex="1" styleId="name"  onkeyup="verifyName();"
											property="driverInstanceName" size="40" maxlength="70"
											style="width:250px" /><font color="#FF0000"> *</font>
										<div id="verifyNameDiv" class="labeltext"></div>
									</td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" width="20%">
										<bean:message bundle="driverResources"
											key="driver.description" />
												<ec:elitehelp headerBundle="driverResources" 
													text="createdriver.description" header="driver.description"/>
									</td>
									<td align="left" class="labeltext" valign="top"><html:text
											tabindex="2" styleId="description" property="driverDesp"
											size="40" style="width:250px" maxlength="70" /></td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" width="20%">
										<bean:message bundle="driverResources"
											key="driver.localhostid" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="mapgw.localhostid" header="driver.localhostid"/>
									</td>
									<td align="left" class="labeltext" valign="top"><html:text
											tabindex="3" styleId="localHostId" property="localHostId"
											size="30" maxlength="200" style="width:250px" /><font
										color="#FF0000"> *</font></td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" width="20%">
										<bean:message bundle="driverResources"
											key="driver.localhostip" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="mapgw.localhostip" header="driver.localhostip"/>
									</td>
									<td align="left" class="labeltext" valign="top"><html:text
											tabindex="4" styleId="localHostIp" property="localHostIp"
											size="30" maxlength="200" style="width:250px" /><font
										color="#FF0000"> *</font></td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" width="20%">
										<bean:message bundle="driverResources"
											key="driver.localhostport" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="mapgw.localhostport" header="driver.localhostport"/>
									</td>
									<td align="left" class="labeltext" valign="top"><html:text
											tabindex="5" styleId="localHostPort" property="localHostPort"
											size="30" maxlength="5" style="width:250px" /><font
										color="#FF0000"> *</font></td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" width="20%">
										<bean:message bundle="driverResources"
											key="driver.map.remotehostid" /> 
												<ec:elitehelp headerBundle="driverResources" 
												text="mapgw.remotehostid" header="driver.map.remotehostid"/>
									</td>
									<td align="left" class="labeltext" valign="top"><html:text
											tabindex="6" styleId="remoteHostId" property="remoteHostId"
											size="30" maxlength="200" style="width:250px" /><font
										color="#FF0000"> *</font></td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" width="20%">
										<bean:message bundle="driverResources"
											key="driver.map.remotehostip" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="mapgw.remotehostip" header="driver.map.remotehostip"/>
									</td>
									<td align="left" class="labeltext" valign="top"><html:text
											tabindex="7" styleId="remoteHostIp" property="remoteHostIp"
											size="30" maxlength="200" style="width:250px" /><font
										color="#FF0000"> *</font></td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" width="20%">
										<bean:message bundle="driverResources"
											key="driver.map.remotehostport" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="mapgw.remotehostport" header="driver.map.remotehostport"/>
									</td>
									<td align="left" class="labeltext" valign="top"><html:text
											tabindex="8" styleId="remoteHostPort"
											property="remoteHostPort" size="30" maxlength="5"
											style="width:250px" /><font color="#FF0000"> *</font></td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" width="20%">
										<bean:message bundle="driverResources"
											key="driver.map.querytimeoutcount" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="mapgw.querytimeout" header="driver.map.querytimeoutcount"/>
									</td>
									<td align="left" class="labeltext" valign="top"><html:text
											tabindex="9" styleId="maxQueryTimeoutCount"
											property="maxQueryTimeoutCount" size="20" maxlength="5"
											style="width:250px" /><font color="#FF0000"> *</font></td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" width="20%">
										<bean:message bundle="driverResources"
											key="driver.mapgwconnpoolsize" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="driver.mapgwconnpoolsize" header="driver.mapgwconnpoolsize"/>
									</td>
									<td align="left" class="labeltext" valign="top"><html:text
											tabindex="10" styleId="mapGwConnPoolSize"
											property="mapGwConnPoolSize" size="20" maxlength="5"
											style="width:250px" /><font color="#FF0000"> *</font></td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" width="20%">
										<bean:message bundle="driverResources"
											key="driver.map.requesttimeout" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="driver.requesttimeout" header="driver.map.requesttimeout"/>
									</td>
									<td align="left" class="labeltext" valign="top"><html:text
											tabindex="11" styleId="requestTimeout"
											property="requestTimeout" size="20" maxlength="10"
											style="width:250px" /><font color="#FF0000"> *</font></td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" width="20%">
										<bean:message bundle="driverResources"
											key="driver.mapgw.statuscheckduration" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="driver.mapgw.statuscheckduration" header="driver.mapgw.statuscheckduration"/>
									</td>
									<td align="left" class="labeltext" valign="top"><html:text
											tabindex="12" styleId="statusCheckDuration"
											property="statusCheckDuration" size="20" maxlength="5"
											style="width:250px" /><font color="#FF0000"> *</font></td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" width="20%">
										<bean:message bundle="driverResources"
											key="driver.map.useridentityattributes" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="driver.usridentity" header="driver.map.useridentityattributes"/>
									</td>
									<td align="left" class="labeltext" valign="top"><html:text
											tabindex="13" property="userIdentityAttributes" name="diameterMappingGWAuthDriverForm"
											styleId="userIdentityAttributes" style="width:250px"
											onkeyup="setColumnsOnUserIdentity();"></html:text></td>
								</tr>

								<tr>
									<td align="left" class="tblheader-bold" valign="top"
										colspan="3"><bean:message bundle="driverResources"
											key="driver.mapgsm" /></td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top">
										<bean:message bundle="driverResources" key="driver.mapgw.sendauthinfo" />
											<ec:elitehelp headerBundle="driverResources" 
												text="driver.sendauthinfo" header="driver.mapgw.sendauthinfo"/>
									</td>
									<td align="left" class="labeltext" valign="top"><html:select
											property="sendAuthInfo" style="width:250px" tabindex="14">
											<html:option value="TRUE">True</html:option>
											<html:option value="False">False</html:option>
										</html:select></td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top">
										<bean:message bundle="driverResources"
											key="driver.mapgw.numberoftriplets" />
												<ec:elitehelp headerBundle="driverResources" 
													text="driver.numberoftriplets" header="driver.mapgw.numberoftriplets"/>
									</td>
									<td align="left" class="labeltext" valign="top"><html:text tabindex="15"
											property="numberOfTriplets" maxlength="5" style="width:250px"></html:text>
									</td>
								</tr>

								<tr>
									<td align="left" class="tblheader-bold" valign="top"
										colspan="3"><bean:message bundle="driverResources"
											key="driver.mapprofilefield" /></td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" colspan="2">
										<input type="button" tabindex="16" onclick='addNewRow("dbMappingTable","mappingtbl","logicalnmVal",true);'
										value=" Add " class="light-btn" style="size: 140px">
									</td>
								</tr>

								<tr>
									<td align="left" class="labeltext" colspan="3" valign="top"
										width="97%">
										<table width="98%" id="mappingtbl" cellpadding="0"
											cellspacing="0" border="0" class="captiontext">
											<tr>
												<td align="left" class="tblheader" valign="top">
													<bean:message bundle="driverResources" key="driver.map.logicalname"/>
														<ec:elitehelp headerBundle="driverResources" 
															text="mapgwdriver.logicalname" header="driver.map.logicalname"/>
												</td>
												<td align="left" class="tblheader" valign="top">
													<bean:message bundle="driverResources" key="driver.map.profilefield" />
														<ec:elitehelp headerBundle="driverResources" 
															text="mapgwdriver.profileattribute" 	
																header="driver.map.profilefield"/>
												</td>
												<td align="left" class="tblheader" valign="top">
													<bean:message bundle="driverResources"
														key="driver.map.defaultvalue" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="mapgwdriver.defaultvalue" 
																	header="driver.map.defaultvalue"/>
												</td>
												<td align="left" class="tblheader" valign="top">
													<bean:message bundle="driverResources"
														key="driver.map.valuemapping" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="mapgwdriver.valuemapping" 
																	header="driver.map.valuemapping"/>		
												</td>
												<td align="left" class="tblheader" valign="top" width="5%">Remove</td>
											</tr>
								<logic:iterate id="obj" name="mapGWAuthFieldMapList"
								type="com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.GatewayFieldMapData">
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
											name="profileField" maxlength="1000" size="28"
											style="width: 100%" tabindex="10"  onfocus="setProfileField(this);"
											value='<bean:write name="obj" property="profileField"/>' /></td>
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
										type="button" tabindex="18" name="c_btnUpdate"
										onclick="validateForm()" id="c_btnCreate2" value="Update"
										class="light-btn"> <input type="reset" tabindex="19"
										name="c_btnCancel"
										onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?/>'"
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
			name="profileField" maxlength="1000" size="28" style="width: 100%" onfocus="setProfileField(this);"
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