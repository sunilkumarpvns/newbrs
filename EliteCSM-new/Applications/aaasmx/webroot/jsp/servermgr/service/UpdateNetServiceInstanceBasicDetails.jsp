<%@ page import="java.util.Collection"%>
<%@ page import="java.util.List"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>







<%
	List lstNetServiceType = (List)request.getAttribute("lstNetServiceType");
%>

<script>
function validateUpdate(){
		document.forms[0].action.value='update';
		if(document.forms[0].netServiceType.value == '0'){
	    	alert('ServiceType is a compulsory field Please enter required data in this field.');
		}else if(document.forms[0].name.value == ''){
			alert('Name is a compulsory field Please enter required data in this field.');
		}else{
			document.forms[0].submit();
		}
}
</script>

<html:form action="/updateNetServiceInstanceBasicDetail">
	<html:hidden name="updateNetServiceInstanceBasicDetailForm"
		styleId="action" property="action" />
	<html:hidden name="updateNetServiceInstanceBasicDetailForm"
		styleId="netServiceId" property="netServiceId" />
	<html:hidden name="updateNetServiceInstanceBasicDetailForm"
		styleId="netServiceType" property="netServiceType" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					height="15%">
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td class="tblheader-bold" colspan="3"><bean:message
								bundle="servermgrResources"
								key="servermgr.updateservicebasicdetails" /></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<%--		  <tr>
			<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.servertype"/></td>
			<td align="left" class="labeltext" valign="top" width="32%" ><bean:write name="updateNetServerInstanceBasicDetailForm" property="netServerType"/>
			   <html:select name="updateNetServerInstanceBasicDetailForm" property="netServerType" size="1" tabindex="2">
			   <html:option value="0" ><bean:message bundle="servermgrResources" key="servermgr.servertype" /></html:option>
			   <html:options collection="lstNetServerType" property="netServerTypeId" labelProperty="name" /> 
			   </html:select><font color="#FF0000"> *</font>      									
			</td>
		  </tr>--%>

					<tr>
						<td align="left" class="labeltext" valign="top" width="10%"><bean:message
								bundle="servermgrResources" key="servermgr.servicename" /></td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<html:text styleId="name" property="name" size="30"
								maxlength="60" tabindex="1" /><font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" width="10%"><bean:message
								bundle="servermgrResources" key="servermgr.description" /></td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<html:textarea styleId="description" property="description"
								cols="50" rows="4" tabindex="2"></html:textarea>
						</td>
					</tr>
					<%--<tr>
					<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.javahome"/></td>
					<td align="left" class="labeltext" valign="top" width="32%" >
						<html:text property="javaHome" size="50" maxlength="60" tabindex="3"/><font color="#FF0000"> *</font>
				</td>
				  </tr>
	
						  	
				  <tr>
					<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.serverhome"/></td>
					<td align="left" class="labeltext" valign="top" width="32%" >
						<html:text property="serverHome" size="50" maxlength="60" tabindex="4"/><font color="#FF0000"> *</font>
					</td>
				  </tr>		
		  								
       --%>
				</table>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="btns-td" valign="middle"><input type="button"
				name="c_btnUpdate" onclick="validateUpdate()" id="c_btnUPdate"
				value="Update" class="light-btn" tabindex="4"> <input
				type="reset" name="c_btnDeletePolicy"
				onclick="javascript:location.href='<%=basePath%>/viewNetServiceInstance.do?netserviceid=<bean:write name="updateNetServiceInstanceBasicDetailForm" property="netServiceId"/>'"
				value="Cancel" class="light-btn" tabindex="5"></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
	</table>
</html:form>