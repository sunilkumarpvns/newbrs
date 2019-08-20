

<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceData"%>

<% 
	String localBasePath = request.getContextPath();
%>

<script>
function validateAdminInterfaceIP()
{
        //Check for valid IPAddress
        var ipre = /((^\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\s*$)|(^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$))/;
        if(document.forms[0].adminInterfaceIP.value != null && document.forms[0].adminInterfaceIP.value!="" && !ipre.test(document.forms[0].adminInterfaceIP.value)){        	
//				alert('Admin Interface IP  is not valid. Please Enter valid data.');
				document.forms[0].adminInterfaceIP.focus();
				return false;
		}
		else {
			return true;
		}
}
		
function validateUpdate(){ 

	if(isNull(document.forms[0].adminInterfaceIP.value))
	{
	alert('Admin Interface IP is a compulsory field Please enter required data in this field.');
	}else if(validateAdminInterfaceIP() == false){
		document.forms[0].adminInterfaceIP.focus();	
		alert('Admin Interface IP Address value is not proper');	
	}else if(isNull(document.forms[0].adminInterfacePort.value))
	{
	alert('Admin Interface Port is a compulsory field Please enter required data in this field.');
	}else if(validatePort(document.forms[0].adminInterfacePort.value) == false ){
		document.forms[0].adminInterfacePort.focus();
		alert('Admin Interface Port value is not proper');
	}
	else if(document.getElementById('chkUpdateStarupConf').checked==true){
	/*~~~~~START validing sever startup config~~~~~~~~~*/
	if(isNull(document.forms[0].shellPrompt.value)){
		document.forms[0].shellPrompt.focus();
		alert('Shell Prompt must be specified');
	}else if(isNull(document.forms[0].userName.value)){
		document.forms[0].userName.focus();
		alert('User Name must be specified');
	}else if(isNull(document.forms[0].password.value)){
		document.forms[0].password.focus();
		alert('password must be specified');
	}else if(isNull(document.forms[0].communicationPort.value)){
		document.forms[0].communicationPort.focus();
		alert('Communication Port be specified');
	}
	else if(validatePort(document.forms[0].communicationPort.value) == false )
	{
	    document.forms[0].communicationPort.focus();
		alert('Communication Port is not valid');
	}
	else if(isNull(document.forms[0].failureMsg.value)){
		document.forms[0].failureMsg.focus();
		alert('Failure Msg be specified');
	}else if(isNull(document.forms[0].operationTimeOut.value) == false  && IsNumeric(document.forms[0].operationTimeOut.value) == false){
		document.forms[0].operationTimeOut.focus();
		alert('Operatiron Timeout value is not proper');
	}else if(isNull(document.forms[0].loginPrompt.value)){
		document.forms[0].loginPrompt.focus();
		alert('Login Prompt must be specified');
	}else if(isNull(document.forms[0].passwordPrompt.value)){
		document.forms[0].passwordPrompt.focus();
		alert('Password Prompt must be specified');
	}else if(isNull(document.forms[0].shell.value)){
		document.forms[0].shell.focus();
		alert('Shell must be specified');
	}
	else
	{
		alert('Updating Admin Interface With Server Connection Details');
	    document.forms[0].action.value='update';
		document.forms[0].submit();
	}
	/*~~~~~END validing sever startup config~~~~~~~~~*/
	
 }else
	{
	    alert('Updating Admin Interface With out Server Connection Details');
	    document.forms[0].action.value='update';
		document.forms[0].submit();
	}

}



function validatePort(txt){
	// check for valid numeric port	 
	if(IsNumeric(txt) == true){
		if(txt >= 0 && txt<=65535){
			return true;
        }
	}else{
		return false;
    }
}

function IsNumeric(strString)
   //  check for valid numeric strings  
   {
   var strValidChars = "0123456789.-";
   var strChar;
   var blnResult = true;

   if (strString.length == 0) return false;

   //  test strString consists of valid characters listed above
   for (i = 0; i < strString.length && blnResult == true; i++)
      {
      strChar = strString.charAt(i);
      if (strValidChars.indexOf(strChar) == -1)
         {
         blnResult = false;
         }
      }
   return blnResult;
}

function chkStartupConf()
{

if(document.getElementById('chkUpdateStarupConf').checked==true)
{
alert('Server Connection Details Will be updated in Update Operation');
}
else
{
alert('Server Connection Details Will not be updated in Update Operation');
}

}
$(document).ready(function(){
	$("#adminInterfaceIP").focus();
});
</script>

