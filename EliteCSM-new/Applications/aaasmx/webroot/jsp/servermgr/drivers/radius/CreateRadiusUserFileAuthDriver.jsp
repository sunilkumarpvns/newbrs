<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="java.util.List"%>

<%
	String basePath = request.getContextPath();

%>

<script language="javascript1.2"
	src="<%=basePath%>/js/checkpwdstrength.js" type="text/javascript"></script>

<script language="javascript1.2">

 	function validateForm(){	

 		if(isNull(document.forms[0].fileLocations.value)){

            alert('Driver File Location must be specified.');
         	 
       }else if(isNull(document.forms[0].expirydate.value)){

              alert('Driver Expiry date must be specified.');
           	 
       }else{    
 		
 			document.forms[0].action.value = 'create';
 	 		document.forms[0].submit();	
         }		
	
	}
</script>


<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<html:form action="/createRadiusUserFileAuthDriver">

	<html:hidden name="createRadiusUserFileAuthDriverForm"
		property="action" />
	<html:hidden name="createRadiusUserFileAuthDriverForm"
		property="driverInstanceName" />
	<html:hidden name="createRadiusUserFileAuthDriverForm"
		property="driverDesp" />
	<html:hidden name="createRadiusUserFileAuthDriverForm"
		property="driverRelatedId" />

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
											bundle="driverResources" key="driver.createuserfileDriver" /></td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td>
										<table cellpadding="0" cellspacing="0" border="0" width="100%">

											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="driverResources"
														key="driver.fileLocations" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="ufauthdriver.filelocation" header="driver.fileLocations"/>	
												</td>
												<td align="left" class="labeltext" valign="top" width="76%">
													<html:text styleId="fileLocations" property="fileLocations"
														size="60" maxlength="255" style="width:300px" tabindex="1" /><font
													color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="driverResources"
														key="driver.userfileauthdriver.expirydate" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="ufauthdriver.expirydate" header="driver.userfileauthdriver.expirydate"/>		
												</td>
												<td align="left" class="labeltext" valign="top" width="76%">
													<html:text styleId="expirydate" property="expiryDateFormat"
														size="30" maxlength="60" style="width:300px" tabindex="2" /><font
													color="#FF0000"> *</font>
												</td>
											</tr>

										</table>
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2"><input
													type="button" name="c_btnCreate" id="c_btnCreate2"
													value=" Create " class="light-btn" onclick="validateForm()"
													tabindex="3"> <input type="reset"
													name="c_btnDeletePolicy"
													onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?'"
													value="Cancel" class="light-btn" tabindex="4"></td>
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

<script>
setTitle('<bean:message bundle="driverResources" key="driver.driver"/>'); 
</script>
