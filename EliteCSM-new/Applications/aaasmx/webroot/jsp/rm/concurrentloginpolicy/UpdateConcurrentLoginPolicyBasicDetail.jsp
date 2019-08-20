<%@ page import="java.util.List"%>
<%@ page
	import="com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.UpdateConcurrentLoginPolicyBasicDetailForm"%>
<%@ page
	import="com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyData"%>
<%@page
	import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page
	import="com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyData"%>

<%
	IConcurrentLoginPolicyData iConcurrentLoginPolicyData = (IConcurrentLoginPolicyData)request.getAttribute("concurrentLoginPolicyData");
%>




<script>
var isValidName;

$(document).ready(function() {	
	changePolicyMode();
});

function customValidate()
{
	document.updateConcurrentLoginPolicyBasicDetailForm.action.value = 'update';
	var retVar=true;
	
	if(document.updateConcurrentLoginPolicyBasicDetailForm.name.value == '' ){
		alert('Conc. Login Policy Name must be specified');
		return false;
	}

	if(!isValidName) {
		alert('Enter Valid Policy Name');
		document.forms[0].name.focus();
		return false;
	}

	if(document.forms[0].name.value == '' ){
		alert('Conc. Login Policy Name must be specified');
		return false;
	}
	if(document.forms[0].serviceWiseRadio.checked==true &&  document.forms[0].attribute.value == '0'){
		alert('Attribute must be selected');
		document.forms[0].attribute.focus();
		return false;
	}
	if(!limitCheck(document.forms[0].description,255)){
       alert("Description should not greater than "+255+ " character.");
       document.forms[0].description.focus();
       return false;
    }

	var regexpNum = /^\d+$/;
		if(document.updateConcurrentLoginPolicyBasicDetailForm.maxLogin[1].checked){
			//alert('unlimited');
			document.updateConcurrentLoginPolicyBasicDetailForm.login.disabled = false;
			document.updateConcurrentLoginPolicyBasicDetailForm.login.value = '-1';
			
		}else{
		
			if(!regexpNum.test(document.updateConcurrentLoginPolicyBasicDetailForm.login.value)  ){
			alert('Invalid value of Max. Concurrent Login. It must be zero or positive Integer.');
			return false;
            }
        } 
	
	
	
	return retVar;
}

function setLoginLimit(value) {
	if (value=="Limited") {
	    document.updateConcurrentLoginPolicyBasicDetailForm.login.disabled = false;
		document.updateConcurrentLoginPolicyBasicDetailForm.login.value="";
		
	} else if (value=="Unlimited") {
		document.updateConcurrentLoginPolicyBasicDetailForm.login.value = "-1";
		document.updateConcurrentLoginPolicyBasicDetailForm.login.disabled = true;
		
	} else {
		document.updateConcurrentLoginPolicyBasicDetailForm.login.value="";
	}
}
function changePolicyMode(){
	var generalElement = document.getElementById("generalRadio");
	var attributeElement = document.getElementById("attribute");
	if(generalElement.checked==true){
		attributeElement.disabled = true;
	}else{
		attributeElement.disabled = false;
	}
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.CONCURRENT_LOGIN_POLICY%>',searchName,'update','<%=iConcurrentLoginPolicyData.getConcurrentLoginId()%>','verifyNameDiv');
}
</script>

<%
	UpdateConcurrentLoginPolicyBasicDetailForm updateConcurrentLoginPolicyBasicDetailForm = (UpdateConcurrentLoginPolicyBasicDetailForm)request.getAttribute("updateConcurrentLoginPolicyBasicDetailForm");	
