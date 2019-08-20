<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page
	import="com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.AddConcurrentLoginPolicyForm"%>
<%@page
	import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%
	String statusVal=(String)request.getParameter("status");
	String concurrentLoginPolicyId=(String)request.getParameter("concurrentLoginPolicyId");
    String basePath = request.getContextPath();
	AddConcurrentLoginPolicyForm addConcurrentLoginPolicyForm = (AddConcurrentLoginPolicyForm)request.getSession().getAttribute("addConcurrentLoginPolicyForm");
	addConcurrentLoginPolicyForm.setLogin(0);
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
var isValidName;

function customValidate()
{
	var retVar=true;
	
	if(isNull(document.forms[0].name.value)){
		alert('Conc. Login Policy Name must be specified');
		document.forms[0].name.focus();
		return false;
	}

	if(!isValidName) {
		alert('Enter Valid Policy Name');
		document.forms[0].name.focus();
		return false;
	}

	if(document.forms[0].name.value == '' ){
		alert('Conc. Login Policy Name must be specified');
		return false;
	}
	if(document.forms[0].serviceWiseRadio.checked==true &&  document.forms[0].attribute.value == '0'){
		alert('Attribute must be selected');
		document.forms[0].attribute.focus();
		return false;
	}
	
	if(!limitCheck(document.forms[0].description,255)){
       alert("Description should not greater than "+255+ " character.");
       document.forms[0].description.focus();
       return false;
    }
	var regexpNum = /^\d+$/;
		if(document.addConcurrentLoginPolicyForm.maxLogin[1].checked){
			document.addConcurrentLoginPolicyForm.login.disabled = false;
			document.addConcurrentLoginPolicyForm.login.value = '-1';
			//alert('unlimited');
		}else{
		
			if(!regexpNum.test(document.addConcurrentLoginPolicyForm.login.value)  ){
			alert('Invalid value of Max. Concurrent Login. It must be zero or positive Integer.');
			return false;
            }
        } 
	
	return retVar;
}

function changeButtonCreate(){

	document.forms[0].c_btnCreate.value="   Create   ";
}
function changeButtonNext(){
	document.forms[0].c_btnCreate.value="   Next   ";
}
$(document).ready(function() {
	var chkBoxVal='<%=statusVal%>';
	if(chkBoxVal=='Hide'){
		document.getElementById("status").checked=false;
	}else{
		document.getElementById("status").checked=true;
	}

	var chkBoxVal='<%=concurrentLoginPolicyId%>';
	if(chkBoxVal=='G'){
		$('input:radio[value=Group]').attr('checked', 'checked');
	}else{
		$('input:radio[value=Individual]').attr('checked', 'checked');
	}
});
function setLoginLimit(value) {
	if (value=="Limited") {
	    document.addConcurrentLoginPolicyForm.login.disabled = false;
		document.addConcurrentLoginPolicyForm.login.value="";
		
	} else if (value=="Unlimited") {
		document.addConcurrentLoginPolicyForm.login.value = "-1";
		document.addConcurrentLoginPolicyForm.login.disabled = true;
		
	} else {
		document.addConcurrentLoginPolicyForm.login.value="";
	}
}

function _changeLogin(){
document.addConcurrentLoginPolicyForm.login.value="-1";
}

function changePolicyMode(){
	var generalElement = document.getElementById("generalRadio");
	var attributeElement = document.getElementById("attribute");
	
	if(generalElement.checked==true){
		attributeElement.disabled = true;
	}else{
		attributeElement.disabled = false;
	}
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.CONCURRENT_LOGIN_POLICY%>',searchName,'create','','verifyNameDiv');
}

<%--
function setColumnsOnRespAttrTextFields(){
	var respAttrVal = document.getElementById("attribute").value;
	retriveRadiusDictionaryAttributes(respAttrVal,"attribute");
}
--%>

