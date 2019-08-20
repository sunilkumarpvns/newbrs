<%@ page language="java" import="java.util.*,com.elite.config.*,com.elite.context.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.elite.user.Userbean"%>


<%
String sssspath = ((WSCConfig)((WSCContext)application.getAttribute("wsc_context")).getAttribute("wsc")).getSsss_path();
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Userbean user = (Userbean)request.getSession().getAttribute("user");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <body>
  
    <form action="<%=sssspath%>/servlet/LoginServlet?actionMode=1" method="post" name="loginForm">
    	<input type="hidden" name="userName" value=<%=user.getUsername()%> class="inputbox1" size="10">
    	<input type="hidden" name="password" value=<%=user.getPassword()%> class="inputbox1" size="10">
    </form>
  </body>
</html>
<script type="text/javascript">
	document.loginForm.submit();
</script>

