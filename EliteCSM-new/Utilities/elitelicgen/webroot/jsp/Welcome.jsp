<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>

<%
String path = request.getContextPath();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Elite License Generator</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="<%=path%>/css/mllnstyles.css" rel="stylesheet" type="text/css">
<link href="../css/style.css" rel="stylesheet" type="text/css">
<link href="../css/mllnstyles.css" rel="stylesheet" type="text/css">

</head>

<body>
<table width="100%" border="0" height="53">
 <tr>
    <td height="100">&nbsp;</td>
  </tr>
</table>
<table width="100%" height="100%">
<tr>
<td width="30%"></td>
<td>
	<table width="100%"   border="1" align="center" cellpadding="0" cellspacing="0">
		<form action="<%=path%>/login.do" method="post">
		
	      <tr>
            <td height="16%" colspan="4"  class="tblheader-bold">Welcome To Elite License Generator</td>
          </tr>
		  <tr>
            <td height="19%" colspan="4" class="tblrows">&nbsp;</td>
          </tr>
          <tr>
            <td width="7%" height="25%" align="center" class="tblcol" >&nbsp;</td>
			<td width="15%" height="25%"  class="tblcol">User Name : </td>
			<td width="57%" height="25%"  class="tblcol" ><input name="userName" type="text" class="textbox"></input></td>
			<td width="7%" height="25%"  class="tblcol-right">&nbsp;</td>
		 </tr>
		 <tr>
            <td width="7%" height="25%" align="center" class="tblcol" >&nbsp;</td>
			<td width="15%" height="25%" class="tblcol">Password : </td>
			<td width="57%" height="25%"  class="tblcol" ><input name="password" type="password" class="textbox"></input></td>
			<td width="7%" height="25%"  class="tblcol-right">&nbsp;</td>
		 </tr>
		 <tr>
            <td height="25%" colspan="4" align="center" class="tblrows">&nbsp;</td>
          </tr>
		  <tr>
            <td height="25%" colspan="4" align="center" class="tblrows"><input type="submit" name="loign" value="   login   " class="large_button"></input></td>
		  </tr>
		   <tr>
            <td height="25%" colspan="4" align="center" class="tblrows">&nbsp;</td>
          </tr>
		  <tr>
            <td height="25%" colspan="4" align="center" class="tblrows">&nbsp;<font class="error"><html:errors/></font></td>
          </tr>
		</form>
   </table> 
</td>
<td width="30%"></td>
</tr> 
</table>
<table width="100%" border="0" height="53">
 <tr>
    <td height="53">&nbsp;</td>
  </tr>
</table>
</body>
</html>
