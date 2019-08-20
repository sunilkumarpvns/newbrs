<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData"%>
<%@page import="com.elitecore.elitesm.web.driver.radius.forms.UpdateCrestelChargingDriverForm"%>
<%@ page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>

<%
	DriverInstanceData driverInstanceData = (DriverInstanceData)request.getSession().getAttribute("driverInstance");
%>
	
<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script language="javascript">
$(document).ready(function(){
	$('#mappingtbl td img.delete').live('click',function() {	
			 $(this).parent().parent().remove(); 
		});
});
function validateForm(){
		if(isNull(document.forms[0].driverInstanceName.value)){
			alert('Instance Name must be specified');
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
var isValidName = true;
function verifyName() {
	var searchName = document.getElementById("driverinstname").value;
	verifyInstanceName('<%=InstanceTypeConstants.DRIVER%>',searchName,'update','<%=driverInstanceData.getDriverInstanceId()%>','verifyNameDiv');
}	
</script>

<html:form action="/updateCrestelChargingDriver">
	<html:hidden name="updateCrestelChargingDriverForm" property="action" />
	<html:hidden name="updateCrestelChargingDriverForm"
		property="driverRelatedId" />
	<html:hidden name="updateCrestelChargingDriverForm"
		property="driverInstanceId" />
	<html:hidden name="updateCrestelChargingDriverForm"
		property="auditUId" />


	<table cellSpacing="0" cellPadding="0" width="100%" border="0">

		<tr>
			<td colspan="4">
				<table width="100%" name="c_tblCrossProductList"
					id="c_tblCrossProductList" align="right" border="0">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="driverResources"
								key="driver.driverinstancedetails" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="35%">
							<bean:message bundle="driverResources" key="driver.instname" />
								<ec:elitehelp headerBundle="driverResources" 
									text="createdriver.name" header="driver.instname"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%"><html:text
								styleId="driverinstname" onkeyup="verifyName();"
								property="driverInstanceName" size="30" maxlength="60"
								style="width:250px" tabindex="1" /><font color="#FF0000">
								*</font>
							<div id="verifyNameDiv" class="labeltext"></div></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="35%">
							<bean:message bundle="driverResources" key="driver.instdesc" />
								<ec:elitehelp headerBundle="driverResources" 
									text="createdriver.description" header="driver.instdesc"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%"><html:text
								styleId="driverinstdesc" property="driverInstanceDesp" size="30"
								maxlength="60" style="width:250px" tabindex="2" /></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" colspan="1" width="25%">
							<bean:message bundle="driverResources"
								key="driver.translation.crestelcharging.transmapconf" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="routingconf.transmapconf" 
											header="driver.translation.crestelcharging.transmapconf"/>
						</td>
						<td align="left" class="labeltext" valign="top"><bean:define
								id="translationMappingConfDataList"
								name="updateCrestelChargingDriverForm"
								property="translationMappingConfDataList"></bean:define> <html:select
								name="updateCrestelChargingDriverForm" tabindex="4"
								styleId="translationMapConfigId"
								property="translationMapConfigId" size="1" style="width: 130px;">
								<html:option value="0">--Select--</html:option>
								<html:options collection="translationMappingConfDataList"
									property="translationMapConfigId" labelProperty="name" />
							</html:select><font color="#FF0000"> *</font></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources"
								key="driver.crestel.charging.noofinstance" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="routingconf.noofInstance" 
											header="driver.crestel.charging.noofinstance"/>
							<font color="#FF0000"> *</font></td>


						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text styleId="instanceNumber" tabindex="3"
								property="instanceNumber" size="30" maxlength="60"
								style="width:250px" />
						</td>
					</tr>

					<tr>
						<td align="left" class="labeltext" valign="top" colspan="4">
							&nbsp;</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="4">
							<bean:message bundle="driverResources"
								key="driver.translation.crestelcharging.jndiprops" />
						</td>
					</tr>
					<tr>
						<td class="captiontext" valign="top" colspan="4"><input
							type="button" tabindex="5" class="light-btn"
							onclick='addRow("dbMappingTable","mappingtbl");' value="Add JNDI Property" /></td>
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
									<td align="left" class="tblheader" valign="top" id="tbl_attrid"><bean:message
											bundle="driverResources"
											key="driver.translation.rating.property" /></td>
									<td align="left" class="tblheader" valign="top"><bean:message
											bundle="driverResources"
											key="driver.translation.rating.value" /></td>
									<td align="left" class="tblheader" valign="top" width="5%">Remove</td>
								</tr>
								<logic:iterate id="obj" name="crestelChargingDriverData" property="jndiPropValMapList" type="com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data.CrestelChargingDriverPropsData">
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
				</table>
			</td>
		</tr>
		<tr>
			<td class="btns-td" valign="middle">&nbsp;</td>
			<td class="btns-td" valign="middle"><input type="button"
				name="c_btnCreate" id="c_btnCreate2" value=" Update "
				class="light-btn" onclick="validateForm();" tabindex="6"> <input
				type="reset" name="c_btnDeletePolicy" tabindex="7"
				onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?'"
				value="Cancel" class="light-btn"></td>
		</tr>

	</table>
</html:form>
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





