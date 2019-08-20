<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.Collections"%>
<%@page
	import="com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page
	import="com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData"%>
<%@page
	import="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.UpdateRadiusPolicyCheckItemForm"%>
<%@ page
	import="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.AddRadiusPolicyForm"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>
<%@ page
	import="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.UpdateRadiusPolicyCheckItemForm"%>
<% 
	
	String checkItem = (String)request.getAttribute("checkItem");
	if(checkItem==null){
		checkItem="";
	}
	UpdateRadiusPolicyCheckItemForm updateRadiusPolicyCheckItemForm = (UpdateRadiusPolicyCheckItemForm)request.getSession().getAttribute("updateRadiusPolicyCheckItemForm");
%>

<script type="text/javascript">
function submitSave()
{
		document.forms[0].action.value = 'Update';
		document.forms[0].submit();
}
function ReloadSubmit()
{
if(!isNull(document.forms[0].strAttributeId.value)){
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

function popup(mylink, windowname)
{

	if (! window.focus)return true;
		var href;
	if (typeof(mylink) == 'string')
		href=mylink;
	else
		href=mylink.href;
					
	window.open(href, windowname, 'width=700,height=500,left=150,top=100,scrollbars=yes');
	
	return false;
}       
function submitAdd()
{
	if(!isNull(document.forms[0].strAttributeId.value)){
		if(document.forms[0].operator.value != ''){
			if(!isNull(document.forms[0].parameterValue.value)){
				var select = document.getElementById('parameterValue');
				 if(select.type == 'textarea')
				 {
				 		select.value = trimText(select.value);
				 		if(!validateParamValue(select.value)){
				 			alert('Put parameter value in the double quotes("), \nif value contains characters \\n, \\r, space, :, (, ), [, ], ||, && and comma(,).');
				 			return;
				 		}
				 }
				 else
				 {
					 if(select.selectedIndex ==0)
				 	{
				 		alert('Please Select Parameter Value ');	
				 		return;
				 	}
			     	
				 }
				 document.forms[0].checkItem.value = document.forms[0].checkItem.value
					+document.forms[0].strAttributeId.value+  document.forms[0].operator.value + document.forms[0].parameterValue.value;
			}else{
				alert('Parameter Value cannot be empty');			
			}
		}else{
			alert('Please Select Operator');
		}
	}else{
		alert('Please enter Attribute Id');
	}
}
function appendChar(ch)
{
	 var exprtxtarea =  document.getElementById("checkItem");
	 //exprtxtarea.value = exprtxtarea.value + ch;
	  insertAtCursor(exprtxtarea,ch);
}
//Insert the value of the val at the cursor location of the textarea.
function insertAtCursor(exprarea, val)
{
 	
 	if(document.selection)
 	{
 		//IE 
 		exprarea.focus();
 		sel = document.selection.createRange();
 		sel.text=val;
 	}
 	else if( exprarea.selectionStart || exprarea.selectionStart == '0')
 	{
 		//Mozilla Firefox & Netscap Navigator 
 		var startPos = exprarea.selectionStart;
 		var endPos = exprarea.selectionEnd;
 		exprarea.value = exprarea.value.substring(0,startPos) + val + exprarea.value.substring(endPos,exprarea.value.length);
 	}else
 	{
 		exprarea.value = exprarea.value + val;
 	}
 }

function validateParamValue(paramValue)
{

	var str =  '[#]#:#||#&&# #,#(#)#\\n#\\r';
	var charArray = new Array();
	charArray = str.split('#');
	if(!isNull(paramValue))
	{
		if(paramValue.charAt(0)!='"' || paramValue.charAt(paramValue.length-1)!='"')
		{
			for(key in charArray)
			{
				if(paramValue.indexOf(charArray[key])>-1){
					return false;
				}
			}
		}
	}
	return true;
}
function putCursorAtEnd(exprarea){
	exprarea.focus();
	//sets the cursor position at the end.
	exprarea.value=exprarea.value;
	if(exprarea.value.length==0){
		document.getElementById("parameterValue").focus();
	}
}	

function setColumnsOnParameterNameTextFields(){
	var multipleUserIdVal = document.getElementById("strAttributeId").value;
	retriveRadiusDictionaryAttributes(multipleUserIdVal,"strAttributeId");
}
</script>
<table cellSpacing="0" cellPadding="0" width="100%" border="0">
	<html:form action="/updateRadiusPolicyCheckItem">

		<html:hidden name="updateRadiusPolicyCheckItemForm" styleId="action"
			property="action" />
		<html:hidden name="updateRadiusPolicyCheckItemForm"
			styleId="radiusPolicyId" property="radiusPolicyId" />
		<tr>
			<td class="tblheader-bold" colspan="3"><bean:message
					bundle="radiusResources" key="radiuspolicy.updatecheckitems" /></td>
		</tr>
		<tr>
			<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="3">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="3">
				<table width="100%" name="c_tblCrossProductList"
					id="c_tblCrossProductList" align="right" border="0" cellpadding="0"
					cellspacing="0">
					<tr>
						<td align="left" class="captiontext" valign="top" width="20%">
							<bean:message bundle="radiusResources"
								key="radiuspolicy.checkitems.parametername" /> <img
							src="<%=basePath%>/images/help-hover.jpg" height="12" width="12"
							style="cursor: pointer"
							onclick="parameterDescription('<bean:message bundle="descriptionResources" key="checkitem.id"/>','<bean:message bundle="radiusResources" key="radiuspolicy.checkitems.parametername" />')" />
						</td>
						<td align="left" class="labeltext" valign="top" width="35%">
							<input type="text" name="strAttributeId" id="strAttributeId"
							size="30" autocomplete="off"
							value='<%=(updateRadiusPolicyCheckItemForm.getStrAttributeId() != null) ? updateRadiusPolicyCheckItemForm.getStrAttributeId() : "" %>'
							onkeyup="setColumnsOnParameterNameTextFields();"
							onblur="ReloadSubmit();" style="width: 250px" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="20%">
							<bean:message bundle="radiusResources"
								key="radiuspolicy.checkitems.operator" /> <img
							src="<%=basePath%>/images/help-hover.jpg" height="12" width="12"
							style="cursor: pointer"
							onclick="parameterDescription('<bean:message bundle="descriptionResources" key="checkitem.operator"/>','<bean:message bundle="radiusResources" key="radiuspolicy.checkitems.operator" />')" />
						</td>
						<td align="left" class="labeltext" valign="top" width="200px">
							<html:select name="updateRadiusPolicyCheckItemForm"
								styleId="operator" property="operator" size="1"
								style="width:130px">
								<html:option value="=">Equals</html:option>
								<html:option value="!=">Not Equal</html:option>
							</html:select>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="20%">
							<bean:message bundle="radiusResources"
								key="radiuspolicy.checkitems.parametervalue" /> <img
							src="<%=basePath%>/images/help-hover.jpg" height="12" width="12"
							style="cursor: pointer"
							onclick="parameterDescription('<bean:message bundle="descriptionResources" key="checkitem.value"/>','<bean:message bundle="radiusResources" key="radiuspolicy.checkitems.parametervalue" />')" />
						</td>
						<td align="left" class="labeltext" valign="top" height="60">
							<%
									Map preDefinedValueMap= (HashMap) request.getAttribute("preDefinedValueMap");
									if(preDefinedValueMap != null && !preDefinedValueMap.isEmpty())
									{
									 %> <html:select name="updateRadiusPolicyCheckItemForm"
								styleId="parameterValue" property="parameterValue" size="1"
								style="width:130px">
								<html:option value="0">
									<bean:message bundle="radiusResources"
										key="radiuspolicy.select.parameter" />
								</html:option>
								<html:options collection="preDefinedValueMap" property="value"
									labelProperty="key" />;
										</html:select> <%} else { %> <html:textarea
								name="updateRadiusPolicyCheckItemForm" styleId="parameterValue"
								property="parameterValue" rows="3" cols="25" style="width:250px" />
							<%}%>
						</td>
						<td id="notes" class="textbold" valign="top" width="35%">
							Note:- Put the value in double quotes(") if contains the special
							characters [ \n, \r, space ].</td>
					</tr>
					<tr>
						<td colspan="1">&nbsp;</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
							<input type="button" name="exp" id="exp" value=" Add Attribute "
							onclick="submitAdd()" class="light-btn" />
						</td>
					</tr>
					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top">&nbsp;</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<input type="button" name="and" id="and" value=" AND "
							onclick="appendChar('&&')" class="light-btn" /> <input
							type="button" name="or" id="or" value=" OR "
							onclick="appendChar('||')" class="light-btn" /> <input
							type="button" name="opar" id="opar" value="    (    "
							onclick="appendChar('(')" class="light-btn" /> <input
							type="button" name="cpar" id="cpar" value="    )    "
							onclick="appendChar(')')" class="light-btn" /> <input
							type="button" name="dquote" id="dquote" value="   &quot;   "
							onclick="appendChar('&quot;')" class="light-btn" /> <input
							type="button" name="obracket" id="obracket" value="   [   "
							onclick="appendChar('[')" class="light-btn" /> <input
							type="button" name="cbracket" id="cbracket" value="   ]   "
							onclick="appendChar(']')" class="light-btn" /> <input
							type="button" name="star" id="star" value="   *   "
							onclick="appendChar('*')" class="light-btn" /> <input
							type="button" name="qmark" id="qmark" value="   ?   "
							onclick="appendChar('?')" class="light-btn" /> <input
							type="button" name="comma" id="comma" value="   ,   "
							onclick="appendChar(',')" class="light-btn" />
						</td>

					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top"><bean:message
								bundle="radiusResources"
								key="radiuspolicy.checkitems.expression" /></td>
						<td colspan="2"><html:textarea property="checkItem"
								styleId="checkItem" cols="84" rows="6"></html:textarea></td>
					</tr>

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
	</html:form>
</table>
<script type="text/javascript">
var exprcomp = document.getElementById("checkItem");
putCursorAtEnd(exprcomp);
</script>