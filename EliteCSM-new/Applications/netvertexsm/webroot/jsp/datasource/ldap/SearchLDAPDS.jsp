
<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.util.List" %>    
<%@page import="com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData" %>
<%@page import="com.elitecore.netvertexsm.web.datasource.ldap.form.SearchLDAPDatasourceForm"%>
 
<%
	List searchLDAPList = (List)session.getAttribute("searchLDAPList");
	SearchLDAPDatasourceForm searchLDAPDatasourceForm = (SearchLDAPDatasourceForm)session.getAttribute("searchLDAPDSForm");
	String action = searchLDAPDatasourceForm.getAction();	
%>

<script>

function validateForm(){
	document.forms[0].action.value = 'list';
	document.forms[0].submit();
}

function  checkAll(){
	
 	if( document.forms[0].toggleAll.checked == true) {
 		var selectVars = document.getElementsByName('select');
	 	for (i = 0; i < selectVars.length;i++)
			selectVars[i].checked = true ;
    } else if (document.forms[0].toggleAll.checked == false){
 		var selectVars = document.getElementsByName('select');	    
		for (i = 0; i < selectVars.length; i++)
			selectVars[i].checked = false ;
	}
}

function removeRecord(){
    var selectVar = false;
    
    for (i=0; i < document.forms[0].elements.length; i++){
        if(document.forms[0].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[0].elements[i].checked == true)
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('At least select one LDAPDS for remove process');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searchLDAPDSjsp.delete.query"/>';        
        //msg = "All the selected Staff Personnel would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
       	    document.forms[0].action.value = 'delete';
        	document.forms[0].submit();
        }
    }
}
$(document).ready(function(){
	setTitle('<bean:message bundle="datasourceResources" key="ldap.ldap" />');
	$("#name").focus();
});

</script>


    
<html:form action="/searchLDAPDS" >
		
		<html:hidden styleId="action" property="action" />
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
		<tr>
			<td width="10">&nbsp;</td>
			<td class="box" cellpadding="0" cellspacing="0" border="0" width="100%">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td class="table-header" colspan="5">
					<bean:message bundle="datasourceResources" key="ldap.searchldap" /></td>
				</tr>
				<tr>
					<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="3">
						<table width="97%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0" >
									
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%">
								<bean:message bundle="datasourceResources" key="ldap.name" /></td>
							<td align="left" class="labeltext" valign="top" width="66%">
								<html:text styleId="name" property="name" size="30" maxlength="60" tabindex="1" />
							</td>							
						</tr>
						<tr>
							<td class="btns-td" valign="middle">&nbsp;</td>
							<td class="btns-td" valign="middle" colspan="2">
								<input type="button" name="c_btnCreate" id="c_btnCreate2" value=" Search " tabindex="2" class="light-btn" onclick="validateForm()">
							</td>
						</tr>
						
		  	
			 		</table>  
			 	</td>
			</tr>
			
					  	
				<%if(action.equals("list")) {%>	
				 <tr>	
				 	<td align="left" class="labeltext" colspan="5" valign="top">
						<table width="100%" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td class="table-header" width="24%" colspan="5">
								<bean:message bundle="datasourceResources" key="ldap.ldapList" />
							</td>
						</tr>
						<tr>
							<td class="btns-td" valign="middle" colspan="6">
						</tr>
						<tr  class="vspace">
							<td class="btns-td" valign="middle" colspan="5">
								<input type="button" value="   Create   " tabindex="3" name="c_btnCreate" class="light-btn" onclick="javascript:location.href='<%=basePath%>/initCreateLDAPDS.do?/>'"/>
								<input type="button" name="Delete" tabindex="4" Onclick="removeRecord()" value="   Delete   " class="light-btn">										
							</td>
						</tr>
						<tr class="vspace">
						<td class="btns-td" valign="middle" colspan="9">
							<table width="100%" border="0" cellpadding="0" cellspacing="0"
								id="listTable">
								<tr>
									<td align="center" class="tblheader" valign="middle" width="1%" >
										<input type="checkbox" name="toggleAll" tabindex="7" value="checkbox" onclick="checkAll()" />
									</td>
									<td align="center" class="tblheaderfirstcol" valign="middle" width="10%" >Sr. No.</td>
									<td align="center" class="tblheader" valign="middle" width="20%" ><bean:message key="general.name"/></td>								
									<td align="center" class="tblheader" valign="middle" width="20%"><bean:message key="general.address"/></td>
									<td align="center" class="tblheader" valign="middle" width="10%"><bean:message key="general.administrator"/></td>
									<td align="center" class="tblheader" valign="middle" width="20%"><bean:message key="general.createddate"/></td>
									<td align="center" class="tblheader" valign="middle" width="20%"><bean:message key="general.lastmodifieddate"/></td>
									<td align="center" class="tblheaderlastcol" valign="middle" width="4%"><bean:message key="general.edit"/></td>
								</tr>
							<%if(searchLDAPList != null && searchLDAPList.size() >0){ %>
							<%int index = 0; %>
								<logic:iterate id="obj" name="searchLDAPList"  type="LDAPDatasourceData">									
								<tr>
									<td align="center" class="tblfirstcol">
									<input type="checkbox" tabindex="8" name="select" value="<bean:write name="obj" property="ldapDsId"/>" />
									</td>
							   		<td align="left" class="tblrows"><%=(index+1)%></td>
							   		<td align="left" class="tblrows"><a href="viewLDAPDS.do?ldapDsId=<bean:write name="obj" property="ldapDsId"/>" tabindex="8"><bean:write name="obj" property="name" /></a></td>
							   		<td align="left" class="tblrows"><bean:write name="obj" property="address"/></td>
							   		<td align="left" class="tblrows"><bean:write name="obj" property="administrator"/></td>
							   		<td align="left" class="tblrows">
							   			<%=EliteUtility.dateToString(obj.getCreatedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>
							   		<td align="left" class="tblrows">
							   			<%=EliteUtility.dateToString(obj.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>
							   		<td align="center" class="tblrows">
										<a href="initupdateLDAPDS.do?ldapDsId=<bean:write name="obj" property="ldapDsId"/>" tabindex="8">
											<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
										</a>
									</td>
							  	</tr>
								<% index++; %>
								</logic:iterate>
							<%}else{ %>
								<tr>
	                  				<td align="center" class="tblfirstcol" colspan="8">No Records Found.</td>
	                			</tr>
							<%	}%>
							</table>
							</td>
						</tr>
						<tr  class="vspace">
							<td class="btns-td" valign="middle" colspan="5">	
								<input type="button" value="   Create   " tabindex="9" name="c_btnCreate" class="light-btn" onclick="javascript:location.href='<%=basePath%>/initCreateLDAPDS.do?/>'"/>
								<input type="button" name="Delete" tabindex="10" OnClick="removeRecord()" value="   Delete   " class="light-btn">									
							</td>
						</tr>
						<tr  class="vspace">
							<td class="btns-td" valign="middle" colspan="6"></td>
						</tr>
							
					  	</table>
					   </td>	
				</tr>	
				<%} %>
			</table>
			</td>
			</tr>
			<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
	</table>
</html:form>
		