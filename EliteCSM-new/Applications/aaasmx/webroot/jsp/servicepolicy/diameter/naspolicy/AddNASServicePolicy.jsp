<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.AddNASServicePolicyForm"%>
<%@ page import="com.elitecore.diameterapi.diameter.common.util.constant.CommandCode" %>

<%
	String basePath = request.getContextPath();		
	AddNASServicePolicyForm addNASServicePolicyForm = (AddNASServicePolicyForm)request.getAttribute("addNASServicePolicyForm");
	String statusVal=(String)request.getParameter("status");
%>

<style>
.main-css {
	height: 8px;
}
</style>

<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script>
	setExpressionData("Diameter");
 	var isValidName;
 
	var commandCodeList = [];
	
	<%for(CommandCode commandCode:CommandCode.VALUES){%>
		commandCodeList.push({'value':'<%=commandCode.code%>','label':'[<%=commandCode.code%>] <%=commandCode.name()%>','id':'<%=commandCode.code%>'});
	<%}%>
 
$(document).ready(function() {
	var chkBoxVal='<%=statusVal%>';
	if(chkBoxVal=='Inactive'){
		document.getElementById("status").checked=false;
	}else{
		document.getElementById("status").checked=true;
	}
	
	$('.responseAttributeTable td img.delete').live('click',function() {
		 $(this).parent().parent().remove(); 
	});
	setCommandCodeAutocompleteData();
	
	/* Advaned CUI Configuration */
	$(".cui-css").change(function(){
		   if($(this).val() == 'Advanced'){
			   $("#advancedCuiExpression").attr("readonly", false);
		   }else{
			   $("#advancedCuiExpression").attr("readonly", true);
		   }
	});
	
	$(".authorizationParam").each(function(){
		if($(this).val() == 'false'){
			$(this).attr('checked', false);
		}else{
			$(this).attr('checked', true);
		}
	});
	
	$(".authorizationParam").change(function(){
		if ($(this).attr('checked')) {
			$(this).val('true');
		} else {
			$(this).val('false');
		}
	});
	
});

	function splitData(val) {
		return val.split(/[,;]\s*/);
	}

	function extractLastItems(term) {
		return splitData(term).pop();
	}

	function setCommandCodeAutocompleteData() {
		$('.commandCode').bind(
				"keydown",
				function(event) {
					if (event.keyCode === $.ui.keyCode.TAB
							&& $(this).autocomplete("instance").menu.active) {
						event.preventDefault();
					}
				}).autocomplete(
				{
					minLength : 0,
					source : function(request, response) {
						response($.ui.autocomplete.filter(commandCodeList,
								extractLastItems(request.term)));
					},
					focus : function() {
						return false;
					},
					select : function(event, ui) {
						var val = this.value;
						var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val
								.lastIndexOf(",");
						var semiColonIndex = val.lastIndexOf(";") == -1 ? 0
								: val.lastIndexOf(";");
						if (commaIndex == semiColonIndex) {
							val = "";
						} else if (commaIndex > semiColonIndex) {
							val = val.substring(0, commaIndex + 1);
						} else if (semiColonIndex != 0
								&& semiColonIndex > commaIndex) {
							val = val.substring(0, semiColonIndex + 1);
						}
						this.value = val + ui.item.value;
						return false;
					}
				});
	}
	function setCommandCodeData(commandCodeObj) {
		var commandCodevalue = $(commandCodeObj).val();
		commandCodevalue = commandCodevalue.trim();
		var lastChar = commandCodevalue.charAt(commandCodevalue.length - 1);
		if (lastChar == ",") {
			var result = commandCodevalue.substring(0,
					commandCodevalue.length - 1);
			$(commandCodeObj).val(result);
		}
	}
	function validate() {

		if (isNull(document.forms[0].name.value)) {
			alert('Policy Name must be specified');
			document.forms[0].name.focus();
			return;
		}

		if (!isValidName) {
			alert('Enter Valid Policy Name');
			document.forms[0].name.focus();
			return;
		}

		if (isNull(document.forms[0].ruleSet.value)) {
			alert('Policy Selection Rule must be specified');
			document.forms[0].ruleSet.focus();
			return;
		}
		var behaviour = $("#defaultResponseBehaviour").val();
		if (behaviour == "REJECT" || behaviour == "HOTLINE") {
			if ($("#defaultResponseBehaviourArgument").val().trim() == "") {
				alert("Default Response Behaviour Argument must be specified when Default Response Behaviour is "
						+ behaviour);
				$("#defaultResponseBehaviourArgument").focus();
				return false;
			}
		}
		if (isNull(document.forms[0].multipleUserIdentity.value)) {
			alert('User Identity Attribute must be specified');
			document.forms[0].multipleUserIdentity.focus();
			return;
		}

		if ($('#diameterConcurrency').val() == '0'
				&& $('#additionalDiameterConcurrency').val() != '0') {
			alert('Diameter Concurrency must be specified');
			$('#diameterConcurrency').focus();
			return;
		}

		if (!(isNull($('#defaultSessionTimeout').val()))
				&& isNaN($('#defaultSessionTimeout').val())) {
			alert('Default Session Timeout must be numeric ');
			$('#defaultSessionTimeout').focus();
			return;
		}

		if (!checkForDot()) {
			$('#defaultSessionTimeout').focus();
			return false;
		}

		if ($('#cui').val() == 'Advanced'
				&& isNull($('#advancedCuiExpression').val())) {
			alert('Advanced CUI Expression must be specified');
			$('#advancedCuiExpression').focus();
			return;
		}

		if (!isValidMappings()) {
			return;
		}
		document.forms[0].submit();
	}

	function isValidMappings() {
		var isValidMapping = true;
		$('.responseAttributeTable').find('.commandCode').each(function() {
			var nameValue = $.trim($(this).val());
			if (nameValue.length == 0) {
				alert("Command Code must be Specified");
				isValidMapping = false;
				$(this).focus();
				return false;
			}
		});
		return isValidMapping;
	}

	function validateName(val) {

		var test1 = /(^[A-Za-z0-9-_]*$)/;
		var regexp = new RegExp(test1);
		if (regexp.test(val)) {
			return true;
		}
		return false;
	}

	function setStripUserIdentity() {
		var stripUserCheckBox = document.getElementById("stripUserCheckBox");
		var realmPatternComp = document.getElementById("realmPattern");
		var separatorComp = document.getElementById("realmSeparator");
		if (stripUserCheckBox.checked == true) {
			realmPatternComp.disabled = false;
			separatorComp.disabled = false;
		} else {
			realmPatternComp.disabled = true;
			separatorComp.disabled = true;
		}
	}

	function setInit() {
		var stripUserCheckBox = document.getElementById("stripUserCheckBox");
		var trimUserIdentityCheckBox = document
				.getElementById("trimUserIdentityCheckBox");
		var trimPasswordCheckBox = document
				.getElementById("trimPasswordCheckBox");

		var papCheckBox = document.getElementById("pap");
		var chapCheckBox = document.getElementById("chap");

		papCheckBox.checked = true;
		chapCheckBox.checked = true;

		stripUserCheckBox.checked = false;
		trimUserIdentityCheckBox.checked = true;
		trimPasswordCheckBox.checked = false;
		setStripUserIdentity();
	}

	function verifyName() {
		var searchName = document.getElementById("name").value;
		isValidName = verifyInstanceName(
				'<%=InstanceTypeConstants.NAS_POLICY%>',searchName,'create','','verifyNameDiv');
	}
