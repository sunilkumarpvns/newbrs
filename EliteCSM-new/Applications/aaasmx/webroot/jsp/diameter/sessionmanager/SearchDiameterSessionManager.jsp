<%@page import="com.elitecore.elitesm.web.diameter.sessionmanager.form.SearchDiameterSessionManagerForm"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData" %>
<%@page import="com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>

<%@ page import="com.elitecore.elitesm.util.constants.SessionManagerConstant"%>
<%@ page import="com.elitecore.elitesm.hibernate.sessionmanager.HSessionManagerDataManager"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.util.PageList"%>

<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>


<%
    
    SearchDiameterSessionManagerForm searchDiameterSessionManagerForm =(SearchDiameterSessionManagerForm)request.getAttribute("searchDiameterSessionManagerForm");
    
    long pageNo = searchDiameterSessionManagerForm.getPageNumber();
    long totalPages = searchDiameterSessionManagerForm.getTotalPages();
    long totalRecord = searchDiameterSessionManagerForm.getTotalRecords();
    String action = searchDiameterSessionManagerForm.getAction();
    List diameterSessionManagerList = searchDiameterSessionManagerForm.getListSessionManager();
    
    String strTotalPages = String.valueOf(totalPages);
    String strTotalRecords = String.valueOf(totalRecord);
    String strPageNumber = String.valueOf(pageNo);
    String strName=searchDiameterSessionManagerForm.getName() != null ? searchDiameterSessionManagerForm.getName() : "";
    
    String basePath = request.getContextPath();
    String strDatePattern = "dd MMM,yyyy hh:mm:ss";
    SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
    Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
    int iIndex =0;
    int count=1;
    String paramString="name="+strName;
    
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<style>
.light-btn {
	border: medium none;
	font-family: Arial;
	font-size: 12px;
	color: #FFFFFF;
	background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg');
	font-weight: bold
}
</style>

<script>
 function validateSearch(){
	    document.forms[0].pageNumber.value = 1;
		document.forms[0].submit();
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
	        alert('At least select one Session Manager Instance for remove process');
	    }else{
	        var msg;
	        msg = '<bean:message bundle="alertMessageResources" key="alert.searchsessionmanager.delete.query"/>';        
	        var agree = confirm(msg);
	        if(agree){
	        	document.forms[0].action.value = 'delete';
	        	document.forms[0].submit();
	        }
	    }
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
 function navigatePageWithType(action,appendAttrbId) {
		createNewForm("newFormData",action);
		var name = $("#"+appendAttrbId).attr("name");
		var val = $("#"+appendAttrbId).val();
		$("#newFormData").append("<input type='hidden' name='"+name+"' value='"+val+"'>")
		
		var name = $("#smType").attr("name");
		var val=$('input[name=smType]:radio:checked').val();
		
		$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+val+"'>")
						 .submit();
	}
setTitle('<bean:message bundle="sessionmanagerResources" key="sessionmanager.header"/>');
</script>

