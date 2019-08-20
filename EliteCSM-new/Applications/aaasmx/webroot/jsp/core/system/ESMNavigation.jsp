<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm" %>
<%@ page import="com.elitecore.elitesm.util.constants.BaseConstant" %>
<%
    String basePath = request.getContextPath();
	SystemLoginForm navigationUserSession = (SystemLoginForm)request.getSession().getAttribute("radiusLoginForm");    
	String jspPath="";
	boolean isOlderVersion=true;
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet" href="<%=basePath%>/css/mllnstyles.css" type="text/css"/>
<SCRIPT LANGUAGE="JavaScript" src="<%=basePath%>/js/changecolor.js"></script>
<SCRIPT LANGUAGE="JavaScript" src="<%=basePath%>/js/commonfunctions.js"></script>
<script language="javascript" src="<%=basePath%>/js/openhelp.js"></script>

<SCRIPT LANGUAGE="JavaScript" TYPE="text/javascript">
if(window.event + "" == "undefined") event = null;
function HM_f_PopUp(){return false};
function HM_f_PopDown(){return false};
popUp = HM_f_PopUp;
popDown = HM_f_PopDown;
</SCRIPT>

<SCRIPT LANGUAGE="JavaScript1.2" TYPE="text/javascript">
HM_PG_FramesEnabled = true;
HM_PG_FramesNavFramePos = "left";
HM_PG_FramesMainFrameName = "mainFrame";
</SCRIPT> 
</head>
<body>
<body bgcolor="#FFFFFF"  leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onhelp="openHelpPage();return false;">
<input type="hidden" value="test" id="jspHidden"/>
<TABLE border="0" cellpadding="0" cellspacing="0" bgcolor="#005197" width="175" height="100%"  > 
  <tr>
    <td height="10" valign="top">
    <table width="175" height="100%"  border="0" cellspacing="0" cellpadding="0">
     	 <tr> 
       		 <td>
       		 	<img src="<%=basePath%>/images/Sterlite_EliteCSM_top_left.jpg" width="175" height="88">
       		 </td>
      	 </tr>
	     <tr>
		     <td class="grey-text style1" width="175" height="100%" >
				 <jsp:include  page="/jsp/core/includes/menus/SMMenu.jsp"></jsp:include>
			</td>
	     </tr>
    </table>
	</td>
 </tr>
</TABLE>
</body>
</html>