<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page
	import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>

<%
   String basePath = request.getContextPath();
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
	function validateForm(){	
		if(document.forms[0].wsAddress.value == 0){
			alert('Web Service Address must be specified.');
		}else if(isNull(document.forms[0].smServiceName.value)){
			alert('Session Manager Service Name must be specified.');
		}else if(isNull(document.forms[0].parleyServiceName.value)){
			alert('Parley Service Name must be specified.');
		}else if(isNull(document.forms[0].userName.value)){
			alert('UserName must be specified.');
		}else if(isNull(document.forms[0].password.value)){
			alert('Password must be specified.');
		}else{ 			 			
				alert('This Module has reached End-of-Life state, use at your own risk.');
				document.forms[0].action.value = 'create';
				document.forms[0].submit();
		}		
	}

	setTitle('<bean:message bundle="driverResources" key="driver.driver"/>'); 
</script>

<html:form action="/createRadiusParleyChargingDriver">
	<html:hidden name="createRadiusPCDriverForm" property="action" />
	<html:hidden name="createRadiusPCDriverForm"
		property="driverInstanceName" />
	<html:hidden name="createRadiusPCDriverForm" property="driverDesp" />
	<html:hidden name="createRadiusPCDriverForm" property="driverRelatedId" />

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
											bundle="driverResources" key="driver.createpcdriver" /></td>
								</tr>
								<tr>
									<td class="small-gap" colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td valign="middle" colspan="2">
										<table cellpadding="0" cellspacing="0" border="0" width="100%"
											height="30%">
											<tr>
												<td align="left" class="captiontext" valign="top" width="20%">
													<bean:message bundle="driverResources"
														key="driver.webserviceaddress" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="driver.webserviceaddress" 
																	header="driver.webserviceaddress"/>
												</td>

												<td align="left" class="labeltext" valign="top"><html:text
														styleId="wsAddress" property="wsAddress" size="30"
														maxlength="30" style="width:250px" tabindex="1" /></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="driverResources" 
														key="driver.smservicename" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="driver.smservicename" 
																	header="driver.smservicename"/> 
												</td>

												<td align="left" class="labeltext" valign="top"><html:text
														styleId="smServiceName" property="smServiceName" size="30"
														maxlength="30" style="width:250px" tabindex="2" /></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="driverResources" 
														key="driver.parleyservicename" />												
															<ec:elitehelp headerBundle="driverResources" 
																text="driver.parleyservicename" 
																	header="driver.parleyservicename"/>
												</td>

												<td align="left" class="labeltext" valign="top"><html:text
														styleId="parleyServiceName" tabindex="3"
														property="parleyServiceName" size="30" maxlength="30"
														style="width:250px" /></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="driverResources" 
														key="driver.username" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="detaildriver.username" 
																	header="driver.username"/>
												</td>

												<td align="left" class="labeltext" valign="top"><html:text
														styleId="userName" tabindex="4" property="userName"
														size="30" maxlength="30" style="width:250px" /></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="driverResources" key="driver.password" /> 
														<ec:elitehelp headerBundle="driverResources" 
															text="detaildriver.password" 
																header="driver.password"/>
												</td>

												<td align="left" class="labeltext" valign="top"><html:password
														styleId="password" property="password" tabindex="5"
														size="30" maxlength="30" style="width:250px" /></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" colspan="1" width="25%">
													<bean:message bundle="driverResources"
														key="driver.parleycharging.transmapconf" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="cresteldriver.transaltionmappingconfiguration" 
																	header="driver.parleycharging.transmapconf"/>
												</td>
												<td align="left" class="labeltext" valign="top"><bean:define
														id="translationMappingConfDataLst"
														name="createRadiusPCDriverForm"
														property="translationMappingConfDataList"
														type="java.util.List"></bean:define> <html:select
														name="createRadiusPCDriverForm" tabindex="6"
														styleId="translationMapConfigId"
														property="translationMapConfigId" size="1"
														style="width: 130px;">
														<html:option value="0">--Select--</html:option>
														<html:options collection="translationMappingConfDataLst"
															property="translationMapConfigId" labelProperty="name" />
													</html:select><font color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td class="small-gap" colspan="2">&nbsp;</td>
											</tr>
											<tr>
												<td class="small-gap" colspan="2">&nbsp;</td>
											</tr>

											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2"><input
													type="button" name="c_btnCreate" tabindex="7"
													onclick="validateForm()" id="c_btnCreate2" value="Create"
													class="light-btn"> <input type="reset"
													name="c_btnCancel" tabindex="8"
													onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?/>'"
													value="Cancel" class="light-btn"></td>
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