<%@ include file="/jsp/core/includes/common/Header.jsp"%>





<%
    String basePath = request.getContextPath();
%>
<script>
	
	function  validateInsert(fileValue)
	{
	var field = window.opener.document.forms[0].fileName;
	field.value = fileValue;
	window.close();
	}
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>

		<td colspan="2">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">

				<tr>
					<td width="81%" background="<%=basePath%>/images/popup-bkgd.jpg"
						valign="top">&nbsp;</td>
					<td width="3%"><img src="<%=basePath%>/images/popup-curve.jpg"></td>
					<td background="<%=basePath%>/images/popup-btn-bkgd.jpg" width="3%"><a
						href="#"><img src="<%=basePath%>/images/refresh.jpg"
							name="Image1" onMouseOut="MM_swapImgRestore()"
							onMouseOver="MM_swapImage('Image1','','<%=basePath%>/images/refresh-hover.jpg',1)"
							border="0"></a></td>
					<td background="<%=basePath%>/images/popup-btn-bkgd.jpg" width="3%"><a
						href="#" onclick="window.print()"><img
							src="<%=basePath%>/images/print.jpg" name="Image2"
							onMouseOut="MM_swapImgRestore()"
							onMouseOver="MM_swapImage('Image2','','<%=basePath%>/images/print-hover.jpg',1)"
							border="0"> </a></td>
					<td background="<%=basePath%>/images/popup-btn-bkgd.jpg" width="3%"><a
						href="#"><img src="<%=basePath%>/images/aboutus.jpg"
							name="Image3" onMouseOut="MM_swapImgRestore()"
							onMouseOver="MM_swapImage('Image3','','<%=basePath%>/images/aboutus-hover.jpg',1)"
							border="0"></a></td>
					<td background="<%=basePath%>/images/popup-btn-bkgd.jpg" width="3%"><a
						href="#"><img src="<%=basePath%>/images/help.jpg"
							name="Image4" onMouseOut="MM_swapImgRestore()"
							onMouseOver="MM_swapImage('Image4','','<%=basePath%>/images/help-hover.jpg',1)"
							border="0"></a></td>
					<td background="<%=basePath%>/images/popup-btn-bkgd.jpg" width="4%">&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<table name="MainTable" id="MainTable" cellSpacing="0" cellPadding="0"
	width="100%" border="0">

	<tr>
		<td width="10">&nbsp;</td>
		<td width="100%" colspan="3" valign="top" class="box">
			<table cellSpacing="0" cellPadding="0" width="100%" border="0">

				<tr>
					<td class="table-header" valign="bottom" colspan="3"><bean:message
							bundle="servermgrResources"
							key="servermgr.fileinformation.searchFileInformation" /></td>
				</tr>
				<tr>
					<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
				</tr>

				<tr>
					<td>
						<form action="#" name="seachErrorProcessingPopup"></form>
						<table align="right" width="97%" border="0" cellspacing="0"
							cellpadding="0">
							<tr>
								<td class="tblheader-bold" valign="bottom" colspan="4">File
									List</td>
							</tr>

							<logic:notEmpty name="errorProcessingFileList" scope="request">
								<tr>
									<td class="tblheader-bold" valign="bottom">No.</td>
									<td class="tblheader-bold" valign="bottom">&nbsp;&nbsp;File
										Name</td>
									<td class="tblheader-bold" valign="bottom" align="center">Success</td>
									<td class="tblheader-bold" valign="bottom" align="center">Failure</td>
								</tr>
							</logic:notEmpty>

							<logic:iterate id="keyValueBean" indexId="i"
								name="errorProcessingFileList" scope="request">
								<tr>
									<td class="tblfirstcol" width="5%"><%=i+1%></td>
									<td class="tblrows">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a
										href="#"
										onclick="validateInsert('<bean:write name="keyValueBean" property="value"/>')"><bean:write
												name="keyValueBean" property="value" /></a>
									</td>
									<td class="tblrows" align="center"><bean:write
											name="keyValueBean" property="filed1" /></td>
									<td class="tblcol" align="center"><bean:write
											name="keyValueBean" property="filed2" /></td>
								</tr>
							</logic:iterate>

							<logic:empty name="errorProcessingFileList" scope="request">
								<tr>
									<td class="tblfirstcol" colspan="4" width="5%">No records
										Found.</td>
								</tr>
							</logic:empty>

						</table>
				<tr>
					<td class="small-gap"></td>
				</tr>
				</form>
				</td>
				</tr>
			</table>
		</td>
	</tr>
	</td>
	</tr>
</table>
<table cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td colspan="2" class="small-gap">&nbsp;</td>
	</tr>

	<tr>
		<td bgcolor="#00477F" class="small-gap" width="99%">&nbsp;</td>
		<td class="small-gap" width="1%"><img
			src="<%=basePath%>/images/pbtm-line-end.jpg"></td>
	</tr>
</table>



