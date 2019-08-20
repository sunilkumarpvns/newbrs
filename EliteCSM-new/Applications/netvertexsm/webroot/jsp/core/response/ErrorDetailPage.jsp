<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<%@ taglib uri="/WEB-INF/config/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/config/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/config/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/config/tlds/taglibs-input.tld" prefix="elitecore" %>
<%
	String localBasePath = request.getContextPath();
	Object elements[] = (Object[]) session.getAttribute("errorDetails");
	//session.removeAttribute("errorDetails");
%>

			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="*" background="<%=request.getContextPath()%>/images/popup-bkgd.jpg" valign="top">
						&nbsp;
					</td>
					<td width="20px">
						<img src="<%=request.getContextPath()%>/images/popup-curve.jpg">
					</td>
					<td background="<%=request.getContextPath()%>/images/popup-btn-bkgd.jpg" width="30px">
						<a href="#"><img src="<%=request.getContextPath()%>/images/refresh.jpg"
								name="Image1" onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image1','','<%=request.getContextPath()%>/images/refresh-hover.jpg',1)"
								border="0">
						</a>
					</td>
					<td background="<%=request.getContextPath()%>/images/popup-btn-bkgd.jpg" width="30px">
						<a href="#" onclick="window.print()"><img
								src="<%=request.getContextPath()%>/images/print.jpg" name="Image2"
								onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image2','','<%=request.getContextPath()%>/images/print-hover.jpg',1)"
								border="0"> </a>
					</td>
					<td background="<%=request.getContextPath()%>/images/popup-btn-bkgd.jpg" width="30px">
						<a href="#"><img src="<%=request.getContextPath()%>/images/aboutus.jpg"
								name="Image3" onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image3','','<%=request.getContextPath()%>/images/aboutus-hover.jpg',1)"
								border="0">
						</a>
					</td>
					<td background="<%=request.getContextPath()%>/images/popup-btn-bkgd.jpg" width="30px">
						<a href="#"><img src="<%=request.getContextPath()%>/images/help.jpg"
								name="Image4" onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image4','','<%=request.getContextPath()%>/images/help-hover.jpg',1)"
								border="0">
						</a>
					</td>
					<td background="<%=request.getContextPath()%>/images/popup-btn-bkgd.jpg" width="1%">
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
					<td bgcolor="#00477F" class="small-gap"  colspan="6">
						&nbsp;
					</td>
					<td class="small-gap" >
						<img src="<%=request.getContextPath()%>/images/pbtm-line-end.jpg">
					</td>
				</tr>
			</table>
			<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
