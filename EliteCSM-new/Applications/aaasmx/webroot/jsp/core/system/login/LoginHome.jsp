<%@ page import="java.util.Date"%>

<%
    String basePath = request.getContextPath();
    //String strWelcomePage = basePath + "/jsp/core/system/login/Welcome.jsp";
    String strWelcomePage = basePath + "/listNetServerInstance.do";
%>
<html>
<head>
<title>EliteCSM - Server Manager</title>
<script language="javascript" src="<%=basePath%>/js/openhelp.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link REL="SHORTCUT ICON" HREF="<%=basePath%>/images/sterlitetech-favicon.ico" type="image/x-icon" title="AAASMX">
</style>


</head>

<frameset cols="175,*" frameborder="NO" border="0" framespacing="0" rows="*">
	<frame name="leftFrame" id="leftFrame" scrolling="NO" noresize="noresize" src="<%=basePath%>/jsp/core/system/ESMNavigation.jsp">
	<frameset rows="55,*" frameborder="NO" border="0" framespacing="0" id="smFrameSet">
		<frame name="topFrame" id="topFrame" scrolling="NO" noresize="noresize" src="<%=basePath%>/jsp/core/system/ESMHeader.jsp">
		<frame name=mainFrame id="mainFrame" src='<%=strWelcomePage%>' scrolling="auto"  style="overflow: auto;">
	</frameset>
</frameset>

<noframes>
	<body bgcolor="#FFFFFF" text="#000000">
		This page uses frames,but your browser doesn't support them.
	</body>
</noframes>
</html>
