<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.accessgroup.forms.ListAccessGroupForm"%>
<%@include file="/jsp/core/includes/common/Header.jsp"%>


<script type="text/javascript">
$(document).ready(function(){
	setTitle('<bean:message key="accessgroup.accessgroup" />');
	checkAll(document.getElementsByName("toggleAll")[0]);
});
function removeRecord(){
    var selectVar = false;
    
    for (i=0; i < document.forms[0].elements.length; i++){
        if(document.forms[0].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[0].elements[i].checked == true)
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('At least select one Staff Personnel for remove process');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.listaccessgroup.delete.query"/>';        
        var agree = confirm(msg);
        if(agree){
       	    document.miscListAccessGroupForm.action.value = 'delete';
        	document.miscListAccessGroupForm.submit();
        }
    }
}
</script>


<table cellspacing="0" border="0" width="100%" cellpadding="0" >
	<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
 
	<%                                   
		List lstAccessGroupData = ((ListAccessGroupForm)request.getAttribute("listAccessGroupForm")).getListAccessGroup();
		int iIndex = 0;
	%>
	<tr>
		<td width="7">&nbsp;</td>
		<td cellpadding="0" class="box" cellspacing="0" border="0" width="100%"
			valign="top"><html:form action="/miscListAccessGroup">
			<html:hidden name="miscListAccessGroupForm" styleId="action"
				property="action" />
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td valign="top" align="right">
					<table width="100% border=" 0" cellspacing="0" cellpadding="0"
						height="15%">
						<tr>
							<td class="table-header" colspan="6"><bean:message
								key="accessgroup.list" /></td>
						</tr>
						<tr>
							<td class="small-gap" width="6">&nbsp;</td>
						</tr>
						<tr class="vspace">
							<td class="btns-td" valign="middle"><input type="button"
								name="c_btnCreate"
								onclick="javascript:location.href='<%=basePath%>/initCreateAccessGroup.do'"
								value="Create" class="light-btn" /> <html:button
								property="c_btnDelete" onclick="removeRecord()" value="Delete"
								styleClass="light-btn" /></td>
						</tr>
						<tr>
							<td class="small-gap" width="6">&nbsp;</td>
						</tr>
						<tr class="vspace">
							<td class="btns-td" valign="middle" colspan="6">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td align="center" class="tblheaderfirstcol" valign="top" width="1%">
									<input type="checkbox" name="toggleAll" value="checkbox"
										onclick="checkAll(this)" /></td>
									<td align="right" class="tblheader" valign="top" width="1%"><bean:message
										key="general.serialnumber" /></td>
									<td align="left" class="tblheader" valign="top" width="30%"><bean:message
										key="accessgroup.name" /></td>
									<td align="left" class="tblheader" valign="top" width="30%"><bean:message
										key="accessgroup.description" /></td>
									<td align="left" class="tblheader" valign="top" width="20%">
										<bean:message key="general.createddate"/></td>
									<td align="left" class="tblheader" valign="top" width="20%">
										<bean:message key="general.lastmodifieddate"/></td>
									<td align="center" class="tblheaderfirstcol" valign="top" width="10%"><bean:message
										key="configurationprofile.edit" /></td>
								</tr>
								<%
    if(lstAccessGroupData != null && lstAccessGroupData.size() > 0){
    	int i=0;
%>
								<logic:iterate id="roleData" name="listAccessGroupForm"
									property="listAccessGroup"
									type="com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleData">
										<tr id="dataRow" name="dataRow" >	
										<td align="center" class="tblfirstcol" valign="top"><input
											type="checkbox" name="select"
											value="<bean:write name="roleData" property="roleId"/>" onclick="onOffHighlightedRow(<%=i++%>,this)"  />
										</td>
										<td align="right" class="tblfirstcol" valign="top"><%=(iIndex+1) %></td>
										<td align="left" class="tblrows" valign="top"><a
											href="<%=basePath%>/viewAccessGroup.do?roleId=<bean:write name="roleData" property="roleId"/>"><bean:write
											name="roleData" property="name" /></a></td>
										<td align="left" class="tblrows" valign="top"><bean:write
											name="roleData" property="description" />&nbsp;</td>
										<td align="left" class="tblrows" valign="top">
											<%=EliteUtility.dateToString(roleData.getCreateDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>
										<td align="left" class="tblrows" valign="top">
											<%=EliteUtility.dateToString(roleData.getLastModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>
										<td align="center" class="tblrows"><a
											href="<%=basePath%>/editAccessGroup.do?roleId=<bean:write name="roleData" property="roleId"/>"><img
											src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0"></a></td>
									</tr>
									<% iIndex += 1; %>
								</logic:iterate>
								<%
    } else {
%>
								<tr>
									<td align="center" class="tblfirstcol" colspan="6">No
									Records Found.</td>
								</tr>
								<%
    }
%>
								<tr>
									<td class="small-gap" width="6">&nbsp;</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr class="vspace">
							<td class="btns-td" valign="middle"><input type="button"
								name="c_btnCreate"
								onclick="javascript:location.href='<%=basePath%>/initCreateAccessGroup.do'"
								value="Create" class="light-btn" /> <html:button
								property="c_btnDelete" onclick="removeRecord()" value="Delete"
								styleClass="light-btn" /></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
		</html:form>
	 </td>
	</tr>
	<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>
