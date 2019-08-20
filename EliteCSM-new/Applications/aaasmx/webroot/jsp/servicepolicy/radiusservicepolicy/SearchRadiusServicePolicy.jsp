<%@page import="com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form.SearchRadiusServicePolicyForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@page import="java.util.*"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager,com.elitecore.elitesm.datamanager.servicepolicy.auth.data.*,com.elitecore.elitesm.util.constants.*"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	String basePath = request.getContextPath();	
	SearchRadiusServicePolicyForm policyForm = (SearchRadiusServicePolicyForm)request.getAttribute("policyForm");
	List servicePolicyList = policyForm.getPolicyList();
	String action = policyForm.getAction();
	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	String status = policyForm.getStatus();
	
	long pageNo = policyForm.getPageNumber();
    long totalPages = policyForm.getTotalPages();
    long totalRecord = policyForm.getTotalRecords();
	int count=1;
    
    String strStatus = policyForm.getStatus() != null ? policyForm.getStatus() : "";
    String strPageNumber = String.valueOf(pageNo);     
    String strTotalPages = String.valueOf(totalPages);
    String strTotalRecords = String.valueOf(totalRecord);
    String strName=policyForm.getName()!=null ? policyForm.getName() : "" ;
    String paramString="name="+strName+"&resultStatus="+strStatus;
    
%>


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
function show(){

	document.forms[0].action.value = 'show';
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
				alert("Please select atleast one Policy to Show.")
			}else{
					document.forms[0].submit();
			}
	}
	else{
		alert("No Records Found For Show Operation! ")
	}


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
function hide(){

document.forms[0].action.value = 'hide';
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
				alert("Please select atleast one Policy to Hide.")
			}else{
			    	document.forms[0].submit();
			}
	}
	else{
		alert("No Records Found For Hide Operation! ")
	}

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
        alert('At least select one Auth Service Policy for remove process');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searchpolicyjsp.delete.query"/>';              
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
</script>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<html:form action="/searchRadiusServicePolicy" styleId="searchRadiusServicePolicy">

	<html:hidden name="policyForm" styleId="action" property="action" />
	<html:hidden name="policyForm" styleId="pageNumber" property="pageNumber" />
	<html:hidden name="policyForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
	<html:hidden name="policyForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />

	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header" colspan="3">
										<bean:message bundle="servicePolicyProperties" key="sericepolicy.proxypolicy.search" />
									</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top" width="18%">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.name" />
										<ec:elitehelp  header="Name" headerBundle="servicePolicyProperties" text="This parameter specifies the name of an Radius Service Policy." ></ec:elitehelp>
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<html:text styleId="name" property="name" size="30" maxlength="60" tabindex="1" />
									</td>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top" width="18%">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.status" />
										<ec:elitehelp  header="Status" headerBundle="servicePolicyProperties" text="This parameter specifies the status of an Radius Service Policy." ></ec:elitehelp>
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<html:radio name="policyForm" styleId="status" property="status" value="Active" tabindex="2" /> 
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.active" />
										<html:radio name="policyForm" styleId="status" property="status" value="Inactive" tabindex="3" /> 
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.inactive" />
										<html:radio name="policyForm" styleId="status" property="status" value="All" tabindex="4" />
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.all" />
									</td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="labeltext" valign="top" width="18%">&nbsp;</td>
									<td class="labeltext" colspan="2" align="left">
										<input type="button" name="c_btnCreate" id="c_btnCreate2" value=" Search " class="light-btn" onclick="validateSearch()" tabindex="5"> 
										<input type="button" name="c_btn_manage" onclick="javascript:location.href='<%=basePath%>/manageRadiusServicePolicyOrder.do?/>'" value="Manage Order" class="light-btn" tabindex="6">
										<input type="button" name="Create" value="   Create   " onclick="javascript:navigatePageWithStatus('initAddRadiusServicePolicy.do','name');" class="light-btn" tabindex="7">
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
												<td class="table-header" width="50%"><bean:message
														bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.list" />
												</td>
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
													type="button" name="Show" onclick="show()"
													value="   Active   " class="light-btn" tabindex="8">
													<input type="button" name="Hide" onclick="hide()"
													value="   Inactive   " class="light-btn" tabindex="9">
													<input type="button" name="Delete" onclick="removeData()"
													value="   Delete   " class="light-btn" tabindex="10">

												</td>
												<td class="btns-td" align="right">
													<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else if(pageNo == totalPages){ %> <a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else { %> <a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
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
																width="20%">Name</td>
															<td align="left" class="tblheader" valign="top" width="20%">	
																<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.authpolicy.ruleset" />
															</td>
															<td align="left" class="tblheader" valign="top" width="22%">
																<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.acctpolicy.ruleset" />
															</td>
															<td align="left" class="tblheader" valign="top" width="*">Description</td>
															<td align="center" class="tblheader" valign="top"
																width="40px">Status</td>
															<td align="center" class="tblheader" valign="top"
																width="40px">Edit</td>

														</tr>
														<tr>
															<%if(servicePolicyList != null && servicePolicyList.size() >0){ %>

															<logic:iterate id="obj" name="policyForm" property="policyList" type="com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData">
																<tr>
																	<td align="center" class="tblfirstcol">
																		<input type="checkbox" name="select" value="<bean:write name="obj" property="radiusPolicyId"/>" />
																	</td>
																	<td align="center" class="tblrows">
																		<%=((pageNo-1)*pageSize)+count%>
																	</td>
																	<td align="left" class="tblrows">
																		<a href="<%=request.getContextPath()%>/viewRadiusServicePolicy.do?radiusPolicyId=<%=obj.getRadiusPolicyId()%>">
																			<bean:write name="obj" property="name" />
																		</a>
																	</td>
																	<td align="left" class="tblrows"><bean:write name="obj" property="authRuleset" />&nbsp;</td>
																	<td align="left" class="tblrows"><bean:write name="obj" property="acctRuleset" />&nbsp;</td>
																	<td align="left" class="tblrows"><%=EliteUtility.formatDescription(obj.getDescription()) %>&nbsp;&nbsp;</td>
																	<td align="center" class="tblrows">
																	<%if(obj.getStatus().equals(BaseConstant.HIDE_STATUS_ID) ||  obj.getStatus().equals(BaseConstant.DEFAULT_STATUS_ID)){%> <img src="<%=basePath%>/images/hide.jpg" border="0">
																		<% }else if(obj.getStatus().equals(BaseConstant.SHOW_STATUS_ID)){ %>
																		<img src="<%=basePath%>/images/show.jpg" border="0">
																		<% } %>
																	</td>
																	<td align="center" class="tblrows"><a
																		href="<%=request.getContextPath()%>/updateRadiusServicePolicyBasicDetail.do?radiusPolicyId=<%=obj.getRadiusPolicyId()%>">
																			<img src="<%=basePath%>/images/edit.jpg" alt="Edit"
																			border="0">
																	</a></td>
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
												<td class="btns-td" valign="middle"><input
													type="button" name="Show" onclick="show()"
													value="   Active   " class="light-btn"> <input
													type="button" name="Hide" onclick="hide()"
													value="   Inactive   " class="light-btn"> <html:button
														property="c_btnDelete" onclick="removeData()"
														value="   Delete   " styleClass="light-btn" /></td>
												<td class="btns-td" align="right">
													<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else if(pageNo == totalPages){ %> <a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else { %> <a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="searchRadiusServicePolicy.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
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
<script>
setTitle('<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy"/>');
</script>