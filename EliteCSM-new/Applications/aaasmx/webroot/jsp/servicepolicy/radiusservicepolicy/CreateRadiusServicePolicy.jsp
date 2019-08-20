<%@page import="com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form.CreateRadiusServicePolicyForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	String basePath = request.getContextPath();
	CreateRadiusServicePolicyForm createRadiusServicePolicyForm=(CreateRadiusServicePolicyForm)request.getSession().getAttribute("createRadiusServicePolicyForm");
	List authMethodTypeList = createRadiusServicePolicyForm.getAuthMethodTypeDataList();
	List lstItemListSelected = (List) request.getAttribute("itemListSelected");
	String statusVal=(String)request.getParameter("status");
%>
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script language="javascript">

$(document).ready(function() {
	setExpressionData("Radius");
	setHotlinePolicy();
	
	$('.radiusMessage').attr('checked', true);
	
	var chkBoxVal='<%=statusVal%>';
	if(chkBoxVal=='Inactive'){
		document.getElementById("status").checked=false;
	}else{
		document.getElementById("status").checked=true;
	}
	
	$('#authentication').change(function(){
		   if($(this).is(":checked")){
		      $("#ruleSetAuth").removeAttr("disabled");
		      $("#authResponseBehavior").removeAttr("disabled");
		      $("#authAttributes").removeAttr("disabled");
		  	  $(this).val(true);
		   }else{
		      $("#ruleSetAuth").attr("disabled" , "disabled");
		      $("#authResponseBehavior").attr("disabled" , "disabled");
		      $("#authAttributes").attr("disabled" , "disabled");
		      $(this).val(false);
		   }
	});
	
	$('#accounting').change(function(){
		   if($(this).is(":checked")){
		      $("#ruleSetAcct").removeAttr("disabled");
		      $("#acctResponseBehavior").removeAttr("disabled");
		      $("#acctAttributes").removeAttr("disabled");
		   	  $(this).val(true);
		   }else{
		      $("#ruleSetAcct").attr("disabled" , "disabled");
		      $("#acctResponseBehavior").attr("disabled" , "disabled");
		      $("#acctAttributes").attr("disabled" , "disabled");
		      $(this).val(false);
		   }
	});
	setUserIdentityTextFields();
	setAuthenticationAttributesTextFields();
	setAccountingAttributesTextFields();
	
	/* Advaned CUI Configuration */
	$(".cui-css").change(function(){
		   if($(this).val() == 'Advanced'){
			   $("#advancedCuiExpression").attr("readonly", false);
		   }else{
			   $("#advancedCuiExpression").attr("readonly", true);
		   }
	});
	
});

function setHotlinePolicy(){
	if($("#authResponseBehavior").val() == 'HOTLINE'){
		$("#hotlinePolicy").attr("disabled","");	
		$("#hotlinePolicy").removeClass("disableClass");	
	}else{
		$("#hotlinePolicy").attr("disabled","disabled");	
		$("#hotlinePolicy").addClass("disableClass");	
	}
}

var isValidName;

function validate()
{
	if(isNull(document.forms[0].name.value)){
		alert('Policy Name must be specified');
		document.forms[0].name.focus();
		return;
	}
 
	if(!isValidName) {
		alert('Enter Valid Policy Name');
		document.forms[0].name.focus();
		return;
	}
	
	if(!validateName(document.forms[0].name.value)){
		alert('Policy Name should have following characters. A-Z, a-z, 0-9, _ and - ');
		document.forms[0].name.focus();
		return;
	}
	
	if($('#ruleSetAuth').is(':disabled') == false && $('#ruleSetAuth').val().trim() == '') {
		alert('Authentication Ruleset must be specified');
		return;
	}
	
	if($('#ruleSetAcct').is(':disabled') == false && $('#ruleSetAcct').val().trim() == '') {
		alert('Accounting Ruleset must be specified');
		return;
	}
	
	if( $('#cui').val() == 'Advanced' && isNull($('#advancedCuiExpression').val())){
		alert('Advanced CUI Expression must be specified');
		$('#advancedCuiExpression').focus();
		return;
	}
	
	document.forms[0].submit();
}

