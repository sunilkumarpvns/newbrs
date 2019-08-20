<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.Set"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.*"%>
<%@ page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>

<%
		DriverInstanceData driverInstanceData = (DriverInstanceData)request.getSession().getAttribute("driverInstance");
%>
<script language="javascript1.2" src="<%=basePath%>/js/checkpwdstrength.js" type="text/javascript"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script>
	var isValidName;
	function validateForm(){	

		if(isNull(document.forms[0].driverinstname.value)){
			alert('Name must be specified');
		}else if(!isValidName) {
			alert('Enter Valid Driver Name');
			document.forms[0].driverinstname.focus();
			return;
		}else if(document.forms[0].ldapdsid.value == 0){
			alert('Select atleast one datasource value ');
		}else if(isNull(document.forms[0].expiryDatePattern.value)){
			alert('Expiry Data Pattern must be specified.');
		}else if(isNull(document.forms[0].passwordDecryptType.value)){
			alert('Password Decrypt Type must be specified');
		}else if(isNull(document.forms[0].queryMaxExecTime.value)){
			alert('Query Max Exec Time must be specified.');
		}else if(document.getElementById('mappingtbl').getElementsByTagName('tr').length < 2){
 			alert('Atleast one field mapping must be present.');
		}else if(isNaN($('#queryMaxExecTime').val())){
 			alert('Query Max Exec Time must be digit only.');
		}else{		
			if(isValidLogicalNameMapping("mappingtbl","logicalnmVal","ldapAttribute")){
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
function verifyName() {
	var searchName = document.getElementById("driverinstname").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DRIVER%>',searchName,'update','<%=driverInstanceData.getDriverInstanceId()%>','verifyNameDiv');
}

function setColumnsOnUserIdentity(){
	var userIdentityAttributes = document.getElementById("userIdentityAttributes").value;
	retriveDiameterDictionaryAttributes(userIdentityAttributes,"userIdentityAttributes");
}

</script>
<html:form action="/updateDiameterLDAPAuthDriver">

	<html:hidden property="action" />
	<html:hidden property="itemIndex" />
	<html:hidden property="driverInstanceId" />
	<html:hidden property="auditUId" name="updateDiameterLDAPAuthDriverForm"/>

	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" height="15%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="driverResources" key="driver.driverinstancedetails" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="24%">
							<bean:message bundle="driverResources" key="driver.instname" />
								<ec:elitehelp headerBundle="driverResources" 
									text="createdriver.name" header="driver.instname"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%">
							<html:text styleId="driverinstname" onkeyup="verifyName();" property="driverInstanceName" size="30" tabindex="1" maxlength="60" style="width:250px" />
							<font color="#FF0000">*</font>
							<div id="verifyNameDiv" class="labeltext"></div>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.instdesc" />
								<ec:elitehelp headerBundle="driverResources" 
									text="createdriver.description" header="driver.instdesc"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%">
							<html:text tabindex="2" styleId="driverDesp" property="driverDesp" size="30" maxlength="60" style="width:250px" />
						</td>
					</tr>

					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="driverResources" key="driver.driverdetails" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.ldapauthdriver.ds" /> 
								<ec:elitehelp headerBundle="driverResources" 
									text="ldapdriver.ds" header="driver.ldapauthdriver.ds"/> 
						</td>
						<td align="left" class="labeltext" valign="top" width="65%">
							<html:select property="ldapDsId" styleId="ldapdsid" style="width:130px" tabindex="3">
								<html:option value="0">Select</html:option>
								<html:optionsCollection name="updateDiameterLDAPAuthDriverForm" property="ldapDSList" label="name" value="ldapDsId" />
							</html:select>
						</td>
					</tr>
					<%-- 
				<tr>
					<td align="left" class="labeltext" valign="top" width="35%">
						<bean:message bundle="driverResources" key="driver.ds.status.check.interval" />(sec)</td>
					<td align="left" class="labeltext" valign="top" width="65%">
						<html:text styleId="statuscheckinterval" property="dsStatusCheckInterval" size="30" maxlength="60" /><font color="#FF0000"> *</font>
					</td>
				</tr>
				--%>
					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.expirydatapattern" /> 
								<ec:elitehelp headerBundle="driverResources" 
									text="ldapdriver.expirydate" header="driver.expirydatapattern"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%">
							<html:select property="expiryDatePattern" styleId="expiryDatePattern" style="width:130px" tabindex="4">
								<html:option value="MM/dd/yyyy">MM/dd/yyyy</html:option>
							</html:select> 
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.pwd.decrypt.type" /> 
								<ec:elitehelp headerBundle="driverResources" 
									text="ldapdriver.pwdderypt" header="driver.pwd.decrypt.type"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%">
							<html:select property="passwordDecryptType" styleId="passwordDecryptType" style="width:130px" tabindex="5">
								<html:option value="0">0</html:option>
							</html:select> <font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.ldapauthdriver.searchscope" /> 
								<ec:elitehelp headerBundle="driverResources" 
									text="ldapauthdriver.searchscope" header="driver.ldapauthdriver.searchscope"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%">
							<html:select property="searchScope" styleId="searchScope" style="width:130px" tabindex="6">
								<html:optionsCollection name="updateDiameterLDAPAuthDriverForm" property="subTreeOptions" label="value" value="key"/>
							</html:select> <font color="#FF0000"> *</font>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.max.exec.time" />
								<bean:message key="general.miliseconds"/>
									<ec:elitehelp headerBundle="driverResources" 
										text="ldapdriver.queryexectime" header="driver.max.exec.time"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%">
							<html:text styleId="queryMaxExecTime" property="queryMaxExecTime" size="20" maxlength="10" tabindex="7" style="width:250px" />
							<font color="#FF0000"> *</font>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.useridentityattributes" />
								<ec:elitehelp headerBundle="driverResources" 
									text="driver.usridentity" header="driver.useridentityattributes"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%">
							<input type="text" name="userIdentityAttributes" tabindex="8" id="userIdentityAttributes" size="60" maxlength="256" autocomplete="off" onkeyup="setColumnsOnUserIdentity();" style="width: 250px" value="<bean:write name="updateDiameterLDAPAuthDriverForm" property="userIdentityAttributes"/>" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="driverResources" key="driver.ldapauthdriver.searchfilter" /> 
								<ec:elitehelp headerBundle="driverResources" 
									text="ldapauthdriver.searchfilter" header="driver.ldapauthdriver.searchfilter"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="70%">
							<html:text property="searchFilter" tabindex="9" size="60" maxlength="256" style="width: 250px"></html:text>
						</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="driverResources" key="driver.ldapfieldmap" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" colspan="2">
							<input type="button" onclick='addNewRow("dbMappingTable","mappingtbl","logicalnmVal",true);' value=" Add " class="light-btn" tabindex="10">
						</td>
					</tr>

				</table>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td align="left" class="labeltext" colspan="3" valign="top" width="97%">
							<table width="98%" id="mappingtbl" cellpadding="0" cellspacing="0" border="0" class="captiontext">
								<tr>
									<td align="left" class="tblheader" valign="top">
										<bean:message bundle="driverResources" 
											key="driver.dia.ldapauthdriver.logicalname" />
												<ec:elitehelp headerBundle="driverResources" 
													text="ldapdriver.logicalname" 
														header="driver.dia.ldapauthdriver.logicalname"/>
									</td>
									<td align="left" class="tblheader" valign="top">
										<bean:message bundle="driverResources" 
											key="driver.dia.ldapauthdriver.ldapattribute"/> 
												<ec:elitehelp headerBundle="driverResources" 
													text="ldapdriver.ldapattribute" 
														header="driver.dia.ldapauthdriver.ldapattribute"/>
									</td>
									<td align="left" class="tblheader" valign="top" width="15%">
										<bean:message bundle="driverResources" key="driver.defaultvalue" />
											<ec:elitehelp headerBundle="driverResources" 
												text="driver.defaultvalue" 
													header="driver.dia.ldapauthdriver.defaultvalue"/>
									</td>
									<td align="left" class="tblheader" valign="top">
										<bean:message bundle="driverResources" key="driver.dia.ldapauthdriver.valuemap" />
											<ec:elitehelp headerBundle="driverResources" 
												text="driver.valuemapping" 
													header="driver.dia.ldapauthdriver.valuemap"/>
									</td>
									<td align="left" class="tblheader" valign="top" width="5%">Remove</td>
								</tr>
								<logic:iterate id="obj" name="ldapAuthFieldMapList" type="com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.LDAPAuthFieldMapData">
									<tr>
										<td class="allborder">
											<select class="noborder" name="logicalnmVal" id="logicalnmVal" style="width: 100%" tabindex="10" onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
												<option value='<bean:write name="obj" property="nameValuePoolData.value"/>'> 
													<bean:write name="obj" property="nameValuePoolData.name" />
												</option>
											</select>
										</td>
										<td class="tblrows">
											<input class="noborder" type="text" name="ldapAttribute" maxlength="1000" size="28" style="width: 100%" tabindex="10" onfocus="setAutoCompleteData(this);" value='<bean:write name="obj" property="ldapAttribute"/>' /></td>
										<td class="tblrows">
											<input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width: 100%" tabindex="10" value='<bean:write name="obj" property="defaultValue"/>' />
										</td>
										<td class="tblrows">
											<input class="noborder" type="text" name="valueMapping" maxlength="1000" size="30" style="width: 100%" tabindex="10" value='<bean:write name="obj" property="valueMapping"/>' />
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
							<input type="button" name="c_btnCreate" tabindex="11" id="c_btnCreate2" value=" Update " class="light-btn" onclick="validateForm()">
							<input type="reset" name="c_btnDeletePolicy" tabindex="12" onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?'" value="Cancel" class="light-btn">
						</td>
					</tr>
				</table>

			</td>
		</tr>


	</table>
</html:form>
<!-- Mapping Table Row template -->
<table style="display: none;" id="dbMappingTable">
	<tr>
		<td class="allborder">
			<select class="noborder" name="logicalnmVal" id="logicalnmVal" style="width: 100%" tabindex="10" onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
			</select>
		</td>
		<td class="tblrows">
			<input class="noborder" type="text" name="ldapAttribute" maxlength="1000" size="28" style="width: 100%" tabindex="10" onfocus="setAutoCompleteData(this);" /></td>
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
</table>


