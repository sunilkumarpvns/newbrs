<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData"%>
<%@page import="com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior"%>
<%
	NASPolicyInstData data = (NASPolicyInstData)request.getAttribute("nasPolicyInstData");   
%>
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script>
setExpressionData("Diameter");

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
	
	if(isNull(document.forms[0].ruleSet.value)){
		alert('Policy Selection Rule must be specified');
		document.forms[0].ruleSet.focus();
		return;
	}

	if(!validateName(document.forms[0].name.value)){
		alert('Policy Name should have following characters. A-Z, a-z, 0-9, _ and - ');
		document.forms[0].name.focus();
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
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.NAS_POLICY%>',searchName,'update','<%=data.getNasPolicyId()%>','verifyNameDiv');
}
</script>



<table width="100%" border="0" cellspacing="0" cellpadding="0">
<html:form action="/updateNASServicePolicyBasicDetail">
<html:hidden name="updateNASServicePolicyBasicForm" styleId="nasPolicyId" property="nasPolicyId" />
<html:hidden name="updateNASServicePolicyBasicForm" styleId="action" property="action" />   
<html:hidden name="updateNASServicePolicyBasicForm" styleId="auditUId" property="auditUId" />   
    <tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
         
									<tr>
										<td align="left" class="tblheader-bold" valign="top"  width="27%" colspan="6">
											<bean:message bundle="servicePolicyProperties" key="basic.details"/>
										</td>									
									</tr>
									<tr>
										<td align="left" class="captiontext" valign="top"  width="10%">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.name" />
											<ec:elitehelp headerBundle="servicePolicyProperties" text="naspolicy.name" header="general.name"/>
										</td>
										<td align="left" class="labeltext" valign="top"  nowrap="nowrap" colspan="3">
												<table border="0" cellspacing="0" cellpadding="0">  
													<tr>
														<td style="width:270px">
															<html:text property="name" size="40" onkeyup="verifyName();" styleId="name" style="width:250px"/><font color="#FF0000"> *</font>&nbsp;
															<div id="verifyNameDiv" class="labeltext"></div>
														</td>
														<td  align="left" class="labeltext" valign="top">
															<input type="checkbox" name="status" id="status" value="1" checked="checked"/>&nbsp;Active
														</td>			
												</table>
										</td>
									</tr>

									<tr>
										<td align="left"  class="captiontext" valign="top" width="10%">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.desp" />
										</td>
										<td align="left" class="labeltext" valign="top" colspan="3">
											<html:textarea property="description"  styleId="description" rows="2" cols="50"/>
										</td>
									</tr>
									<tr>
										<td align="left" class="captiontext" valign="top" width="10%">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.ruleset"/>
											<ec:elitehelp headerBundle="servicePolicyProperties" text="naspolicy.ruleset" header="servicepolicy.naspolicy.ruleset"/>
										</td>
										<td align="left" class="labeltext" valign="top" colspan="3">
											<html:textarea property="ruleSet" styleId="ruleSet"  rows="2" cols="50"/><font color="#FF0000"> *</font>
											<img alt="Expression" src="<%=basePath%>/images/lookup.jpg" onclick="popupExpression('ruleSet');" />
										</td>
									</tr>
									<tr>
										<td align="left" class="captiontext" valign="top" width="10%">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.sessionmanagement" /> 
											<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.sessionmanagement" header="servicepolicy.naspolicy.sessionmanagement"/>
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
											<ec:elitehelp headerBundle="servicePolicyProperties" text="authpolicy.reqmod" header="servicepolicy.authpolicy.requestmode"/>
										</td>
										<td align="left" class="labeltext" valign="top">
											<html:select property="requestType" styleClass="labeltext" styleId="requestType" style="width: 200;">
													<html:option value="1">Authenticate-Only</html:option>
													<html:option value="2">Authorize-Only</html:option>
													<html:option value="3">Authenticate and Authorize</html:option>
											</html:select>
										</td>	
									</tr>																		
									<tr>	
										<td align="left" class="captiontext" valign="top">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.defaultresponsebehaviour" /> 
											<ec:elitehelp  header="servicepolicy.naspolicy.defaultresponsebehaviour"  headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.defaultauthresponsebehavior" ></ec:elitehelp>
										</td>
										<td align="left" class="labeltext" valign="top">
											<html:select name="updateNASServicePolicyBasicForm" styleId="defaultResponseBehaviour" property="defaultResponseBehaviour" style="width:90px" tabindex="9" >
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
											<html:text property="defaultResponseBehaviourArgument" name="updateNASServicePolicyBasicForm" styleId="defaultResponseBehaviourArgument"  maxlength="1000" styleClass="textbox_width" tabindex="12" />
										</td> 
								</tr>
									<tr>
										<td class="btns-td" valign="middle">
											&nbsp;
										</td>
										<td class="btns-td" valign="middle" colspan="3">
											<input type="button" value="Update "  class="light-btn" onclick="validate()"/>
											<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/viewNASServicePolicyDetail.do?nasPolicyId=<%=data.getNasPolicyId()%>'"
												value="Cancel" class="light-btn"/>
										</td>
									</tr>
		</table>
		</td>
    </tr>
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
   </tr>
</table>

</html:form>

<div id="popupExpr" style="display: none;" title="ExpressionBuilder"> 
	<div id="expBuilderId" align="center" ></div>
</div>