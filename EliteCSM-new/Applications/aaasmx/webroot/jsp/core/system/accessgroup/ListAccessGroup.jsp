<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.web.core.system.accessgroup.forms.ListAccessGroupForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
    String basePath = request.getContextPath();
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script type="text/javascript">
function  checkAll(){
 	if( document.forms[0].toggleAll.checked == true) {
	 	for (i = 0; i < document.forms[0].select.length;i++)
			document.forms[0].select[i].checked = true ;
    } else if (document.forms[0].toggleAll.checked == false){
		for (i = 0; i < document.forms[0].select.length; i++)
			document.forms[0].select[i].checked = false ;
	}
}
function removeData(){
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
setTitle('<bean:message key="accessgroup.accessgroup"/>');
</script>

<%                                   
	List lstAccessGroupData = ((ListAccessGroupForm)request.getAttribute("listAccessGroupForm")).getListAccessGroup();
	int iIndex = 0;
%>

<html:form action="/miscListAccessGroup">
	<html:hidden name="miscListAccessGroupForm" styleId="action"
		property="action" />
	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header"><bean:message
											key="accessgroup.list" /></td>
								</tr>
								<tr>
									<td class="small-gap" width="6">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle"><input type="button"
										name="c_btnCreate"
										onclick="javascript:location.href='<%=basePath%>/initCreateAccessGroup.do'"
										value="Create" class="light-btn" /> <html:button
											property="c_btnDelete" onclick="removeData()" value="Delete"
											styleClass="light-btn" /></td>
								</tr>
								<tr>
									<td class="small-gap" width="6">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle" colspan="6">
										<table cellpadding="0" cellspacing="0" border="0" width="99%">
											<tr>
												<td align="center" class="tblheader" valign="top" width="1%">
													<input type="checkbox" name="toggleAll" value="checkbox"
													onclick="checkAll()" />
												</td>
												<td align="center" class="tblheader" valign="top"
													width="40px"><bean:message key="general.serialnumber" /></td>
												<td align="left" class="tblheader" valign="top" width="30%"><bean:message
														key="accessgroup.name" /></td>
												<td align="left" class="tblheader" valign="top" width="*"><bean:message
														key="accessgroup.description" /></td>
												<td align="center" class="tblheader" valign="top"
													width="40px"><bean:message
														key="configurationprofile.edit" /></td>
											</tr>
											<%
    if(lstAccessGroupData != null && lstAccessGroupData.size() > 0){
%>
											<logic:iterate id="groupData" name="listAccessGroupForm"
												property="listAccessGroup"
												type="com.elitecore.elitesm.datamanager.core.system.accessgroup.data.IGroupData">
												<tr>
													<td align="center" class="tblfirstcol" valign="top"><input
														type="checkbox" name="select"
														value="<bean:write name="groupData" property="groupId"/>" />
													</td>
													<td align="center" class="tblfirstcol" valign="top"><%=(iIndex+1) %></td>
													<td align="left" class="tblrows" valign="top"><a
														href="<%=basePath%>/viewAccessGroup.do?groupid=<bean:write name="groupData" property="groupId"/>"><bean:write
																name="groupData" property="name" /></td>
													<td align="left" class="tblrows" valign="top"><bean:write
															name="groupData" property="description" />&nbsp;</td>
													<td align="center" class="tblrows"><a
														href="<%=basePath%>/editAccessGroup.do?groupId=<bean:write name="groupData" property="groupId"/>"><img
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
								<tr>
									<td class="btns-td" valign="middle"><input type="button"
										name="c_btnCreate"
										onclick="javascript:location.href='<%=basePath%>/initCreateAccessGroup.do'"
										value="Create" class="light-btn" /> <html:button
											property="c_btnDelete" onclick="removeData()" value="Delete"
											styleClass="light-btn" /></td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
</html:form>