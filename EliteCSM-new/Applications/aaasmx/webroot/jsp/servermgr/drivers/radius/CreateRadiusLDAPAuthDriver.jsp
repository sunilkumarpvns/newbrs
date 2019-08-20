<%@page import="com.elitecore.elitesm.util.constants.AccountFieldConstants"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.LDAPAuthFieldMapData"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.web.driver.radius.forms.CreateRadiusLDAPAuthDriverForm"%>
<%
	String basePath = request.getContextPath();	
%>

<script language="javascript"
	src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script language="javascript"
	src="<%=request.getContextPath()%>/js/driver/radius-dbauth-driver.js"></script>
<script language="javascript">
	
	$(document).ready(function() {
	
	var logicalNameData =eval(('<bean:write name="logicalNameData"/>').replace(new RegExp("&quot;", 'g'),"\""));
	/* set Logical Data JsonObject */
	setLogicalNameData(logicalNameData);
	
	/* set Logical Name drop down for default value */ 
	setLogicalnameDropDown("mappingtbl","logicalnmVal",false);
	
	/* bind click event of delete image */
	$('#mappingtbl td img.delete').live('click',function() {
		 $(this).parent().parent().remove(); 
		 setLogicalnameDropDown("mappingtbl","logicalnmVal",false); /* set Logical Name drop down after remove row */
	});
});		
 	function validateForm(){	

 		if(document.forms[0].ldapDsId.value == 0){
 			alert('Select atleast one datasource value ');
 		}else if(isNull(document.forms[0].expiryDatePattern.value)){
 			alert('Expiry Data Pattern must be specified.');
 		}else if(isNull(document.forms[0].passwordDecryptType.value)){
 			alert('Password Decrypt Type must be specified');
 		}else if(isNull(document.forms[0].queryMaxExecTime.value)){
 			alert('Query Max Exec Time must be specified.');
 		}else if(isNull(document.forms[0].maxQueryTimeoutCnt.value)){
 			alert('Max Query Timeout Count must be specified.');
 		}else if(isNaN(document.forms[0].maxQueryTimeoutCnt.value)){
 			alert('Max Query Timeout Count must be any numeric.');
 		}else if(document.forms[0].maxQueryTimeoutCnt.value <= 0){
 			alert('Max Query Timeout Count must be greater than zero.');
 		}else if(document.getElementById('mappingtbl').getElementsByTagName('tr').length < 2){
 			alert('Atleast one field mapping must be present.');
 		}else if(isNaN($('#queryMaxExecTime').val())){
 			alert('Query Max Exec Time must be digit only.');
 		}else{
 			if(isValidLogicalNameMapping("mappingtbl","logicalnmVal","ldapAttribute")){
 				document.forms[0].action.value = 'create';
 	 			document.forms[0].submit();
			} 		 			
 		}		
	
	}
	function setColumnsOnUserIdentity(){
		var userIdentityAttributes = document.getElementById("userIdentityAttributes").value;
		retriveRadiusDictionaryAttributes(userIdentityAttributes,"userIdentityAttributes");
	}

