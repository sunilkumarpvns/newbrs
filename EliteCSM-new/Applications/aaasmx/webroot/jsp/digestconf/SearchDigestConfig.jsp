<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.util.constants.DigestConfConstant"%>
<%@ page import="com.elitecore.elitesm.web.digestconf.forms.SearchDigestConfForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData"%>

<%
    String basePath = request.getContextPath();
    String strDatePattern = "dd MMM,yyyy hh:mm:ss";
    SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
    Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
    int iIndex =0;
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
    
    for (i=0; i < document.forms[1].elements.length; i++){
        if(document.forms[1].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[1].elements[i].checked == true)
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('At least select one Digest Configuration for remove process');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searchdigestconfigjsp.delete.query"/>';        
        //msg = "All the selected Staff Personnel would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
       	    document.miscDigestConfForm.action.value = 'delete';
        	document.miscDigestConfForm.submit();
        }
    }
}
function  checkAll(){
	 	if( document.forms[1].toggleAll.checked == true) {
	 		var selectVars = document.getElementsByName('select');
		 	for (i = 0; i < selectVars.length;i++)
				selectVars[i].checked = true ;
	    } else if (document.forms[1].toggleAll.checked == false){
	 		var selectVars = document.getElementsByName('select');	    
			for (i = 0; i < selectVars.length; i++)
				selectVars[i].checked = false ;
		}
}
setTitle('<bean:message bundle="digestconfResources" key="digestconf.header"/>');
</script>
<%																							 	
     SearchDigestConfForm searchDigestConfForm = (SearchDigestConfForm)request.getAttribute("searchDigestConfForm");
	 List<DigestConfigInstanceData> lstDigestConf = searchDigestConfForm.getDigestConfigList();
     String strName = searchDigestConfForm.getName();
     
     long pageNo = searchDigestConfForm.getPageNumber();
     long totalPages = searchDigestConfForm.getTotalPages();
     long totalRecord = searchDigestConfForm.getTotalRecords();
	 int count=1;
     
     String strDraftAAASipEnable=searchDigestConfForm.getDraftAAASipEnable();
     String strPageNumber = String.valueOf(pageNo);     
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     
     
     
     
