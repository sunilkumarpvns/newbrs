<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.form.TGPPAAAPolicyForm"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData"%>
<%@page import="com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.diameterapi.diameter.common.util.constant.CommandCode" %>

<%
	TGPPAAAPolicyData tgppAAAPolicyDataObj = (TGPPAAAPolicyData) request.getAttribute("tgppAAAPolicyData");
	TGPPAAAPolicyForm tgppAAAPolicyForm = (TGPPAAAPolicyForm) request.getAttribute("tgppAAAPolicyForm");
%>
<script>
	var commandCodeList = [];
	
	<%for(CommandCode commandCode:CommandCode.VALUES){%>
	commandCodeList.push({'value':'<%=commandCode.code%>','label':'[<%=commandCode.code%>] <%=commandCode.name()%>','id':'<%=commandCode.code%>'});
	<%}%>
</script>
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/servicepolicy/tgppaaapolicy/tgpp-aaa-policy.js"></script>
<style>
/* Overridden this class for managing auto-complete height of Command Code and Response Attribute in TGPP Basic Detail*/
.ui-autocomplete {
        max-height: 100px;
        overflow-y: auto;
        /* prevent horizontal scrollbar */
        overflow-x: hidden;
} 
</style>
<script>
$(document).ready(function(){
	setExpressionData("Diameter");
	addCommandCodeResponseAttrRows('responseAttributesTemplate');
});

