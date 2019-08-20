<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.web.driver.forms.SearchDriverInstanceForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.*"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.service.data.ServiceTypeData"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
    	String basePath = request.getContextPath();

    	SearchDriverInstanceForm searchForm = (SearchDriverInstanceForm) session
    			.getAttribute("searchForm");
    	List driversList = searchForm.getDriverList();
    	System.out.println(driversList);
    	String action = searchForm.getAction();
    	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
    	long pageNo = searchForm.getPageNumber();
    	long totalPages = searchForm.getTotalPages();
    	long totalRecord = searchForm.getTotalRecords();
    	String strTotalPages=String.valueOf(totalPages);
    	String strTotalRecords=String.valueOf(totalRecord);
    	String strName = searchForm.getName();
    	//String strDriverType="0";
    	String strDriverType = searchForm.getSelecteDriver();
    	
    	String paramString = "name=" + strName + "&driverType="+ strDriverType;
        int count=1;
    %>


<script>

function validateForm(){
	document.forms[0].pageNumber.value = 1;
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
        alert('At least select Driver Instance for remove process');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searchdriver.delete.query"/>';        
        //msg = "All the selected Staff Personnel would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){            
       	    document.forms[0].action.value = 'delete';
        	document.forms[0].submit();
        }
    }
}

function navigatePageWithDriverType(action,appendAttrbId) {
	createNewForm("newFormData",action);
	var name = $("#"+appendAttrbId).attr("name");
	var val = $("#"+appendAttrbId).val();
	$("#newFormData").append("<input type='hidden' name='"+name+"' value='"+val+"'>")
	
	var name = $("#selecteDriver").attr("name");
	var val = $("#selecteDriver").val();
	$("#newFormData").append("<input type='hidden' name='"+name+"' value='"+val+"'>")
					 .submit();
}