function addResponseAttributesTable(tableId,templateId){
	var tableRowStr = $("#"+templateId).find("tr");
	$("#"+tableId+" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
	$("#"+tableId+" tr:last").find("input:first").focus();
	setCommandCodeAutocompleteData();
}

function checkForDot(){
	if( !(isNull( $('#defaultSessionTimeout').val() ))) {
		var defaultValue = $('#defaultSessionTimeout').val();
		if (defaultValue.indexOf(".") !== -1) {
			alert('Floating point value not allowed in Default Session Timeout ');
			return false;
		}
		return true;
	}else{
		return true;
	}
}

function setColumnsOnCuiRespAttrTextFields(){
	var cuiRespAttrVal = document.getElementById("cuiResponseAttributes").value;
	retriveRadiusDictionaryAttributes(cuiRespAttrVal,"cuiResponseAttributes");
}
</script>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<table cellpadding="0" cellspacing="0" border="0"
	width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td class="box" cellpadding="0" cellspacing="0" border="0"
						width="100%">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">

							<tr>
								<td class="table-header" colspan="3"><bean:message
										bundle="servicePolicyProperties"
										key="servicepolicy.naspolicy.create" /></td>
							</tr>
							<tr>
								<td colspan="3"><html:form action="/addNASServicePolicy">
										<table name="c_tblCrossProductList" width="100%" border="0"
											cellpadding="0" cellspacing="0">
											<tr>
												<td align="left" class="tblheader-bold" valign="top"
													width="27%"><bean:message
														bundle="servicePolicyProperties" key="basic.details" /></td>
											</tr>
											<tr>
												<td>
													<table border="0" width="100%" cellpadding="0"
														cellspacing="0">
														<tr>
															<td align="left" class="captiontext" valign="top" width="25%">
																<bean:message key="general.name" /> 
																<ec:elitehelp  header="naspolicy.name" headerBundle="servicePolicyProperties" text="naspolicy.name" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top"
																nowrap="nowrap" colspan="3">
																<table border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td style="width: 270px"><html:text
																				property="name" size="40" onkeyup="verifyName();"
																				styleId="name" style="width:250px"
																				name="addNASServicePolicyForm" tabindex="1" /><font
																			color="#FF0000"> *</font>&nbsp;
																			<div id="verifyNameDiv" class="labeltext"></div></td>
																		<td align="left" class="labeltext" valign="top">
																			<input type="checkbox" name="status" id="status"
																			value="1" checked="checked" tabindex="2" />&nbsp;Active
																		</td>
																</table>
															</td>
														</tr>

														<tr>
															<td align="left" class="captiontext" valign="top"><bean:message
																	key="general.description" /></td>
															<td align="left" class="labeltext" valign="top"
																colspan="3"><html:textarea property="description"
																	name="addNASServicePolicyForm" styleId="description"
																	rows="2" cols="60" tabindex="3" /></td>
														</tr>

														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.ruleset" /> 
																<ec:elitehelp  header="servicepolicy.naspolicy.ruleset" headerBundle="servicePolicyProperties" text="naspolicy.ruleset" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top"
																colspan="3"><html:textarea property="ruleSet"
																	styleId="ruleSet" rows="2" cols="60" tabindex="4" /><font
																color="#FF0000"> *</font> <img alt="Expression"
																src="<%=basePath%>/images/lookup.jpg"
																onclick="popupExpression('ruleSet');" tabindex="5" /></td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.sessionmanagement" /> 
																<ec:elitehelp  header="servicepolicy.naspolicy.sessionmanagement" headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.sessionmanagement" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:select property="sessionManagement" styleId="sessionManagement">
																	<html:option value="true">True</html:option>
																	<html:option value="false">False</html:option>
																</html:select>
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.requestmode" /> 
																<ec:elitehelp  header="servicepolicy.authpolicy.requestmode" headerBundle="servicePolicyProperties" text="authpolicy.reqmod" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top"><html:select
																	property="requestType" styleClass="labeltext"
																	styleId="requestType" style="width: 200;" tabindex="6">
																	<html:option value="1">Authenticate-Only</html:option>
																	<html:option value="2">Authorize-Only</html:option>
																	<html:option value="3">Authenticate and Authorize</html:option>
																</html:select></td>
														</tr>
														<tr>	
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.defaultresponsebehaviour" /> 
																<ec:elitehelp  header="servicepolicy.naspolicy.defaultresponsebehaviour"  headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.defaultauthresponsebehavior" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top">
																<html:select name="addNASServicePolicyForm" styleId="defaultResponseBehaviour" property="defaultResponseBehaviour" style="width:90px" tabindex="9" >
																	<logic:iterate id="behaviour"  collection="<%=DefaultResponseBehavior.DefaultResponseBehaviorType.values() %>">
																		<html:option value="<%=((DefaultResponseBehavior.DefaultResponseBehaviorType)behaviour).name()%>"><%=((DefaultResponseBehavior.DefaultResponseBehaviorType)behaviour).name()%></html:option>
																	</logic:iterate>
																</html:select>
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.defaultresponsebehaviourargument" /> 
																<ec:elitehelp  header="servicepolicy.naspolicy.defaultresponsebehaviourargument" headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.defaultresponsebehaviourargument" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top">
																<html:text property="defaultResponseBehaviourArgument" name="addNASServicePolicyForm" styleId="defaultResponseBehaviourArgument"  maxlength="1000" styleClass="textbox_width" tabindex="12" />
															</td> 
														</tr>
														

													</table>
												</td>
											</tr>
											<tr>
												<td align="left" class="tblheader-bold" valign="top"
													width="27%"><bean:message
														bundle="servicePolicyProperties"
														key="servicepolicy.naspolicy.authenticationdetails" /></td>
											</tr>
											<tr>
												<td>
													<table border="0" width="100%" cellpadding="0"
														cellspacing="0">
														<tr>
															<td class="captiontext" width="25%">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.authmethods" /> 
																<ec:elitehelp  header="servicepolicy.naspolicy.authmethods" headerBundle="servicePolicyProperties" text="naspolicy.methods" ></ec:elitehelp>
															</td>
															<td class="labeltext"><html:checkbox property="pap"
																	styleId="pap" tabindex="7">
																	<bean:message bundle="servicePolicyProperties"
																		key="servicepolicy.naspolicy.pap" />
																</html:checkbox> <html:checkbox property="chap" styleId="chap"
																	tabindex="8">
																	<bean:message bundle="servicePolicyProperties"
																		key="servicepolicy.naspolicy.chap" />
																</html:checkbox></td>
														</tr>


														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.multipleuid" />
																<ec:elitehelp  header="servicepolicy.naspolicy.multipleuid" headerBundle="servicePolicyProperties" text="naspolicy.idattribute" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top"
																colspan="3">
																<!--  	<html:text property="multipleUserIdentity" styleId="multipleUserIdentity" size="30"/>    -->
																<input type="text" name="multipleUserIdentity"
																id="multipleUserIdentity" size="30" autocomplete="off"
																onkeyup="retriveDiameterDictionaryAttributes(this.value,'multipleUserIdentity');"
																style="width: 250px" tabindex="9" /> <font
																color="#FF0000"> *</font>
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.casesensitiveuid" />
																<ec:elitehelp  header="servicepolicy.naspolicy.casesensitiveuid" headerBundle="servicePolicyProperties" text="naspolicy.casesensitiveuid" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top"
																colspan="3"><html:select
																	property="caseSensitiveUserIdentity"
																	styleClass="labeltext"
																	styleId="caseSensitiveUserIdentity" style="width: 175;"
																	tabindex="10">
																	<html:option value="1">No Change</html:option>
																	<html:option value="2">Lower Case</html:option>
																	<html:option value="3">Upper Case</html:option>
																</html:select></td>
														</tr>


														<tr>
															<td align="left" class="captiontext" valign="top"><bean:message
																	bundle="servicePolicyProperties"
																	key="update.user.identity" /> 
																	<ec:elitehelp  header="update.user.identity" headerBundle="servicePolicyProperties" text="naspolicy.updateuserid" ></ec:elitehelp>
															</td>
															<td class="labeltext" colspan="3" valign="top"
																style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">
																<table width="100%" border="0">
																	<tr>
																		<td class="labeltext"><html:checkbox
																				property="stripUserIdentity"
																				styleId="stripUserCheckBox" value="true"
																				onclick="setStripUserIdentity()" tabindex="11">
																			</html:checkbox> <bean:message bundle="servicePolicyProperties"
																				key="servicepolicy.naspolicy.stripuid" />

																			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																			<bean:message bundle="servicePolicyProperties"
																				key="servicepolicy.naspolicy.realmseparator" /> <html:text
																				property="realmSeparator" size="5" maxlength="1"
																				styleId="realmSeparator" tabindex="12" />


																			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																			<bean:message bundle="servicePolicyProperties"
																				key="servicepolicy.naspolicy.realmpattern" /> <html:select
																				property="realmPattern" styleClass="labeltext"
																				styleId="realmPattern" style="width: 175;"
																				tabindex="13">
																				<html:option value="prefix">Prefix</html:option>
																				<html:option value="suffix">Suffix</html:option>
																			</html:select></td>
																	</tr>
																	<tr>
																		<td class="labeltext"><html:checkbox
																				property="trimUserIdentity" value="true"
																				styleId="trimUserIdentityCheckBox" tabindex="14"></html:checkbox>
																			<bean:message bundle="servicePolicyProperties"
																				key="servicepolicy.naspolicy.trimuid" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																			<html:checkbox property="trimPassword" value="true"
																				styleId="trimPasswordCheckBox" tabindex="15"></html:checkbox>
																			<bean:message bundle="servicePolicyProperties"
																				key="servicepolicy.naspolicy.trimpassword" /></td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.username" /> 
																<ec:elitehelp  header="servicepolicy.naspolicy.username" headerBundle="servicePolicyProperties" text="naspolicy.username" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top"><html:select
																	property="userNameAttribute" styleClass="labeltext"
																	styleId="userName" style="width: 175;" tabindex="18">
																	<html:option value="NONE">NONE</html:option>
																	<html:option value="Authenticated-Username">Authenticated-Username</html:option>
<%-- 																	<html:option value="CUI">CUI</html:option> --%>
																	<html:option value="Request">Request</html:option>
																</html:select></td>

															<td align="left" class="labeltext" valign="top">
																 <bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.usernameresattrs" /> 
																 <ec:elitehelp  header="servicepolicy.naspolicy.usernameresattrs" headerBundle="servicePolicyProperties" text="naspolicy.usernameresattrs" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top"><input
																type="text" name="userNameResonseAttributes"
																id="userNameResonseAttributes" size="30"
																autocomplete="off"
																onkeyup="	retriveDiameterDictionaryAttributes(this.value,'userNameResonseAttributes');"
																style="width: 250px" tabindex="19" /></td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.anonymousidentity" /> 
																<ec:elitehelp  header="servicepolicy.naspolicy.anonymousidentity" headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.anonymousidentity" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:text property="anonymousProfileIdentity" styleId="anonymousProfileIdentity" style="width:175px;"></html:text>
															</td>
														</tr>
														
													</table>
												</td>
											</tr>


											<tr>
												<td align="left" class="tblheader-bold" valign="top"
													width="27%"><bean:message
														bundle="servicePolicyProperties"
														key="servicepolicy.authpolicy.authorizationdetails" /></td>
											</tr>

											<tr>
												<td>
													<table border="0" width="100%">
														<tr>
															<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.wimax" /> 
																<ec:elitehelp  header="servicepolicy.naspolicy.wimax" headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.wimax" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:select property="wimax" styleClass="labeltext" styleId="wimax" tabindex="18">
																	<html:option value="true">Enabled</html:option>
																	<html:option value="false">Disabled</html:option>
																</html:select>
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top"
																width="25%" nowrap="nowrap">Diameter Policy</td>
															<td align="left" class="box" valign="top" colspan="3">
																<table width="100%">
																	<tr>
																		<td class="labeltext" width="40%"><html:checkbox
																				property="rejectOnCheckItemNotFound" value="false" 
																				styleClass="authorizationParam"
																				styleId="rejectOnCheckItemNotFound" tabindex="21">
																				<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.rejectoncheckitemnotfound" />
																				<ec:elitehelp  header="servicepolicy.authpolicy.rejectoncheckitemnotfound" headerBundle="servicePolicyProperties" text="authpolicy.rejectoncheck" ></ec:elitehelp>
																			</html:checkbox></td>
																		<td></td>
																	</tr>
																	<tr>
																		<td class="labeltext"><html:checkbox
																				property="rejectOnRejectItemNotFound"
																				styleClass="authorizationParam" value="false"
																				styleId="rejectOnRejectItemNotFound" tabindex="22">
																				<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.rejectonrejectitemnotfound" />
																				<ec:elitehelp  header="servicepolicy.authpolicy.rejectonrejectitemnotfound" headerBundle="servicePolicyProperties" text="authpolicy.rejectonreject" ></ec:elitehelp>
																			</html:checkbox></td>
																		<td></td>
																	</tr>
																	<tr>
																		<td class="labeltext"><html:checkbox
																				styleClass="authorizationParam"
																				property="actionOnPolicyNotFound" value="false"
																				styleId="actionOnPolicyNotFound" tabindex="23">
																				<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.acceptonpolicynotfound" />
																				<ec:elitehelp  header="servicepolicy.authpolicy.acceptonpolicynotfound" headerBundle="servicePolicyProperties" text="authpolicy.actionpolicy" ></ec:elitehelp>
																			</html:checkbox></td>
																		<td></td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.gracepolicy" /> 
																<ec:elitehelp  header="servicepolicy.naspolicy.gracepolicy" headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.gracepolicy" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:select property="gracePolicy" styleClass="labeltext" styleId="gracePolicy" tabindex="18" style="width:175px;">
																	<html:option value="">--select--</html:option>
																	<logic:iterate id="gracePolicyInst" name="addNASServicePolicyForm" property="gracePolicyList" type="com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData">
																		<html:option value="<%=gracePolicyInst.getName()%>"><%=gracePolicyInst.getName()%></html:option>
																	</logic:iterate>
																</html:select>
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.diameterconcurrency" /> 
																<ec:elitehelp  header="servicepolicy.naspolicy.diameterconcurrency" headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.diameterconcurrency" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:select property="diameterConcurrency" styleClass="labeltext" styleId="diameterConcurrency" tabindex="18" style="width:175px;">
																	<html:option value="0">--select--</html:option>
																	<logic:iterate id="diameterConcurrencyInst" name="addNASServicePolicyForm" property="diameterConcurrencyDataList" type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData">
																		<html:option value="<%=String.valueOf(diameterConcurrencyInst.getDiaConConfigId())%>"><%=diameterConcurrencyInst.getName()%></html:option>
																	</logic:iterate>
																</html:select>
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.additionaldiameterconcurrency" /> 
																<ec:elitehelp  header="servicepolicy.naspolicy.additionaldiameterconcurrency" headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.additionaldiameterconcurrency" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:select property="additionalDiameterConcurrency" styleClass="labeltext" styleId="additionalDiameterConcurrency" tabindex="18" style="width:175px;">
																	<html:option value="0">--select--</html:option>
																	<logic:iterate id="additionalDiameterConcurrencyInst" name="addNASServicePolicyForm" property="diameterConcurrencyDataList" type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData">
																		<html:option value="<%=String.valueOf(additionalDiameterConcurrencyInst.getDiaConConfigId())%>"><%=additionalDiameterConcurrencyInst.getName()%></html:option>
																	</logic:iterate>
																</html:select>
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.defaultsessiontimeout" /> 
																<ec:elitehelp  header="servicepolicy.naspolicy.defaultsessiontimeout" headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.defaultsessiontimeout" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:text styleId="defaultSessionTimeout" property="defaultSessionTimeout" name="addNASServicePolicyForm" maxlength="10" style="width:175px;"/>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align="left" class="tblheader-bold" valign="top">
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.rfc4372cuiparameters" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<table border="0" width="100%" cellspacing="0" cellpadding="0">
														<tr>
															<td class="labeltext" width="23%">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.cui" />
																<ec:elitehelp  header="servicepolicy.naspolicy.cui" headerBundle="servicePolicyProperties" text="naspolicy.cui" ></ec:elitehelp>
															</td>
															<td class="labeltext">
																<html:select property="cui"  styleClass="cui-css labeltext" styleId="cui" style="width: 175;" tabindex="16">
																	<html:option value="NONE">NONE</html:option>
																	<html:option value="Authenticated-Identity">Authenticated-Identity</html:option>
																	<html:option value="Group">Group</html:option>
																	<html:option value="Profile-CUI">Profile-CUI</html:option>
																	<html:option value="Advanced">Advanced</html:option>
																</html:select>
															</td>
														</tr>
														<tr>
															<td class="labeltext" width="23%">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.advancedcuiexpression" />
																<ec:elitehelp  header="servicepolicy.naspolicy.advancedcuiexpression" headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.advancedcuiexpression" ></ec:elitehelp>
															</td>
															<td class="labeltext">
																<html:text property="advancedCuiExpression" styleId="advancedCuiExpression" styleClass="advancedCuiExpression" readonly="true" style="width:250px;"></html:text>
															</td>
														</tr>
														<tr>
															<td class="labeltext" width="23%">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.cuiresattrs" /> 
																<ec:elitehelp  header="servicepolicy.naspolicy.cuiresattrs" headerBundle="servicePolicyProperties" text="naspolicy.cuiresattrs" ></ec:elitehelp>
															</td>
															<td class="labeltext">
																<input type="text" name="cuiResponseAttributes" id="cuiResponseAttributes"	size="30" autocomplete="off" onkeyup="setColumnsOnCuiRespAttrTextFields();" style="width: 250px" tabindex="17" />
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align="left" class="tblheader-bold" valign="top">
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.responseattributes" />
												</td>
											</tr>
											<tr>
												<td>
													<table border="0" width="100%" cellspacing="0" cellpadding="0">
														<tr>
															<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
																<input type="button" value=" Add Mapping " class="light-btn" onclick="addResponseAttributesTable('responseAttributeTable','responseAttributesTemplate');" tabindex="25" />
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
																<!-- Attributes Table -->
																<table width="60%" cellspacing="0" cellpadding="0" border="0" id="responseAttributeTable" class="responseAttributeTable">
																	<tr>
																		<td align="left" class="tblheader" valign="top" width="35%">
																			<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.commandcode" />
																			<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.commandcode" header="servicepolicy.naspolicy.commandcode"/>
																		</td>
																		<td align="left" class="tblheader" valign="top" width="60%">
																			<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.responseattribute" />
																			<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.responseattribute" header="servicepolicy.naspolicy.responseattribute"/>
																		</td>
																		<td align="left" class="tblheader" valign="top"width="5%">Remove</td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>	
												<td>&nbsp;</td>
											</tr>
											<tr>
												<td>
													<table border="0" width="100%">
														<tr>
															<td class="btns-td" valign="middle">&nbsp;</td>
															<td class="btns-td" valign="middle" colspan="3"><input
																type="button" value="Next " class="light-btn"
																onclick="validate()" tabindex="25" /> <input
																type="reset" name="c_btnDeletePolicy"
																onclick="javascript:location.href='<%=basePath%>/initSearchNASServicePolicy.do?/>'"
																value="Cancel" class="light-btn" tabindex="26">
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</html:form></td>
							</tr>
						</table>
					</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
			</table>
		</td>
	</tr>
</table>
<script>
setInit();
setTitle('<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy"/>');
</script>
<div id="popupExpr" style="display: none;" title="ExpressionBuilder">
	<div id="expBuilderId" align="center"></div>
</div>
<table style="display:none;" id="responseAttributesTemplate">
	<tr>
		<td class="allborder" width="35%">
			<input  autocomplete="off" class="commandCode noborder" type="text" name="commandCode" maxlength="1000" size="28" style="width:200px;" onblur="setCommandCodeData(this);"/></td>
		<td class="tblrows" width="60%">
			<textarea rows="1" class="responseAttributes noborder" name="responseAttributes"  id="responseAttributes"  style="min-width:100%;min-height:20px;height:20px;"></textarea>
		</td>
		<td class="tblrows" align="center" width="5%"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  /></td>
	</tr>
</table> 