setTitle('<bean:message bundle="radiusResources" key="concurrentloginpolicy.concurrentloginpolicy"/>');
</script>
<html>
<body onload="document.addConcurrentLoginPolicyForm.name.focus();">
	<html:form action="/addConcurrentLoginPolicy">
		<html:hidden name="addConcurrentLoginPolicyForm" styleId="action"
			property="action" value="next" />
		<html:hidden name="addConcurrentLoginPolicyForm" styleId="tempLogin"
			property="tempLogin" value="" />

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
										<td class="table-header"><bean:message bundle="radiusResources"
												key="concurrentloginpolicy.concurrentloginpolicy" /></td>
									</tr>
									<tr>
										<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
									</tr>
									<tr>
										<td colspan="3">
											<table width="100%" name="c_tblCrossProductList"
												id="c_tblCrossProductList" align="right" cellSpacing="0"
												cellPadding="0" border="0">
												<tr>
													<td align="left" class="captiontext" valign="top"
														width="25%">
														<bean:message bundle="radiusResources" key="concurrentloginpolicy.name" /> 
														<ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.name" 
														header="concurrentloginpolicy.name"/>
													</td>
													<td width="30%">&nbsp;&nbsp; <html:text
															name="addConcurrentLoginPolicyForm" tabindex="1"
															onkeyup="verifyName();" styleId="name" property="name"
															size="30" styleClass="flatfields" maxlength="30"
															style="width:250px" />
														<div id="verifyNameDiv" class="labeltext"></div>
													</td>
													<td width="45%" class="labeltext">&nbsp;<html:checkbox
															name="addConcurrentLoginPolicyForm" styleId="status"
															property="status" value="1" />&nbsp;Active
													</td>
												</tr>




												<tr>
													<td align="left" class="captiontext" valign="top" width="12%">
														<bean:message bundle="radiusResources"
														key="concurrentloginpolicy.description" />
														<ec:elitehelp headerBundle="radiusResources" 
														text="concurrentpolicy.description" header="concurrentloginpolicy.description"/>	
													</td>
													<td align="left" valign="top" colspan="2">
														<table width="100%" border="0" cellpadding="0"
															cellspacing="0">
															<tr>
																<td>&nbsp;&nbsp;<html:textarea tabindex="2"
																		styleId="description" property="description" cols="30"
																		rows="4" style="width:250px" />
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top"
														width="12%">
														<bean:message bundle="radiusResources" 
														key="concurrentloginpolicy.concurrentloginpolicytype" />
														<ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.policytype" 
														header="concurrentloginpolicy.concurrentloginpolicytype"/>
													</td>
													<td align="left" valign="top" colspan="2">
														<table width="100%" border="0" cellpadding="0"
															cellspacing="0">
															<tr>
																<td align="left" class="labeltext" valign="top"
																	width="5%">&nbsp; <html:radio
																		name="addConcurrentLoginPolicyForm"
																		styleId="concurrentLoginPolicy"
																		property="concurrentLoginPolicy" value="Individual"
																		tabindex="3" />Individual <html:radio
																		name="addConcurrentLoginPolicyForm"
																		styleId="concurrentLoginPolicy"
																		property="concurrentLoginPolicy" value="Group"
																		tabindex="4" />Group
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top"
														width="12%">
														<bean:message bundle="radiusResources" key="concurrentloginpolicy.maximumconcurrentlogin" />
														<ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.maxlogin" 
														header="concurrentloginpolicy.maximumconcurrentlogin"/>
													</td>
													<td align="left" valign="top" colspan="2">
														<table width="100%" border="0" cellpadding="0"
															cellspacing="0">
															<tr>
																<td align="left" class="labeltext" valign="top"
																	width="5%">&nbsp;&nbsp;<html:radio
																		name="addConcurrentLoginPolicyForm" styleId="maxLogin"
																		property="maxLogin" tabindex="5" value="Limited"
																		onclick="setLoginLimit('Limited')" />Limited
																	&nbsp;&nbsp;&nbsp;&nbsp;<html:text
																		name="addConcurrentLoginPolicyForm" property="login"
																		size="15" styleClass="flatfields" maxlength="30"
																		tabindex="6" /></br> &nbsp;&nbsp;<html:radio
																		name="addConcurrentLoginPolicyForm" tabindex="7"
																		styleId="maxLogin" property="maxLogin"
																		value="Unlimited" onclick="setLoginLimit('Unlimited')" />Unlimited
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top"
														width="12%"><bean:message bundle="radiusResources"
															key="concurrentloginpolicy.policymode" /> 
															<ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.mode" 
															header="concurrentloginpolicy.policymode"/>
													</td>
													<td align="left" valign="top" colspan="2">
														<table width="100%" border="0" cellpadding="0"
															cellspacing="0">
															<tr>
																<td align="left" class="labeltext" valign="top"
																	width="5%">&nbsp;&nbsp;<html:radio
																		name="addConcurrentLoginPolicyForm" tabindex="8"
																		styleId="generalRadio"
																		property="concurrentLoginPolicyMode" value="General"
																		onchange="changePolicyMode()" />General &nbsp;&nbsp;<html:radio
																		name="addConcurrentLoginPolicyForm"
																		styleId="serviceWiseRadio" tabindex="9"
																		property="concurrentLoginPolicyMode"
																		value="Service Wise" onchange="changePolicyMode()" />Service
																	Wise
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top"
														width="12%"><bean:message bundle="radiusResources"
 															key="concurrentloginpolicy.attribute" />
 															<ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.attribute" 
 															header="concurrentloginpolicy.attribute"/>
 													</td>
													<td align="left" valign="top" colspan="2">
														<table width="100%" border="0" cellpadding="0"
															cellspacing="0">
															<tr>
																<td>&nbsp;&nbsp; <html:select property="attribute"
																		styleId="attribute" tabindex="10" value="NAS-Port-Type">
																		<html:option value="0">--Select--</html:option>
																		<html:optionsCollection
																			property="dictionaryParameterDetailList"
																			name="addConcurrentLoginPolicyForm" label="name"
																			value="name" />
																	</html:select> <%-- input type="text" name="attribute" id="attribute" size="30" autocomplete="off" onkeyup="setColumnsOnRespAttrTextFields();" style="width:250px"/--%>
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td colspan="3">&nbsp;</td>
												</tr>

												<tr>
													<td colspan="3">&nbsp;</td>
												</tr>
												<tr>

													<td align="center" valign="middle" colspan="2"><input
														type="submit" name="c_btnCreate" tabindex="11"
														onclick="return customValidate();" value="   Next   "
														class="light-btn">&nbsp;&nbsp; <input type="reset"
														name="c_btnDeletePolicy" tabindex="12"
														onclick="javascript:location.href='<%=basePath%>/initSearchConcurrentLoginPolicy.do?isMenuCall=yes'"
														value=" Cancel " class="light-btn"></td>
												</tr>

											</table>
										</td>
									</tr>
								</table>


							</td>
						</tr>
						<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
						<tr>
							<td class="small-gap" colspan="2">&nbsp;</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</html:form>
</body>
</html>