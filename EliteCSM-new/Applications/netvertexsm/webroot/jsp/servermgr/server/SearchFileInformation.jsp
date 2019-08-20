






<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ErrorProcessingConstant"%>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.server.form.SearchFileInformationForm"%>

<script type="text/javascript">
<!--
function selectFile(){
	document.searchFileInformationForm.action.value="search";
	document.searchFileInformationForm.submit();
}
function updateFile(){
	
	var checkboxes = document.getElementsByName("select");
	var state = false;
	
		for(var i=0;i<checkboxes.length;i++){
		if(checkboxes[i].checked){
			checkboxes[i].value=i;
			state = true;
		}
	}
	document.forms[0].deviceName.value = '<bean:write name="searchFileInformationForm" property="deviceName" />';
	if(state){	
		msg = "Are you sure the selected file(s) have been updated.";
	    var agree = confirm(msg);
	    if(agree){
			document.searchFileInformationForm.action.value="update";
			document.searchFileInformationForm.submit();
		}
		}else
		{
		alert('Atleast one Processing rule must be selected');
		}
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
		document.searchFileInformationForm.select[i].checked=true
	}	
	
}
function unCheckAll(){

	var checkboxes = document.getElementsByName("select");
	var totalBoxes = checkboxes.length;
	for(var i=0;i<totalBoxes;i++){
		document.searchFileInformationForm.select[i].checked=false;
	}
	
}

 function popup(mylink, windowname)
			{
				if (! window.focus)return true;
				
				var elementList = document.getElementsByName("radioChoice");
				for(i=i;i<elementList.length;i++)
					{
					if(elementList[i].checked){
						mylink = mylink +'type='+ elementList[i].value;
					}
				}
     			var href;
				if (typeof(mylink) == 'string')
   					href=mylink;
				else
   					href=mylink.href;
   				
				window.open(href,windowname,'width=700,height=500,left=150,top=100,scrollbars=yes');
				
				return false;
			}       

			function popupuploadfile(mylink, windowname)
			{
				if (! window.focus)return true;
				window.open(mylink,windowname,'width=700,height=500,left=150,top=100,scrollbars=yes');
				
				return false;
			}       

//-->
</script>
	
<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <bean:define id="netServerInstanceBean" name="netServerInstanceData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData" />
    <tr> 
	    <td class="table-header" ><bean:message bundle="servermgrResources" key="servermgr.serversummary"/>
        </td>
    </tr>
    <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servermgrResources" key="servermgr.serverinformation"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="20%" height="20%" ><bean:message bundle="servermgrResources" key="servermgr.servername"/></td>
            <td class="tblcol" width="30%" height="20%" ><bean:write name="netServerInstanceBean" property="name"/></td>
          </tr>
       </table>
	  </td>
    </tr>
</table>

<%

	String localBasePath = request.getContextPath();
	SearchFileInformationForm searchFileInformationForm = (SearchFileInformationForm)request.getAttribute("searchFileInformationForm");
	
	int recordPerPage = searchFileInformationForm.getNumerOfRecordsPerPage();
	int totalNumberOfRecord = searchFileInformationForm.getTotalNumberOfRecord();
	int totalPage = searchFileInformationForm.getTotalNoOfPage();
	int nextPage = searchFileInformationForm.getNextPage();
	int previousPage = searchFileInformationForm.getPreviousPage();
	int start = searchFileInformationForm.getStart();
	int end = searchFileInformationForm.getEnd();
	int currentPage=searchFileInformationForm.getPageNo();
	String netServerid=searchFileInformationForm.getNetServerId();
	List lstFiles=searchFileInformationForm.getLstFiles();
	String filePath = searchFileInformationForm.getFilePath();
	
%>

