<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.netvertexsm.web.servermgr.drivers.form.EditDriverInstanceForm"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" %>
<script type="text/javascript">

$(document).ready(function(){
	$("#name").focus();
});

function validateForm(){	
	verifyName();
	var flag = false;
		if(isNull(document.forms[0].name.value)){
			alert('Name must be specified.');
			document.forms[0].name.focus();			
		} else if(!isValidName) {
			alert('Enter Valid Driver Name');
			document.forms[0].name.focus();		
		} else{ 			 
 			flag = true;
	 	}		
	return flag;
}

var isValidName;
function verifyFormat (){
	var searchName = document.getElementById("name").value;
	isValidName = callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.DRIVER%>',searchName:searchName,mode:'update',id:'<%=driverInstanceData.getDriverInstanceId()%>'},'verifyNameDiv');
}
function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.DRIVER%>',searchName:searchName,mode:'update',id:'<%=driverInstanceData.getDriverInstanceId()%>'},'verifyNameDiv');
}
setTitle('<bean:message bundle="driverResources" key="driver.driver"/>');
	
</script>
<html:form action="/editDriverBasicDetails" styleId="mainform"  onsubmit="return validateForm();"  >
  <html:hidden name="editDriverInstanceForm" styleId="driverInstanceId" property="driverInstanceId"/>
  <table width="97%" align="right" border="0" cellpadding="0" cellspacing="0" class="box">
  	<tr>
		<td align="left" class="tblheader-bold" valign="top" colspan="2" style="padding-left: 2em">
		<bean:message key="general.update.basicdetails" />
		</td>
	</tr>
	 <tr>
		<td align="left" class="captiontext" valign="top" width="10%">
			<bean:message bundle="driverResources" key="driver.driverinstance.name" />
			<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="driverResources" key="driver.driverinstance.name"/>','<bean:message bundle="driverResources" key="driver.driverinstance.name" />')"/>
			</td> 
			<sm:nvNameField maxLength="60" size="30" value="${editDriverInstanceForm.name}"/>	
	</tr>
	<tr>
		<td align="left" class="captiontext" valign="top" width="10%">
			<bean:message bundle="driverResources" key="driver.driverinstance.decription" /></td>
		<td align="left" valign="top" width="32%">
			<html:textarea styleId="description" property="description" cols="40" rows="4" tabindex="2"/></td>							
	</tr>			

	<tr> 
		 <td></td>
         <td class="btns-td" valign="middle">
         	<html:submit styleClass="light-btn" styleId="c_btnCreate2"  tabindex="3" value="Update"  />                           
            <input type="reset" onclick="javascript:window.location.href='<%=basePath%>/initSearchDriverInstance.do?'" value="Cancel" class="light-btn" tabindex="4"/>                                                       
	     </td>
     </tr>
 </table>
						
</html:form>




