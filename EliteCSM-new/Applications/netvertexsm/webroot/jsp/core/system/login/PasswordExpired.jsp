 
<%@ page import="java.util.Date"%>

<%
    String basePath = request.getContextPath();
    //String strWelcomePage = basePath + session.getAttribute("strFirstLoginPath").toString();
    String strWelcomePage = basePath + "/initChangePassword.do";
    		
%>
<html>
  <head>
    <title>Server Manager</title>
    <script language="javascript" src="<%=basePath%>/js/openhelp.js"></script>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <link REL="SHORTCUT ICON" HREF="<%=basePath%>/images/favicon_icon.ico" type="image/x-icon" title="AAASMX"> 
  </head>

  <frameset cols="175,*" frameborder="NO" border="0" framespacing="0" rows="*"> 
    <frame name="leftFrame" scrolling="NO"  noresize="noresize"  src="<%=basePath%>/jsp/core/system/ESMNavigation.jsp">
      <frameset rows="55,*" frameborder="NO" border="0" framespacing="0"> 
        <frame name="topFrame" scrolling="NO"  noresize="noresize"  src="<%=basePath%>/jsp/core/system/ESMHeader.jsp" >
        <frame name="mainFrame" src='<%=strWelcomePage%>' scrolling="YES">
      </frameset>
   </frameset> 

  <noframes>
    <body bgcolor="#FFFFFF" text="#000000">
        This page uses frames, but your browser doesn't support them.
    </body>
  </noframes> 
</html>