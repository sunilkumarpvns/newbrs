

<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatabaseSubscriberProfileDataBean"%>





<%@ page import="java.util.List"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page
	import="com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.SearchDatabaseSubscriberProfileDataForm"%>
<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatasourceSchemaData"%>
<%
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ConfigManager.get("DATE_FORMAT"));
	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
    int iIndex =0;
    
%>

<script>

function searchRow(){
	document.forms[0].action.value="search";		
	document.forms[0].submit();
}

function check(){

	var test=document.getElementById('toggleAll');
	
	if(test.checked==true){
		checkAll();
	}
	if(test.checked==false){
		unCheckAll();
	}
	
}

function checkAll(){

	var checkboxes = document.getElementsByName("select");
	var totalBoxes = checkboxes.length;
	if(checkboxes.length==1){
		document.miscSearchDatabaseSubscriberProfileForm.select.checked=true
		return;
	}
	for(var i=0;i<totalBoxes;i++){
		document.miscSearchDatabaseSubscriberProfileForm.select[i].checked=true
	}	
	
}
function unCheckAll(){

	var checkboxes = document.getElementsByName("select");
	var totalBoxes = checkboxes.length;
	if(checkboxes.length==1){
		document.miscSearchDatabaseSubscriberProfileForm.select.checked=false;
		return;
	}
	for(var i=0;i<totalBoxes;i++){
		document.miscSearchDatabaseSubscriberProfileForm.select[i].checked=false;
	}
	
}
function deleteDatasourceData(){
	var flag=false;
	var checkboxes = document.getElementsByName("select");
	var totalBoxes = checkboxes.length;
	document.miscSearchDatabaseSubscriberProfileForm.action.value = 'delete';
	if(totalBoxes==1){
	
		if(document.miscSearchDatabaseSubscriberProfileForm.select.checked==true){
			flag=true;
		}	
		
	}else{
		for(var i=0;i<totalBoxes;i++){
			if(document.miscSearchDatabaseSubscriberProfileForm.select[i].checked==true){
				flag=true;
			}
		}		
	}	

	if(flag==false){
		alert("select atleast one data");
		return false;
	}	
	document.miscSearchDatabaseSubscriberProfileForm.submit();
	
}

</script>
<%                                         														   
	SearchDatabaseSubscriberProfileDataForm searchDatabaseSubscriberProfileDataForm = (SearchDatabaseSubscriberProfileDataForm) request.getAttribute("searchDatabaseSubscriberProfileDataForm");
    String firstFieldName = searchDatabaseSubscriberProfileDataForm.getFirstFieldName();
	String firstFieldData = searchDatabaseSubscriberProfileDataForm.getFirstFieldData();
	
	String idFieldName = searchDatabaseSubscriberProfileDataForm.getIdFieldName();
	List lstFieldName = searchDatabaseSubscriberProfileDataForm.getLstFieldName();
	List lstSeachRow = searchDatabaseSubscriberProfileDataForm.getSeachRowList();
	DatasourceSchemaData datasourceSchemaData = null;
	int totalField = searchDatabaseSubscriberProfileDataForm.getTotalField();
	
	long totalRecord = searchDatabaseSubscriberProfileDataForm.getTotalNumberOfRecord(); 
	long totalPages = searchDatabaseSubscriberProfileDataForm.getTotalNoOfPage(); 
	int pageNo = searchDatabaseSubscriberProfileDataForm.getCurrentPage(); 
	
	String strTotalPages = String.valueOf(totalPages);
    String strTotalRecords = String.valueOf(totalRecord);
    String strPageNumber = String.valueOf(pageNo);
	int serialNo = 1;
    String searchTailedUrl = "driverInstanceId="+searchDatabaseSubscriberProfileDataForm.getDriverInstanceId()+"&dbAuthId="+searchDatabaseSubscriberProfileDataForm.getDbAuthId()+"&firstFieldData="+firstFieldData;
%>

