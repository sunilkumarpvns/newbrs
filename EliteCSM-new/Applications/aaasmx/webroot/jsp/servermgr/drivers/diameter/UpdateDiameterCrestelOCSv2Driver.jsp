<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>

<bean:define id="driverInstanceData" scope="request"
	name="driverInstanceData"
	type="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"></bean:define>
<bean:define id="crestelRatingDriverData" scope="request"
	name="crestelRatingDriverData"
	type="com.elitecore.elitesm.datamanager.servermgr.drivers.ratingdriver.data.CrestelRatingDriverData"></bean:define>
	
<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script>
var isValidName = true;
	function verifyName() {
		var searchName = document.getElementById("name").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.DRIVER%>',searchName,'update','<%=driverInstanceData.getDriverInstanceId()%>','verifyNameDiv');
	}

	$(document).ready(function(){
		$('#mappingtbl td img.delete').live('click',function() {	
				 $(this).parent().parent().remove(); 
			});
	});
	function validateForm(){	
		if(isNull(document.forms[0].driverInstanceName.value)){
			alert('Instance Name must be selected.');
			return;
		}else if(document.forms[0].translationMapConfigId.value == '0'){
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
	 				document.forms[0].action.value = 'update';
	 	 			document.forms[0].submit();
				}
		 	}		
	}

	setTitle('<bean:message bundle="driverResources" key="driver.translation.rating.title"/>');
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="tblheader-bold" height="20%" colspan="2"><bean:message
							bundle="driverResources" key="driver.view" /></td>
				</tr>

				<tr>
					<td align="left" class="tblheader-bold" valign="top" colspan="2">
						<bean:message bundle="driverResources"
							key="driver.driverinstancedetails" />
					</td>
				</tr>

				<tr>
					<td class="tblfirstcol" width="20%" height="20%">
						<bean:message bundle="driverResources" key="driver.instname" />
					</td>
					<td class="tblcol" width="30%" height="20%"><bean:write
							name="driverInstanceData" property="name" />&nbsp;</td>
				</tr>

				<tr>
					<td class="tblfirstcol" width="20%" height="20%">
						<bean:message bundle="driverResources" key="driver.instdesc" />
					</td>
					<td class="tblcol" width="30%" height="20%"><bean:write
							name="driverInstanceData" property="description" />&nbsp;</td>
				</tr>
				<tr>
					<td align="left" class="tblheader-bold" valign="top" colspan="2">
						Update <bean:write name="driverInstanceData"
							property="driverTypeData.name" />
					</td>
				</tr>

				<tr>
					<td colspan="2"><html:form
							action="/updateDiameterCrestelOCSv2Driver">
							<html:hidden name="diameterCrestelOCSv2DriverForm"
								property="action" value="update" />
							<html:hidden name="diameterCrestelOCSv2DriverForm"
								property="driverInstanceId" />
							<html:hidden name="diameterCrestelOCSv2DriverForm"
								property="auditUId" />
							<table width="100%">
								<tr>
									<td align="left" class="captiontext" valign="top" colspan="1" width="25%">
										<bean:message bundle="driverResources"
											key="driver.instname" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="createdriver.name" header="driver.instname"/>
									</td>
									<td align="left" class="labeltext" valign="top" nowrap="nowrap">
										<html:text tabindex="1" styleId="name"
											property="driverInstanceName" size="30" maxlength="60"
											onkeyup="verifyName();" style="width:250px" /><font
										color="#FF0000"> *</font>
										<div id="verifyNameDiv" class="labeltext"></div>
									</td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" colspan="1" width="25%">
										<bean:message bundle="driverResources"
											key="driver.driverinstancedetails" />
												<ec:elitehelp headerBundle="driverResources" 
													text="createdriver.description" header="driver.instdesc"/>
									</td>
									<td align="left" class="labeltext" valign="top" nowrap="nowrap">
										<html:text tabindex="2" property="driverInstanceDesp"
											size="30" maxlength="255" style="width:250px" />
									</td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" colspan="1" width="25%">
										<bean:message bundle="driverResources"
											key="driver.translation.rating.transmapconf" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="cresteldriver.transaltionmappingconfiguration" 
														header="driver.translation.rating.transmapconf"/>
									</td>
									<td align="left" class="labeltext" valign="top"><html:select
											tabindex="3" styleId="translationMapConfigId"
											property="translationMapConfigId" size="1"
											style="width: 250px;">
											<html:option value="0">--Select--</html:option>
											<html:optionsCollection name="diameterCrestelOCSv2DriverForm"
												property="translationMappingConfDataList" label="name"
												value="translationMapConfigId" />
										</html:select><font color="#FF0000"> *</font></td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" colspan="1" width="25%">
										<bean:message bundle="driverResources"
											key="driver.translation.rating.noofinstance" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="cresteldriver.numberofinstance" 
														header="driver.translation.rating.noofinstance"/>
									</td>
									<td align="left" class="labeltext" valign="top" nowrap="nowrap">
										<html:text tabindex="4" styleId="instanceNumber"
											property="instanceNumber" size="30" maxlength="60"
											style="width:250px" /><font color="#FF0000"> *</font>
									</td>
								</tr>

								<tr>
									<td align="left" class="labeltext" valign="top" colspan="4">
										&nbsp;
								</tr>
								</tr>

								<tr>
									<td align="left" class="tblheader-bold" valign="top"
										colspan="4"><bean:message bundle="driverResources"
											key="driver.translation.rating.jndiprops" /></td>
								</tr>

								<tr>
									<td class="captiontext" valign="top" colspan="4"><input
										type="button" tabindex="5" class="light-btn"
										onclick='addRow("dbMappingTable","mappingtbl");' value="Add JNDI Property" />
									</td>
								</tr>

								<tr>
									<td align="left" class="labeltext" valign="top" colspan="4">
										&nbsp;</td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" colspan="4">
										<table cellSpacing="0" cellPadding="0" width="70%" border="0"
											id="mappingtbl">
											<tr>
												<td align="left" class="tblheader" valign="top"
													id="tbl_attrid"><bean:message bundle="driverResources"
														key="driver.translation.rating.property" /></td>
												<td align="left" class="tblheader" valign="top"><bean:message
														bundle="driverResources"
														key="driver.translation.rating.value" /></td>
												<td align="left" class="tblheader" valign="top" width="5%">Remove</td>
											</tr>
											<logic:iterate id="obj" name="crestelRatingDriverData" property="jndiPropValMapList" type="com.elitecore.elitesm.datamanager.servermgr.drivers.ratingdriver.data.RatingDriverPropsData">
												<tr>
														<td class="tblfirstcol">
															<input class="noborder" type="text" name="jndiProperty" maxlength="1000" size="28" style="width: 100%" tabindex="10" value='<bean:write name="obj" property="name"/>' />
														</td>
														<td class="tblrows">
															<input class="noborder" type="text" name="jndiPropertyValue" maxlength="1000" size="28" style="width: 100%" tabindex="10" value='<bean:write name="obj" property="value"/>' />
														</td>
														<td class="tblrows" align="center" colspan="3">
															<img value="top" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" tabindex="10" />
														</td>
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
										type="button" tabindex="8" name="c_btnUpdate" id="c_btnUpdate"
										value="Update" class="light-btn" onclick="validateForm()">
										<input type="reset" tabindex="9" name="c_btnDeletePolicy"
										onclick="javascript:window.location.href='<%=basePath%>/initSearchDriver.do?'"
										value="Cancel" class="light-btn" tabindex="7" /></td>
								</tr>
								</html:form>
							</table></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<!-- Mapping Table Row template -->
<table style="display: none;" id="dbMappingTable">
	<tr>
		<td class="tblfirstcol"><input class="noborder" type="text"
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
