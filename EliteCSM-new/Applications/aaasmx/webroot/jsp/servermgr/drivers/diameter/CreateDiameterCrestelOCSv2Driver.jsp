<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<% 	
	String basePath = request.getContextPath();
%>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script language="javascript">

	$(document).ready(function(){
		$('#mappingtbl td img.delete').live('click',function() {	
				 $(this).parent().parent().remove(); 
			});
	});
	function validateForm(){	
		if(document.forms[0].translationMapConfigId.value == '0'){
			alert('Translation Mapping Configuration must be selected.');
			return;
		}else if(isNull(document.forms[0].instanceNumber.value)){
 			alert('Instance Number must be specified.');
 			return;
 		}else if(isNaN(document.forms[0].instanceNumber.value)){
 			alert('Instance Number must be Numeric.');
 			return;
 		}else if(document.forms[0].instanceNumber.value <= 0){
 			alert('Instance Number must be positive number.');
 			return;
 		}else{
 			if(isValidJNDIPropertyMapping("mappingtbl","jndiProperty","jndiPropertyValue")){
 				document.forms[0].action.value = 'create';
 	 			document.forms[0].submit();
			}
 	 	}		
	}
	setTitle('<bean:message bundle="driverResources" key="driver.driver"/>');
</script>

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
										bundle="driverResources" key="driver.crestel.ocsv.translation" />
								</td>
							</tr>

							<tr>
								<td align="left" class="labeltext" valign="top" colspan="4">&nbsp;</td>
							</tr>
							<tr>
								<td colspan="4"><html:form
										action="/createDiameterCrestelOCSv2Driver">
										<html:hidden name="diameterCrestelOCSv2DriverForm" property="action" value="create" />
										<html:hidden name="diameterCrestelOCSv2DriverForm" property="driverInstanceName" />
										<html:hidden name="diameterCrestelOCSv2DriverForm" property="driverInstanceDesp" />
										<html:hidden name="diameterCrestelOCSv2DriverForm" property="driverRelatedId" />

										<table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0" cellpadding="0" cellspacing="0">
											<tr>
												<td align="left" class="captiontext" valign="top" colspan="1" width="25%">
													<bean:message bundle="driverResources"
														key="driver.translation.rating.transmapconf" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="cresteldriver.transaltionmappingconfiguration" 
																	header="driver.translation.rating.transmapconf"/>
												</td>
												<td align="left" class="labeltext" valign="top">
														<html:select
															tabindex="1" styleId="translationMapConfigId"
															property="translationMapConfigId" size="1"
															style="width: 130px;">
															<html:option value="0">--Select--</html:option>
																<html:optionsCollection
																	name="diameterCrestelOCSv2DriverForm"
																	property="translationMappingConfDataList" label="name"
																	value="translationMapConfigId" />
														</html:select><font color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" colspan="1" width="25%">
													<bean:message bundle="driverResources" key="driver.translation.rating.noofinstance" /> 
														<ec:elitehelp headerBundle="driverResources" 
															text="cresteldriver.numberofinstance" 
																header="driver.translation.rating.noofinstance"/> 
												</td>
												<td align="left" class="labeltext" valign="top" nowrap="nowrap">
														<html:text tabindex="2"
														styleId="instanceNumber" property="instanceNumber"
														size="30" maxlength="60" style="width:250px" /><font
													color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top" colspan="4">
													&nbsp;</td>
											</tr>

											<tr>
												<td align="left" class="tblheader-bold" valign="top" colspan="4">
													<bean:message bundle="driverResources" key="driver.translation.rating.jndiprops" /></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" colspan="4">
												<input tabindex="3" type="button"
													class="light-btn" 	onclick='addRow("dbMappingTable","mappingtbl");'
													value="Add JNDI Property" /></td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top" colspan="4">
													&nbsp;</td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top" colspan="4">
													<table cellSpacing="0" cellPadding="0" width="70%"
														border="0" id="mappingtbl" class="captiontext">
														<tr>
															<td align="left" class="tblheader" valign="top" id="tbl_attrid">
																<bean:message bundle="driverResources" key="driver.translation.rating.property" />
															</td>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources" key="driver.translation.rating.value" />
															</td>
															<td align="left" class="tblheader" valign="top"
																width="5%">Remove</td>
														</tr>
														<logic:iterate id="obj" name="diameterCrestelOCSv2DriverForm" property="defaultRatingDriverPropsDataList" type="com.elitecore.elitesm.datamanager.servermgr.drivers.ratingdriver.data.RatingDriverPropsData">
															<tr>
																<td class="tblfirstcol">
																	<input class="noborder" type="text" name="jndiProperty" maxlength="1000" size="28" style="width: 100%" tabindex="10" value='<bean:write name="obj" property="name"/>' /></td>
																<td class="tblrows">
																	<input class="noborder" type="text" name="jndiPropertyValue" maxlength="1000" size="28" style="width: 100%" tabindex="10" value='<bean:write name="obj" property="value"/>' /></td>
																<td class="tblrows" align="center" colspan="3">
																	<img value="top" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" tabindex="10" /></td>
															</tr>
														</logic:iterate>
													</table>
												</td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top" colspan="4">
													&nbsp;</td>
											</tr>

											<tr>
												<td class="btns-td" valign="middle" colspan="4"><input
													type="button" tabindex="6" value="Previous "
													onclick="history.go(-1)" class="light-btn" /> <input
													type="button" tabindex="7" name="c_btnCreate"
													id="c_btnCreate2" value="Create" class="light-btn"
													onclick="validateForm()"> <input type="reset"
													tabindex="8" name="c_btnDeletePolicy"
													onclick="javascript:window.location.href='<%=basePath%>/initSearchDriver.do?'"
													value="Cancel" class="light-btn" tabindex="7" /></td>
											</tr>
										</table>
									</html:form></td>
							</tr>
						</table>
					</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
			</table>
		</td>
	</tr>
</table>
<!-- Mapping Table Row template -->
<table style="display: none;" id="dbMappingTable">
	<tr>
		<td class="tblrows"><input class="noborder" type="text"
			name="jndiProperty" maxlength="1000" size="28" style="width: 100%"
			tabindex="10" /></td>
		<td class="tblrows"><input class="noborder" type="text"
			name="jndiPropertyValue" maxlength="1000" size="28" style="width: 100%"
			tabindex="10" /></td>
		<td class="tblrows" align="center" colspan="3"><img value="top"
			src="<%=basePath%>/images/minus.jpg" class="delete"
			style="padding-right: 5px; padding-top: 5px;" height="14"
			tabindex="10" /></td>
	</tr>
</table>
