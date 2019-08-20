<%@page import="com.elitecore.elitesm.datamanager.diameter.diameterpolicygroup.data.DiameterPolicyGroup"%>
<%@page import="com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyData"%>

<%
	DiameterPolicyGroup diameterPolicyGroupData = (DiameterPolicyGroup)request.getAttribute("diameterPolicyGroup");
%>
<bean:define id="diameterPolicyGroupBean" name="diameterPolicyGroup" scope="request" type="com.elitecore.elitesm.datamanager.diameter.diameterpolicygroup.data.DiameterPolicyGroup" />
<script>

var isValidName;

function customValidate()
{
	if(isNull(document.forms[0].policyname.value)){
		alert('Diameter Policy Group Name must be specified');
		return false;
	}
	if(!isValidName) {
		alert('Enter Valid Diameter Policy Group Name');
		document.forms[0].policyName.focus();
		return false;
	}
		
	document.diameterPolicyGroupForm.action.value = 'update';	
	document.diameterPolicyGroupForm.submit();
	
}
function validateName(val)
{
	var test1 = /(^[A-Za-z0-9-]*$)/;
	var regexp =new RegExp(test1);
	if(regexp.test(val)){
		return true;
	}
	return false;
}

function verifyName() {
	var searchName = document.getElementById("policyname").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DIAMETER_POLICY_GROUP%>',searchName,'update','<%=diameterPolicyGroupData.getPolicyId()%>','verifyNameDiv');
}

</script>

<html:form action="/updateDiameterPolicyGroup">
	<html:hidden name="diameterPolicyGroupForm" styleId="action" property="action" value="update"/>
	<html:hidden name="diameterPolicyGroupForm" styleId="policyId" property="policyId" />
	<html:hidden name="diameterPolicyGroupForm" styleId="auditUId" property="auditUId" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
		<tr>
			<td height="30%" class="captiontext" valign="top">&nbsp;</td>
			<td height="70%" class="labeltext" valign="top">&nbsp;</td>
		</tr>
		<tr>
			<td width="30%" height="20%" class="captiontext" valign="top">
				<bean:message bundle="diameterResources"  key="diameterpolicy.diameterpolicygroup.name" /> 
				<ec:elitehelp headerBundle="diameterResources" text="diameterpolicy.diameterpolicygroup.name" header="diameterpolicy.diameterpolicygroup.name"/>
			</td>
			<td width="70%" height="20%" class="labeltext" valign="top">
				<html:text styleId="policyname" tabindex="1" name="diameterPolicyGroupForm" property="policyname" onblur="verifyName();" size="30" styleClass="flatfields" style="font-family: Verdana; width:250px " maxlength="30"  />
				<div id="verifyNameDiv" class="labeltext"></div>
			</td>
		</tr>
		<tr>
			<td height="30%" class="captiontext" valign="top">
				<bean:message bundle="diameterResources" key="diameterpolicy.diameterpolicygroup.expression" />
				<ec:elitehelp headerBundle="diameterResources" text="diameterpolicy.diameterpolicygroup.expression" header="diameterpolicy.diameterpolicygroup.expression"/>
			</td>
			<td height="70%" class="labeltext" valign="top">
				<html:text styleId="expression" tabindex="1" name="diameterPolicyGroupForm" property="expression"  maxlength="100" style="width:250px;" />
			</td>
		</tr>
		<tr>
			<td height="30%" class="captiontext" valign="top">&nbsp;</td>
			<td height="70%" class="labeltext" valign="top">&nbsp;</td>
		</tr>
		<tr>
			<td height="30%" class="labeltext">&nbsp;</td>
			<td height="70%" class="labeltext">
				<input type="button" tabindex="12" name="c_btnCreate" onclick="customValidate();" value="   Update   " class="light-btn">&nbsp;&nbsp; 
				<input type="reset" tabindex="13" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/viewDiameterPolicyGroup.do?policyId=<bean:write name="diameterPolicyGroupBean" property="policyId"/>'" value=" Cancel " class="light-btn">
			</td>
		</tr>

	</table>
</html:form>