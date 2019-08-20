<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.web.driver.radius.forms.CreateWebServiceAuthDriverForm"%>

<%
	String basePath = request.getContextPath();
%>

<script language="javascript1.2" src="<%=basePath%>/js/checkpwdstrength.js" type="text/javascript"></script>
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
});		

	var webMethodKeyArray = new Array();
	var webMethodKeyArrayIndex = 0;
	<logic:iterate id="obj1" name="createWebServiceAuthDriverForm" property="webMethodKeyList">
		webMethodKeyArray[webMethodKeyArrayIndex++] = '<bean:write name="obj1" property="value"/>';
	</logic:iterate>

	function setWebMethodKey(webMethodKey){
		$(webMethodKey).autocomplete({
	    source: webMethodKeyArray
	});
	} 
 	function validateForm(){	
 		if(isNull(document.forms[0].serviceAddress.value)){
            alert('Service Address must be specified.');      
        }else if(isNull(document.forms[0].imsiAttribute.value)){
           alert('IMSI Attribute must be specified.');        	 
        }else if(isNull(document.forms[0].maxQueryTimeoutCnt.value)){
           alert('Max Query Timeout Count must be specified.');        	 
        }else if(isNaN(document.forms[0].maxQueryTimeoutCnt.value)){
           alert('Max Query Timeout Count must be numeric.');        	 
        }else if(isNull(document.forms[0].statusChkDuration.value)){
           alert('Status Check Duration must be specified.');        	 
        }else if(isNaN(document.forms[0].statusChkDuration.value)){
           alert('Status Check Duration must be numeric.');  
        }else{               	 
        	if(isValidLogicalNameMapping("mappingtbl","logicalnmVal","webMethodKey")){
 				document.forms[0].action.value = 'create';
 	 			document.forms[0].submit();
			}
        }		
	}
	
 
 	function setIMSIAttributes(){
 		var imsiAttributeVal = document.getElementById("imsiAttribute").value;
 		retriveRadiusDictionaryAttributes(imsiAttributeVal,"imsiAttribute");
 	}
 	
 	function setColumnsOnUserIdentity(){
		var userIdentityAttributes = document.getElementById("userIdentityAttributes").value;
		retriveRadiusDictionaryAttributes(userIdentityAttributes,"userIdentityAttributes");
	}
 	
