<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>

<%@ page import="com.elitecore.elitesm.web.radius.policies.accesspolicy.forms.SearchAccessPolicyForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyData"%>
<%@ page import="com.elitecore.elitesm.hibernate.radius.policies.accesspolicy.HAccessPolicyDataManager"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyData"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.util.constants.AccessPolicyConstant"%>


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
function navigatePageWithStatus(action,appendAttrbId) {
	createNewForm("newFormData",action);
	var name = $("#"+appendAttrbId).attr("name");
	var val = $("#"+appendAttrbId).val();
	$("#newFormData").append("<input type='hidden' name='"+name+"' value='"+val+"'>")

	var name = $("#status").attr("name");
	var val=$('input[name=status]:radio:checked').val();
	
	$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+val+"'>")
					 .submit();
}
function show(){

	document.miscAccessPolicyForm.action.value = 'show';
	
	var selectArray = document.getElementsByName('select');
	
	if(selectArray.length>0){
	
			var b = true;
			for (i=0; i<selectArray.length; i++){
		
			 		 if (selectArray[i].checked == false){  			
			 		 	b=false;
			 		 }
			 		 else{
			 		 	
						b=true;
						break;
					}
				}
			if(b==false){
			alert("Please select atleast one policy to Show.")
			
			}else{
			
				document.miscAccessPolicyForm.submit();
			}
	}
	else{
		alert("No Records Found For Show Operation! ")
	}
}
function hide(){
	
	document.miscAccessPolicyForm.action.value = 'hide';
		var selectArray = document.getElementsByName('select');
	
	if(selectArray.length>0){
			
			var b = true;
			for (i=0; i<selectArray.length; i++){
		
			 		 if (selectArray[i].checked == false){  			
			 		 	b=false;
			 		 }
			 		 else{
			 		 	
						b=true;
						break;
					}
				}
			if(b==false){
			alert("Please select atleast one policy to Hide.")
			
			}else{
			document.miscAccessPolicyForm.submit();
			}
	}
	else{
		alert("No Records Found For Hide Operation! ")
	}
}
function removeData(){
	document.miscAccessPolicyForm.action.value = 'delete';
		
	var selectArray = document.getElementsByName('select');
	if(selectArray.length>0){
	var b = true;
	for (i=0; i<selectArray.length; i++){

	 		 if (selectArray[i].checked == false){  			
	 		 	b=false;
	 		 }
	 		 else{
	 		 	
				b=true;
				break;
			}
		}
	if(b==false){
	alert("Selection Required To Perform Delete Operation.")
	
	}else{
		var r=confirm("This will delete the selected items. Do you want to continue ?");
			if (r==true)
  			{
  				document.miscAccessPolicyForm.submit();
  			}
		
	}
	}
	else{
		alert("No Records Found For Delete Operation! ")
	}
}
function  checkAll(){

var arrayCheck = document.getElementsByName('select');

	if( document.forms[1].toggleAll.checked == true) {
		for (i = 0; i < arrayCheck.length;i++)
			arrayCheck[i].checked = true ;
	} else if (document.forms[1].toggleAll.checked == false){
		for (i = 0; i < arrayCheck.length; i++)
			arrayCheck[i].checked = false ;
	}
}
setTitle('<bean:message bundle="radiusResources" key="accesspolicy.accesspolicy"/>');
</script>

