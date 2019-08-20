<%@ page import="com.elitecore.elitesm.web.rm.ippool.forms.IPPoolForm"%>
<%                                         														   
     IPPoolForm ipPoolForm = (IPPoolForm)request.getAttribute("ipPoolForm");
    
     long pageNo = ipPoolForm.getPageNumber();
     long totalPages = ipPoolForm.getTotalPages();
     long totalRecord = ipPoolForm.getTotalRecords();
	 Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	 long  srNo   = (pageNo == 1)? 0 : ((pageNo-1)*pageSize);
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     String strPageNumber = String.valueOf(pageNo); 
%>
<script>

function navigate(direction, pageNumber ){
	document.forms[0].pageNumber.value = pageNumber;
	document.forms[0].submit();
}
function validateSearch(){
	document.forms[0].pageNumber.value = "1";
	document.forms[0].action.value = 'search';
	document.forms[0].submit();
}
function removeRecords(){
	
	var selectVar = false;
	
	for (i=0; i < document.forms[0].elements.length; i++){
		if(document.forms[0].elements[i].name.substr(0,6) == 'select'){
			if(document.forms[0].elements[i].checked == true)
				selectVar = true;
		}
	}

	if(selectVar == false){
		alert('At least select one IPAddress for remove process');
	}else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searchipaddressjsp.delete.query"/>';        
        //msg = "All the selected IP Addresses would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
            document.forms[0].action.value = 'delete';
    		document.forms[0].submit();
        }
    }     
}
function  checkAll(){
	 	if( document.forms[0].toggleAll.checked == true) {
		 	for (i = 0; i < document.forms[0].select.length;i++)
				document.forms[0].select[i].checked = true ;
	    } else if (document.forms[0].toggleAll.checked == false){
			for (i = 0; i < document.forms[0].select.length; i++)
				document.forms[0].select[i].checked = false ;
		}
}
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0"  >
    <tr> 
      <td valign="top" align="right" height="15%" >
      	<html:form action="/searchIPAddress"> 
      	<html:hidden name="ipPoolForm" property="ipPoolId" />
      	<html:hidden name="ipPoolForm" styleId="action" property="action" value="search" />
      	<html:hidden name="ipPoolForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNumber%>" />
		<html:hidden name="ipPoolForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
		<html:hidden name="ipPoolForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
											
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr>
          	<td colspan="2" >
          		<table width="100%">
          			<tr>
          				<td align="left" class="tblheader-bold" valign="top" colspan="8">
          					<bean:message bundle="ippoolResources" key="ippool.searchipaddress" />
          				</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="20%">
							<bean:message bundle="ippoolResources" key="ippool.ipaddress" /> 
							<ec:elitehelp headerBundle="ippoolResources" text="ippool.ipaddress" header="ippool.ipaddress" />
						</td>
						<td align="left" class="labeltext" valign="top" width="80%">
							<html:text styleId="ipAddress" tabindex="1" property="ipAddress" size="20" maxlength="15" style="width:250px" />
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td align="left">
							<input type="button" tabindex="2" name="c_btnSearch" onclick="validateSearch()" id="c_btnSearch" value="   Search   " class="light-btn"> 
							<input type="button" tabindex="3" name="c_btnCancel" onclick="javascript:location.href='<%=basePath%>/viewIPPool.do?ipPoolId=<bean:write name="ipPoolForm" property="ipPoolId"/>'" id="c_btnCancel" value="Cancel" class="light-btn"></td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
					</tr>
          		</table>
          		<logic:notEmpty name="ipPoolForm" property="action">
          			<tr>
		          	<td colspan="2" >
		          		<table width="100%">
							
							<tr>
								<td class="table-header" width="50%">
									<bean:message bundle="ippoolResources" key="ippool.ippooldetails" />
								</td>

								<td align="right" class="blue-text" valign="middle" width="50%">
									<%if(totalRecord == 0){ 
									  }else if(pageNo == totalPages+1) { %>
									  	[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
									<% } else if(pageNo == 1) { %> 
										[<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>] of <%= totalRecord %> 
									<% } else { %> 
										[<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>] of <%= totalRecord %> 
									<% } %>
								</td>
							</tr>	
							
							<tr>
								<td class="btns-td" valign="middle">
									<input type="button" name="Delete" Onclick="removeRecords()" value="   Delete   " class="light-btn">
								</td>
								<td class="btns-td" align="right" colspan="2">
									<% if(totalPages >= 1) { %> 
										<% if(pageNo == 1){ %> 
											<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0" />
											<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0"/> 
										<% } %> 
										<% if(pageNo>1 && pageNo!=totalPages+1) {%> 
											<%  if(pageNo-1 == 1){ %>
											<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
											<img src="<%=basePath%>/images/previous.jpg" onclick="navigate('next',<%= pageNo-1%>)" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
											<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
											<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
											<% } else if(pageNo == totalPages){ %> 
											<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
											<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%= pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
											<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
											<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
											<% } else { %> 
											<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
											<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%=pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
											<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
											<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
											<% } %> 
										<% } %> 
										<% if(pageNo == totalPages+1) { %> 
											<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%=1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
											<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%=pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
										<% } %> 
									<% } %>
								</td>
							</tr>
		          		</table>
		          	</td>	
		          </tr>
				          <tr>
							<td width="100%">
							<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listTable">
							<tr>
								<td align="center" class="tblheader" valign="top" width="5%">
									<input type="checkbox" tabindex="4" name="toggleAll"  onclick="checkAll()" />
								</td>
								<td align="center" class="tblheader" valign="top" width="5%">
									<bean:message bundle="ippoolResources" key="ippool.serialnumber" />
								</td>
								<td align="left" class="tblheader" valign="top" width="10%">
									<bean:message bundle="ippoolResources" key="ippool.ipaddress" />
								</td>
								<td align="left" class="tblheader" valign="top" width="7%">
									<bean:message bundle="ippoolResources" key="ippool.assigned" />
								</td>
								<td align="left" class="tblheader" valign="top" width="7%">
									<bean:message bundle="ippoolResources" key="ippool.reserved" />
								</td>
								<td align="left" class="tblheader" valign="top" width="10%">
									<bean:message bundle="ippoolResources" key="ippool.nas.ipaddress" />
								</td>
								<td align="left" class="tblheader" valign="top" width="20%">
									<bean:message bundle="ippoolResources" key="ippool.callingstationid" />
								</td>
								<td align="left" class="tblheader" valign="top" width="20%">
									<bean:message bundle="ippoolResources" key="ippool.useridentity" />
								</td>
								<td align="left" class="tblheader" valign="top" width="15%">
									<bean:message bundle="ippoolResources" key="ippool.lastupdate" />
								</td>
							</tr>
							<bean:define id="lstIPPoolDetail" name="lstIPPoolDetail" scope="request"/>
					        <logic:notEmpty name="lstIPPoolDetail">
					          	<logic:iterate id="ipPool" name="lstIPPoolDetail" type="com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolDetailData" >
					          		<tr>
					          			<td align="center" class="tblfirstcol"><input type="checkbox" name="select" value="<bean:write name="ipPool" property="ipAddress"/>" />
					          			<td align="center" class="tblfirstcol"><%=++srNo %>&nbsp;</td>
					          			<td align="left" class="tblrows"><bean:write name="ipPool" property="ipAddress" />&nbsp;</td>
					          			<td align="left" class="tblrows"><bean:write name="ipPool" property="assigned" />&nbsp;</td>
					          			<td align="left" class="tblrows"><bean:write name="ipPool" property="reserved" />&nbsp;</td>
					          			<td align="left" class="tblrows"><bean:write name="ipPool" property="nasIPAddress" />&nbsp;</td>
					          			
					          			<% 
					          				if(ipPool.getCallingStationId() == null || (ipPool.getCallingStationId() != null && ipPool.getCallingStationId().length() < 25) ) {
					          			%>
					          				<td align="left" class="tblrows">
					          					<bean:write name="ipPool" property="callingStationId" /> 
					          			<%		
					          				} else {
					          			%>
					          				<td align="left" class="tblrows" title="<%=ipPool.getCallingStationId()%>">
					          			<%
					          					out.write(ipPool.getCallingStationId().substring(0, 25) + "..");
					          				}
							          	%>
							          	&nbsp;
					          			</td>
					          			
					          			<% 
					          				if(ipPool.getUserIdentity() == null || (ipPool.getUserIdentity() != null && ipPool.getUserIdentity().length() < 25) ) {
					          			%>
					          				<td align="left" class="tblrows">
					          					<bean:write name="ipPool" property="userIdentity" /> 
					          			<%		
					          				} else {
					          			%>
					          				<td align="left" class="tblrows" title="<%=ipPool.getUserIdentity()%>">
					          			<%
					          					out.write(ipPool.getUserIdentity().substring(0, 25) + "..");
					          				}
							          	%>
							          	&nbsp;
					          			</td>
					          			<td align="left" class="tblrows"><%=EliteUtility.dateToString(ipPool.getLastUpdatedTime(), ConfigManager.get(ConfigConstant.DATE_FORMAT))%>&nbsp;</td>
					          		</tr>
					          	</logic:iterate>
					          </logic:notEmpty>
					          <logic:empty name="lstIPPoolDetail">
					          	<tr>
					          		<td colspan="8" class="tblfirstcol" align="center">No Record Found</td>
					          	</tr>
					          </logic:empty>
						</table>	
					</td>          
		          </tr>
		          <tr>
		          	<td colspan="2" height="20%">
		          		<table width="100%">
		          			<tr>
		          				<td class="btns-td" valign="middle">
									<input type="button" name="Delete" Onclick="removeRecords()" value="   Delete   " class="light-btn">
								</td>
								<td class="btns-td" align="right" colspan="2">
									<% if(totalPages >= 1) { %> 
										<% if(pageNo == 1){ %> 
											<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0" />
											<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0"/> 
										<% } %> 
										<% if(pageNo>1 && pageNo!=totalPages+1) {%> 
											<%  if(pageNo-1 == 1){ %>
											<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
											<img src="<%=basePath%>/images/previous.jpg" onclick="navigate('next',<%= pageNo-1%>)" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
											<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
											<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
											<% } else if(pageNo == totalPages){ %> 
											<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
											<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%= pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
											<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
											<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
											<% } else { %> 
											<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
											<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%=pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
											<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
											<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
											<% } %> 
										<% } %> 
										<% if(pageNo == totalPages+1) { %> 
											<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%=1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
											<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%=pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
										<% } %> 
									<% } %>
								</td>
							</tr>
		          		</table>
		          	</td>
		          </tr>	
          		</logic:notEmpty>
        </html:form>
       </td>
   </tr>
</table>


<%-- <%@ page import="java.util.*"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>





<% 
	String localBasePath = request.getContextPath();
%>

<script>
function validateSearch(){
//	alert('here the search button is pressed');
	document.forms[0].action.value = 'search';
	document.forms[0].submit();
}
function remove(){
	
	var selectVar = false;
	
	for (i=0; i < document.forms[0].elements.length; i++){
		if(document.forms[0].elements[i].name.substr(0,6) == 'select'){
			if(document.forms[0].elements[i].checked == true)
				selectVar = true;
		}
	}

	if(selectVar == false){
		alert('At least select one IPAddress for remove process');
	}else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searchipaddressjsp.delete.query"/>';        
        //msg = "All the selected IP Addresses would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
            document.forms[0].action.value = 'delete';
    		document.forms[0].submit();
        }
    }     
}
function  checkAll(){
	 	if( document.forms[0].toggleAll.checked == true) {
		 	for (i = 0; i < document.forms[0].select.length;i++)
				document.forms[0].select[i].checked = true ;
	    } else if (document.forms[0].toggleAll.checked == false){
			for (i = 0; i < document.forms[0].select.length; i++)
				document.forms[0].select[i].checked = false ;
		}
}
</script>
<%
	int count = 1;
	double recordsPerColumn = 0;
	double totalRecord = 0;
	List listIPPoolDetail = (List)request.getAttribute("listIPPoolDetail");
	
