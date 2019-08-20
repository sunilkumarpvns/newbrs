<%@ page import="com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm" %>
<%@ page import="com.elitecore.netvertexsm.util.constants.BaseConstant" %>

<%
     String basePath = request.getContextPath(); 
	SystemLoginForm navigationUserSession = (SystemLoginForm)request.getSession().getAttribute("radiusLoginForm");        
%>

<html>
<head>  
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="<%=basePath%>/css/mllnstyles.css" type="text/css">
<SCRIPT LANGUAGE="JavaScript" src="<%=basePath%>/js/changecolor.js"></script>
<SCRIPT LANGUAGE="JavaScript" src="<%=basePath%>/js/commonfunctions.js"></script>
<script language="javascript" src="<%=basePath%>/js/openhelp.js"></script>

<SCRIPT LANGUAGE="JavaScript" TYPE="text/javascript">
<!--
if(window.event + "" == "undefined") event = null;
function HM_f_PopUp(){return false};
function HM_f_PopDown(){return false};
popUp = HM_f_PopUp;
popDown = HM_f_PopDown;
//-->
</SCRIPT>
<SCRIPT LANGUAGE="JavaScript1.2" TYPE="text/javascript">
<!--
HM_PG_FramesEnabled = true;
HM_PG_FramesNavFramePos = "left";
HM_PG_FramesMainFrameName = "mainFrame";
//-->
</SCRIPT> 

<style type="text/css">
<!--
.style1 {color: #FFFFFF}
-->
</style>
</head>

<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onhelp="openHelpPage();return false;">
<Script language='JavaScript'>




	//tblheight=document.body.clientHeight;
//document.writeln("<TABLE border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#005197\" width=\"175\" height=\"100%\"  > ");
</Script>
<TABLE border="0" cellpadding="0" cellspacing="0" bgcolor="#005197" width="175" height="100%"  > 
  <tr>
    <td height="10" valign="top">
    
    
    <table width="175" height="100%"  border="0" cellspacing="0" cellpadding="0">
    
      <tr> 
        <td><img src="<%=basePath%>/images/jispbilling.jpg" width="175" height="88"></td>
       </tr>
       
      <tr>
        <td class="grey-text style1" width="175" height="100%" >
				<Script language='JavaScript' SRC="<%=basePath%>/js/coolmenus3.js"></script>
					<%@ include file="/jsp/core/includes/menus/ESMMenu.jsp" %>
		</td>
      </tr>
      
      
      
    </table>
    
	</td>
 </tr>
 
<Script language='JavaScript'>
document.writeln("</TABLE>");



</Script>
</body>
</html>
