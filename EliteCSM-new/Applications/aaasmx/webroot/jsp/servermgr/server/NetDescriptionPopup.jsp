<%@ include file="/jsp/core/includes/common/Header.jsp"%>





<% 
	String localBasePath = request.getContextPath();
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top" class="box" colspan="8">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="*" background="<%=localBasePath%>/images/popup-bkgd.jpg"
						valign="top">&nbsp;</td>
					<td width="25"><img
						src="<%=localBasePath%>/images/popup-curve.jpg"></td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg"
						width="30"><a href="#"><img
							src="<%=localBasePath%>/images/refresh.jpg" name="Image1"
							onMouseOut="MM_swapImgRestore()"
							onMouseOver="MM_swapImage('Image1','','<%=localBasePath%>/images/refresh-hover.jpg',1)"
							border="0"></a></td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg"
						width="30"><a href="#" onclick="window.print()"><img
							src="<%=localBasePath%>/images/print.jpg" name="Image2"
							onMouseOut="MM_swapImgRestore()"
							onMouseOver="MM_swapImage('Image2','','<%=localBasePath%>/images/print-hover.jpg',1)"
							border="0"> </a></td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg"
						width="30"><a href="#"><img
							src="<%=localBasePath%>/images/aboutus.jpg" name="Image3"
							onMouseOut="MM_swapImgRestore()"
							onMouseOver="MM_swapImage('Image3','','<%=localBasePath%>/images/aboutus-hover.jpg',1)"
							border="0"></a></td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg"
						width="30"><a href="#"><img
							src="<%=localBasePath%>/images/help.jpg" name="Image4"
							onMouseOut="MM_swapImgRestore()"
							onMouseOver="MM_swapImage('Image4','','<%=localBasePath%>/images/help-hover.jpg',1)"
							border="0"></a></td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg"
						width="5">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="8">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>

								<td width="38%" class="blue-text-bold" valign="bottom">Description</td>
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
								
								<%String description=request.getParameter("description"); 
								if(description != null && ( description.toLowerCase().indexOf("@") >= 0) ) {
									String result = description.replaceAll("@","");	 
								%>
									<bean:message bundle="serverConfigResources" key="<%=result%>" /> 
								<% }else{%>
									<%=request.getParameter("description")%>
								<%}%>
								
								</td>
							</tr>
							<tr>
								<td colspan="2">&nbsp;</td>
							</tr>
							<tr>
								<td colspan="2">&nbsp;</td>
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
							<tr>
								<td colspan="2">&nbsp;</td>
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
</table>
<table cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td colspan="2" class="small-gap">&nbsp;</td>
	</tr>

	<tr>
		<td bgcolor="#00477F" class="small-gap" width="99%">&nbsp;</td>
		<td class="small-gap" width="1%"><img
			src="<%=localBasePath%>/images/pbtm-line-end.jpg"></td>
	</tr>
</table>
