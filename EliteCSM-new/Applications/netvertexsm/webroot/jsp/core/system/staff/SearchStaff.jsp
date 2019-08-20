<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData"%>

<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.AccessPolicyConstant"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.StaffConstant"%>

<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.staff.forms.SearchStaffForm"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.List"%>

<script language="javascript" src="<%=basePath%>/js/commonfunctions.js"></script>

<%    
    String strDatePattern = "dd MMM,yyyy hh:mm:ss";
    SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
    int iIndex =0;
%>
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
        alert('Please select at least one Staff Personnel for activation');
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
        alert('Please select at least one Staff Personnel for deactivation');
    }else{
    	document.miscStaffForm.action.value = 'inactive';
    	document.miscStaffForm.submit();
    }
}
function removeRecord(){
    var selectVar = false;
    
    for (i=0; i < document.forms[1].elements.length; i++){
        if(document.forms[1].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[1].elements[i].checked == true)
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('Select at least one Staff Personnel for remove process');
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

$(document).ready(function(){
	setTitle('<bean:message key="staff.staff" />');
	$("#userName").focus();
	checkAll(document.getElementsByName("toggleAll")[0]);
});
</script>
<%
     SearchStaffForm searchStaffForm = (SearchStaffForm)request.getAttribute("searchStaffForm");
     List lstStaff = searchStaffForm.getListSearchStaff();
     String strName = searchStaffForm.getName();
     
     long pageNo = searchStaffForm.getPageNumber();
     long totalPages = searchStaffForm.getTotalPages();
     long totalRecord = searchStaffForm.getTotalRecords();
	 int count=1;
	 String strPageNumber = String.valueOf(pageNo);
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
%>
<table name="MainTable" id="MainTable" width="100%" cellpadding="0" cellspacing="0" border="0">
	<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<tr>
		<td width="10">&nbsp;</td>
		<td width="100%" colspan="2" valign="top" class="box">
		<table cellSpacing="0" cellPadding="0" width="100%" border="0">
			<html:form action="/searchStaff">
				<html:hidden name="searchStaffForm" styleId="action" property="action" />
				<html:hidden name="searchStaffForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNumber%>"/>  
				<html:hidden name="searchStaffForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
				<html:hidden name="searchStaffForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
					<tr>
						<td class="table-header" colspan="5">
							<bean:message key="staff.searchstaff" /> 
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="3">
						<table width="97%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0">
							<tr>
								<td align="left" class="labeltext" valign="top" width="10%">
									<bean:message key="staff.username" />
								</td>
								<td align="left" class="labeltext" valign="top" width="32%">
									<html:text styleId="userName" property="userName" size="30" maxlength="30" />
								</td>
							</tr>
							<tr>
								<td align="left" class="labeltext" valign="top" width="10%">
									<bean:message key="staff.name" />
								</td>
								<td align="left" class="labeltext" valign="top" width="32%">
									<html:text styleId="name" property="name" size="30" maxlength="30" />
								</td>
							</tr>
							<tr>
								<td align="left" class="labeltext" valign="top" width="10%">
									<bean:message key="staff.status" />
								</td>
								<td align="left"  valign="top" width="5%">
									<table>
										<tr class="labeltext" style="font-size: 13px">
											<td><html:radio name="searchStaffForm"  styleId="status" property="status" value="Active"  /></td>
											<td><bean:message key="staff.active" /></td>
											<td><html:radio name="searchStaffForm" styleId="status" property="status" value="Inactive" /></td>
											<td><bean:message key="staff.inactive" /></td>
											<td><html:radio name="searchStaffForm" styleId="status" property="status" value="All" /></td>
											<td><bean:message key="staff.all" /></td>
										</tr>
									</table>									
								</td>
							</tr>
							<tr class="small-gap">
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td class="btns-td" valign="middle">&nbsp;</td>
								<td align="left" class="labeltext" valign="top" width="5%">
									<input type="button" name="Search" width="5%" name="RadiusPolicyName" Onclick="validateSearch()" value="   Search   " class="light-btn" /> 
								</td>
							</tr>
						</table>
						</td>
					</tr>
			</html:form>
			<%
				if(searchStaffForm.getAction()!=null && searchStaffForm.getAction().equalsIgnoreCase(StaffConstant.LISTACTION)){
		    %>
			<html:form action="/miscStaff">
				<html:hidden name="miscStaffForm" styleId="action" property="action" />				
				<html:hidden name="miscStaffForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
				<html:hidden name="miscStaffForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
				<tr>					
					<td>
					<table cellSpacing="0" cellPadding="0" width="100%" border="0" >
						<tr>
							<td class="table-header" width="24%" colspan="2">
								<bean:message key="staff.stafflist" />
							</td>
							<td align="left" class="blue-text" valign="middle" width="62%" colspan="3">&nbsp;</td>
							<td align="right" class="blue-text" valign="middle" width="14%" colspan="4">
									<% if(pageNo == totalPages+1) { %> 
										[<%=((pageNo-1)*10)+1%>-<%=totalRecord%>] of <%= totalRecord %> 
									<% } else if(pageNo == 1) { %> 
										[<%=(pageNo-1)*10+1%>-<%=(pageNo-1)*10+10%>] of <%= totalRecord %> 
									<% } else { %>
									 	[<%=((pageNo-1)*10)+1%>-<%=((pageNo-1)*10)+10%>] of <%= totalRecord %> 
									<% } %>
							</td>
						</tr>
						<tr>

							<td class="btns-td" align="right" colspan="8">
							<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> 
								<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%=pageNo+1%>)"
									onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
									onmouseout="MM_swapImgRestore()" alt="Next" border="0" />
								<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%=totalPages+1%>)"
									onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
									onmouseout="MM_swapImgRestore()" alt="Last" border="0"> 
							<% } %>
							<% if(pageNo>1 && pageNo!=totalPages+1) {%> 
								<%  if(pageNo-1 == 1){ %>
								<img src="<%=basePath%>/images/first.jpg" name="Image511"
									onclick="navigate('first',<%= 1%>)"
									onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
									onmouseout="MM_swapImgRestore()" alt="First" border="0">
								<img src="<%=basePath%>/images/previous.jpg"
									onclick="navigate('next',<%= pageNo-1%>)" name="Image5"
									onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
									onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
								<img src="<%=basePath%>/images/next.jpg" name="Image61"
									onclick="navigate('next',<%= pageNo+1%>)"
									onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
									onmouseout="MM_swapImgRestore()" alt="Next" border="0"> <img
									src="<%=basePath%>/images/last.jpg" name="Image612"
									onclick="navigate('last',<%= totalPages+1%>)"
									onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
									onmouseout="MM_swapImgRestore()" alt="Last" border="0"> <% } else if(pageNo == totalPages){ %>
								<img src="<%=basePath%>/images/first.jpg" name="Image511"
									onclick="navigate('previous',<%= 1%>)"
									onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
									onmouseout="MM_swapImgRestore()" alt="First" border="0">
								<img src="<%=basePath%>/images/previous.jpg" name="Image5"
									onclick="navigate('previous',<%= pageNo-1%>)"
									onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
									onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
								<img src="<%=basePath%>/images/next.jpg" name="Image61"
									onclick="navigate('next',<%= pageNo+1%>)"
									onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
									onmouseout="MM_swapImgRestore()" alt="Next" border="0"> <img
									src="<%=basePath%>/images/last.jpg" name="Image612"
									onclick="navigate('last',<%= totalPages+1%>)"
									onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
									onmouseout="MM_swapImgRestore()" alt="Last" border="0"> <% } else { %>
								<img src="<%=basePath%>/images/first.jpg" name="Image511"
									onclick="navigate('previous',<%= 1%>)"
									onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
									onmouseout="MM_swapImgRestore()" alt="First" border="0">
								<img src="<%=basePath%>/images/previous.jpg" name="Image5"
									onclick="navigate('previous',<%=pageNo-1%>)"
									onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
									onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
								<img src="<%=basePath%>/images/next.jpg" name="Image61"
									onclick="navigate('next',<%=pageNo+1%>)"
									onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
									onmouseout="MM_swapImgRestore()" alt="Next" border="0"> <img
									src="<%=basePath%>/images/last.jpg" name="Image612"
									onclick="navigate('last',<%=totalPages+1%>)"
									onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
									onmouseout="MM_swapImgRestore()" alt="Last" border="0"> 
							 	<% } %>
							<% } %> 
							<% if(pageNo == totalPages+1) { %> 
							<img
								src="<%=basePath%>/images/first.jpg" name="Image511"
								onclick="navigate('first',<%=1%>)"
								onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="First" border="0">
							<img src="<%=basePath%>/images/previous.jpg" name="Image5"
								onclick="navigate('previous',<%=pageNo-1%>)"
								onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
							<% } %> 
						<% } %>
						</td>
						</tr>
						<tr>
							<td class="btns-td" valign="middle" colspan="5"><input
								type="button" name="Create" value="   Create   "
								class="light-btn"
								onclick="javascript:location.href='<%=basePath%>/initCreateStaff.do?/>'">
							<input type="button" name="Show" Onclick="active()"
								value="   Active   " class="light-btn"> <input
								type="button" name="Hide" Onclick="inactive()"
								value="   Inactive   " class="light-btn"> <input
								type="button" name="Delete" OnClick="removeRecord()"
								value="   Delete   " class="light-btn"></td>

						</tr>
						 
						<tr>
							<td class="btns-td" valign="middle" colspan="9">
							<table width="100%" border="0" cellpadding="0" cellspacing="0"
								id="listTable">
								<tr>
									<td align="center" class="tblheaderfirstcol" valign="top" width="1%">
										<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll(this)" />
									</td>
									<td align="right" class="tblheader" valign="top" width="1%">
										<bean:message key="general.serialnumber" />
									</td>
									<td align="left" class="tblheader" valign="top" width="20%">
										<bean:message key="staff.username" />
									</td>
									<td align="left" class="tblheader" valign="top" width="20%">
										<bean:message key="staff.name" />
									</td>
									<td align="left" class="tblheader" valign="top" width="20%">
										<bean:message key="staff.lastlogin" />
									</td>
									<td align="left" class="tblheader" valign="top" width="20%">
										<bean:message key="general.createddate" />
									</td>
									<td align="left" class="tblheader" valign="top" width="20%">
										<bean:message key="general.lastmodifieddate" />
									</td>
									<td align="center" class="tblheader" valign="top" width="8%">
										<bean:message key="staff.status" />
									</td>
									<td align="center" class="tblheaderlastcol" valign="top" width="7%">
										<bean:message key="staff.edit" />
									</td>
								</tr>
								<%
