<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>

<%@ page import="com.elitecore.elitesm.web.datasource.database.forms.SearchDatabaseDSForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData"%>
<%@ page import="com.elitecore.elitesm.util.constants.DatabaseDSConstant"%>
<%@ page import="com.elitecore.elitesm.hibernate.datasource.database.HDatabaseDSDataManager"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData"%>
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
function show(){

	document.miscDatabaseDSForm.action.value = 'show';
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
				alert("Please select atleast one Database Datasource to Show.")
			}else{
					document.miscDatabaseDSForm.submit();
			}
	}
	else{
		alert("No Records Found For Show Operation! ")
	}


}
function hide(){

document.miscDatabaseDSForm.action.value = 'hide';
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
				alert("Please select atleast one Database Datasource to Hide.")
			}else{
			    	document.miscDatabaseDSForm.submit();
			}
	}
	else{
		alert("No Records Found For Hide Operation! ")
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
        alert('At least select one Database Datasource for remove process');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searchdatabasedsjsp.delete.query"/>';        
        //msg = "All the selected Staff Personnel would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
       	    document.miscDatabaseDSForm.action.value = 'delete';
        	document.miscDatabaseDSForm.submit();
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
setTitle('<bean:message bundle="datasourceResources" key="database.datasource"/>');
</script>
<%
     SearchDatabaseDSForm searchDatabaseDSForm = (SearchDatabaseDSForm)request.getAttribute("searchDatabaseDSForm");
     List lstDatabaseDS = searchDatabaseDSForm.getLstDatabaseDS();
     String strName = searchDatabaseDSForm.getName();
     
     long pageNo = searchDatabaseDSForm.getPageNumber();
     long totalPages = searchDatabaseDSForm.getTotalPages();
     long totalRecord = searchDatabaseDSForm.getTotalRecords();
	 int count=1;
     
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
								<td class="table-header">SEARCH DATABASE DATASOURCE</td>
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
										<html:form action="/searchDatabaseDS">
											<input type="hidden" name="c_strActionMode"
												id="c_strActionMode" value="102231105" />
											<html:hidden name="searchDatabaseDSForm" styleId="action"
												property="action" />
											<html:hidden name="searchDatabaseDSForm" styleId="pageNumber"
												property="pageNumber" />
											<html:hidden name="searchDatabaseDSForm" styleId="totalPages"
												property="totalPages" value="<%=strTotalPages%>" />
											<html:hidden name="searchDatabaseDSForm"
												styleId="totalRecords" property="totalRecords"
												value="<%=strTotalRecords%>" />
											<tr>
												<td align="left" class="captiontext" valign="top"
													width="10%">
													<bean:message bundle="datasourceResources" key="database.datasource.datasourcename"/> 
													<ec:elitehelp headerBundle="datasourceResources" text="dbdatasource.name" 
													header="database.datasource.datasourcename" />
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="name" tabindex="1" property="name"
														size="30" maxlength="30" style="width:250px" />
												</td>
											</tr>

											<tr>
												<td>&nbsp;</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<input type="button" tabindex="2" name="Search" width="5%"
													name="DatabaseDSName" Onclick="validateSearch()"
													value="   Search   " class="light-btn" /> <!-- <input type="button" name="Reset" onclick="reset();" value="   Reset    " class="light-btn" > -->
													<input type="button" tabindex="3" name="Create"
													value="   Create   "
													onclick="javascript:navigatePage('initCreateDatabaseDS.do','name');"
													class="light-btn">
												</td>
											</tr>
										</html:form>
										<%
		    //System.out.println("Every time check the value of pageno and strname : "+pageNo+" str is :"+strName);
			if(searchDatabaseDSForm.getAction()!=null && searchDatabaseDSForm.getAction().equalsIgnoreCase(DatabaseDSConstant.LISTACTION)){
	       	%>
										<html:form action="/miscDatabaseDS">
											<html:hidden name="miscDatabaseDSForm" styleId="action"
												property="action" />
											<html:hidden name="miscDatabaseDSForm" styleId="name"
												property="name" value="<%=strName%>" />
											<html:hidden name="miscDatabaseDSForm" styleId="pageNumber"
												property="pageNumber" value="<%=strPageNumber%>" />
											<html:hidden name="miscDatabaseDSForm" styleId="totalPages"
												property="totalPages" value="<%=strTotalPages%>" />
											<html:hidden name="miscDatabaseDSForm" styleId="totalRecords"
												property="totalRecords" value="<%=strTotalRecords%>" />

											<tr>
												<td align="left" class="labeltext" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="100%"
														border="0">
														<tr>
															<td class="table-header" width="50%">DATABASE
																DATASOURCE LIST</td>

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
																	property="c_btnDelete" tabindex="4" onclick="removeData()"
																	value="   Delete   " styleClass="light-btn" />
															</td>
															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchDatabaseDS.do?action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a href="searchDatabaseDS.do?action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchDatabaseDS.do?action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else if(pageNo == totalPages){ %> <a
																href="searchDatabaseDS.do?action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchDatabaseDS.do?action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else { %> <a
																href="searchDatabaseDS.do?action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchDatabaseDS.do?action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="searchDatabaseDS.do?action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo-1%>"><img
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
																<table width="99%" border="0" cellpadding="0"
																	cellspacing="0" id="listTable">
																	<tr>
																		<td align="center" class="tblheader" valign="top"
																			width="1%"><input type="checkbox"
																			name="toggleAll" value="checkbox"
																			onclick="checkAll()" /></td>
																		<td align="center" class="tblheader" valign="top"
																			width="40px"><bean:message
																				bundle="datasourceResources"
																				key="database.datasource.serialnumber" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="25%"><bean:message
																				bundle="datasourceResources"
																				key="database.datasource.name" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="30%"><bean:message
																				bundle="datasourceResources"
																				key="database.datasource.connectionurl" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="*"><bean:message
																				bundle="datasourceResources"
																				key="database.datasource.username" /></td>
																		<td align="center" class="tblheader" valign="top"
																			width="40px"><bean:message
																				bundle="datasourceResources"
																				key="database.datasource.edit" /></td>
																	</tr>
																	<%	if(lstDatabaseDS!=null && lstDatabaseDS.size()>0){%>
																	<logic:iterate id="databaseDSBean"
																		name="searchDatabaseDSForm" property="lstDatabaseDS"
																		type="com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData">
																		<%	IDatabaseDSData sData = (IDatabaseDSData)lstDatabaseDS.get(iIndex);
							Timestamp dtLastUpdate = sData.getLastmodifiedDate();
						%>
																		<tr>
																			<td align="center" class="tblfirstcol" width="5%">
																				<input type="checkbox" name="select"
																				value="<bean:write name="databaseDSBean" property="databaseId"/>" />
																			</td>
																			<td align="center" class="tblrows"><%=((pageNo-1)*pageSize)+count%></td>
																			<td align="left" class="tblrows"><a
																				href="<%=basePath%>/viewDatabaseDS.do?databaseId=<bean:write name="databaseDSBean" property="databaseId"/>"><bean:write
																						name="databaseDSBean" property="name" /></a></td>
																			<td align="left" class="tblrows"><bean:write
																					name="databaseDSBean" property="connectionUrl" />&nbsp;&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="databaseDSBean" property="userName" />&nbsp;&nbsp;</td>
																			<td align="center" class="tblrows"><a
																				href="updateDatabaseDS.do?databaseId=<bean:write name="databaseDSBean" property="databaseId"/>"><img
																					src="<%=basePath%>/images/edit.jpg" alt="Edit"
																					border="0"></a></td>
																		</tr>
																		<% count=count+1; %>
																		<% iIndex += 1; %>
																	</logic:iterate>
																	<%	}else{
				%>
																	<tr>
																		<td align="center" class="tblfirstcol" colspan="8">No
																			Records Found.</td>
																	</tr>
																	<%	}%>
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
																href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchDatabaseDS.do?action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a href="searchDatabaseDS.do?action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchDatabaseDS.do?action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else if(pageNo == totalPages){ %> <a
																href="searchDatabaseDS.do?action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchDatabaseDS.do?action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else { %> <a
																href="searchDatabaseDS.do?action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchDatabaseDS.do?action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="searchDatabaseDS.do?action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchDatabaseDS.do?action=list&pageNo=<%= pageNo-1%>"><img
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

