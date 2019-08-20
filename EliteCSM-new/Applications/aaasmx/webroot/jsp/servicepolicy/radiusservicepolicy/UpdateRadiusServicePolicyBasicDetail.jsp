<%@page import="com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form.UpdateRadiusServicePolicyForm"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="java.util.List"%>

<%
	RadServicePolicyData radiusServicePolicyData  = (RadServicePolicyData) request.getAttribute("radServicePolicyData");
	UpdateRadiusServicePolicyForm updateRadiusServicePolicyForm =(UpdateRadiusServicePolicyForm)request.getAttribute("updateRadiusServicePolicyForm");
%>

<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script>
$(document).ready(function(){
	setExpressionData("Radius");
	setHotlinePolicy();
	setRulesetData();
	setAdvancedCUIConfiguration();
	
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
	
	/* Advaned CUI Configuration */
	$(".cui-css").change(function(){
		   if($(this).val() == 'Advanced'){
			   $("#advancedCuiExpression").attr("readonly", false);
		   }else{
			   $("#advancedCuiExpression").attr("readonly", true);
		   }
	});
	
	setUserIdentityTextFields();
	setAuthenticationAttributesTextFields();
	setAccountingAttributesTextFields();
});

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
	
	document.forms[0].action.value="update";
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

 function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.RADIUS_SERVICE_POLICY%>',searchName,'update','<%=radiusServicePolicyData.getRadiusPolicyId()%>','verifyNameDiv');
}
 
