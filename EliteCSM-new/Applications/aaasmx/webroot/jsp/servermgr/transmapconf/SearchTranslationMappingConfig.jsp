<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.web.servermgr.transmapconf.forms.SearchTranslationMappingConfigForm"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	String basePath = request.getContextPath();	
	SearchTranslationMappingConfigForm configForm = (SearchTranslationMappingConfigForm)request.getAttribute("configForm");
	List mappingConfigList = configForm.getConfigList();
	String action = configForm.getAction();
	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	String status = configForm.getStatus();
	
	long pageNo = configForm.getPageNumber();
    long totalPages = configForm.getTotalPages();
    long totalRecord = configForm.getTotalRecords();
	int count=1;
    
    
    String strPageNumber = String.valueOf(pageNo);     
    String strTotalPages = String.valueOf(totalPages);
    String strTotalRecords = String.valueOf(totalRecord);
    String strName=configForm.getName() != null ? configForm.getName() :"";
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
function prepareUrl(image,value,sortOrderValue){
	var name = '';
	image.href = image.href + escape(name);
	makeUrl(image,value,sortOrderValue);
}
function navigate(direction, pageNumber ){
	document.forms[0].pageNumber.value = pageNumber;
	document.forms[0].submit();
}

function removeData(){
    var selectVar = false;
    
    for (i=0; i < document.forms[0].elements.length; i++){
        if(document.forms[0].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[0].elements[i].checked == true){
            	   selectVar = true;
            }
             
        }
    }
    if(selectVar == false){
        alert('At least select one Config Instance for remove process');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.transmapconf.delete.confirmation"/>';              
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

setTitle('<bean:message bundle="servermgrResources" key="translationmapconf.title"/>');
</script>



<html:form action="/searchTranslationMappingConfig">

	<html:hidden name="searchTranslationMappingConfigForm" styleId="action"
		property="action" />
	<html:hidden name="searchTranslationMappingConfigForm"
		styleId="pageNumber" property="pageNumber" />
	<html:hidden name="searchTranslationMappingConfigForm"
		styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
	<html:hidden name="searchTranslationMappingConfigForm"
		styleId="totalRecords" property="totalRecords"
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
											bundle="servermgrResources" key="translationmapconf.search" /></td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top" width="18%">
										<bean:message bundle="servermgrResources" 
											key="translationmapconf.name" />
												<ec:elitehelp headerBundle="servermgrResources" 
													text="transmapping.name" 
														header="translationmapconf.name"/>
										
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<html:text styleId="name" property="name" size="30"
											maxlength="60" style="width:250px" />
									</td>
								</tr>

								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="labeltext" valign="middle" width="18%">&nbsp;</td>
									<td class="labeltext" colspan="2" align="left"><input
										type="button" name="c_btnCreate" id="c_btnCreate2"
										value=" Search " class="light-btn" onclick="validateSearch()">
										<%-- <input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchTranslationMappingConfig.do?/>'" value="Cancel" class="light-btn"> --%>
										<input type="button" name="Create" value="   Create   "
										onclick="javascript:navigatePage('initCreateTranslationMappingConfig.do','name')"
										class="light-btn"></td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<%if(action.equals("list")) {%>
								<tr>
									<td align="left" class="labeltext" colspan="5" valign="top">
										<table cellSpacing="0" cellPadding="0" width="99%" border="0">
											<tr>
												<td class="table-header" width="50%"><bean:message
														bundle="servermgrResources"
														key="translationmapconf.search.list" /></td>
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
													type="button" name="Delete" onclick="removeData()"
													value="   Delete   " class="light-btn">

												</td>
												<td class="btns-td" align="right">
													<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else if(pageNo == totalPages){ %> <a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else { %> <a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
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
															<td align="left" class="tblheader" valign="top" width="*">Description</td>
															<td align="left" class="tblheader" valign="top"
																width="20%">From</td>
															<td align="left" class="tblheader" valign="top"
																width="20%">To</td>
															<td align="center" class="tblheader" valign="top"
																width="40px">Edit</td>

														</tr>
														<tr>
															<%if(mappingConfigList != null && mappingConfigList.size() >0){ %>

															<logic:iterate id="obj"
																name="searchTranslationMappingConfigForm"
																property="configList"
																type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData">
																<tr>
																	<td align="center" class="tblfirstcol"><input
																		type="checkbox" name="select"
																		value="<bean:write name="obj" property="translationMapConfigId"/>" />
																	</td>
																	<td align="center" class="tblrows" width="5%"><%=((pageNo-1)*pageSize)+count%></td>
																	<td align="left" class="tblrows"><a
																		href="<%=basePath%>/viewTranslationMappingConfigBasicDetail.do?translationMapConfigId=<%=obj.getTranslationMapConfigId()%>"><bean:write
																				name="obj" property="name" /></a></td>
																	<td align="left" class="tblrows"><%=EliteUtility.formatDescription(obj.getDescription()) %>&nbsp;</td>
																	<td align="left" class="tblrows" valign="top"><bean:write
																			name="obj" property="translatorTypeFrom.name" /></td>
																	<td align="left" class="tblrows" valign="top"><bean:write
																			name="obj" property="translatorTypeTo.name" /></td>
																	<td align="center" class="tblrows"><a
																		href="<%=basePath%>/updateTranslationMappingConfigBasicDetail.do?translationMapConfigId=<%=obj.getTranslationMapConfigId()%>"><img
																			src="<%=basePath%>/images/edit.jpg" alt="Edit"
																			border="0"></a></td>
																</tr>
																<%count ++ ;%>
															</logic:iterate>
															<%}else{ %>
														
														<tr>
															<td align="center" class="tblfirstcol" colspan="8">No
																Records Found.</td>
														</tr>
														<%	}%>
														</tr>
													</table>
												</td>
											</tr>

											<tr>
												<td class="btns-td" valign="middle">&nbsp; <html:button
														property="c_btnDelete" onclick="removeData()"
														value="   Delete   " styleClass="light-btn" />
												</td>
												<td class="btns-td" align="right">
													<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else if(pageNo == totalPages){ %> <a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else { %> <a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchTranslationMappingConfig.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
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
