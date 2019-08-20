
<%@page import="com.elitecore.coreradius.util.mbean.data.MBeanConstants"%>




<%@ page import="java.util.Map"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.server.form.ListUserfileAccountInformationForm"%>

<%
	String localBasePath=request.getContextPath();
	String netserverid=(String)request.getParameter("netserverid");
	
	ListUserfileAccountInformationForm listUserfileAccountInformationForm=(ListUserfileAccountInformationForm)request.getAttribute("listUserfileAccountInformationForm");
	Map accountMaps = listUserfileAccountInformationForm.getUserAccountMap();
	
	int recordPerPage = listUserfileAccountInformationForm.getNumerOfRecordsPerPage();
	int totalNumberOfRecord = listUserfileAccountInformationForm.getTotalNumberOfRecord();
	int totalPage = listUserfileAccountInformationForm.getTotalNoOfPage();
	int nextPage = listUserfileAccountInformationForm.getNextPage();
	int previousPage = listUserfileAccountInformationForm.getPreviousPage();
	int start = listUserfileAccountInformationForm.getStart();
	int end = listUserfileAccountInformationForm.getEnd();
	int currentPage=listUserfileAccountInformationForm.getPageNo();
	String selectedFileName=listUserfileAccountInformationForm.getSelectedFileName();
	
