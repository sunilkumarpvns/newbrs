<%@ page import="com.opensymphony.xwork2.*" %>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
 response.setDateHeader ("Expires", 0);
    response.setHeader ("Pragma", "no-cache");
    if (request.getProtocol().equals ("HTTP/1.1")) {
       response.setHeader ("Cache-Control", "no-cache");
    }
 SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy kk:mm:ss");

%>
<html>
	<head>
		<LINK href="<%=request.getContextPath()%>/css/portal_style.css" rel=stylesheet>
	</head>
	<table width="100%">
		<tr>
			<td align="left">
				<table align="left" width="96%" border="0" cellpadding="0"
					cellspacing="0" class="Notes">
					<tr>
						<td>
							Displayed result for Duration From&nbsp;
							<b><s:property value="c_dateCreateFrom"/></b>&nbsp;To&nbsp;
							<b><s:property value="c_dateCreateTo"/></b>
						</td>

					</tr>
				</table>
			</td>
		</tr>

		<tr>
			<td align="right" colspan="2">
			</td>
		</tr>
		<tr>
			<td height="20%" colspan="3" valign="middle" width="100%">

				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					class="table-rows-col" >
					<tr>
						<td>
							<table border="0" cellspacing="2" cellpadding="0" class="table-rows-col" width="100%">
								<tr>
									<td width="6%" class="TableHeader" align="center">
										<b>Sr. No.</b>
									</td>
							
									<td width="15%" class="TableHeader" align="center">
										<b>Start Time</b>
									</td>
								
									<td width="15%" class="TableHeader" align="center">
										<b>End Time</b>
									</td>

									<td width="12%" class="TableHeader" align="center">
										<b>Duration (HH:MM:SS)</b>
									</td>

									<td width="14%" class="TableHeader" align="center">
										<b>Uploaded Data (In KB)</b>
									</td>

									<td width="14%" class="TableHeader" align="center">
										<b>Downloaded Data (In KB)</b>
									</td>

									<td width="12%" class="TableHeader" align="center">
										<b>Total Data (In KB)</b>
									</td>
									<td width="8%" class="TableHeader" align="center">
										<b>INR</b>
									</td>
								</tr>
								
								<% 
								ResultSet rs = (ResultSet)request.getAttribute("searchRs");
								int i = (((Integer)request.getAttribute("page")).intValue() * 10) + 1;
								while(rs.next())
								{ 
								%>
									<tr>
									<td class="TableRows" align="center">
										<%=i++ %>
									</td>
									<td class="TableRows" align="center">
										<%=  (rs.getTimestamp(1) == null)?"-": sdf.format(rs.getTimestamp(1))%>
									</td>
									<td class="TableRows" align="center">
										<%=  (rs.getTimestamp(2) == null)?"-": sdf.format(rs.getTimestamp(2)) %>
									</td>
									<td class="TableRows" align="center">
										<%=  (rs.getString(3) == null)?"-": rs.getString(3) %>
									</td>
									<td class="TableRows" align="center">
										<%=(rs.getLong(4)/1024) %>
									</td>
									<td class="TableRows" align="center">
										<%=(rs.getLong(5)/1024) %>
									</td>
									<td class="TableRows" align="center">
										<%=(rs.getLong(6)/1024) %>
									</td>
									<td class="TableRows" align="center">
										<%=(rs.getDouble(7)/100) %>
									</td>
									</tr>
								<%
								} 
								if (i == 1)
								{
								%> 
									<tr>
									<td width="23%" align="center" colspan="9">
										<FONT color=#cc0000 size=2>No Records Found</FONT>
									</td>
									</tr>
								<%
								}
								%>
								
							</table>
						</td>
					</tr>
				</table>

			</td>
		</tr>

		<tr>
			<td>
				&nbsp;
			</td>
		</tr>
	</table>
</html>