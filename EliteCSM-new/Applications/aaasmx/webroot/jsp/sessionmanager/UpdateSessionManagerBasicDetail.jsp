<%@ page import="java.util.*"%>
<%@page
	import="com.elitecore.elitesm.util.constants.ServicePolicyConstants"%>
<%@page
	import="com.elitecore.elitesm.web.sessionmanager.forms.UpdateSessionManagerBasicDetailForm"%>
<%@page
	import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page
	import="com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerInstanceData"%>

<% 
UpdateSessionManagerBasicDetailForm updateSessionManagerBasicDetailForm = (UpdateSessionManagerBasicDetailForm)request.getAttribute("updateSessionManagerBasicDetailForm");
SessionManagerInstanceData sessionManagerData = (SessionManagerInstanceData)request.getAttribute("sessionManagerInstanceData");
%>


<script>
var isValidName;
function validateUpdate()
{
	if(isNull(document.forms[0].name.value)){
		document.forms[0].name.focus();
		alert('SessionName must be specified');
	}else if(!isValidName) {
		alert('Enter Valid Name');
		document.forms[0].name.focus();
		return false;
	}else{
		document.forms[0].action.value="update";
		document.forms[0].submit();
	}
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.SESSION_MANAGER%>',searchName,'update','<%=sessionManagerData.getSmInstanceId()%>','verifyNameDiv');
} 

</script>
<html:form action="/updateSessionManagerBasicDetail">
	<html:hidden name="updateSessionManagerBasicDetailForm"
		styleId="action" property="action" />
	<html:hidden name="updateSessionManagerBasicDetailForm"
		styleId="smInstanceId" property="smInstanceId" />

	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0 cellspacing=" 0" cellpadding="0"
					height="15%">
					<tr>
						<td class="small-gap" width="7" colspan="3">&nbsp;</td>
					</tr>


					<tr>
						<td align="left" class="tblheader-bold" valign="top" width="27%"
							colspan="3"><bean:message bundle="sessionmanagerResources"
								key="sessionmanager.updatesessionmanagerdetails" /></td>
					</tr>
					<tr>
						<td class="small-gap" colspan="3">&nbsp;</td>
					</tr>



					<tr>
						<td align="left" class="captiontext" valign="top" width="28%">
							<bean:message bundle="sessionmanagerResources" 
								key="sessionmanager.name" />
									<ec:elitehelp headerBundle="sessionmanagerResources" 
										text="sessionmanager.name" 
											header="sessionmanager.name" />
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text styleId="name" property="name" onkeyup="verifyName();"
								size="25" maxlength="50" tabindex="1" style="width:250px;" /><font color="#FF0000">
								*</font>
							<div id="verifyNameDiv" class="labeltext"></div>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="28%" valign="top">
							<bean:message bundle="sessionmanagerResources"
								key="sessionmanager.description" />
									<ec:elitehelp headerBundle="sessionmanagerResources" 
										text="sessionmanager.desc" 
											header="sessionmanager.description" /> 
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:textarea styleId="description" property="description"
								cols="50" rows="2" tabindex="2" />
						</td>
					</tr>


					<tr>
						<td align="left" class="labeltext" valign="top">&nbsp;</td>
						<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
					</tr>

					<tr>
						<td>&nbsp;</td>
						<td align="left" class="labeltext" valign="top"><input
							type="button" name="c_btnNext" onclick="validateUpdate()"
							value=" Update " class="light-btn" tabindex="3" /> <input
							type="button" name="c_btnCancel"
							onclick="javascript:location.href='<%=basePath%>/viewSessionManager.do?sminstanceid=<%=updateSessionManagerBasicDetailForm.getSmInstanceId()%>'"
							value="   Cancel   " class="light-btn" tabindex="4" /></td>

					</tr>

				</table>
			</td>
		</tr>
	</table>
</html:form>

