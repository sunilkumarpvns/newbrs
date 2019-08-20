<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@page import="com.elitecore.netvertexsm.web.servermgr.sessionmgr.form.SearchASMForm"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.*"%>
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
		alert("Selection Required To Perform Close Session Operation.");
	else
		
		document.forms[1].submit();
		
	
}
else{
	if(document.forms[1].selected.checked==true)
		document.forms[1].submit();
		
	else
		alert("Selection Required To Perform Close Session Operation.");	
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
  			javascript:location.href='<%=basePath%>/purgeClosedSession.do?sminstanceid=<%=searchASMForm.getSessionManagerId()%>'+
					'&subscriptionId=<bean:write name="searchASMForm" property="subscriptionId"/>'+
					'&gxSessionId=<bean:write name="searchASMForm" property="gxSessionId"/>'+
					'&location=<bean:write name="searchASMForm" property="location"/>'+
					'&framedIpAddress=<bean:write name="searchASMForm" property="framedIpAddress"/>'+
					'&gatewayURL=<bean:write name="searchASMForm" property="gatewayURL"/>'+
					'&userEquipment=<bean:write name="searchASMForm" property="userEquipment"/>'+
					'&idleTime=<bean:write name="searchASMForm" property="idleTime"/>'+
					'&pageNumber=<bean:write name="searchASMForm" property="pageNumber"/>'+
					'&groupByCriteria=<bean:write name="searchASMForm" property="groupbyCriteria"/>'+
  					'&action=purgeClosedSession';
  		}
		
	}
	 
	function purgeAll_confirm()
	{
		var r=confirm("This Operation Will Delete All Sessions From Database. Do you Want To Continue?");
		if (r==true)
  		{
  			javascript:location.href='<%=basePath%>/purgeAllSession.do?sminstanceid=<%=searchASMForm.getSessionManagerId()%>'+
				'&subscriptionId=<bean:write name="searchASMForm" property="subscriptionId"/>'+
					'&gxSessionId=<bean:write name="searchASMForm" property="gxSessionId"/>'+
					'&location=<bean:write name="searchASMForm" property="location"/>'+
					'&framedIpAddress=<bean:write name="searchASMForm" property="framedIpAddress"/>'+
					'&gatewayURL=<bean:write name="searchASMForm" property="gatewayURL"/>'+
					'&userEquipment=<bean:write name="searchASMForm" property="userEquipment"/>'+
					'&idleTime=<bean:write name="searchASMForm" property="idleTime"/>'+
					'&pageNumber=<bean:write name="searchASMForm" property="pageNumber"/>'+
					'&groupByCriteria=<bean:write name="searchASMForm" property="groupbyCriteria"/>'+
					'&action=purgeAllSession';
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
	  var url =  "<%=basePath%>/viewASMDetailsPopUp.do?sminstanceid="+<%=searchASMForm.getSessionManagerId()%>+"&selected=";
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
	$( "#"+divnm ).dialog({
		width: 700,
		height: 250, 
		open: function () {
            
        }					       
    });					
}
</script>

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
	<html:form action="/searchASM.do">
	<html:hidden name="searchASMForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNumber%>"/>
	<html:hidden name="searchASMForm" styleId="sessionManagerId" property="sessionManagerId" value="<%=searchASMForm.getSessionManagerId().toString()%>"/>
	<html:hidden name="searchASMForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>"/>
	<html:hidden name="searchASMForm" styleId="action" property="action" value="search"/>	    			
  				<tr>
				<td class="table-header" colspan="5"><bean:message bundle="sessionMgrResources" key="session.gx.asm.search"/></td> 
			</tr>	
  				<tr>
				<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
			</tr> 
			
			<tr>
				<td colspan="3">
					<table width="97%" align="right" border="0" >
					
									<tr>
										<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.subscriptionid"/></td>
										<td align="left" class="labeltext" valign="top" width="32%" >
											<html:text styleId="subscriptionId" property="subscriptionId" size="30" maxlength="30"/>
										</td>
									</tr>
									
									<tr>
										<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.gatewayurl"/></td>
										<td align="left" class="labeltext" valign="top" width="32%" >
											<html:text styleId="gatewayURL" property="gatewayURL" size="30" maxlength="30"/>
										</td>
									</tr>
								
									<tr>
										<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.framedipaddress"/></td>
										<td align="left" class="labeltext" valign="top" width="32%" >
											<html:text styleId="framedIpAddress" property="framedIpAddress" size="30" maxlength="30"/>
										</td>
									</tr>
									
									<tr>
										<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.location"/></td>
										<td align="left" class="labeltext" valign="top" width="32%" >
											<html:text styleId="location" property="location" size="30" maxlength="30"/>
										</td>
									</tr>
									
									<tr>
										<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.userequipment"/></td>
										<td align="left" class="labeltext" valign="top" width="32%" >
											<html:text styleId="userEquipment" property="userEquipment" size="30" maxlength="30"/>
										</td>
									</tr>

									<tr>
										<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.idletime"/></td>
										<td align="left" class="labeltext" valign="top" width="16%" >
											<html:text styleId="idleTime" property="idleTime" size="10" maxlength="10"/>
											(In Mins.)
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
								    </tr>
									<tr>
		 								<td class="btns-td" valign="middle">&nbsp;</td>  
										<td align="left" class="labeltext" valign="top" width="5%">
										    <input type="button" name="Search" width="5%" name="ASMName" Onclick="validateSearch()" value="   Search   " class="light-btn" />       
											<input type="button" name="Reset" onclick="reset();" value="   Reset    " class="light-btn" >  
										</td>
									</tr>
					
					</table>
				</td>
			</tr>		
</html:form>						
					<%
  if(actionCheck!=null){
%>

	
<html:form action="/closeSession.do">	
		<html:hidden name="searchASMForm" styleId="action" property="action" value="closeSelected"/>
		<html:hidden name="searchASMForm" styleId="sessionManagerId" property="sessionManagerId" value="<%=searchASMForm.getSessionManagerId().toString()%>"/>
		<html:hidden name="searchASMForm" styleId="idleTime" property="idleTime"/>
		<html:hidden name="searchASMForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNumber%>"/>
		<html:hidden name="searchASMForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>"/>
		<tr>
			<td>
				<table cellSpacing="0" cellPadding="0" width="100%" border="0" >
					
					<tr>
				    		<td class="table-header" valign="bottom" colspan="3" align="left"><bean:message bundle="sessionMgrResources" key="session.gx.asm.list" /></td>
				    		<td align="right" class="blue-text" valign="middle" width="14%" colspan="4" >
									<% if(totalRecord == 0){ %>
									<% }else if(pageNo == totalPages+1) { %>
										    [<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
									<% } else if(pageNo == 1) { %>
										    [<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>] of <%= totalRecord %>
									<% } else { %>
										    [<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>] of <%= totalRecord %>
									<% } %>
							</td>
				    	
				    </tr>
				    
				    <tr>
			   				<td class="btns-td" align="right" colspan="8">
			   					<% if(totalPages >= 1) { %>
									  	<% if(pageNo == 1){ %>
													<img  src="<%=basePath%>/images/next.jpg"  name="Image61"  onclick="navigate('next',<%=pageNo+1%>)"  onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" />
													<img  src="<%=basePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
									  	<% } %>
									  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
									  		<%  if(pageNo-1 == 1){ %>
													<img  src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%= 1%>)"   onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
													<img  src="<%=basePath%>/images/previous.jpg" onclick="navigate('next',<%= pageNo-1%>)" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
													<img  src="<%=basePath%>/images/next.jpg"  name="Image61"  onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" >
													<img  src="<%=basePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
									  		<% } else if(pageNo == totalPages){ %>
													<img  src="<%=basePath%>/images/first.jpg"  name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
													<img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onclick="navigate('previous',<%= pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
													<img  src="<%=basePath%>/images/next.jpg"  name="Image61"  onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" >
													<img  src="<%=basePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"   onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
									  		<% } else { %>
													<img  src="<%=basePath%>/images/first.jpg"  name="Image511" onclick="navigate('previous',<%= 1%>)"  onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
										  			<img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onclick="navigate('previous',<%=pageNo-1%>)"onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
													<img  src="<%=basePath%>/images/next.jpg"  name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" >
													<img  src="<%=basePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
											<% } %>
									  	<% } %>
									 	<% if(pageNo == totalPages+1) { %>
													<img  src="<%=basePath%>/images/first.jpg"  name="Image511"  onclick="navigate('first',<%=1%>)"onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
													<img  src="<%=basePath%>/images/previous.jpg"  name="Image5"  onclick="navigate('previous',<%=pageNo-1%>)"onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
									  	<% } %>
								<% } %>
				   			</td>
					</tr>
					<tr>
				 	  <td colspan="7">	
				 	  	<table cellSpacing="0" cellPadding="0" width="97%" border="0" align="right">
							<tr height="2">        
								<td></td>                   
							</tr>
							<%
	  						if(lstASM!=null && lstASM.size()>0){
							%>	 

								<%--tr>
									
									<td valign="middle" colspan="8">
										<input type="button" name="closeSession"  onclick="validateCloseSession('closeSelected')" value=" Close Selected Session " class="light-btn" /> 
										<input type="button" name="closeAllSession"  value=" Close All Result Session " class="light-btn" onclick="javascript:location.href='<%=basePath%>/closeSession.do?sminstanceid=<bean:write name="searchASMForm" property="sessionManagerId"/>&strUserName=<bean:write name="searchASMForm" property="userName"/>&nasIp=<bean:write name="searchASMForm" property="nasIpAddress"/>&groupName=<bean:write name="searchASMForm" property="groupName"/>&idleTime=<bean:write name="searchASMForm" property="idleTime"/>&groupByCriteria=<bean:write name="searchASMForm" property="groupbyCriteria"/>'"/>                 
										<input type="button" name="purge" onclick="purge_confirm()" value=" Purge " class="light-btn" /> 
										<input type="button" name="purgeAll" onclick="purgeAll_confirm()" value=" Purge All " class="light-btn" /> 
										<input type="button" name="download" value=" Download " class="light-btn" onclick="validateCloseSession('download')"/>
									</td>
								</tr--%>	
								
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
									<%--td align="center" class="tblheader" valign="top" width="2%">
						  				<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll()"/>
									</td--%>
									<td align="left" class="tblheader" valign="top" width="2%"><bean:message  key="general.serialnumber"/></td>
									<td align="left" class="tblheader" valign="top" width="10%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.subscriptionid"/></td>
									<td align="left" class="tblheader" valign="top" width="17%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.lastupdatedtime"/></td>
									<td align="left" class="tblheader" valign="top" width="16%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.starttime"/></td>	
								    <td align="left" class="tblheader" valign="top" width="25%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.gxsessionid"/></td>
								    <td align="left" class="tblheader" valign="top" width="16%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.framedipaddress"/></td>
								     <td align="left" class="tblheader" valign="top" width="16%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.gatewayurl"/></td>
								    <td align="left" class="tblheader" valign="top" width="16%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.location"/></td>
								    <td align="left" class="tblheader" valign="top" width="16%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.userequipment"/></td>
								   
								    
								</tr>	
								 
								<tr>
								<logic:iterate id="asmBean" name="searchASMForm" property="asmList" type="com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.CoreSessData">
								   
					   			<tr>
					   				<%--td align="left" class="tblfirstcol" valign="top">
                  							<input type="checkbox" name="selected" id="<%=(iIndex+1) %>" value="<bean:write name="asmBean" property="concUserId"/>">
                  					</td--%>
					   				<td align="left" class="tblfirstcol" valign="top"><%=(iIndex+1) %></td>
					   				<td align="left" class="tblrows" valign="top"><a style="cursor: pointer;"   onclick="openPopup('<%=asmBean.getCId()%>')"><bean:write name="asmBean" property="subscriptionId"/></a>&nbsp;</td>
					   							   				<td align="left" class="tblrows">
					   				<%=EliteUtility.dateToString(asmBean.getLastUpdateTime(), ConfigManager.get(ConfigConstant.DATE_FORMAT))%>
					   				&nbsp;</td>
					   				<td align="left" class="tblrows" valign="top"><bean:write name="asmBean" property="totalSessionTime"/>&nbsp;</td>
					   				<td align="left" class="tblrows" valign="top"><bean:write name="asmBean" property="sessionId"/>&nbsp;</td> 
									<td align="left" class="tblrows" valign="top"><bean:write name="asmBean" property="framedIpAddress"/>
									<td align="left" class="tblrows" valign="top"><bean:write name="asmBean" property="gatewayURL"/>
									<td align="left" class="tblrows" valign="top"><bean:write name="asmBean" property="location"/>
					   				<td align="left" class="tblcol" valign="top"><bean:write name="asmBean" property="userEquipment"/>&nbsp;
					   				<%					   										   				
					   					String divName = "fieldMappingDiv" + asmBean.getCId(); 
										String divTitle = 	"Session Information [ Session Id : "+asmBean.getSessionId()+" ]";				   				
					   				%>
					   				
					   				<div id=<%=divName %> style="display: none;" title="<%=divTitle%>">
					   					<table cellSpacing="0" cellPadding="0" width="100%" border="0" class="box">
					   						<tr>
					   							<td class="tblfirstcol" width="30%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.subscriptionid"/></td>
					   							<td class="tblrows" width="70%"><bean:write name="asmBean" property="subscriptionId"/>&nbsp;</td>					   												   								
					   						</tr>
					   						<tr>
					   							<td class="tblfirstcol" ><bean:message bundle="sessionMgrResources" key="session.gx.asm.gatewayurl"/></td>
					   							<td class="tblrows"><bean:write name="asmBean" property="gatewayURL"/>&nbsp;</td>					   												   								
					   						</tr>
					   						<tr>
					   							<td class="tblfirstcol" ><bean:message bundle="sessionMgrResources" key="session.gx.asm.gxsessionid"/></td>
					   							<td class="tblrows" ><bean:write name="asmBean" property="sessionId"/>&nbsp;</td>					   												   								
					   						</tr>
					   						<tr>
					   							<td class="tblfirstcol" ><bean:message bundle="sessionMgrResources" key="session.gx.asm.framedipaddress"/></td>
					   							<td class="tblrows" ><bean:write name="asmBean" property="framedIpAddress"/>&nbsp;</td>					   												   								
					   						</tr>	
					   										   										   		
						   					<logic:iterate id="obj" collection="<%=asmBean.getMappingFieldResultMap()%>">
						   							<tr>
						   								<td class="tblfirstcol" ><bean:write name="obj" property="key"/>&nbsp;</td>
						   								<td class="tblrows" ><bean:write name="obj" property="value"/>&nbsp;</td>					   												   								
						   							</tr>
						   					</logic:iterate>
						   					
						   					<tr>
					   							<td colspan="2" class="tblheader-bold"> Bearer Sessions</td>					   												   								
					   						</tr>
						   					<tr>
						   						<td colspan="2" align="center">
						   						<%if(asmBean.getBearerSessDataList()!=null && !asmBean.getBearerSessDataList().isEmpty()){%>
						   							<table cellpadding="0" cellspacing="0" border="0" width="100%">
																				   							
						   								
						   									<% BearerSessData headerBearerSessData = (BearerSessData)asmBean.getBearerSessDataList().get(0); 
						   									   
						   									%>
						   									<tr>
						   									<logic:iterate id="obj" collection="<%=headerBearerSessData.getMappingFieldResultMap()%>">
								   								<td class="tblheader" ><bean:write name="obj" property="key"/>&nbsp;</td>
										   					</logic:iterate>
										   					</tr>
										   					<logic:iterate id="bearerSessData" collection="<%=asmBean.getBearerSessDataList()%>">
										   						<tr>
										   						<logic:iterate id="fieldMap" name="bearerSessData" property="mappingFieldResultMap" >
								   									<td class="tblrows" ><bean:write name="fieldMap" property="value"/>&nbsp;</td>
										   						</logic:iterate>
										   						<tr>
										   					</logic:iterate>
										   					
						   								
						   								
						   							</table>
						   							<%}else{%>
						   							No Record Found.
						   							<%}%>
						   						</td>
						   					</tr>
					   					</table>
					   				</div>
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
							    
								
								<%--tr>
										
										<td valign="middle" colspan="8">
											<input type="button" name="closeSession"  onclick="validateCloseSession('closeSelected')" value=" Close Selected Session " class="light-btn" />         
											<input type="button" name="closeAllSession"  value=" Close All Result Session " class="light-btn" onclick="javascript:location.href='<%=basePath%>/closeSession.do?sminstanceid=<bean:write name="searchASMForm" property="sessionManagerId"/>&strUserName=<bean:write name="searchASMForm" property="userName"/>&nasIp=<bean:write name="searchASMForm" property="nasIpAddress"/>&groupName=<bean:write name="searchASMForm" property="groupName"/>&idleTime=<bean:write name="searchASMForm" property="idleTime"/>&groupByCriteria=<bean:write name="searchASMForm" property="groupbyCriteria"/>'"/>                 
											<input type="button" name="purge" onclick="purge_confirm()" value=" Purge " class="light-btn" /> 
											<input type="button" name="purgeAll" onclick="purgeAll_confirm()" value=" Purge All " class="light-btn" />
											<input type="button" name="download" value=" Download " class="light-btn" onclick="validateCloseSession('download')"/>
										</td>
								</tr--%>
								
								<tr height="4">        
									<td></td>                   
								</tr>
							<%
							}else{
							%>		
								<%--tr>
									
									<td valign="middle" colspan="8">
										<input type="button" name="purge" onclick="purge_confirm()" value=" Purge " class="light-btn" /> 
										<input type="button" name="purgeAll" onclick="purgeAll_confirm()" value=" Purge All " class="light-btn" /> 							
									</td>
								</tr--%>	
								
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
									<%--td align="center" class="tblheader" valign="top" width="2%">
						  				<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll()"/>
									</td--%>
									<td align="left" class="tblheader" valign="top" width="2%"><bean:message  key="general.serialnumber"/></td>
									<td align="left" class="tblheader" valign="top" width="10%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.subscriptionid"/></td>
									<td align="left" class="tblheader" valign="top" width="17%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.lastupdatedtime"/></td>
									<td align="left" class="tblheader" valign="top" width="16%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.starttime"/></td>	
								    <td align="left" class="tblheader" valign="top" width="25%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.gxsessionid"/></td>
								    <td align="left" class="tblheader" valign="top" width="16%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.framedipaddress"/></td>
								    <td align="left" class="tblheader" valign="top" width="16%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.gatewayurl"/></td>
								    <td align="left" class="tblheader" valign="top" width="16%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.location"/></td>
								    <td align="left" class="tblheader" valign="top" width="16%"><bean:message bundle="sessionMgrResources" key="session.gx.asm.userequipment"/></td>							</tr>	
							<tr>	
								 <td align="center" colspan="9" class="tblfirstcol">
								 	No Records Found.
						    	</td>
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
							
							<%--tr>
									
								<td valign="middle" colspan="8">
									<input type="button" name="purge" onclick="purge_confirm()" value=" Purge " class="light-btn" /> 
									<input type="button" name="purgeAll" onclick="purgeAll_confirm()" value=" Purge All " class="light-btn" /> 							
								</td>
							</tr--%>	
							<tr height="4">        
									<td></td>                   
							</tr>			
					
					<%
						}
					
					%>
					
					
					</table>
					</td>
				</tr>
			    <tr>
	   				<td class="btns-td" align="right" colspan="8">
	   					<% if(totalPages >= 1) { %>
						  	<% if(pageNo == 1){ %>
												<img  src="<%=basePath%>/images/next.jpg"  name="Image61"  onclick="navigate('next',<%=pageNo+1%>)"  onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" />
												<img  src="<%=basePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
								  	<% } %>
								  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
								  		<%  if(pageNo-1 == 1){ %>
												<img  src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%= 1%>)"   onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
												<img  src="<%=basePath%>/images/previous.jpg" onclick="navigate('next',<%= pageNo-1%>)" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
												<img  src="<%=basePath%>/images/next.jpg"  name="Image61"  onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" >
												<img  src="<%=basePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
								  		<% } else if(pageNo == totalPages){ %>
												<img  src="<%=basePath%>/images/first.jpg"  name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
												<img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onclick="navigate('previous',<%= pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
												<img  src="<%=basePath%>/images/next.jpg"  name="Image61"  onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" >
												<img  src="<%=basePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"   onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
								  		<% } else { %>
												<img  src="<%=basePath%>/images/first.jpg"  name="Image511" onclick="navigate('previous',<%= 1%>)"  onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
									  			<img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onclick="navigate('previous',<%=pageNo-1%>)"onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
												<img  src="<%=basePath%>/images/next.jpg"  name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" >
												<img  src="<%=basePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
										<% } %>
								  	<% } %>
								 	<% if(pageNo == totalPages+1) { %>
												<img  src="<%=basePath%>/images/first.jpg"  name="Image511"  onclick="navigate('first',<%=1%>)"onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
												<img  src="<%=basePath%>/images/previous.jpg"  name="Image5"  onclick="navigate('previous',<%=pageNo-1%>)"onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
								  	<% } %>
								  <% } %>
				 	</td>
				</tr>
			</table>
			</td>
		</tr>	
			</html:form>
		<%}%>
</table>