<html:form action="/searchDatabaseSubscriberProfileData">

	<html:hidden name="searchDatabaseSubscriberProfileDataForm"
		styleId="driverInstanceId" property="driverInstanceId" />
	<html:hidden name="searchDatabaseSubscriberProfileDataForm"
		styleId="dbAuthId" property="dbAuthId" />
	<html:hidden name="searchDatabaseSubscriberProfileDataForm"
		styleId="action" property="action" value="search" />
	<html:hidden name="searchDatabaseSubscriberProfileDataForm"
		styleId="currentPage" property="currentPage"
		value="<%=strPageNumber%>" />
	<html:hidden name="searchDatabaseSubscriberProfileDataForm"
		styleId="totalNoOfPage" property="totalNoOfPage"
		value="<%=strTotalPages%>" />
	<html:hidden name="searchDatabaseSubscriberProfileDataForm"
		styleId="totalNumberOfRecord" property="totalNumberOfRecord"
		value="<%=strTotalRecords%>" />

	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					height="15%">
					<tr>
						<td class="tblheader-bold" colspan="7">Field Data</td>
					<tr>
						<%if(firstFieldName.contains("USER")){ %>
					
					<tr>
						<td class="captiontext" width="20%" height="20%"><bean:write
								name="searchDatabaseSubscriberProfileDataForm"
								property="firstFieldName" /></td>
						<td class="labeltext" width="80%" height="20%"><html:text
								styleId="" property="firstFieldData" size="20" /> &nbsp;[search
							across username and userIdentity] </br></td>
					</tr>
					<%}else{ %>
					<tr>
						<td class="captiontext" width="20%" height="20%"><bean:write
								name="searchDatabaseSubscriberProfileDataForm"
								property="firstFieldName" /></td>
						<td class="labeltext" width="80%" height="20%"><html:text
								styleId="" property="firstFieldData" size="20" /> &nbsp; </br></td>
					</tr>

					<%} %>
					</br>
					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td align="left" class="captiontext" width="20%" height="20%">
							<input type="button" value="  Search   " class="light-btn"
							onclick="searchRow()" />
						</td>

					</tr>
				</table>
			</td>
		</tr>
	</table>
	</br>
</html:form>