</script>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<html:form action="/createRadiusLDAPAuthDriver">

	<html:hidden name="createRadiusLDAPAuthDriverForm" property="action" />
	<html:hidden name="createRadiusLDAPAuthDriverForm" property="driverInstanceName" />
	<html:hidden name="createRadiusLDAPAuthDriverForm" property="driverDesp" />
	<html:hidden name="createRadiusLDAPAuthDriverForm" property="driverRelatedId" />
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
									<td class="table-header"><bean:message bundle="driverResources" key="driver.createldapauthDriver" /></td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td>
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td align="left" class="tblheader-bold" valign="top" colspan="3"><bean:message bundle="driverResources" key="driver.ldapauthdriver.details" /></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="30%">
													<bean:message bundle="driverResources" key="driver.ldapauthdriver.ds" /> 
													<ec:elitehelp headerBundle="driverResources" 
													text="ldapdriver.ds" header="driver.ldapauthdriver.ds"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="70%">
													<html:select property="ldapDsId" styleId="ldapDsId" style="width:130px" tabindex="1">
														<html:option value="0">Select</html:option>
														<html:optionsCollection name="createRadiusLDAPAuthDriverForm" property="ldapDSList" label="name" value="ldapDsId" />
													</html:select>
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<%-- 
						<tr>
							<td align="left" class="labeltext" valign="top" width="30%">
							<bean:message bundle="driverResources" key="driver.ds.status.check.interval" />(sec)</td>
							<td align="left" class="labeltext" valign="top" width="70%">
								<html:text styleId="statuscheckinterval" property="dsStatusCheckInterval" size="30" maxlength="60" /><font color="#FF0000"> *</font>
							</td>							
						</tr>
						--%>
											<tr>
												<td align="left" class="captiontext" valign="top" width="30%">
													<bean:message bundle="driverResources" key="driver.expirydatapattern" /> 
													<ec:elitehelp headerBundle="driverResources" 
													text="ldapdriver.expirydate" header="driver.expirydatapattern"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="70%">
													<html:select property="expiryDatePattern" styleId="expiryDatePattern" style="width:130px" tabindex="2">
														<html:option value="MM/dd/yyyy">MM/dd/yyyy</html:option>
													</html:select>
													<font color="#FF0000"> *</font>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="30%">
													<bean:message bundle="driverResources" key="driver.pwd.decrypt.type" /> 
													<ec:elitehelp headerBundle="driverResources" 
													text="ldapdriver.pwdderypt" header="driver.pwd.decrypt.type"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="70%">
													<html:select property="passwordDecryptType" styleId="passwordDecryptType" style="width:130px" tabindex="3">
														<html:option value="0">0</html:option>
													</html:select>
													<font color="#FF0000"> *</font>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="30%">
													<bean:message bundle="driverResources" key="driver.ldapauthdriver.searchscope" />
													<ec:elitehelp headerBundle="driverResources" 
													text="ldapauthdriver.searchscope" header="driver.ldapauthdriver.searchscope"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="70%">
													<html:select property="searchScope" styleId="searchScope" style="width:130px" tabindex="4" >
														<html:optionsCollection name="createRadiusLDAPAuthDriverForm" property="subTreeOptions" label="value" value="key"/>
													</html:select>
													<font color="#FF0000"> *</font>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="30%">
													<bean:message bundle="driverResources" key="driver.max.exec.time" />(Sec) 
													<ec:elitehelp headerBundle="driverResources" 
													text="ldapdriver.queryexectime" header="driver.max.exec.time"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="70%">
													<html:text styleId="queryMaxExecTime" property="queryMaxExecTime" size="20" maxlength="20" style="width:200px" tabindex="5" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="30%">
													<bean:message bundle="driverResources" key="driver.max.query.timeout.count" /> 
													<ec:elitehelp headerBundle="driverResources" 
													text="ldapdriver.maxquerytimeoutcount" header="driver.max.query.timeout.count"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="70%">
													<html:text styleId="maxQueryTimeoutCnt" property="maxQueryTimeoutCnt" size="20" maxlength="20" style="width:200px" tabindex="6" />
													<font color="#FF0000">*</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="30%">
													<bean:message bundle="driverResources" key="driver.useridentityattributes" /> 
													<ec:elitehelp headerBundle="driverResources" 
													text="driver.usridentity" header="driver.useridentityattributes"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="70%">
													<input type="text" name="userIdentityAttributes" id="userIdentityAttributes" tabindex="7" size="60" maxlength="256" autocomplete="off" onkeyup="setColumnsOnUserIdentity();" style="width: 250px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="30%">
													<bean:message bundle="driverResources" key="driver.ldapauthdriver.searchfilter" /> 
													<ec:elitehelp headerBundle="driverResources" 
													text="ldapauthdriver.searchfilter" header="driver.ldapauthdriver.searchfilter"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="70%">
													<input type="text" name="searchFilter" id="searchFilter" tabindex="8" size="60" maxlength="256"  style="width: 250px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="tblheader-bold" valign="top" colspan="3">
													<bean:message bundle="driverResources" key="driver.ldapfieldmap" /></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" colspan="2">
													<input type="button" value=" Add " class="light-btn" style="size: 140px" onclick='addNewRow("dbMappingTable","mappingtbl","logicalnmVal",false);' tabindex="9">
												</td>
											</tr>

										</table>
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td align="left" class="captiontext" colspan="3" valign="top" width="97%">
													<table width="98%" id="mappingtbl" cellpadding="0" cellspacing="0" border="0">
														<tr>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources" key="driver.logicalname" />
																<ec:elitehelp headerBundle="driverResources" 
																text="ldapdriver.logicalname" header="driver.logicalname"/>
															</td>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources" key="driver.ldapauthdriver.ldapattribute" />
																<ec:elitehelp headerBundle="driverResources"
																text="ldapdriver.ldapattribute" header="driver.ldapauthdriver.ldapattribute"/> 
															</td>
															<td align="left" class="tblheader" valign="top" width="15%">
																<bean:message bundle="driverResources" key="driver.defaultvalue" /> 
																<ec:elitehelp headerBundle="driverResources" 
																text="driver.defaultvalue" header="driver.defaultvalue"/>
															</td>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources" key="driver.valuemapping" />
																<ec:elitehelp headerBundle="driverResources" 
																text="driver.valuemapping" header="driver.valuemapping"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="5%">Remove</td>
														</tr>
														<logic:iterate id="obj" name="defaultMapping" type="com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.LDAPAuthFieldMapData">
															<tr>
																<td class="allborder">
																	<select class="noborder" name="logicalnmVal" id="logicalnmVal" style="width: 100%" tabindex="10" onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",false);'>
																		<option value='<bean:write name="obj" property="nameValuePoolData.value"/>'>
																			<bean:write name="obj" property="nameValuePoolData.name" />
																		</option>
																	</select>
																</td>
																<td class="tblrows">
																	<input class="noborder" type="text" name="ldapAttribute" maxlength="1000" size="28" style="width: 100%" tabindex="10" value='<bean:write name="obj" property="ldapAttribute"/>' />
																</td>
																<td class="tblrows">
																	<input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width: 100%" tabindex="10" />
																</td>
																<td class="tblrows">
																	<input class="noborder" type="text" name="valueMapping" maxlength="1000" size="30" style="width: 100%" tabindex="10" />
																</td>
																<td class="tblrows" align="center" colspan="3">
																	<img value="top" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" tabindex="10" />
																</td>
															</tr>
														</logic:iterate>
													</table>
												</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2">
													<input type="button" name="c_btnCreate" id="c_btnCreate2" value=" Create " class="light-btn" onclick="validateForm()" tabindex="11"> 
													<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?'" value="Cancel" class="light-btn" tabindex="9">
												</td>
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
		<td class="allborder">
			<select class="noborder" name="logicalnmVal" id="logicalnmVal" style="width: 100%" tabindex="10" onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",false);'/>
		</td>
		<td class="tblrows">
			<input class="noborder" type="text" name="ldapAttribute" maxlength="1000" size="28" style="width: 100%" tabindex="10"  />
		</td>
		<td class="tblrows">
			<input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width: 100%" tabindex="10" /></td>
		<td class="tblrows">
			<input class="noborder" type="text"name="valueMapping" maxlength="1000" size="30" style="width: 100%" tabindex="10" />
		</td>
		<td class="tblrows" align="center" colspan="3">
			<img value="top" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" tabindex="10" />
		</td>
	</tr>
</table>
<script>
setTitle('<bean:message bundle="driverResources" key="driver.driver"/>'); 
</script>