<html:form action="/searchDiameterSessionManager">

	<html:hidden name="searchDiameterSessionManagerForm" styleId="action" property="action" />
	<html:hidden name="searchDiameterSessionManagerForm" styleId="pageNumber" property="pageNumber" />
	<html:hidden name="searchDiameterSessionManagerForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
	<html:hidden name="searchDiameterSessionManagerForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
						
	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header" colspan="3">
										<bean:message  bundle="sessionmanagerResources" key="diameter.sessionmanager.searchsessionmanager"/>
									</td>
									</tr>
									<tr>
										<td class="small-gap" colspan="3">&nbsp;</td>
									</tr>
									<tr>
										<td class="small-gap" colspan="3">&nbsp;</td>
									</tr>
									<tr>
										<td align="left" class="captiontext" valign="top" width="10%">
											<bean:message bundle="sessionmanagerResources" key="sessionmanager.name" />
											<ec:elitehelp headerBundle="sessionmanagerResources" text="sessionmanager.name" header="sessionmanager.name"/>
										</td>	
										<td align="left" class="labeltext" valign="top" width="32%">
											<html:text styleId="name" property="name" size="30" maxlength="30" tabindex="1" style="width:250px;"/>
										</td>
									</tr>
									<tr>
										<td colspan="3">&nbsp;</td>
									</tr>
									<tr>
										<td class="labeltext" valign="middle">&nbsp;</td>
										<td align="left" class="labeltext" valign="top" width="5%">
											<input type="button" name="Search" width="5%" name="SessionManagerName" Onclick="validateSearch()" value="   Search   " class="light-btn" tabindex="5" /> 
											<input type="button" name="Create" onclick="javascript:navigatePageWithType('initCreateDiameterSessionManager.do','name');" value="   Create   " class="light-btn" tabindex="6">
										</td>
									</tr>
									<tr>
										<td colspan="3">&nbsp;</td>
									</tr>
									<%if(action.equals("list")) {%>
									<tr>
										<td align="left" class="labeltext" colspan="5" valign="top">
											<table cellSpacing="0" cellPadding="0" width="99%" border="0">
												<tr>
													<td class="table-header" width="50%">
														<bean:message bundle="sessionmanagerResources" key="sessionmanager.list" />
													</td>
													<td align="right" class="blue-text" valign="middle" width="50%">
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
												<td class="btns-td" valign="middle">
													<input type="button" name="Delete" onclick="removeData()" value="   Delete   " class="light-btn" tabindex="10">
												</td>
												<td class="btns-td" align="right">
													<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else if(pageNo == totalPages){ %> <a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else { %> <a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<% } %> <% } %>
												</td>
											</tr>
											<tr height="2">
												<td></td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle" colspan="2">
													<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listTable">
														<tr>
															<td align="center" class="tblheader" valign="top"
																width="1%"><input type="checkbox" name="toggleAll"
																value="checkbox" onclick="checkAll()" /></td>
															<td align="center" class="tblheader" valign="top"
																width="40px">Sr. No.</td>
															<td align="left" class="tblheader" valign="top"
																width="20%">Name</td>
															<td align="left" class="tblheader" valign="top" width="23%">Description</td>
															<td align="left" class="tblheader" valign="top" width="23%">Data Source</td>
															<td align="left" class="tblheader" valign="top" width="23%">Table Name</td>
															<td align="center" class="tblheader" valign="top"
																width="40px">Edit</td>
														</tr>
														<tr>
															<%if(diameterSessionManagerList != null && diameterSessionManagerList.size() >0){ %>

															<logic:iterate id="obj" name="searchDiameterSessionManagerForm" property="listSessionManager" type="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData">
																<tr>
																	<td align="center" class="tblfirstcol">
																		<input type="checkbox" name="select" value="<bean:write name="obj" property="sessionManagerId"/>" />
																	</td>
																	<td align="center" class="tblrows"><%=((pageNo-1)*pageSize)+count%></td>
																	<td align="left" class="tblrows">
																		<a href="<%=request.getContextPath()%>/viewDiameterSessionManager.do?sessionManagerId=<%=obj.getSessionManagerId()%>">
																			<bean:write name="obj" property="name" />
																		</a>
																	</td>
																	<td align="left" class="tblrows"><%=EliteUtility.formatDescription(obj.getDescription()) %>&nbsp;&nbsp;</td>
																	<td align="left" class="tblrows">
																		<logic:iterate id="databaseDsDataObj" name="searchDiameterSessionManagerForm" property="databaseDSDataList" type="com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData">
																			<%if(obj.getDatabaseDatasourceId().equals(databaseDsDataObj.getDatabaseId())){ %>
																				<%=databaseDsDataObj.getName() %>
																			<%} %>
																		</logic:iterate>
																	</td>
																	<td align="left" class="tblrows" valign="top"><bean:write name="obj" property="tableName" /></td>
																	<td align="center" class="tblrows"><a href="<%=request.getContextPath()%>/initUpdateDiameterSessionManager.do?sessionManagerId=<%=obj.getSessionManagerId()%>">
																			<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
																	</a></td>
																</tr>
																<%count ++ ;%>
															</logic:iterate>
															<%}else{ %>
															<tr>
																<td align="center" class="tblfirstcol" colspan="8">
																	No Records Found.
																</td>
															</tr>
														<%	}%>
														</tr>
													</table>
												</td>
											</tr>

											<tr>
												<td class="btns-td" valign="middle">
													 <html:button property="c_btnDelete" onclick="removeData()" value="   Delete   " styleClass="light-btn" />
												</td>
												<td class="btns-td" align="right">
													<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else if(pageNo == totalPages){ %> <a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else { %> <a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchDiameterSessionManager.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<% } %> <% } %>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<%} %>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
</html:form>