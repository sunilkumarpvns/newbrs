<%@page import="com.elitecore.netvertexsm.web.group.form.GroupDataForm"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map.Entry" %>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>

<% 
	GroupDataForm form = (GroupDataForm)request.getAttribute("groupDataForm");

%>
<script language = "javascript">
var isValidName;
function validate() {
	document.forms[0].submit();
	return true;
}
</script>
 
<html:form action="/groupManagement.do?method=update" onsubmit="return validate();"> 
<html:hidden name="groupDataForm" property="id"/>
<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 

   
	<tr> 
		<td colspan="2" align="right"> 
			 	<table cellpadding="0" cellspacing="0" border="0" width="97%">
					<tr>
						<td class="tblheader-bold"  valign="top" colspan="2"  >	<bean:message  bundle="groupDataMgmtResources" key="group.update.title"/> </td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" width="10%">
							<bean:message bundle="groupDataMgmtResources" key="group.name" />
						</td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<html:text property="name" name="name" size="30" maxlength="50" styleId="name" value="${groupDataForm.name}" readonly="true"/>
						</td>
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
						<input type="button" name="c_btnUpdate" value="   Update   " class="light-btn" onclick="return validate();" tabindex="8"> 
						<input type="button" align="left" value=" Cancel " tabindex="8" class="light-btn" onclick="javascript:location.href='<%=basePath%>/groupManagement.do?method=initSearch'"/>
					</td>
				</tr>				  					 
			   </table>  
			</td>
		  </tr>	 
		 <tr> 
			<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td> 
		  </tr>
           
		</table> 
	  </td> 
	</tr> 
</table> 
</html:form> 
