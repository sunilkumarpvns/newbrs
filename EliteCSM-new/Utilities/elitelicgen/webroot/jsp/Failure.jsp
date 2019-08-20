<%@ page contentType="text/html; charset=iso-8859-1" language="java"
	import="java.sql.*" errorPage=""%>
<jsp:directive.page import="org.apache.struts.util.RequestUtils" />
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic"
	prefix="logic"%>

<%
String path = request.getContextPath();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>Elite License Generator</title>
		<meta http-equiv="Content-Type"
			content="text/html; charset=iso-8859-1">
		<link href="<%=path%>/css/mllnstyles.css" rel="stylesheet"
			type="text/css">
		<link href="../css/style.css" rel="stylesheet" type="text/css">
		<link href="../css/mllnstyles.css" rel="stylesheet" type="text/css">

	</head>

	<body>
		<table width="100%" border="0" height="53">
			<tr>
				<td height="100">
					&nbsp;
				</td>
			</tr>
		</table>
		<table width="100%" height="100%">
			<tr>
				<td width="30%"></td>
				<td>
					<table width="100%" border="1" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td height="16%" colspan="4" class="tblheader-bold">Failure Reason</td>
						</tr>
						
						<tr>
							<td height="25%" colspan="4" align="center" class="tblrows">
								&nbsp;

								<font class="error">
								<html:errors /></font>
							</td>
						</tr>
						
						<tr><td>&nbsp;</td></tr>
						<tr>
							<td height="25%" colspan="4" align="center" class="tblrows">
								&nbsp;
							
								<input type ="button" value="Back" class="light-btn" onclick="javascript:history.go(-1);"/>
							</td>
						</tr>
						
					</table>
				</td>
				<td width="30%"></td>
			</tr>
		</table>
		<table width="100%" border="0" height="53">
			<tr>
				<td height="53">
					&nbsp;
				</td>
			</tr>
		</table>
	</body>
</html>
