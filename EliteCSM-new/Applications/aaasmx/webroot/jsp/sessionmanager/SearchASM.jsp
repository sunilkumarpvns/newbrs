<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@page
	import="com.elitecore.elitesm.web.sessionmanager.forms.SearchASMForm"%>
<%@page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="java.text.*,java.util.*"%>






<%                                        														   
     SearchASMForm searchASMForm = (SearchASMForm)request.getAttribute("searchASMForm");
%>
<script>
	function  validateSearch(){
	document.forms[0].pageNumber.value = 0;
	document.forms[0].submit();
	}
	
function  validateCloseSession(actionMode){
	
	
	document.forms[1].action.value = actionMode;
	
	if(actionMode == 'download'){
	
		document.forms[1].submit();
	
	}
	else{

	if(document.forms[1].selected[0]){
	
	var b = true;
	for (i=0; i<document.forms[1].selected.length; i++){

	 		 if (document.forms[1].selected[i].checked == false)  			
	 		 	b=false;
	 		 
	 		 else{
	 		 	
				b=true;
				break;
			}
		}
	if(b==false)
		alert("Selection Required To Perform Close Session Operation.")
	else
		
		document.forms[1].submit();
		
	
}
else{
	if(document.forms[1].selected.checked==true)
		document.forms[1].submit();
		
	else
		alert("Selection Required To Perform Close Session Operation.")	
}	
}	
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
	
	function popup(mylink, windowname)
			{
				if (! window.focus)return true;
					var href;
				if (typeof(mylink) == 'string')
   					href=mylink;
				else
   					href=mylink.href;
   					
   				//alert(mylink)
				window.open(href, windowname, 'width=700,height=500,left=150,top=100,scrollbars=yes');
				
				return false;
	}  
	
	function purge_confirm()
	{
		
		var r=confirm("This Operation Will Delete All Closed Sessions From Database. Do you Want To Continue?");
		if (r==true)
  		{
  			javascript:location.href='<%=basePath%>/purgeClosedSession.do?sminstanceid=<%=searchASMForm.getSessionManagerId()%>&strUserName=<bean:write name="searchASMForm" property="userName"/>&nasIp=<bean:write name="searchASMForm" property="nasIpAddress"/>&framedIp=<bean:write name="searchASMForm" property="framedIpAddress"/>&groupName=<bean:write name="searchASMForm" property="groupName"/>&idleTime=<bean:write name="searchASMForm" property="idleTime"/>&pageNumber=<bean:write name="searchASMForm" property="pageNumber"/>&groupByCriteria=<bean:write name="searchASMForm" property="groupbyCriteria"/>&action=purgeClosedSession';
  		}
		
	}
	 
	function purgeAll_confirm()
	{
		var r=confirm("This Operation Will Delete All Sessions From Database. Do you Want To Continue?");
		if (r==true)
  		{
  			javascript:location.href='<%=basePath%>/purgeAllSession.do?sminstanceid=<%=searchASMForm.getSessionManagerId()%>&strUserName=<bean:write name="searchASMForm" property="userName"/>&nasIp=<bean:write name="searchASMForm" property="nasIpAddress"/>&framedIp=<bean:write name="searchASMForm" property="framedIpAddress"/>&groupName=<bean:write name="searchASMForm" property="groupName"/>&idleTime=<bean:write name="searchASMForm" property="idleTime"/>&pageNumber=<bean:write name="searchASMForm" property="pageNumber"/>&groupByCriteria=<bean:write name="searchASMForm" property="groupbyCriteria"/>&action=purgeAllSession';
  		}
		
	} 
	function  checkAll(){
	var arrayCheck = document.getElementsByName('selected');
	 	if( document.forms[1].toggleAll.checked == true) {
		 	for (i = 0; i < arrayCheck.length;i++)
				arrayCheck[i].checked = true ;
	    } else if (document.forms[1].toggleAll.checked == false){
			for (i = 0; i < arrayCheck.length; i++)
				arrayCheck[i].checked = false ;
		}
}
function popupViewDetails(windowname){

	var b = false;

	if(document.forms[1].selected[0]){
		for(i=0;i<document.forms[1].selected.length;i++){
			if(document.forms[1].selected[i].checked == true){
				b=true;
				break;
			}
		}

  }else{
	if(document.forms[1].selected.checked==true)
		b=true;
  }
  
  if(b==false)
	  alert("Selection is required to view details of the session");
  else{
	  var arrayCheck = document.getElementsByName('selected');
	  var url =  "<%=basePath%>/viewASMDetailsPopUp.do?sminstanceid=<%=searchASMForm.getSessionManagerId()%>"+"&selected=";
	  for (i = 0; i < arrayCheck.length;i++){
		if(arrayCheck[i].checked == true){
			url = url+arrayCheck[i].value +",";
		}
	  }
      popup(url, windowname);
   }
}


function openPopup(id){
	var divnm = 'fieldMappingDiv' + id;	
	document.getElementById(divnm).style.visibility = "visible";
	$("#"+divnm).css({height:"260px", overflow:"auto"});
	$( "#"+divnm ).dialog({
		width: 500,
		height: 260, 
		resizable: true,
		open: function () {
            
        }					       
    });			
	$("#"+divnm).css({height:"260px", overflow:"auto"});
}
</script>

<style>
	.ui-dialog .ui-dialog-content {
		padding: 0 !important;
	}
</style>

<%                                        														   
    
     
	 List lstASM = searchASMForm.getAsmList();
     List lstASMGroupBy = searchASMForm.getAsmListGroupBy();
     String groupByCrit = searchASMForm.getGroupbyCriteria();          
     
     long pageNo = searchASMForm.getPageNumber();
     long totalPages = searchASMForm.getTotalPages();
     long totalRecord = searchASMForm.getTotalRecords();
     
	 int count=1;
	 Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     String strPageNumber = String.valueOf(pageNo);

	 String actionCheck =  searchASMForm.getAction();
	 long iIndex = ((pageNo-1)*pageSize);
	 
%>




<table cellSpacing="0" cellPadding="0" width="100%" border="0">

	<tr>
		<td class="table-header" colspan="5"><bean:message
				bundle="sessionmanagerResources" key="sessionmanager.asm.search" /></td>
	</tr>
	<tr>
		<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
	</tr>

	<tr>
		<td colspan="3">
			<table width="100%" align="right" border="0">
				<html:form action="/searchASM.do">
					<html:hidden name="searchASMForm" styleId="pageNumber"
						property="pageNumber" value="<%=strPageNumber%>" />
					<html:hidden name="searchASMForm" styleId="sessionManagerId"
						property="sessionManagerId"
						value="<%=searchASMForm.getSessionManagerId().toString()%>" />
					<html:hidden name="searchASMForm" styleId="totalPages"
						property="totalPages" value="<%=strTotalPages%>" />
					<html:hidden name="searchASMForm" styleId="action"
						property="action" value="search" />
					<tr>
						<td align="left" class="captiontext" valign="top" width="10%">

							<bean:message bundle="sessionmanagerResources"
								key="sessionmanager.asm.username" /> 
									<ec:elitehelp headerBundle="sessionmanagerResources" 
										text="sessionmanager.asm.username" 
											header="sessionmanager.asm.username" />
						</td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<html:text styleId="userName" property="userName" size="30"
								maxlength="30" tabindex="1" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="sessionmanagerResources"
								key="sessionmanager.asm.nasipaddress" /> 
									<ec:elitehelp headerBundle="sessionmanagerResources" 
										text="sessionmanager.asm.nasipaddress" 
											header="sessionmanager.asm.nasipaddress" /> 
						</td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<html:text styleId="nasIpAddress" property="nasIpAddress"
								size="30" maxlength="30" tabindex="2" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="sessionmanagerResources"
								key="sessionmanager.asm.framedipaddress" /> 
									<ec:elitehelp headerBundle="sessionmanagerResources" 
										text="sessionmanager.asm.framedipaddress" 
											header="sessionmanager.asm.framedipaddress" />
						</td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<html:text styleId="framedIpAddress" property="framedIpAddress"
								size="30" maxlength="30" tabindex="3" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="sessionmanagerResources"
								key="sessionmanager.asm.groupname" /> 
									<ec:elitehelp headerBundle="sessionmanagerResources" 
										text="sessionmanager.asm.groupname" 
											header="sessionmanager.asm.groupname" />
						</td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<html:text styleId="groupName" property="groupName" size="30"
								maxlength="30" tabindex="4" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="sessionmanagerResources"
								key="sessionmanager.asm.idletime" /> 
									<ec:elitehelp headerBundle="sessionmanagerResources" 
										text="sessionmanager.asm.idletime" 
											header="sessionmanager.asm.idletime" />
						</td>
						<td align="left" class="labeltext" valign="top" width="16%">
							<html:text styleId="idleTime" property="idleTime" size="10"
								maxlength="10" tabindex="5" /> (In Mins.)
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="sessionmanagerResources"
								key="sessionmanager.asm.groupby" /> 
									<ec:elitehelp headerBundle="sessionmanagerResources"
										text="sessionmanager.asm.groupby"
											header="sessionmanager.asm.groupby" />
						</td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<html:select styleId="groupbyCriteria" property="groupbyCriteria"
								tabindex="6">
								<html:option value="">Select</html:option>
								<html:option value="USER_NAME">
									<bean:message bundle="sessionmanagerResources"
										key="sessionmanager.asm.username" />
								</html:option>
								<html:option value="GROUPNAME">
									<bean:message bundle="sessionmanagerResources"
										key="sessionmanager.asm.groupname" />
								</html:option>
							</html:select>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td align="left" class="labeltext" valign="top" width="5%"><input
							type="button" name="Search" width="5%" name="ASMName"
							Onclick="validateSearch()" value="   Search   " class="light-btn"
							tabindex="7" /> <input type="button" name="Reset"
							onclick="reset();" value="   Reset    " class="light-btn"
							tabindex="8"></td>
					</tr>
				</html:form>

			</table>
		</td>
	</tr>
	<%
  if(actionCheck!=null){
%>


	<html:form action="/closeSession.do">
		<html:hidden name="searchASMForm" styleId="action" property="action"
			value="closeSelected" />
		<html:hidden name="searchASMForm" styleId="userName"
			property="userName" />
		<html:hidden name="searchASMForm" styleId="sessionManagerId"
			property="sessionManagerId"
			value="<%=searchASMForm.getSessionManagerId().toString()%>" />
		<html:hidden name="searchASMForm" styleId="nasIpAddress"
			property="nasIpAddress" />
		<html:hidden name="searchASMForm" styleId="groupName"
			property="groupName" />
		<html:hidden name="searchASMForm" styleId="idleTime"
			property="idleTime" />
		<html:hidden name="searchASMForm" styleId="pageNumber"
			property="pageNumber" value="<%=strPageNumber%>" />
		<html:hidden name="searchASMForm" styleId="totalPages"
			property="totalPages" value="<%=strTotalPages%>" />
		<tr>
			<td>
				<table cellSpacing="0" cellPadding="0" width="100%" border="0">

					<tr>
						<td class="table-header" valign="bottom" colspan="3" align="left">
							<bean:message bundle="sessionmanagerResources"
								key="sessionmanager.asm.list" /> 
									<ec:elitehelp headerBundle="sessionmanagerResources" 
										text="sessionmanager.asm.list" 
											header="sessionmanager.asm.list" /> 
						</td>
						<td align="right" class="blue-text" valign="middle" width="14%"
							colspan="4">
							<% if(totalRecord == 0){ %> <% }else if(pageNo == totalPages+1) { %>
							[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
							<% } else if(pageNo == 1) { %> [<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>]
							of <%= totalRecord %> <% } else { %> [<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>]
							of <%= totalRecord %> <% } %>
						</td>

					</tr>

					<tr>
						<td class="btns-td" align="right" colspan="8">
							<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <img
							src="<%=basePath%>/images/next.jpg" name="Image61"
							onclick="navigate('next',<%=pageNo+1%>)"
							onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Next" border="0" /> <img
							src="<%=basePath%>/images/last.jpg" name="Image612"
							onclick="navigate('last',<%=totalPages+1%>)"
							onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Last" border="0"> <% } %>
							<% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
							<img src="<%=basePath%>/images/first.jpg" name="Image511"
							onclick="navigate('first',<%= 1%>)"
							onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="First" border="0"> <img
							src="<%=basePath%>/images/previous.jpg"
							onclick="navigate('next',<%= pageNo-1%>)" name="Image5"
							onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
							onclick="navigate('next',<%= pageNo+1%>)"
							onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Next" border="0"> <img
							src="<%=basePath%>/images/last.jpg" name="Image612"
							onclick="navigate('last',<%= totalPages+1%>)"
							onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Last" border="0"> <% } else if(pageNo == totalPages){ %>
							<img src="<%=basePath%>/images/first.jpg" name="Image511"
							onclick="navigate('previous',<%= 1%>)"
							onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="First" border="0"> <img
							src="<%=basePath%>/images/previous.jpg" name="Image5"
							onclick="navigate('previous',<%= pageNo-1%>)"
							onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
							onclick="navigate('next',<%= pageNo+1%>)"
							onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Next" border="0"> <img
							src="<%=basePath%>/images/last.jpg" name="Image612"
							onclick="navigate('last',<%= totalPages+1%>)"
							onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Last" border="0"> <% } else { %>
							<img src="<%=basePath%>/images/first.jpg" name="Image511"
							onclick="navigate('previous',<%= 1%>)"
							onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="First" border="0"> <img
							src="<%=basePath%>/images/previous.jpg" name="Image5"
							onclick="navigate('previous',<%=pageNo-1%>)"
							onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
							onclick="navigate('next',<%=pageNo+1%>)"
							onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Next" border="0"> <img
							src="<%=basePath%>/images/last.jpg" name="Image612"
							onclick="navigate('last',<%=totalPages+1%>)"
							onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Last" border="0"> <% } %>
							<% } %> <% if(pageNo == totalPages+1) { %> <img
							src="<%=basePath%>/images/first.jpg" name="Image511"
							onclick="navigate('first',<%=1%>)"
							onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="First" border="0"> <img
							src="<%=basePath%>/images/previous.jpg" name="Image5"
							onclick="navigate('previous',<%=pageNo-1%>)"
							onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
							<% } %> <% } %>
						</td>
					</tr>
					<tr>
						<td colspan="7">
							<table cellSpacing="0" cellPadding="0" width="97%" border="0"
								align="right">
								<tr height="2">
									<td></td>
								</tr>
								<%
							if(groupByCrit.equalsIgnoreCase("")){
						%>
								<%
	  						if(lstASM!=null && lstASM.size()>0){
							%>

								<tr>

									<td valign="middle" colspan="8"><input type="button"
										name="closeSession"
										onclick="validateCloseSession('closeSelected')"
										value=" Close Selected Session " class="light-btn" /> <input
										type="button" name="closeAllSession"
										value=" Close All Result Session " class="light-btn"
										onclick="javascript:location.href='<%=basePath%>/closeSession.do?sminstanceid=<bean:write name="searchASMForm" property="sessionManagerId"/>&strUserName=<bean:write name="searchASMForm" property="userName"/>&nasIp=<bean:write name="searchASMForm" property="nasIpAddress"/>&groupName=<bean:write name="searchASMForm" property="groupName"/>&idleTime=<bean:write name="searchASMForm" property="idleTime"/>&groupByCriteria=<bean:write name="searchASMForm" property="groupbyCriteria"/>'" />
										<input type="button" name="purge" onclick="purge_confirm()"
										value=" Purge " class="light-btn" /> <input type="button"
										name="purgeAll" onclick="purgeAll_confirm()"
										value=" Purge All " class="light-btn" /> <input type="button"
										name="download" value=" Download " class="light-btn"
										onclick="validateCloseSession('download')" /></td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>

								<tr>
									<td align="center" class="tblheader" valign="top" width="2%">
										<input type="checkbox" name="toggleAll" value="checkbox"
										onclick="checkAll()" />
									</td>
									<td align="left" class="tblheader" valign="top" width="2%"><bean:message
											key="general.serialnumber" /></td>
									<td align="left" class="tblheader" valign="top" width="10%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.username" /></td>
									<td align="left" class="tblheader" valign="top" width="12%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.groupname" /></td>
									<td align="left" class="tblheader" valign="top" width="17%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.lastupdatedtime" /></td>
									<td align="left" class="tblheader" valign="top" width="16%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.starttime" /></td>
									<td align="left" class="tblheader" valign="top" width="16%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.acctsessionid" /></td>
									<td align="left" class="tblheader" valign="top" width="16%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.framedipaddress" /></td>
									<td align="left" class="tblheader" valign="top" width="16%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.protocoltype" /></td>
								</tr>
								<tr>
									<logic:iterate id="asmBean" name="searchASMForm"
										property="asmList"
										type="com.elitecore.elitesm.datamanager.sessionmanager.data.IASMData">

										<tr>
											<td align="left" class="tblfirstcol" valign="top"><input
												type="checkbox" name="selected" id="<%=(iIndex+1) %>"
												value="<bean:write name="asmBean" property="concUserId"/>">
											</td>
											<td align="left" class="tblrows" valign="top"><%=(iIndex+1) %></td>
											<td align="left" class="tblrows"><a
												style="cursor: pointer;"
												onclick="openPopup('<%=asmBean.getConcUserId()%>')"><bean:write
														name="asmBean" property="userName" /></a>&nbsp;</td>
											<td align="left" class="tblrows"><bean:write
													name="asmBean" property="groupName" />&nbsp;</td>
											<td align="left" class="tblrows"><%=EliteUtility.dateToString(asmBean.getLastUpdatedTime(), ConfigManager.get(ConfigConstant.DATE_FORMAT))%>
												&nbsp;</td>
											<td align="left" class="tblrows"><bean:write
													name="asmBean" property="startTime" /></a>&nbsp;</td>
											<td align="left" class="tblrows"><bean:write
													name="asmBean" property="acctSessionId" /></a>&nbsp;</td>

											<td align="left" class="tblrows"><bean:write
													name="asmBean" property="framedIpAddress" /></a>&nbsp; <%					   										   				
					   					String divName = "fieldMappingDiv" + asmBean.getConcUserId(); 
					   				%>


												<div id=<%=divName %> style="display: none;" title="Session Information">
													<table cellSpacing="0" cellPadding="0" width="100%" border="0" class="box">
														<tr>
															<td class="tblfirstcol" width="40%">UserName</td>
															<td class="tblrows" width="60%"><bean:write
																	name="asmBean" property="userName" />&nbsp;</td>
														</tr>
														<tr>
															<td class="tblfirstcol" width="40%">GroupName</td>
															<td class="tblrows" width="60%"><bean:write
																	name="asmBean" property="groupName" />&nbsp;</td>
														</tr>
														<tr>
															<td class="tblfirstcol" width="40%">Start-Time</td>
															<td class="tblrows" width="60%"><bean:write
																	name="asmBean" property="startTime" />&nbsp;</td>
														</tr>
														<tr>
															<td class="tblfirstcol" width="40%">Acct-Session-Id</td>
															<td class="tblrows" width="60%"><bean:write
																	name="asmBean" property="acctSessionId" />&nbsp;</td>
														</tr>
														<tr>
															<td class="tblfirstcol" width="40%">Framed-Ip-Address</td>
															<td class="tblrows" width="60%"><bean:write
																	name="asmBean" property="framedIpAddress" />&nbsp;</td>
														</tr>
														<tr>
															<td class="tblfirstcol" width="40%">Protocol Type</td>
															<td class="tblrows" width="60%"><bean:write
																	name="asmBean" property="protocolType" />&nbsp;</td>
														</tr>
														<logic:iterate id="obj"
															collection="<%=asmBean.getMappingMap()%>">
															<tr>
																<td class="tblfirstcol" width="40%"><bean:write
																		name="obj" property="key" />&nbsp;</td>
																<td class="tblrows" width="60%"><bean:write
																		name="obj" property="value" />&nbsp;</td>
															</tr>
														</logic:iterate>
													</table>
												</div></td>
												<td align="left" class="tblcol">
													<bean:write name="asmBean" property="protocolType" />&nbsp;
												</td>
										</tr>

										<% iIndex += 1; %>
									</logic:iterate>
								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>


								<tr>

									<td valign="middle" colspan="8"><input type="button"
										name="closeSession"
										onclick="validateCloseSession('closeSelected')"
										value=" Close Selected Session " class="light-btn" /> <input
										type="button" name="closeAllSession"
										value=" Close All Result Session " class="light-btn"
										onclick="javascript:location.href='<%=basePath%>/closeSession.do?sminstanceid=<bean:write name="searchASMForm" property="sessionManagerId"/>&strUserName=<bean:write name="searchASMForm" property="userName"/>&nasIp=<bean:write name="searchASMForm" property="nasIpAddress"/>&groupName=<bean:write name="searchASMForm" property="groupName"/>&idleTime=<bean:write name="searchASMForm" property="idleTime"/>&groupByCriteria=<bean:write name="searchASMForm" property="groupbyCriteria"/>'" />
										<input type="button" name="purge" onclick="purge_confirm()"
										value=" Purge " class="light-btn" /> <input type="button"
										name="purgeAll" onclick="purgeAll_confirm()"
										value=" Purge All " class="light-btn" /> <input type="button"
										name="download" value=" Download " class="light-btn"
										onclick="validateCloseSession('download')" /></td>
								</tr>

								<tr height="4">
									<td></td>
								</tr>
								<%
							}else{
							%>
								<tr>

									<td valign="middle" colspan="8"><input type="button"
										name="purge" onclick="purge_confirm()" value=" Purge "
										class="light-btn" /> <input type="button" name="purgeAll"
										onclick="purgeAll_confirm()" value=" Purge All "
										class="light-btn" /></td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>

								<tr>
									<td align="center" class="tblheader" valign="top" width="2%">
										<input type="checkbox" name="toggleAll" value="checkbox"
										onclick="checkAll()" />
									</td>
									<td align="left" class="tblheader" valign="top" width="2%"><bean:message
											key="general.serialnumber" /></td>
									<td align="left" class="tblheader" valign="top" width="10%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.username" /></td>
									<td align="left" class="tblheader" valign="top" width="12%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.groupname" /></td>
									<td align="left" class="tblheader" valign="top" width="16%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.lastupdatedtime" /></td>
									<td align="left" class="tblheader" valign="top" width="16%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.starttime" /></td>
									<td align="left" class="tblheader" valign="top" width="16%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.acctsessionid" /></td>
									<td align="left" class="tblheader" valign="top" width="16%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.framedipaddress" /></td>
									<td align="left" class="tblheader" valign="top" width="16%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.protocoltype" /></td>
								</tr>
								<tr>
									<td align="center" colspan="9" class="tblfirstcol">No
										Records Found.</td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
								</tr>

								<tr>

									<td valign="middle" colspan="8"><input type="button"
										name="purge" onclick="purge_confirm()" value=" Purge "
										class="light-btn" /> <input type="button" name="purgeAll"
										onclick="purgeAll_confirm()" value=" Purge All "
										class="light-btn" /></td>
								</tr>
								<tr height="4">
									<td></td>
								</tr>

								<%
						}
					}else{
					%>

								<% 
							if(lstASMGroupBy!=null && lstASMGroupBy.size()>0){
						%>
								<tr>

									<td align="left" class="tblheader" valign="top" width="2%"><bean:message
											key="general.serialnumber" /></td>
									<%
										if(groupByCrit.equalsIgnoreCase("USER_NAME")){						   				
					   				%>
									<td align="left" class="tblheader" valign="top" width="47%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.username" /></td>
									<%
					   				} else if(groupByCrit.equalsIgnoreCase("NAS_IP_ADDRESS")){
					   				%>
									<td align="left" class="tblheader" valign="top" width="47%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.nasipaddress" /></td>
									<%
					   				}else{
					   				%>
									<td align="left" class="tblheader" valign="top" width="47%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.groupname" /></td>
									<%
					   				}
					   				 %>



									<td align="left" class="tblheader" valign="top" width="47%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.username.count" /></td>
								</tr>

								<logic:iterate id="asmBean" name="searchASMForm"
									property="asmListGroupBy"
									type="com.elitecore.elitesm.datamanager.sessionmanager.data.IASMData">


									<tr>
										<td align="left" class="tblfirstcol" valign="top"><%=(iIndex+1) %></td>

										<%
										if(groupByCrit.equalsIgnoreCase("USER_NAME")){						   				
					   				%>
										<td align="left" class="tblrows"><bean:write
												name="asmBean" property="userName" /></a>&nbsp;</td>
										<%
					   				} else if(groupByCrit.equalsIgnoreCase("NAS_IP_ADDRESS")){
					   				%>

										<td align="left" class="tblrows"><bean:write
												name="asmBean" property="nasIpAddress" /></a>&nbsp;</td>
										<%
					   				}else{
					   				%>
										<td align="left" class="tblrows"><bean:write
												name="asmBean" property="groupName" /></a>&nbsp;</td>
										<%
					   				}
					   				 %>
										<td align="left" class="tblcol"><bean:write
												name="asmBean" property="nameCount" /></a>&nbsp;</td>
									</tr>

									<% iIndex += 1; %>

								</logic:iterate>

								<tr height="4">
									<td></td>
								</tr>

								<%
						}else{
						%>
								<tr>
									<td align="left" class="tblheader" valign="top" width="2%"><bean:message
											key="general.serialnumber" /></td>
									<%
										if(groupByCrit.equalsIgnoreCase("USER_NAME")){						   				
					   				%>
									<td align="left" class="tblheader" valign="top" width="47%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.username" /></td>
									<%
					   				} else if(groupByCrit.equalsIgnoreCase("NAS_IP_ADDRESS")){
					   				%>
									<td align="left" class="tblheader" valign="top" width="47%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.nasipaddress" /></td>
									<%
					   				}else{
					   				%>
									<td align="left" class="tblheader" valign="top" width="47%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.groupname" /></td>
									<%
					   				}
					   				 %>
									<td align="left" class="tblheader" valign="top" width="47%"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.asm.username.count" /></td>
								</tr>

								<tr>
									<td align="center" colspan="4" class="tblfirstcol">No
										Records Found.</td>
								</tr>

								<tr height="4">
									<td></td>
								</tr>
								<%
					}
					}
					 %>
							</table>
						</td>
					</tr>
					<tr>
						<td class="btns-td" align="right" colspan="8">
							<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <img
							src="<%=basePath%>/images/next.jpg" name="Image61"
							onclick="navigate('next',<%=pageNo+1%>)"
							onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Next" border="0" /> <img
							src="<%=basePath%>/images/last.jpg" name="Image612"
							onclick="navigate('last',<%=totalPages+1%>)"
							onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Last" border="0"> <% } %>
							<% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
							<img src="<%=basePath%>/images/first.jpg" name="Image511"
							onclick="navigate('first',<%= 1%>)"
							onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="First" border="0"> <img
							src="<%=basePath%>/images/previous.jpg"
							onclick="navigate('next',<%= pageNo-1%>)" name="Image5"
							onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
							onclick="navigate('next',<%= pageNo+1%>)"
							onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Next" border="0"> <img
							src="<%=basePath%>/images/last.jpg" name="Image612"
							onclick="navigate('last',<%= totalPages+1%>)"
							onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Last" border="0"> <% } else if(pageNo == totalPages){ %>
							<img src="<%=basePath%>/images/first.jpg" name="Image511"
							onclick="navigate('previous',<%= 1%>)"
							onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="First" border="0"> <img
							src="<%=basePath%>/images/previous.jpg" name="Image5"
							onclick="navigate('previous',<%= pageNo-1%>)"
							onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
							onclick="navigate('next',<%= pageNo+1%>)"
							onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Next" border="0"> <img
							src="<%=basePath%>/images/last.jpg" name="Image612"
							onclick="navigate('last',<%= totalPages+1%>)"
							onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Last" border="0"> <% } else { %>
							<img src="<%=basePath%>/images/first.jpg" name="Image511"
							onclick="navigate('previous',<%= 1%>)"
							onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="First" border="0"> <img
							src="<%=basePath%>/images/previous.jpg" name="Image5"
							onclick="navigate('previous',<%=pageNo-1%>)"
							onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
							onclick="navigate('next',<%=pageNo+1%>)"
							onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Next" border="0"> <img
							src="<%=basePath%>/images/last.jpg" name="Image612"
							onclick="navigate('last',<%=totalPages+1%>)"
							onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Last" border="0"> <% } %>
							<% } %> <% if(pageNo == totalPages+1) { %> <img
							src="<%=basePath%>/images/first.jpg" name="Image511"
							onclick="navigate('first',<%=1%>)"
							onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="First" border="0"> <img
							src="<%=basePath%>/images/previous.jpg" name="Image5"
							onclick="navigate('previous',<%=pageNo-1%>)"
							onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
							<% } %> <% } %>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</html:form>
	<%}%>
</table>

