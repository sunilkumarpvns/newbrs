<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.netvertexsm.web.servermgr.alert.forms.CreateAlertListenerForm"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>

<%
   CreateAlertListenerForm createAlertListenerForm=(CreateAlertListenerForm)request.getAttribute("createAlertListenerForm");
%>

<script>
var isValidName;
$(document).ready(function(){
	setTitle('<bean:message bundle="servermgrResources" key="servermgr.alert.alertlistener"/>');
});
function validateNext(){
	verifyName();
	var flag = false;
	if(isNull(document.forms[0].name.value)){
		document.forms[0].name.focus();
		alert('Enter valid Alert Listener Name');
	}else if(!isValidName) {
		alert('Enter Valid Name');
		document.forms[0].name.focus();		
	} else if(document.forms[0].typeId.value =='0'){
		document.forms[0].typeId.focus();
		alert('Listener Type must be selected');
	} else{
	     flag = true;
    }
	return flag;
}	

function isNumber(val){
	nre= /^\d+$/;
	var regexp = new RegExp(nre);
	if(!regexp.test(val))
	{
		return false;
	}
	return true;
}

function verifyFormat (){
	var searchName = document.getElementById("name").value;
	isValidName = callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.ALERT_CONFIGURATION%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}
function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.ALERT_CONFIGURATION%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}
</script>

<html:form action="/createAlertListener" onsubmit="return validateNext();" >
<table cellpadding="0" cellspacing="0" border="0" width="100%">	
	
		<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
		<tr>
			<td width="10">
				&nbsp;
			</td>
			<td class="box" cellpadding="0" cellspacing="0" border="0" width="100%">
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td class="table-header" colspan="5"><bean:message bundle="servermgrResources" key="servermgr.alert.createheader"/>
						</td>
					</tr>
					<tr>
						<td class="small-gap" colspan="2">
							&nbsp;
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle" colspan="2">
							<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
								
								<tr>
									<td align="left" class="labeltext" valign="top" >
										<bean:message bundle="servermgrResources" key="servermgr.alert.name"/>
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="servermgrResources" key="servermgr.alert.listenerName"/>','<bean:message bundle="servermgrResources" key="servermgr.alert.listenerName" />')"/>
									</td>
									<sm:nvNameField maxLength="60" size="25" />
								</tr>
							
							  <tr>
								<td align="left" class="labeltext" valign="top" width="10%">
									<bean:message bundle="servermgrResources" key="servermgr.alert.listenerType"/>
									<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="alertlistener.type"/>','<bean:message bundle="servermgrResources" key="servermgr.alert.listenerType"/>')"  />																				
								</td>
								<td align="left" class="labeltext" valign="top" width="32%">														
									<html:select property="typeId">
									<html:option value="0">Select</html:option>
									<html:optionsCollection name="createAlertListenerForm" property="availableListenerTypes" label="typeName" value="typeId"/>
									</html:select>
								</td>							
					 	   </tr>	
						  							 
		    			   <tr> 
							  <td align="left" class="labeltext" valign="top" >&nbsp;</td>
							  <td align="left" class="labeltext" valign="top" colspan="2" >&nbsp;</td>
						   </tr>
								
								<tr>
									<td class="btns-td" valign="middle">
										&nbsp;
									</td>
									<td class="btns-td" valign="middle" colspan="2">
										<html:submit styleClass="light-btn" styleId="c_btnCreate2"  value="Next"  />										
									 	<input type="reset" name="c_btnCancel" onclick="javascript:location.href='<%=basePath%>/initSearchAlertListener.do?/>'" value="Cancel" class="light-btn"> 
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		
	<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
  
</table>		
</html:form>
