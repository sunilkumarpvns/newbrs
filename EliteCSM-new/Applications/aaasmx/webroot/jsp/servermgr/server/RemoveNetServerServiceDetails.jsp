<%@ page import="java.util.List"%>
<%@ page
	import="com.elitecore.elitesm.datamanager.servermgr.data.NetServiceInstanceData"%>
<%@ page
	import="com.elitecore.elitesm.web.servermgr.server.forms.RemoveNetServerServiceInstanceForm"%>

<%@ include file="/jsp/core/includes/common/Header.jsp"%>





<% 
	String localBasePath = request.getContextPath();
%>

<%
	  List netServiceInstanceList = (List)request.getAttribute("netServiceInstanceList");
	  List lstServiceData = ((com.elitecore.elitesm.web.servermgr.server.forms.RemoveNetServerServiceInstanceForm)request.getAttribute("removeNetServerServiceInstanceForm")).getListServices();
	  List lstNetServiceType = (List)request.getAttribute("lstNetServiceType");
	  int iIndex = 0;
%>
<script>
function removeService(){
    var msg;
    msg = '<bean:message bundle="alertMessageResources" key="alert.removenetserverservicedetailsjsp.delete"/>';        
    //msg = "Are your sure you want to delete selected Service(s) ? "
    var agree = confirm(msg);
    if(agree){
		document.forms[0].action.value = 'remove';
		document.forms[0].submit();
	}
}
function  checkAll(){
 	if( document.forms[0].toggleAll.checked == true) {
	 	for (i = 0; i < document.forms[0].select.length;i++)
			document.forms[0].select[i].checked = true ;
    } else if (document.forms[0].toggleAll.checked == false){
		for (i = 0; i < document.forms[0].select.length; i++)
			document.forms[0].select[i].checked = false ;
	}
}
</script>

<html:form action="/removeNetServerServiceInstance">
	<html:hidden name="removeNetServerServiceInstanceForm" styleId="action"
		property="action" />
	<html:hidden name="removeNetServerServiceInstanceForm"
		styleId="itemIndex" property="itemIndex" />
	<html:hidden name="removeNetServerServiceInstanceForm"
		styleId="netServerId" property="netServerId" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top" align="right">
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td class="tblheader-bold" colspan="3"><bean:message
								bundle="servermgrResources" key="servermgr.removeservices" /></td>
					</tr>
					<tr>
						<td colspan="3">
							<table width="100%" cols="8" id="listTable" type="tbl-list"
								border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="tblheader" valign="top" width="4%">
										<input type="checkbox" name="toggleAll" value="checkbox"
										onclick="checkAll()" />
									</td>
									<td align="right" class="tblheader" valign="top" width="5%"><bean:message
											bundle="servermgrResources" key="servermgr.serialnumber" /></td>
									<td align="left" class="tblheader" valign="top" width="25%"><bean:message
											bundle="servermgrResources" key="servermgr.name" /></td>
									<!--                   <td align="left" class="tblheader" valign="top" width="20%" ><bean:message bundle="servermgrResources" key="servermgr.alias"/></td> -->
									<td align="left" class="tblheader" valign="top" width="30%"><bean:message
											bundle="servermgrResources" key="servermgr.description" /></td>
								</tr>
								<%
    if(lstServiceData != null && lstServiceData.size() > 0){
%>
								<logic:iterate id="netServiceInstanceData"
									name="removeNetServerServiceInstanceForm"
									property="listServices"
									type="com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData">
									<tr>
										<td align="center" class="tblfirstcol" valign="top"><input
											type="checkbox" name="select"
											value="<bean:write name="netServiceInstanceData" property="netServiceId"/>" />
										</td>
										<td align="right" class="tblfirstcol" valign="top"><%=(iIndex+1) %></td>
										<td align="left" class="tblrows" valign="top"><bean:write
												name="netServiceInstanceData" property="name" /></td>
										<!--    				  <td align="left" class="tblrows"><bean:write name="netServiceInstanceData" property="displayName"/></td>  -->
										<td align="left" class="tblrows"><bean:write
												name="netServiceInstanceData" property="description" />&nbsp;</td>
									</tr>
									<% iIndex += 1; %>
								</logic:iterate>
								<%
    }else{
%>
								<tr>
									<td align="center" class="tblfirstcol" colspan="8">No
										Records Found.</td>
								</tr>
								<%
    }
%>

							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="btns-td" valign="middle"><html:button
					property="c_btnDelete" onclick="removeService()" value="Delete"
					styleClass="light-btn" /> <!--          <input type="button" name="c_btnUpdate"  onclick="javascript:location.href='<%=localBasePath%>/jsp/servermgr/ServerContainer.jsp'"  id="c_btnUPdate"  value="Save"  class="light-btn">                   -->
				<input type="reset" name="c_btnDeletePolicy"
				onclick="javascript:location.href='<%=localBasePath%>/viewNetServerInstance.do?netserverid=<bean:write name="removeNetServerServiceInstanceForm" property="netServerId"/>'"
				value="Cancel" class="light-btn"></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
	</table>
</html:form>
