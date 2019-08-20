<%@page import="com.elitecore.elitesm.util.logger.Logger"%>
<%@ page import="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.RadiusPolicyForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.SearchRadiusPolicyForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyData"%>
<%@ page import="com.elitecore.elitesm.hibernate.radius.policies.radiuspolicy.HRadiusPolicyDataManager"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyData"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.util.constants.RadiusPolicyConstant"%>

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
	document.searchRadiusPolicyForm.pageNumber.value = 1;
	document.searchRadiusPolicyForm.submit();
}
function prepareUrl(image,value,sortOrderValue){
	var name = '';
	image.href = image.href + escape(name);
	makeUrl(image,value,sortOrderValue);
}
function navigate(direction, pageNumber ){
	document.searchRadiusPolicyForm.pageNumber.value = pageNumber;
	document.searchRadiusPolicyForm.submit();
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
	document.miscRadiusPolicyForm.action.value = 'show';	
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
			
					document.miscRadiusPolicyForm.submit();
			}
	}
	else{
		alert("No Records Found For Show Operation! ")
	}
	
}
function hide(){
	document.miscRadiusPolicyForm.action.value = 'hide';
	
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
				document.miscRadiusPolicyForm.submit();
			}
	}
	else{
		alert("No Records Found For Hide Operation! ")
	}
	
	
	
}
function removeData(){
	document.miscRadiusPolicyForm.action.value = 'delete';
	
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
  				document.miscRadiusPolicyForm.submit();
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
setTitle('<bean:message bundle="radiusResources" key="radiuspolicy.radiuspolicy"/>');

</script>
<%
     SearchRadiusPolicyForm searchRadiusPolicyForm = (SearchRadiusPolicyForm)request.getAttribute("searchRadiusPolicyForm");
     List lstRadiusPolicy = searchRadiusPolicyForm.getListRadiusPolicy();
     String strName = searchRadiusPolicyForm.getName();
     
     long pageNo = searchRadiusPolicyForm.getPageNumber();
     long totalPages = searchRadiusPolicyForm.getTotalPages();
     long totalRecord = searchRadiusPolicyForm.getTotalRecords();
	 int count=1;
     
     String strStatus = searchRadiusPolicyForm.getStatus();
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
								<td class="table-header" colspan="5">SEARCH RADIUS POLICY</td>
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
										<html:form action="/searchRadiusPolicy">
											<input type="hidden" name="c_strActionMode"
												id="c_strActionMode" value="102231105" />
											<html:hidden name="searchRadiusPolicyForm" styleId="action"
												property="action" />
											<html:hidden name="searchRadiusPolicyForm"
												styleId="pageNumber" property="pageNumber" />
											<html:hidden name="searchRadiusPolicyForm"
												styleId="totalPages" property="totalPages"
												value="<%=strTotalPages%>" />
											<html:hidden name="searchRadiusPolicyForm"
												styleId="totalRecords" property="totalRecords"
												value="<%=strTotalRecords%>" />
											<tr>
												<td align="left" class="captiontext" valign="top" width="8%">
													<bean:message bundle="radiusResources" key="radiuspolicy.name" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="radiuspolicy.name" header="radiuspolicy.name"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="name" property="name" size="30"
														maxlength="30" tabindex="1" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="8%">
													<bean:message bundle="radiusResources" key="radiuspolicy.status" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="radiuspolicy.status" header="radiuspolicy.status"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<html:radio name="searchRadiusPolicyForm" styleId="status"
														property="status" value="Show" tabindex="2" />Show <html:radio
														name="searchRadiusPolicyForm" styleId="status"
														property="status" value="Hide" tabindex="3" />Hide <html:radio
														name="searchRadiusPolicyForm" styleId="status"
														property="status" value="All" tabindex="4" />All
												</td>

											</tr>
											<tr>
												<td colspan="3">&nbsp;</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<input type="button" name="Search" width="5%"
													name="RadiusPolicyName" tabindex="5"
													Onclick="validateSearch(document.searchRadiusForm,'list')"
													value="   Search   " class="light-btn" /> <!-- <input type="button" name="Reset" onclick="reset();" value="   Reset    " class="light-btn" > -->
													<input type="button" name="Create" tabindex="6"
													value="   Create   "
													onclick="javascript:navigatePageWithStatus('addRadiusPolicy.do','name');"
													class="light-btn">
												</td>
											</tr>

										</html:form>
										<%
		    //System.out.println("Every time check the value of pageno and strname : "+pageNo+" str is :"+strName);
			if(searchRadiusPolicyForm.getAction()!=null && searchRadiusPolicyForm.getAction().equalsIgnoreCase(RadiusPolicyConstant.LISTACTION)){
	       	%>
										<html:form action="/miscRadiusPolicy">
											<html:hidden name="miscRadiusPolicyForm" styleId="action"
												property="action" />
											<html:hidden name="miscRadiusPolicyForm" styleId="name"
												property="name" value="<%=strName%>" />
											<html:hidden name="miscRadiusPolicyForm" styleId="status"
												property="status" value="<%=strStatus%>" />
											<html:hidden name="miscRadiusPolicyForm" styleId="pageNumber"
												property="pageNumber" value="<%=strPageNumber%>" />
											<html:hidden name="miscRadiusPolicyForm" styleId="totalPages"
												property="totalPages" value="<%=strTotalPages%>" />
											<html:hidden name="miscRadiusPolicyForm"
												styleId="totalRecords" property="totalRecords"
												value="<%=strTotalRecords%>" />

											<tr>
												<td align="left" class="labeltext" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="99%"
														border="0">
														<tr>
															<td class="table-header" width="50%">RADIUS POLICY
																LIST</td>

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
																type="button" name="Show" tabindex="7" Onclick="show()"
																value="   Show   " class="light-btn"> <input
																type="button" name="Hide" tabindex="8" Onclick="hide()"
																value="   Hide   " class="light-btn"> <html:button
																	property="c_btnDelete" onclick="removeData()"
																	value="   Delete   " styleClass="light-btn" /></td>
															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else if(pageNo == totalPages){ %> <a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else { %> <a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
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
																			width="20%"><bean:message
																				bundle="radiusResources"
																				key="radiuspolicy.lastupdate" /></td>
																		<td align="center" class="tblheader" valign="top"
																			width="40px"><bean:message
																				bundle="radiusResources" key="radiuspolicy.status" /></td>
																	</tr>
																	<%	if(lstRadiusPolicy!=null && lstRadiusPolicy.size()>0){%>
																	<logic:iterate id="radiusPolicyBean"
																		name="searchRadiusPolicyForm"
																		property="listRadiusPolicy"
																		type="com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyData">
																		<%	IRadiusPolicyData sData = (IRadiusPolicyData)lstRadiusPolicy.get(iIndex);
							Timestamp dtLastUpdate = sData.getLastUpdated();
						%>
																		<tr>
																			<td align="center" class="tblfirstcol"><input
																				type="checkbox" name="select"
																				value="<bean:write name="radiusPolicyBean" property="radiusPolicyId"/>" />
																			</td>
																			<td align="center" class="tblrows"><%=((pageNo-1)*pageSize)+count%></td>
																			<td align="left" class="tblrows"><a
																				href="<%=basePath%>/viewRadiusPolicy.do?radiusPolicyId=<bean:write name="radiusPolicyBean" property="radiusPolicyId"/>"><bean:write
																						name="radiusPolicyBean" property="name" /></a></td>
																			<td align="left" class="tblrows"><%=EliteUtility.formatDescription(radiusPolicyBean.getDescription()) %>&nbsp;&nbsp;</td>
																			<td align="left" class="tblrows"><%=EliteUtility.dateToString(dtLastUpdate, ConfigManager.get(ConfigConstant.DATE_FORMAT))%></td>
																			<td align="center" class="tblrows">
																				<%
									String CommonStatusValue = (String)org.apache.struts.util.RequestUtils.lookup(pageContext, "radiusPolicyBean", "commonStatusId", null);
									//System.out.println("value of the commonstatusid is :"+CommonStatusValue);
									if(CommonStatusValue.equals(RadiusPolicyConstant.HIDE_STATUS_ID) ||   CommonStatusValue.equals(RadiusPolicyConstant.DEFAULT_STATUS_ID)){ 
								%> <img src="<%=basePath%>/images/hide.jpg" border="0"> <% }else if(CommonStatusValue.equals(RadiusPolicyConstant.SHOW_STATUS_ID)){ %>
																				<img src="<%=basePath%>/images/show.jpg" border="0">
																				<% } %>
																			</td>
																		</tr>
																		<% count=count+1; %>
																		<% iIndex += 1; %>
																	</logic:iterate>
																	<%	}else{
				%>
																	<tr>
																		<td align="center" class="tblfirstcol" colspan="6">No
																			Records Found.</td>
																	</tr>
																	<%	}%>
																</table>
															</td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle"><input
																type="button" name="Show" Onclick="show()"
																value="   Show   " class="light-btn"> <input
																type="button" name="Hide" Onclick="hide()"
																value="   Hide   " class="light-btn"> <html:button
																	property="c_btnDelete" onclick="removeData()"
																	value="   Delete   " styleClass="light-btn" /></td>
															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else if(pageNo == totalPages){ %> <a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else { %> <a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchRadiusPolicy.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
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