%>
<script type="text/javascript">
<!--
function addNewUserAccount(){

	location.href="<%=localBasePath%>/createUserfileDatasource.do?netserverid=<%=netserverid%>&filename=<bean:write name="listUserfileAccountInformationForm" property="selectedFileName"/>";
	
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
	
	for(var i=0;i<totalBoxes;i++){
		document.miscUserfileDatasourceForm.select[i].checked=true
	}	
	
}
function unCheckAll(){

	var checkboxes = document.getElementsByName("select");
	var totalBoxes = checkboxes.length;
	for(var i=0;i<totalBoxes;i++){
		document.miscUserfileDatasourceForm.select[i].checked=false;
	}
	
}
function deleteDatasourceData(){
	var flag=false;
	var checkboxes = document.getElementsByName("select");
	var totalBoxes = checkboxes.length;
	document.miscUserfileDatasourceForm.action.value="paging";
	if(totalBoxes==1){
		if(document.miscUserfileDatasourceForm.select.checked==true){
			flag=true;
		}	
	}else{
		for(var i=0;i<totalBoxes;i++){
			if(document.miscUserfileDatasourceForm.select[i].checked==true){
				flag=true;
			}
		}		
	}	

	if(flag==false){
		alert("select atleast one data");
		return false;
	}else{
	  var r=confirm("This will delete the selected items. Do you want to continue ?");
			if (r==true)
  			{
  			     document.miscUserfileDatasourceForm.submit();
	        }
	}        
		
	
}
//-->
</script>
<html:form action="/miscUserfileDatasource">
	<html:hidden name="miscUserfileDatasourceForm" styleId="action" property="action" />
	<html:hidden name="miscUserfileDatasourceForm" styleId="pageNo" property="pageNo"	value="<%=Integer.toString(currentPage)%>" />
	<html:hidden name="miscUserfileDatasourceForm" styleId="totalNumberOfRecord" property="totalNumberOfRecord" value="<%=Integer.toString(totalNumberOfRecord)%>" />
	<html:hidden name="miscUserfileDatasourceForm" styleId="totalPage" property="totalPage" value="<%=Integer.toString(totalPage)%>" />
	<html:hidden name="miscUserfileDatasourceForm" styleId="selectedFileName" property="selectedFileName" value="<%= selectedFileName%>" />
	<html:hidden name="miscUserfileDatasourceForm" styleId="netserverid" property="netserverid" value="<%= netserverid%>" />
	
	<html:hidden name="miscUserfileDatasourceForm" styleId="totalRecords" property="totalRecords" value="<%=Integer.toString(totalNumberOfRecord)%>" />
    <html:hidden name="miscUserfileDatasourceForm" styleId="totalPages" property="totalPages" value="<%=Integer.toString(totalPage)%>" />
    <html:hidden name="miscUserfileDatasourceForm" styleId="pageNumber" property="pageNumber"	value="<%=Integer.toString(currentPage)%>" />
    
	<table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right">
		<tr>
			<td class="table-header" width="100%" align="left">
				<bean:message bundle="servermgrResources" key="servermgr.userfile.userfiledatasourcelist"/>
			</td>
			<td align="left" class="blue-text" valign="middle" width="62%" colspan="3">
				&nbsp;
			</td>
			<td align="right" class="blue-text" valign="middle" width="50%"  colspan="4">
			
				<%
				    
					if (end >= totalNumberOfRecord)
						end = totalNumberOfRecord;
					if (totalNumberOfRecord == start)
						start = 0;
					else
						++start;
				%>
				[<%=start%> - <%=end%>] of <%=totalNumberOfRecord%>
			</td>
		</tr>
		<tr>
			<td valign="left" colspan="4">
				<input name="c_btnAdd" type="button" class="light-btn"  onclick="addNewUserAccount()" id="c_btnAdd" value="   Add  ">
				&nbsp;
				<input type="button" name="c_btnDelete" value=" Delete   " class="light-btn" onclick="deleteDatasourceData();" />
			</td>
			<td class="btns-td" align="right" colspan="4">
				<%
					if (totalPage > 1) {
				%>
				<%
					if (previousPage != 0) {
				%>
				<a href="<%=localBasePath%>/listUserfileAccountInformation.do?action=paging&pageNo=1&netserverid=<%=netserverid%>&totalNumberOfRecord=<%=totalNumberOfRecord%>&totalPage=<%=totalPage%>&selectedFileName=<%=selectedFileName%>" ><img src="<%=localBasePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=localBasePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()" alt="First" border="0">
				</a>
				<a href="<%=localBasePath%>/listUserfileAccountInformation.do?action=paging&pageNo=<%=previousPage%>&netserverid=<%=netserverid%>&totalNumberOfRecord=<%=totalNumberOfRecord%>&totalPage=<%=totalPage%>&selectedFileName=<%=selectedFileName%>"  ><img src="<%=localBasePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=localBasePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
				</a>
				<%
					}
				%>
				<%
					if (nextPage != 0 && (previousPage + 1) != totalPage) {
				%>
				<a href="<%=localBasePath%>/listUserfileAccountInformation.do?action=paging&pageNo=<%=nextPage%>&netserverid=<%=netserverid%>&totalNumberOfRecord=<%=totalNumberOfRecord%>&totalPage=<%=totalPage%>&selectedFileName=<%=selectedFileName%>" ><img src="<%=localBasePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=localBasePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
				</a>
				<a	href="<%=localBasePath%>/listUserfileAccountInformation.do?action=paging&pageNo=<%=totalPage%>&netserverid=<%=netserverid%>&totalNumberOfRecord=<%=totalNumberOfRecord%>&totalPage=<%=totalPage%>&selectedFileName=<%=selectedFileName%>" ><img
					src="<%=localBasePath%>/images/last.jpg" name="Image612" onmouseover="MM_swapImage('Image612','','<%=localBasePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
				</a>
				<%
					}
				%>
				<%
					}
				%>
			</td>
		</tr>
		<pg:pager maxIndexPages="10" maxPageItems="<%=recordPerPage%>">
		<tr>
		<td valign="top" align="center" colspan="8">
			<table cellpadding="0" cellspacing="0"  width="100%">
				<tr>
					<td colspan="8">
						<table width="100%" id="listTable" type="tbl-list"
							border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" valign="top" width="5%" class="tblheader">
									<input type="checkbox" name="toggleAll" id="toggleAll" value="checkbox" onclick="check();" />
								</td>
								<td align="center" class="tblheader" valign="top" width="7%">
									<bean:message bundle="servermgrResources"
										key="servermgr.serialnumber" />
								</td>
								<td align="center" class="tblheader" valign="top" width="13%">
									<bean:message bundle="servermgrResources"
										key="servermgr.userfile.accountname" />
								</td>
								<td align="center" class="tblheader" valign="top" width="15%">
									<bean:message bundle="servermgrResources"
										key="servermgr.userfile.attributevalue" />
								</td>
								<td align="center" class="tblheader" valign="top" width="10%">
									<bean:message bundle="servermgrResources"
										key="servermgr.userfile.calledstationid" />
								</td>
								<td align="center" class="tblheader" valign="top" width="10%">
									<bean:message bundle="servermgrResources"
										key="servermgr.userfile.accountstatus" />
								</td>
								<td align="center" class="tblheader" valign="top" width="10%">
									<bean:message bundle="servermgrResources"
										key="servermgr.userfile.encryptiontype" />
								</td>
								<td align="center" class="tblheader" valign="top" width="10%">
									<bean:message bundle="servermgrResources"
										key="servermgr.userfile.passwordcheckenable" />
								</td>
								<td align="center" class="tblheader" valign="top" width="10%">
									<bean:message bundle="servermgrResources"
										key="servermgr.userfile.servicetype" />
								</td>
								<td align="center" valign="top" width="5%" class="tblheader">
									<bean:message  key="general.edit" />
								</td>

							</tr>
							<pg:item>
							
							<%
								if(accountMaps.size()>0){
								
								Set set = accountMaps.keySet();
								Iterator iter = set.iterator();
								String[] keyArray = new String[set.size()];

								int i = 0;
								while (iter.hasNext()) {
									keyArray[i] = (String) iter.next();
									i++;
								}
							%>


							<%
							for (int j = 0; j < keyArray.length; j++) {
							%>
							<%
									Map userAccount = (Map) accountMaps.get(keyArray[j]);
									Map userAttributeInfo = (Map) userAccount.get("AUTH_ATTRIBUTES_MAP");
									Map userAccountInfo = (Map) userAccount.get("ACCOUNT_INFO_MAP");

									Set accountInfoSet = userAccountInfo.keySet();
									Iterator accountInfoIter = accountInfoSet.iterator();
									String[] accountInfoKeyArray = new String[accountInfoSet.size()];
									int k = 0;
									while (accountInfoIter.hasNext()) {
										accountInfoKeyArray[k] = (String) accountInfoIter.next();
										k++;
									}
									
							%>
							
								<tr>
									<td align="center" class="tblleftlinerows" valign="top" width="2%">
										<input type="checkbox" name="select"  value="<%=keyArray[j]%>" />
									</td>
									<td align="center" class="tblrows" >
										<%=(start + j)%>
									</td>
									<td align="left" class="tblrows" >
										<%="Account" + keyArray[j]%>
									</td>
									<%
										String attributeColumn = "-";
										if(userAttributeInfo!=null&& userAttributeInfo.size()>0){
											Set attributeInfoSet = userAttributeInfo.keySet();
											Iterator attributeInfoIter = attributeInfoSet.iterator();
											if(attributeInfoIter.hasNext()){
											 String firstAttr =(String) attributeInfoIter.next();
											 attributeColumn = (String) userAttributeInfo.get(firstAttr);
											}
											if(attributeColumn==null){
												attributeColumn = "-";
											}
											
										}

										String encryptionType = (String)userAccountInfo.get(MBeanConstants.USER_PASSWORD_ENCRYPTION_TYPE);
									    String userPasswordCheckEnabled = (String)userAccountInfo.get(MBeanConstants.USER_PASSWORD_CHECK_ENABLED);
										String accountStatus  = (String) userAccountInfo.get(MBeanConstants.ACCOUNT_STATUS);
										String calledStationId = (String) userAccountInfo.get(MBeanConstants.CALLED_STATION_ID);
										String serviceType = (String) userAccountInfo.get(MBeanConstants.SERVICE_TYPE);
										
										
									 %>
									<td align="left" class="tblrows" >
										<%=attributeColumn%>
									</td>
									<td align="left" class="tblrows" >
									<%
									 if(calledStationId!= null && !calledStationId.trim().equals("")) {
									%>
										<%=calledStationId%>
									<%}else { %>
										<%="-"%>
									<%} %>
									</td>
									<td align="left" class="tblrows" >
									<%
									 if(accountStatus!= null && !accountStatus.trim().equals("")) {
									%>
										<%=accountStatus%>
									<%}else { %>
										<%="-"%>
									<%} %>
									</td>
									<td align="left" class="tblrows" >
									<%
									 if(encryptionType!= null && !encryptionType.trim().equals("")) {
									%>
										<%=encryptionType%>
									<%}else { %>
										<%="-"%>
									<%} %>
									</td>
									<td align="left" class="tblrows" >
									<%
									 if(userPasswordCheckEnabled!= null && !userPasswordCheckEnabled.trim().equals("")) {
									%>
										<%=userPasswordCheckEnabled%>
									<%}else { %>
										<%="-"%>
									<%} %>
									</td>
									<td align="left" class="tblrows" >
									<%
									 if(serviceType!= null && !serviceType.trim().equals("")) {
									%>
										<%=serviceType%>
									<%}else { %>
										<%="-"%>
									<%} %>
									</td>
									<td align="center" class="tblrows" >
									<a href="<%=localBasePath%>/viewUserfileDatasourceUserAccount.do?netserverid=<%=netserverid%>&id=<%=keyArray[j]%>&filename=<bean:write name="listUserfileAccountInformationForm" property="selectedFileName"/> "><img src="<%=localBasePath%>/images/edit.jpg" alt="Edit" border="0"></a>
									</td> 
								</tr>
								
								<% }
								}else { %>
									<tr>
										<td align="center" class="tblleftlinerows" valign="top" width="100%" colspan="11">
											<bean:message bundle="radiusResources"	key="database.datasource.norecordfound" />
										</td>	
									</tr>
								<%} %>
							</pg:item>	
							
							
						</table>
					</td></tr>
				</pg:pager>	
				</table>
				</td>
				</tr>
				<tr>
					<td valign="left" colspan="4">
						<input name="c_btnAdd" type="button" class="light-btn"  onclick="addNewUserAccount()" id="c_btnAdd" value="   Add  ">
						&nbsp;
						<input type="button" name="c_btnDelete" value=" Delete   " class="light-btn" onclick="deleteDatasourceData();" />
					</td>
					<td class="btns-td" align="right" colspan="4">
						<%
							if (totalPage > 1) {
						%>
						<%
							if (previousPage != 0) {
						%>
						<a href="<%=localBasePath%>/listUserfileAccountInformation.do?action=paging&pageNo=1&netserverid=<%=netserverid%>&totalNumberOfRecord=<%=totalNumberOfRecord%>&totalPage=<%=totalPage%>&selectedFileName=<%=selectedFileName%>" ><img src="<%=localBasePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=localBasePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()" alt="First" border="0">
						</a>
						<a href="<%=localBasePath%>/listUserfileAccountInformation.do?action=paging&pageNo=<%=previousPage%>&netserverid=<%=netserverid%>&totalNumberOfRecord=<%=totalNumberOfRecord%>&totalPage=<%=totalPage%>&selectedFileName=<%=selectedFileName%>"  ><img src="<%=localBasePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=localBasePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
						</a>
						<%
							}
						%>
						<%
							if (nextPage != 0 && (previousPage + 1) != totalPage) {
						%>
						<a href="<%=localBasePath%>/listUserfileAccountInformation.do?action=paging&pageNo=<%=nextPage%>&netserverid=<%=netserverid%>&totalNumberOfRecord=<%=totalNumberOfRecord%>&totalPage=<%=totalPage%>&selectedFileName=<%=selectedFileName%>" ><img src="<%=localBasePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=localBasePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
						</a>
						<a	href="<%=localBasePath%>/listUserfileAccountInformation.do?action=paging&pageNo=<%=totalPage%>&netserverid=<%=netserverid%>&totalNumberOfRecord=<%=totalNumberOfRecord%>&totalPage=<%=totalPage%>&selectedFileName=<%=selectedFileName%>" ><img
							src="<%=localBasePath%>/images/last.jpg" name="Image612" onmouseover="MM_swapImage('Image612','','<%=localBasePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
						</a>
						<%
							}
						%>
						<%
							}
						%>
					</td>
				</tr>
			
	</table>
</html:form>
