<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="java.util.HashMap" />
<jsp:directive.page import="java.util.Date" />
<jsp:directive.page
	import="com.elitecore.elitesm.util.constants.MBeanNameConstant" />





<script>
	function submitPage() {
		document.forms[0].checkAction.value = 'SearchSummary';
		document.forms[0].submit();
	}
</script>

<html:form action="/searchCDRSummary">
	<html:hidden styleId="checkAction" property="checkAction"
		name="searchCDRSummaryForm" />
	<html:hidden styleId="serverId" property="serverId"
		name="searchCDRSummaryForm" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					height="15%">

					<tr>
						<td>&nbsp;</td>
					</tr>

					<tr>
						<td class="tblheader-bold" colspan="3">CDR Search Summary</td>
					</tr>

					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>

					<tr>
						<td align="left" class="labeltext" width="10%"><strong>Group
								Field Name&nbsp;:&nbsp;</strong> <html:select styleId="groupField"
								property="groupField" name="searchCDRSummaryForm">
								<html:option value="Date">&nbsp;Date&nbsp;</html:option>
								<html:option value="DeviceID">&nbsp;Device ID&nbsp;</html:option>
							</html:select></td>
						<td align="left" class="labeltext" valign="top" width="18%">&nbsp;</td>
					</tr>

					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>

					<tr>
						<td align="left" width="10%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<input type="button" name="search" value="Search"
							onclick="submitPage()" class="light-btn" />
						</td>
						<td>&nbsp;</td>
					</tr>

					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>

					<% if(request.getAttribute("cdrSummaryDataList") != null) { %>
					<tr>
						<td colspan="3">
							<table border="1" cellSpacing="0" cellPadding="0">
								<tr>
									<td align="left" class="tblheader" valign="top" width="4%">
										Sr.No.</td>

									<%if(request.getAttribute("groupField").equals("Date")) { %>
									<td align="left" class="tblheader" valign="top" width="3%">
										Date</td>
									<% } %>

									<%if(request.getAttribute("groupField").equals("DeviceID")) { %>
									<td align="left" class="tblheader" valign="top" width="3%">
										Device</td>
									<% } %>

									<td align="left" class="tblheader" valign="top" width="5%">
										In Parsing</td>
									<td align="left" class="tblheader" valign="top" width="5%">
										Parsing Failed</td>

									<td align="left" class="tblheader" valign="top" width="5%">
										Parsed</td>

									<td align="left" class="tblheader" valign="top" width="5%">
										In Processing</td>
									<td align="left" class="tblheader" valign="top" width="5%">
										Processing Failed</td>
									<td align="left" class="tblheader" valign="top" width="5%">
										Filtered</td>
									<td align="left" class="tblheader" valign="top" width="5%">
										Processed</td>
									<td align="left" class="tblheader" valign="top" width="5%">
										Distributed</td>
									<td align="left" class="tblheader" valign="top" width="5%">
										Distribution Failed</td>
									<td align="left" class="tblheader" valign="top" width="5%">
										New</td>
									<td align="left" class="tblheader" valign="top" width="5%">
										Updated</td>
									<td align="left" class="tblheader" valign="top" width="5%">
										Checked Out</td>
								</tr>

								<%
									List fileSummaryList = (List) request.getAttribute("cdrSummaryDataList");

									if(fileSummaryList != null && fileSummaryList.size() > 0) {
									
										for(int index = 0;index < fileSummaryList.size();index++) {
											HashMap fileSummaryMap = (HashMap)fileSummaryList.get(index);
										
											String date = "";
											if(request.getAttribute("groupField").equals("Date")) {
												date = ((Date)fileSummaryMap.get(MBeanNameConstant.DATE)).toString();
											}
											
											String deviceId = "";	
											if(request.getAttribute("groupField").equals("DeviceID")) {
												deviceId = (String)fileSummaryMap.get(MBeanNameConstant.DEVICE_ID);
											}
											
											int inParsing = ((Integer)fileSummaryMap.get(MBeanNameConstant.INPARSING)).intValue();	
											int parsingFailed = ((Integer)fileSummaryMap.get(MBeanNameConstant.PARSINGFAILED)).intValue();	
											int parsed = ((Integer)fileSummaryMap.get(MBeanNameConstant.PARSED)).intValue();	
											int inProcessing = ((Integer)fileSummaryMap.get(MBeanNameConstant.INPROCESSING)).intValue();	
											int processingFailed = ((Integer)fileSummaryMap.get(MBeanNameConstant.PROCESSINGFAILED)).intValue();	
											int filtered = ((Integer)fileSummaryMap.get(MBeanNameConstant.FILTERED)).intValue();	
											int processed = ((Integer)fileSummaryMap.get(MBeanNameConstant.PROCESSED)).intValue();	
											int distributed  = ((Integer)fileSummaryMap.get(MBeanNameConstant.DISTRIBUTED)).intValue();	
											int distributionFailed = ((Integer)fileSummaryMap.get(MBeanNameConstant.DISTRIBUTEDFAILED)).intValue();	
											int New = ((Integer)fileSummaryMap.get(MBeanNameConstant.NEW)).intValue();	
											int updated = ((Integer)fileSummaryMap.get(MBeanNameConstant.UPDATED)).intValue();												
											int checkedOut = ((Integer)fileSummaryMap.get(MBeanNameConstant.CHECKEDOUT)).intValue();												
								%>
								<tr>
									<td align="left" class="tblfirstcol"><%=(index+1)%></td>

									<%if(request.getAttribute("groupField").equals("Date")) { %>
									<td align="left" class="tblrows"><%=date%></td>
									<% } %>

									<%if(request.getAttribute("groupField").equals("DeviceID")) { %>
									<td align="left" class="tblrows"><%=deviceId%></td>
									<% } %>

									<td align="left" class="tblrows"><%=inParsing%></td>
									<td align="left" class="tblrows"><%=parsingFailed%></td>
									<td align="left" class="tblrows"><%=parsed%></td>
									<td align="left" class="tblrows"><%=inProcessing%></td>
									<td align="left" class="tblrows"><%=processingFailed%></td>
									<td align="left" class="tblrows"><%=filtered%></td>
									<td align="left" class="tblrows"><%=processed%></td>
									<td align="left" class="tblrows"><%=distributed%></td>
									<td align="left" class="tblrows"><%=distributionFailed%></td>
									<td align="left" class="tblrows"><%=New%></td>
									<td align="left" class="tblrows"><%=updated%></td>
									<td align="left" class="tblrows"><%=checkedOut%></td>

								</tr>
								<% } %>

								<% } else {  %>
								<tr>
									<td align="center" class="tblrows" colspan="14">No Records
										Found...</td>
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