</script>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<html:form action="/searchDriverInstance">
	<html:hidden property="action" styleId="action" />
	<html:hidden property="pageNumber" styleId="pageNumber" />
	<html:hidden property="totalPages" styleId="totalPages"
		value="<%=strTotalPages%>" />
	<html:hidden property="totalRecords" styleId="totalRecords"
		value="<%=strTotalRecords%>" />

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
											bundle="driverResources" key="driver.searchdriver" /></td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top" width="18%">
										<bean:message bundle="driverResources" key="driver.name" /> 
										<ec:elitehelp headerBundle="driverResources" 
										text="createdriver.name" header="driver.name"/>
									</td>
									<td align="left" class="labeltext" valign="top" width="66%">
										<html:text styleId="name" property="name" size="30"
											maxlength="60" style="width:250px" tabindex="1" />
									</td>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top" width="18%">
										<bean:message bundle="driverResources" key="driver.drivertype" />
										<ec:elitehelp headerBundle="driverResources" 
										text="createdriver.type" header="driver.drivertype"/>
									</td>
									<td align="left" class="labeltext" valign="top" width="66%">
										<html:select name="searchDriverInstanceForm"
											styleId="selecteDriver" property="selecteDriver" tabindex="2" style="width:250px;">
											<html:option value="0">Select</html:option>
											<logic:iterate id="objservice"
												name="searchDriverInstanceForm" type="ServiceTypeData"
												property="serviceList">
												<optgroup label="<%=objservice.getDisplayName()%>"
													class="labeltext">
													<logic:iterate id="objdriver" name="objservice"
														type="DriverTypeData" property="driverTypeSet">
														<html:option
															value="<%=String.valueOf(objdriver.getDriverTypeId())%>"><%=objdriver.getDisplayName()%>
														</html:option>
													</logic:iterate>
												</optgroup>
											</logic:iterate>
										</html:select>
									</td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="labeltext" valign="middle">&nbsp;</td>
									<td class="labeltext" valign="middle" colspan="2"><input
										type="button" name="c_btnCreate" id="c_btnCreate2"
										value=" Search " class="light-btn" onclick="validateForm()"
										tabindex="3"> <%-- <input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?/>'" value="Cancel" class="light-btn"> --%>
										<input type="button" name="Create" value="   Create   "
										class="light-btn"
										onclick="javascript:navigatePageWithDriverType('initCreateDriver.do','name');"
										tabindex="4"></td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>

								<%
					if (action.equals("list")) {
				%>
								<tr>
									<td align="left" class="labeltext" colspan="5" valign="top">
										<table width="99%" cellpadding="0" cellspacing="0" border="0">
											<tr>
												<td class="table-header" width="50%"><bean:message
														bundle="driverResources" key="driver.list" /></td>
												<td align="right" class="blue-text" valign="middle"
													width="50%">
													<%
					    					if (totalRecord == 0) {
					    				%> <%
												} else if (pageNo == totalPages + 1) {
											%> [<%=((pageNo - 1) * pageSize) + 1%>-<%=totalRecord%>] of <%=totalRecord%>
													<%
												  	} else if (pageNo == 1) {
												  %> [<%=(pageNo - 1) * pageSize + 1%>-<%=(pageNo - 1) * pageSize + pageSize%>]
													of <%=totalRecord%> <%
												} else {
											%> [<%=((pageNo - 1) * pageSize) + 1%>-<%=((pageNo - 1) * pageSize) + pageSize%>]
													of <%=totalRecord%> <%
												}
											%>

												</td>
											</tr>
											<tr>
												<td></td>
											</tr>

											<tr>
												<td class="btns-td" valign="middle"><input
													type="button" name="Delete" OnClick="removeData()"
													value="   Delete   " class="light-btn" tabindex="5"></td>
												<td class="btns-td" align="right">
													<%
									  		if (totalPages >= 1) {
									  	%> <%
										  		if (pageNo == 1) {
										  	%> <a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=pageNo + 1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=totalPages + 1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<%
										  		}
										  	%> <%
												if (pageNo > 1 && pageNo != totalPages + 1) {
											%> <%
													if (pageNo - 1 == 1) {
												%> <a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=pageNo - 1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=pageNo + 1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=totalPages + 1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<%
													} else if (pageNo == totalPages) {
												%> <a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=pageNo - 1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=pageNo + 1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=totalPages + 1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<%
													} else {
												%> <a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=pageNo - 1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=pageNo + 1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=totalPages + 1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<%
													}
												%> <%
												}
											%> <%
											if (pageNo == totalPages + 1) {
										%> <a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=pageNo - 1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<%
											}
										%> <%
								  	}
								  %>
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
																width="25%">Driver Type</td>
															<td align="left" class="tblheader" valign="top" width="*">Driver
																Description</td>
															<td align="center" class="tblheader" valign="top"
																width="40px">Edit</td>

														</tr>


														<%
															if (driversList != null && !driversList.isEmpty()) {
														%>

														<logic:iterate id="obj" name="searchDriverInstanceForm"
															property="driverList" type="DriverInstanceData">
															<tr>
																<td align="center" class="tblfirstcol"><input
																	type="checkbox" name="select"
																	value="<bean:write name="obj" property="driverInstanceId"/>" />
																</td>
																<td align="center" class="tblfirstcol"><%=((pageNo-1)*pageSize)+count%></td>
																<td align="left" class="tblrows"><a
																	href="viewDriverInstance.do?action=view&driverInstanceId=<bean:write name="obj" property="driverInstanceId"/>"><bean:write
																			name="obj" property="name" /></a></td>
																<td align="left" class="tblrows"><bean:write
																		name="obj" property="driverTypeName" /></td>
																<td align="left" class="tblrows"><%=EliteUtility.formatDescription(obj.getDescription())%>&nbsp;&nbsp;</td>
																<td align="center" class="tblrows"><a
																	href="initupdateDriverInstance.do?driverInstanceId=<bean:write name="obj" property="driverInstanceId"/>">
																		<img src="<%=basePath%>/images/edit.jpg" alt="Edit"
																		border="0">
																</a></td>
															</tr>

															<% count=count+1; %>

														</logic:iterate>
														<%
									} else {
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

											<tr>
												<td class="btns-td" valign="middle"><input
													type="button" name="Delete" OnClick="removeData()"
													value="   Delete   " class="light-btn"></td>
												<td class="btns-td" align="right">
													<%
							  		if (totalPages >= 1) {
							  	%> <%
										  		if (pageNo == 1) {
										  	%> <a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=pageNo + 1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=totalPages + 1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<%
										  		}
										  	%> <%
												if (pageNo > 1 && pageNo != totalPages + 1) {
											%> <%
													if (pageNo - 1 == 1) {
												%> <a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=pageNo - 1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=pageNo + 1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=totalPages + 1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<%
													} else if (pageNo == totalPages) {
												%> <a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=pageNo - 1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=pageNo + 1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=totalPages + 1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<%
													} else {
												%> <a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=pageNo - 1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=pageNo + 1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=totalPages + 1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<%
													}
												%> <%
												}
											%> <%
											if (pageNo == totalPages + 1) {
										%> <a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchDriverInstance.do?<%=paramString%>&action=list&pageNo=<%=pageNo - 1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<%
											}
										%> <%
								  	}
								  %>
												</td>

											</tr>
										</table>
									</td>
								</tr>
								<%
						}
					%>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
</html:form>
<script>
setTitle('<bean:message bundle="driverResources" key="driver.driver"/>'); 	
</script>
