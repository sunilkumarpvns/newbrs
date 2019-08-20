<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData"%>
<%@ page import="com.elitecore.elitesm.web.datasource.ldap.forms.SearchLDAPDatasourceForm"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
    String basePath = request.getContextPath();
	SearchLDAPDatasourceForm searchLDAPDatasourceForm = (SearchLDAPDatasourceForm)request.getAttribute("searchLDAPDS");
	List searchLDAPList = searchLDAPDatasourceForm.getSearchList();
	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	
	long pageNo = searchLDAPDatasourceForm.getPageNumber();
    long totalPages = searchLDAPDatasourceForm.getTotalPages();
    long totalRecord = searchLDAPDatasourceForm.getTotalRecords();
	int count=1;
	
	String strPageNumber = String.valueOf(pageNo);     
	String strTotalPages = String.valueOf(totalPages);
	String strTotalRecords = String.valueOf(totalRecord);
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>

function validateSearch(){
	document.forms[0].pageNumber.value=1;
	document.forms[0].action.value='search';
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

function removeData(){
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

function showall(){  
	var path = '<%=basePath%>/searchLDAPDS.do?action=showall'; 
	window.open(path,'DiameterPeer','resizable=yes,scrollbars=yes');
}

setTitle('<bean:message bundle="datasourceResources" key="ldap.ldap"/>');
</script>

<table cellpadding="0" cellspacing="0" border="0"
	width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td width="100%" class="box">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td class="table-header" colspan="5"><bean:message
										bundle="datasourceResources" key="ldap.searchldap" /></td>
							</tr>
							<tr>
								<td colspan="3">
									<table width="100%" name="c_tblDiameterPeer"
										id="c_tblDiameterPeer" align="right" border="0"
										cellpadding="0" cellspacing="0">
										<html:form action="/searchLDAPDS">
											<html:hidden name="searchLDAPDS" styleId="pageNumber"
												property="pageNumber" />
											<html:hidden name="searchLDAPDS" styleId="totalPages"
												property="totalPages" value="<%=strTotalPages%>" />
											<html:hidden name="searchLDAPDS" styleId="totalRecords"
												property="totalRecords" value="<%=strTotalRecords%>" />
											<html:hidden name="searchLDAPDS" styleId="action"
												property="action" />
											<tr>
												<td align="left" class="btns-td" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="100%"
														border="0">
														<tr>
															<td colspan='100%'>&nbsp;</td>
														</tr>
														<tr>
															<td align="left" class="labeltext" valign="top"
																width="8%">
																<bean:message bundle="datasourceResources" key="ldap.name" /> 
																<ec:elitehelp headerBundle="datasourceResources" text="ldapdatasource.name" header="ldap.name"/>
																<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ldap.name"/>','<bean:message bundle="datasourceResources" key="ldap.name"/>')" /> --%>

															</td>
															<td align="left" class="labeltext" valign="top"
																width="22%"><html:text styleId="name"
																	property="name" tabindex="1" size="30" maxlength="256"
																	style="width:250px" /></td>
														</tr>
														<tr>
															<td>&nbsp;</td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle">&nbsp;</td>
															<td align="left" class="labeltext" valign="top"
																width="5%"><input type="button" name="Search"
																tabindex="2" width="5%" Onclick="validateSearch()"
																value="  Search  " class="light-btn" /> <input
																type="button" name="Create" tabindex="3"
																value="   Create   " onclick="javascript:create();"
																class="light-btn"></td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="99%"
														border="0">
														<tr>
															<td class="table-header" width="50%"><bean:message
																	bundle="datasourceResources" key="ldap.ldapList" /></td>

															<td align="right" class="blue-text" valign="middle"
																width="50%">
																<% if(totalRecord == 0) { %> <% }else if(pageNo == totalPages+1) { %>
																[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
																<% } else if(pageNo == 1) { %> [<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>]
																of <%= totalRecord %> <% } else { %> [<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>]
																of <%= totalRecord %> <% } %>

															</td>
														</tr>
														<tr>

															<td></td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle"><html:button
																	property="c_btnDelete" tabindex="4" onclick="removeData()"
																	value="   Delete   " styleClass="light-btn" /> <html:button
																	property="c_btnshowall" tabindex="5"
																	onclick="showall()" value="   Show All   "
																	styleClass="light-btn" /></td>
															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="javascript:void(0)"
																onclick="navigate(<%=pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%=totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a href="javascript:void(0)" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a href="javascript:void(0)"
																onclick="navigate(<%= pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else if(pageNo == totalPages){ %> <a
																href="javascript:void(0)" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a href="javascript:void(0)"
																onclick="navigate(<%= pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else { %> <a href="javascript:void(0)"
																onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a href="javascript:void(0)"
																onclick="navigate(<%= pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= totalPages+1%>);""><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="javascript:void(0)" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%=pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <% } %> <% } %>
															</td>
														</tr>
														<tr height="2">
															<td></td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle" colspan="2">
																<table width="100%" border="0" cellpadding="0"
																	cellspacing="0" id="listTable">
																	<tr>
																		<td align="center" class="tblheader" valign="top"
																			width="1%"><input type="checkbox"
																			name="toggleAll" value="checkbox"
																			onclick="checkAll()" /></td>
																		<td align="center" class="tblheader" valign="top"
																			width="40px">Sr. No.</td>
																		<td align="left" class="tblheader" valign="top"
																			width="15%">Name</td>
																		<td align="left" class="tblheader" valign="top"
																			width="15%">Address</td>
																		<td align="left" class="tblheader" valign="top"
																			width="14%">Min Connections</td>
																		<td align="left" class="tblheader" valign="top"
																			width="14%">Max Connections</td>
																		<td align="left" class="tblheader" valign="top"
																			width="15%">Timeout</td>
																		<td align="left" class="tblheader" valign="top"
																			width="15%"><bean:message
																				bundle="datasourceResources" key="ldap.userdnprefix" /></td>
																		<td align="center" class="tblheader" valign="top"
																			width="40px">Edit</td>
																	</tr>
																	<%	if(searchLDAPList!=null && searchLDAPList.size()>0){%>
																	<logic:iterate id="obj" name="searchLDAPDS" property="searchList"
																		type="com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData">
																		<tr>
																			<td align="center" class="tblfirstcol"><input
																				type="checkbox" name="select"
																				value="<bean:write name="obj" property="ldapDsId"/>" />
																			</td>
																			<td align="center" class="tblfirstcol"><%=((pageNo-1)*pageSize)+count%></td>
																			<td align="left" class="tblrows"><a
																				href="<%=basePath%>/viewLDAPDS.do?ldapDsId=<bean:write name="obj" property="ldapDsId"/>"><bean:write
																						name="obj" property="name" /></a></td>
																			<td align="left" class="tblrows"><bean:write
																					name="obj" property="address" />&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="obj" property="minimumPool" />&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="obj" property="maximumPool" />&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="obj" property="timeout" />&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="obj" property="userDNPrefix" />&nbsp;</td>
																			<td align="center" class="tblrows"><a
																				href="<%=basePath%>/viewLDAPDS.do?ldapDsId=<bean:write name="obj" property="ldapDsId"/>">
																					<img src="<%=basePath%>/images/edit.jpg" alt="Edit"
																					border="0">
																			</a></td>
																		</tr>
																		<% count++;%>
																	</logic:iterate>

																	<%}else{ %>
																	<tr>
																		<td align="center" class="tblfirstcol" colspan="8">No
																			Records Found.</td>
																	</tr>
																	<%}%>
																</table>
															</td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle"><html:button
																	property="c_btnDelete" onclick="removeData()"
																	value="   Delete   " styleClass="light-btn" /> <html:button
																	property="c_btnshowall" onclick="showall()"
																	value="   Show All   " styleClass="light-btn" /></td>
															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="javascript:void(0)"
																onclick="navigate(<%=pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%=totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a href="javascript:void(0)" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a href="javascript:void(0)"
																onclick="navigate(<%= pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else if(pageNo == totalPages){ %> <a
																href="javascript:void(0)" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a href="javascript:void(0)"
																onclick="navigate(<%= pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else { %> <a href="javascript:void(0)"
																onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a href="javascript:void(0)"
																onclick="navigate(<%= pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= totalPages+1%>);""><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="void(0)" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="void(0)" onclick="navigate(<%=pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <% } %> <% } %>
															</td>
														</tr>
														<tr height="2">
															<td></td>
														</tr>
													</table>
												</td>
											</tr>
										</html:form>
									</table>
								</td>
							</tr>

						</table>
					</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
			</table>
		</td>
	</tr>
</table>

<script language="javascript">
  document.forms[0].name.focus();
  function navigate(pageNo){
	 document.searchLDAPDS.action="searchLDAPDS.do?pageNo="+pageNo;
	 document.searchLDAPDS.submit();  
  }
  
  function create(){
	  location.href='<%=basePath%>/initCreateLDAPDS.do?name='+document.searchLDAPDS.name.value;
  }
</script>
