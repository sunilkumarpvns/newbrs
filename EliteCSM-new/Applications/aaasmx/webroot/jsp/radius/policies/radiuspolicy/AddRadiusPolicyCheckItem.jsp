<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page
	import="com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData"%>
<%@page import="java.util.Iterator"%>
<%@page
	import="com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page
	import="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.AddRadiusPolicyForm"%>
<%@ page
	import="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.AddRadiusPolicyCheckItemForm"%>
<%@page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<script type="text/javascript">
function submitNext()
{
	if(validate())
	{
 		document.forms[0].checkAction.value = 'Next';
		document.forms[0].submit();
	}
}

function validate()
{
		if(isNull(document.forms[0].checkItem.value)){
			alert('Please Enter the Check Item Expression.');
			return false;
		}
		return true;
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
				 document.forms[0].strAttributeId.value ='';
				 document.forms[0].parameterValue.value ='';
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

function deleteSubmit(index)
{
	document.forms[0].checkAction.value = 'Remove';
	document.forms[0].itemIndex.value = index;
	document.forms[0].submit();
}

function ReloadSubmit()
{

	
	if(!isNull(document.forms[0].strAttributeId.value)){
        
		
		document.forms[0].checkAction.value = 'Reload';
		document.forms[0].submit();
		return true;
	}
	
}
function popup(mylink, windowname)
{

	if (! window.focus)return true;
		var href;
	if (typeof(mylink) == 'string')
					href=mylink;
	else
					href=mylink.href;
					
	//alert(mylink)
	window.open(href, windowname, 'width=700,height=500,left=150,top=100,scrollbars=yes');
	
	return false;
}       


function appendChar(ch)
{
	
	 var exprtxtarea =  document.getElementById("checkItem");
	 insertAtCursor(exprtxtarea,ch);
	 
}
//Insert the value of the val at the cursor location of the textarea if cursor.
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
setTitle('<bean:message bundle="radiusResources" key="radiuspolicy.radiuspolicy"/>');
</script>

<%
	String basePath = request.getContextPath();
	AddRadiusPolicyForm addRadiusPolicyForm = (AddRadiusPolicyForm)request.getSession().getAttribute("addRadiusPolicyForm");
	AddRadiusPolicyCheckItemForm addRadiusPolicyCheckItemForm = (AddRadiusPolicyCheckItemForm)request.getAttribute("addRadiusPolicyCheckItemForm");

	if(addRadiusPolicyForm!=null){	
%>

<html:form action="/addRadiusPolicyCheckItem">
	<html:hidden name="addRadiusPolicyCheckItemForm" styleId="checkAction"
		property="checkAction" />
	<html:hidden name="addRadiusPolicyCheckItemForm" styleId="itemIndex"
		property="itemIndex" />
	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header"><bean:message
											bundle="radiusResources" key="radiuspolicy.checkitems" /></td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td colspan="3">
										<table width="100%" name="c_tblCrossProductList"
											id="c_tblCrossProductList" align="right" border="0"
											cellpadding="0" cellspacing="0">
											<tr>
												<td align="left" class="labeltext" valign="top" width="20%">
													<bean:message bundle="radiusResources"
														key="radiuspolicy.checkitems.parametername" /> <img
													src="<%=basePath%>/images/help-hover.jpg" height="12"
													width="12" style="cursor: pointer"
													onclick="parameterDescription('<bean:message bundle="descriptionResources" key="checkitem.id"/>','<bean:message bundle="radiusResources" key="radiuspolicy.checkitems.parametername" />')" />

												</td>
												<td align="left" class="labeltext" valign="top" width="35%">
													<input type="text" name="strAttributeId"
													id="strAttributeId" size="30" autocomplete="off"
													value='<%=(addRadiusPolicyCheckItemForm.getStrAttributeId() != null) ? addRadiusPolicyCheckItemForm.getStrAttributeId() : "" %>'
													onkeyup="setColumnsOnParameterNameTextFields();"
													onblur="return ReloadSubmit();" style="width: 250px" />

												</td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top"><bean:message
														bundle="radiusResources"
														key="radiuspolicy.checkitems.operator" /> <img
													src="<%=basePath%>/images/help-hover.jpg" height="12"
													width="12" style="cursor: pointer"
													onclick="parameterDescription('<bean:message bundle="descriptionResources" key="checkitem.operator"/>','<bean:message bundle="radiusResources" key="radiuspolicy.checkitems.operator" />')" />

												</td>
												<td align="left" class="labeltext" valign="top"><html:select
														name="addRadiusPolicyCheckItemForm" styleId="operator"
														property="operator" size="1" style="width:130px">
														<html:option value="=">Equals</html:option>
														<html:option value="!=">Not Equal</html:option>
													</html:select></td>
												<td>&nbsp;</td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top"><bean:message
														bundle="radiusResources"
														key="radiuspolicy.checkitems.parametervalue" /> <img
													src="<%=basePath%>/images/help-hover.jpg" height="12"
													width="12" style="cursor: pointer"
													onclick="parameterDescription('<bean:message bundle="descriptionResources" key="checkitem.value"/>','<bean:message bundle="radiusResources" key="radiuspolicy.checkitems.parametervalue" />')" />
												</td>
												<td align="left" class="labeltext" valign="top" height="60"
													style="vertical-align: top;">
													<%
									Map preDefinedValueMap= (HashMap) request.getAttribute("preDefinedValueMap");
									if(preDefinedValueMap != null && !preDefinedValueMap.isEmpty())
									{
									 %> <html:select name="addRadiusPolicyCheckItemForm"
														styleId="parameterValue" property="parameterValue"
														size="1" styleClass="labeltext" style="width:130px">
														<html:option value="0">
															<bean:message bundle="radiusResources"
																key="radiuspolicy.select.parameter" />
															<img src="<%=basePath%>/images/help-hover.jpg"
																height="12" width="12" style="cursor: pointer"
																onclick="parameterDescription('<bean:message bundle="descriptionResources" key="checkitem.value"/>','<bean:message bundle="radiusResources" key="radiuspolicy.select.parameter"  />')" />

														</html:option>
														<html:options collection="preDefinedValueMap"
															property="value" labelProperty="key" />;
										</html:select> <%} else { %> <html:textarea
														name="addRadiusPolicyCheckItemForm"
														styleId="parameterValue" property="parameterValue"
														rows="3" cols="25" style="width:250px" /> <%} %>
												</td>
												<td id="notes" class="textbold" valign="top">Note:- Put
													the value in double quotes(") if contains the special
													characters [ \n, \r, space ].</td>
											</tr>
											<tr>
												<td colspan="3">&nbsp;</td>
											</tr>
											<tr>
												<td colspan="1">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<input type="button" tabindex="4" name="exp" id="exp"
													value=" Add Attribute " onclick="submitAdd()"
													class="light-btn" />
												</td>
											</tr>
											<tr>
												<td colspan="3">&nbsp;</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<input type="button" name="and" id="and" value=" AND "
													onclick="appendChar('&&')" class="light-btn" /> <input
													type="button" name="or" id="or" value=" OR "
													onclick="appendChar('||')" class="light-btn" /> <input
													type="button" name="opar" id="opar" value="    (    "
													onclick="appendChar('(')" class="light-btn" /> <input
													type="button" name="cpar" id="cpar" value="    )    "
													onclick="appendChar(')')" class="light-btn" /> <input
													type="button" name="dquote" id="dquote"
													value="   &quot;   " onclick="appendChar('&quot;')"
													class="light-btn" /> <input type="button" name="obracket"
													id="obracket" value="   [   " onclick="appendChar('[')"
													class="light-btn" /> <input type="button" name="cbracket"
													id="cbracket" value="   ]   " onclick="appendChar(']')"
													class="light-btn" /> <input type="button" name="star"
													id="star" value="   *   " onclick="appendChar('*')"
													class="light-btn" /> <input type="button" name="qmark"
													id="qmark" value="   ?   " onclick="appendChar('?')"
													class="light-btn" /> <input type="button" name="comma"
													id="comma" value="   ,   " onclick="appendChar(',')"
													class="light-btn" />
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top"><bean:message
														bundle="radiusResources"
														key="radiuspolicy.checkitems.expression" /></td>

												<td valign="top" colspan="2"><html:textarea
														property="checkItem" styleId="checkItem" cols="84"
														rows="6"></html:textarea></td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td colspan="3">
										<table width="97%" name="inTable" id="inTable" align="left"
											border="0" cellpadding="0" cellspacing="0">
											<tr>
												<td class="btns-td" valign="middle"><input
													type="button" value="Previous" onclick="history.go(-1)"
													class="light-btn" />&nbsp;&nbsp; <input type="button"
													name="c_btnNext" onclick="return submitNext();"
													value="Next" class="light-btn">&nbsp; &nbsp; <input
													type="reset" name="c_btnDeletePolicy" value="Cancel"
													onclick="javascript:location.href='initSearchRadiusPolicy.do'"
													class="light-btn"></td>
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
<%}%>
<script type="text/javascript">
var exprcomp = document.getElementById("checkItem");
putCursorAtEnd(exprcomp);
</script>