%>





<html:form action="/searchIPAddress">
	<html:hidden name="searchIPAddressForm" styleId="action" property="action" value="search" />
	<html:hidden name="searchIPAddressForm" styleId="ipPoolId" property="ipPoolId" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%">




		<bean:define id="IPPoolBean" name="ipPoolData" scope="request"
			type="com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolData" />
		<tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="8"><bean:message
					bundle="ippoolResources" key="ippool.searchipaddress" /></td>
		</tr>



		<tr>
			<td align="left" class="captiontext" valign="top" width="20%"><bean:message
					bundle="ippoolResources" key="ippool.ipaddress" /> <img
				src="<%=basePath%>/images/help-hover.jpg" height="12" width="12"
				style="cursor: pointer"
				onclick="parameterDescription('<bean:message bundle="descriptionResources" key="ippool.ipaddress"/>','<bean:message bundle="ippoolResources" key="ippool.ipaddress"/>')" />

			</td>
			<td align="left" class="labeltext" valign="top" width="80%"><html:text
					styleId="ipAddress" tabindex="1" property="ipAddress" size="20"
					maxlength="15" style="width:250px" /></td>
		</tr>
		<tr>
			<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td align="left"><input type="button" tabindex="2"
				name="c_btnSearch" onclick="validateSearch()" id="c_btnSearch"
				value="   Search   " class="light-btn"> <input type="button"
				tabindex="3" name="c_btnCancel"
				onclick="javascript:location.href='<%=localBasePath%>/viewIPPool.do?ippoolid=<bean:write name="IPPoolBean" property="ipPoolId"/>'"
				id="c_btnCancel" value="Cancel" class="light-btn"></td>
		</tr>
		<tr>
			<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
		</tr>

		<tr>
			<td align="left" colspan="8">
				<% if(listIPPoolDetail != null){%>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td colspan="8">&nbsp;</td>
					</tr>

					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="8"><bean:message
								bundle="ippoolResources" key="ippool.listipaddresses" /></td>
					</tr>
					<tr>
						<td class="small-gap" colspan="9">&nbsp;</td>
					</tr>

					<tr>
						<td colspan="8" align="left">
							<table width="100%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="left" class="tblheader" valign="top" width="3%">
										<input type="checkbox" tabindex="4" name="toggleAll"
										value="checkbox" onclick="checkAll()" />
									</td>
									<td align="left" class="tblheader" valign="top" width="6%"><bean:message
											bundle="ippoolResources" key="ippool.serialnumber" /></td>
									<td align="left" class="tblheader" valign="top" width="20%"><bean:message
											bundle="ippoolResources" key="ippool.ipaddress" /></td>
									<td align="left" class="tblheader" valign="top" width="10%"><bean:message
											bundle="ippoolResources" key="ippool.assigned" /></td>
									<td align="left" class="tblheader" valign="top" width="10%"><bean:message
											bundle="ippoolResources" key="ippool.reserved" /></td>
									<td align="left" class="tblheader" valign="top" width="20%"><bean:message
											bundle="ippoolResources" key="ippool.nas.ipaddress" /></td>
									<td align="left" class="tblheader" valign="top" width="20%"><bean:message
											bundle="ippoolResources" key="ippool.useridentity" /></td>
									<td align="left" class="tblheader" valign="top" width="20%"><bean:message
											bundle="ippoolResources" key="ippool.lastupdate" /></td>
								</tr>
								<%if(listIPPoolDetail.size() > 0){%>
								<logic:iterate id="ipPoolDetailBean" name="listIPPoolDetail"
									scope="request"
									type="com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolDetailData">
									<tr>
										<td align="left" class="tblfirstcol"><input
											type="checkbox" name="select"
											value="<bean:write name="ipPoolDetailBean" property="ipAddress"/>" />
										<td align="left" class="tblrows"><%=count%></td>
										<td align="left" class="tblrows"><bean:write
												name="ipPoolDetailBean" property="ipAddress" />&nbsp;</td>
										<td align="left" class="tblrows"><bean:write
												name="ipPoolDetailBean" property="assigned" />&nbsp;</td>
										<td align="left" class="tblrows"><bean:write
												name="ipPoolDetailBean" property="reserved" />&nbsp;</td>
										<td align="left" class="tblrows"><bean:write
												name="ipPoolDetailBean" property="nasIPAddress" />&nbsp;</td>
										<td align="left" class="tblrows"><bean:write
												name="ipPoolDetailBean" property="userIdentity" />&nbsp;</td>
										<td align="left" class="tblrows"><%=EliteUtility.dateToString(ipPoolDetailBean.getLastUpdatedTime(), ConfigManager.get(ConfigConstant.DATE_FORMAT))%>&nbsp;</td>

									</tr>
									<% count=count+1; %>
								</logic:iterate>
								<%}else{%>
								<tr>
									<td align="center" class="tblfirstcol" colspan="8"><B><bean:message
												bundle="ippoolResources" key="ippool.norecordsfound" /></B></td>
								</tr>
								<%}%>
							</table>
						</td>
					</tr>
					<tr>
						<td class="small-gap" colspan="9">&nbsp;</td>
					</tr>
					<tr>
						<td class="labeltext" colspan="8" valign="top"><input
							type="button" tabindex="5" name="c_btnRemove" onclick="remove()"
							value="   Remove   " class="light-btn"></td>
					</tr>
					<tr>
						<td class="small-gap" colspan="8">&nbsp;</td>
					</tr>
				</table> <%} %>
			</td>
		</tr>
	</table>


</html:form> --%>