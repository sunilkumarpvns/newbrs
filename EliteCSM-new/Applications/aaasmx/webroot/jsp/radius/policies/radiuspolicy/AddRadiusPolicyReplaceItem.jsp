<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>
<%@ page
	import="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.AddRadiusPolicyForm"%>


<script type="text/javascript">
function submitNext()
{
	document.forms[0].replaceAction.value = 'Next';
	document.forms[0].submit();
}
function submitPrevious()
{
	document.forms[0].replaceAction.value = 'Previous';
	document.forms[0].submit();
}



function submitAdd()
{
	if(document.forms[0].parameterId.value != '0'){
		if(document.forms[0].operatorId.value != '0'){
			if(!isNull(document.forms[0].parameterValue.value)){
				document.forms[0].replaceAction.value = 'Add';
				
				var select = document.getElementById('parameterValue');
				 if(select.type == 'textarea')
				 {
				 	document.forms[0].parameterDisplayValue.value = select.value;
				 }
				 else
				 {
					 if(select.selectedIndex ==0)
				 	{
				 		alert('Please Select Parameter Value ');	
				 		return;
				 	}
			     	var selectText = select.options[select.selectedIndex].text
 				 	document.forms[0].parameterDisplayValue.value = selectText;
				 }
				
				
				document.forms[0].submit();
			}else{
				alert('Parameter Value cannot be empty');			
			}
		}else{
			alert('Please Select Operator');
		}
	}else{
		alert('Please Select Parameter');
	}
}


function deleteSubmit(index)
{
	document.forms[0].replaceAction.value = 'Remove';
	document.forms[0].itemIndex.value = index;
	document.forms[0].submit();
}

function ReloadSubmit()
{
if(document.forms[0].parameterId.value != '0'){
	document.forms[0].replaceAction.value = 'Reload';
	document.forms[0].submit();
 }
}
setTitle('<bean:message bundle="radiusResources" key="radiuspolicy.radiuspolicy"/>');
</script>

<%
    String basePath = request.getContextPath();
%>

