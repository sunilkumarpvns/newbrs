<%@page import= "com.elitecore.elitesm.util.constants.InstanceTypeConstants" %>
<%@page import="com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior"%>
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script>
setExpressionData("Diameter");

var isValidName;

 	function validateForm(){
 		 var behaviour = $("#defaultResponseBehavior").val();
 		if(isNull(document.forms[0].name.value)){
 			alert('Name must be specified');
 		}else if(!isValidName) {
 			alert('Enter Valid Name');
 			document.forms[0].name.focus();
 			return;
 		}else if(isNull(document.forms[0].ruleSet.value)){
 			alert('RuleSet must be specified');
 		}else if((behaviour == "REJECT" || behaviour == "HOTLINE") && $("#defaultResponseBehaviorArgument").val().trim() == ""){
 				alert("Default Response Behaviour Argument must be specified when Default Response Behaviour is "+behaviour);
				$("#defaultResponseBehaviorArgument").focus();
 				return false;
 		}
 		else{
 			document.forms[0].action.value = 'update';
 	 	 	document.forms[0].submit();	 				
 		}
	}

 	

function verifyName() {
 	var searchName = document.getElementById("name").value;
 	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DIAMETER_EAP_POLICY%>',searchName,'update',$("#policyId").val(),'verifyNameDiv');
 }

 	
</script>

<html:form action="/updateEAPPolicyBasicDetail">
	<html:hidden name="updateEAPPolicyForm" styleId="action" property="action" value="update"/>
	<html:hidden name="updateEAPPolicyForm" styleId="policyId" property="policyId"/>  
	<html:hidden name="updateEAPPolicyForm" styleId="auditUId" property="auditUId"/>  
	<table  width="100%" >
			
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							 <bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.update.basic.details" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top"  width="12%" >
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.name" />
							<ec:elitehelp headerBundle="servicePolicyProperties" text="eappolicy.name" header="servicepolicy.eappolicy.name"/>
						</td>
						<td align="left" class="labeltext" valign="top"  nowrap="nowrap" colspan="2">
							<html:text styleId="name" property="name" size="30" onkeyup="verifyName();" maxlength="60" style="width:250px"/><font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div>
						</td>		
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="12%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.desp" />
						</td>
						<td align="left" class="labeltext" valign="top" width="30%">
							<html:textarea styleId="description" property="description" cols="40" rows="2" style="width:250px"/>
						</td>							
					</tr>	
					<tr>
						<td align="left" class="captiontext" valign="top" width="12%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.ruleset" />
							<ec:elitehelp headerBundle="servicePolicyProperties" text="eappolicy.ruleset" header="servicepolicy.eappolicy.ruleset"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="30%" colspan="2">
							<html:textarea styleId="ruleSet" property="ruleSet" cols="40" rows="2" style="width:250px"/>
							<font color="#FF0000"> *</font>		
							<img alt="Expression" src="<%=basePath%>/images/lookup.jpg" onclick="popupExpression('ruleSet');" />													
						</td>							
					</tr>	
					<tr>
						<td align="left" class="captiontext" valign="top" width="12%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.sessionmanagement" />
							<ec:elitehelp  header="servicepolicy.eappolicy.sessionmanagement" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.sessionmanagement" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" width="30%" colspan="2">
							<html:select property="sessionManagement" styleId="sessionManagement">
								<html:option value="true">True</html:option>
								<html:option value="false">False</html:option>
							</html:select>														
						</td>							
					</tr>	
					
					<tr>
						<td class="captiontext" valign="top">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.requestmode" /> 
							<ec:elitehelp headerBundle="servicePolicyProperties" text="authpolicy.reqmod" header="servicepolicy.authpolicy.requestmode"/>
						</td>
						<td class="labeltext" valign="top">
							<html:select property="requestType" styleClass="labeltext" styleId="requestType"  >
								<html:option value="1">Authenticate-Only</html:option>
								<html:option value="2">Authorize-Only</html:option>
								<html:option value="3">Authenticate and Authorize</html:option>
							</html:select>
						</td>	
				   </tr>
				   <tr>	
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.defaultresponsebehaviour" /> 
							<ec:elitehelp  header="servicepolicy.eappolicy.defaultresponsebehaviour"  headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.defaultauthresponsebehavior" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:select name="updateEAPPolicyForm" styleId="defaultResponseBehavior" property="defaultResponseBehavior" style="width:90px" tabindex="9" >
								<logic:iterate id="behaviour"  collection="<%=DefaultResponseBehavior.DefaultResponseBehaviorType.values() %>">
									<html:option value="<%=((DefaultResponseBehavior.DefaultResponseBehaviorType)behaviour).name()%>"><%=((DefaultResponseBehavior.DefaultResponseBehaviorType)behaviour).name()%></html:option>
								</logic:iterate>
							</html:select>
						</td>
				</tr>
				<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.defaultresponsebehaviourargument" /> 
							<ec:elitehelp  header="servicepolicy.eappolicy.defaultresponsebehaviourargument" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.defaultresponsebehaviourargument" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:text property="defaultResponseBehaviorArgument" name="updateEAPPolicyForm" styleId="defaultResponseBehaviorArgument"  maxlength="1000" styleClass="textbox_width" tabindex="12"/>
						</td> 
				</tr>
			
				   <tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="2">
							<input type="button" name="c_btnCreate" onclick="validateForm()" id="c_btnCreate2" value="Update" class="light-btn">
							<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchEAPPolicy.do?'" value="Cancel" class="light-btn"> 
						</td>
					</tr>
				</table>
</html:form>
<div id="popupExpr" style="display: none;" title="ExpressionBuilder"> 
	<div id="expBuilderId" align="center" ></div>
</div>
