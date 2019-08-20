<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.web.driver.radius.forms.CreateRadiusDBAuthDriverForm"%>

<%
	String basePath = request.getContextPath();
%>
<script language="javascript"
	src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script language="javascript">
	var flag=false;
	var responseParameterIndex=false;
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
	});		
 	function setResponseParamIndex(obj){
 		if(isNaN(obj.value)){
 			responseParameterIndex=true;
 			flag=true;
 			return;
 		}
 		flag=false;
 	}
	function setColumnsOnUserIdentity(){
		var userIdentityAttributes = document.getElementById("userIdentityAttributes").value;
		retriveRadiusDictionaryAttributes(userIdentityAttributes,"userIdentityAttributes");
	}
	
 	function validateForm(){
 		if(isNull(document.forms[0].http_url.value)){
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
 				document.forms[0].action.value = 'create';
 	 			document.forms[0].submit();
			}
 			
 		}		
	}
</script>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<html:form action="/createRadiusHttpAuthDriver">
	<html:hidden name="createRadiusHttpAuthDriverForm" property="action" />
	<html:hidden name="createRadiusHttpAuthDriverForm"
		property="driverInstanceName" />
	<html:hidden name="createRadiusHttpAuthDriverForm"
		property="driverDesp" />
	<html:hidden name="createRadiusHttpAuthDriverForm"
		property="driverRelatedId" />

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
											bundle="driverResources" key="driver.createhttpauthDriver" />
									</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td>
										<table cellpadding="0" cellspacing="0" border="0" width="100%">

											<tr>
												<td align="left" class="tblheader-bold" valign="top"
													width="5%" colspan="3"><bean:message
														bundle="driverResources"
														key="driver.httpauthdriver.details" /></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="26%">
													<bean:message bundle="driverResources" key="driver.httpurl" /> 
													<ec:elitehelp headerBundle="driverResources" 
													text="httpdriver.url" header="driver.httpurl"/>
												</td>
												<td align="left" class="labeltext" valign="top"><html:text
														styleId="http_url" property="http_url" size="30"
														maxlength="255" style="width:250px" tabindex="1" /><font
													color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="26%">
													<bean:message bundle="driverResources" key="driver.mapgw.statuscheckduration" />
													<ec:elitehelp headerBundle="driverResources" 
													text="httpdriver.statuscheckduration" header="driver.mapgw.statuscheckduration"/>
												</td>
												<td align="left" class="labeltext" valign="top"><html:text
														styleId="statusCheckDuration"
														property="statusCheckDuration" size="10" maxlength="10"
														style="width:250px" tabindex="2" /><font color="#FF0000">
														*</font></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="26%">
													<bean:message bundle="driverResources" key="driver.querytimeoutcount" />
													<ec:elitehelp headerBundle="driverResources" 
													text="httpdriver.querytimeout" header="driver.querytimeoutcount"/>
												</td>
												<td align="left" class="labeltext" valign="top"><html:text
														styleId="maxQueryTimeoutCount"
														property="maxQueryTimeoutCount" size="10" maxlength="10"
														style="width:250px" tabindex="3" /><font color="#FF0000">
														*</font></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="26%">
													<bean:message bundle="driverResources" key="driver.expirydatepatterns" />
													<ec:elitehelp headerBundle="driverResources" 
													text="httpdriver.expdateformat" header="driver.expirydatepatterns"/>
												</td>
												<td align="left" class="labeltext" valign="top"><html:text
														styleId="expiryDateFormats" property="expiryDateFormats"
														size="20" maxlength="255" style="width:250px" tabindex="4" /><font
													color="#FF0000"> *</font></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="26%">
													<bean:message bundle="driverResources"
													key="driver.useridentityattributes" /> 
													<ec:elitehelp headerBundle="driverResources" 
													text="driver.usridentity" header="driver.useridentityattributes"/>
												</td>
												<td align="left" class="labeltext" valign="top"><input
													type="text" name="userIdentityAttributes"
													id="userIdentityAttributes" size="20" maxlength="256"
													autocomplete="off" onkeyup="setColumnsOnUserIdentity();"
													style="width: 250px" tabindex="5" /></td>
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
													colspan="2"><input type="button"
													onclick='addNewRow("dbMappingTable","mappingtbl","logicalnmVal",true);'
													value=" Add " class="light-btn" style="size: 140px"
													tabindex="6"></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" colspan="3"
													valign="top" width="97%">
													<table width="98%" id="mappingtbl" cellpadding="0"
														cellspacing="0" border="0">
														<tr>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources" key="driver.logicalname" />
																<ec:elitehelp headerBundle="driverResources" 
																text="httpdriver.logicalname" header="driver.logicalname"/>
															</td>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources" key="driver.responseparamindex" />
																<ec:elitehelp headerBundle="driverResources" 
																text="httpdriver.responseParameterIndex" header="driver.responseparamindex"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="15%">
																<bean:message bundle="driverResources" key="driver.defaultvalue" />
																<ec:elitehelp headerBundle="driverResources" 
																text="httpdriver.defaultvalue" header="driver.defaultvalue"/>
															</td>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources" key="driver.valuemapping" />
																<ec:elitehelp headerBundle="driverResources" 
																text="httpdriver.valuemapping" header="driver.valuemapping"/>
															</td>
															<td align="left" class="tblheader" valign="top"
																width="5%">Remove</td>
														</tr>
														<logic:iterate id="obj" name="defaultMapping"
															type=" com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverFieldMapData">
															<tr>
																<td class="allborder"><select class="noborder"
																	name="logicalnmVal" id="logicalnmVal"
																	style="width: 100%" tabindex="10"
																	onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
																		<option
																			value='<bean:write name="obj" property="nameValuePoolData.value"/>'>
																			<bean:write name="obj"
																				property="nameValuePoolData.name" />
																		</option>
																</select></td>
																<td class="tblrows"><input class="noborder"
																	type="text" name="responseParamIndex" maxlength="10"
																	size="10" style="width: 100%" tabindex="10" 
																	value='<bean:write name="obj" property="responseParamIndex"/>' onchange="setResponseParamIndex(this);"/></td>
																<td class="tblrows"><input class="noborder"
																	type="text" name="defaultValue" maxlength="1000"
																	size="28" style="width: 100%" tabindex="10" /></td>
																<td class="tblrows"><input class="noborder"
																	type="text" name="valueMapping" maxlength="1000"
																	size="30" style="width: 100%" tabindex="10" /></td>
																<td class="tblrows" align="center" colspan="3"><img
																	value="top" src="<%=basePath%>/images/minus.jpg"
																	class="delete"
																	style="padding-right: 5px; padding-top: 5px;"
																	height="14" tabindex="10" /></td>
															</tr>
														</logic:iterate>
													</table>
												</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2"><input
													type="button" name="c_btnCreate" id="c_btnCreate2"
													value=" Create " class="light-btn" onclick="validateForm()"
													tabindex="7"> <input type="reset"
													name="c_btnDeletePolicy"
													onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?'"
													value="Cancel" class="light-btn" tabindex="8"></td>
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
<table style="display: none;" id="dbMappingTable">
	<tr>
		<td class="allborder"><select class="noborder"
			name="logicalnmVal" id="logicalnmVal" style="width: 100%"
			tabindex="10"
			onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);' >
		</select></td>
		<td class="tblrows"><input class="noborder" type="text" 
			name="responseParamIndex" maxlength="10" size="10" style="width: 100%"  onchange="setResponseParamIndex(this);" 
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
<script>
setTitle('<bean:message bundle="driverResources" key="driver.driver"/>'); 
</script>