</script>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<html:form action="/createWebServiceAuthDriver">

	<html:hidden name="createWebServiceAuthDriverForm" property="action"
		value="create" />
	<html:hidden name="createWebServiceAuthDriverForm"
		property="driverInstanceName" />
	<html:hidden name="createWebServiceAuthDriverForm"
		property="driverDesp" />
	<html:hidden name="createWebServiceAuthDriverForm"
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
											bundle="driverResources"
											key="driver.webserviceauthdriver.driver" /></td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td>
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="driverResources"
														key="driver.webserviceauthdriver.serviceaddress" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="webservicedriver.address" 
																	header="driver.webserviceauthdriver.serviceaddress"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="76%">
													<html:text styleId="serviceAddress"
														property="serviceAddress" size="60" maxlength="70"
														style="width:250px" tabindex="1" /><font color="#FF0000">
														*</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="driverResources" 
														key="driver.webserviceauthdriver.imsiattribute" />
															<ec:elitehelp headerBundle="driverResources" 
																text="webservicedriver.imsi" 
																	header="driver.webserviceauthdriver.imsiattribute"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="76%">
													<input type="text" name="imsiAttribute" id="imsiAttribute"
													size="30" autocomplete="off" maxlength="128"
													onkeyup="setIMSIAttributes();" tabindex="2"
													style="width: 250px" value="0:1" /> <font color="#FF0000">
														*</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="driverResources"
														key="driver.webserviceauthdriver.maxquerytimeoutcount" />
															<ec:elitehelp headerBundle="driverResources" 
																text="webservicedriver.maxquerytimeoutcount" 
																	header="driver.webserviceauthdriver.maxquerytimeoutcount"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="76%">
													<html:text styleId="maxQueryTimeoutCnt" tabindex="3"
														property="maxQueryTimeoutCnt" size="60" maxlength="20"
														style="width:250px" /><font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="driverResources"
														key="driver.webserviceauthdriver.statuschkduration" />
															<ec:elitehelp headerBundle="driverResources" 
																text="webservicedriver.statuscheckduration" 
																	header="driver.webserviceauthdriver.statuschkduration" />
												</td>
												<td align="left" class="labeltext" valign="top" width="76%">
													<html:text styleId="statusChkDuration" tabindex="4"
														property="statusChkDuration" size="60" maxlength="20"
														style="width:250px" /><font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="driverResources"
														key="driver.useridentityattributes" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="driver.usridentity" header="driver.useridentityattributes"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="76%">
													<input type="text" name="userIdentityAttributes"
													tabindex="5" id="userIdentityAttributes" size="60"
													maxlength="256" autocomplete="off"
													onkeyup="setColumnsOnUserIdentity();" style="width: 250px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="tblheader-bold" valign="top"
													colspan="4"><bean:message bundle="driverResources"
														key="driver.webserviceauthdriver.wsmapping" /></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top"
													colspan="3" id="button"><input type="button"
													tabindex="6" onclick='addNewRow("dbMappingTable","mappingtbl","logicalnmVal",true);' value=" Add Mapping"
													class="light-btn" style="size: 140px"></td>
											</tr>
											<tr>
												<td width="10" class="small-gap">&nbsp;</td>
												<td class="small-gap" colspan="2">&nbsp;</td>
											</tr>
											<tr>
												<td width="97%" colspan="4" valign="top" class="captiontext">
													<table cellSpacing="0" cellPadding="0" width="97%"
														border="0" id="mappingtbl">
														<tr>
															<td align="left" class="tblheader" valign="top" id="tbl_attrid">
																<bean:message bundle="driverResources" 
																	key="driver.webserviceauthdriver.logicalname" /> 
																		<ec:elitehelp headerBundle="driverResources" 
																			text="webservicedriver.logicalname" 
																				header="driver.webserviceauthdriver.logicalname"/>
															</td>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources"
																	key="driver.webserviceauthdriver.wsmethodkey" /> 
																		<ec:elitehelp headerBundle="driverResources" 
																			text="webservicedriver.webservicekey" 
																				header="driver.webserviceauthdriver.wsmethodkey"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="15%">
																<bean:message bundle="driverResources"
																	key="driver.webserviceauthdriver.defaultvalue" /> 
																		<ec:elitehelp headerBundle="driverResources" 
																			text="webservicedriver.defaultvalue" 
																				header="driver.webserviceauthdriver.defaultvalue"/>
															</td>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources"
																	key="driver.webserviceauthdriver.valuemapping" /> 
																		<ec:elitehelp headerBundle="driverResources" 
																			text="webservicedriver.valuemapping" header="driver.webserviceauthdriver.valuemapping"/>
															</td>
															<td align="left" class="tblheader" valign="top"
																width="5%">Remove</td>
														</tr>
														<logic:iterate id="obj" name="defaultMapping" type="com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebMethodKeyMapRelData">
														<tr>
															<td class="allborder">
																<select class="noborder"  name="logicalnmVal"  id="logicalnmVal" style="width:100%" tabindex="10" onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
																	<option value='<bean:write name="obj" property="nameValuePoolData.value"/>'><bean:write name="obj" property="nameValuePoolData.name"/> </option>
																</select>
															</td>
															<td class="tblrows"><input class="noborder" type="text" name="webMethodKey"  maxlength="1000" size="28" style="width:100%" tabindex="10"  value='<bean:write name="obj" property="webMethodKey"/>' onfocus="setWebMethodKey(this);"/></td>
															<td class="tblrows"><input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width:100%" tabindex="10" value='<bean:write name="obj" property="defaultValue"/>'/></td>
															<td class="tblrows"><input class="noborder" type="text" name="valueMapping" maxlength="1000" size="30" style="width:100%" tabindex="10" value='<bean:write name="obj" property="valueMapping"/>'/></td>
															<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="10"/></td>
														</tr>
														</logic:iterate>
													</table>
												</td>
											</tr>
											<tr>
												<td width="10" class="small-gap">&nbsp;</td>
												<td class="small-gap" colspan="2">&nbsp;</td>
											</tr>
										</table>

										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2"><input
													type="button" name="c_btnCreate" id="c_btnCreate2"
													value=" Create " tabindex="7" class="light-btn"
													onclick="validateForm()"> <input type="reset"
													name="c_btnDeletePolicy" tabindex="8"
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
			<td class="tblrows"><input class="noborder" type="text" name="webMethodKey"  maxlength="1000" size="28"  style="width:100%" tabindex="10" onfocus="setWebMethodKey(this);" /></td>
			<td class="tblrows"><input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width:100%" tabindex="10"/></td>
			<td class="tblrows"><input class="noborder" type="text" name="valueMapping" maxlength="1000" size="30" style="width:100%" tabindex="10"/></td>
			<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="10"/></td>
  		</tr>
	</table>
	<script>
	setTitle('<bean:message bundle="driverResources" key="driver.driver"/>'); 
	</script>
