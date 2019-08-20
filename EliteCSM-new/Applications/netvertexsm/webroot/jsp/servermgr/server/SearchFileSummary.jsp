<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.HashMap" />
<jsp:directive.page import="java.util.Date"/>
<jsp:directive.page import="com.elitecore.netvertexsm.util.constants.MBeanNameConstant"/>







<script>
	function submitPage() {
		document.forms[0].checkAction.value = 'SearchSummary';
		document.forms[0].submit();
	}
</script>

<html:form action="/searchFileSummary">
	<html:hidden styleId="checkAction" property="checkAction" name="searchFileSummaryForm"/>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top" align="right">
				<table width="97%" border="0" cellspacing="0" cellpadding="0"
					height="15%">

					<tr>
						<td>
							&nbsp;
						</td>
					</tr>

					<tr>
						<td class="tblheader-bold" colspan="3">
							CDR File Search Summary
						</td>
					</tr>

					<tr>
						<td colspan="3">
							&nbsp;
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" width="10%">
							<strong>Group Field&nbsp;:&nbsp;</strong>
							<html:select styleId="groupField" property="groupField" name="searchFileSummaryForm">
								<html:option value="Date">&nbsp;Date&nbsp;</html:option>
								<html:option value="DeviceID">&nbsp;Device ID&nbsp;</html:option>
							</html:select>
							
						</td>
						<td align="left" class="labeltext" valign="top" width="18%">&nbsp;
						</td>
					</tr>
					
					<tr>
						<td colspan="3">
							&nbsp;
						</td>
					</tr>
					
					<tr>
						<td align="left" width="10%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<input type="button" name="search" value="Search" onclick="submitPage()" class="light-btn"/>
						</td>
						<td>&nbsp;</td>
					</tr>
					
					<tr>
						<td colspan="3">
							&nbsp;
						</td>
					</tr>
					
					
					<% if(request.getAttribute("fileSummaryList") != null) { %>
					<tr>
						<td colspan="2">
							<table border="1" cellSpacing="0" cellPadding="0">
								<tr>
									<td align="left" class="tblheader" valign="top" width="8%">
										Sr. No.
									</td>
									<% if(request.getAttribute("groupField").equals("Date")) { %>
									<td align="left" class="tblheader" valign="top" width="8%">
										Date
									</td>
									<% } %>
									<% if(request.getAttribute("groupField").equals("DeviceID")) { %>
									<td align="left" class="tblheader" valign="top" width="8%">
										DeviceID
									</td>
									<% } %>
									<td align="left" class="tblheader" valign="top" width="8%">
										Failed
									</td>
									
									<td align="left" class="tblheader" valign="top" width="8%">
										Collected
									</td>
									
									<td align="left" class="tblheader" valign="top" width="12%">
										In Collection
									</td>
									<td align="left" class="tblheader" valign="top" width="15%">
										In Parsing
									</td>
									<td align="left" class="tblheader" valign="top" width="10%">
										Parsed
									</td>
									<td align="left" class="tblheader" valign="top" width="15%">
										Parsing Failed
									</td>
								</tr>

								<%
									List fileSummaryList = (List) request.getAttribute("fileSummaryList");
									
									if(fileSummaryList != null && fileSummaryList.size() > 0) {
									
										for(int index = 0;index < fileSummaryList.size();index++) {
											HashMap fileSummaryMap = (HashMap)fileSummaryList.get(index);
											
											String date = "";
											if(request.getAttribute("groupField").equals("Date")) {
												date = ((Date)fileSummaryMap.get(MBeanNameConstant.DATE)).toString();
											}
											
											String DeviceID = "";	
											if(request.getAttribute("groupField").equals("DeviceID")) {
												DeviceID = (String)fileSummaryMap.get(MBeanNameConstant.DEVICE_ID);
											}
											
											int failed = ((Integer)fileSummaryMap.get(MBeanNameConstant.FAILED)).intValue();	
											int collected = ((Integer)fileSummaryMap.get(MBeanNameConstant.COLLECTED)).intValue();	
											int inCollection  = ((Integer)fileSummaryMap.get(MBeanNameConstant.INCOLLECTION)).intValue();	
											int inParsing = ((Integer)fileSummaryMap.get(MBeanNameConstant.INPARSING)).intValue();	
											int parsed = ((Integer)fileSummaryMap.get(MBeanNameConstant.PARSED)).intValue();	
											int parsingFailed = ((Integer)fileSummaryMap.get(MBeanNameConstant.PARSINGFAILED)).intValue();												
								%>	
								<tr>
									<td align="left" class="tblfirstcol">
										<%=(index+1)%>
									</td>
									<%if(request.getAttribute("groupField").equals("Date")) { %>
									<td align="left" class="tblrows">
										<%=date%>
									</td>
									<%} %>
									<%if(request.getAttribute("groupField").equals("DeviceID")) { %>
									<td align="left" class="tblrows">
										<%=DeviceID%>
									</td>
									<%} %>
									<td align="left" class="tblrows">
										<%=failed%>
									</td>
									<td align="left" class="tblrows">
										<%=collected%>
									</td>
									<td align="left" class="tblrows">
										<%=inCollection%>
									</td>
									<td align="left" class="tblrows">
										<%=inParsing%>
									</td>
									<td align="left" class="tblrows">
										<%=parsed%>
									</td>
									<td align="left" class="tblrows">
										<%=parsingFailed%>
									</td>
									
								</tr>
								<% } %>

								<% } else {  %>
								<tr>
									<td align="center" class="tblrows" colspan="8">
										No Records Found...
									</td>
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