function setHotlinePolicy(){
	if($("#authResponseBehavior").val() == 'HOTLINE'){
		$("#hotlinePolicy").attr("disabled","");	
		$("#hotlinePolicy").removeClass("disableClass");	
	}else{
		$("#hotlinePolicy").attr("disabled","disabled");	
		$("#hotlinePolicy").addClass("disableClass");	
	}
}
function setRulesetData(){
	 if($('#authentication').is(":checked")){
	      $("#ruleSetAuth").removeAttr("disabled");
	  	  $('#authentication').val(true);
	   }else{
	      $("#ruleSetAuth").attr("disabled" , "disabled");
	      $('#authentication').val(false);
	   }
	 
	 if($('#accounting').is(":checked")){
	      $("#ruleSetAcct").removeAttr("disabled");
	   	  $('#accounting').val(true);
	   }else{
	       $("#ruleSetAcct").attr("disabled" , "disabled");
	       $('#accounting').val(false);
	   }
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
function setAdvancedCUIConfiguration(){
	var cuiOption = '<%=radiusServicePolicyData.getCui()%>';
	if(cuiOption == 'Advanced'){
		$("#advancedCuiExpression").attr("readonly", false);
	}
}
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<html:form action="/updateRadiusServicePolicyBasicDetail">
		<html:hidden name="updateRadiusServicePolicyForm" styleId="radiusPolicyId" property="radiusPolicyId" value="<%=radiusServicePolicyData.getRadiusPolicyId()%>" />
		<html:hidden name="updateRadiusServicePolicyForm" styleId="action" property="action" />
		<html:hidden name="updateRadiusServicePolicyForm" styleId="auditUid" property="auditUid"/>
		<html:hidden name="updateRadiusServicePolicyForm" styleId="radiusPolicyXml" property="radiusPolicyXml"/>
		<bean:define id="radServicePolicyData" name="radServicePolicyData" scope="request" type="com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData"  />
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" width="30%" colspan="6">
							<bean:message bundle="servicePolicyProperties" key="basic.details" />
						</td>
					</tr>
					<tr>
						<td class="small-gap" colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message key="general.name" /> 
							<ec:elitehelp  header="Name" headerBundle="servicePolicyProperties" text="This parameter specifies the name of an Radius Service Policy." ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" width="40%" nowrap="nowrap" colspan="3">
							<html:text property="name" size="30" style="width: 200;" styleId="name" tabindex="1" onblur="verifyName();"/>
							<font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div></td>
						<td align="left" class="labeltext" valign="top">
							<html:checkbox styleId="status" property="status" value="ACTIVE" tabindex="2" />Active
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message key="general.description" />
							<ec:elitehelp header="Description" headerBundle="servicePolicyProperties" text="This parameter specifies the description of an Radius Service Policy." ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2"  width="40%">
							<html:textarea styleId="description" property="description" cols="50" rows="4" styleClass="input-textarea labeltext" tabindex="3" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.radiusmessages" />
							<ec:elitehelp  header="servicepolicy.proxypolicy.radiusmessages" headerBundle="servicePolicyProperties" text="servicepolicy.proxypolicy.radiusmessages" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3" width="40%">
							<table width="100%" cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td align="left" class="labeltext" valign="top">
										<label for="authentication">
											<html:checkbox property="authentication" styleId="authentication" styleClass="radiusMessage" tabindex="4" ></html:checkbox>
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.authentication" />
										</label>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top">
										<label for="accounting">
											<html:checkbox property="accounting" styleId="accounting" styleClass="radiusMessage" tabindex="5"></html:checkbox>
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.accounting" />
										</label>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.authpolicy.ruleset" />
							<ec:elitehelp header="Authentication Ruleset" headerBundle="servicePolicyProperties" text="radiusservicepolicy.basic.ruleset" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2" width="40%">
							<html:textarea property="ruleSetAuth" styleId="ruleSetAuth" rows="2" cols="60" tabindex="6" disabled="false"/>
							<font color="#FF0000"> *</font> 
							<img alt="Expression" src="<%=basePath%>/images/lookup.jpg" onclick="popupExpression('ruleSetAuth');" class="authRuleset" tabindex="7" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.acctpolicy.ruleset" /> 
							<ec:elitehelp header="Accounting Ruleset" headerBundle="servicePolicyProperties" text="radiusservicepolicy.basic.acctruleset" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2" width="40%">
							<html:textarea property="ruleSetAcct" styleId="ruleSetAcct" rows="2" cols="60" tabindex="8" disabled="false" />
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
							<html:text property="userIdentity" styleId="userIdentity" style="width: 200;" tabindex="16"  onkeyup="setUserIdentityTextFields();"/>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.validatepacket" /> 
							<ec:elitehelp  header="servicepolicy.authpolicy.validatepacket" headerBundle="servicePolicyProperties" text="radiusservicepolicy.validatepacket" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2" width="40%">
							<html:select property="validatePacket" styleClass="labeltext" style="width: 200;" styleId="validatePacket" tabindex="10">
								<html:option value="true">True</html:option>
								<html:option value="false">False</html:option>
							</html:select>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.defaultauthresponsebehavior" /> 
							<ec:elitehelp  header="servicepolicy.proxypolicy.defaultauthresponsebehavior"  headerBundle="servicePolicyProperties" text="servicepolicy.proxypolicy.defaultauthresponsebehavior" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2" width="40%">
							<html:select property="authResponseBehavior" styleId="authResponseBehavior" style="width: 200;"  tabindex="11" onchange="setHotlinePolicy();">
								<html:option value="REJECT">Reject(Default)</html:option>
								<html:option value="DROP">Drop</html:option>
								<html:option value="HOTLINE">Hotline</html:option>
							</html:select>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.hotlinepolicy" /> 
							<ec:elitehelp  header="servicepolicy.authpolicy.hotlinepolicy" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.hotlinepolicy" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2" width="40%">
							<html:text property="hotlinePolicy" styleId="hotlinePolicy" style="width: 200;" maxlength="255" styleClass="disableClass" tabindex="12" disabled="true"/>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.defaultacctresponsebehavior" /> 
							<ec:elitehelp  header="servicepolicy.proxypolicy.defaultacctresponsebehavior" headerBundle="servicePolicyProperties" text="servicepolicy.proxypolicy.defaultacctresponsebehavior" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2" width="40%">
							<html:select property="acctResponseBehavior" styleId="acctResponseBehavior" style="width: 200;"  tabindex="13">
								<html:option value="DROP">Drop(Default)</html:option>
								<html:option value="RESPONSE">Response</html:option>
							</html:select>	
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.resattrs" /> 
							<ec:elitehelp  header="servicepolicy.authpolicy.resattrs" example="servicepolicy.authpolicy.resattrs" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.resattrs" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2" width="40%">
							<html:textarea property="authResponseAttributes" styleId="responseAttributes" rows="2" cols="60" tabindex="15" />
						</td>
					</tr>
					
					<!-- Acct response attribute -->
					<tr>
						<td align="left" class="captiontext" valign="top" width="16%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.acctpolicy.resattrs" /> 
							<ec:elitehelp header="servicepolicy.acctpolicy.resattrs" example="servicepolicy.acctpolicy.resattrs" headerBundle="servicePolicyProperties" text="servicepolicy.acctpolicy.resattrs"></ec:elitehelp>
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
						<td align="left" class="captiontext" valign="top" width="30%" style="padding-top: 5px;padding-bottom: 5px;">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.sessionmanager" />
							<ec:elitehelp  header="servicepolicy.proxypolicy.sessionmanager" headerBundle="servicePolicyProperties" text="servicepolicy.proxypolicy.sessionmanager" note="radiusservicepolicy.basic.sessionmanager.note" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2" width="40%" style="padding-top: 5px;padding-bottom: 5px;">
							<html:select property="sessionManagerId" styleId="sessionManagerId" style="width:200px;"  tabindex="14">
								<html:option value="0">--Select--</html:option>
								<html:optionsCollection property="sessionManagerInstanceDataList" name="updateRadiusServicePolicyForm" value="name" label="name"/>
							</html:select>
						</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" width="27%" colspan="6">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.cuiheader" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" colspan="6" width="50%">
							<div class="fieldset" style="width: 64%;"><h1><span>Authentication</span></h1>
							  	<table width="100%" cellspacing="0" cellpadding="0" border="0">
							  		<tr>
										<td align="left" class="captiontext" valign="top" width="43%">
											<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.cui" /> 
											<ec:elitehelp   header="radiusservicepolicy.cui" headerBundle="servicePolicyProperties" text="radiusservicepolicy.cui" ></ec:elitehelp>
										</td>
										<td align="left" class="labeltext" valign="top" width="59%">
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
										<td align="left" class="captiontext" valign="top" width="43%">
											<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.advancedcuiexpression" /> 
											<ec:elitehelp  header="radiusservicepolicy.advancedcuiexpression" headerBundle="servicePolicyProperties" text="radiusservicepolicy.advancedcuiexpression" ></ec:elitehelp>
																		
										</td>
										<td align="left" class="labeltext" valign="top" width="59%">
											<html:text property="advancedCuiExpression" styleId="advancedCuiExpression" styleClass="advancedCuiExpression" readonly="true" style="width:200px;"></html:text>
										</td>
									</tr>
									<tr>
										<td align="left" class="captiontext" valign="top" width="43%">
											<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.authttributes" /> 
											<ec:elitehelp  header="servicepolicy.authpolicy.cuiresattrs" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.cuiresattrs" ></ec:elitehelp>
										</td>
										<td align="left" class="labeltext" valign="top" width="59%">
											<html:text property="authAttributes" styleId="authAttributes" style="width: 200;" maxlength="255" styleClass="" tabindex="17"  onkeyup="setAuthenticationAttributesTextFields();"/>
										</td>
									</tr>
								</table>
							</div>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" colspan="6">
							<div class="fieldset" style="width: 64%;"><h1><span>Accounting</span></h1>
							  	<table width="100%" cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td align="left" class="captiontext" valign="top" width="43%">
											<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.acctttributes" /> 
											<ec:elitehelp   header="radiusservicepolicy.acctttributes" headerBundle="servicePolicyProperties" text="radiusservicepolicy.cui" ></ec:elitehelp>
										</td>
										<td align="left" class="labeltext" valign="top" width="59%">
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
							type="button" value="Update " class="light-btn"
							onclick="validate()" tabindex="20" /> <input type="reset"
							name="c_btnDeletePolicy"
							onclick="javascript:location.href='<%=basePath%>/viewRadiusServicePolicy.do?radiusPolicyId=<%=radiusServicePolicyData.getRadiusPolicyId()%>'"
							value="Cancel" class="light-btn" tabindex="21"></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="small-gap" colspan="2">&nbsp;</td>
		</tr>
</table>
<div id="popupExpr" style="display: none;" title="ExpressionBuilder">
	<div id="expBuilderId" align="center"></div>
</div>
</html:form>