function validateName(val){
	var test1 = /(^[A-Za-z0-9-_]*$)/;
	var regexp =new RegExp(test1);
	if(regexp.test(val)){
		return true;
	}
	return false;
}
function validateAuthMethod(){
	var requestType = document.getElementById("requestType");
	if(requestType.value==1 || requestType.value==3){
		var selectedAuthMethodCheBoxes = document.getElementsByName("selectedAuthMethodTypes");
		if(selectedAuthMethodCheBoxes.length>0){
				for (i=0; i<selectedAuthMethodCheBoxes.length; i++){
				 		 if (selectedAuthMethodCheBoxes[i].checked == true){  	
								return true;				 			 
				 		 }
				}
		}
		return false;	
	
	}
	return true;
	
}
function validateEAP(){
	var eapChkBox = document.getElementById("eapCheckBox");
	if(eapChkBox.checked==true){
		if(document.forms[0].eapConfigId.value==0){
			return false;	
		}
		
	}
	return true;
}
function validateHttpDigest(){
	var digestChkBox = document.getElementById("httpDigestCheckBox");
	if(digestChkBox.checked==true){
		if(document.forms[0].digestConfigId.value==0){
			return false;	
		}
	}
	return true;
}
function setUserIdentityTextFields(){
	var userIdentity = document.getElementById("userIdentity").value;
	retriveRadiusDictionaryAttributes(userIdentity,"userIdentity");
}

function setAuthenticationAttributesTextFields(){
	var authAttributes = document.getElementById("authAttributes").value;
	retriveRadiusDictionaryAttributes(authAttributes,"authAttributes");
}

