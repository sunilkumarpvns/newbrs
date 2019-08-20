<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.List" %>
<%@page import="java.util.ArrayList"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>

<%
    String basePath = request.getContextPath();
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
	function validateForm(){	
		if(isNull(document.forms[0].disConnectUrl.value)){
			document.forms[0].disConnectUrl.focus();
			alert('Disconnect URL must be specified');
		}else if(document.forms[0].translationMapConfigId.value == '0'){
			document.forms[0].translationMapConfigId.focus();
			alert('Translation Mapping Configuration must be selected');
		}else{
			document.forms[0].action.value = 'nextMapping';
			document.forms[0].submit();
		}
	}
	setTitle('<bean:message bundle="driverResources" key="driver.driver"/>');
</script>

<html:form action="/createRadiusDCDriver">
	
	<html:hidden name="createRadiusDCDriverForm" property="action"/>
	<html:hidden name="createRadiusDCDriverForm" property = "driverInstanceName" />
	<html:hidden name="createRadiusDCDriverForm" property = "driverDesp" />
	<html:hidden name="createRadiusDCDriverForm" property = "driverRelatedId" />
		  				

	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
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
							<td class="table-header">		
							<bean:message bundle="driverResources" key="driver.dc.createdcdriver"/></td> 
					</tr>
					<tr>
						<td class="small-gap" colspan="2">
							&nbsp;
						</td>
					</tr>
				<tr>
						<td valign="middle" colspan="2">
							<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
						
						<tr>
							<td align="left" class="captiontext" valign="top">
								<bean:message bundle="driverResources" key="driver.dc.disconnecturl"/>
									<ec:elitehelp headerBundle="driverResources" 
										text="diameterchargingdriver.disconnecturl"
											header="driver.dc.disconnecturl"/>
							</td>
							
							<td align="left" class="labeltext" valign="top">
								<html:text styleId="disConnectUrl" property="disConnectUrl" size="100" maxlength="100" style="width:250px" tabindex="12"/><font color="#FF0000"> *</font>   
							</td>
						</tr>
						
						<tr>
							<td align="left" class="captiontext" valign="top" colspan="1" width="25%">
								<bean:message bundle="driverResources" key="driver.dc.transmapconf"/>
									<ec:elitehelp headerBundle="driverResources" 
										text="diameterchargingdriver.transmapconf" 
											header="driver.dc.transmapconf"/>
							</td>
							<td align="left" class="labeltext" valign="top" >
					    		<bean:define id="translationMappingConfDataLst" name="createRadiusDCDriverForm" property="translationMappingConfDataList" type="java.util.List" ></bean:define>
					   				<html:select name="createRadiusDCDriverForm" styleId="translationMapConfigId" property="translationMapConfigId" size="1" style="width: 130px;" tabindex="14">
										 <html:option value="0" >--Select--</html:option>
						   				  <html:options collection="translationMappingConfDataLst" property="translationMapConfigId" labelProperty="name" />
									</html:select><font color="#FF0000"> *</font>      
							</td>
						</tr>
				 		<tr>
							<td class="btns-td" valign="middle">
								&nbsp;
							</td>
							<td class="btns-td" valign="middle" colspan="2">
								<input type="button" name="c_btnCreate" onclick="validateForm()" id="c_btnCreate2" value="Create" class="light-btn" tabindex="27">
								<input type="reset" name="c_btnCancel" onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?/>'" value="Cancel" class="light-btn" tabindex="28"> 
							</td>
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