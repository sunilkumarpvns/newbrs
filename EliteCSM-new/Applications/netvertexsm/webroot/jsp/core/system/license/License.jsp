<%@ page import="java.util.List"%>

<%@ include file="/jsp/core/includes/common/Header.jsp"%>


<%
    List lstLicenseFiles = (List)request.getAttribute("lstLicenseFiles");
  
%>


<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<tr>
		<td width="7">&nbsp;</td>
		<td cellpadding="0" class="box" cellspacing="0" border="0" width="100%"
			valign="top">

		<table cellpadding="0" cellspacing="0" border="0" width="100%">
			<tr>
				<td class="table-header" colspan="5">LICENSE INFORMATION</td>
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
								License File</td>
							</tr>

							<logic:notEmpty name="lstLicenseFiles">
								<% int iIndex = 0; %>
								<logic:iterate id="licFile" name="lstLicenseFiles" type="java.lang.String">
									<%iIndex++;	
									%>
								
									<tr>
										<td align="left" class="tblfirstcol"><%=iIndex%></td>
										<td align="left" class="tblrows">
											<a href="<%=basePath%>/servlet/DownloadFileServlet?filetype=license&filename=<%=licFile%>"><%=licFile%></a>
										</td>
									</tr>
								</logic:iterate>
							</logic:notEmpty>
							<logic:empty name="lstLicenseFiles">
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
						<td colspan="3" >
						<table width="60%">
						<tr>
						<td width="5%" valign="top"><img src="<%=basePath%>/images/tick.jpg"/> </td>
						<td class="labeltext">
							Sterlite uses certain open source components in NetVertex and use of the said open source components are subject
							to terms and condition of the respective open source license under which the same are made open source.
						</td>
						</tr>
						<tr>
						<td width="5%" valign="top"><img src="<%=basePath%>/images/tick.jpg"/> </td>
						<td class="labeltext">  
							Please visit the link <b><a href="http://www.opensource.org/licenses/alphabetical" target="licdetails">http://www.opensource.org/licenses/alphabetical</a></b> for the details of mentioned licenses.
						</td>
						</tr>
						</table>
					</tr>
					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="3">
						<table width="60%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="left" class="labeltext" valign="top">
									<img src="<%=basePath%>/images/gplv3.png"/>
									<img src="<%=basePath%>/images/lgplv3.png"/>
								</td>
							</tr>
						</table>
						</td>
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