var isValidName;
function validate()
{	
	if(isNull(document.forms[0].name.value)){
		alert('Name must be specified');
		document.forms[0].name.focus();
		return;
	}

	if($('#ruleset').val().trim() == ''){
		alert('Ruleset must be specified');
		$('#ruleset').focus();
		return;
	}
	
	if(!isValidName) {
		alert('Enter Valid Name');
		document.forms[0].name.focus();
		return;
	}

	if(!validateName(document.forms[0].name.value)){
		alert('Name should have following characters. A-Z, a-z, 0-9, _ and - ');
		document.forms[0].name.focus();
		return;
	}
	
	if(isRespAttrValidMappings() == false){
		return;
	}
	
	var behaviour = $("#defaultResponseBehaviour").val();
	if(behaviour == "REJECT" || behaviour == "HOTLINE"){
		if($("#defaultResponseBehaviourArgument").val().trim() == ""){
			alert("Default Response Behaviour Argument must be specified when Default Response Behaviour is "+behaviour);
			$("#defaultResponseBehaviourArgument").focus();
			return false;
		}
	}
	if($("#sessionManagement").val() == "true"){
		alert("For achieving 3GPP policy specific session management make sure that session manager should be bounded at Diameter stack");
	}
	fetchResponseAttrData();
	
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
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.TGPP_AAA_POLICY%>',searchName,'update','<%=tgppAAAPolicyDataObj.getTgppAAAPolicyId()%>','verifyNameDiv');
} 
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<html:form action="/updateTGPPAAAServicePolicyBasicDetail">
		<html:hidden name="tgppAAAPolicyForm" styleId="tgppAAAPolicyId" property="tgppAAAPolicyId" value="<%=tgppAAAPolicyDataObj.getTgppAAAPolicyId()%>" />
		<html:hidden name="tgppAAAPolicyForm" styleId="action" property="action" />
		<html:hidden name="tgppAAAPolicyForm" styleId="auditUid" property="auditUid" />
		<html:hidden name="tgppAAAPolicyForm" styleId="tgppAAAPolicyXml" property="tgppAAAPolicyXml" />
		<html:hidden name="tgppAAAPolicyForm" styleId="commandCodeWiseRespAttrib" property="commandCodeWiseRespAttrib" />
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
					
					<!-- Name -->
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message key="general.name" /> 
							<ec:elitehelp  header="tgppaaapolicy.name" headerBundle="servicePolicyProperties" text="tgppaaapolicy.name" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" width="25%" nowrap="nowrap" colspan="2">
							<html:text property="name" size="30" name="tgppAAAPolicyForm" style="width: 250px;" styleId="name" tabindex="1" onblur="verifyName();"/>
							<font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:checkbox styleId="status" property="status" value="1" tabindex="2" />Active
						</td>
					</tr>
					
					<!-- Description -->
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message key="general.description" />
							<ec:elitehelp header="tgppaaapolicy.description" headerBundle="servicePolicyProperties" text="tgppaaapolicy.description" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2"  width="40%">
							<html:textarea styleId="description" name="tgppAAAPolicyForm" property="description" cols="60" rows="2" styleClass="labeltext" tabindex="3" />
						</td>
					</tr>
					
					<!-- Ruleset -->
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.ruleset" />
							<ec:elitehelp header="tgppaaapolicy.ruleset" headerBundle="servicePolicyProperties" text="tgppaaapolicy.ruleset" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2" width="50%">
							<html:textarea property="ruleset" name="tgppAAAPolicyForm" styleId="ruleset" rows="2" cols="60" tabindex="4" disabled="false"/>
							<font color="#FF0000"> *</font> 
							<img alt="Expression" src="<%=basePath%>/images/lookup.jpg" onclick="popupExpression('ruleset');" class="ruleset" tabindex="5" />
						</td>
					</tr>
					
					<!-- User Identity -->
					<tr>
						<td align="left" class="captiontext" valign="top" width="16%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.useridentity" /> 
							<ec:elitehelp  header="servicepolicy.authpolicy.useridentity" headerBundle="servicePolicyProperties" text="radiusservicepolicy.useridentity" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
							<html:text property="userIdentity" name="tgppAAAPolicyForm" styleId="userIdentity" style="width: 250px;" tabindex="6" onkeyup="setDiameterDicAttr('userIdentity');"/>
						</td>
					</tr>
					<!-- Session management -->
					<tr>
						<td align="left" class="captiontext" valign="top" width="16%">
							<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.sessionmanagement" />
							<ec:elitehelp  header="tgppaaapolicy.sessionmanagement" headerBundle="servicePolicyProperties" text="tgppaaapolicy.sessionmanagment" note="tgppaaapolicy.sessionmanagement.note" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:select property="sessionManagement" name="tgppAAAPolicyForm" styleId="sessionManagement" tabindex="7">
								<html:option value="false">False</html:option>
								<html:option value="true">True</html:option>
							</html:select>
						</td>
                    </tr>
					
					<!-- CUI -->
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.cui" />
							<ec:elitehelp  header="tgppaaapolicy.cui" headerBundle="servicePolicyProperties" text="tgppaaapolicy.cui" note="tgppaaapolicy.cui.note" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2" width="40%">
							<html:text property="cui" name="tgppAAAPolicyForm" size="30" style="width: 250px;" styleId="cui" tabindex="8" onkeyup="setDiameterDicAttr('cui')"/>
						</td>
					</tr>
					
					<tr>	
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="servicePolicyProperties" key="tgppserviceepolicy.defaultresponsebehavior" />  
							<ec:elitehelp  header="tgppserviceepolicy.defaultresponsebehavior"  headerBundle="servicePolicyProperties" text="tgppaaapolicy.defaultresponsebehavior" ></ec:elitehelp>							
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:select name="tgppAAAPolicyForm" styleId="defaultResponseBehaviour" property="defaultResponseBehaviour" style="width:90px" tabindex="9" >
								<logic:iterate id="behaviour"  collection="<%=DefaultResponseBehavior.DefaultResponseBehaviorType.values() %>">
									<html:option value="<%=((DefaultResponseBehavior.DefaultResponseBehaviorType)behaviour).name()%>"><%=((DefaultResponseBehavior.DefaultResponseBehaviorType)behaviour).name()%></html:option>
								</logic:iterate>
							</html:select>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="servicePolicyProperties" key="tgppserviceepolicy.defaultresponsebehaviourargument" /> 
							<ec:elitehelp  header="tgppserviceepolicy.defaultresponsebehaviourargument" headerBundle="servicePolicyProperties" text="tgppaaapolicy.defaultresponsebehaviourargument" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:text property="defaultResponseBehaviourArgument" name="tgppAAAPolicyForm" styleId="defaultResponseBehaviourArgument"  maxlength="1000" styleClass="textbox_width" tabindex="10" />
						</td> 
					</tr>
					
					<!-- Command Code wise Response Attribute -->
					<tr>
						<td align="left" class="tblheader-bold" valign="top"  width="10%" colspan="6">
							<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.commandcodewiseresattribute" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
							<input type="button" value=" Add Mapping " class="light-btn" onclick="addResponseAttributesTable('responseAttributeTable','responseAttributesTemplate');" tabindex="9"/>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap" colspan="5">
							<!-- Attributes Table -->
							<table width="76%" cellspacing="0" cellpadding="0" border="0" id="responseAttributeTable" class="responseAttributeTable">
								<tr>
									<td align="left" class="tblheader" valign="top" width="48%">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.commandcode" />
										<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.commandcode" header="servicepolicy.naspolicy.commandcode"/>
									</td>
									<td align="left" class="tblheader" valign="top" width="48%">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.responseattribute" />
										<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.responseattribute" header="servicepolicy.naspolicy.responseattribute"/>
									</td>
									<td align="left" class="tblheader" valign="top"width="4%">Remove</td>
								</tr>
							</table>
						</td>
					</tr>
					
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="3">
							<input 	type="button" value="Update " class="light-btn" onclick="validate()" tabindex="10" /> 
							<input type="reset" name="c_btnDeletePolicy"	onclick="javascript:location.href='<%=basePath%>/viewTGPPAAAPolicy.do?tgppAAAPolicyId=<%=tgppAAAPolicyDataObj.getTgppAAAPolicyId()%>'" value="Cancel" class="light-btn" tabindex="11">
						</td>
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

<table style="display:none;" id="responseAttributesTemplate">
	<tr>
		<td class="allborder" width="48%">
			<input  autocomplete="off" class="commandCode noborder" type="text" name="commandCode" maxlength="2000" size="28" style="width:100%;"/></td>
		<td class="tblrows" width="48%">
			<textarea rows="1" class="responseAttributes noborder" name="responseAttributes"  style="min-width:100%;min-height:17px;height:17px;"></textarea>
		</td>
		<td class="tblrows" align="center" width="4%">
			<span class='delete remove-proxy-server' onclick="deleteMe(this);" />&nbsp;
		</td>
	</tr>
</table> 