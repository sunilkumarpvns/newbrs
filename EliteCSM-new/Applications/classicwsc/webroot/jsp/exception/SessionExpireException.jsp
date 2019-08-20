<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html >
  <head>
  	<LINK href="<%=request.getContextPath()%>/css/portal_style.css" rel=stylesheet>
    <base href="<%=basePath%>">
    
    <title>My JSP 'Exception.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script>
	function login()
	{
		top.location.href="<%=request.getContextPath() %>/"+"Login.action";
	}
	</script>

  </head>
  
  <body >
  <table class="Box" cellSpacing="0" cellPadding="0" width="90%" height="90%"	align="center" border="0">
  <tbody>
  	<tr><td align="center">
    Session Expire... <br>
    
   <a name="aa" href="javascript:login()">Click here to ReLogin</a>
   </td></tr>
   </tbody>
	</table>
   </body>
</html>
