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
		document.forms[0].filedName.value = "";
		document.forms[0].deviceId.value = "";
		document.forms[0].dateFrom.value = "";
		document.forms[0].dateTo.value = "";
		document.forms[0].reason.value = "";
		document.forms[0].location.value = "";
		document.forms[0].status.value = "ALL";
	}
	
	function validateCreate(){
		if(document.forms[0].dateFrom.value != null && document.forms[0].dateFrom.value != "" && document.forms[0].dateTo.value != null && document.forms[0].dateTo.value != "") {
			if (Date.parse(document.forms[0].dateFrom.value) > Date.parse(document.forms[0].dateTo.value)) {
				alert("Invalid Date Range!\nStart Date cannot be after End Date!");
				return false;
			}
		}
		document.forms[0].submit();
	}

</script>

<html:form action="/searchFile">	

<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
        
		  <tr>
		  	<td>&nbsp;</td>
		  </tr>
		  
		  <tr> 
			<td class="tblheader-bold" colspan="3">CDR File Search</td>
		  </tr>
		  
		  <tr>
		  	<td>&nbsp;</td>
		  </tr>
		  					<tr>
								<td align="left" class="labeltext" valign="top" width="10%"><strong>File Name</strong></td>
								<td align="left" class="labeltext" valign="top" width="18%" ><html:text styleId="filedName" property="filedName" size="30"/></td>
						  	</tr>
						  	<tr>
								<td align="left" class="labeltext" valign="top" width="10%"><strong>Device</strong></td>
								<td align="left" class="labeltext" valign="top" width="32%" ><html:text styleId="deviceId" property="deviceId" size="30"/></td>
						  	</tr>
						  	<tr>
								<td align="left" class="labeltext" valign="top" width="10%"><strong>Status</strong></td>
								<td align="left" class="labeltext" valign="top" width="32%" >
									<html:select styleId="status" property="status" style="width:150;">
										<html:option value="ALL">All</html:option>
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
								<td align="left" class="labeltext" valign="top" width="10%"><strong>Date</strong></td>
								<td align="left" class="labeltext" valign="top" width="32%" >
									<strong>From:</strong>&nbsp;<html:text styleId="dateFrom" property="dateFrom" size="15" maxlength="15" readonly="true"/>
									<a  href="javascript:void(0)"  onclick="popUpCalendar(this, document.forms[0].dateFrom)" >
										<img  src="<%=basePath%>/images/calendar.jpg" border="0" tabindex="6">
									</a> &nbsp;&nbsp;
									<strong>To:</strong>&nbsp;<html:text styleId="dateTo" property="dateTo" size="15" maxlength="15" readonly="true"/>
									<a  href="javascript:void(0)"  onclick="popUpCalendar(this, document.forms[0].dateTo)" >
										<img  src="<%=basePath%>/images/calendar.jpg" border="0" tabindex="6">
									</a>
								</td>
						  	</tr>
						  	
						  	<tr>
								<td align="left" class="labeltext" valign="top" width="10%"><strong>Reason</strong></td>
								<td align="left" class="labeltext" valign="top" width="32%" ><html:text styleId="reason" property="reason" size="30"/></td>
						  	</tr>
						  	<tr>
								<td align="left" class="labeltext" valign="top" width="10%"><strong>Location</strong></td>
								<td align="left" class="labeltext" valign="top" width="32%" ><html:text styleId="location" property="location" size="30"/></td>
						  	</tr>
						  	<tr>
								<td align="left" class="labeltext" valign="top" width="10%">&nbsp;</td>
								<td align="left" class="labeltext" valign="top" width="32%" >
									<input type="button" name="search" value=" Search " class="light-btn" onclick="validateCreate()"/> &nbsp;
									<input type="button" name="clear" value=" Reset " class="light-btn" onclick="clearData()"/>
								</td>
						  	</tr>
						  	
						  	<% if(request.getAttribute("fileList") != null) { %>
						  	<tr>
								<td colspan="2">&nbsp;</td>
						  	</tr>
				
						<tr>
						<td colspan="2">
						<table border="1" cellSpacing="0" cellPadding="0">
							<tr>
								<td align="left"  class="tblheader" valign="top" width="5%">Sr. No.</td>
								<td align="left"  class="tblheader" valign="top" width="8%">File Name</td>
								<td align="left"  class="tblheader" valign="top" width="8%">Device</td>
								<td align="left"  class="tblheader" valign="top" width="15%">Date and Time</td>
								<td align="left"  class="tblheader" valign="top" width="10%">State</td>
							</tr>
							 
								<%
									List fileList = (List) request.getAttribute("fileList");
									
									if(fileList != null && fileList.size() > 0) {
										for(int index = 0;index < fileList.size();index++) {
											HashMap fileMap = (HashMap)fileList.get(index);
											
											//String fileId = (String)fileMap.get(MBeanNameConstant.FILE_ID);	
											String fileName = 	(String)fileMap.get(MBeanNameConstant.FIELD_NAME);	
											String deviceId = 	(String)fileMap.get(MBeanNameConstant.DEVICE_ID);	
											String dateAndTime = ((Date)fileMap.get(MBeanNameConstant.DATE_AND_TIME)).toString();	
											String state = (String)fileMap.get(MBeanNameConstant.STATE);
											
											%>	

							 				<tr>
												<td align="left" class="tblfirstcol"><%=(index+1)%></td>
								  				<td align="left" class="tblrows"><a href="" onclick="return popup('<%=basePath%>/viewFile.do?fileName=<%=fileName%>','notes')"><%=fileName%></a></td>
								  				<td align="left" class="tblrows"><%=deviceId%></td>
								  				<td align="left" class="tblrows"><%=dateAndTime%></td>
								  				<td align="left" class="tblrows"><%=state%></td>
											</tr>
										<% } %>	
										
										<tr>
											<td align="right" class="tblrows" colspan="5"><a href="<%=basePath%>/searchFileSummary.do?">View Summary</a>&nbsp;</td>
										</tr>
										
									<% } else {  %>	
										<tr>
											<td align="center" class="tblrows" colspan="5">No Records Found...</td>
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
