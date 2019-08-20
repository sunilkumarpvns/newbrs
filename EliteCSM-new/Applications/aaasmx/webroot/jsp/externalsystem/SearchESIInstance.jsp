<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List,com.elitecore.elitesm.datamanager.externalsystem.data.ESITypeAndInstanceData,com.elitecore.elitesm.web.externalsystem.forms.SearchESIInstanceForm "%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData"%>
<%
    String basePath = request.getContextPath();
	SearchESIInstanceForm searchESIInstance = (SearchESIInstanceForm)request.getAttribute("searchESIInstance");
	String action = searchESIInstance.getAction();
	List externalSystemInterfaceList = (List)request.getAttribute("externalSysInterfaceList");
	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
  
	long pageNo = searchESIInstance.getPageNumber();
    long totalPages = searchESIInstance.getTotalPages();
    long totalRecord = searchESIInstance.getTotalRecords();
	int count=1;
	
	 String strPageNumber = String.valueOf(pageNo);     
	 String strTotalPages = String.valueOf(totalPages);
	 String strTotalRecords = String.valueOf(totalRecord);
	 String strName = searchESIInstance.getName() != null ? searchESIInstance.getName() : "";
	 String esiTypeId = searchESIInstance.getEsiTypeId();
	    
	 String pageNavigationLink="searchESIInstance.do?name="+strName+"&action=list&esiTypeId="+esiTypeId+"&pageNo=";
	 
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<script>
function validateForm(){
	document.forms[0].pageNumber.value=1;
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
function navigatePageWithInfo(action,appendAttrbId) {
	createNewForm("newFormData",action);
	var name = $("#"+appendAttrbId).attr("name");
	var val = $("#"+appendAttrbId).val();
	$("#newFormData").append("<input type='hidden' name='"+name+"' value='"+val+"'>")

	var name = $("#esiTypeId").attr("name");
	var val=$("#esiTypeId option:selected").val();
	
	$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+val+"'>")
					 .submit();
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
        alert('At least select one ESI for remove process');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searchESIjsp.delete.query"/>';        
        //msg = "All the selected Staff Personnel would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
       	    document.forms[0].action.value = 'delete';       	           	  
        	document.forms[0].submit();
        }
    }
}
setTitle('<bean:message bundle="externalsystemResources" key="externalsystem.externalsystem"/>');
</script>