<logic:equal name="searchFileInformationForm" property="errorCode" value="0">

	<html:form action="/searchFileInformation">
	<html:hidden name="searchFileInformationForm" styleId="action" property="action"/>
	<html:hidden name="searchFileInformationForm" styleId="netServerId" property="netServerId"/>
	<html:hidden name="searchFileInformationForm" styleId="pageNo" property="pageNo"/>
	<table width="100%" id="listTable" border="0" cellpadding="0" cellspacing="0" >
		<tr>
			<td class="table-header" width="100%" colspan="4" align="left">
				<bean:message bundle="servermgrResources" key="servermgr.fileinformation.searchFileInformation"/>
			</td>
		</tr>	
		
		<tr>
			<td width="100%" colspan="4" align="left">
			
			
			<table width="97%"  id="listTable" type="tbl-list" border="0" cellpadding="0" cellspacing="0" align="right">		
			
			<tr><td class="small-gap" >&nbsp;&nbsp;</td></tr>
			<tr>
			<td align="left" class="labeltext" valign="top">
					Parsing
			    	&nbsp;&nbsp;&nbsp;<html:radio name="searchFileInformationForm"  styleId="radioChoice" property="radioChoice" value="<%=ErrorProcessingConstant.PARSING%>"></html:radio>
			    </td>
			    <td align="left" class="labeltext" valign="top">  
	    	         Processing 
			    	&nbsp;&nbsp;&nbsp;<html:radio name="searchFileInformationForm" styleId="radioChoice" property="radioChoice"  value="<%=ErrorProcessingConstant.PROCESSING%>"></html:radio>
	           	</td>	
			</tr>
			<tr><td class="small-gap">&nbsp;&nbsp;</td></tr>
			<tr>
				<td align="left" class="labeltext" width="20%" >
					<bean:message bundle="servermgrResources" key="servermgr.fileinformation.filename"  />
					
				</td>
				<td align="left" colspan="3">
				<html:text name="searchFileInformationForm" styleId="fileName" property="fileName" size="40" > </html:text>
						<a href='#' onClick="return popup('<%=localBasePath%>/searchErrorProcessingFilePopup.do?','null')">
							<img src="<%=localBasePath%>/images/lookup.jpg" name="Image521" border=0 id="Image5" onMouseOver="MM_swapImage('Image521','','<%=localBasePath%>/images/lookup-hover.jpg',1)" onMouseOut="MM_swapImgRestore()">
						</a>
						
						
				</td>
			</tr>
			<tr>
				<td align="left" class="labeltext">
					<bean:message bundle="servermgrResources" key="servermgr.fileinformation.devicename" />
				</td>
				<td align="left">
				<html:text name="searchFileInformationForm" styleId="deviceName" property="deviceName" size="30"></html:text>
				</td>
			</tr>	
			
			
			<tr>
				<td>
					 <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right">
					  <tr>
					  <td>
						<input type="button" value="Search" class="light-btn" onclick="selectFile()" />
						<input type="button" value="Cancel" onclick="javascript:location.href='<%=localBasePath%>/viewNetServerInstance.do?netserverid=<%=netServerid%>'" class="light-btn" />
						</td>
						</tr>
						</table>
				</td>
			</tr>
			
			
			
			
			</table>
			</td>
			</tr>
	<pg:pager maxIndexPages="10" maxPageItems="<%=recordPerPage%>">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	    	<% if(lstFiles!=null){ %>	
	<tr>
				<td align="left" width="100%" colspan="2"  class="small-gap" >&nbsp;</td>
	</tr>
      	
	<tr>
				<td class="table-header" align="left" width="50%">
					<bean:message bundle="servermgrResources" key="servermgr.fileinformation.filelist"/>
				</td>
				<td class="blue-text" align="right" width="50%">
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
				<td class="btns-td" align="right" colspan="2">
					<%
					if (totalPage > 1) {
					%>
					<%
					if (previousPage != 0) {
					%>
					<a href="<%=localBasePath%>/searchFileInformation.do?action=paging&pageNo=1&netserverid=<%=netServerid%>&totalNumberOfRecord=<%=totalNumberOfRecord%>&totalPage=<%=totalPage%>&fileName=<bean:write name="searchFileInformationForm" property="fileName"/>&deviceName=<bean:write name="searchFileInformationForm" property="deviceName"/>&radioChoice=<bean:write name="searchFileInformationForm" property="radioChoice"/>" ><img src="<%=localBasePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=localBasePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()" alt="First" border="0">
					</a>
					<a href="<%=localBasePath%>/searchFileInformation.do?action=paging&pageNo=<%=previousPage%>&netserverid=<%=netServerid%>&totalNumberOfRecord=<%=totalNumberOfRecord%>&totalPage=<%=totalPage%>&fileName=<bean:write name="searchFileInformationForm" property="fileName"/>&deviceName=<bean:write name="searchFileInformationForm" property="deviceName"/>&radioChoice=<bean:write name="searchFileInformationForm" property="radioChoice"/>"  ><img src="<%=localBasePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=localBasePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
					</a>
					<%
					}
					%>
					<%
					if (nextPage != 0 && (previousPage + 1) != totalPage) {
					%>
					<a href="<%=localBasePath%>/searchFileInformation.do?action=paging&pageNo=<%=nextPage%>&netserverid=<%=netServerid%>&totalNumberOfRecord=<%=totalNumberOfRecord%>&totalPage=<%=totalPage%>&fileName=<bean:write name="searchFileInformationForm" property="fileName"/>&deviceName=<bean:write name="searchFileInformationForm" property="deviceName"/>&radioChoice=<bean:write name="searchFileInformationForm" property="radioChoice"/>" ><img src="<%=localBasePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=localBasePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
					</a>
					<a	href="<%=localBasePath%>/searchFileInformation.do?action=paging&pageNo=<%=totalPage%>&netserverid=<%=netServerid%>&totalNumberOfRecord=<%=totalNumberOfRecord%>&totalPage=<%=totalPage%>&fileName=<bean:write name="searchFileInformationForm" property="fileName"/>&deviceName=<bean:write name="searchFileInformationForm" property="deviceName"/>&radioChoice=<bean:write name="searchFileInformationForm" property="radioChoice"/>" ><img
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
   		 <tr> 
      <td valign="top" align="right" colspan="2">  

        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
        	<tr>
				<td align="right" class="blue-text" valign="middle" colspan="4">
				</td>
			</tr>
        	<tr> 
           		<td align="center"  width="5%" class="tblheader">
					<input type="checkbox" name="toggleAll" id="toggleAll" value="checkbox" onclick="check();" />
				</td>
				<td align="center" class="tblheader" width="5%">
					<bean:message bundle="servermgrResources" key="servermgr.serialnumber" />
				</td>
				<td align="left" class="tblheader"  width="70%">
					&nbsp;&nbsp;&nbsp;&nbsp;<bean:message bundle="servermgrResources" key="servermgr.fileinformation.filename" />
				</td>
				<td align="left" class="tblheader"  width="25%">
				    <bean:message bundle="servermgrResources" key="servermgr.fileinformation.filesize" />
				</td>
				<td align="center" class="tblheader"  width="10%">
					<bean:message bundle="servermgrResources" key="servermgr.fileinformation.download" />
				</td>
				<td align="center" class="tblheader"  width="10%">
					<bean:message bundle="servermgrResources" key="servermgr.fileinformation.upload" />
				</td>
				</tr>
				<% int i=0;%>
			<logic:iterate id="searchFileBean" indexId="i"  name="searchFileInformationForm"  property="lstFiles" type="com.elitecore.netvertexsm.web.servermgr.server.form.SearchFileInformationBean">
				<pg:item>
				<tr>
					
					<td align="center" width="5%" class="tblfirstcol">
						<html:checkbox name="searchFileInformationForm" styleId="select" property="select" />
					</td>
					<td align="center" width="3%" class="tblrows">
						<%=i+1%>
					</td>					
					<td align="left" width="70%" class="tblrows">
						&nbsp;&nbsp;&nbsp;&nbsp;<bean:write name="searchFileBean" property="fileName"/>
					</td>
					<td align="left" width="70%" class="tblrows">
						&nbsp;&nbsp;&nbsp;&nbsp;<bean:write name="searchFileBean" property="fileSize"/>
					</td>
					<td align="center"  width="70%" class="tblrows">
						<a href="<%=localBasePath%>/downloadErrorProcessingFile.do?filePath=<bean:write name="searchFileBean" property="filePath"/>&fileName=<bean:write name="searchFileBean" property="fileName"/>&netServerId=<%=netServerid %>"><img src="<%=localBasePath%>/images/ftv2link.gif" alt="Download" border="0"></a>
					</td>
					<td align="center"  width="5%" class="tblcol">
						<a  onclick="return popupuploadfile('<%=localBasePath%>/uploadErrorProcessingFile.do?serviceType=<bean:write name="searchFileInformationForm" property="radioChoice"/>&deviceName=<bean:write name="searchFileInformationForm" property="deviceName" />&netServerId=<%=netServerid%>&fileName=<bean:write name="searchFileBean" property="fileName"/>&','<%=i+1%><bean:write name="searchFileBean" property="fileSize"/>')" href="#"><img src="<%=localBasePath%>/images/edit.jpg" alt="Upload" border="0"></a>
					</td>					
					</tr>
			</pg:item>
		</logic:iterate>
		
		<logic:empty name="searchFileInformationForm" property="lstFiles" >
				<tr>
					<td align="center" width="100%" class="tblfirstcol" colspan="6">
						<bean:message bundle="servermgrResources" key="servermgr.fileinformation.norecordfound" />
					</td>
				</tr>
		</logic:empty>
         </table>

      </td>
     </tr>
		<tr>
			
					<td class="btns-td" align="right" colspan="2">
							<%
					if (totalPage > 1) {
					%>
					<%
					if (previousPage != 0) {
					%>
					<a href="<%=localBasePath%>/searchFileInformation.do?action=paging&pageNo=1&netserverid=<%=netServerid%>&totalNumberOfRecord=<%=totalNumberOfRecord%>&totalPage=<%=totalPage%>&fileName=<bean:write name="searchFileInformationForm" property="fileName"/>&deviceName=<bean:write name="searchFileInformationForm" property="deviceName"/>&radioChoice=<bean:write name="searchFileInformationForm" property="radioChoice"/>" ><img src="<%=localBasePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=localBasePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()" alt="First" border="0">
					</a>
					<a href="<%=localBasePath%>/searchFileInformation.do?action=paging&pageNo=<%=previousPage%>&netserverid=<%=netServerid%>&totalNumberOfRecord=<%=totalNumberOfRecord%>&totalPage=<%=totalPage%>&fileName=<bean:write name="searchFileInformationForm" property="fileName"/>&deviceName=<bean:write name="searchFileInformationForm" property="deviceName"/>&radioChoice=<bean:write name="searchFileInformationForm" property="radioChoice"/>"  ><img src="<%=localBasePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=localBasePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
					</a>
					<%
					}
					%>
					<%
					if (nextPage != 0 && (previousPage + 1) != totalPage) {
					%>
					<a href="<%=localBasePath%>/searchFileInformation.do?action=paging&pageNo=<%=nextPage%>&netserverid=<%=netServerid%>&totalNumberOfRecord=<%=totalNumberOfRecord%>&totalPage=<%=totalPage%>&fileName=<bean:write name="searchFileInformationForm" property="fileName"/>&deviceName=<bean:write name="searchFileInformationForm" property="deviceName"/>&radioChoice=<bean:write name="searchFileInformationForm" property="radioChoice"/>" ><img src="<%=localBasePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=localBasePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
					</a>
					<a	href="<%=localBasePath%>/searchFileInformation.do?action=paging&pageNo=<%=totalPage%>&netserverid=<%=netServerid%>&totalNumberOfRecord=<%=totalNumberOfRecord%>&totalPage=<%=totalPage%>&fileName=<bean:write name="searchFileInformationForm" property="fileName"/>&deviceName=<bean:write name="searchFileInformationForm" property="deviceName"/>&radioChoice=<bean:write name="searchFileInformationForm" property="radioChoice"/>" ><img
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
				<tr>
					<td>
					 <table width="94%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right">
					  <tr>
					  <td>
						<input type="button" value=" Update " class="light-btn" 	onclick="updateFile()" />
						<input type="button" value="  Cancel  "	 onclick="javascript:location.href='<%=localBasePath%>/searchFileInformation.do?netserverid=<%=netServerid%>'" class="light-btn" />
					</td>
					</table>
					</td>
				</tr>
				<%
				}
				%> 
	   </table>
     </pg:pager>
 </html:form>
 
</logic:equal>
<logic:notEqual name="searchFileInformationForm" property="errorCode" value="0">
	<table width="100%" id="listTable" border="0" cellpadding="0" cellspacing="0" >
	  <tr><td class="small-gap">&nbsp;&nbsp;</td></tr>	
		<tr>
			<td class="table-header" width="100%" colspan="4" align="left">
				<bean:message bundle="servermgrResources" key="servermgr.fileinformation.searchFileInformation"/>
			</td>
	   </tr>
	   <tr><td class="small-gap">&nbsp;&nbsp;</td></tr>	
	   <tr> 
    		<td valign="top" align="right"> 
    		    <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
    		    <tr><td class="small-gap" >&nbsp;&nbsp;</td></tr>	
     				<tr>
						<td class="blue-text-bold"><bean:message bundle="servermgrResources" key="servermgr.connectionfailure" />
						<br>
						<bean:message bundle="servermgrResources" key="servermgr.admininterfaceip" />
						:
						<bean:write name="netServerInstanceData" property="adminHost" />
						<br>
						<bean:message bundle="servermgrResources" key="servermgr.admininterfaceport" />
						:
						<bean:write name="netServerInstanceData" property="adminPort" />
						&nbsp;
						</td>
				</tr>
				<tr>
					<td>
						&nbsp;
					</td>
				</tr>
       		</table>
	  	</td>
    </tr>
</table>	
</logic:notEqual>