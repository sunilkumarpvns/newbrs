<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms.UpdateEAPPolicyForm"%>
<%
	UpdateEAPPolicyForm eapPolicyForm = (UpdateEAPPolicyForm) request.getAttribute("updateEAPPolicyForm");
%>
<script type="text/javascript">
function validateForm(){
	if( $('#cui').val() == 'Advanced' && isNull($('#advancedCuiExpression').val())){
		alert('Advanced CUI Expression must be specified');
		$('#advancedCuiExpression').focus();
		return;
	}else{
		document.forms[0].submit();
	}
}

$(document).ready(function(){
	setAdvancedCUIConfiguration();
	
	/* Advaned CUI Configuration */
	$(".cui-css").change(function(){
		   if($(this).val() == 'Advanced'){
			   $("#advancedCuiExpression").attr("readonly", false);
		   }else{
			   $("#advancedCuiExpression").attr("readonly", true);
		   }
	});
});

function setColumnsOnCuiRespAttrTextFields(){
	var cuiRespAttrVal = document.getElementById("cuiResponseAttributes").value;
	retriveRadiusDictionaryAttributes(cuiRespAttrVal,"cuiResponseAttributes");
}

function setAdvancedCUIConfiguration(){
	var cuiOption = '<%=eapPolicyForm.getCui()%>';
	if(cuiOption == 'Advanced'){
		$("#advancedCuiExpression").attr("readonly", false);
	}
}
</script>

<html:form action="/updateRFC4372CUIDetails">
	<html:hidden name="updateEAPPolicyForm" styleId="action" property="action" value="update"/>
	<html:hidden name="updateEAPPolicyForm" styleId="policyId" property="policyId"/>
	<html:hidden name="updateEAPPolicyForm" styleId="auditUId" property="auditUId"/>
	<html:hidden name="updateEAPPolicyForm" styleId="name" property="name"/>
<table width="100%">
<tr>
	<td align="left" class="tblheader-bold" valign="top" colspan="4">
		<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.update.rfc4372cui.details" />
	</td>
</tr>
<tr>
	<td align="left" class="captiontext" valign="top" nowrap="nowrap" width="30%" >
		<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.cui" /> 
		<ec:elitehelp  header="servicepolicy.eappolicy.cui" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.cui" ></ec:elitehelp>
	</td>
	<td align="left" class="labeltext" valign="top" colspan="3" width="70%" >
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
	<td align="left" class="captiontext" valign="top" nowrap="nowrap" width="30%" >
		<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.advancedcuiexpression" /> 
		<ec:elitehelp  header="servicepolicy.eappolicy.advancedcuiexpression" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.advancedcuiexpression" ></ec:elitehelp>
	</td>
	<td align="left" class="labeltext" valign="top" colspan="3" width="70%" >
		<html:text property="advancedCuiExpression" styleId="advancedCuiExpression" styleClass="advancedCuiExpression" readonly="true" style="width:250px;"></html:text>
	</td>
</tr>	
<tr>
	<td align="left" class="captiontext" valign="top" nowrap="nowrap" width="30%" >
		<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.cuiresattrs" /> 
		<ec:elitehelp  header="servicepolicy.naspolicy.cuiresattrs" headerBundle="servicePolicyProperties" text="naspolicy.cuiresattrs" ></ec:elitehelp>
	</td>
	<td align="left" class="labeltext" valign="top" colspan="3" width="70%" >
		<html:text property="cuiResponseAttributes" styleId="cuiResponseAttributes" styleClass="cuiResponseAttributes" style="width:250px;" onkeyup="setColumnsOnCuiRespAttrTextFields();"></html:text>
	</td>
</tr>						

<tr> 
	<td colspan="2" class="small-gap">&nbsp;</td>
</tr>

<tr>
	<td class="btns-td" valign="middle">&nbsp;</td>
	<td class="btns-td" valign="middle" colspan="2">
		<input type="button" name="c_btnUpdate" id="c_btnUpdate" value="Update" class="light-btn"  onclick="validateForm()"> 
		<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchEAPPolicy.do?'" value="Cancel" class="light-btn">
	</td>
</tr>

</table>
</html:form>							