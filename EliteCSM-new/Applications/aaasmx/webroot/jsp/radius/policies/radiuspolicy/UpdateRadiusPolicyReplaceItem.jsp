<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page
	import="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.AddRadiusPolicyForm"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>


<script type="text/javascript">
function submitSave()
{
	document.forms[0].action.value = 'Update';
	document.forms[0].submit();
}
function submitAdd()
{
	if(document.forms[0].parameterId.value != '0'){
		if(document.forms[0].operatorId.value != '0'){
			if(!isNull(document.forms[0].parameterValue.value)){
				document.forms[0].action.value = 'Add';
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
function ReloadSubmit()
{
if(document.forms[0].parameterId.value != '0'){
	document.forms[0].action.value = 'Reload';
	document.forms[0].submit();
	}
}

function deleteSubmit(index)
{
	document.forms[0].action.value = 'Remove';
	document.forms[0].itemIndex.value = index;
	document.forms[0].submit();
}

</script>

<table cellSpacing="0" cellPadding="0" width="97%" border="0">
	<html:form action="/updateRadiusPolicyReplaceItem">

		<html:hidden name="updateRadiusPolicyReplaceItemForm" styleId="action"
			property="action" />
		<html:hidden name="updateRadiusPolicyReplaceItemForm"
			styleId="itemIndex" property="itemIndex" />
		<html:hidden name="updateRadiusPolicyReplaceItemForm"
			styleId="radiusPolicyId" property="radiusPolicyId" />
		<tr>
			<td class="tblheader-bold" colspan="3"><bean:message
					bundle="radiusResources" key="radiuspolicy.updatereplaceitems" /></td>
		</tr>
		<tr>
			<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="3">
				<table width="100%" name="c_tblCrossProductList"
					id="c_tblCrossProductList" align="right" border="0">
					<tr>
						<td align="left" class="captiontext" valign="top" width="20%"><bean:message
								bundle="radiusResources"
								key="radiuspolicy.replaceitems.parametername" /></td>
						<td align="left" class="labeltext" valign="top" width="40%">
							<html:select name="updateRadiusPolicyReplaceItemForm"
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
								name="updateRadiusPolicyReplaceItemForm" styleId="status"
								property="status" value="Y" />&nbsp;<bean:message
								bundle="radiusResources" key="radiuspolicy.replaceitems.active" /></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top"><bean:message
								bundle="radiusResources"
								key="radiuspolicy.replaceitems.operator" /></td>
						<td align="left" class="labeltext" valign="top"><html:select
								name="updateRadiusPolicyReplaceItemForm" styleId="operatorId"
								property="operatorId" size="1">
								<html:option value="0">
									<bean:message bundle="radiusResources"
										key="radiuspolicy.select.operator" />
								</html:option>
								<html:options collection="dictionaryOperators"
									property="operatorId" labelProperty="operatorName" />
							</html:select></td>
						<td width="30%">&nbsp;</td>
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
									 %> <html:textarea name="updateRadiusPolicyReplaceItemForm"
								styleId="parameterValue" property="parameterValue" rows="3" />
							<%} else { %> <html:select name="updateRadiusPolicyReplaceItemForm"
								styleId="parameterValue" property="parameterValue" size="1">
								<html:option value="0">
									<bean:message bundle="radiusResources"
										key="radiuspolicy.select.parameter" />
								</html:option>
								<html:options collection="preDefinedValueMap" property="value"
									labelProperty="key" />;
										</html:select> <%} %>
						</td>
						<td width="30%">&nbsp;</td>
					</tr>

					<tr>
						<td align="left" class="labeltext" valign="top">&nbsp;</td>
						<td align="left" class="labeltext" valign="top"><input
							type="button" value="    Add    " property="ADD"
							onclick="submitAdd()" class="light-btn" /></td>
						<td width="30%">&nbsp;</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="3">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="3">
				<table width="100%" align="center" border="0" cellpadding="0"
					cellspacing="0">
					<%
							int index = 0;
//							System.out.println("Size : "+((List)request.getSession().getAttribute("replaceItemsList")).size());
						%>
					<tr>
						<td align="left" class="tblheader" valign="top" width="5%"><bean:message
								key="general.serialnumber" /></td>
						<td align="left" class="tblheader" valign="top" width="30%"><bean:message
								bundle="radiusResources"
								key="radiuspolicy.replaceitems.parametername" /></td>
						<td align="left" class="tblheader" valign="top" width="25%"><bean:message
								bundle="radiusResources"
								key="radiuspolicy.replaceitems.operator" /></td>
						<td align="left" class="tblheader" valign="top" width="25%"><bean:message
								bundle="radiusResources"
								key="radiuspolicy.replaceitems.parametervalue" /></td>
						<td align="left" class="tblheader" valign="top" width="8%"><bean:message
								bundle="radiusResources" key="radiuspolicy.replaceitems.status" /></td>
						<td align="left" class="tblheader" valign="top" width="7%"><bean:message
								bundle="radiusResources" key="radiuspolicy.replaceitems.remove" /></td>
					</tr>

					<logic:iterate id="replaceItemBean" name="replaceItemsList"
						type="com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyParamDetailData"
						scope="session">
						<bean:define id="opeartorBean" name="replaceItemBean"
							property="radiusOperator"
							type="com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IOperatorData" />
						<tr>
							<td align="left" class="tblfirstcol"><%=(index+1)%></td>
							<td align="left" class="tblrows"><bean:write
									name="replaceItemBean" property="dictionaryParameterName" /></td>
							<td align="left" class="tblrows"><bean:write
									name="opeartorBean" property="operatorName" /></td>
							<td align="left" class="tblrows"><bean:write
									name="replaceItemBean" property="displayValue" /></td>

							<logic:equal name="replaceItemBean" property="status" value="Y">
								<td align="center" class="tblrows"><img
									src="<%=basePath%>/images/active.jpg" /></td>
							</logic:equal>
							<logic:notEqual name="replaceItemBean" property="status"
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
			<td colspan="3" height="20%" class="labeltext">&nbsp;</td>
		</tr>

		<tr>
			<td colspan="3" align="center">
				<table width="90%" name="inTable" id="inTable" align="center"
					border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td class="btns-td" valign="middle" align="center"><input
							type="button" name="c_btnSave" onclick="return submitSave();"
							value="Save" class="light-btn">&nbsp; &nbsp; <input
							type="reset" name="c_btnDeletePolicy"
							onclick="javascript:location.href='<%=basePath%>/viewRadiusPolicy.do?radiusPolicyId=<bean:write name="radiusPolicyBean" property="radiusPolicyId"/>'"
							value=" Cancel " class="light-btn"></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="3" height="20%" class="labeltext">&nbsp;</td>
		</tr>
		<html:hidden name="updateRadiusPolicyReplaceItemForm"
			styleId="parameterDisplayValue" property="parameterDisplayValue" />
	</html:form>
</table>