function setAccountingAttributesTextFields(){
	var acctAttributes = document.getElementById("acctAttributes").value;
	retriveRadiusDictionaryAttributes(acctAttributes,"acctAttributes");
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.RADIUS_SERVICE_POLICY%>', searchName, 'create','', 'verifyNameDiv');
} 
</script>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH)%>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td class="box" cellpadding="0" cellspacing="0" border="0" width="100%">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td class="table-header" colspan="6">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.create" />
								</td>
							</tr>
							<tr>
								<td colspan="3">
									<html:form action="/addRadiusServicePolicyAuth">
										<table name="c_tblCrossProductList" width="100%" border="0">
											<tr>
												<td align="left" class="tblheader-bold" valign="top" width="27%">
													<bean:message bundle="servicePolicyProperties" key="basic.details" />
												</td>
											</tr>
											<tr>
												<td>
													<table border="0" width="100%">
														<tr>
															<td align="left" class="captiontext" valign="top" width="25%">
																<bean:message key="general.name" /> 
																<ec:elitehelp  header="Name" headerBundle="servicePolicyProperties" text="This parameter specifies the name of an Radius Service Policy." ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" nowrap="nowrap" width="25%">
																<html:text property="name" size="30" style="width: 200;" styleId="name" tabindex="1" onblur="verifyName();"/>
																<font color="#FF0000" colspan="3"> *</font>
																<div id="verifyNameDiv" class="labeltext"></div></td>
															<td align="left" class="labeltext" valign="top">
																<html:checkbox property="status" styleId="status" value="ACTIVE" tabindex="2"></html:checkbox>
																&nbsp;Active</td>
														</tr>

														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message key="general.description" />
																<ec:elitehelp header="Description" headerBundle="servicePolicyProperties" text="This parameter specifies the description of an Radius Service Policy." ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:textarea property="description" styleId="description" rows="2" cols="60" tabindex="3" />
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.radiusmessages" />
																<ec:elitehelp  header="servicepolicy.proxypolicy.radiusmessages" headerBundle="servicePolicyProperties" text="servicepolicy.proxypolicy.radiusmessages" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<table width="41%" cellspacing="0" cellpadding="0" border="0">
																	<tr>
																		<td align="left" class="labeltext" valign="top">
																			<label for="authentication">
																				<html:checkbox property="authentication" value="true" name="createRadiusServicePolicyForm" styleId="authentication" styleClass="radiusMessage" tabindex="4" ></html:checkbox>
																				<html:hidden property="authentication" value="false"/>
																				<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.authentication" />
																			</label>
																		</td>
																	</tr>
																	<tr>
																		<td align="left" class="labeltext" valign="top">
																			<label for="accounting">
																				<html:checkbox property="accounting" name="createRadiusServicePolicyForm" value="true" styleId="accounting" styleClass="radiusMessage" tabindex="5"></html:checkbox>
																				<html:hidden property="accounting" value="false"/>
																				<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.accounting" />
																			</label>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.authpolicy.ruleset" />
																<ec:elitehelp header="Authentication Ruleset" headerBundle="servicePolicyProperties" text="radiusservicepolicy.basic.ruleset" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:textarea property="ruleSetAuth" name="createRadiusServicePolicyForm" styleId="ruleSetAuth" rows="2" cols="60" value="0:1=\"*\"" tabindex="6" disabled="false"/>
																<font color="#FF0000"> *</font> 
																<img alt="Expression" src="<%=basePath%>/images/lookup.jpg" onclick="popupExpression('ruleSetAuth');"  class="authRuleset" tabindex="7" />
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.acctpolicy.ruleset" /> 
																<ec:elitehelp header="Accounting Ruleset" headerBundle="servicePolicyProperties" text="radiusservicepolicy.basic.acctruleset" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:textarea property="ruleSetAcct" name="createRadiusServicePolicyForm" styleId="ruleSetAcct" value="0:1=\"*\""  rows="2" cols="60" tabindex="8" disabled="false" />
																<font color="#FF0000"> *</font> 
																<img alt="Expression" src="<%=basePath%>/images/lookup.jpg" onclick="popupExpression('ruleSetAcct');" class="acctRuleset" tabindex="9" />
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top" width="16%">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.useridentity" /> 
																<ec:elitehelp  header="servicepolicy.authpolicy.useridentity" headerBundle="servicePolicyProperties" text="radiusservicepolicy.useridentity" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:text property="userIdentity" styleId="userIdentity" style="width: 200;" tabindex="16" value="0:1" onkeyup="setUserIdentityTextFields();"/>
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.validatepacket" /> 
																<ec:elitehelp  header="servicepolicy.authpolicy.validatepacket" headerBundle="servicePolicyProperties" text="radiusservicepolicy.validatepacket"></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top">
																<html:select property="validatePacket" styleClass="labeltext" style="width: 200;" styleId="validatePacket" tabindex="10">
																	<html:option value="true">True</html:option>
																	<html:option value="false">False</html:option>
																</html:select>
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.defaultauthresponsebehavior" /> 
																<ec:elitehelp  header="servicepolicy.proxypolicy.defaultauthresponsebehavior"  headerBundle="servicePolicyProperties" text="servicepolicy.proxypolicy.defaultauthresponsebehavior" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top">
																<html:select property="authResponseBehavior" styleId="authResponseBehavior" style="width: 200;"  tabindex="11" onchange="setHotlinePolicy();" disabled="false">
																	<html:option value="REJECT">Reject(Default)</html:option>
																	<html:option value="DROP">Drop</html:option>
																	<html:option value="HOTLINE">Hotline</html:option>
																</html:select>
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.hotlinepolicy" /> 
																<ec:elitehelp  header="servicepolicy.authpolicy.hotlinepolicy" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.hotlinepolicy" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top">
																<html:text property="hotlinePolicy" name="createRadiusServicePolicyForm" styleId="hotlinePolicy" style="width: 200;" maxlength="255" styleClass="disableClass" tabindex="12" disabled="true"/>
															</td> 
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.defaultacctresponsebehavior" /> 
																<ec:elitehelp  header="servicepolicy.proxypolicy.defaultacctresponsebehavior" headerBundle="servicePolicyProperties" text="servicepolicy.proxypolicy.defaultacctresponsebehavior" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top">
																<html:select property="acctResponseBehavior" styleId="acctResponseBehavior" style="width: 200;"  tabindex="13">
																	<html:option value="DROP">Drop(Default)</html:option>
																	<html:option value="RESPONSE">Response</html:option>
																</html:select>
															</td>
														</tr>
														
														<!-- Auth response attribute -->
														
														<tr>
															<td align="left" class="captiontext" valign="top" width="16%">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.resattrs" /> 
																<ec:elitehelp  header="servicepolicy.authpolicy.resattrs" example="servicepolicy.authpolicy.resattrs" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.resattrs" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:textarea property="authResponseAttributes" styleId="responseAttributes" rows="2" cols="60" tabindex="15" />
															</td>
														</tr>
														
														<!-- Acct response attribute -->
														<tr>
															<td align="left" class="captiontext" valign="top" width="16%">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.acctpolicy.resattrs" /> 
																<ec:elitehelp  header="servicepolicy.acctpolicy.resattrs" example="servicepolicy.acctpolicy.resattrs" headerBundle="servicePolicyProperties" text="servicepolicy.acctpolicy.resattrs" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:textarea property="acctResponseAttributes" styleId="responseAttributes" rows="2" cols="60" tabindex="15" />
															</td>
														</tr>
														
														<tr>
															<td align="left" class="tblheader-bold" valign="top" width="27%" colspan="7"> 
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.sessionmanagementheader" />
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top" width="16%" style="padding-top: 5px;padding-bottom: 5px;">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.sessionmanager" />
																<ec:elitehelp  header="servicepolicy.proxypolicy.sessionmanager" headerBundle="servicePolicyProperties" text="servicepolicy.proxypolicy.sessionmanager" note="radiusservicepolicy.basic.sessionmanager.note" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3" style="padding-top: 5px;padding-bottom: 5px;">
																<html:select property="sessionManagerId" styleId="sessionManagerId" style="width:200px;" tabindex="14">
																	<html:option value="0">--Select--</html:option>
																	<html:optionsCollection property="sessionManagerInstanceDataList" name="createRadiusServicePolicyForm" value="name" label="name"/>
																</html:select>
															</td>
														</tr>
														<tr>
															<td align="left" class="tblheader-bold" valign="top" width="27%" colspan="7"> 
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.cuiheader" />
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top" colspan="3">
																	<div class="fieldset"><h1><span>Authentication</span></h1>
																	  	<table width="100%" cellspacing="0" cellpadding="0" border="0">
																	  		<tr>
																				<td align="left" class="captiontext" valign="top" width="42%">
																					<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.cui" /> 
																					<ec:elitehelp   header="radiusservicepolicy.cui" headerBundle="servicePolicyProperties" text="radiusservicepolicy.cui" ></ec:elitehelp>
																				</td>
																				<td align="left" class="labeltext" valign="top" width="58%">
																					<html:select styleClass="labeltext cui-css" style="width: 200px;" styleId="cui" property="cui" tabindex="19">
																						<html:option value="NONE">NONE</html:option>
																						<html:option value="Authenticated-Identity">Authenticated-Identity</html:option>
																						<html:option value="Group">Group</html:option>
																						<html:option value="Profile-CUI">Profile-CUI</html:option>
																						<html:option value="Advanced">Advanced</html:option>
																					</html:select>
																				</td>
																			</tr>
																			<tr>
																				<td align="left" class="captiontext" valign="top" width="42%">
																					<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.advancedcuiexpression" /> 
																					<ec:elitehelp  header="radiusservicepolicy.advancedcuiexpression" headerBundle="servicePolicyProperties" text="radiusservicepolicy.advancedcuiexpression" ></ec:elitehelp>
																				</td>
																				<td align="left" class="labeltext" valign="top" width="58%">
																					<html:text property="advancedCuiExpression" styleId="advancedCuiExpression" styleClass="advancedCuiExpression" readonly="true" style="width:200px;"></html:text>
																				</td>
																			</tr>
																			<tr>
																				<td align="left" class="captiontext" valign="top" width="42%">
																					<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.authttributes" /> 
																					<ec:elitehelp  header="servicepolicy.authpolicy.cuiresattrs" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.cuiresattrs" ></ec:elitehelp>
																				</td>
																				<td align="left" class="labeltext" valign="top" width="58%">
																					<html:text property="authAttributes" styleId="authAttributes" style="width: 200;" maxlength="255" styleClass="" tabindex="17" disabled="false" onkeyup="setAuthenticationAttributesTextFields();"/>
																				</td>
																			</tr>
																		</table>
																	 </div>
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top" colspan="3">
																	<div class="fieldset"><h1><span>Accounting</span></h1>
																	  	<table width="100%" cellspacing="0" cellpadding="0" border="0">
																	  		<tr>
																				<td align="left" class="captiontext" valign="top" width="42%">
																					<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.acctttributes" /> 
																					<ec:elitehelp   header="radiusservicepolicy.acctttributes" headerBundle="servicePolicyProperties" text="radiusservicepolicy.cui" ></ec:elitehelp>
																				</td>
																				<td align="left" class="labeltext" valign="top" width="58%" >
																					<html:text property="acctAttributes" styleId="acctAttributes" style="width: 200;" maxlength="255" styleClass="" tabindex="18" onkeyup="setAccountingAttributesTextFields();"/>
																				</td>
																			</tr>
																		</table>
																	  </div>
															</td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle">&nbsp;</td>
															<td class="btns-td" valign="middle" colspan="3"><input
																type="button" value="Next " class="light-btn"
																onclick="validate()" tabindex="20" /> <input
																type="reset" name="c_btnDeletePolicy"
																onclick="javascript:location.href='<%=basePath%>/searchRadiusServicePolicy.do?/>'"
																value="Cancel" class="light-btn" tabindex="21"></td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</html:form>
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
<div id="popupExpr" style="display: none;" title="ExpressionBuilder">
	<div id="expBuilderId" align="center"></div>
</div>
<script>
setTitle('<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy"/>');
</script>