<%                                         														   
     SearchAccessPolicyForm searchAccessPolicyForm = (SearchAccessPolicyForm)request.getAttribute("searchAccessPolicyForm");
     List lstAccessPolicy = searchAccessPolicyForm.getListAccessPolicy();
     String strName = searchAccessPolicyForm.getName();
     String strStatus = searchAccessPolicyForm.getStatus();
     long pageNo = searchAccessPolicyForm.getPageNumber();
     long totalPages = searchAccessPolicyForm.getTotalPages();
     long totalRecord = searchAccessPolicyForm.getTotalRecords();
	 int count=1;
	 
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     String strPageNumber = String.valueOf(pageNo);
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
								<td class="table-header" colspan="5"><bean:message
										bundle="radiusResources" key="accesspolicy.searchaccesspolicy" />
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
										<html:form action="/searchAccessPolicy">
											<html:hidden name="searchAccessPolicyForm" styleId="action"
												property="action" />
											<html:hidden name="searchAccessPolicyForm"
												styleId="pageNumber" property="pageNumber"
												value="<%=strPageNumber%>" />
											<html:hidden name="searchAccessPolicyForm"
												styleId="totalPages" property="totalPages"
												value="<%=strTotalPages%>" />
											<html:hidden name="searchAccessPolicyForm"
												styleId="totalRecords" property="totalRecords"
												value="<%=strTotalRecords%>" />
											<tr>
												<td align="left" class="captiontext" valign="top"
													width="10%">
													<bean:message bundle="radiusResources" key="accesspolicy.name" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="accesspolicy.name" header="accesspolicy.name" />
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="name" property="name" size="30"
														maxlength="30" style="width:250px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top"
													width="10%">
													<bean:message bundle="radiusResources"
													key="accesspolicy.status" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="accesspolicy.status" header="accesspolicy.status"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<html:radio name="searchAccessPolicyForm" styleId="status"
														property="status" value="Show" />Show <html:radio
														name="searchAccessPolicyForm" styleId="status"
														property="status" value="Hide" />Hide <html:radio
														name="searchAccessPolicyForm" styleId="status"
														property="status" value="All" />All
												</td>
											</tr>
											<tr>
												<td>&nbsp;</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<input type="button" name="Search" width="5%"
													name="RadiusPolicyName"
													Onclick="validateSearch(document.searchRadiusForm,'list')"
													value="   Search   " class="light-btn" /> <!-- <input type="button" name="Reset" onclick="reset();" value="   Reset    " class="light-btn" > -->
													<input type="button" name="Create"
													onclick="javascript:navigatePageWithStatus('initCreateAccessPolicy.do','name');"
													value="   Create   " class="light-btn">
												</td>
											</tr>
										</html:form>
										<%
		if(searchAccessPolicyForm.getAction()!=null && searchAccessPolicyForm.getAction().equalsIgnoreCase(AccessPolicyConstant.LISTACTION)){
   	%>
										<html:form action="/miscAccessPolicy">
											<html:hidden name="miscAccessPolicyForm" styleId="action"
												property="action" />
											<html:hidden name="miscAccessPolicyForm" styleId="pageNumber"
												property="pageNumber" value="<%=strPageNumber%>" />
											<html:hidden name="miscAccessPolicyForm" styleId="totalPages"
												property="totalPages" value="<%=strTotalPages%>" />
											<html:hidden name="miscAccessPolicyForm" styleId="totalPages"
												property="totalRecords" value="<%=strTotalRecords%>" />
											<html:hidden name="miscAccessPolicyForm" styleId="name"
												property="name" value="<%=strName%>" />

											<tr>

												<td align="left" class="labeltext" colspan="5" valign="top">

													<table cellSpacing="0" cellPadding="0" width="99%"
														border="0">
														<tr>
															<td class="table-header" width="50%"><bean:message
																	bundle="radiusResources"
																	key="accesspolicy.accesspolicylist" /></td>

															<td align="right" class="blue-text" valign="middle"
																width="50%">
																<% if(totalRecord == 0) { %> <%}else if(pageNo == totalPages+1) { %>
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
																type="button" name="Show" Onclick="show()"
																value="   Show   " class="light-btn"> <input
																type="button" name="Hide" Onclick="hide()"
																value="   Hide   " class="light-btn"> <input
																type="button" name="Delete" OnClick="removeData()"
																value="   Delete   " class="light-btn"></td>
															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

																<% } else if(pageNo == totalPages){ %> <a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

																<% } else { %> <a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
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
																			width="40px"><bean:message
																				bundle="radiusResources"
																				key="radiuspolicy.serialnumber" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="25%"><bean:message
																				bundle="radiusResources" key="radiuspolicy.name" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="*"><bean:message bundle="radiusResources"
																				key="radiuspolicy.description" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="25%"><bean:message
																				bundle="radiusResources"
																				key="radiuspolicy.lastupdate" /></td>
																		<td align="center" class="tblheader" valign="top"
																			width="40px"><bean:message
																				bundle="radiusResources" key="radiuspolicy.status" /></td>
																		<td align="center" class="tblheader" valign="top"
																			width="40px"><bean:message
																				bundle="radiusResources" key="accesspolicy.edit" /></td>
																	</tr>
																	<% 	if(lstAccessPolicy!=null && lstAccessPolicy.size()>0){ %>
																	<logic:iterate id="radiusPolicyBean"
																		name="searchAccessPolicyForm"
																		property="listAccessPolicy"
																		type="com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyData">
																		<% 
								IAccessPolicyData sData = (IAccessPolicyData)lstAccessPolicy.get(iIndex);
								Timestamp dtLastUpdate = sData.getLastUpdated();
							%>
																		<tr>
																			<td align="center" class="tblfirstcol"><input
																				type="checkbox" name="select" id="<%=(iIndex+1) %>"
																				value="<bean:write name="radiusPolicyBean" property="accessPolicyId"/>" />
																			</td>
																			<td align="center" class="tblrows"><%=((pageNo-1)*pageSize)+count%></td>
																			<td align="left" class="tblrows"><a
																				href="viewAccessPolicy.do?accesspolicyid=<bean:write name="radiusPolicyBean" property="accessPolicyId"/>"><bean:write
																						name="radiusPolicyBean" property="name" /></a></td>
																			<td align="left" class="tblrows"><%=EliteUtility.formatDescription(radiusPolicyBean.getDescription()) %>&nbsp;</td>
																			<td align="left" class="tblrows"><%=EliteUtility.dateToString(dtLastUpdate, ConfigManager.get(ConfigConstant.DATE_FORMAT))%></td>
																			<td align="center" class="tblrows">
																				<%
								String CommonStatusValue = (String)org.apache.struts.util.RequestUtils.lookup(pageContext, "radiusPolicyBean", "commonStatusId", null);
								if(CommonStatusValue.equals(AccessPolicyConstant.HIDE_STATUS_ID) ||   CommonStatusValue.equals(AccessPolicyConstant.DEFAULT_STATUS_ID)){ 
							%> <img src="<%=basePath%>/images/hide.jpg" border="0"> <% }else if(CommonStatusValue.equals(AccessPolicyConstant.SHOW_STATUS_ID)){ %>
																				<img src="<%=basePath%>/images/show.jpg" border="0">
																				<% } %>
																			</td>
																			<td align="center" class="tblrows"><a
																				href="editAccessPolicy.do?action=list&accesspolicyid=<bean:write name="radiusPolicyBean" property="accessPolicyId"/>"><img
																					src="<%=basePath%>/images/edit.jpg" alt="Edit"
																					border="0"></a></td>
																		</tr>
																		<% count=count+1; %>
																		<% iIndex += 1; %>
																	</logic:iterate>
																	<%}else{%>
																	<tr>
																		<td align="center" class="tblfirstcol" colspan="7">No
																			Records Found.</td>
																	</tr>
																	<%}%>
																</table>
															</td>
														</tr>
														<tr height="2">
															<td></td>
														</tr>
														<tr>
															<td class="btns-td" valign="left"><input
																type="button" name="Show" Onclick="show()"
																value="   Show   " class="light-btn"> <input
																type="button" name="Hide" Onclick="hide()"
																value="   Hide   " class="light-btn"> <input
																type="button" name="Delete" OnClick="removeData()"
																value="   Delete   " class="light-btn"></td>
															<!-- <td class="btns-td" valign="middle" colspan="1" >&nbsp; </td>
					    <td class="btns-td" align="right" >  -->
															<td class="btns-td" align="right">
																<!--  <td class="btns-td" valign="middle" colspan="3" >&nbsp; </td>
			    	 <td class="btns-td" align="right" > --> <% if(totalPages >= 1) { %>
																<% if(pageNo == 1){ %> <a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

																<% } else if(pageNo == totalPages){ %> <a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

																<% } else { %> <a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchAccessPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
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