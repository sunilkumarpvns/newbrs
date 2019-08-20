<%@page import="com.elitecore.netvertexsm.web.group.form.GroupDataForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp" %>


<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Map.Entry" %>

<script language = "javascript">
$(document).ready(function(){
	setTitle('<bean:message bundle="groupDataMgmtResources" key="group.title"/>');
	document.forms[0].name.focus();
	$("#description").attr('maxlength','255');
	
});

var isValidName;
function validate(){
	if(isNull(document.forms[0].name.value)){
		alert('Group Name must be specified');
		document.forms[0].name.focus();
		return false;
	}else if(!isValidName) {
		alert('Enter Valid Group Name');
		document.forms[0].name.focus();
		return false;
	}else{
		document.forms[0].submit();
	 	return true;	
	}			 	 			
}
function verifyFormat (){
	var searchName = document.getElementById("name").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.GROUP_MANAGEMENT%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}
function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.GROUP_MANAGEMENT%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}

</script>
 
<html:form action="/groupManagement.do?method=create" onsubmit="return validate();"> 
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
				<bean:message bundle="groupDataMgmtResources" key="group.create.title"/>
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
						<bean:message bundle="groupDataMgmtResources" key="group.name" />
						</td> 
						<sm:nvNameField size="30" maxLength="50"  id="name" name="name" >
						</sm:nvNameField>
				</tr> 
				<tr>
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="groupDataMgmtResources" key="group.description" /></td>
					<td align="left" class="labeltext" valign="top" width="32%">
						<html:textarea styleId="description" property="description" cols="40" rows="4" tabindex="2"/></td>							
				</tr>						
				<tr>
					<td class="btns-td" valign="middle">&nbsp;</td>
					<td class="btns-td" valign="middle" colspan="2">
						<input type="button" name="c_btnCreate" value="   Create   " class="light-btn" onclick="return validate()" tabindex="8"> 
						<input type="button" align="left" value=" Cancel " tabindex="8" class="light-btn" onclick="javascript:location.href='<%=basePath%>/groupManagement.do?method=initSearch'"/>
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
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table> 
</html:form> 