<html:form action="/updateNetServerAdminInterfaceDetail">
<html:hidden name="updateNetServerAdminInterfaceDetailForm" styleId="action" property="action" />
<html:hidden name="updateNetServerAdminInterfaceDetailForm" styleId="netServerId" property="netServerId"/>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr> 
      <td valign="top" align="right"> 
       <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%">
		 <tr>
		  	<td>&nbsp;</td>
		 </tr>
		 <tr> 
			<td class="tblheader-bold" colspan="3"><bean:message bundle="servermgrResources" key="servermgr.changeadmininterfacedetails"/></td>
		 </tr>
	 	 <tr>
			<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.admininterfaceip"/></td>
			<td align="left" class="labeltext" valign="top" width="32%" >
				<html:text styleId="adminInterfaceIP" property="adminInterfaceIP" size="30" maxlength="60" onblur="trimvalue(this);"/><font color="#FF0000"> *</font>
			</td>
		 </tr>								
		 <tr>
			<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.admininterfaceport"/></td>
			<td align="left" class="labeltext" valign="top" width="32%" >
				<html:text styleId="adminInterfacePort" property="adminInterfacePort" size="30" maxlength="60"/><font color="#FF0000"> *</font>
			</td>
		 </tr>	
		 
		 
	   </table >
	 </td>
    </tr>
	<tr>
	  <td valign="top" align="right">
       <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right">
      	 <tr>
		 <td align="left" class="labeltext" valign="top" width="2%" >
				<html:checkbox styleId="chkUpdateStarupConf" property="chkUpdateStarupConf" value="1" onclick="chkStartupConf()"></html:checkbox>
		</td>
			<td align="left" class="labeltext" valign="top" width="90%"><bean:message bundle="servermgrResources" key="servermgr.admininterfaceport"/></td>
		 </tr>	
	  </table>
     </td>
   </tr>
   <tr>
   <td valign="top" align="right">
   <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right">
   <tr> 
      <td valign="top" align="right"> 
        <table id="startupBlock" width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
  				<td class="tblheader-bold" colspan="4"><bean:message bundle="servermgrResources" key="servermgr.server.connectiondetail"/></td>
					                                   <tr > 
                                                              <td align="left" class="labeltext" valign="top" width="18%" ><bean:message bundle="servermgrResources" key="servermgr.server.protocol"/></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
                                                               <html:select styleId="protocol" property="protocol" >
                                                              		<html:option value="Telnet"></html:option>
                                                              </html:select>
                                                              </td>
                                                        </tr><%--
                                                        
                                                        <tr > 
                                                              <td align="left" class="labeltext" valign="top" width="18%" ><bean:message bundle="servermgrResources"  key="servermgr.server.shell"/></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
                                                                       <html:text property="shell" size="25" maxlength="15"/><font color="#FF0000"> *</font> 
                                                              </td>
                                                        </tr>
                                                        
                                                         --%><tr > 
                                                              <td align="left" class="labeltext" valign="top" width="18%" ><bean:message bundle="servermgrResources" key="servermgr.server.shellprompt"/></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2">
                                                                  <html:text styleId="shellPrompt" property="shellPrompt" size="1" maxlength="1"/><font color="#FF0000">*</font> 
                                                              </td>
                                                        </tr>
                                                        <tr > 
                                                              <td align="left" class="labeltext" valign="top" width="18%" ><bean:message bundle="servermgrResources"  key="servermgr.server.communicationport"/></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
                                                                       <html:text styleId="communicationPort" property="communicationPort" size="10"/><font color="#FF0000">*</font> 
                                                              </td>
                                                        </tr>
                                                          <tr > 
                                                              <td align="left" class="labeltext" valign="top" width="18%" ><bean:message bundle="servermgrResources"  key="servermgr.server.username"/></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
                                                                      <html:text styleId="userName" property="userName" size="25"/><font color="#FF0000"> *</font> 
                                                              </td>
                                                        </tr>
                                                        <tr > 
                                                              <td align="left" class="labeltext" valign="top" width="18%" ><bean:message bundle="servermgrResources" key="servermgr.server.password"/></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
                                                                       <html:password  styleId="password" property="password" size="25"/><font color="#FF0000"> *</font> 
                                                              </td>
                                                        </tr>
                                                        
                                                        <tr > 
                                                              <td align="left" class="labeltext" valign="top" width="18%" ><bean:message bundle="servermgrResources"  key="servermgr.server.operationtimeout"/></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
                                                                       <html:text styleId="operationTimeOut" property="operationTimeOut" size="10" maxlength="15"/><font color="#FF0000"> *</font> 
                                                              </td>
                                                         </tr>
                                                         
                                                         <tr > 
                                                  <td align="left" class="labeltext" valign="top" width="2%" ><bean:message bundle="servermgrResources"  key="servermgr.server.failuremsg"/></td> 
                                                  <td align="left" class="labeltext"  valign="top" colspan="2" >
                                                             <html:text styleId="failureMsg" property="failureMsg" value="ogin incorrect" size="25"/><font color="#FF0000"> *</font> 
                                                  </td>
                                              </tr>
                                       	  
											  <tr>
											  
											  <td colspan="4" width="100%" align="right">
											  <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right">
											   <tr> 
												<td class="tblheader-bold" colspan="5"><bean:message bundle="servermgrResources" key="servermgr.server.logonscripts"/></td>
											   </tr> 
											  
											  <tr>
											  <td align="left" class="tblfirstcol" valign="top" width="2%">1.</td>
											  <td align="left" class="tblcol" valign="top" width="5%"><bean:message bundle="servermgrResources" key="servermgr.server.expect"/> </td>
											  <td align="left" class="tblfirstcol" valign="top" width="18%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:text styleId="loginPrompt" property="loginPrompt" size="25"/><font color="#FF0000">*</font> </td>
											  <td align="left" class="tblcol" valign="top" width="5%"><bean:message bundle="servermgrResources" key="servermgr.server.send"/> </td>
											  <td align="left" class="tblfirstcol" valign="top" width="18%"><bean:message bundle="servermgrResources" key="servermgr.server.send.1"/> </td>
											  </tr>
											  
											  <tr>
											  <td align="left" class="tblfirstcol" valign="top" >2.</td>
											  <td align="left" class="tblcol" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.server.expect"/> </td>
											  <td align="left" class="tblfirstcol" valign="top">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:text styleId="passwordPrompt" property="passwordPrompt" size="25" /><font color="#FF0000">*</font></td>
											  <td align="left" class="tblcol" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.server.send"/> </td>
											  <td align="left" class="tblfirstcol" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.server.send.2"/> </td>
											  </tr>
											  
											  <tr>
											  <td align="left" class="tblfirstcol" valign="top" >3.</td>
											  <td align="left" class="tblcol" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.server.expect"/> </td>
										  	  <td align="left" class="tblfirstcol" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.server.expect.3"/> </td>
											  <td align="left" class="tblcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.server.send"/> </td>
											  <td align="left" class="tblfirstcol" valign="top" ><html:text styleId="shell" property="shell" size="25" maxlength="15"/><font color="#FF0000"> *</font></td>
											  </tr>
											  
											  <tr>
											  <td align="left" class="tblfirstcol" valign="top">4.</td>
											  <td align="left" class="tblcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.server.expect"/> </td>
										  	  <td align="left" class="tblfirstcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.server.expect.4"/> </td>
											  <td align="left" class="tblcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.server.send"/> </td>
											  <td align="left" class="tblfirstcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.server.send.4"/> </td>
											  </tr>
											  
											  <tr>
											  <td align="left" class="tblfirstcol" valign="top">5.</td>
											  <td align="left" class="tblcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.server.expect"/> </td>
										  	  <td align="left" class="tblfirstcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.server.expect.5"/> </td>
											  <td align="left" class="tblcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.server.send"/> </td>
											  <td align="left" class="tblfirstcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.server.send.5"/> </td>
											  </tr>
											  
											  <tr>
											  <td align="left" class="tblfirstcol" valign="top">6.</td>
											  <td align="left" class="tblcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.server.expect"/> </td>
										  	  <td align="left" class="tblfirstcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.server.expect.6"/> </td>
											  <td align="left" class="tblcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.server.send"/> </td>
											  <td align="left" class="tblfirstcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.server.send.6"/> </td>
											  </tr>
											  
											 
											  
											  <tr>
											  <td align="left" class="tblfirstcol" valign="top">7.</td>
											  <td align="left" class="tblcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.server.expect"/> </td>
										  	  <td align="left" class="tblfirstcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.server.expect.8"/> </td>
											  <td align="left" class="tblcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.server.send"/> </td>
											  <td align="left" class="tblfirstcol" valign="top"> 
											  <logic:iterate id="netServerTypeData" name="netServerTypeList" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerTypeData">
													<logic:equal name="netServerTypeData" property="netServerTypeId" value="<%=netServerInstanceBean.getNetServerTypeId()%>">
														$shell <bean:write name="netServerTypeData" property="startupScriptName" />.sh
													</logic:equal>
												</logic:iterate>
											  </td>
											  </tr>
											  
											  </table>
	    	  </table>   
 		 </table>
   </td>
   </tr>
    <tr>
    <td>
    <table id="buttonTb2">
  	<tr > 
      <td class="btns-td" valign="middle">
       	<input type="button" name="c_btnUpdate"  onclick="validateUpdate()"  id="c_btnUPdate"  value="Update"  class="light-btn">                   
       	<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=localBasePath%>/viewNetServerInstance.do?netserverid=<bean:write name="updateNetServerAdminInterfaceDetailForm" property="netServerId"/>'" value="Cancel" class="light-btn"> 
      </td>
	</tr>
   </table>
   </td>
   </tr>
	<tr> 
      <td>&nbsp;</td>
    </tr>
</table>
</html:form>

<%@ include file="/jsp/core/includes/common/Footer.jsp" %>	