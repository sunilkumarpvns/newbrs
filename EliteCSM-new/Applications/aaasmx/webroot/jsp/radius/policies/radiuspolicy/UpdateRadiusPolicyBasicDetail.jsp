<%@ page import="java.util.List"%>
<%@ page
	import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData"%>
<%@page
	import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page
	import="com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyData"%>

<% 
	RadiusPolicyData radiusPolicyData = (RadiusPolicyData)request.getAttribute("radiusPolicyData");
%>

<script>
var isValidName;

function customValidate()
{
		
	if(isNull(document.forms[0].name.value)){
		alert('Radius Policy Name must be specified');
		return false;
	}if(!isValidName) {
		alert('Enter Valid Policy Name');
		document.forms[0].name.focus();
		return false;
	}else{
		document.viewRadiusPolicyForm.action.value = 'update';	
		return true;
	}
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
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.RADIUS_POLICY%>',searchName,'update','<%=radiusPolicyData.getRadiusPolicyId()%>','verifyNameDiv');
}
</script>

<html:form action="/updateRadiusPolicyBasicDetail">
	<html:hidden name="viewRadiusPolicyForm" styleId="action"
		property="action" />
	<html:hidden name="viewRadiusPolicyForm" styleId="radiusPolicyId"
		property="radiusPolicyId" />
	<html:hidden name="viewRadiusPolicyForm" styleId="lastUpdated"
		property="lastUpdated" />
	<html:hidden name="viewRadiusPolicyForm" styleId="createDate"
		property="createDate" />
	<html:hidden name="viewRadiusPolicyForm"
		styleId="lastModifiedByStaffId" property="lastModifiedByStaffId" />
	<html:hidden name="viewRadiusPolicyForm" styleId="commonStatusId"
		property="commonStatusId" />
	<html:hidden name="viewRadiusPolicyForm" styleId="systemGenerated"
		property="systemGenerated" />
	<html:hidden name="viewRadiusPolicyForm" styleId="createdByStaffId"
		property="createdByStaffId" />
	<html:hidden name="viewRadiusPolicyForm" styleId="statusChangeDate"
		property="statusChangeDate" />
	<html:hidden name="viewRadiusPolicyForm" styleId="editable"
		property="editable" />


	<table width="100%" border="0" cellspacing="0" cellpadding="0"
		height="15%">
		<tr>
			<td width="20%" height="20%" class="captiontext" valign="top"><bean:message
					bundle="radiusResources" key="radiuspolicy.name" /></td>
			<td width="60%" height="20%" class="labeltext" valign="top">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td width="55%"><html:text name="viewRadiusPolicyForm"
								styleId="name" onkeyup="verifyName();" property="name" size="30"
								styleClass="flatfields" maxlength="30" style="width:250px" />
							<div id="verifyNameDiv" class="labeltext"></div></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td height="20%" class="captiontext" valign="top"><bean:message
					bundle="radiusResources" key="radiuspolicy.description" /></td>
			<td height="20%" class="labeltext" valign="top">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td><html:textarea name="viewRadiusPolicyForm"
								styleId="description" property="description" cols="30" rows="4"
								style="width:250px" /></td>
					</tr>
				</table>

			</td>
		</tr>
		<tr>
			<td height="20%" class="labeltext">&nbsp;</td>
			<td height="20%" class="labeltext">&nbsp;</td>
		</tr>

		<tr>
			<td height="20%" class="labeltext">&nbsp;</td>
			<td height="20%" class="labeltext">&nbsp;</td>
		</tr>

		<tr>
			<td height="20%" class="labeltext">&nbsp;</td>
			<td height="20%" class="labeltext"><input type="submit"
				name="c_btnCreate" onclick="return customValidate();"
				value="   Update   " class="light-btn">&nbsp;&nbsp; <input
				type="reset" name="c_btnDeletePolicy"
				onclick="javascript:location.href='<%=basePath%>/viewRadiusPolicy.do?radiusPolicyId=<bean:write name="radiusPolicyBean" property="radiusPolicyId"/>'"
				value=" Cancel " class="light-btn"></td>
		</tr>

	</table>
</html:form>

