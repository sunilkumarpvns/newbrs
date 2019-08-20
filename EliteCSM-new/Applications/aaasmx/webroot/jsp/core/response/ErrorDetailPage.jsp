<%@ include file="/jsp/core/includes/common/Header.jsp"%>


<%
	String localBasePath = request.getContextPath();
	Object elements[] = (Object[]) session.getAttribute("errorDetails");
	String errormsg=elements.toString();
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top" class="box" colspan="8">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="81%" background="<%=localBasePath%>/images/popup-bkgd.jpg" valign="top">
						&nbsp;
					</td>
					<td width="3%">
						<img src="<%=localBasePath%>/images/popup-curve.jpg">
					</td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg" width="3%">
						<a href="#"><img src="<%=localBasePath%>/images/refresh.jpg"
								name="Image1" onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image1','','<%=localBasePath%>/images/refresh-hover.jpg',1)"
								border="0">
						</a>
					</td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg" width="3%">
						<a href="#" onclick="window.print()"><img
								src="<%=localBasePath%>/images/print.jpg" name="Image2"
								onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image2','','<%=localBasePath%>/images/print-hover.jpg',1)"
								border="0"> </a>
					</td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg" width="3%">
						<a href="#"><img src="<%=localBasePath%>/images/aboutus.jpg"
								name="Image3" onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image3','','<%=localBasePath%>/images/aboutus-hover.jpg',1)"
								border="0">
						</a>
					</td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg" width="3%">
						<a href="#"><img src="<%=localBasePath%>/images/help.jpg"
								name="Image4" onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image4','','<%=localBasePath%>/images/help-hover.jpg',1)"
								border="0">
						</a>
					</td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg"
						width="4%">
						&nbsp;
					</td>
				</tr>
				<tr>
					<td align="left" class="small-gap" >
						&nbsp;
					</td>
				</tr>
				<tr>
					<td align="left" class="small-gap" >
						&nbsp;
					</td>
				</tr>
				<tr>
					<td colspan="8">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td width="38%" class="tblheader-bold" valign="bottom">
									Error Details
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td align="left" class="small-gap" >
						&nbsp;
					</td>
				</tr>
				<tr>
					<td align="left" class="small-gap" >
						&nbsp;
					</td>
				</tr>

				<tr>
					<td align="left" class="small-gap" >
						&nbsp;
					</td>
				</tr>
				<%
					if (elements != null && elements.length > 0) {
						for (int i = 0; i < elements.length; i++) {
				%>
				<tr>

					<td align="left" class="labeltext" style="padding-left:30px"><%=elements[i].toString()%></td>
				</tr>
				<%
					}
					} else {
				%>

				<tr>
					<td class="labeltext">
						No details exist.
					</td>
				</tr>
				<%
					}
				%>
				<tr>
					<td align="left" class="small-gap" >
						&nbsp;
					</td>
				</tr>
				<tr>
					<td align="left" class="small-gap" >
						&nbsp;
					</td>
				</tr>
				<tr>
					<td align="left" class="small-gap" >
						&nbsp;
					</td>
				</tr>
  				<tr>
					<td bgcolor="#00477F" class="small-gap" width="99%">
						&nbsp;
					</td>
					<td class="small-gap" width="1%">
						<img src="<%=localBasePath%>/images/pbtm-line-end.jpg">
					</td>
				</tr>
			</table>
			<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
