<jsp:directive.page import="java.util.List"/>
<jsp:directive.page import="java.util.HashMap"/>


<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<jsp:directive.page import="com.elitecore.netvertexsm.util.constants.MBeanNameConstant"/>
<jsp:directive.page import="java.util.Date"/>





<%
	String basePath = request.getContextPath();
		String dateFormat = ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT); 
%>

<script>
	var dFormat;
	dFormat = '<%=dateFormat%>';
	
	function popUpCalendar(ctl,	ctl2, datestyle)
	{
		datestyle = dFormat;
		jsPopUpCalendar( ctl, ctl2, datestyle ); 
	}
	
	function popup(mylink, windowname)
	{
		if (! window.focus)return true;
			var href;
					
		if (typeof(mylink) == 'string')
			href=mylink;
		else
			href=mylink.href;

		window.open(href, windowname, 'width=700,height=450,left=150,top=100,scrollbars=yes');
		return false;
	}
	
	function clearData() {
		document.forms[0].batchId.value = "";
		document.forms[0].dateFrom.value = "";
		document.forms[0].dateTo.value = "";
		document.forms[0].reason.value = "";
		document.forms[0].userName.value = "Select";
		document.forms[0].status.value = "ALL";
	}
	
	function validateCreate(){
		if(document.forms[0].dateFrom.value != null && document.forms[0].dateFrom.value != "" && document.forms[0].dateTo.value != null && document.forms[0].dateTo.value != "") {
			if (Date.parse(document.forms[0].dateFrom.value) > Date.parse(document.forms[0].dateTo.value)) {
				alert("Invalid Date Range!\nStart Date cannot be after End Date!");
				return false;
			}
		}
		
		document.forms[0].checkAction.value = 'search';
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
	
	 function chkForProcess() {
    	var selectVars = document.getElementsByName('select');
    	var flag = false;
    	for(i = 0; i < selectVars.length; i++) {
    		if(selectVars[i].checked == true) {
    			flag = true;
    			break;
    		}	
    	}
    	if(!flag) {
    		alert("At Least Select One Record To Be Process...");
    		return false;
    	}
    	return true;
    }
    
    function checkInBatch() {
		if(chkForProcess()) {
			document.forms[0].checkAction.value = 'checkInBatch';
			document.forms[0].submit();
		}	
	}
	
	function undoCheckedOut() {
		if(chkForProcess()) {
			document.forms[0].checkAction.value = 'undoCheckedOut';
			document.forms[0].submit();
		}	
	}
	
	function updateBatch() {
		if(chkForProcess()) {
			document.forms[0].checkAction.value = 'updateBatch';
			document.forms[0].submit();
		}	
	}	

</script>

<html:form action="/searchBatch">	
 <html:hidden styleId="checkAction" property="checkAction" />
 	<html:hidden styleId="serverId" property="serverId"/>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
        
		  <tr>
		  	<td>&nbsp;</td>
		  </tr>
		  
		  <tr> 
			<td class="tblheader-bold" colspan="3">Batch Search</td>
		  </tr>
		  
		  <tr>
		  	<td>&nbsp;</td>
		  </tr>
		  					<tr>
								<td align="left" class="labeltext" valign="top" width="10%">Batch Id</td>
								<td align="left" class="labeltext" valign="top" width="18%" ><html:text styleId="batchId" property="batchId" size="25"/></td>
						  	</tr>
						  	<tr>
								<td align="left" class="labeltext" valign="top" width="10%">Date</td>
								<td align="left" class="labeltext" valign="top" width="32%" >
									From:&nbsp;<html:text styleId="dateFrom" property="dateFrom" size="15" maxlength="15" readonly="true"/>
									<a  href="javascript:void(0)"  onclick="popUpCalendar(this, document.forms[0].dateFrom)" >
										<img  src="<%=basePath%>/images/calendar.jpg" border="0" tabindex="6">
									</a> &nbsp;&nbsp;
									To:&nbsp;<html:text styleId="dateTo" property="dateTo" size="15" maxlength="15" readonly="true"/>
									<a  href="javascript:void(0)"  onclick="popUpCalendar(this, document.forms[0].dateTo)" >
										<img  src="<%=basePath%>/images/calendar.jpg" border="0" tabindex="6">
									</a>
								</td>
						  	</tr>
						  	<tr>
								<td align="left" class="labeltext" valign="top" width="10%">Status</td>
								<td align="left" class="labeltext" valign="top" width="32%" >
									<html:select styleId="status" property="status" style="width:120;">
										<html:option value="ALL">All</html:option>
										<html:option value="CHECKEDOUT">Checked Out</html:option>
										<html:option value="PARSED">Parsed</html:option>
										<html:option value="FAILED">Failed</html:option>
										<html:option value="COLLECTED">Collected</html:option>
										<html:option value="INCOLLECTION">In Collection</html:option>
										<html:option value="INPARSING">In Parsing</html:option>
										<html:option value="PARSINGFAILED">Parsing Failed</html:option>
									</html:select>
								</td>
						  	</tr>
						  	<tr>
								<td align="left" class="labeltext" valign="top" width="10%">Reason</td>
								<td align="left" class="labeltext" valign="top" width="32%" ><html:text styleId="reason" property="reason" size="25"/></td>
						  	</tr>
						  	<tr>
								<td align="left" class="labeltext" valign="top" width="10%">User Name</td>
								<td align="left" class="labeltext" valign="top" width="32%">
									<html:select styleId="userName" property="userName" style="width:120;">
										<html:option value="Select">-- Select --</html:option>
										<html:option value="ADMINISTRATOR">Administrator</html:option>
									</html:select>
								</td>
						  	</tr>
						  	<tr>
								<td align="left" class="labeltext" valign="top" width="10%">&nbsp;</td>
								<td align="left" class="labeltext" valign="top" width="32%" >
									<input type="button" name="search" value=" Search " class="light-btn" onclick="validateCreate()"/> &nbsp;
									<input type="button" name="clear" value=" Reset " class="light-btn" onclick="clearData()"/>
								</td>
						  	</tr>
						  	
						  	<% if(request.getSession().getAttribute("batchList") != null) { %>
						  	<tr>
								<td colspan="2">&nbsp;</td>
						  	</tr>
				
						<tr>
						<td colspan="2">
						<table border="0" cellSpacing="0" cellPadding="0">
							<tr>
								<td align="left"  valign="top" width="2%" class="tblheader-bold"><input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll()"/></td>
								<td align="left"  class="tblheader" valign="top" width="6%">Sr. No.</td>
								<td align="left"  class="tblheader" valign="top" width="15%">Date and Time</td>
								<td align="left"  class="tblheader" valign="top" width="10%">Batch Id</td>
								<td align="left"  class="tblheader" valign="top" width="15%">Reason</td>
								<td align="left"  class="tblheader" valign="top" width="15%">Status</td>
								<td align="left"  class="tblheader" valign="top" width="10%">User</td>
								<td align="left"  class="tblheader" valign="top" width="5%">View</td>
							</tr>
							 
								<%
									List batchList = (List) request.getSession().getAttribute("batchList");
									
									if(batchList != null && batchList.size() > 0) {
										for(int index = 0;index < batchList.size();index++) {
											HashMap batchMap = (HashMap)batchList.get(index);

											String dateAndTime = ((Date)batchMap.get(MBeanNameConstant.DATE_AND_TIME)).toString();			
											String batchId = 	(String)batchMap.get(MBeanNameConstant.BATCH_ID);	
											String reason = 	(String)batchMap.get(MBeanNameConstant.REASON);	
											String status =     (String)batchMap.get(MBeanNameConstant.STATUS);
											String userName =   (String)batchMap.get(MBeanNameConstant.USER_NAME);
											
											%>	

							 				<tr>
							 					<td align="left" class="tblfirstcol"><input type="checkbox" name="select" value="<%=batchId%>"/></td>
												<td align="left" class="tblfirstcol"><%=(index+1)%></td>
												<td align="left" class="tblrows"><%=dateAndTime%></td>
								  				<td align="left" class="tblrows"><%=batchId%></td>
								  				<td align="left" class="tblrows"><%=reason%></td>
								  				<td align="left" class="tblrows"><%=status%></td>
								  				<td align="left" class="tblrows"><%=userName%></td>
								  				<td align="left" class="tblrows">
								  					<a href="" onclick="return popup('<%=basePath%>/viewBatchInfo.do?batchId=<%=batchId%>&netserverid=<bean:write name="searchBatchForm" property="serverId"/>','notes')">
								  						<img src="<%=basePath%>/images/view-info.jpg" border="0">
								  					</a>
								  				</td>
											</tr>
										<% } %>	
										
											<tr>
				  								<td colspan="8" align="center" class="tblfirstcol">
				  									<input type="button" name="checkInBatch_btn" value="Check In Batch" class="light-btn" onclick="checkInBatch()"/>
				  									<input type="button" name="undoCheckedOut_btn" value="Undo Checked Out" class="light-btn" onclick="undoCheckedOut()"/>
				  									<input type="button" name="updateBatch_btn" value="Update Batch" class="light-btn" onclick="updateBatch()"/>
				  								</td>
										</tr>
<%--										<tr>--%>
<%--											<td align="right" class="tblfirstcol" colspan="8"><a href="<%=basePath%>/searchFileSummary.do?">View Summary Information</a>&nbsp;</td>--%>
<%--										</tr>--%>
										
										
									<% } else {  %>	
										<tr>
											<td align="center" class="tblfirstcol" colspan="8">No Records Found...</td>
										</tr>
									<% } %>
							
						</table>
					</td>
				</tr>
			<% } %>	
		</table>
	</td>
</tr>
</table>		  
		  
</html:form>			
