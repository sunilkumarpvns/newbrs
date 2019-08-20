<%@page import="com.elitecore.elitesm.util.constants.BaseConstant"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptTypeData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData"%>
<%@page import="com.elitecore.elitesm.web.script.form.ScriptInstanceForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

 	<% String basePath = request.getContextPath(); %>	
<%
  

 		ScriptInstanceForm searchScriptForm = (ScriptInstanceForm) session.getAttribute("searchScriptForm");
     	List<ScriptInstanceData> scriptList = searchScriptForm.getScriptInstanceDataList();

		String action = searchScriptForm.getAction(); 
     	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW")); 
     	long pageNo = searchScriptForm.getPageNumber(); 
     	long totalPages = searchScriptForm.getTotalPages(); 
     	long totalRecord = searchScriptForm.getTotalRecords(); 
     	String strTotalPages=String.valueOf(totalPages); 
     	String strTotalRecords=String.valueOf(totalRecord); 
     	String strName = searchScriptForm.getScriptName(); 
     	String strStatus = searchScriptForm.getStatus() != null ? searchScriptForm.getStatus() : ""; 
     	
     	String strScriptType = searchScriptForm.getSelectedScript(); 
     	String paramString = "name=" + strName + "&scriptType="+ strScriptType+"&resultStatus="+strStatus; 
	    int count=1; 
%>
    	
<script>
function initCreateScript() {
		document.forms[0].action = 'script.do?method=initCreate';
        document.forms[0].submit();
	
}
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
        alert('At least select Script Instance for remove process');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searchscript.delete.query"/>';        
        var agree = confirm(msg);
        if(agree){            
       	    document.forms[0].action.value = 'delete';
        	document.forms[0].submit();
        }
    }
}

function show(){

	document.forms[0].action.value = 'show';
	var selectArray = document.getElementsByName('select');
	
	if(selectArray.length>0){
	
			var b = true;
			for (i=0; i<selectArray.length; i++){
		
			 	 if (selectArray[i].checked == false){  			
			 	 	b=false;
			 	 } else {
					b=true;
					break;
				}
			}
			if(b==false){
				alert("Please select atleast one Script Instance to Show.");
			}else{
					document.forms[0].submit();
			}
	}
	else{
		alert("No Records Found For Show Operation! ")
	}
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
					alert("Please select atleast one Script Instance to Hide.");
				}else{
				    document.forms[0].submit();
				}
		}
		else{
			alert("No Records Found For Hide Operation! ");
		}
	}
