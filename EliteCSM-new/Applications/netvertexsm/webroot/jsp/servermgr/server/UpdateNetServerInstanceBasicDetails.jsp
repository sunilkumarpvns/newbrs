<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.List" %>






<% 
	String localBasePath = request.getContextPath();
%>

<%
	List lstNetServerType = (List)request.getAttribute("lstNetServerType");
%>

<script>
$(document).ready(function(){
	$("#name").focus();
	verfiyName();
});
function validateUpdate(){
		document.forms[0].action.value='update';
		if(document.forms[0].name.value == ''){
			alert('Name is a compulsory field Please enter required data in this field.');
		}else if(!isValidName){		
			alert('Server Name is Invalid.');
			document.forms[0].name.focus();
			return false;	
		}else if(document.forms[0].javaHome.value == ''){
			alert('Java Home is a compulsory field Please enter required data in this field.');
		}else if(document.forms[0].serverHome.value == ''){
			alert('Server Home is a compulsory field Please enter required data in this field.');
		} else{
			document.forms[0].submit();
		}
}
function verifyFormat (){
	var searchName = document.getElementById("name").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.SERVER%>',searchName:searchName,mode:'update',id:'updateNetServerInstanceBasicDetailForm.netServerId'},'verifyNameDiv');
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	searchName = $.trim(searchName);
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.SERVER%>',searchName:searchName,mode:'update',id:'${updateNetServerInstanceBasicDetailForm.netServerId}'},'verifyNameDiv');
}
</script>

<html:form action="/updateNetServerInstanceBasicDetail">
<html:hidden name="updateNetServerInstanceBasicDetailForm" styleId="action" property="action" />
<html:hidden name="updateNetServerInstanceBasicDetailForm" styleId="netServerId" property="netServerId"/>
<html:hidden name="updateNetServerInstanceBasicDetailForm" styleId="netServerType" property="netServerType"/>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
		  <tr>
		  	<td>&nbsp;</td>
		  </tr>
		  <tr> 
			<td class="tblheader-bold" colspan="3"><bean:message bundle="servermgrResources" key="servermgr.updatebasicdetails"/></td>
		  </tr>
		  <tr>
		  	<td>&nbsp;</td>
		  </tr>

          
		  <tr>
			<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.servername"/></td>
			<sm:nvNameField maxLength="60" size="30" id="name" name="name" value="${updateNetServerInstanceBasicDetailForm.name}"/>
			
		  </tr>								
		  <tr>
			<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.description"/></td>
			<td align="left" class="labeltext" valign="top" width="32%" >
			   <html:textarea styleId="description" property="description" cols="50" rows="4" tabindex="2" styleClass="input-textarea"></html:textarea>
			</td>
		  </tr>

		  <tr>
			<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.serverhome"/></td>
			<td align="left" class="labeltext" valign="top" width="32%">
				<html:text styleId="serverHome" property="serverHome" size="50" maxlength="255" tabindex="3"/><font color="#FF0000"> *</font>
			</td>
		  </tr>	
		 <tr>
			<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.javahome"/></td>
			<td align="left" class="labeltext" valign="top" width="32%" >
				<html:text styleId="javaHome" property="javaHome" size="50" maxlength="255" tabindex="4"/><font color="#FF0000"> *</font>
			</td>
		</tr>
						  	
					
		  								
       </table>
	  </td>
    </tr>
	<tr> 
      <td>&nbsp;</td>
    </tr>
   	<tr> 
      <td class="btns-td" valign="middle"  >
       	 <input type="button" name="c_btnUpdate"  onclick="validateUpdate()"  id="c_btnUPdate"  value="Update"  class="light-btn" tabindex="5">                   
         <input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=localBasePath%>/viewNetServerInstance.do?netserverid=<bean:write name="updateNetServerInstanceBasicDetailForm" property="netServerId"/>'" value="Cancel" class="light-btn" tabindex="6"> 
       </td>
	</tr>
	<tr> 
      <td>&nbsp;</td>
    </tr>
</table>
</html:form>

<%@ include file="/jsp/core/includes/common/Footer.jsp" %>	