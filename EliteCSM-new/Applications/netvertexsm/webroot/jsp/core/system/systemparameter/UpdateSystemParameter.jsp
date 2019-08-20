<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData"%>
<%@ page import="com.elitecore.core.commons.util.constants.BaseConstants"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.BaseConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.web.core.system.systemparameter.forms.UpdateSystemParameterForm" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.SystemParameterData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.ISystemParameterData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.SystemParameterValuePoolData" %>
<%@ page import="com.elitecore.commons.base.Strings" %>

<%
	UpdateSystemParameterForm updateSystemParameterForm = (UpdateSystemParameterForm) request.getAttribute("updateSystemParameterForm");
	PasswordPolicyConfigData passwordPolicyConfigData = ((UpdateSystemParameterForm) request.getAttribute("updateSystemParameterForm")).getPasswordSelectionConfigData();	
	
	int iIndex = 0;
	int j = 0;
	int pkgAttributeIndex = 0;
	int pwdIndex =1;
	boolean flag=true;
	String pwdRange="",digitRange="",alphaRange="",specialCharRange="";	
	
  %>
	
<script>

function validateCreate(){
	var datasourceRowValue = $("[title='TOTAL_ROW']").val();
	var datasourceFieldValue = $("[title='TOTAL_FIELD']").val();
	var updateActionValue = $("[title='UPDATE_ACTION']").val();
	
	if(isNull(datasourceRowValue) || isNull(datasourceFieldValue)){
		alert("Value of Datasource Max Row or Datasource Max Field cannot be empty");
		return;
	}
	if(isNaturalNumber(datasourceRowValue) == false){
		alert("Please enter positive numeric value in DataSource Max Row");
		return;
	} 
	if(isNaturalNumber(datasourceFieldValue) == false ){
		alert("Please enter positive numeric value in DataSource Max Field");
		return;
	}
	if(datasourceRowValue.length > 8){
		alert("Value of Datasource Max Row cannot be greater than 8 digits");
		return;
	}

	
	if(updateActionValue !='undefined' && updateActionValue != undefined){
		var actionReg = new RegExp('^[0-2]');
		if(isNull(updateActionValue) || actionReg.test(updateActionValue)==false){
			alert("Valid values for 'Update Action For WS' are 0/1/2");
			return;
		}
	}
	
	var isValid=isValidPasswordSelectionPolicy();
    if(isValid == true){
    	$('#passwordRange').val($('#passRangeId').val());
		$('#aplhabetsRange').val($('#alphaRangeId').val());
		$('#digitRange').val($('#digitRangeId').val());
		$('#speCharRange').val($('#specialCharRangeId').val());
		$('#proChar').val($('#prohibitedCharId').val());
		$('#pwdValidity').val($('#passwordValidityId').val());
		$('#changePwdOnFirstLogin').val($('#changePwdOnFirstLoginId').val());
		$('#totalHistoricalPasswords').val($('#totalHistoricalPasswordsId').val());
					
    	document.forms[0].action.value = 'update';
   		document.forms[0].submit();
    }
}

