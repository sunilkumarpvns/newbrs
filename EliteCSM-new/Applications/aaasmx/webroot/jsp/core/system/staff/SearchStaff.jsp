<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>

<%@ page import="com.elitecore.elitesm.web.core.system.staff.forms.SearchStaffForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData"%>
<%@ page import="com.elitecore.elitesm.util.constants.StaffConstant"%>
<%@ page import="com.elitecore.elitesm.hibernate.core.system.staff.HStaffDataManager"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
    String basePath = request.getContextPath();
    String strDatePattern = "dd MMM,yyyy hh:mm:ss";
    SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
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

function active(){
    var selectVar = false;
    
    for (i=0; i < document.forms[1].elements.length; i++){
        if(document.forms[1].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[1].elements[i].checked == true)
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('Please select atleast one Staff Personnel for Activation');
    }else{
    	document.miscStaffForm.action.value = 'active';
    	document.miscStaffForm.submit();
    }
}
function inactive(){
    var selectVar = false;
    
    for (i=0; i < document.forms[1].elements.length; i++){
        if(document.forms[1].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[1].elements[i].checked == true)
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('Please select atleast one Staff Personnel for DeActivation');
    }else{
    	document.miscStaffForm.action.value = 'inactive';
    	document.miscStaffForm.submit();
    }
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
        alert('At least select one Staff Personnel for remove process');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searchstaffjsp.delete.query"/>';        
        //msg = "All the selected Staff Personnel would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
       	    document.miscStaffForm.action.value = 'delete';
        	document.miscStaffForm.submit();
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

function navigatePageWithNameAndUserName(action,name,UserName) {
	createNewForm("newFormData",action);
	var name = $("#"+name).attr("name");
	var val = $("#"+name).val();
	$("#newFormData").append("<input type='hidden' name='"+name+"' value='"+val+"'>");
	
	name = $("#"+UserName).attr("name");
	val = $("#"+UserName).val();
	$("#newFormData").append("<input type='hidden' name='"+name+"' value='"+val+"'>");
	
	$("#newFormData").submit();
}
setTitle('<bean:message bundle="StaffResources" key="staff.staff"/>');
</script>
<%
     SearchStaffForm searchStaffForm = (SearchStaffForm)request.getAttribute("searchStaffForm");
     List lstStaff = searchStaffForm.getListSearchStaff();
     String strName = searchStaffForm.getName() != null ? searchStaffForm.getName() : "";
     String strUserName = searchStaffForm.getUsername() != null ? searchStaffForm.getUsername(): "";
     Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
     
     long pageNo = searchStaffForm.getPageNumber();
     long totalPages = searchStaffForm.getTotalPages();
     long totalRecord = searchStaffForm.getTotalRecords();
	 int count=1;
	 
	 String strStatus = searchStaffForm.getStatus();
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     String strPageNumber = String.valueOf(pageNo);
     
     String paramString="name="+strName+"&resultStatus="+strStatus+"&username="+strUserName;
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

							<html:form action="/searchStaff">
								<html:hidden name="searchStaffForm" styleId="action"
									property="action" />
								<html:hidden name="searchStaffForm" styleId="pageNumber"
									property="pageNumber" value="<%=strPageNumber%>" />
								<html:hidden name="searchStaffForm" styleId="totalPages"
									property="totalPages" value="<%=strTotalPages%>" />
								<html:hidden name="searchStaffForm" styleId="totalRecords"
									property="totalRecords" value="<%=strTotalRecords%>" />
								<tr>
									<td class="table-header" colspan="5"><bean:message
											bundle="StaffResources" key="staff.searchstaff" /></td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top" width="10%">
										<bean:message bundle="StaffResources" key="staff.name" />
											<ec:elitehelp headerBundle="StaffResources" 
												text="staff.name" 
													header="staff.name"/>
									</td>
									<td align="left" class="labeltext" valign="top" width="32%">
										<html:text styleId="name" tabindex="1" property="name"
											size="30" maxlength="30" />
									</td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" width="10%">
										<bean:message bundle="StaffResources" key="staff.username" />
											<ec:elitehelp headerBundle="StaffResources" 
												text="staff.username" 
													header="staff.username"/>
									</td>
									<td align="left" class="labeltext" valign="top" width="32%">
										<html:text styleId="userName" tabindex="2" property="username"
											size="30" maxlength="30" />
									</td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" width="10%">
										<bean:message bundle="StaffResources" key="staff.status" />
											<ec:elitehelp headerBundle="StaffResources" 
												text="staff.status" 
													header="staff.status"/>
									</td>
									<td align="left" class="labeltext" valign="top" width="5%">
										<html:radio name="searchStaffForm" tabindex="3"
											styleId="status" property="status" value="Active" /> <bean:message
											bundle="StaffResources" key="staff.active" /> <html:radio name="searchStaffForm"
											styleId="status" tabindex="4" property="status"
											value="Inactive" /> <bean:message bundle="StaffResources" key="staff.inactive" /> <html:radio
											name="searchStaffForm" styleId="status" tabindex="5"
											property="status" value="All" /> <bean:message
											bundle="StaffResources" key="staff.all" />
									</td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle">&nbsp;</td>
									<td align="left" class="labeltext" valign="top" width="5%">
										<input type="button" name="Search" tabindex="6" width="5%"
										name="RadiusPolicyName" Onclick="validateSearch()"
										value="   Search   " class="light-btn" /> <!-- <input type="button" name="Cancel" onclick="reset();" value="   Cancel    " class="light-btn"> -->
										<input type="button" name="Create" tabindex="7"
										value="   Create   "
										onclick="javascript:navigatePageWithNameAndUserName('initCreateStaff.do','name','userName');"
										class="light-btn">
									</td>
								</tr>
							</html:form>
							<tr>
								<td colspan="3">&nbsp;</td>
							</tr>

							<%
						if(searchStaffForm.getAction()!=null && searchStaffForm.getAction().equalsIgnoreCase(StaffConstant.LISTACTION)){
		         	%>
							<html:form action="/miscStaff">
								<html:hidden name="miscStaffForm" styleId="action"
									property="action" />
								<html:hidden name="miscStaffForm" property="pageNumber"
									value="<%=strPageNumber%>" />
								<html:hidden name="miscStaffForm" styleId="totalPages"
									property="totalPages" value="<%=strTotalPages%>" />
								<html:hidden name="miscStaffForm" styleId="totalRecords"
									property="totalRecords" value="<%=strTotalRecords%>" />
								<tr>
									<td align="left" class="labeltext" colspan="6" valign="top">
										<table cellSpacing="0" cellPadding="0" width="100%" border="0">
											<tr>
												<td class="table-header" width="50%"><bean:message
														bundle="StaffResources" key="staff.stafflist" /></td>
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
												<td class="btns-td" valign="middle"><input
													type="button" name="Show" onclick="active()"
													value="   Active   " class="light-btn"> <input
													type="button" name="Hide" onclick="inactive()"
													value="   Inactive   " class="light-btn"> <input
													type="button" name="Delete" onclick="removeData()"
													value="   Delete   " class="light-btn"></td>
												<td class="btns-td" align="right">
													<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else if(pageNo == totalPages){ %> <a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else { %> <a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
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
													<table width="99%" border="0" cellpadding="0"
														cellspacing="0" id="listTable">
														<tr>
															<td align="center" class="tblheader" valign="top"
																width="1%"><input type="checkbox" name="toggleAll"
																value="checkbox" onclick="checkAll()" /></td>
															<td align="center" class="tblheader" valign="top"
																width="40px"><bean:message
																	key="general.serialnumber" /></td>
															<td align="left" class="tblheader" valign="top"
																width="20%"><bean:message bundle="StaffResources" key="staff.username" />
															</td>
															<td align="left" class="tblheader" valign="top"
																width="30%"><bean:message bundle="StaffResources" key="staff.name" /></td>
															<td align="left" class="tblheader" valign="top" width="*">
																<bean:message bundle="StaffResources" key="staff.lastlogin" />
															</td>
															<td align="left" class="tblheader" valign="top"
																width="10%"><bean:message bundle="StaffResources" key="staff.status" /></td>
															<td align="center" class="tblheader" valign="top"
																width="40px"><bean:message bundle="StaffResources" key="staff.edit" /></td>
														</tr>
														<%
if(lstStaff!=null && lstStaff.size()>0){												
%>
														<logic:iterate id="staffBean" name="searchStaffForm"
															property="listSearchStaff"
															type="com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData">
															<%
	IStaffData sData = (IStaffData)lstStaff.get(iIndex);
	Timestamp dtLastUpdate = sData.getLastLoginTime();
%>
															<tr>
																<td align="center" class="tblfirstcol"><input
																	type="checkbox" name="select"
																	value="<bean:write name="staffBean" property="staffId"/>" />
																</td>
																<td align="center" class="tblrows"><%=((pageNo-1)*pageSize)+count%></td>
																<td align="left" class="tblrows"><a
																	href="viewStaff.do?staffid=<bean:write name="staffBean" property="staffId"/>"><bean:write
																			name="staffBean" property="username" /></a></td>
																<td align="left" class="tblrows"><bean:write
																		name="staffBean" property="name" />&nbsp;</td>
																<td align="left" class="tblrows"><bean:write
																		name="staffBean" property="lastLoginTime" />&nbsp;</td>
																<td align="center" class="tblrows">
																	<%	String CommonStatusValue = (String)org.apache.struts.util.RequestUtils.lookup(pageContext, "staffBean", "commonStatusId", null);
											//System.out.println("value of the commonstatusid is :"+CommonStatusValue);
											if(CommonStatusValue.equals(StaffConstant.HIDE_STATUS_ID) ||   CommonStatusValue.equals(StaffConstant.DEFAULT_STATUS_ID)){ 
										%> <!-- 	<img src="<%=basePath%>/images/hide.jpg" border="0">   -->
																	<img src="<%=basePath%>/images/deactive.jpg" border="0">
																	<% }else if(CommonStatusValue.equals(StaffConstant.SHOW_STATUS_ID)){ %>
																	<!-- 		<img src="<%=basePath%>/images/show.jpg" border="0">   -->
																	<img src="<%=basePath%>/images/active.jpg" border="0">
																	<% } %>
																</td>
																<td align="center" class="tblrows"><a
																	href="updateStaff.do?staffid=<bean:write name="staffBean" property="staffId"/>">
																		<img src="<%=basePath%>/images/edit.jpg" alt="Edit"
																		border="0">
																</a></td>
															</tr>
															<% count=count+1; %>
															<% iIndex += 1; %>
														</logic:iterate>


														<%
							}else{
						%>
														<tr>
															<td align="center" class="tblfirstcol" colspan="7">No
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
													type="button" name="Show" onclick="active()"
													value="   Active   " class="light-btn"> <input
													type="button" name="Hide" onclick="inactive()"
													value="   Inactive   " class="light-btn"> <input
													type="button" name="Delete" onclick="removeData()"
													value="   Delete   " class="light-btn"></td>
												<td class="btns-td" align="right">
													<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else if(pageNo == totalPages){ %> <a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else { %> <a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchStaff.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<% } %> <% } %>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</html:form>
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