</script>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

	<html:form action="/script.do?method=search">
	<html:hidden property="action" styleId="action" />
	<html:hidden property="pageNumber" styleId="pageNumber" />
	<html:hidden property="totalPages" styleId="totalPages" value="<%=strTotalPages%>" />
	<html:hidden property="totalRecords" styleId="totalRecords" value="<%=strTotalRecords%>" />

	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header" colspan="5">
										<bean:message bundle="scriptResources" key="script.searchscript" />
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
										<bean:message bundle="scriptResources" key="script.name" /> 
										<ec:elitehelp headerBundle="scriptResources" text="script.name" header="script.name"/>
									</td>
									<td align="left" class="labeltext" valign="top" width="66%">
										<html:text styleId="name" property="scriptName" size="30" maxlength="60" style="width:250px" tabindex="1" />
									</td>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top" width="18%">
										<bean:message bundle="scriptResources" key="script.scripttype" />
										<ec:elitehelp headerBundle="scriptResources" text="script.scripttype" header="script.scripttype"/>
									</td>
									<td align="left" class="labeltext" valign="top" width="66%">
										<html:select name="searchScriptForm" styleId="selectedScript" property="selectedScript" tabindex="2" style="width:250px;">
											<html:option value="">Select</html:option>
											<logic:iterate id="objservice" name="searchScriptForm" type="ScriptTypeData" property="scriptTypeList">
												<html:option value="<%=objservice.getScriptTypeId()%>"><%=objservice.getDisplayName()%></html:option>
											</logic:iterate>
										</html:select>
									</td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="labeltext" valign="middle">&nbsp;</td>
									<td class="labeltext" valign="middle" colspan="2">
										<input type="button" name="c_btnCreate" id="c_btnCreate2" value=" Search " class="light-btn" onclick="validateForm()" tabindex="3"> 
										<input type="button" name="Create" value="   Create   " class="light-btn" onclick="javascript:initCreateScript();" tabindex="4">
									</td>
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
												<td class="table-header" width="50%">
													<bean:message bundle="scriptResources" key="script.list" />
												</td>
												<td align="right" class="blue-text" valign="middle" width="50%">
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
												<td class="btns-td" valign="middle">
													<input type="button" name="Show" onclick="show()" value="   Active   " class="light-btn" tabindex="8" />
													<input type="button" name="Hide" onclick="hide()" value="   Inactive   " class="light-btn" tabindex="9" />
													<input type="button" name="Delete" OnClick="removeData()" value="   Delete   " class="light-btn" tabindex="5" />
												</td>
												<td class="btns-td" align="right">
												<%
									  				if (totalPages >= 1) {
									  			%> <%
										  			if (pageNo == 1) { 	%>
										  			<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=pageNo + 1%>">
										  				<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0" />
													</a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=totalPages + 1%>">
														<img src="<%=basePath%>/images/last.jpg" name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0">
													</a>
													<%
										  				}
										  			%> <%
														if (pageNo > 1 && pageNo != totalPages + 1) {
													%> <%
														if (pageNo - 1 == 1) {
													%> 
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=1%>">
														<img src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0">
													</a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=pageNo - 1%>">
														<img src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=pageNo + 1%>">
														<img src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0">
													</a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=totalPages + 1%>">
														<img src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0">
													</a>
													<% } else if (pageNo == totalPages) { %>
													 <a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=1%>">
													 	<img src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0">
													</a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=pageNo - 1%>">
														<img src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=pageNo + 1%>">
														<img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=totalPages + 1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<%
													} else {
													%>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=1%>">
														<img src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0">
													</a>
													<a 	href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=pageNo - 1%>">
														<img src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=pageNo + 1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0">
													</a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=totalPages + 1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0">
													</a>
													<%
													}
													%> <%
													}
												%> <%
													if (pageNo == totalPages + 1) {
												 %>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=1%>">
														<img src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0" />
													</a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=pageNo - 1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
													</a>
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
													<table width="100%" border="0" cellpadding="0" 	cellspacing="0" id="listTable">
														<tr>
															<td align="center" class="tblheader" valign="top" width="1%">
																<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll()" />
															</td>
															<td align="center" class="tblheader" valign="top" width="40px">Sr. No.</td>
															<td align="left" class="tblheader" valign="top" width="25%">Name</td>
															<td align="left" class="tblheader" valign="top" width="25%">Script Type</td>
															<td align="left" class="tblheader" valign="top" width="*">Script Description</td>
															<td align="left" class="tblheader" valign="top" width="40px">Status</td>
															<td align="center" class="tblheader" valign="top" width="40px">Edit</td>
														</tr>

														<%
															if (scriptList != null && !scriptList.isEmpty()) {
														%>

														<logic:iterate id="obj" name="searchScriptForm" property="scriptInstanceDataList" type="ScriptInstanceData">
															<tr>
																<td align="center" class="tblfirstcol">
																	<input type="checkbox" name="select" value="<bean:write name="obj" property="scriptId"/>" />
																</td>
																<td align="center" class="tblfirstcol">
																	<%=((pageNo-1)*pageSize)+count%>
																</td>
																<td align="left" class="tblrows">
																	<a href="script.do?method=viewScript&action=view&scriptId=<bean:write name="obj" property="scriptId"/>">
																		<bean:write name="obj" property="name" />
																	</a>
																</td>
																<td align="left" class="tblrows">
																	<bean:write name="obj" property="scriptTypeName" />
																</td>
																<td align="left" class="tblrows"><%=EliteUtility.formatDescription(obj.getDescription())%>&nbsp;&nbsp;</td>
																<td align="center" class="tblrows">
																	<%if(obj.getStatus().equals(BaseConstant.HIDE_STATUS_ID) ||  obj.getStatus().equals(BaseConstant.DEFAULT_STATUS_ID)){%> <img src="<%=basePath%>/images/hide.jpg" border="0">
																		<% }else if(obj.getStatus().equals(BaseConstant.SHOW_STATUS_ID)){ %>
																		<img src="<%=basePath%>/images/show.jpg" border="0">
																		<% } %>
																</td>
																<td align="center" class="tblrows">
																	<a href="script.do?method=initUpdate&scriptId=<bean:write name="obj" property="scriptId"/>">
																		<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
																	</a>
																</td>
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
												<td class="btns-td" valign="middle">
													<input type="button" name="Show" onclick="show()" value="   Active   " class="light-btn" tabindex="8" />
													<input type="button" name="Hide" onclick="hide()" value="   Inactive   " class="light-btn" tabindex="9" />
													<input type="button" name="Delete" OnClick="removeData()" value="   Delete   " class="light-btn" tabindex="5" />
												</td>
												<td class="btns-td" align="right">
													<%
							  							if (totalPages >= 1) {
							  						%> <%
										  				if (pageNo == 1) {
										  			%> 
										  			<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=pageNo + 1%>">
										  				<img src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0">
													</a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=totalPages + 1%>">
														<img src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %>
													<% if (pageNo > 1 && pageNo != totalPages + 1) {
													%> <%
														if (pageNo - 1 == 1) {
													%> 
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=pageNo - 1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=pageNo + 1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=totalPages + 1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<%
														} else if (pageNo == totalPages) {
													%> 
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a	href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=pageNo - 1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=pageNo + 1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=totalPages + 1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<%
													} else {
													%> 
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=1%>"><img
													   src="<%=basePath%>/images/first.jpg" name="Image511"
													   onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
													   onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=pageNo - 1%>"><img
													   src="<%=basePath%>/images/previous.jpg" name="Image5"
													   onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
													   onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=pageNo + 1%>"><img
													   src="<%=basePath%>/images/next.jpg" name="Image61"
													   onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
													   onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=totalPages + 1%>"><img
													   src="<%=basePath%>/images/last.jpg" name="Image612"
													   onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
													   onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<%
														}
													%> <%
													}
											%> <% if (pageNo == totalPages + 1) {
												%> 
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a href="script.do?method=search<%=paramString%>&action=list&pageNo=<%=pageNo - 1%>"><img
													   src="<%=basePath%>/images/previous.jpg" name="Image5"
													   onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
													   onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
											<% } %> <%
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
	setTitle('<bean:message bundle="scriptResources" key="script.title"/>'); 	
</script>
