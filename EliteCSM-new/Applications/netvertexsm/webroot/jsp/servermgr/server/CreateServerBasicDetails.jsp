<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<jsp:directive.page import="com.elitecore.netvertexsm.util.constants.BaseConstant"/>
<%@page import="com.elitecore.netvertexsm.web.servermgr.server.form.CreateServerForm"%>

<%
    CreateServerForm  createServerForm = (CreateServerForm) request.getAttribute("createServerForm");
%>

<style>
.light-btn {  border:medium none; font-family: Arial; font-size: 12px; color: #FFFFFF; background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg'); font-weight: bold}
</style>

<script>
function validateAdminInterfaceIP()
{
	   //Check for valid IPAddress
        var ipre = /((^\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\s*$)|(^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$))/;
		if(document.forms[0].adminInterfaceIP.value != null && document.forms[0].adminInterfaceIP.value!="" && !ipre.test(document.forms[0].adminInterfaceIP.value)){
			//	alert('Admin Interface IP  is not valid. Please Enter valid data.');
				document.forms[0].adminInterfaceIP.focus();
				return false;
		}
		else {
			return true;
		}
}

function validateCreate(act){
	var check;
	var checkPort;
	if(document.forms[0].netServerType.value == '0'){
	    alert('ServerType is a compulsory field Please enter required data in this field.');
	}else if(document.forms[0].name.value == ''){
		alert('\'Server Instance Name\' is a compulsory field Please enter required data in this field.');
	}else if(!isValidName){
		alert('Server Name is Invalid.');
		document.forms[0].name.focus();
		return false;	
	}else if(document.forms[0].adminInterfaceIP.value == ''){
		alert('\'Admin Interface\' is a compulsory field Please enter required data in this field.');
	}else if(document.forms[0].adminInterfacePort.value == ''){
		alert('\'Admin Interface Port\' is a compulsory field Please enter required data in this field.');
	}else{	
		check = validateAdminInterfaceIP();
		checkPort = validatePort(document.forms[0].adminInterfacePort.value)
		if(check == true ){
			if(checkPort == true){
				if(act=='f'){
				  document.forms[0].action.value='create';
				}
				document.forms[0].submit();
			}else
				alert('\'Admin Interface Port\' is not valid. Please Enter valid data.');
		}else{
			alert('\'Admin Interface IP\'  is not valid. Please Enter valid data.');
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
$(document).ready(function(){
	setTitle('<bean:message bundle="servermgrResources" key="servermgr.create.server"/>');
	$("#netServerType").focus();
	$("#description").attr('maxlength','255');
	verfiyName();
});
function verifyFormat (){
	var searchName = document.getElementById("name").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.SERVER%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}
function verifyName() {
	var searchName = document.getElementById("name").value;
	searchName = $.trim(searchName);
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.SERVER%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}

</script>
<% String groupId = (String)request.getAttribute("groupId"); %>
<html:form action="/createServer">
<html:hidden styleId="isInSync" property="isInSync" value="<%=BaseConstant.HIDE_STATUS_ID%>"/>
<html:hidden styleId="action" property="action" value="next"/>
<html:hidden styleId="status" property="status" value="1"  />
<html:hidden property="groupId" value="<%=groupId %>"/>
<table cellpadding="0" cellspacing="0" border="0" width="100%" >
	<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<tr>
	  <td width="10">&nbsp;</td>
	  <td width="100%" colspan="2" valign="top" class="box">
		<table cellSpacing="0" cellPadding="0" width="100%" border="0">
	 	  <tr>
			<td class="table-header" colspan="5"><bean:message bundle="servermgrResources" key="servermgr.createserver"/>
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
						   <html:options collection="lstNetServerType" property="netServerTypeId" labelProperty="name" /> 
						</html:select><font color="#FF0000"> *</font>	      
					</td>
				  </tr>
				  <tr>
					<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.servername"/></td>
						<sm:nvNameField maxLength="60" size="30" id="name" name="name" />
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
						<html:text styleId="adminInterfaceIP" property="adminInterfaceIP" size="30" maxlength="255" tabindex="4" onblur="trimvalue(this);" /><font color="#FF0000"> *</font>
					</td>
				  </tr>								
				  <tr>
					<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.admininterfaceport"/></td>
					<td align="left" class="labeltext" valign="top" width="32%" >
						<html:text styleId="adminInterfacePort" property="adminInterfacePort" size="10" maxlength="5" tabindex="5"/><font color="#FF0000"> *</font>
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
				<input type="button" name="c_btnCreate" onclick="validateCreate('n')" id="c_btnCreate2"  value="   Next   "  class="light-btn" tabindex="6">
                <input type="button" name="c_btnCreate" onclick="validateCreate('f')" id="c_btnCreate2"  value="   Finish   "  class="light-btn" tabindex="6">
                <input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/serverGroupManagement.do?method=initSearch'" value="Cancel" class="light-btn" tabindex="7"> 
	        </td>
   		  </tr>
		</table>
	  </td>
	</tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table>
</html:form>


