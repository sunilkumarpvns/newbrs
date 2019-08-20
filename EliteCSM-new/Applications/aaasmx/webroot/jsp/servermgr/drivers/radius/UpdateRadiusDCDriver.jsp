<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.web.driver.radius.forms.UpdateRadiusDCDriverForm" %>
<%@page import="java.util.List" %>
<%
    UpdateRadiusDCDriverForm updateRadiusDCDriverForm = (UpdateRadiusDCDriverForm) request.getAttribute("updateRadiusDCDriverForm");
	DriverInstanceData driverInstanceData = (DriverInstanceData)request.getSession().getAttribute("driverInstance");
%>

<script>
	var isValidName;
	function verifyName() {
		var searchName = document.getElementById("driverinstname").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.DRIVER%>',searchName,'update','<%=driverInstanceData.getDriverInstanceId()%>','verifyNameDiv');
	}

	function validateForm(){
		if(!isValidName) {
			alert('Enter Valid Name');
			document.forms[0].driverinstname.focus();
			return false;
		}else if(isNull(document.forms[0].driverInstanceName.value)){
 			alert('Name must be specified');
 			document.forms[0].driverInstanceName.focus();
 		}else if(!isValidName) {
 			alert('Enter Valid Driver Name');
 			document.forms[0].driverInstanceName.focus();
 		}if(isNull(document.forms[0].disConnectUrl.value)){
			document.forms[0].disConnectUrl.focus();
			alert('Disconnect URL must be specified');
		}else if(document.forms[0].translationMapConfigId.value == '0'){
			document.forms[0].translationMapConfigId.focus();
			alert('Translation Mapping Configuration must be selected');
		}else{
			document.forms[0].action.value = 'Update';
			document.forms[0].submit();
		}
	 }
	
</script>

<html:form action="/updateRadiusDCDriver">
	<html:hidden property="action" />
	<html:hidden property="driverInstanceId" />
	<html:hidden property="auditUId"/>
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" height="15%">
						
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="5">
								<bean:message bundle="driverResources" key="driver.details" />
							</td>
						</tr>
		
						<tr>
							<td align="left" class="captiontext" valign="top" width="30%">
								<bean:message bundle="driverResources" key="driver.instname" />
									<ec:elitehelp headerBundle="driverResources" 
										text="createdriver.name" header="driver.instname"/>
							</td>
							<td align="left" class="labeltext" valign="top" nowrap="nowrap">
								<html:text styleId="driverinstname" onkeyup="verifyName();" property="driverInstanceName" size="30" maxlength="60" style="width:250px" tabindex="1"/>
								<font color="#FF0000"> *</font>
								<div id="verifyNameDiv" class="labeltext"></div>
							</td>
						</tr>
		
						<tr>
							<td align="left" class="captiontext" valign="top" width="30%">
								<bean:message bundle="driverResources" key="driver.instdesc" />
									<ec:elitehelp headerBundle="driverResources" 
										text="createdriver.description" header="driver.instdesc"/>
							</td>
							<td align="left" class="labeltext" valign="top">
								<html:textarea property="driverDesp" styleId="driverDesp" rows="4" cols="50" tabindex="2" style="width:250px"></html:textarea>
							</td>
						</tr>
						<tr>
							<td align="left" class="captiontext" valign="top" width="30%">
								<bean:message bundle="driverResources" key="driver.dc.disconnecturl"/>
									<ec:elitehelp headerBundle="driverResources" 
										text="diameterchargingdriver.disconnecturl"
											header="driver.dc.disconnecturl"/>									
							</td>
							
							<td align="left" class="labeltext" valign="top">
								<html:text styleId="disConnectUrl" property="disConnectUrl" size="100" maxlength="100" style="width:250px" tabindex="3"/>
								<font color="#FF0000"> *</font>
							</td>
						</tr>
						<tr>
							<td align="left" class="captiontext" valign="top" colspan="1" width="30%">
								<bean:message bundle="driverResources" key="driver.dc.transmapconf"/>
									<ec:elitehelp headerBundle="driverResources" 
										text="diameterchargingdriver.transmapconf" 
											header="driver.dc.transmapconf"/>
							</td>
							<td align="left" class="labeltext" valign="top" >
					    		<bean:define id="translationMappingConfDataLst" name="updateRadiusDCDriverForm" property="translationMappingConfDataList" type="java.util.List" ></bean:define>
					   			<html:select name="updateRadiusDCDriverForm" styleId="translationMapConfigId" property="translationMapConfigId" size="1" style="width: 132px;" tabindex="4">
								   <html:option value="0" >--Select--</html:option>
						   		   <html:options collection="translationMappingConfDataLst" property="translationMapConfigId" labelProperty="name" />
								</html:select>
								<font color="#FF0000"> *</font>      
							</td>
						</tr>
						<tr>
							<td class="small-gap" colspan="2">
								&nbsp;
							</td>
						</tr>
									
                        <tr>
							<td class="btns-td" valign="middle">
								&nbsp;
							</td>
							<td class="btns-td" valign="middle" colspan="2">
								<input type="button" name="c_btnCreate" onclick="validateForm()" id="c_btnCreate2" value="Update" class="light-btn" tabindex="29">
								<input type="reset" name="c_btnCancel" onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?/>'" value="Cancel" class="light-btn" tabindex="30"> 
							</td>
					 </tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
