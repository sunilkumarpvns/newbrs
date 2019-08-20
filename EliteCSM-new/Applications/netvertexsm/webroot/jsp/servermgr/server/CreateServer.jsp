
<jsp:directive.page import="com.elitecore.netvertexsm.util.constants.BaseConstant"/><%@ include file="/jsp/core/includes/common/Header.jsp"%>




<style>
.light-btn {  border:medium none; font-family: Arial; font-size: 12px; color: #FFFFFF; background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg'); font-weight: bold}
</style>

<script>
function validateAdminInterfaceIP()
{
	   //Check for valid IPAddress
		var ipre=/^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
		if(document.forms[0].adminInterfaceIP.value != null && document.forms[0].adminInterfaceIP.value!="" && !ipre.test(document.forms[0].adminInterfaceIP.value)){
			//	alert('Admin Interface IP  is not valid. Please Enter valid data.');
				document.forms[0].adminInterfaceIP.focus();
				return false;
		}
		else {
			return true;
		}
}
function validateCreate(){
	var check;
	var checkPort;
	if(document.forms[0].netServerType.value == '0'){
	    alert('ServerType is a compulsory field Please enter required data in this field.');
	}else if(document.forms[0].name.value == ''){
		alert('Name is a compulsory field Please enter required data in this field.');
	}else if(document.forms[0].adminInterfaceIP.value == ''){
		alert('AdminInterface is a compulsory field Please enter required data in this field.');
	}else if(document.forms[0].adminInterfacePort.value == ''){
		alert('AdminInterfacePort is a compulsory field Please enter required data in this field.');
	}else if(document.forms[0].javaHome.value == ''){
		alert('Java Home is a compulsory field Please enter required data in this field.');
	}else if(document.forms[0].serverHome.value == ''){
		alert('Server Home is a compulsory field Please enter required data in this field.');
	}	else{	
		check = validateAdminInterfaceIP();
		checkPort = validatePort(document.forms[0].adminInterfacePort.value)
		if(check == true ){
			if(checkPort == true)
				document.forms[0].submit();
			else
				alert('Admin Interface Port is not valid. Please Enter valid data.');
		}else{
			alert('Admin Interface IP  is not valid. Please Enter valid data.');
		}
	}
}
function validatePort(txt){
	// check for valid numeric port	 
	if(IsNumeric(txt) == true){
		if(txt >= 0 && txt<=65535)
			return(true);
	}else
		return(false);
}
</script>

<html:form action="/createServer">
<html:hidden styleId="isInSync" property="isInSync" value="<%=BaseConstant.HIDE_STATUS_ID%>"/>
<table cellpadding="0" cellspacing="0" border="0" width="828" >
  <tr> 
    <td width="10">&nbsp;</td>
    <td colspan="2"> 
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td width="26" valign="top" rowspan="2"><img src="<%=basePath%>/images/left-curve.jpg"></td>
          <td background="<%=basePath%>/images/header-gradient.jpg" width="133" rowspan="2" align="center" class="page-header">Server dd</td>
          <td width="32" rowspan="2"><img src="<%=basePath%>/images/right-curve.jpg"></td>
          <td width="633"></td>
        </tr>
        <tr> 
          <td width="633" valign="bottom"><img src="<%=basePath%>/images/line.jpg"></td>
        </tr>
      </table>
    </td>
  </tr>
  <tr> 
    <td width="10" class="small-gap">&nbsp;</td>
    <td class="small-gap" colspan="2">&nbsp;</td>
  </tr>
	<tr>
	  <td width="10">&nbsp;</td>
	  <td width="100%" colspan="2" valign="top" class="box">
		<table cellSpacing="0" cellPadding="0" width="100%" border="0">
	 	  <tr>
			<td class="table-header" colspan="5">
			<bean:message bundle="servermgrResources" key="servermgr.createserver"/><%--<img src="<%=basePath%>/images/open.jpg" border="0" name="closeopen"></td> --%>
		  </tr>
		   <tr>
			<td class="tblheader-bold" colspan="5">
			<bean:message bundle="servermgrResources" key="servermgr.details"/><%--<img src="<%=basePath%>/images/open.jpg" border="0" name="closeopen"></td> --%>
		  </tr>
		  <tr>
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
		  </tr> 
		  <tr>
			<td colspan="3">
			   <table width="97%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0" >
				    <tr>
					<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.servertype"/></td>
					<td align="left" class="labeltext" valign="top" width="32%" >
					    <html:select name="createServerForm" styleId="netServerType" property="netServerType" size="1" tabindex="1">
						   <html:option value="0" ><bean:message bundle="servermgrResources" key="servermgr.servertype" /></html:option>
						   <html:options collection="lstNetServerType" property="netServerTypeId" labelProperty="name" /> 
						</html:select><font color="#FF0000"> *</font>	      
					</td>
				  </tr>
				  <tr>
					<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.servername"/></td>
					<td align="left" class="labeltext" valign="top" width="32%" >
						<html:text styleId="name" property="name" size="30" maxlength="60" tabindex="2"/><font color="#FF0000"> *</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:checkbox name="createServerForm" styleId="status" property="status" value="1"  /> Active
					</td>
				  </tr>								
				  <tr>
					<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.description"/></td>
					<td align="left" class="labeltext" valign="top" width="32%" >
					    <html:textarea styleId="description" property="description" cols="50" rows="4" tabindex="3" styleClass="input-textarea"></html:textarea>
					</td>
				  </tr>								
				  <tr>
					<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.admininterfaceip"/></td>
					<td align="left" class="labeltext" valign="top" width="32%" >
						<html:text styleId="adminInterfaceIP" property="adminInterfaceIP" size="30" maxlength="60" tabindex="4"/><font color="#FF0000"> *</font>
					</td>
				  </tr>								
				  <tr>
					<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.admininterfaceport"/></td>
					<td align="left" class="labeltext" valign="top" width="32%" >
						<html:text styleId="adminInterfacePort" property="adminInterfacePort" size="10" maxlength="60" tabindex="5"/><font color="#FF0000"> *</font>
					</td>
				  </tr>	
				  <tr>
					<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.serverhome"/></td>
					<td align="left" class="labeltext" valign="top" width="32%" >
						<html:text styleId="serverHome" property="serverHome" size="50" maxlength="255" tabindex="5"/><font color="#FF0000"> *</font>
					</td>
				  </tr>		
				  <tr>
					<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.javahome"/></td>
					<td align="left" class="labeltext" valign="top" width="32%" >
						<html:text styleId="javaHome" property="javaHome" size="50" maxlength="255" tabindex="5"/><font color="#FF0000"> *</font>
					</td>
				  </tr>	
				  						
			   </table>  
			</td>
		  </tr>	 
		  <tr>
			<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
		  </tr> 
          <tr > 
	        <td class="btns-td" valign="middle" >&nbsp;</td>
            <td class="btns-td" valign="middle"  >
				<input type="button" name="c_btnCreate" onclick="validateCreate()" id="c_btnCreate2"  value="Create"  class="light-btn" tabindex="6">
                <input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/serverGroupManagement.do?method=initSearch'" value="Cancel" class="light-btn" tabindex="7"> 
	        </td>
   		  </tr>
		</table>
	  </td>
	</tr>
  <tr> 
    <td width="10">&nbsp;</td>
    <td colspan="2" valign="top" align="right"> 
      <table width="99%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td width="82%" valign="top"><img src="<%=basePath%>/images/btm-line.jpg"></td>
          <td align="right" rowspan="2" valign="top"><img src="<%=basePath%>/images/btm-gradient.jpg" width="140" height="23"></td>
        </tr>
        <tr> 
          <td width="82%" valign="top" align="right" class="small-text-grey">Copyright 
            &copy; <a href="http://www.elitecore.com" target="_blank">Elitecore 
            Technologies Pvt. Ltd.</a> </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</html:form>

<%@ include file="/jsp/core/includes/common/Footer.jsp" %>