if(lstStaff!=null && lstStaff.size()>0){
	int i=0;
%>
								<logic:iterate id="staffBean" name="searchStaffForm"
									property="listSearchStaff"
									type="com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData">
									<%
	IStaffData sData = (IStaffData)lstStaff.get(iIndex);
	Timestamp dtLastUpdate = sData.getLastLoginTime();
%>
										<tr id="dataRow" name="dataRow" >
	
										<td align="center" class="tblfirstcol"><input
											type="checkbox" name="select"
											value="<bean:write name="staffBean" property="staffId"/>" onclick="onOffHighlightedRow(<%=i++%>,this)"  />
										</td>
										<td align="left" class="tblrows"><%=((pageNo-1)*10)+count%></td>
										<td align="left" class="tblrows"><a
											href="viewStaff.do?staffid=<bean:write name="staffBean" property="staffId"/>"><bean:write
											name="staffBean" property="userName" /></a></td>
										<td align="left" class="tblrows"><bean:write
											name="staffBean" property="name" />&nbsp;</td>
										<td align="left" class="tblrows">
											<%=EliteUtility.dateToString(dtLastUpdate, "dd MMMM yyyy - hh:mm:ss a ")%>
										</td>
										<td align="left" class="tblrows">
											<%=EliteUtility.dateToString(staffBean.getCreateDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;
										</td>
										<td align="left" class="tblrows">
											<%=EliteUtility.dateToString(staffBean.getLastModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;
										</td>
										<td align="center" class="tblrows">
											<logic:equal name="staffBean" property="commonStatusId" value="<%=AccessPolicyConstant.SHOW_STATUS_ID%>" >
												<img src="<%=basePath%>/images/active.jpg" border="0">
											</logic:equal>
											<logic:equal name="staffBean" property="commonStatusId" value="<%=AccessPolicyConstant.HIDE_STATUS_ID%>" >
												<img src="<%=basePath%>/images/deactive.jpg" border="0">
											</logic:equal>
										</td>
										<td align="center" class="tblrows">
											<a href="updateStaff.do?staffid=<bean:write name="staffBean" property="staffId"/>">
											<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
											</a>
										</td>
									</tr>
									<% count=count+1; %>
									<% iIndex += 1; %>
								</logic:iterate>
							
					
						<%
		}else{
%>
						<tr>
							<td align="center" class="tblfirstcol" colspan="9">No Records Found.</td>
						</tr>
						<%
		}
%>
							</table>
							</td>
						</tr>
					 
						<tr>
							<td class="btns-td" valign="middle" colspan="5">
								<input type="button" name="Create" value="   Create   " class="light-btn"
								onclick="javascript:location.href='<%=basePath%>/initCreateStaff.do?/>'">
								<input type="button" name="Show" Onclick="active()" value="   Active   " class="light-btn">
								<input type="button" name="Hide" Onclick="inactive()" value="   Inactive   " class="light-btn"> 
								<input type="button" name="Delete" Onclick="removeRecord()" value="   Delete   " class="light-btn">
							</td>
							<td class="btns-td" valign="middle" colspan="2">&nbsp;</td>
							<td class="btns-td" align="right">
							<% if(totalPages >= 1) { %>
							 <% if(pageNo == 1){ %> 
							 <img src="<%=basePath%>/images/next.jpg" name="Image61"
								onclick="navigate('next',<%=pageNo+1%>)"
								onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Next" border="0" /> <img
								src="<%=basePath%>/images/last.jpg" name="Image612"
								onclick="navigate('last',<%=totalPages+1%>)"
								onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Last" border="0"> <% } %>
							<% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
							<img src="<%=basePath%>/images/first.jpg" name="Image511"
								onclick="navigate('first',<%= 1%>)"
								onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="First" border="0"> 
								<img src="<%=basePath%>/images/previous.jpg"
								onclick="navigate('next',<%= pageNo-1%>)" name="Image5"
								onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
								onclick="navigate('next',<%= pageNo+1%>)"
								onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Next" border="0"> <img
								src="<%=basePath%>/images/last.jpg" name="Image612"
								onclick="navigate('last',<%= totalPages+1%>)"
								onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Last" border="0"> <% } else if(pageNo == totalPages){ %>
							<img src="<%=basePath%>/images/first.jpg" name="Image511"
								onclick="navigate('previous',<%= 1%>)"
								onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="First" border="0">
							<img src="<%=basePath%>/images/previous.jpg" name="Image5"
								onclick="navigate('previous',<%= pageNo-1%>)"
								onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
								onclick="navigate('next',<%= pageNo+1%>)"
								onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Next" border="0"> <img
								src="<%=basePath%>/images/last.jpg" name="Image612"
								onclick="navigate('last',<%= totalPages+1%>)"
								onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Last" border="0"> <% } else { %>
							<img src="<%=basePath%>/images/first.jpg" name="Image511"
								onclick="navigate('previous',<%= 1%>)"
								onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="First" border="0">
							<img src="<%=basePath%>/images/previous.jpg" name="Image5"
								onclick="navigate('previous',<%=pageNo-1%>)"
								onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
								onclick="navigate('next',<%=pageNo+1%>)"
								onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Next" border="0"> <img
								src="<%=basePath%>/images/last.jpg" name="Image612"
								onclick="navigate('last',<%=totalPages+1%>)"
								onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Last" border="0"> <% } %>
							<% } %> <% if(pageNo == totalPages+1) { %> <img
								src="<%=basePath%>/images/first.jpg" name="Image511"
								onclick="navigate('first',<%=1%>)"
								onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="First" border="0">
							<img src="<%=basePath%>/images/previous.jpg" name="Image5"
								onclick="navigate('previous',<%=pageNo-1%>)"
								onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
							<% } %> <% } %>
							</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>
					</table>
					</td>
 
				</tr>
				 
			</html:form>
<%	//		}
	}   %>
		 
		</table>
		<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>


