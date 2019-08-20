<%@page import="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.SystemParameterData" %>
<%@ page import="com.elitecore.elitesm.web.core.system.systemparameter.forms.ViewSystemParameterForm" %>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.ISystemParameterData" %>
<%@ page import="java.util.List" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<% List lstParameterValue = ((ViewSystemParameterForm) request.getAttribute("viewSystemParameterForm")).getLstParameterValue();
   PasswordPolicyConfigData passwordPolicyConfigData = ((ViewSystemParameterForm) request.getAttribute("viewSystemParameterForm")).getPasswordSelectionConfigData();
   int iIndex = 0;
   int pwdIndex =1;
%>


<script>
	setTitle('<bean:message key="systemparameter.systemparameter"/>');
</script>

<html:form action="/viewSystemParameter">
	<html:hidden name="viewSystemParameterForm" styleId="action" 		property="action" />
	<html:hidden name="viewSystemParameterForm" styleId="parameterId" 	property="parameterId" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
  			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">
  				&nbsp;
  			</td>
			<td>
   				<table cellpadding="0" cellspacing="0" border="0" width="100%">
  		  		<tr>
		    		<td  cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td class="table-header" colspan="4">		
							<bean:message key="systemparameter.systemparameterlist" />
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
											<% if (lstParameterValue != null
											   && lstParameterValue.size() > 0) {
												%>
											   <logic:iterate id="systemParameterBean" name="viewSystemParameterForm" property="lstParameterValue" type="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.ISystemParameterData">
												<% ISystemParameterData dData = (ISystemParameterData) lstParameterValue.get(iIndex); %>
												<tr>
													<td align="center"class="tblleftlinerows" width="5%">
													<%=(iIndex + 1)%>
													</td>
													<td align="left" class="tblleftlinerows" width="20%">
														<b><bean:write name="systemParameterBean" property="name" /></b>
													</td>
													<td align="left" class="tblleftlinerows" width="75%">
														<b><bean:write name="systemParameterBean" property="value" />&nbsp;</b>
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
														<bean:write name="systemParameterBean" property="description" />
														&nbsp;
													</td>
												</tr>
												<%iIndex += 1;%>
											</logic:iterate>
											<%} else {%>
											<tr>
												<td align="center" class="tblfirstcol" colspan="8">
													No Records Found.
												</td>
											</tr>
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
							<bean:message key="systemparameter.passwordselectionpolicy" />
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
													<b><%=(passwordPolicyConfigData.getPasswordRange() != null?passwordPolicyConfigData.getPasswordRange() : "") %></b>
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
													<b><%=(passwordPolicyConfigData.getAlphabetRange() != null ? passwordPolicyConfigData.getAlphabetRange() :"") %></b>
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
													<b><%=(passwordPolicyConfigData.getDigitsRange() != null ? passwordPolicyConfigData.getDigitsRange() : "") %></b>
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
													<b><%=(passwordPolicyConfigData.getSpecialCharRange() != null ? passwordPolicyConfigData.getSpecialCharRange() : "") %></b>
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
													<b><%=(passwordPolicyConfigData.getProhibitedChars() != null ? passwordPolicyConfigData.getProhibitedChars() :"") %></b>
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
													<b><%=(passwordPolicyConfigData.getPasswordValidity() != 0 ? passwordPolicyConfigData.getPasswordValidity() :"") %></b>
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
													<b><%=passwordPolicyConfigData.getChangePwdOnFirstLogin()%></b>
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
													<b><%=passwordPolicyConfigData.getMaxHistoricalPasswords()%></b>
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
                        <td class="small-gap" width="50%" colspan="3" >&nbsp;</td>
                    </tr>
					<tr>
						<td class="small-gap" width="50%" colspan="3">
							&nbsp;
						</td>
					</tr>
			 		<tr> 
			 		  	<td width="18%">&nbsp;</td>
						<td class="btns-td" valign="middle">
			   			<input type="button" name="c_btnCreate" onclick="javascript:location.href='<%=basePath%>/updateSystemParameter.do?action=list'" value="   Edit   "  class="light-btn"/> 
        		        <input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/viewSystemParameter.do?/>'"  value="Cancel" class="light-btn"> 
        		        <input type="button" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/viewSystemParameter.do?action=refresh'"  value=" Refresh " class="light-btn"> 
        		        
   		                </td>
                        <td width="50%" >&nbsp;</td>
                    </tr>
                    <tr>
                        <td class="small-gap" width="50%" colspan="3" >&nbsp;</td>
                    </tr>
					<tr>
						<td class="small-gap" width="50%" colspan="3">
							&nbsp;
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</td>
</tr>
</table>
</html:form>