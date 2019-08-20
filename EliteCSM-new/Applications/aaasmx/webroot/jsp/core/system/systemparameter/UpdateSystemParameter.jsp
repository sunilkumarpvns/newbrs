<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@page import="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData"%>
<%@ page import="com.elitecore.elitesm.web.core.system.systemparameter.forms.UpdateSystemParameterForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.SystemParameterData"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.ISystemParameterData"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.SystemParameterValuePoolData"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	UpdateSystemParameterForm updateSystemParameterForm = (UpdateSystemParameterForm) request.getAttribute("updateSystemParameterForm");
	PasswordPolicyConfigData passwordPolicyConfigData = ((UpdateSystemParameterForm) request.getAttribute("updateSystemParameterForm")).getPasswordSelectionConfigData();
	int iIndex = 0;
	int j = 0;
	int pwdIndex =1;
	boolean flag=true;
	String pwdRange="",digitRange="",alphaRange="",specialCharRange="";
%>

<script>
$(document).ready(function(){
	$("#changePwdOnFirstLoginId").val('<%=passwordPolicyConfigData.getChangePwdOnFirstLogin()%>');
})
function validateCreate(){

	        var isValid=isValidPasswordSelectionPolicy();
	        if(isValid == true){
	        	$('#passwordRange').val($('#passRangeId').val());
	 			$('#aplhabetsRange').val($('#alphaRangeId').val());
	 			$('#digitRange').val($('#digitRangeId').val());
	 			$('#speCharRange').val($('#specialCharRangeId').val());
	 			$('#proChar').val($('#prohibitedCharId').val());
	 			$('#pwdValidity').val($('#passwordValidityId').val());
	 			$('#changePwdOnFirstLogin').val($('#changePwdOnFirstLoginId').val());


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
	var totallength=0,maxLength=0;
	if(checkPasswordRange =='' && checkAlphaRange == '' && chechDigitRange == '' && chechSpecialCharRange == '' && chechProhibitedChar == '' && chechPasswordValidity == ''){
		alert('Password Selection Policy will not apply');
		return true;
	}else{
		
		if(checkPasswordRange != ''){
			if(!(checkPasswordRange.indexOf("-") != -1)){
				alert('Invalid min-max password Range !!');
				return false;
			}else{
				var pwdRangefields = checkPasswordRange.split(/-/);
				var minPwdRange = pwdRangefields[0];
				var maxPwdRange = pwdRangefields[1];
				
				if(minPwdRange !='' && maxPwdRange !=''){
					var checkminPwdRange=isNaN(minPwdRange);
					var checkmaxPwdRange=isNaN(maxPwdRange);
					if( checkminPwdRange == true || checkmaxPwdRange == true){
						alert('Only Decimals are allowed in Min-Max Password Range');
						return false;
					}else if(parseInt(minPwdRange) > parseInt(checkmaxPwdRange)){
						alert('Minimum Password length must be less than or equal to maximum password Length');
						return false;
					}
				}else{
					alert('Please check Min-Max Password Range');
					return false;
				}
			}
			maxLength=parseInt(minPwdRange);
		}
		
		
		if(checkAlphaRange != ''){
			if(!(checkAlphaRange.indexOf("-") != -1)){
				alert('Invalid Alphabets Range !!');
				return false;
			}else{
				var alphaRangefields = checkAlphaRange.split(/-/);
				var minAlphaRange = alphaRangefields[0];
				var maxAlphaRange = alphaRangefields[1];
				
				if(minAlphaRange != '' && maxAlphaRange !=''){
					var checkminAlphaRange=isNaN(minAlphaRange);
					var checkmaxAlphaRange=isNaN(maxAlphaRange);
					if( checkminAlphaRange == true || checkmaxAlphaRange == true){
						alert('Only Decimals are allowed in Min-Max Alphabets Range');
						return false;
					}else if(parseInt(minAlphaRange) > parseInt(maxAlphaRange)){
						alert('Minimum Alphabets Range must be less than or equal to maximum Alphabets Range');
						return false;
					}
				}else{
					alert('Invalid value in Min-Max Alphabets Range');
					return false;
				}
			}
			totallength=parseInt(totallength)+parseInt(minAlphaRange);
		}
		
		if(chechDigitRange != ''){
			if(!(chechDigitRange.indexOf("-") != -1)){
				alert('Invalid Digits Range !!');
				return false;
			}else{
				var digitRangefields = chechDigitRange.split(/-/);
				var minDigitRange = digitRangefields[0];
				var maxDigitRange = digitRangefields[1];
				
				if(minDigitRange != '' && maxDigitRange !=''){
					var checkminDigitRange=isNaN(minDigitRange);
					var checkmaxDigitRange=isNaN(maxDigitRange);
					if( checkminDigitRange == true || checkmaxDigitRange == true){
						alert('Only Decimals are allowed in Min-Max Digits Range');
						return false;
					}else if(parseInt(minDigitRange) > parseInt(maxDigitRange)){
						alert('Minimum Digits Range must be less than or equal to maximum Digits Range');
						return false;
					}
				}else{
					alert('Invalid value in Min-Max Digits Range');
					return false;
				}
			}
			totallength=parseInt(totallength)+parseInt(minDigitRange);
		}
		
		if(chechSpecialCharRange != ''){
			if(!(chechSpecialCharRange.indexOf("-") != -1)){
				alert('Invalid Special Characters Range !!');
				return false;
			}else{
				var specCharRangefields = chechSpecialCharRange.split(/-/);
				var minspecCharRange = specCharRangefields[0];
				var maxspecCharRange = specCharRangefields[1];
				
				if(minspecCharRange != '' && maxspecCharRange !=''){
					var checkminspecCharRange=isNaN(minspecCharRange);
					var checkmaxspecCharRange=isNaN(maxspecCharRange);
					if( checkminspecCharRange == true || checkmaxspecCharRange == true){
						alert('Only Decimals are allowed in Min-Max Special Characters Range');
						return false;
					}else if(parseInt(minspecCharRange) > parseInt(maxspecCharRange)){
						alert('Minimum Special Characters Range must be less than or equal to maximum Range');
						return false;
					}
				}else{
					alert('Invalid value in Min-Max Special Characters');
					return false;
				}
			}
			totallength=parseInt(totallength)+parseInt(minspecCharRange);
		}
		
	    if(checkPasswordRange != ''){
			if(totallength > maxLength){
				alert('Total of all minimum ranges should be less than minimum password length');
				return false;
			}
		}
		return true;
	}
	
	
}
	setTitle('<bean:message key="systemparameter.systemparameter"/>');
</script>

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
	
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header" colspan="4">
									<bean:message key="systemparameter.updatesystemparameter" /></td>
								</tr>

								<tr>
									<td valign="middle" colspan="3">
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td class="small-gap" colspan="3">&nbsp;</td>
											</tr>
											<tr>
												<td colspan="3">
													<table width="100%" type="tbl-list" border="0"
														cellpadding="0" cellspacing="0">
														<tr>
															<td align="center" class="tblheader-bold" valign="top" width="5%"><bean:message key="systemparameter.serialno" /></td>
															<td align="left" class="tblheader-bold" valign="top" width="20%"><bean:message key="systemparameter.name" /></td>
															<td align="left" class="tblheader-bold" valign="top" width="75%"><bean:message key="systemparameter.value" /></td>
														</tr>

														<%	 if (updateSystemParameterForm.getLstParameterValue() != null) { %>

														<logic:iterate id="updateParameterBean" name="updateSystemParameterForm" property="lstParameterValue" type="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.ISystemParameterData">
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

															<html:hidden name="updateSystemParameterForm" styleId="<%=strParameterId%>" property="<%=strParameterId%>" />
															<html:hidden name="updateSystemParameterForm" styleId="<%=strName%>" property="<%=strName%>" />
															<html:hidden name="updateSystemParameterForm" styleId="<%=strDescription%>" property="<%=strDescription%>" />
															<html:hidden name="updateSystemParameterForm" styleId="<%=strAlias%>" property="<%=strAlias%>" />
															<html:hidden name="updateSystemParameterForm" styleId="<%=strSytemGenerated%>" property="<%=strSytemGenerated%>" />
															<tr>
																<td align="center" class="tblleftlinerows" width="5%">
																	<%=(iIndex + 1)%>
																</td>
																<td align="left" valign="top" width="20%" class="tblleftlinerows"><b> 
																	<bean:write name="updateSystemParameterForm" property="<%=strName %>" /></b></td>
																<td align="left" valign="top" width="75%" class="tblrightlinerows">
																	<%if(systemParameterValuePoolData!=null && systemParameterValuePoolData.size() > 0){%>
																	<html:select name="updateSystemParameterForm" styleId="<%=strValue%>" property="<%=strValue%>" size="1" style="width:255px">
																	<%
													 					Iterator itr = systemParameterValuePoolData.iterator();
													 					while(itr.hasNext()){
													 					SystemParameterValuePoolData parameterValuePoolData = (SystemParameterValuePoolData)itr.next();
													 				%>
																		<html:option value="<%=parameterValuePoolData.getValue()%>"><%=parameterValuePoolData.getName()%></html:option>
																	<% } %>
																	</html:select>
																	<%}else{%>
																	<html:text name="updateSystemParameterForm" size="50" styleId="<%=strValue%>" property="<%=strValue%>" maxlength="255"  style="width:255px"/>
																	<%}%>
																</td>
															</tr>
															<tr>
																<td align="left" class="tblleftlinerows" valign="top" width="5%">&nbsp;</td>
																<td align="left" class="tblleftlinerows" valign="top" width="20%"><bean:message key="systemparameter.description" /></td>
																<td align="left" valign="top" width="75%"class="tblrightlinerows"><bean:write name="updateSystemParameterForm" property="<%=strDescription %>" />&nbsp;</td>
															</tr>
															<%j++;%>
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
									 <td class="table-header" colspan="4">		
										<bean:message key="systemparameter.updatepasswordselectionpolicy" />
									 </td>
								</tr>
										<tr>
						<td valign="middle" colspan="3">
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
													<input type="text" id="passRangeId" name="PassRange" value='<%=(passwordPolicyConfigData.getPasswordRange() != null ? passwordPolicyConfigData.getPasswordRange() : "") %>' style="width: 255px"/>
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
													<input type="text" id="alphaRangeId" name="alphaRange" value='<%=(passwordPolicyConfigData.getAlphabetRange() != null ? passwordPolicyConfigData.getAlphabetRange() : "") %>' style="width: 255px"/>
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
												    <input type="text" id="digitRangeId" name="digitRange" value='<%=(passwordPolicyConfigData.getDigitsRange() != null ? passwordPolicyConfigData.getDigitsRange() : "")%>' style="width: 255px"/>
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
													<input type="text" id="specialCharRangeId" name="specialCharRange" value='<%=(passwordPolicyConfigData.getSpecialCharRange() != null ? passwordPolicyConfigData.getSpecialCharRange() :"") %>' style="width: 255px"/>
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
													<input type="text" id="prohibitedCharId" name="prohibitedChar" value='<%=(passwordPolicyConfigData.getProhibitedChars() != null ? passwordPolicyConfigData.getProhibitedChars() : "") %>' style="width: 255px"/>
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
													<input type="text" id="passwordValidityId" name="passwordValidityId" value='<%=(passwordPolicyConfigData.getPasswordValidity() != 0 ? passwordPolicyConfigData.getPasswordValidity() :"")  %>' style="width: 255px"/>
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
													<b><bean:message key="systemparameter.maxhistory.password" /></b>
												</td>
												<td align="left" class="tblleftlinerows"  valign="top" width="75%">
													<input type="text" id="maxHistoricalPassword" maxlength="1" name="maxHistoricalPassword" value='<%=passwordPolicyConfigData.getMaxHistoricalPasswords()%>' style="width: 255px"/>
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
														<bean:message key="systemparameter.maxhistory.password.desc" />
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
									<td class="small-gap" width="50%" colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td class="small-gap" width="50%" colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td width="18%">&nbsp;</td>
									<td class="btns-td" valign="middle">
										<input type="button" name="c_btnCreate" onclick="validateCreate()" id="c_btnCreate2" value="   Update   " class="light-btn">
										<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/viewSystemParameter.do?/>'" value="Cancel" class="light-btn">
									</td>
									<td width="50%">&nbsp;</td>
								</tr>
								<tr>
									<td class="small-gap" width="50%" colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td class="small-gap" width="50%" colspan="2">&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
