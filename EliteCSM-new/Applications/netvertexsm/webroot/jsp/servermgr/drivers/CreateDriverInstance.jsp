<%@ include file="/jsp/core/includes/common/Header.jsp" %>

<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverTypeData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.ServiceTypeData" %>
<%@ page import="com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager" %>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="java.util.List" %>

<script language = "javascript">

var isValidName;
function validate(){
	 verifyName();
	var flag = false;
	if(isNull(document.forms[0].name.value)){
		alert('Name must be specified');
		document.forms[0].name.focus();
	}else if(!isValidName) {
		alert('Enter Valid Driver Name');
		document.forms[0].name.focus();		
	}else if(document.forms[0].driverTypeId.value == 0){
		alert('Select atleast one driver.');
	}else{
		document.forms[0].action.value = 'next';
		flag = true;
	}			 	
	return flag;
}

function verifyFormat (){
	var searchName = document.getElementById("name").value;
	isValidName = callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.DRIVER%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}
function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.DRIVER%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}
$(document).ready(function(){
	setTitle('<bean:message bundle="driverResources" key="driver"/>');
	$("#name").focus();
	$("#description").attr('maxlength','255');

});

</script>
 
<html:form action="/createDriverInstance" onsubmit="return validate()" > 
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
				<bean:message bundle="driverResources" key="driver.driverinstance.create"/>
			</td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="97%" id="c_tblCrossProductList" align="right" border="0" > 
			   	
				<tr>
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="driverResources" key="driver.driverinstance.name" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="driverResources" key="driver.driverinstance.name"/>','<bean:message bundle="driverResources" key="driver.driverinstance.name" />')"/>
						</td> 
					<sm:nvNameField maxLength="60" size="30"/>
				</tr>
				<tr>
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="driverResources" key="driver.driverinstance.decription" /></td>
					<td align="left" class="labeltext" valign="top" width="32%">
						<html:textarea styleId="description" property="description" cols="40" rows="4" tabindex="2"/></td>							
				</tr>						
				<tr>
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="driverResources" key="driver.driverinstance.drivertype" /></td>
					<td align="left" class="labeltext" valign="top" width="32%">
						<html:select name="createDriverInstanceForm" styleId="driverTypeId" property="driverTypeId" tabindex="3" >
							<html:option value="0"><bean:message bundle="driverResources" key="driver.select"/></html:option>
							<logic:iterate id="objservice" name="createDriverInstanceForm" type="ServiceTypeData" property="serviceTypeList">
								<optgroup  label="<%=objservice.getDisplayName()%>" class="labeltext">
							<logic:iterate id="driverTypes"  name="createDriverInstanceForm" property="driverTypeList" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverTypeData">
								<html:option value="<%=Long.toString(driverTypes.getDriverTypeId())%>">
								<bean:write name="driverTypes" property="name"/></html:option>
							</logic:iterate>
							</optgroup>	
							</logic:iterate>
						</html:select>
					</td>
				</tr>
				<tr>
					<td class="btns-td" valign="middle">&nbsp;</td>
					<td class="btns-td" valign="middle" colspan="2">
						<html:submit styleClass="light-btn" styleId="c_btnCreate2"  value="   Next   "   />												
						<input type="button" align="left" value=" Cancel " class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchDriverInstance.do?/>'"/>
					</td>
				</tr>				  					 
			   </table>  
			</td>
		  </tr>	 
		  <tr> 
			<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr>
           
		</table> 
	  </td> 
	</tr> 
  <%@include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table> 
</html:form> 