<%@ page import="java.util.List"%>

<%@ include file="/jsp/core/includes/common/Header.jsp"%>


<%
    List lstHelpFiles = (List)request.getAttribute("lstHelpFiles");
   
%>


<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<tr>
		<td width="7">&nbsp;</td>
		<td cellpadding="0" class="box" cellspacing="0" border="0" width="100%"
			valign="top">

		<table cellpadding="0" cellspacing="0" border="0" width="100%">
			<tr>
				<td class="table-header" colspan="5">HELP</td>
			</tr>
			<tr>
				<td align="left" class="labeltext" valign="top" colspan="5">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="5">
				<table width="97%" name="c_tblCrossProductList"
					id="c_tblCrossProductList" align="right" border="0">
					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>
			
					<tr>
						<td colspan="3">
						<table width="60%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="right" class="tblheader" valign="top" width="5%">
								Sr.No.</td>
								<td align="left" class="tblheader" valign="top">
								Help Files</td>
							</tr>

							<logic:notEmpty name="lstHelpFiles">
								<% int iIndex = 0; %>
								<logic:iterate id="lstHelp" name="lstHelpFiles" type="java.lang.String">
									<%iIndex++;%>
								
									<tr>
										<td align="left" class="tblfirstcol"><%=iIndex%></td>
										<td align="left" class="tblrows">
											<a href="<%=basePath%>/servlet/DownloadFileServlet?filetype=help&filename=<%=lstHelp%>"><%=lstHelp%></a>
										</td>
									</tr>
								</logic:iterate>
							</logic:notEmpty>
							<logic:empty name="lstHelpFiles">
									<tr>
										<td align="center" class="tblfirstcol" colspan="2">No files found.</td>
									</tr>
							</logic:empty>
						</table>
						</td>
					</tr>
					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>

		</td>
	</tr>
	<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>