function isValidPasswordSelectionPolicy(){
	var checkPasswordRange=$('#passRangeId').val();
	var checkAlphaRange=$('#alphaRangeId').val();
	var chechDigitRange=$('#digitRangeId').val();
	var chechSpecialCharRange=$('#specialCharRangeId').val();
	var chechProhibitedChar=$('#prohibitedCharId').val();
	var chechPasswordValidity=$('#passwordValidityId').val();
	var checkTotalHistoricalPasswords=$('#totalHistoricalPasswordsId').val();
	
	
	if($.trim(checkPasswordRange) == ''){
		alert('Min-Max Password length must be Provided');
		$("#passRangeId").focus();
		return false;
	}
	
	var totallength=0,maxLength=0;
	if(checkPasswordRange =='' && checkAlphaRange == '' && chechDigitRange == '' && chechSpecialCharRange == '' && chechProhibitedChar == '' && chechPasswordValidity == ''){
		alert('Password Selection Policy will not apply');
		return true;
	}else{
		
		if(checkPasswordRange != ''){
			if(!(checkPasswordRange.indexOf("-") != -1)){
				alert('Invalid min-max password Range !!');				
				$("#passRangeId").focus();
				return false;
			}else{				
				if(checkPasswordRange.lastIndexOf('-') == checkPasswordRange.length-1){
					alert('Invalid min-max password Range !!');				
					$("#passRangeId").focus();
					return false;
				}
				
				if(checkPasswordRange.lastIndexOf('-') != checkPasswordRange.indexOf('-')){
					alert('Invalid min-max password Range !!');				
					$("#passRangeId").focus();
					return false;
				}
				
				var pwdRangefields = checkPasswordRange.split(/-/);
				var minPwdRange = pwdRangefields[0];
				var maxPwdRange = pwdRangefields[1];
				
				if(minPwdRange !='' && maxPwdRange !=''){
					var checkminPwdRange=isInteger(minPwdRange);
					var checkmaxPwdRange=isInteger(maxPwdRange);
					if( checkminPwdRange == false || checkmaxPwdRange == false){
						alert('Only Positive Integer are allowed in Min-Max Password Range');
						$("#passRangeId").focus();
						return false;
					}else if(parseInt(minPwdRange) < 5 ){						
						alert('Minimum Password length must be at least Five(5)');
						$("#passRangeId").focus();
						return false;
					}else if(parseInt(minPwdRange) > parseInt(maxPwdRange)){						
						alert('Minimum Password length must be less than or equal to maximum password Length');
						$("#passRangeId").focus();
						return false;
					}else if(parseInt(minPwdRange) == 0 || parseInt(maxPwdRange) == 0){
						alert('Password Range must be greater than zero');
						$("#passRangeId").focus();
						return false;
					}else if(parseInt(minPwdRange) > 50 || parseInt(maxPwdRange) > 50){
						alert('Allowed value for Password Min & Max range is between 5-50');
						$("#passRangeId").focus();
						return false;
					} 
					
				}else{
					alert('Please check Min-Max Password Range');
					$("#passRangeId").focus();
					return false;
				}
			}
			maxLength=parseInt(minPwdRange);
		}
		
		if(checkAlphaRange != ''){
				var alphaFlag =isInteger(checkAlphaRange);
				if( alphaFlag == false){
					alert('Only Positive Integer are allowed in Required Alphabets');
					$("#alphaRangeId").focus();
					return false;
				}else if(parseInt(checkAlphaRange) >50){
					alert('Max Allowed value for Required Alphabets is 50');
					$("#alphaRangeId").focus();
					return false;
				} else if(parseInt(checkAlphaRange) > parseInt(maxPwdRange)){
					alert('Required Alphabets should not be greater than Max Password');
					$("#alphaRangeId").focus();
					return false;						
				}
				totallength=parseInt(totallength)+parseInt(checkAlphaRange);
		}
		
		if(chechDigitRange != ''){
				var digitFlag =isInteger(chechDigitRange);
				if( digitFlag == false ){
					alert('Only Positive Integer are allowed in Required Digits');
					$("#digitRangeId").focus();
					return false;
				} else if(parseInt(chechDigitRange) > 50){
					alert('Max Allowed value for Required Digits is 50');
					$("#digitRangeId").focus();
					return false;
				}else if(parseInt(chechDigitRange) > parseInt(maxPwdRange)){
					alert('Required Digits should not be greater than Max Password');
					$("#digitRangeId").focus();
					return false;						
				}
				 
				totallength=parseInt(totallength)+parseInt(chechDigitRange);
		}
		
		if(chechSpecialCharRange != ''){
			 				
					var specialCharFlag =isInteger(chechSpecialCharRange);
					if( specialCharFlag == false){
						alert('Only Positive Integer are allowed in Required Special Characters');
						$("#specialCharRangeId").focus();
						return false;
					} else if (parseInt(chechSpecialCharRange) > 50){
						alert('Max Allowed value for Required Special Characters is 50');
						$("#specialCharRangeId").focus();
						return false;
					}else if(parseInt(chechSpecialCharRange) > parseInt(maxPwdRange)){
						alert('Required Special Characters should not be greater than Max Password');
						$("#specialCharRangeId").focus();
						return false;						
					}
			 
					totallength=parseInt(totallength)+parseInt(chechSpecialCharRange);
		}
		
		if(chechProhibitedChar.length > 50){
			alert('Allowed prohibited characters are 50 only');
			$('#prohibitedCharId').focus();
			return false;
		}
		
	    if(checkPasswordRange != ''){
			if(totallength > maxLength){
				alert('Total of all Required values should not be greater than minimum password length');
				return false;
			}
		}	    
	    
	    if(chechPasswordValidity!='' && isInteger(chechPasswordValidity)==false){
	    	alert('Password Validity should be greater than or equal to zero');
	    	$("#passwordValidityId").focus();
	    	return false;
	    }
	    
	    if(chechPasswordValidity<0){
	    	alert('Password Validity should be greater than or equal to zero');
	    	$("#passwordValidityId").focus();
	    	return false;
	    }
	    
	    if(chechPasswordValidity > 999999999){
	    	alert('Invalid Password Validity. Max nine digits allowed');
	    	$("#passwordValidityId").focus();
	    	return false;
	    }
	    
	    if(checkTotalHistoricalPasswords=='' || isInteger(checkTotalHistoricalPasswords)==false){	    	
	    	alert('Historical Passwords must be positive Integer');
	    	$("#totalHistoricalPasswordsId").focus();
	    	return false;
	    }
	    
	    if(checkTotalHistoricalPasswords<1){
	    	alert('Historical Passwords must be greater than zero');
	    	$("#totalHistoricalPasswordsId").focus();
	    	return false;
	    }
	    
	    if(checkTotalHistoricalPasswords>10){
	    	alert('Allowed Historical Passwords are 1 to 10');
	    	$("#totalHistoricalPasswordsId").focus();
	    	return false;
	    }
		return true;
	}
}


