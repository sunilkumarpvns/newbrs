<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.UpdateNASServicePolicyRFC4372CUIParamsForm"%>
<%
	UpdateNASServicePolicyRFC4372CUIParamsForm nasPolicyForm = (UpdateNASServicePolicyRFC4372CUIParamsForm) request.getAttribute("nasPolicyForm");
%>
<script>
function validate(){
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
	var cuiOption = '<%=nasPolicyForm.getCui()%>';
	if(cuiOption == 'Advanced'){
		$("#advancedCuiExpression").attr("readonly", false);
	}
}
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<html:form action="/updateNASServicePolicyRFC4372CUIParamDetail">
<html:hidden name="updateNASServicePolicyRFC4372CUIParamsForm" styleId="nasPolicyId" property="nasPolicyId"/>
<html:hidden name="updateNASServicePolicyRFC4372CUIParamsForm" styleId="action" property="action" value = "update"/>	  
<html:hidden name="updateNASServicePolicyRFC4372CUIParamsForm" styleId="auditUId" property="auditUId"/>	 
<html:hidden name="updateNASServicePolicyRFC4372CUIParamsForm" styleId="name" property="name"/>	 
	<tr> 
      <td valign="top" align="left"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
			<tr>
				<td  class="tblheader-bold" valign="top"  width="27%" colspan="4">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.rfc4372cuiparameters"/>
				</td>
			</tr>
			<tr>
				<td align="left" class="captiontext" valign="top" nowrap="nowrap" width="30%">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.cui" />
					<ec:elitehelp  header="servicepolicy.naspolicy.cui" headerBundle="servicePolicyProperties" text="naspolicy.cui" ></ec:elitehelp>
				</td>
				<td align="left" class="labeltext" valign="top" colspan="3" width="70%">
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
				<td align="left" class="captiontext" valign="top" nowrap="nowrap" width="30%">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.advancedcuiexpression" />
					<ec:elitehelp  header="servicepolicy.naspolicy.advancedcuiexpression" headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.advancedcuiexpression" ></ec:elitehelp>
				</td>
				<td align="left" class="labeltext" valign="top" colspan="3" width="70%">
					<html:text property="advancedCuiExpression" styleId="advancedCuiExpression" styleClass="advancedCuiExpression" readonly="true" style="width:250px;"></html:text>
				</td>
			</tr>
			<tr>
				<td align="left" class="captiontext" valign="top" nowrap="nowrap" width="30%">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.cuiresattrs" /> 
					<ec:elitehelp  header="servicepolicy.naspolicy.cuiresattrs" headerBundle="servicePolicyProperties" text="naspolicy.cuiresattrs" ></ec:elitehelp>
				</td>
				<td align="left" class="labeltext" valign="top" colspan="3" width="70%">
					<html:text property="cuiResponseAttributes" styleId="cuiResponseAttributes" styleClass="cuiResponseAttributes" style="width:250px;" onkeyup="setColumnsOnCuiRespAttrTextFields();"></html:text>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td class="btns-td" valign="middle">
					&nbsp;
				</td>
				<td class="btns-td" valign="middle" colspan="3">
					<input type="button" value="Update "  class="light-btn" onclick="validate();"/>
					<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/viewNASServicePolicyDetail.do?nasPolicyId=<%=nasPolicyForm.getNasPolicyId()%>'" value="Cancel" class="light-btn">
				</td>
			</tr>
		</table>
		</td>
		</tr>
</html:form>
</table>							