<%
      if(searchDatabaseSubscriberProfileDataForm.getAction()!=null && searchDatabaseSubscriberProfileDataForm.getAction().equalsIgnoreCase("search")){
%>


<%



if (lstFieldName != null && lstFieldName.size() > 0) {
	int lstFieldNameSize = lstFieldName.size(); 
	if(totalField>=lstFieldNameSize)
	{
		totalField=lstFieldNameSize-1;
	}
    
%>
<html:form action="/miscSearchDatabaseSubscriberProfile">
	<html:hidden name="miscSearchDatabaseSubscriberProfileForm"
		styleId="miscAction" property="miscAction" value="delete" />
	<html:hidden name="miscSearchDatabaseSubscriberProfileForm"
		styleId="driverInstanceId" property="driverInstanceId"
		value="<%=searchDatabaseSubscriberProfileDataForm.getDriverInstanceId()%>" />
	<html:hidden name="miscSearchDatabaseSubscriberProfileForm"
		styleId="dbAuthId" property="dbAuthId"
		value="<%=searchDatabaseSubscriberProfileDataForm.getDbAuthId()%>" />
	<html:hidden name="miscSearchDatabaseSubscriberProfileForm"
		styleId="pageNo" property="pageNo"
		value="<%=Integer.toString(pageNo)%>" />
	<html:hidden name="miscSearchDatabaseSubscriberProfileForm"
		styleId="totalNumberOfRecord" property="totalNumberOfRecord"
		value="<%=Long.toString(totalRecord)%>" />
	<html:hidden name="miscSearchDatabaseSubscriberProfileForm"
		styleId="totalPage" property="totalPage"
		value="<%=Long.toString(totalPages)%>" />
	<html:hidden name="miscSearchDatabaseSubscriberProfileForm"
		styleId="firstFieldName" property="firstFieldName"
		value="<%=firstFieldName%>" />
	<html:hidden name="miscSearchDatabaseSubscriberProfileForm"
		styleId="firstFieldData" property="firstFieldData"
		value="<%=firstFieldData%>" />



	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table cellpading="0" cellspacing="0" border="0" width="100%"
					height="15%">
					<tr>
						<td class="table-header" width="24%"><bean:message
								bundle="driverResources"
								key="subscriberprofile.database.fieldlist" /></td>
						<td align="left" class="blue-text" valign="middle" width="62%">
							&nbsp;</td>
						<td align="right" class="blue-text" valign="middle" width="14%">
							<% if(totalRecord == 0) {%> <%}else if(pageNo == totalPages+1) { %> [<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>]
							of <%= totalRecord %> <% } else if(pageNo == 1) { %> [<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>]
							of <%= totalRecord %> <% } else { %> [<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>]
							of <%= totalRecord %> <% } %>
						</td>
					</tr>

					<tr>
						<td valign="left"><input type="button" name="addRow"
							value="  Create  " class="light-btn"
							onclick="javascript:location.href='<%=basePath%>/addDatabaseSubscriberProfileData.do?<%=searchTailedUrl%>'">
							<input type="button" name="c_btnDelete" value=" Delete   "
							class="light-btn" onclick="deleteDatasourceData();" /></td>
						<td align="right" colspan="2">
							<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= pageNo+1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/next.jpg" name="Image61"
								onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= totalPages+1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/last.jpg" name="Image612"
								onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a> <% } %>
							<% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
							<a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%=1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/first.jpg" name="Image511"
								onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="First" border="0"></a> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= pageNo-1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/previous.jpg" name="Image5"
								onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
							<a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= pageNo+1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/next.jpg" name="Image61"
								onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= totalPages+1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/last.jpg" name="Image612"
								onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a> <% } else if(pageNo == totalPages){ %>

							<a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= 1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/first.jpg" name="Image511"
								onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="First" border="0"></a> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= pageNo-1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/previous.jpg" name="Image5"
								onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
							<a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= pageNo+1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/next.jpg" name="Image61"
								onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= totalPages+1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/last.jpg" name="Image612"
								onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a> <% } else { %>
							<a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= 1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/first.jpg" name="Image511"
								onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="First" border="0"></a> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= pageNo-1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/previous.jpg" name="Image5"
								onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
							<a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= pageNo+1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/next.jpg" name="Image61"
								onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= totalPages+1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/last.jpg" name="Image612"
								onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a> <% } %>
							<% } %> <% if(pageNo == totalPages+1) { %> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%=1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/first.jpg" name="Image511"
								onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="First" border="0"></a> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= pageNo-1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/previous.jpg" name="Image5"
								onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>

							<% } %> <% } %>

						</td>
					</tr>
					<tr>
						<td colspan="3">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td valign="top" align="right" width="100%">
										<table cellpadding="0" cellspacing="0" border="0" width="100%"
											height="15%">
											<tr align="left">
												<td class="tblheader-bold" colspan="<%=totalField + 3%>">
													<bean:message bundle="driverResources"
														key="subscriberprofile.database.tablefielddata" />
												</td>
											</tr>
											<tr>
												<td colspan="<%=totalField %>">
													<table cellpadding="0" cellspacing="0" border="0"
														width="100%" height="15%">
														<tr>
															<td align="center" valign="top" width="3%"
																class="tblheader"><input type="checkbox"
																name="toggleAll" id="toggleAll" value="checkbox"
																onclick="check();" /></td>
															<td align="center" valign="top" width="3%"
																class="tblheader"><bean:message
																	bundle="driverResources"
																	key="subscriberprofile.database.serialnumber" /></td>
															<%
																		int dataTypeValue = 0;
																		for (int i = 1; i < (totalField + 1); i++) {
																			datasourceSchemaData = (DatasourceSchemaData) lstFieldName.get(i);

																%>
															<td align="center" valign="top" width="5%"
																class="tblheader"><%=datasourceSchemaData.getFieldName()%>
															</td>
															<%
																}
																%>
															<td align="center" valign="top" width="3%"
																class="tblheader"><bean:message
																	bundle="driverResources"
																	key="subscriberprofile.database.edit" /></td>
															<td align="center" valign="top" width="3%"
																class="tblheader"><bean:message
																	bundle="driverResources"
																	key="subscriberprofile.database.duplicate" /></td>
														</tr>

														<%
																if (lstSeachRow.size() <= 0) {
															%>
														<tr>
															<td align="center" class="tblleftlinerows" valign="top"
																width="100%" colspan="<%=totalField+4%>"><bean:message
																	bundle="driverResources"
																	key="subscriberprofile.database.norecordfound" /></td>
														</tr>
														<%
															}
															%>

														<%
																serialNo = ((pageNo-1)*pageSize)+serialNo;
															%>

														<%
																for (int i = 0; i < lstSeachRow.size(); i++) {
															%>
														<pg:item>
															<%
																List lstColumn = (List) lstSeachRow.get(i);
																IDatabaseSubscriberProfileDataBean datasourceDataFieldId = null;
															for(int k=0; k<lstColumn.size(); k++){
																 datasourceDataFieldId = (IDatabaseSubscriberProfileDataBean) lstColumn.get(k);
																if("USER_IDENTITY".equalsIgnoreCase(datasourceDataFieldId.getFieldName())){
																	break;
																}
															}
																
															%>
															<tr>
																<td align="center" class="tblleftlinerows" valign="top"
																	width="2%"><input type="checkbox" name="select"
																	value="<%=datasourceDataFieldId.getFieldValue()%>" /></td>

																<td align="center" valign="top" width="3%"
																	class="tblleftlinerows"><%=serialNo++%></td>
																<%
																	
																	for (int j = 1; j < (totalField + 1); j++) {
																		IDatabaseSubscriberProfileDataBean datasourceDataBean = (IDatabaseSubscriberProfileDataBean) lstColumn.get(j);
																%>

																<%
																	java.sql.Timestamp date = new java.sql.Timestamp((new java.util.Date()).getTime());
																	String fieldValue = null;
																	if (datasourceDataBean.getFieldValue() != null && datasourceDataBean.getFieldValue().getClass().isInstance(date)) {
																		date = (java.sql.Timestamp) datasourceDataBean.getFieldValue();
																		fieldValue = simpleDateFormat.format(date);
																	} else if(datasourceDataBean.getFieldValue()==null){
																		fieldValue="-"; 
																	}
																	else{
																		fieldValue = (String) datasourceDataBean.getFieldValue();
																	}
																%>
																<td align="center" valign="top" width="5%"
																	class="tblleftlinerows">
																	<% if(datasourceDataBean.getFieldName().equals("USERNAME")){ %>
																		 <a
																			href="<%=basePath%>/viewDatabaseSubscriberProfileData.do?driverInstanceId=<bean:write name="searchDatabaseSubscriberProfileDataForm" property="driverInstanceId"/>&strFieldId=<%=datasourceDataFieldId.getFieldValue()%>&strFieldName=<%=datasourceDataFieldId.getFieldName()%>&strType=edit">
																		<u><%=fieldValue%></u>
																		</a>
																		  
																	<%}else{ %>
																		<%=fieldValue%>
																	<%} %>
																	</td>
																<%
																	}
																%>
																
																<td align="center" valign="middle" width="3%"
																	class="tblleftlinerows"><html:hidden
																		name="miscSearchDatabaseSubscriberProfileForm"
																		styleId="fieldName" property="fieldName"
																		value="<%=datasourceDataFieldId.getFieldName()%>" />
																	<a
																	href="<%=basePath%>/updateDatabaseSubscriberProfileData.do?driverInstanceId=<bean:write name="searchDatabaseSubscriberProfileDataForm" property="driverInstanceId"/>&strFieldId=<%=datasourceDataFieldId.getFieldValue()%>&strFieldName=<%=datasourceDataFieldId.getFieldName()%>&strType=edit"><img
																		src="<%=basePath%>/images/edit.jpg" alt="Edit"
																		border="0"> </a></td>
																<td align="center" valign="middle" width="3%"
																	class="tblleftlinerows"><html:hidden
																		name="miscSearchDatabaseSubscriberProfileForm"
																		styleId="fieldName" property="fieldName"
																		value="<%=datasourceDataFieldId.getFieldName()%>" />
																	<a
																	href="<%=basePath%>/updateDatabaseSubscriberProfileData.do?driverInstanceId=<bean:write name="searchDatabaseSubscriberProfileDataForm" property="driverInstanceId"/>&strFieldId=<%=datasourceDataFieldId.getFieldValue()%>&strFieldName=<%=datasourceDataFieldId.getFieldName()%>&strType=duplicate"><img
																		src="<%=basePath%>/images/Copy.gif" alt="Edit"
																		border="0"> </a></td>
															</tr>
														</pg:item>
														<%
															}
															%>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td valign="left"><input type="button" name="addRow"
							value="  Create  " class="light-btn"
							onclick="javascript:location.href='<%=basePath%>/addDatabaseSubscriberProfileData.do?<%=searchTailedUrl%>'">
							<input type="button" name="c_btnDelete" value=" Delete   "
							class="light-btn" onclick="deleteDatasourceData();" /></td>
						<td align="right" colspan="2">
							<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= pageNo+1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/next.jpg" name="Image61"
								onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= totalPages+1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/last.jpg" name="Image612"
								onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a> <% } %>
							<% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
							<a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%=1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/first.jpg" name="Image511"
								onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="First" border="0"></a> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= pageNo-1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/previous.jpg" name="Image5"
								onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
							<a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= pageNo+1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/next.jpg" name="Image61"
								onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= totalPages+1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/last.jpg" name="Image612"
								onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a> <% } else if(pageNo == totalPages){ %>

							<a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= 1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/first.jpg" name="Image511"
								onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="First" border="0"></a> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= pageNo-1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/previous.jpg" name="Image5"
								onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
							<a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= pageNo+1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/next.jpg" name="Image61"
								onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= totalPages+1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/last.jpg" name="Image612"
								onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a> <% } else { %>
							<a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= 1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/first.jpg" name="Image511"
								onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="First" border="0"></a> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= pageNo-1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/previous.jpg" name="Image5"
								onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
							<a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= pageNo+1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/next.jpg" name="Image61"
								onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= totalPages+1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/last.jpg" name="Image612"
								onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a> <% } %>
							<% } %> <% if(pageNo == totalPages+1) { %> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%=1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/first.jpg" name="Image511"
								onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="First" border="0"></a> <a
							href="searchDatabaseSubscriberProfileData.do?action=paging&pageNo=<%= pageNo-1%>&<%=searchTailedUrl%>"><img
								src="<%=basePath%>/images/previous.jpg" name="Image5"
								onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>

							<% } %> <% } %>

						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

</html:form>
<%
}
}
%>