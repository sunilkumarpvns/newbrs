<%@page import="com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyData"%>

<%
	RadiusPolicyGroup radiusPolicyGroupData =(RadiusPolicyGroup)request.getAttribute("radiusPolicyGroup");
%>
<bean:define id="radiusPolicyGroupBean" name="radiusPolicyGroup" scope="request" type="com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup" />
<script>

var isValidName;

function customValidate()
{
	if(isNull(document.forms[0].policyname.value)){
		alert('Radius Policy Group Name must be specified');
		return false;
	}
	if(!isValidName) {
		alert('Enter Valid Radius Policy Group Name');
		document.forms[0].policyName.focus();
		return false;
	}
		
	document.radiusPolicyGroupForm.action.value = 'update';	
	document.radiusPolicyGroupForm.submit();
	
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
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.RADIUS_POLICY_GROUP%>',searchName,'update','<%=radiusPolicyGroupData.getPolicyId()%>','verifyNameDiv');
}

</script>

<html:form action="/updateRadiusPolicyGroup">
	<html:hidden name="radiusPolicyGroupForm" styleId="action" property="action" value="update"/>
	<html:hidden name="radiusPolicyGroupForm" styleId="policyId" property="policyId" />
	<html:hidden name="radiusPolicyGroupForm" styleId="auditUId" property="auditUId" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
		<tr>
			<td height="30%" class="captiontext" valign="top">&nbsp;</td>
			<td height="70%" class="labeltext" valign="top">&nbsp;</td>
		</tr>
		<tr>
			<td width="30%" height="20%" class="captiontext" valign="top">
				<bean:message bundle="radiusResources"  key="radiuspolicy.radiuspolicygroup.name" /> 
				<ec:elitehelp headerBundle="radiusResources" text="radiuspolicy.radiuspolicygroup.name" header="radiuspolicy.radiuspolicygroup.name"/>
			</td>
			<td width="70%" height="20%" class="labeltext" valign="top">
				<html:text styleId="policyname" tabindex="1" name="radiusPolicyGroupForm" property="policyname" onblur="verifyName();" size="30" styleClass="flatfields" style="font-family: Verdana; width:250px " maxlength="30"  />
				<div id="verifyNameDiv" class="labeltext"></div>
			</td>
		</tr>
		<tr>
			<td height="30%" class="captiontext" valign="top">
				<bean:message bundle="radiusResources" key="radiuspolicy.radiuspolicygroup.expression" />
				<ec:elitehelp headerBundle="radiusResources" text="radiuspolicy.radiuspolicygroup.expression" header="radiuspolicy.radiuspolicygroup.expression"/>
			</td>
			<td height="70%" class="labeltext" valign="top">
				<html:text styleId="expression" tabindex="1" name="radiusPolicyGroupForm" property="expression"  maxlength="100" style="width:250px;" />
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
				<input type="reset" tabindex="13" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/viewRadiusPolicyGroup.do?policyId=<bean:write name="radiusPolicyGroupBean" property="policyId"/>'" value=" Cancel " class="light-btn">
			</td>
		</tr>

	</table>
</html:form>