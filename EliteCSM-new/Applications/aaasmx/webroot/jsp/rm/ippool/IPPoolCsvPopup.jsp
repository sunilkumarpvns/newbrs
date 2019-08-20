<%@ include file="/jsp/core/includes/common/Header.jsp"%>


<% 
	String localBasePath = request.getContextPath();
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top" class="box" colspan="8">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="81%"
						background="<%=localBasePath%>/images/popup-bkgd.jpg" valign="top">&nbsp;
					</td>
					<td width="3%"><img
						src="<%=localBasePath%>/images/popup-curve.jpg"></td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg"
						width="3%"><a href="#"><img
							src="<%=localBasePath%>/images/refresh.jpg" name="Image1"
							onMouseOut="MM_swapImgRestore()"
							onMouseOver="MM_swapImage('Image1','','<%=localBasePath%>/images/refresh-hover.jpg',1)"
							border="0"></a></td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg"
						width="3%"><a href="#" onclick="window.print()"><img
							src="<%=localBasePath%>/images/print.jpg" name="Image2"
							onMouseOut="MM_swapImgRestore()"
							onMouseOver="MM_swapImage('Image2','','<%=localBasePath%>/images/print-hover.jpg',1)"
							border="0"> </a></td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg"
						width="3%"><a href="#"><img
							src="<%=localBasePath%>/images/aboutus.jpg" name="Image3"
							onMouseOut="MM_swapImgRestore()"
							onMouseOver="MM_swapImage('Image3','','<%=localBasePath%>/images/aboutus-hover.jpg',1)"
							border="0"></a></td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg"
						width="3%"><a href="#"><img
							src="<%=localBasePath%>/images/help.jpg" name="Image4"
							onMouseOut="MM_swapImgRestore()"
							onMouseOver="MM_swapImage('Image4','','<%=localBasePath%>/images/help-hover.jpg',1)"
							border="0"></a></td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg"
						width="4%">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="8">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>

								<td width="38%" class="blue-text-bold" valign="bottom"><bean:message
										bundle="ippoolResources" key="ippool.csvformat" /></td>
								<td class="blue-text-bold" width="43%"><a href="#"><img
										src="<%=localBasePath%>/images/pdf.jpg" name="Image21"
										onMouseOver="MM_swapImage('Image21','','<%=localBasePath%>/images/pdf-hover.jpg',1)"
										border="0" alt="Save as PDF"></a><a href="#"><img
										src="<%=localBasePath%>/images/html.jpg" name="Image31"
										onMouseOver="MM_swapImage('Image31','','<%=localBasePath%>/images/html-hover.jpg',1)"
										border="0" alt="Save as HTML"></a></td>
								<td width="3%" class="blue-text-bold">&nbsp;</td>
								<td width="3%" class="blue-text-bold">&nbsp;</td>
								<td width="3%" class="blue-text-bold">&nbsp;</td>
								<td width="3%" class="blue-text-bold">&nbsp;</td>
								<td width="3%" class="blue-text-bold">&nbsp;</td>
								<td width="4%" class="blue-text-bold">&nbsp;</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="8" class="small-gap">&nbsp;</td>
				</tr>
				<tr align="left">
					<td colspan="8" class="top-btmlines">
						<html:form action="/downloadCSVFormatFile">
							<html:hidden property="action" value="downloadFile"/>
						<table width="100%" border="0" cellspacing="0" cellpadding="0"
							class="box">
							<tr>
								<td height="17" colspan="2" class="labeltext">&nbsp;</td>
							</tr>
							<tr>
								<td colspan="2">
									<table width="100%" border="0" cellspacing=0 cellpadding=0>
										<tr bgcolor="#FFFFFF">
											<td class="tblheader" width="10%">
												<bean:message key="general.serialnumber" /></td>
											<td width="25%" class="tblheader">
												<bean:message bundle="ippoolResources" key="ippool.ipaddress" />
											</td>
										</tr>
										<logic:iterate id="csvFileData" name="csvFileData" indexId="lstIndex">
											<tr bgcolor="#FFFFFF">
												<td class="tblrows"><%=lstIndex+1 %></td>
												<td class="tblrows"><bean:write name="csvFileData"/> </td>
											</tr>
										</logic:iterate>	
									
									</table>
								</td>
							</tr>
							<tr>
								<td colspan="2">&nbsp;</td>
							</tr>
							<tr>
								<td width="25%">&nbsp;</td>
								<td  class="btn-td">
									<input type="button" name="c_btnClose" value="Close" class="light-btn" onClick="window.close();">
									<input type="submit" name="c_btnDownload" value="Download File" class="light-btn">
								</td>
							</tr>
						</table>
						</html:form>
					</td>
				</tr>
				<tr>
					<td colspan="8" class="small-gap">&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td bgcolor="#00477F" class="small-gap" width="99%">&nbsp;</td>
		<td class="small-gap" width="1%"><img
			src="<%=localBasePath%>/images/pbtm-line-end.jpg"></td>
	</tr>
</table>

