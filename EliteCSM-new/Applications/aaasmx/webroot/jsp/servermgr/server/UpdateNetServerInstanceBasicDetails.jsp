<%@ page import="java.util.Collection"%>
<%@ page import="java.util.List"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<% 
	String localBasePath = request.getContextPath();
%>

<%
	List lstNetServerType = (List)request.getAttribute("lstNetServerType");
%>

<script>
$(document).ready(function (){
	var isAliveDB = '<bean:write name="updateNetServerInstanceBasicDetailForm" property="alive"/>';
	if(isAliveDB == 'false') {
		alert("Server Communication failed. Connection can not established.Some parameter will not be affected");
		$('#staffRelId').attr('disabled', 'disabled');
	}else {
		$('#staffRelId').removeAttr('disabled');
	}
})
function validateUpdate(){
		var server=document.forms[0].serverHome.value;
		
		var e = server;
		e = e.replace(/\\/g, "!");
		document.forms[0].serverHome.value=e;
		
		document.forms[0].action.value='update';
		if(document.forms[0].name.value == ''){
			alert('Name is a compulsory field Please enter required data in this field.');
		} else if(document.forms[0].javaHome.value == ''){
			alert('Java Home is a compulsory field Please enter required data in this field.');
		}else if(document.forms[0].serverHome.value == ''){
			alert('Server Home is a compulsory field Please enter required data in this field.');
		} else if (document.forms[0].staff.value == '0') {
			alert("Please select the staff");
		}  else{
			document.forms[0].submit();
		}
}
</script>

<html:form action="/updateNetServerInstanceBasicDetail">
	<html:hidden name="updateNetServerInstanceBasicDetailForm"
		styleId="action" property="action" />
	<html:hidden name="updateNetServerInstanceBasicDetailForm"
		styleId="netServerId" property="netServerId" />
	<html:hidden name="updateNetServerInstanceBasicDetailForm"
		styleId="instanceStaffRelId" property="instanceStaffRelId" />
	<html:hidden name="updateNetServerInstanceBasicDetailForm"
		styleId="netServerType" property="netServerType" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					height="15%">
					<tr>
						<td class="tblheader-bold" colspan="3"><bean:message
								bundle="servermgrResources" key="servermgr.updatebasicdetails" /></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>


					<tr>
						<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="servermgrResources"
								key="servermgr.servername" /> 
									<ec:elitehelp headerBundle="servermgrResources" 
										text="servermgr.servername" 
											header="servermgr.servername"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<html:text styleId="name" property="name" size="30"
								maxlength="60" tabindex="1" style="width:280px" /><font
							color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="servermgrResources" 
								key="servermgr.description" />
									<ec:elitehelp headerBundle="servermgrResources" 
										text="servermgr.desc" 
											header="servermgr.description"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<html:textarea styleId="description" property="description"
								cols="50" rows="4" tabindex="2" style="width:280px"></html:textarea>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="servermgrResources"
								key="servermgr.serverhome" /> 
									<ec:elitehelp headerBundle="servermgrResources" 
										text="servermgr.serverhome" 
											header="servermgr.serverhome"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<html:text styleId="serverHome" property="serverHome" size="50"
								maxlength="255" tabindex="3" /><font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="servermgrResources"
								key="servermgr.javahome" /> 
									<ec:elitehelp headerBundle="servermgrResources" 
										text="servermgr.javahome" 
											header="servermgr.javahome"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<html:text styleId="javaHome" property="javaHome" size="50"
								maxlength="255" tabindex="4" /><font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="servermgrResources" key="servermgr.user" /> 
							<ec:elitehelp headerBundle="servermgrResources" text="servermgr.staffUser" header="servermgr.user"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="32%">
						
							<html:select  property="staff" styleId="staffRelId" name="updateNetServerInstanceBasicDetailForm" styleClass="dropdown-width" tabindex="5">
								<html:option value="0">--None--</html:option>
								<html:optionsCollection property="staffDataList"  label="username" value="username"/>
							</html:select>	
							<font color="#FF0000">*</font>
						</td>
					</tr>
					
					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td class="btns-td" valign="middle"><input type="button"
							name="c_btnUpdate" onclick="validateUpdate()" id="c_btnUPdate"
							value="Update" class="light-btn" tabindex="6"> <input
							type="reset" name="c_btnDeletePolicy"
							onclick="javascript:location.href='<%=localBasePath%>/viewNetServerInstance.do?netserverid=<bean:write name="updateNetServerInstanceBasicDetailForm" property="netServerId"/>'"
							value="Cancel" class="light-btn" tabindex="7"></td>
					</tr>


				</table>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>

		<tr>
			<td>&nbsp;</td>
		</tr>
	</table>
</html:form>