%>

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
								<td class="table-header">SEARCH DIGEST CONFIGURATION</td>
							</tr>
							<tr>
								<td class="small-gap" colspan="3">&nbsp;</td>
							</tr>
							<tr>
								<td class="small-gap" colspan="3">&nbsp;</td>
							</tr>
							<tr>
								<td colspan="3">
									<table width="100%" name="c_tblCrossProductList"
										id="c_tblCrossProductList" align="right" border="0"
										cellpadding="0" cellspacing="0">
										<html:form action="/searchDigestConf">
											<input type="hidden" name="c_strActionMode"
												id="c_strActionMode" value="102231105" />
											<html:hidden name="searchDigestConfForm" styleId="action"
												property="action" />
											<html:hidden name="searchDigestConfForm" styleId="pageNumber"
												property="pageNumber" />
											<html:hidden name="searchDigestConfForm" styleId="totalPages"
												property="totalPages" value="<%=strTotalPages%>" />
											<html:hidden name="searchDigestConfForm"
												styleId="totalRecords" property="totalRecords"
												value="<%=strTotalRecords%>" />

											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message bundle="digestconfResources" key="digestconf.name" />
													<ec:elitehelp headerBundle="digestconfResources" 
													text="digestconf.name" header="digestconf.name"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="name" property="name" size="30"
														maxlength="30" style="width:200px" tabindex="1" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="digestconfResources" key="digestconf.drafaaasipenable" /> 
													<ec:elitehelp headerBundle="digestconfResources" 
													text="digestconf.draftsterman" header="digestconf.drafaaasipenable"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:select name="searchDigestConfForm" tabindex="2"  styleId="draftAAASipEnable" property="draftAAASipEnable" size="1" style="width:200px">
														<html:option value="none">--Select--</html:option>
														<html:option value="true">True</html:option>
														<html:option value="false">False</html:option>
													</html:select></td>
											</tr>
											<tr>
												<td>&nbsp;</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<input type="button" tabindex="3" name="Search" width="5%"
													name="DigestConfName" Onclick="validateSearch()"
													value="   Search   " class="light-btn" /> <!-- <input type="button" name="Reset" onclick="reset();" value="   Reset    " class="light-btn" > -->
													<input type="button" tabindex="4" name="Create"
													value="   Create   "
													onclick="javascript:navigatePage('initCreateDigestConf.do','name');"
													class="light-btn">
												</td>
											</tr>
										</html:form>
										<%
		    	//System.out.println("Every time check the value of pageno and strname : "+pageNo+" str is :"+strName);
				if(searchDigestConfForm.getAction()!=null && searchDigestConfForm.getAction().equalsIgnoreCase(DigestConfConstant.LISTACTION)){
	       	%>
										<html:form action="/miscDigestConf">
											<html:hidden name="miscDigestConfForm" styleId="action"
												property="action" />
											<html:hidden name="miscDigestConfForm" styleId="name"
												property="name" value="<%=strName%>" />
											<html:hidden name="miscDigestConfForm"
												styleId="draftAAASipEnable" property="draftAAASipEnable"
												value="<%=strDraftAAASipEnable%>" />
											<html:hidden name="miscDigestConfForm" styleId="pageNumber"
												property="pageNumber" value="<%=strPageNumber%>" />
											<html:hidden name="miscDigestConfForm" styleId="totalPages"
												property="totalPages" value="<%=strTotalPages%>" />
											<html:hidden name="miscDigestConfForm" styleId="totalRecords"
												property="totalRecords" value="<%=strTotalRecords%>" />

											<tr>
												<td align="left" class="labeltext" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="99%"
														border="0">
														<tr>
															<td class="table-header" width="50%">Digest
																Configuration List</td>

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
															<td class="btns-td" valign="middle">&nbsp; <html:button
																	property="c_btnDelete" onclick="removeData()"
																	value="   Delete   " styleClass="light-btn" />
															</td>
															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else if(pageNo == totalPages){ %> <a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else { %> <a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= pageNo-1%>"><img
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
																			width="5%"><input type="checkbox"
																			name="toggleAll" value="checkbox"
																			onclick="checkAll()" /></td>
																		<td align="center" class="tblheader" valign="top"
																			width="40px"><bean:message
																				key="general.serialnumber" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="20%"><bean:message
																				bundle="digestconfResources" key="digestconf.name" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="*"><bean:message
																				bundle="digestconfResources"
																				key="digestconf.drafaaasipenable" /></td>
																		<td align="center" class="tblheader" valign="top"
																			width="40px"><bean:message
																				bundle="digestconfResources" key="digestconf.edit" /></td>
																	</tr>
																	<%	if(lstDigestConf!=null && lstDigestConf.size()>0){%>
																	<logic:iterate id="digestConfigBean"
																		name="searchDigestConfForm"
																		property="digestConfigList"
																		type="com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData">
																		<tr>
																			<td align="center" class="tblfirstcol"><input
																				type="checkbox" name="select"
																				value="<bean:write name="digestConfigBean" property="digestConfId"/>" />
																			</td>
																			<td align="center" class="tblrows"><%=((pageNo-1)*pageSize)+count%></td>
																			<td align="left" class="tblrows"><a
																				href="<%=basePath%>/viewDigestConf.do?digestConfId=<bean:write name="digestConfigBean" property="digestConfId"/>"><bean:write
																						name="digestConfigBean" property="name" /></a></td>
																			<td align="left" class="tblrows"><bean:write
																					name="digestConfigBean"
																					property="draftAAASipEnable" />&nbsp;&nbsp;</td>

																			<td align="center" class="tblrows"><a
																				href="updateDigestConf.do?digestConfId=<bean:write name="digestConfigBean" property="digestConfId"/>"><img
																					src="<%=basePath%>/images/edit.jpg" alt="Edit"
																					border="0"></a></td>
																		</tr>
																		<% count=count+1; %>
																		<% iIndex += 1; %>
																	</logic:iterate>
																	<%}else{%>
																	<tr>
																		<td align="center" class="tblfirstcol" colspan="5">No
																			Records Found.</td>
																	</tr>
																	<%}%>
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
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else if(pageNo == totalPages){ %> <a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else { %> <a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchDigestConf.do?name=<%=strName%>&draftAAASipEnable=<%=strDraftAAASipEnable%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <% } %> <% } %>
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</html:form>
										<%}%>
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