<% /* --- Start of Page Header --- only edit module name*/ %>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td width="7">&nbsp;</td>
		<td width="821" colspan="2">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td width="7">&nbsp;</td>
					<td width="100%" colspan="2">
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td class="small-gap" width="7">&nbsp;</td>
	</tr>

	<% /* --- End of Page Header --- 
      --- Module specific code starts from below.*/ %>

	<%
	AddRadiusPolicyForm addRadiusPolicyForm = (AddRadiusPolicyForm)request.getSession().getAttribute("addRadiusPolicyForm");
	%>


	<tr>
		<td width="10">&nbsp;</td>
		<td width="100%" colspan="2" valign="top" class="box">
			<table cellSpacing="0" cellPadding="0" width="100%" border="0">
				<html:form action="/addRadiusPolicyReplaceItem">

					<html:hidden name="addRadiusPolicyReplaceItemForm"
						styleId="replaceAction" property="replaceAction" />
					<html:hidden name="addRadiusPolicyReplaceItemForm"
						styleId="itemIndex" property="itemIndex" />
					<tr>
						<td class="table-header" colspan="3"><bean:message
								bundle="radiusResources" key="radiuspolicy.replaceitems" /></td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="3">
							<table width="97%" name="c_tblCrossProductList"
								id="c_tblCrossProductList" align="right" border="0">
								<tr>
									<td align="left" class="captiontext" valign="top" width="20%"><bean:message
											bundle="radiusResources"
											key="radiuspolicy.replaceitems.parametername" /></td>
									<td align="left" class="labeltext" valign="top" width="40%">
										<html:select name="addRadiusPolicyReplaceItemForm"
											styleId="parameterId" property="parameterId" size="1"
											onchange="ReloadSubmit()">
											<html:option value="0">
												<bean:message bundle="radiusResources"
													key="radiuspolicy.select.parameter" />
											</html:option>
											<html:options collection="dictionaryParameters"
												property="dictionaryParameterDetailId" labelProperty="name" />
										</html:select>
									</td>
									<td width="30%" class="labeltext" align="left"><html:checkbox
											name="addRadiusPolicyReplaceItemForm" styleId="status"
											property="status" value="Y" />&nbsp;<bean:message
											bundle="radiusResources"
											key="radiuspolicy.replaceitems.active" /></td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top"><bean:message
											bundle="radiusResources"
											key="radiuspolicy.replaceitems.operator" /></td>
									<td align="left" class="labeltext" valign="top"><html:select
											name="addRadiusPolicyReplaceItemForm" styleId="operatorId"
											property="operatorId" size="1" onchange="setDisplayValue()">
											<html:option value="0">
												<bean:message bundle="radiusResources"
													key="radiuspolicy.select.operator" />
											</html:option>
											<html:options collection="dictionaryOperators"
												property="operatorId" labelProperty="operatorName" />
										</html:select></td>
									<td width="10%">&nbsp;</td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top"><bean:message
											bundle="radiusResources"
											key="radiuspolicy.replaceitems.parametervalue" /></td>
									<td align="left" class="labeltext" valign="top" height="60">
										<%
									Map preDefinedValueMap= (HashMap) request.getAttribute("preDefinedValueMap");
									if(preDefinedValueMap != null && preDefinedValueMap.size() == 0 )
									{
									 %> <html:textarea name="addRadiusPolicyReplaceItemForm"
											styleId="parameterValue" property="parameterValue" rows="3" />
										<%} else { %> <html:select name="addRadiusPolicyReplaceItemForm"
											styleId="parameterValue" property="parameterValue" size="1">
											<html:option value="0">
												<bean:message bundle="radiusResources"
													key="radiuspolicy.select.parameter" />
											</html:option>
											<html:options collection="preDefinedValueMap"
												property="value" labelProperty="key" />;
										</html:select> <%} %>
									</td>
									<td width="10%">&nbsp;</td>
								</tr>

								<tr>
									<td align="left" class="labeltext" valign="top">&nbsp;</td>
									<td align="left" class="labeltext" valign="top"><input
										type="button" value="    Add    " property="ADD"
										onclick="submitAdd()" class="light-btn" /></td>
									<td width="10%">&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td class="table-header"><bean:message
								bundle="radiusResources" key="radiuspolicy.selectedreplaceitems" /></td>
					</tr>
					<tr>
						<td colspan="3">
							<table width="95%" align="center">
								<%
							int index = 0;
						%>
								<tr>
									<td align="left" class="tblheader" valign="top" width="5%"><bean:message
											key="general.serialnumber" /></td>
									<td align="left" class="tblheader" valign="top" width="20%"><bean:message
											bundle="radiusResources"
											key="radiuspolicy.replaceitems.parametername" /></td>
									<td align="left" class="tblheader" valign="top" width="20%"><bean:message
											bundle="radiusResources"
											key="radiuspolicy.replaceitems.operator" /></td>
									<td align="left" class="tblheader" valign="top" width="20%"><bean:message
											bundle="radiusResources"
											key="radiuspolicy.replaceitems.parametervalue" /></td>
									<td align="left" class="tblheader" valign="top" width="20%"><bean:message
											bundle="radiusResources"
											key="radiuspolicy.replaceitems.status" /></td>
									<td align="left" class="tblheader" valign="top" width="10%"><bean:message
											bundle="radiusResources"
											key="radiuspolicy.replaceitems.remove" /></td>
								</tr>

								<logic:iterate id="replaceItems" name="addRadiusPolicyForm"
									property="replaceItems"
									type="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.AddRadiusPolicyReplaceItemForm"
									scope="session">
									<tr>
										<td align="left" class="tblfirstcol"><%=(index+1)%></td>
										<td align="left" class="tblrows"><bean:write
												name="replaceItems" property="parameterName" /></td>
										<td align="left" class="tblrows"><bean:write
												name="replaceItems" property="operatorName" /></td>
										<td align="left" class="tblrows"><bean:write
												name="replaceItems" property="parameterDisplayValue" /></td>

										<logic:equal name="replaceItems" property="status" value="Y">
											<td align="center" class="tblrows"><img
												src="<%=basePath%>/images/active.jpg" /></td>
										</logic:equal>
										<logic:notEqual name="replaceItems" property="status"
											value="Y">
											<td align="center" class="tblrows"><img
												src="<%=basePath%>/images/deactive.jpg" /></td>
										</logic:notEqual>
										<td align="center" class="tblrows"><img
											src="<%=basePath%>/images/minus.jpg"
											onclick="deleteSubmit('<%=index%>')" border="0" /></td>
									</tr>
									<% index++; %>
								</logic:iterate>
							</table>
						</td>
					</tr>

					<tr>
						<td colspan="3">
							<table width="97%" name="inTable" id="inTable" align="left"
								border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td class="btns-td" valign="middle"><input type="button"
										value="Previous" onclick="submitPrevious()" class="light-btn" />&nbsp;&nbsp;
										<input type="button" name="c_btnNext"
										onclick="return submitNext();" value="Next" class="light-btn">&nbsp;
										&nbsp; <input type="reset" name="c_btnDeletePolicy"
										value="Cancel" class="light-btn"></td>
								</tr>
							</table>
						</td>
					</tr>
					<html:hidden name="addRadiusPolicyReplaceItemForm"
						styleId="parameterDisplayValue" property="parameterDisplayValue"
						value="" />
				</html:form>
			</table>
		</td>
	</tr>

	<tr>
		<td class="small-gap" colspan="3">&nbsp;</td>
	</tr>
</table>
<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
