<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>

<%@ page import="com.elitecore.elitesm.web.radius.clientprofile.forms.SearchClientProfileForm"%>
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
    String basePath = request.getContextPath();
    String strDatePattern = "dd MMM,yyyy hh:mm:ss";
    SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
    Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
    int iIndex =0;
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<%@page import="com.elitecore.elitesm.datamanager.radius.clientprofile.data.ClientTypeData"%>
<%@page import="com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData"%>
<%@page import="com.elitecore.elitesm.util.constants.BaseConstant"%>
<%@page import="com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData"%><style>
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
function navigatePageWithInfo(action,appendAttrbId) {
	createNewForm("newFormData",action);
	var name = $("#"+appendAttrbId).attr("name");
	var val = $("#"+appendAttrbId).val();
	$("#newFormData").append("<input type='hidden' name='"+name+"' value='"+val+"'>")

	var name = $("#clientTypeId").attr("name");
	var val=$('#clientTypeId').val();
	
	$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+val+"'>")
	
	var name = $("#vendorInstanceId").attr("name");
	var val=$('#vendorInstanceId').val();
	
	$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+val+"'>")
					 .submit();
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
        alert('At least select one Client Profile for remove process');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searchclientprofile.delete.query"/>';        
        var agree = confirm(msg);

        if(agree){
            document.miscClientProfileForm.action.value ='delete';
        	document.miscClientProfileForm.submit();
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
setTitle('<bean:message bundle="radiusResources" key="radius.clientprofile"/>');
</script>
<%
     // search based on client profile name ,client type,vendor name
     SearchClientProfileForm searchClientProfileForm = (SearchClientProfileForm)request.getAttribute("searchClientProfileForm");
     List<RadiusClientProfileData> lstClientProfile = searchClientProfileForm.getClientProfileList();
     
     
     List<ClientTypeData> clientTypeinCombo=searchClientProfileForm.getClientTypeList();
     List<VendorData> vendorListinCombo=searchClientProfileForm.getVendorList();
     
     long pageNo = searchClientProfileForm.getPageNumber();
     long totalPages = searchClientProfileForm.getTotalPages();
     long totalRecord = searchClientProfileForm.getTotalRecords();
	 int count=1;
   
     String strPageNumber = String.valueOf(pageNo);     
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     String strClientTypeId = String.valueOf(searchClientProfileForm.getClientTypeId());
     String strVendorInstanceId=String.valueOf(searchClientProfileForm.getVendorInstanceId());
     String strName=searchClientProfileForm.getProfileName();
    
     
     
     
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
								<td class="table-header" colspan="5">Search Client Profile
								</td>
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
										id="c_tblCrossProductList" align="right" border="0">
										<html:form action="/searchClientProfile">
											<input type="hidden" name="c_strActionMode"
												id="c_strActionMode" value="102231105" />
											<html:hidden name="searchClientProfileForm" styleId="action"
												property="action" />
											<html:hidden name="searchClientProfileForm"
												styleId="pageNumber" property="pageNumber" />
											<html:hidden name="searchClientProfileForm"
												styleId="totalPages" property="totalPages"
												value="<%=strTotalPages%>" />
											<html:hidden name="searchClientProfileForm"
												styleId="totalRecords" property="totalRecords"
												value="<%=strTotalRecords%>" />
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message bundle="radiusResources" key="radius.clientprofile" /> 
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.name" header="radius.clientprofile"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="profileName" property="profileName"
														size="30" maxlength="30" tabindex="1" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top"
													width="10%"> 
													<bean:message bundle="radiusResources" key="radius.clientprofile.clienttypename" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.type" header="radius.clientprofile.clienttypename"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">

													<html:select styleId="clientTypeId" property="clientTypeId"
														style="width: 120px;" tabindex="2">
														<html:option value="0">----Select----</html:option>
														<%
											if (clientTypeinCombo != null && !clientTypeinCombo.isEmpty()) {
											Iterator itr = clientTypeinCombo.iterator();
											while (itr.hasNext()) {
												ClientTypeData clientTypeData = (ClientTypeData) itr.next();
												String clientTypeId=String.valueOf(clientTypeData.getClientTypeId());
											%>

														<html:option value="<%=clientTypeId%>"><%=clientTypeData.getClientTypeName()%></html:option>

														<%}	%>

														<%}%>
													</html:select>
												</td>

											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top"
													width="10%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.vendorname" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.vendor" header="radius.clientprofile.vendorname"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">

													<html:select styleId="vendorInstanceId"
														property="vendorInstanceId" style="width: 120px;"
														tabindex="3">
														<html:option value="0">----Select----</html:option>
														<%
											if (vendorListinCombo != null && !vendorListinCombo.isEmpty()) {
											Iterator itr = vendorListinCombo.iterator();
											while (itr.hasNext()) {
												VendorData vendorData = (VendorData) itr.next();
												String vendorInstanceId=vendorData.getVendorInstanceId();
											%>

														<html:option value="<%=vendorInstanceId%>"><%=vendorData.getVendorName()%></html:option>

														<%}	%>

														<%}%>
													</html:select>
												</td>


											</tr>
											<tr>
												<td>&nbsp;</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<input type="button" name="Search" width="5%" name="search"
													Onclick="validateSearch()" value="   Search   "
													class="light-btn" tabindex="4" /> <!-- <input type="button" name="Reset" onclick="reset();" value="   Reset    " class="light-btn" > -->
													<input type="button" name="Create" value="   Create   "
													onclick="javascript:navigatePageWithInfo('initCreateClientProfile.do','profileName');"
													class="light-btn" tabindex="5">
												</td>
											</tr>

										</html:form>

										<%
		    //System.out.println("Every time check the value of pageno and strname : "+pageNo+" str is :"+strName);
			if(searchClientProfileForm.getAction()!=null && searchClientProfileForm.getAction().equalsIgnoreCase(BaseConstant.LISTACTION)){
	       	%>
										<html:form action="/miscClientProfile">



											<html:hidden name="miscClientProfileForm" styleId="action"
												property="action" />
											<html:hidden name="miscClientProfileForm"
												styleId="clientProfileName" property="clientProfileName"
												value="<%=strName%>" />
											<html:hidden name="miscClientProfileForm"
												styleId="clientTypeId" property="clientTypeId"
												value="<%=strClientTypeId%>" />
											<html:hidden name="miscClientProfileForm"
												styleId="vendorInstanceId" property="vendorInstanceId"
												value="<%=strVendorInstanceId%>" />

											<html:hidden name="miscClientProfileForm"
												styleId="pageNumber" property="pageNumber"
												value="<%=strPageNumber%>" />
											<html:hidden name="miscClientProfileForm"
												styleId="totalPages" property="totalPages"
												value="<%=strTotalPages%>" />
											<html:hidden name="miscClientProfileForm"
												styleId="totalRecords" property="totalRecords"
												value="<%=strTotalRecords%>" />

											<tr>
												<td align="left" class="labeltext" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="99%"
														border="0">
														<tr>
															<td class="table-header" width="50%">Client Profile
																List</td>

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
															<td class="btns-td" valign="middle">&nbsp; <html:button
																	property="c_btnDelete" onclick="removeData()"
																	value="   Delete   " styleClass="light-btn" />
															</td>
															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else if(pageNo == totalPages){ %> <a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else { %> <a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <% } %> <% } %>
															</td>
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
																			width="40px"><bean:message
																				bundle="radiusResources"
																				key="radius.clientprofile.serialnumber" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="25%"><bean:message
																				bundle="radiusResources"
																				key="radius.clientprofile.clientprofilename" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="20%"><bean:message
																				bundle="radiusResources"
																				key="radius.clientprofile.clienttypename" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="*"><bean:message bundle="radiusResources"
																				key="radius.clientprofile.vendorname" /></td>
																		<td align="center" class="tblheader" valign="top"
																			width="40px"><bean:message
																				bundle="radiusResources"
																				key="radius.clientprofile.edit" /></td>
																	</tr>
																	<%	if(lstClientProfile!=null && lstClientProfile.size()>0){%>


																	<logic:iterate id="clientProfileBean"
																		name="searchClientProfileForm"
																		property="clientProfileList"
																		type="com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData">
																		<tr>
																			<td align="center" class="tblfirstcol"><input
																				type="checkbox" name="select"
																				value="<bean:write name="clientProfileBean" property="profileId"/>" />
																			</td>
																			<td align="center" class="tblrows"><%=((pageNo-1)*pageSize)+count%></td>
																			<td align="left" class="tblrows"><a
																				href="<%=basePath%>/viewClientProfile.do?viewType=basic&profileId=<bean:write name="clientProfileBean" property="profileId"/>"><bean:write
																						name="clientProfileBean" property="profileName" />&nbsp;</a></td>
																			<td align="left" class="tblrows"><bean:write
																					name="clientProfileBean"
																					property="clientTypeData.clientTypeName" />&nbsp;&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="clientProfileBean"
																					property="vendorData.vendorName" />&nbsp;&nbsp;</td>

																			<td align="center" class="tblrows" width="5%"><a
																				href="<%=basePath%>/updateClientProfile.do?viewType=basic&profileId=<bean:write name="clientProfileBean" property="profileId"/>"><img
																					src="<%=basePath%>/images/edit.jpg" alt="Edit"
																					border="0"></a></td>
																		</tr>
																		<% count=count+1; %>

																	</logic:iterate>

																	<%
				}else{
				%>
																	<tr>
																		<td align="center" class="tblfirstcol" colspan="8">No
																			Records Found.</td>
																	</tr>
																	<%}%>
																</table>
															</td>
														</tr>

														<tr>
															<td class="small-gap" width="7" colspan="3">&nbsp;</td>
														</tr>

														<tr>
															<td class="btns-td" valign="middle">&nbsp; <html:button
																	property="c_btnDelete" onclick="removeData()"
																	value="   Delete   " styleClass="light-btn" />
															</td>
															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else if(pageNo == totalPages){ %> <a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else { %> <a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchClientProfile.do?profileName=<%=strName%>&clientTypeId=<%=strClientTypeId%>&vendorInstanceId=<%=strVendorInstanceId%>&action=list&pageNo=<%= pageNo-1%>"><img
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