$(document).ready(function(){
	setTitle('<bean:message key="systemparameter.systemparameter"/>');
	$("#changePwdOnFirstLoginId").val('<%=passwordPolicyConfigData.getChangePwdOnFirstLogin()%>');
	var param1 = $("[title='PARAM_1']").val();
	var param2 = $("[title='PARAM_2']").val();
	if(isNull(param1) == false) {
		$("[title='PARAM_1']").attr("disabled", "disabled");
	}
	if(isNull(param2) == false) {
		$("[title='PARAM_2']").attr("disabled", "disabled");
	}
});

</script>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<tr>
		<td width="10">
			&nbsp;
		</td>
		<td width="100%" colspan="2" valign="top" class="box">
			<table cellSpacing="0" cellPadding="0" width="100%" border="0">
				<html:form action="/updateSystemParameter">
					<html:hidden name="updateSystemParameterForm" styleId="action" property="action" />
					<html:hidden name="updateSystemParameterForm" styleId="passwordRange" property="passwordRange"/>
					<html:hidden name="updateSystemParameterForm" styleId="aplhabetsRange" property="aplhabetsRange"/>
					<html:hidden name="updateSystemParameterForm" styleId="digitRange" property="digitRange"/>
					<html:hidden name="updateSystemParameterForm" styleId="speCharRange" property="speCharRange"/>
					<html:hidden name="updateSystemParameterForm" styleId="proChar" property="proChar"/>
					<html:hidden name="updateSystemParameterForm" styleId="passwordPolicyId" property="passwordPolicyId" value='<%=String.valueOf(passwordPolicyConfigData.getPasswordPolicyId()) %>'/>
					<html:hidden name="updateSystemParameterForm" styleId="pwdValidity" property="pwdValidity" />
					<html:hidden name="updateSystemParameterForm" styleId="changePwdOnFirstLogin" property="changePwdOnFirstLogin" />					
					<html:hidden name="updateSystemParameterForm" styleId="totalHistoricalPasswords" property="totalHistoricalPasswords" />
					
					<tr>
						<td class="table-header" colspan="3">
							<bean:message key="systemparameter.updatesystemparameter" />
						</td>
					</tr>
					<tr>
						<td class="small-gap" colspan="3">
							&nbsp;
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle" colspan="3">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="small-gap" colspan="3">
										&nbsp;
									</td>
								</tr>
								<tr>
									<td colspan="3">
										<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0">
											<tr>
											    <td align="center" class="tblheader-bold" valign="top" width="5%">
													<bean:message key="systemparameter.serialno" />
												</td>
											    <td align="left" class="tblheader-bold" valign="top" width="20%">
													<bean:message key="systemparameter.name" />
												</td>
												<td align="left" class="tblheader-bold" valign="top" width="75%">
													<bean:message key="systemparameter.value" />
												</td>
											</tr>

											<%	System.out.println("Update Prameter JSP Form : "
												+ updateSystemParameterForm.getLstParameterValue().size());
												if (updateSystemParameterForm.getLstParameterValue() != null) {

												%>
												
											<logic:iterate id="updateParameterBean" name="updateSystemParameterForm" property="lstParameterValue" type="com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.ISystemParameterData">
											<%	
												String strParameterId = "parameterId[" + j + "]";											
												String strName = "name[" + j + "]";
												String strAlias = "alias[" + j + "]";
												String strValue = "value[" + j + "]";
												String strDescription = "description[" + j + "]";
												String strSytemGenerated = "systemGenerated[" + j + "]";
												String strSystemParameterValuePoolData = "parameterDetail["+j+"]";
        										Set systemParameterValuePoolData = updateSystemParameterForm.getSystemParameterValuePoolData(j);
        										String descriptionVal = updateSystemParameterForm.getValue(j);
        										
												%>
												
												<html:hidden name="updateSystemParameterForm" styleId="<%=strParameterId%>" 	property="<%=strParameterId%>" />
												<html:hidden name="updateSystemParameterForm" styleId="<%=strName%>" 			property="<%=strName%>" />
												<html:hidden name="updateSystemParameterForm" styleId="<%=strDescription%>" 	property="<%=strDescription%>" />
												<html:hidden name="updateSystemParameterForm" styleId="<%=strAlias%>" 			property="<%=strAlias%>" />
												<html:hidden name="updateSystemParameterForm" styleId="<%=strSytemGenerated%>" 	property="<%=strSytemGenerated%>" />
												<%if(updateSystemParameterForm.getAlias(j).equalsIgnoreCase("PARAM_1") == false && updateSystemParameterForm.getAlias(j).equalsIgnoreCase("PARAM_2") == false){ %>
												<tr>
												<td align="center"class="tblleftlinerows" width="5%">
													<%=(iIndex + 1)%>
													</td>
													<td align="left" valign="top" width="20%" class="tblleftlinerows"><b>
														<bean:write name="updateSystemParameterForm" property="<%=strName %>" /></b>
													</td>
			                                 		<td align="left"  valign="top" width="75%" class="tblrightlinerows" >
			                            				<%if(systemParameterValuePoolData!=null && systemParameterValuePoolData.size() > 0){%>
			                            		    	<html:select name="updateSystemParameterForm" styleId="<%=strValue%>" property="<%=strValue%>" size="1" >
													   <%
													 	Iterator itr = systemParameterValuePoolData.iterator();
													 	while(itr.hasNext()){
													 	SystemParameterValuePoolData parameterValuePoolData = (SystemParameterValuePoolData)itr.next();
													 	%>
													 	 <html:option value="<%=parameterValuePoolData.getValue()%>" ><%=parameterValuePoolData.getName()%></html:option>
													 	<%
														 }
														%>
			                            		   	 </html:select>
			                            			<%}else{
			                            				if(updateSystemParameterForm.getAlias(j).equalsIgnoreCase(BaseConstant.TOTAL_ROW) || updateSystemParameterForm.getAlias(j).equalsIgnoreCase(BaseConstant.TOTAL_FIELD)){ %>
			                            					<html:text name="updateSystemParameterForm" size = "50" styleId="<%=strValue%>" property="<%=strValue%>" maxlength="8" title="<%=updateSystemParameterForm.getAlias(j)%>" onkeypress="return isNaturalInteger(event);"/>
			                            				<%}else if(updateSystemParameterForm.getAlias(j).equalsIgnoreCase(BaseConstant.UPDATE_ACTION) || updateSystemParameterForm.getAlias(j).equalsIgnoreCase(BaseConstant.UPDATE_ACTION)){ %>
			                            					<html:text name="updateSystemParameterForm" size = "1" styleId="<%=strValue%>" property="<%=strValue%>" maxlength="1" title="<%=updateSystemParameterForm.getAlias(j)%>" onkeypress="return isNaturalInteger(event);"/>
			                            				<%}else{%>
				                                    		<html:text name="updateSystemParameterForm" size = "50" styleId="<%=strValue%>" property="<%=strValue%>" maxlength="255"/>
				                                    	<%}
			                            			}	%>
										            </td>
												</tr>
												<tr>
												    <td align="left" class="tblleftlinerows" valign="top" width="5%">&nbsp;</td>
												    <td align="left" class="tblleftlinerows" valign="top" width="20%">
													<bean:message key="systemparameter.description" />
												    </td>
												    <td align="left" valign="top" width="75%" class="tblrightlinerows">
														<bean:write name="updateSystemParameterForm" property="<%=strDescription %>" />&nbsp;
													</td>
												</tr>
												<%}j++;%>
												<%iIndex += 1;%>
											</logic:iterate>
											<%}%>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td  colspan="3" >&nbsp;</td>
					</tr>
					<tr>
						<td class="table-header" colspan="4">&nbsp;
						</td>
					</tr>
					<tr>
						<td class="table-header" colspan="4">
							<bean:message key="systemparameter.packageparameters" />
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle" colspan="3">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="small-gap" colspan="3">
										&nbsp;
									</td>
								</tr>
								<tr>
									<td colspan="3">
										<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0">
											<tr>
												<td align="center" class="tblheader-bold" valign="top" width="5%">
													<bean:message key="systemparameter.serialno" />
												</td>
												<td align="left" class="tblheader-bold" valign="top" width="20%">
													<bean:message key="systemparameter.name" />
												</td>
												<td align="left" class="tblheader-bold" valign="top" width="75%">
													<bean:message key="systemparameter.value" />
												</td>
											</tr>

											<%	System.out.println("Update Prameter JSP Form : "
													+ updateSystemParameterForm.getLstParameterValue().size());
												if (updateSystemParameterForm.getLstParameterValue() != null) {
													j=0;

											%>

											<logic:iterate id="updateParameterBean" name="updateSystemParameterForm" property="lstParameterValue" type="com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.ISystemParameterData">
												<%

													String strParameterId = "parameterId[" + j + "]";
													String strName = "name[" + j + "]";
													String strAlias = "alias[" + j + "]";
													String strValue = "value[" + j + "]";
													String strDescription = "description[" + j + "]";
													String strSytemGenerated = "systemGenerated[" + j + "]";
													String strSystemParameterValuePoolData = "parameterDetail["+j+"]";
													Set systemParameterValuePoolData = updateSystemParameterForm.getSystemParameterValuePoolData(j);
													String descriptionVal = updateSystemParameterForm.getValue(j);

												%>

												<%if(updateSystemParameterForm.getAlias(j).equalsIgnoreCase("PARAM_1") || updateSystemParameterForm.getAlias(j).equalsIgnoreCase("PARAM_2")){ %>
												<html:hidden name="updateSystemParameterForm" styleId="<%=strParameterId%>" 	property="<%=strParameterId%>" />
												<html:hidden name="updateSystemParameterForm" styleId="<%=strName%>" 			property="<%=strName%>" />
												<html:hidden name="updateSystemParameterForm" styleId="<%=strDescription%>" 	property="<%=strDescription%>" />
												<html:hidden name="updateSystemParameterForm" styleId="<%=strAlias%>" 			property="<%=strAlias%>" />
												<html:hidden name="updateSystemParameterForm" styleId="<%=strSytemGenerated%>" 	property="<%=strSytemGenerated%>" />
												<tr>

													<td align="center"class="tblleftlinerows" width="5%">
														<%=(pkgAttributeIndex + 1)%>
													</td>
													<td align="left" valign="top" width="20%" class="tblleftlinerows"><b>
														<bean:write name="updateSystemParameterForm" property="<%=strName %>" /></b>
													</td>
													<td align="left"  valign="top" width="75%" class="tblrightlinerows" >
														<html:text name="updateSystemParameterForm" size = "50" styleId="<%=strValue%>" property="<%=strValue%>" maxlength="255" title="<%=updateSystemParameterForm.getAlias(j)%>"/>
														<html:hidden name="updateSystemParameterForm" styleId="<%=strValue%>" property="<%=strValue%>" />
													</td>

												</tr>
												<tr>
													<td align="left" class="tblleftlinerows" valign="top" width="5%">&nbsp;</td>
													<td align="left" class="tblleftlinerows" valign="top" width="20%">
														<bean:message key="systemparameter.description" />
													</td>
													<td align="left" valign="top" width="75%" class="tblrightlinerows">
														<bean:write name="updateSystemParameterForm" property="<%=strDescription %>" />&nbsp;
													</td>
												</tr>
												<%pkgAttributeIndex += 1;}	%>
												<%j++;%>
											</logic:iterate>
											<%}%>
										</table>
									</td>
								</tr>
							</table>
						</td>
					<tr>
                    	<td  colspan="3" >&nbsp;</td>
                  	</tr> 
					<tr>
						<td class="table-header" colspan="4">&nbsp;		
						</td>
					</tr>					
					<tr>
						<td class="table-header" colspan="4">		
							<bean:message key="systemparameter.passwordselectionpolicy" />
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle" colspan="3">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="small-gap" colspan="3">
										&nbsp;
									</td>
								</tr>
								<tr>
									<td colspan="3">
										<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0">
											<tr>
												<td align="center" class="tblheader-bold" valign="top" width="5%">
													<bean:message key="systemparameter.serialno" />
												</td>
												<td align="left" class="tblheader-bold" valign="top" width="20%">
													<bean:message key="systemparameter.name" />
												</td>
												<td align="left" class="tblheader-bold" valign="top" width="75%">
													<bean:message key="systemparameter.value" />
												</td>
											</tr>
										    <tr>
												<td align="center"class="tblleftlinerows" width="5%">
													<%=pwdIndex++%>
												</td>
												<td align="left" class="tblleftlinerows" width="20%">
													<b><bean:message key="systemparameter.minmaxlength" /></b>
												</td>
												<td align="left" class="tblleftlinerows" width="75%">
													<input type="text" id="passRangeId" name="PassRange" size="50" maxlength="5" value='<%=(passwordPolicyConfigData.getPasswordRange() != null ? passwordPolicyConfigData.getPasswordRange() : "") %>' style="width: 255px"/> <font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
													<td class="tblleftlinerows" width="5%">
														&nbsp;
													</td>
													<td align="left" class="tblleftlinerows" valign="top" width="20%">
														<bean:message key="systemparameter.description" />
													</td>
													<td align="left" class="tblleftlinerows" width="75%">
														<bean:message key="systemparameter.minmaxlengthdesc" />
														&nbsp;
													</td>
										   </tr>
											<tr>
												<td align="center" class="tblleftlinerows"  valign="top" width="5%">
													<%=pwdIndex++%>
												</td>
												<td align="left" class="tblleftlinerows"  valign="top" width="20%">
													<b><bean:message key="systemparameter.minmaxalphabets" /></b>
												</td>
												<td align="left" class="tblleftlinerows"  valign="top" width="75%">
													<input type="text" id="alphaRangeId" name="alphaRange" size="50" maxlength="2" value='<%=(passwordPolicyConfigData.getAlphabetRange() != null ? passwordPolicyConfigData.getAlphabetRange() : "") %>' style="width: 255px"/>
												</td>
											</tr>
											<tr>
													<td class="tblleftlinerows" width="5%">
														&nbsp;
													</td>
													<td align="left" class="tblleftlinerows" valign="top" width="20%">
														<bean:message key="systemparameter.description" />
													</td>
													<td align="left" class="tblleftlinerows" width="75%">
														<bean:message key="systemparameter.minmaxalphadesc" />
														&nbsp;
													</td>
										   </tr>
											<tr>
												<td align="center" class="tblleftlinerows"  valign="top" width="5%">
													<%=pwdIndex++%>
												</td>
												<td align="left" class="tblleftlinerows"  valign="top" width="20%">
													<b><bean:message key="systemparameter.minmaxdigits" /></b>
												</td>
												<td align="left" class="tblleftlinerows"  valign="top" width="75%">
												    <input type="text" id="digitRangeId" name="digitRange" size="50"  maxlength="2"  value='<%=(passwordPolicyConfigData.getDigitsRange() != null ? passwordPolicyConfigData.getDigitsRange() : "")%>' style="width: 255px"/>
												</td>
											</tr>
											<tr>
													<td class="tblleftlinerows" width="5%">
														&nbsp;
													</td>
													<td align="left" class="tblleftlinerows" valign="top" width="20%">
														<bean:message key="systemparameter.description" />
													</td>
													<td align="left" class="tblleftlinerows" width="75%">
														<bean:message key="systemparameter.minmaxdigitdesc" />
														&nbsp;
													</td>
										   </tr>
											<tr>
												<td align="center" class="tblleftlinerows"  valign="top" width="5%">
													<%=pwdIndex++%>
												</td>
												<td align="left" class="tblleftlinerows"  valign="top" width="20%">
													<b><bean:message key="systemparameter.minmaxspecialchars" /></b>
												</td>
												<td align="left" class="tblleftlinerows"  valign="top" width="75%">
													<input type="text" id="specialCharRangeId" name="specialCharRange" size="50"  maxlength="2"  value='<%=(passwordPolicyConfigData.getSpecialCharRange() != null ? passwordPolicyConfigData.getSpecialCharRange() :"") %>' style="width: 255px"/>
												</td>
											</tr>
											<tr>
													<td class="tblleftlinerows" width="5%">
														&nbsp;
													</td>
													<td align="left" class="tblleftlinerows" valign="top" width="20%">
														<bean:message key="systemparameter.description" />
													</td>
													<td align="left" class="tblleftlinerows" width="75%">
														<bean:message key="systemparameter.minmaxspecialchardesc" />
														&nbsp;
													</td>
										   </tr>
											<tr>
												<td align="center" class="tblleftlinerows"  valign="top" width="5%">
													<%=pwdIndex++%>
												</td>
												<td align="left" class="tblleftlinerows"  valign="top" width="20%">
													<b><bean:message key="systemparameter.prohibitedchars" /></b>
												</td>
												<td align="left" class="tblleftlinerows"  valign="top" width="75%">
													<input type="text" id="prohibitedCharId" name="prohibitedChar" size="50" maxlength="50"  value="<%=StringEscapeUtils.escapeHtml(passwordPolicyConfigData.getProhibitedChars()!=null ?passwordPolicyConfigData.getProhibitedChars():"")%>" style="width: 255px"/>
												</td>
											</tr>
											<tr>
													<td class="tblleftlinerows" width="5%">
														&nbsp;
													</td>
													<td align="left" class="tblleftlinerows" valign="top" width="20%">
														<bean:message key="systemparameter.description" />
													</td>
													<td align="left" class="tblleftlinerows" width="75%">
														<bean:message key="systemparameter.minmaxprohibitedchardesc" />
														&nbsp;
													</td>
										   </tr>
										   <tr>
												<td align="center" class="tblleftlinerows"  valign="top" width="5%">
													<%=pwdIndex++%>
												</td>
												<td align="left" class="tblleftlinerows"  valign="top" width="20%">
													<b><bean:message key="systemparameter.passwordvalidity" /></b>
												</td>
												<td align="left" class="tblleftlinerows"  valign="top" width="75%">
													<input type="text" id="passwordValidityId" name="passwordValidityId" size="20"  maxlength="9"  value='<%=(passwordPolicyConfigData.getPasswordValidity())%>' style="width: 255px"/>
												</td>
											</tr>
											<tr>
													<td class="tblleftlinerows" width="5%">
														&nbsp;
													</td>
													<td align="left" class="tblleftlinerows" valign="top" width="20%">
														<bean:message key="systemparameter.description" />
													</td>
													<td align="left" class="tblleftlinerows" width="75%">
														<bean:message key="systemparameter.passwordvaliditydesc" />
														&nbsp;
													</td>
										   </tr>
										   <tr>
												<td align="center" class="tblleftlinerows"  valign="top" width="5%">
													<%=pwdIndex++%>
												</td>
												<td align="left" class="tblleftlinerows"  valign="top" width="20%">
													<b><bean:message key="systemparameter.changepwd.onfirstlogin" /></b>
												</td>
												<td align="left" class="tblleftlinerows"  valign="top" width="75%">
													<select id="changePwdOnFirstLoginId" name="changePwdOnFirstLoginId">
														<option value="true" id="true" > True </option>
														<option value="false" id ="false"> False </option>
													</select>
												</td>
											</tr>
											<tr>
													<td class="tblleftlinerows" width="5%">
														&nbsp;
													</td>
													<td align="left" class="tblleftlinerows" valign="top" width="20%">
														<bean:message key="systemparameter.description" />
													</td>
													<td align="left" class="tblleftlinerows" width="75%">
														<bean:message key="systemparameter.changepwd.onfirstlogin.desc" />
														&nbsp;
													</td>
										   </tr>
										   <tr>
												<td align="center" class="tblleftlinerows"  valign="top" width="5%">
													<%=pwdIndex++%>
												</td>
												<td align="left" class="tblleftlinerows"  valign="top" width="20%">
													<b><bean:message key="systemparameter.historical.passwords" /></b>
												</td>
												<td align="left" class="tblleftlinerows"  valign="top" width="75%">
													<input type="text" id="totalHistoricalPasswordsId" name="totalHistoricalPasswordsId" size="20"  maxlength="2"  value='<%=passwordPolicyConfigData.getTotalHistoricalPasswords()%>' style="width: 255px"/> <font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
													<td class="tblleftlinerows" width="5%">
														&nbsp;
													</td>
													<td align="left" class="tblleftlinerows" valign="top" width="20%">
														<bean:message key="systemparameter.description" />
													</td>
													<td align="left" class="tblleftlinerows" width="75%">
														<bean:message key="systemparameter.historical.passwords.desc" />
														&nbsp;
													</td>
										   </tr>
										   
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>														
					<tr>
                        <td class="small-gap" width="50%" colspan="2" >&nbsp;</td>
                    </tr>
					<tr>
						<td class="small-gap" width="50%" colspan="2">
							&nbsp;
						</td>
					</tr>
			 		<tr> 
			 		  	<td width="18%">&nbsp;</td>
						<td class="btns-td" valign="middle">
			   			<input type="button" name="c_btnCreate" onclick="validateCreate()" id="c_btnCreate2" value="   Update   " class="light-btn">
        		        <input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/viewSystemParameter.do?/>'"  value="Cancel" class="light-btn"> 
   		                </td>
                        <td width="50%" >&nbsp;</td>
                    </tr>
                    <tr>
                        <td class="small-gap" width="50%" colspan="2" >&nbsp;</td>
                    </tr>
					<tr>
						<td class="small-gap" width="50%" colspan="2">
							&nbsp;
						</td>
					</tr>
					</html:form>
			   </table>			
		</td>
	</tr>
	<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>		

