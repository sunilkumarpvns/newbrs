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
										bundle="radiusResources" key="accesspolicy.csvformat" /></td>
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

						<table width="100%" border="0" cellspacing="0" cellpadding="0"
							class="box">
							<tr>
								<td height="17" colspan="2" class="labeltext">&nbsp;</td>
							</tr>
							<tr>
								<td colspan="2">
									<table width="100%" border="0" cellspacing=0 cellpadding=0>
										<tr bgcolor="#FFFFFF">
											<td class="tblheader" width="10%"><bean:message
													key="general.serialnumber" /></td>
											<td width="25%" class="tblheader"><bean:message
													bundle="radiusResources" key="accesspolicy.day" /></td>
											<td width="20%" class="tblheader"><bean:message
													bundle="radiusResources" key="accesspoilcy.starttime" /></td>
											<td width="20%" class="tblheader"><bean:message
													bundle="radiusResources" key="accesspolicy.endtime" /></td>
											<td width="15%" class="tblheader"><bean:message
													bundle="radiusResources" key="accesspolicy.access" /></td>
										</tr>

										<tr bgcolor="#FFFFFF">
											<td class="tblrows">1</td>
											<td class="tblrows">Monday</td>
											<td class="tblrows">6:00</td>
											<td class="tblrows">22:00</td>
											<td class="tblrows">Denied</td>
										</tr>
										<tr bgcolor="#FFFFFF">
											<td class="tblrows">2</td>
											<td class="tblrows">Tuesday</td>
											<td class="tblrows">6:00</td>
											<td class="tblrows">22:00</td>
											<td class="tblrows">Denied</td>
										</tr>
										<tr bgcolor="#FFFFFF">
											<td class="tblrows">3</td>
											<td class="tblrows">Wednesday</td>
											<td class="tblrows">6:00</td>
											<td class="tblrows">22:00</td>
											<td class="tblrows">Denied</td>
										</tr>
										<tr bgcolor="#FFFFFF">
											<td class="tblrows">4</td>
											<td class="tblrows">Thursday</td>
											<td class="tblrows">6:00</td>
											<td class="tblrows">22:00</td>
											<td class="tblrows">Denied</td>
										</tr>
										<tr bgcolor="#FFFFFF">
											<td class="tblrows">5</td>
											<td class="tblrows">Friday</td>
											<td class="tblrows">6:00</td>
											<td class="tblrows">22:00</td>
											<td class="tblrows">Denied</td>
										</tr>
										<tr bgcolor="#FFFFFF">
											<td class="tblrows">6</td>
											<td class="tblrows">Saturday</td>
											<td class="tblrows">2:00</td>
											<td class="tblrows">3:00</td>
											<td class="tblrows">Denied</td>
										</tr>
										<tr bgcolor="#FFFFFF">
											<td class="tblrows">7</td>
											<td class="tblrows">Sunday</td>
											<td class="tblrows">2.00</td>
											<td class="tblrows">3:00</td>
											<td class="tblrows">Denied</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td colspan="2">&nbsp;</td>
							</tr>
							<tr>
								<td width="42%">&nbsp;</td>
								<td width="58%" class="btn-td"><input type="button"
									name="c_btnClose" value="Close" class="light-btn"
									onClick="window.close();"></td>
							</tr>
						</table>
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