<html:form action="/searchESIInstance">
	<html:hidden styleId="action" property="action" />
	<html:hidden name="searchESIForm" styleId="pageNumber"
		property="pageNumber" value="<%=strPageNumber%>" />
	<html:hidden name="searchESIForm" styleId="totalPages"
		property="totalPages" value="<%=strTotalPages%>" />
	<html:hidden name="searchESIForm" styleId="totalRecords"
		property="totalRecords" value="<%=strTotalRecords%>" />
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
									<td class="table-header" colspan="5"><bean:message
											bundle="externalsystemResources" key="esi.searchesi" /></td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top" width="18%">
										<bean:message bundle="externalsystemResources" key="esi.name" />
										<ec:elitehelp headerBundle="externalsystemResources" text="esi.name" header="esi.name"/>
										<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.name"/>','<bean:message bundle="externalsystemResources" key="esi.name"/>')" /> --%>
									</td>
									<td align="left" class="labeltext" valign="top" width="66%">
										<html:text styleId="name" property="name" size="30"
											maxlength="60" style="width:250px" tabindex="1" />
									</td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" width="10%">
										<bean:message bundle="externalsystemResources" key="esi.esitype" /> 
										<ec:elitehelp headerBundle="externalsystemResources" text="esi.esitype" header="esi.esitype"/>
										<%-- <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="esi.esitype"/>','<bean:message bundle="externalsystemResources" key="esi.esitype"/>')" /> --%>
									</td>
									<td align="left" class="labeltext" valign="top" width="32%">
										<html:select property="esiTypeId" style="width:130px" styleId="esiTypeId"
											tabindex="2">
											<html:option value="0">Select</html:option>
											<html:optionsCollection name="searchESIForm"
												property="esiTypeList" label="name" value="esiTypeId" />
										</html:select>
									</td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>

								<tr>
									<td class="labeltext" valign="middle">&nbsp;</td>
									<td class="labeltext" valign="middle" colspan="2"><input
										type="button" tabindex="3" name="c_btnCreate"
										id="c_btnCreate2" value=" Search " class="light-btn"
										onclick="validateForm()"> <%-- <input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchESIInstance.do?/>'" value="Cancel" class="light-btn"> --%>
										<input type="button" name="Create" tabindex="4"
										value="   Create   " class="light-btn"
										onclick="javascript:navigatePageWithInfo('initCreateExternalInterfaceInstance.do','name');">
									</td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<%if(action.equals("list")) {%>
								<tr>
									<td align="left" class="labeltext" colspan="5" valign="top">
										<table width="99%" cellpadding="0" cellspacing="0" border="0">
											<tr>
												<td class="table-header" width="50%"><bean:message
														bundle="externalsystemResources" key="esi.esilist" /></td>
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
												<td class="btns-td" valign="middle">&nbsp; <input
													type="button" tabindex="5" name="Delete" OnClick="removeData()"
													value="   Delete   " class="light-btn">
												</td>

												<td class="btns-td" align="right">
													<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
													href="<%=pageNavigationLink+(pageNo+1)%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a href="<%=pageNavigationLink+(totalPages+1)%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <a
													href="<%=pageNavigationLink+(1L)%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a href="<%=pageNavigationLink+(pageNo-1)%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a href="<%=pageNavigationLink+(pageNo+1)%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a href="<%=pageNavigationLink+(totalPages+1)%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

													<% } %> <% if(pageNo == totalPages+1) { %> <a
													href="<%=pageNavigationLink+(1L)%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a href="<%=pageNavigationLink+(pageNo-1)%>"><img
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
													<table width="100%" border="0" cellpadding="0"
														cellspacing="0" id="listTable">
														<tr>
															<td align="center" class="tblheader" valign="top"
																width="1%"><input type="checkbox" name="toggleAll"
																value="checkbox" onclick="checkAll()" /></td>
															<td align="center" class="tblheader" valign="top"
																width="40px">Sr. No.</td>
															<td align="left" class="tblheader" valign="top"
																width="25%">Name</td>
															<td align="left" class="tblheader" valign="top"
																width="30%">Address</td>
															<td align="left" class="tblheader" valign="top" width="*">Extended
																Radius Type</td>
															<td align="center" class="tblheader" valign="top"
																width="40px">Edit</td>

														</tr>

														<%if(externalSystemInterfaceList != null && externalSystemInterfaceList.size() >0){ %>
														<logic:iterate id="obj" name="externalSysInterfaceList"
															type="ESITypeAndInstanceData">
															<tr>
																<td align="center" class="tblfirstcol"><input
																	type="checkbox" name="select"
																	value="<bean:write name="obj" property="esiInstanceId"/>" />
																</td>
																<td align="center" class="tblfirstcol"><%=((pageNo-1)*pageSize)+count%></td>
																<td align="left" class="tblrows"><a
																	href="viewESI.do?esiInstanceId=<bean:write name="obj" property="esiInstanceId"/>"><bean:write
																			name="obj" property="name" /></a></td>
																<td align="left" class="tblrows"><bean:write
																		name="obj" property="address" /></td>
																<td align="left" class="tblrows"><bean:write
																		name="obj" property="esiTypeName" /></td>
																<td align="center" class="tblrows"><a
																	href="initupdateESI.do?esiInstanceId=<bean:write name="obj" property="esiInstanceId"/>">
																		<img src="<%=basePath%>/images/edit.jpg" alt="Edit"
																		border="0">
																</a></td>
															</tr>
															<% count++; %>
														</logic:iterate>
														<%}else{ %>
														<tr>
															<td align="center" class="tblfirstcol" colspan="8">No
																Records Found.</td>
														</tr>
														<%	}%>
													</table>
												</td>
											</tr>

											<tr>
												<td class="btns-td" valign="middle"><input
													type="button" name="Delete" OnClick="removeData()"
													value="   Delete   " class="light-btn" /></td>

												<td class="btns-td" align="right">
													<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
													href="<%=pageNavigationLink+(pageNo+1)%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a href="<%=pageNavigationLink+(totalPages+1)%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <a
													href="<%=pageNavigationLink+(1L)%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a href="<%=pageNavigationLink+(pageNo-1)%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a href="<%=pageNavigationLink+(pageNo+1)%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a href="<%=pageNavigationLink+(totalPages+1)%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

													<% } %> <% if(pageNo == totalPages+1) { %> <a
													href="<%=pageNavigationLink+(1L)%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a href="<%=pageNavigationLink+(pageNo-1)%>"><img
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
