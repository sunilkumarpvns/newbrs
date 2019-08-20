<%@page import="com.elitecore.elitesm.web.diameter.diameterpolicygroup.forms.DiameterPolicyGroupForm"%>
<%@page import="com.elitecore.elitesm.web.radius.radiuspolicygroup.forms.RadiusPolicyGroupForm"%>
<%@page import="com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.RadiusPolicyForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	DiameterPolicyGroupForm diameterPolicyGroupForm = (DiameterPolicyGroupForm)request.getAttribute("diameterPolicyGroupForm");
	String basePath = request.getContextPath();
    String statusVal=(String)request.getParameter("status");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>

var isValidName;

function customValidate(){
	
	if(isNull(document.forms[0].policyname.value)){
		alert('Diameter Policy Group Name must be specified');
		return false;
	}

	if(!isValidName) {
		alert('Enter Valid Diameter Policy Group Name');
		document.forms[0].policyname.focus();
		return false;
	}
	
	if(!validateName(document.forms[0].policyname.value)){
		alert('Diameter Policy Group Name should have following characters. A-Z, a-z, 0-9, _ and - ');
		return false;
	}
	
	document.forms[0].submit();
}
function validateName(val)
{
	var test1 = /(^[A-Za-z0-9-_]*$)/;
	var regexp =new RegExp(test1);
	if(regexp.test(val)){
		return true;
	}//04/19/2011
	return false;
}

function verifyName() {
	var searchName = document.getElementById("policyname").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DIAMETER_POLICY_GROUP%>',searchName,'create','','verifyNameDiv');
}

setTitle('<bean:message bundle="diameterResources" key="diameterpolicy.diameterpolicygroup"/>');
</script>
<html>
<body onload="document.diameterPolicyGroupForm.policyname.focus();">

	<html:form action="/createDiameterPolicyGroup">
		<html:hidden name="diameterPolicyGroupForm" styleId="action" property="action" value="create" />
		<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
			<tr>
				<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
				<td>
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
								<table cellpadding="0" cellspacing="0" border="0" width="100%">
									<tr>
										<td class="table-header">
											<bean:message bundle="diameterResources" key="diameterpolicy.diameterpolicygroup.create" />
										</td>
									</tr>
									<tr>
										<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
									</tr>
									<tr>
										<td colspan="3">
											<table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" cellSpacing="0" cellPadding="0" border="0">
												<tr>
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="diameterResources"  key="diameterpolicy.diameterpolicygroup.name" /> 
														<ec:elitehelp headerBundle="diameterResources" text="diameterpolicy.diameterpolicygroup.name" header="diameterpolicy.diameterpolicygroup.name"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="50%">
														<html:text styleId="policyname" tabindex="1" name="diameterPolicyGroupForm" property="policyname" onblur="verifyName();" size="30" styleClass="flatfields" style="font-family: Verdana; width:250px " maxlength="30"  />
														<div id="verifyNameDiv" class="labeltext"></div>
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top" width="12%">
														<bean:message bundle="diameterResources" key="diameterpolicy.diameterpolicygroup.expression" />
														<ec:elitehelp headerBundle="diameterResources" text="diameterpolicy.diameterpolicygroup.expression" header="diameterpolicy.diameterpolicygroup.expression"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="2">
														<html:text styleId="expression" tabindex="1" name="diameterPolicyGroupForm" property="expression"  maxlength="100" style="width:250px;" />
													</td>
												</tr>
												<tr>
													<td colspan="3">&nbsp;</td>
												</tr>
												<tr>
													<td>&nbsp;</td>
													<td align="left" valign="middle" colspan="3">&nbsp;&nbsp;
														<input type="button" tabindex="11" name="c_btnCreate" onclick="customValidate();" value="   Create   " class="light-btn">&nbsp;&nbsp; 
														<input type="reset" tabindex="12" name="c_btnDeletePolicy" onclick="javascript:location.href='searchDiameterPolicyGroup.do'" value=" Cancel " class="light-btn">
													</td>
												</tr>
												<tr>
													<td colspan="3">&nbsp;</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
					</table>
				</td>
			</tr>
		</table>
	</html:form>
</body>
</html>