%>
<html:form action="/updateConcurrentLoginPolicyBasicDetail">
	<html:hidden name="updateConcurrentLoginPolicyBasicDetailForm"
		styleId="action" property="action" value='update' />
	<html:hidden name="updateConcurrentLoginPolicyBasicDetailForm"
		styleId="concurrentLoginId" property="concurrentLoginId" />
	<html:hidden name="updateConcurrentLoginPolicyBasicDetailForm"
		styleId="auditUId" property="auditUId" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0"
		height="15%" align="right">
		<tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="3"><bean:message
					bundle="radiusResources"
					key="concurrentloginpolicy.update.basicdetails" /></td>
		</tr>
		<tr>
			<td width="30%" height="20%" class="captiontext" valign="top"><bean:message
					bundle="radiusResources" key="concurrentloginpolicy.name" /> 
					<ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.name" 
					header="concurrentloginpolicy.name" />
			</td>
			<td width="70%" height="20%" class="labeltext" valign="top">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td width="55%"><html:text
								name="updateConcurrentLoginPolicyBasicDetailForm" tabindex="1"
								onkeyup="verifyName();" styleId="name" property="name" size="30"
								styleClass="flatfields" maxlength="30" style="width:250px" />&nbsp;&nbsp;
							<font color="#FF0000">*</font>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td width="30%" height="20%" class="captiontext" valign="top">
					<bean:message bundle="radiusResources" key="concurrentloginpolicy.description" />
					<ec:elitehelp headerBundle="radiusResources" 
					text="concurrentpolicy.description" header="concurrentloginpolicy.description"/>		
			</td>
			<td width="70%" height="20%" class="labeltext" valign="top">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td><html:textarea
								name="updateConcurrentLoginPolicyBasicDetailForm" tabindex="2"
								styleId="description" property="description" cols="30" rows="4"
								style="width:250px" /></td>
					</tr>
				</table>

			</td>
		</tr>

		<tr>
			<td align="left" class="captiontext" valign="top" width="12%"><bean:message
					bundle="radiusResources" key="concurrentloginpolicy.concurrentloginpolicytype" /> 
					<ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.policytype" 
					header="concurrentloginpolicy.concurrentloginpolicytype" />
			</td>
			<td align="left" class="labeltext" valign="top" colspan="2">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td align="left" class="labeltext" valign="top" width="5%">
							&nbsp;&nbsp; <html:radio
								name="updateConcurrentLoginPolicyBasicDetailForm" tabindex="3"
								styleId="concurrentLoginPolicy" property="concurrentLoginPolicy"
								value="SMS0138" />Individual <html:radio
								name="updateConcurrentLoginPolicyBasicDetailForm" tabindex="4"
								styleId="concurrentLoginPolicy" property="concurrentLoginPolicy"
								value="SMS0139" />Group
						</td>
					</tr>
				</table>
			</td>
		</tr>

		<tr>
			<td align="left" class="captiontext" valign="top" width="12%"><bean:message
					bundle="radiusResources"
					key="concurrentloginpolicy.maximumconcurrentlogin" />
					<ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.maxlogin" 
					header="concurrentloginpolicy.maximumconcurrentlogin" />
			</td>
			<td align="left" class="labeltext" valign="top" colspan="2">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td align="left" class="labeltext" valign="top" width="5%">
							&nbsp;&nbsp;<html:radio
								name="updateConcurrentLoginPolicyBasicDetailForm" tabindex="5"
								styleId="maxLogin" property="maxLogin" value="L"
								onclick="setLoginLimit('Limited')" />Limited
							&nbsp;&nbsp;&nbsp;&nbsp;<html:text tabindex="6"
								name="updateConcurrentLoginPolicyBasicDetailForm"
								styleId="login" property="login" size="30"
								styleClass="flatfields" maxlength="30" /><font color="#FF0000">
								*</font></br> &nbsp;&nbsp;<html:radio
								name="updateConcurrentLoginPolicyBasicDetailForm" tabindex="7"
								styleId="maxLogin" property="maxLogin" value="U"
								onclick="setLoginLimit('Unlimited')" />Unlimited
						</td>
					</tr>
				</table>
			</td>
		</tr>

		<tr>
			<td align="left" class="captiontext" valign="top" width="12%"><bean:message
					bundle="radiusResources" key="concurrentloginpolicy.policymode" />
					<ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.mode" 
					header="concurrentloginpolicy.policymode" />
			</td>
			<td align="left" class="labeltext" valign="top" colspan="2">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td align="left" class="labeltext" valign="top" width="5%">
							&nbsp;&nbsp;<html:radio
								name="updateConcurrentLoginPolicyBasicDetailForm" tabindex="8"
								styleId="generalRadio" property="concurrentLoginPolicyMode"
								value="SMS0149" onchange="changePolicyMode()" />General
							&nbsp;&nbsp;<html:radio
								name="updateConcurrentLoginPolicyBasicDetailForm" tabindex="9"
								styleId="serviceWiseRadio" property="concurrentLoginPolicyMode"
								value="SMS0150" onchange="changePolicyMode()" />ServiceWise
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="left" class="captiontext" valign="top" width="12%"><bean:message
					bundle="radiusResources" key="concurrentloginpolicy.attribute" />
					<ec:elitehelp headerBundle="radiusResources" text="concurrentpolicy.attribute" 
					header="concurrentloginpolicy.attribute" />
			</td>
			<td align="left" class="labeltext" valign="top" colspan="2">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td>&nbsp;&nbsp; <html:select property="attribute"
								styleId="attribute" tabindex="10">
								<html:option value="0">--Select--</html:option>
								<html:optionsCollection property="dictionaryParameterDetailList"
									name="updateConcurrentLoginPolicyBasicDetailForm" label="name"
									value="name" />
							</html:select>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td width="30%" height="20%" class="labeltext">&nbsp;</td>
			<td width="70%" height="20%" class="labeltext">&nbsp;</td>
		</tr>
		<tr>
			<td width="30%" height="20%" class="labeltext">&nbsp;</td>
			<td width="70%" height="20%" class="labeltext">&nbsp;</td>
		</tr>

		<tr>
			<td width="30%" height="20%" class="labeltext">&nbsp;</td>
			<td width="70%" height="20%" class="labeltext"><input
				type="submit" tabindex="11" name="c_btnCreate"
				onclick="return customValidate();" value="   Update   "
				class="light-btn">&nbsp;&nbsp; <input type="reset"
				tabindex="12" name="c_btnDeletePolicy"
				onclick="javascript:location.href='<%=basePath%>/viewConcurrentLoginPolicy.do?concurrentLoginPolicyId=<bean:write name="concurrentLoginPolicyBean" property="concurrentLoginId"/>'"
				value=" Cancel " class="light-btn"></td>
		</tr>

	</table>
</html:form>

