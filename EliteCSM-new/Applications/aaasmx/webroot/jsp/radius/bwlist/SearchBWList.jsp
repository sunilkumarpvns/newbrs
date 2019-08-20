<%@page import="com.elitecore.elitesm.util.constants.BaseConstant"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.datamanager.radius.bwlist.data.BWListData"%>
<%@page import="com.elitecore.elitesm.web.radius.bwlist.forms.SearchBWListForm"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>

<%
    String basePath = request.getContextPath();
    String strDatePattern = "dd MMM,yyyy hh:mm:ss";
    SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
    Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
    int iIndex =0;
    int cnt1=0;
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
<%
     SearchBWListForm searchBWListForm = (SearchBWListForm)request.getAttribute("searchBWListForm");
     List<BWListData> bwList = searchBWListForm.getBwList();
     String strName = searchBWListForm.getAttribute();
     String strStatus = searchBWListForm.getStatus();
     long pageNo = searchBWListForm.getPageNumber();
     long totalPages = searchBWListForm.getTotalPages();
     long totalRecord = searchBWListForm.getTotalRecords();
	 int count=1;
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     String strPageNumber = String.valueOf(pageNo);
    
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

function active(){
    var selectVar = false;
    
    for (i=0; i < document.forms[1].elements.length; i++){
        if(document.forms[1].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[1].elements[i].checked == true)
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('Please select atleast one blacklisted candidate for Activation.');
    }else{
    	document.miscBWListForm.action.value = 'active';
    	document.miscBWListForm.submit();
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
        alert('Please select atleast one Blacklisted Candidate for DeActivation');
    }else{
    	document.miscBWListForm.action.value = 'inactive';
    	document.miscBWListForm.submit();
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
        alert('At least select one Blacklisted candidate for remove process');
    }else{
        var msg;
        msg = 'All the selected Blacklisted Candidates would be deleted. Would you like to continue?';        
        //msg = "All the selected Staff Personnel would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
       	    document.miscBWListForm.action.value = 'delete';
        	document.miscBWListForm.submit();
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

setTitle('<bean:message bundle="radiusResources" key="radius.bwlist.title"/>');
function navigatePageWithAttributeId(action) {
	createNewForm("newFormData",action);
	attributeId = $("#attribute").val();
	$("#newFormData").append("<input type='hidden' name='attributeId' value='"+attributeId+"'>")
	
	
	var attributeName=$("#attributeValue").val();
	$("#newFormData").append("<input type='hidden' name='attributeValue' value='"+attributeName+"'>")
	
	var name = $("#status").attr("name");
	var val=$('input[name=status]:radio:checked').val();
	
	$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+val+"'>")
	 				.submit();
	
	
}

function chkAssignType(){
	//alert(document.searchBWListForm.typeName.value);
}
</script>
<script type="text/javascript">
$(document).ready(function() {
	<%-- $('#upArwId').click(function(){
		var typeValue=$('#typeName').val();
		
		if(typeValue == "B"){
			alert('if Portion');
			<%pageNo++;%>
			alert("page nu" +<%=%>);
			<%strTypeName="B";%>
		}else{
			alert('else Portion');
			$('#typeName').val('B');
			<%strTypeName="B";pageNo=0;%>;
		}
	}); --%>
	 
});

</script>

<table cellpadding="0" cellspacing="0" border="0"
	width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td width="100%" class="box">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">

							<tr>
								<td class="table-header" colspan="5"><bean:message
										bundle="radiusResources" key="radius.bwlist.search" /></td>
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
										<html:form action="/searchBWList">
											<html:hidden name="searchBWListForm" styleId="action"
												property="action" />
											<html:hidden name="searchBWListForm" styleId="pageNumber"
												property="pageNumber" value="<%=strPageNumber%>" />
											<html:hidden name="searchBWListForm" styleId="totalPages"
												property="totalPages" value="<%=strTotalPages%>" />
											<html:hidden name="searchBWListForm" styleId="totalRecords"
												property="totalRecords" value="<%=strTotalRecords%>" />
											<html:hidden name="searchBWListForm" styleId="typeName"
												property="typeName" />
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message bundle="radiusResources"
													key="radius.bwlist.attribute" /> 
													<ec:elitehelp headerBundle="radiusResources" 
													text="radius.bwlist.attribute" header="radius.bwlist.attribute"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="attribute" property="attribute"
														size="30" maxlength="30" tabindex="1" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">	
													<bean:message bundle="radiusResources"
													key="radius.bwlist.attributevalue" /> 
													<ec:elitehelp headerBundle="radiusResources" 
													text="radius.bwlist.attributevalue" header="radius.bwlist.attributevalue"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="attributeValue" property="attributeValue"
														size="30" maxlength="30" tabindex="1" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message bundle="radiusResources"
													key="radius.bwlist.status" /> 
													<ec:elitehelp headerBundle="radiusResources" 
													text="radius.bwlist.status" header="radius.bwlist.status"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<html:radio name="searchBWListForm" styleId="status"
														property="status" value="Active" tabindex="2" /> <bean:message
														bundle="radiusResources" key="radius.bwlist.active" /> <html:radio
														name="searchBWListForm" styleId="status" property="status"
														value="Inactive" tabindex="3" /> <bean:message
														bundle="radiusResources" key="radius.bwlist.inactive" />
													<html:radio name="searchBWListForm" styleId="status"
														property="status" value="All" tabindex="4" /> <bean:message
														bundle="radiusResources" key="radius.bwlist.all" />
												</td>
											</tr>
											<tr>
												<td colspan="3">&nbsp;</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<input type="button" name="Search" width="5%"
													name="RadiusPolicyName" Onclick="validateSearch()"
													value="   Search   " class="light-btn" tabindex="5" /> <!-- <input type="button" name="Cancel" onclick="reset();" value="   Cancel    " class="light-btn"> -->
													<input type="button" name="Create" value="   Create   "
													class="light-btn"
													onclick="javascript:navigatePageWithAttributeId('initCreateBWList.do');"
													tabindex="6"> <input type="button" name="Upload"
													value="   Upload   " class="light-btn"
													onclick="javascript:navigatePage('initUploadBWList.do');"
													tabindex="7">
												</td>
											</tr>
										</html:form>
										<%
			if(searchBWListForm.getAction()!=null && searchBWListForm.getAction().equalsIgnoreCase(BaseConstant.LISTACTION)){
		    %>
										<html:form action="/miscBWList" styleId="miscform">
											<html:hidden name="miscBWListForm" styleId="action"
												property="action" />
											<html:hidden name="miscBWListForm" property="pagetNumber"
												value="<%=strPageNumber%>" />
											<html:hidden name="miscBWListForm" styleId="totalPages"
												property="totalPages" value="<%=strTotalPages%>" />
											<html:hidden name="miscBWListForm" styleId="totalRecords"
												property="totalRecords" value="<%=strTotalRecords%>" />
											<html:hidden name="searchBWListForm" styleId="typeName"
												property="typeName"/>
											<tr>
												<td align="left" class="labeltext" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="99%"
														border="0">
														<tr>
															<td class="table-header" width="50%"><bean:message
																	bundle="radiusResources" key="radius.bwlist.title" />
															</td>

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
																type="button" name="Active" Onclick="active()"
																value="   Active   " class="light-btn" tabindex="8">
																<input type="button" name="Inactive"
																Onclick="inactive()" value="   Inactive   "
																class="light-btn" tabindex="9"> <input
																type="button" name="Delete" OnClick="removeRecord()"
																value="   Delete   " class="light-btn" tabindex="10">
															</td>
															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>


																<% } else if(pageNo == totalPages){ %> <a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>


																<% } else { %> <a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>


																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
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
																		<td align="center" class="tblheader" valign="top" width="1%">
																			<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll()" tabindex="11" />
																		</td>
																		<td align="left" class="tblheader" valign="top" width="5%"><bean:message bundle="radiusResources" key="radius.bwlist.type" />
				<%-- 															<table border="0" cellpadding="0" cellspacing="0">
																				<tr>
																					<td rowspan="2" colspan="3" align="left" class="tblheader" valign="top" ><bean:message bundle="radiusResources" key="radius.bwlist.type" />&nbsp;&nbsp;</td>
																					<td align="right">
																						<a id="upArwId" onclick="chkAssignType();" href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>&typeName=<%=%>" >
																						<img src="<%=basePath%>/images/uparw.gif" name="Image61" id="imgUpArw"
																						onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																						onmouseout="MM_swapImgRestore()" alt="Up" border="0" ></a>
																					</td>
																				</tr>
																				<tr>
																					<td align="right">
																						<a href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>">
																						<img src="<%=basePath%>/images/downarw.gif" name="Image61"
																						onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																						onmouseout="MM_swapImgRestore()" alt="Up" border="0"></a>
																				
																					</td>
																				</tr>
																			</table> --%>
																		</td>
																		<td align="left" class="tblheader" valign="top" width="20%"><bean:message bundle="radiusResources" key="radius.bwlist.attribute" />
																		<%-- 	<table border="0" cellpadding="0" cellspacing="0">
																				<tr>
																					<td rowspan="2" align="left" class="tblheader" valign="top" ><bean:message bundle="radiusResources" key="radius.bwlist.attribute" />&nbsp;&nbsp;</td>
																					<td align="right">
																						<a href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>" on>
																						<img src="<%=basePath%>/images/uparw.gif" name="Image61"
																						onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																						onmouseout="MM_swapImgRestore()" alt="Up" border="0"></a>
																					</td>
																				</tr>
																				<tr>
																					<td align="right">
																						<a href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>">
																						<img src="<%=basePath%>/images/downarw.gif" name="Image61"
																						onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																						onmouseout="MM_swapImgRestore()" alt="Up" border="0"></a>
																				
																					</td>
																				</tr>
																			</table> --%>
																		</td>
																		<td align="left" class="tblheader" valign="top"
																			width="20%"><bean:message bundle="radiusResources"
																				key="radius.bwlist.attributevalue" /></td>
																		<td align="left" class="tblheader" valign="top" width="20%"><bean:message bundle="radiusResources" key="radius.bwlist.validity" />
																			<%-- <table border="0" cellpadding="0" cellspacing="0">
																				<tr align="right">
																					<td rowspan="2" align="left" class="tblheader" valign="top" >&nbsp;&nbsp;</td>
																					<td style="right: 100px;">
																						<a  href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>&validity=A">
																						<img src="<%=basePath%>/images/uparw.gif" name="Image61"
																						onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																						onmouseout="MM_swapImgRestore()" alt="Up" border="0"></a>
																					</td>
																				</tr>
																				<tr>
																					<td align="right">
																						<a href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>&validity=D">
																						<img src="<%=basePath%>/images/downarw.gif" name="Image61"
																						onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																						onmouseout="MM_swapImgRestore()" alt="Up" border="0"></a>
																				
																					</td>
																				</tr>
																			</table> --%>
																		</td>
																		<td align="left" class="tblheader" valign="top"
																			width="20%"><bean:message bundle="radiusResources"
																				key="radius.bwlist.remainingtime" /></td>
																		<td align="center" class="tblheader" valign="top" width="5%">
																			<bean:message bundle="radiusResources" key="radius.bwlist.status" />
																		</td>
																		<td align="center" class="tblheader" valign="top" width="5%">
																			<bean:message bundle="radiusResources" key="radius.bwlist.edit" />
																		</td>

																	</tr>
																										<%
																	if(bwList!=null && bwList.size()>0){		
																	%>
																	<logic:iterate id="bwBean" name="searchBWListForm"
																		property="bwList" type="BWListData">
																		<tr>
																			<td align="center" class="tblfirstcol">
																				<input type="checkbox" name="select" value="<bean:write	name="bwBean" property="bwId" />" />
																			</td>
																			
																			<logic:equal value="W" name="bwBean" property="typeName">
																				<td align="center" class="tblrows" style="color:#006400;">
																					<logic:notEmpty name="bwBean" property="typeName"><%-- <%=bwBean.getTypeName() %> --%>White</logic:notEmpty>
																					<logic:empty name="bwBean" property="typeName">-</logic:empty>
																				</td>
																				<td align="left" class="tblrows" style="color:#006400;">
																					<bean:write name="bwBean" property="attributeId" />
																				</td>
																				<td align="left" class="tblrows" style="color:#006400;"><%=EliteUtility.formatDescription(bwBean.getAttributeValue())%>&nbsp;</td>
																				<td align="left" class="tblrows" style="color:#006400;"><%=EliteUtility.getValidity(bwBean.getValidity())%>&nbsp;</td>
																				<td align="left" class="tblrows" style="color:#006400;"><%=EliteUtility.getRemainingTime(bwBean.getValidity())%>&nbsp;</td>
																				
																			</logic:equal>
																			<logic:notEqual value="W" name="bwBean" property="typeName">
																				<td align="center" class="tblrows">
																					<logic:notEmpty name="bwBean" property="typeName">Black</logic:notEmpty>
																					<logic:empty name="bwBean" property="typeName">-</logic:empty>
																				</td>
																				<td align="left" class="tblrows">
																				<bean:write name="bwBean" property="attributeId" />
																				</td>
																				<td align="left" class="tblrows"><%=EliteUtility.formatDescription(bwBean.getAttributeValue())%>&nbsp;</td>
																				<td align="left" class="tblrows"><%=EliteUtility.getValidity(bwBean.getValidity())%>&nbsp;</td>
																				<td align="left" class="tblrows"><%=EliteUtility.getRemainingTime(bwBean.getValidity())%>&nbsp;</td>
																				
																			</logic:notEqual>
																			
																		<%-- 	<td align="left" class="tblrows">
																				<bean:write name="bwBean" property="attributeId" />
																			</td>
																			<td align="left" class="tblrows"><%=EliteUtility.formatDescription(bwBean.getAttributeValue())%>&nbsp;</td>
																			<td align="left" class="tblrows"><%=EliteUtility.getValidity(bwBean.getValidity())%>&nbsp;</td>
																			<td align="left" class="tblrows"><%=EliteUtility.getRemainingTime(bwBean.getValidity())%>&nbsp;</td>
																		 --%>	<td align="center" class="tblrows">
																				<%
																					String CommonStatusValue = (String)org.apache.struts.util.RequestUtils.lookup(pageContext, "bwBean", "commonStatusId", null);
																					if(CommonStatusValue.equals(BaseConstant.HIDE_STATUS_ID) || CommonStatusValue.equals(BaseConstant.DEFAULT_STATUS_ID)){ 
																				%> <img src="<%=basePath%>/images/deactive.jpg" border="0">
																				<% }else if(CommonStatusValue.equals(BaseConstant.SHOW_STATUS_ID)){ %>
																					<img src="<%=basePath%>/images/active.jpg"
																					border="0"> <% } %>
																			</td>
																			<td align="center" class="tblrows">
																				<a href="<%=request.getContextPath()%>/updateBWList.do?bwId=<bean:write	name="bwBean" property="bwId" />">
																					<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
																				</a>
																			</td>
																		</tr>
																		<% count=count+1; %>
																		<% iIndex += 1; %>
																	</logic:iterate>
																	<%}else{%>
																	<tr>
																		<td align="center" class="tblfirstcol" colspan="8">No
																			Records Found.</td>
																	</tr>
																	<%} %>

																</table>
															</td>
														</tr>
														<tr height="2">
															<td></td>
														</tr>
														<tr>
															<td class="btns-td" valign="left"><input
																type="button" name="Active" Onclick="active()"
																value="   Active   " class="light-btn"> <input
																type="button" name="Inactive" Onclick="inactive()"
																value="   Inactive   " class="light-btn"> <input
																type="button" name="Delete" Onclick="removeRecord()"
																value="   Delete   " class="light-btn"></td>
															<!-- 	<td class="btns-td" valign="middle" colspan="3">&nbsp;</td> -->
															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>


																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>


																<% } else if(pageNo == totalPages){ %> <a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>


																<% } else { %> <a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>

																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchBWList.do?resultStatus=<%=strStatus%>&action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <% } %> <% } %>
															</td>
														</tr>
														<tr>
															<td>&nbsp;</td>
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
						</table>
					</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
			</table>
		</td>
	</tr>
</table>
