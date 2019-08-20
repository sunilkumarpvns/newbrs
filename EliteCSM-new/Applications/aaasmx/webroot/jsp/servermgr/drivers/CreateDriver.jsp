<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.service.data.ServiceTypeData,com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverTypeData"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	String basePath = request.getContextPath();
%>
<script language="javascript1.2" src="<%=basePath%>/js/checkpwdstrength.js" type="text/javascript"></script>
<script language="javascript1.2">
 var isValidName;

 	var obj=top.frames["mainFrame"].document.getElementById('contentIframe');
	
 	function validateForm(){
 		if(isNull(document.forms['nextDriver'].name.value)){
 			alert('Name must be specified');
 		}else if(!isValidName) {
 			alert('Enter Valid Driver Name');
 			document.forms['nextDriver'].name.focus();
 			return;
 		}else  if(document.forms['nextDriver'].selecteDriver.value == 0){
 			alert('Select atleast one driver.');
 		}else{
 			document.forms['nextDriver'].action.value = 'next';
 	 		document.forms['nextDriver'].submit();	
 		}	
 	 	 			
	}

 	function verifyName() {
 		var searchName = document.getElementById("name").value;
 		isValidName = verifyInstanceName('<%=InstanceTypeConstants.DRIVER%>',searchName,'create','','verifyNameDiv');
 	}
 	
 	if(obj != null){
	 	if(obj.id !== "contentIframe"){
			setTitle('<bean:message bundle="driverResources" key="driver.driver"/>'); 		
		}
 	}
</script>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<html:form action="/nextCreateDriver" styleId="nextDriver">
	<html:hidden name="createDriverForm" property="action" value="next" />
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
											bundle="driverResources" key="driver.instance" /></td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle" colspan="3">
										<table cellpadding="0" cellspacing="0" border="0" width="100%"
											height="30%">

											<tr>
												<td align="left" class="labeltext" valign="top" width="18%">
													<bean:message bundle="driverResources" key="driver.name" />
													<ec:elitehelp headerBundle="driverResources" 
													text="createdriver.name" header="driver.name"/>
												</td>

												<td align="left" class="labeltext" valign="top"
													nowrap="nowrap"><html:text styleId="name"
														onkeyup="verifyName();" property="name" size="40"
														maxlength="70" style="width:250px" tabindex="1" /><font
													color="#FF0000"> *</font>
													<div id="verifyNameDiv" class="labeltext"></div></td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top" width="18%">
													<bean:message bundle="driverResources"
													key="driver.description" />
													<ec:elitehelp headerBundle="driverResources" 
													text="createdriver.description" header="driver.description"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="66%">
													<html:textarea styleId="description" property="description"
														cols="40" rows="4" style="width:250px" tabindex="2" />
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top" width="18%">
													<bean:message bundle="driverResources" key="driver.drivertype" /> 
													<ec:elitehelp headerBundle="driverResources" 
													text="createdriver.type" header="driver.drivertype"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="66%">
													<html:select name="createDriverForm"
														styleId="selecteDriver" property="selecteDriver"
														style="width:250px" tabindex="3">
														<html:option value="0">Select</html:option>
														<logic:iterate id="objservice" name="createDriverForm"
															type="ServiceTypeData" property="serviceList">
															<optgroup label="<%=objservice.getDisplayName() %>"
																class="labeltext">
																<logic:iterate id="objdriver" name="objservice"
																	type="DriverTypeData" property="driverTypeSet">
																	<html:option
																		value="<%=String.valueOf(objdriver.getDriverTypeId())%>"><%=objdriver.getDisplayName() %>
																	</html:option>
																</logic:iterate>
															</optgroup>
														</logic:iterate>
													</html:select>
												</td>
											</tr>


											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2"><input
													type="button" name="c_btnCreate" id="c_btnCreate2"
													value="Next" class="light-btn" onclick="validateForm()"
													tabindex="4"> <input type="reset"
													name="c_btnDeletePolicy"
													onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?'"
													value="Cancel" class="light-btn" tabindex="5"></td>
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
