<%@page import="com.elitecore.netvertexsm.util.constants.SystemLoginConstants"%>
<%@ taglib uri="/WEB-INF/config/tlds/struts-bean.tld" 	prefix="bean" %>
<%@ taglib uri="/WEB-INF/config/tlds/struts-html.tld" 	prefix="html" %>
<%@ taglib uri="/WEB-INF/tld/elite_netvertex.tld" 	prefix="Elitecore"%>
<%
    String basePath = request.getContextPath();
%>

<html>
  <head>
    <title>Server Manager Login</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <LINK REL ="stylesheet" TYPE="text/css" HREF="css/mllnbillling.css" >
     <link REL="SHORTCUT ICON" HREF="<%=basePath%>/images/favicon_icon.ico" type="image/x-icon" title="NETVERTEXSM"> 
     <script src="<%=basePath%>/js/jquery-1.9.1.js"></script>
	<script language="Javascript1.2">
	
	
	$(document).ready(function() {
		$("input").keypress(function(event) {			
			if (event.which == 13) {
				event.preventDefault();
				if(validateForm()==true){
			    	//$("#loginFormId").submit();
				}
			}
		});
	});
	
	var validityResult;

	function validateForm(){
		 
		if(document.forms[0].userName.value==''){
			alert('User Name must be specified.');
			$("#userName").focus();
			return false;
		}else if(document.forms[0].password.value==''){
			alert('Password must be specified.');
			$("#password").focus();
			return false;
		}else {
			 document.forms[0].method = "POST";
			 document.forms[0].action='<%=request.getContextPath()%>/CheckPasswordValidity';
			 document.forms[0].submit();
		}
	}
	
		window.focus();
		window.location.hash = "no-back-button";
		window.location.hash = "Again-no-back-button";  
		window.onhashchange = function () {
		    window.location.hash = "no-back-button";
		}
	  /** Break out of frames on redirect*/
	  function breakout_of_frame()
	  {
	    if (top.location != location) 
	    {
	      top.location.href = document.location.href ;
	    }
	  }	
	</script>
	
  </head>
  <body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" scroll="No" onload="breakout_of_frame();document.systemLoginForm.userName.focus()" onhelp="return false;">
  <Script language='JavaScript'>
	tblheight=document.body.clientHeight;
	document.writeln("<TABLE border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#005197\" width=\"100%\" height=\"100%\" > ");
  </Script>
 <%
  String errorCode = "";
  errorCode = (String)request.getAttribute("errorCode");
 %>
  <tr>
    <td bgcolor="#000000" height="10"></td>
  </tr>
  <tr> 
  <td align="right" height="22%"  valign="top"><%--<img src="<%=basePath%>/images/elitecore-product.jpg" width="263" height="54" usemap="#Map" border="0">--%>
    <%--<map name="Map">
      <area shape="rect" coords="28,3,209,20" href="http://www.elitecore.com" target="_blank">
    </map>--%>
  </td>
  </tr>
  <tr>
    <td height="10">&nbsp;</td>
  </tr>
  <tr>
    <td height="10">&nbsp;</td>
  </tr>
  <tr>
    <td height="10" align="center"><font class="login-error-message" >
	<%
		if(errorCode!=null)
		{
			if(errorCode.equalsIgnoreCase("USERNAME")){
	%>
		<bean:message bundle="resultMessageResources" key="general.login.invalid.username"/>
	<%
			}else if(errorCode.equalsIgnoreCase("PASSWORD")){
	%>
		<bean:message bundle="resultMessageResources" key="general.login.invalid.password"/>	
	<%
			}else if(errorCode.equalsIgnoreCase("LOGINFAILED")){	
	%>
		<bean:message bundle="resultMessageResources" key="general.login.failed"/>		
	<%
			}
		}
	%>
	</font></td>
  </tr>
  <tr>
    <td height="10" align="center">&nbsp;</td>
  </tr>
  <tr> 
    <td align="center" valign="top">
    <table width="272" border="0" cellspacing="0" cellpadding="0" align="center">
		
   	  <html:form action="/login" method="POST" styleId="loginFormId" >
   	  	<tr> 
          <td width="72"><Elitecore:logoImage/></td>
          <td width="200"><img src="<%=basePath%>/images/billing.jpg" width="171" height="113"></td>
        </tr>
        <tr> 
          <td width="72"><img src="<%=basePath%>/images/login.jpg" width="101" height="26"></td>
          <td width="200" background="<%=basePath%>/images/login-bkgd.jpg"> 
            <input type="text" name="userName" id="userName"  size="15" maxlength="20" class="textbox-text" style="width: 120px; height: 22px;" >
          </td>
        </tr>
        <tr> 
          <td width="72"><img src="<%=basePath%>/images/pwd.jpg" width="101" height="27"></td>
          <td width="200" background="<%=basePath%>/images/pwd-bkgd.jpg"> 
            <input type="password" name="password"  id="password" size="15" maxlength="50" class="textbox-text" style="width: 120px; height: 22px;" autocomplete="off">
          </td>
        </tr>
        <tr> 
          <td width="72"><img src="<%=basePath%>/images/btm.jpg" width="101" height="48"></td>
          <td width="200"><img src="<%=basePath%>/images/btm1.jpg" width="104" height="48"><input type="image" src="<%=basePath%>/images/go.jpg" width="49" height="48" onclick="validateForm();return false;"  ></td>
        </tr>
        <input type="hidden" name="loginMode" value="1"> <!-- 1 is for Staff -->
        <input type="hidden" name="skipPasswordCheck" value="1"> 
      </html:form>
    </table>
	</td>
 </tr>
<Script language='JavaScript'>
	document.writeln("</TABLE>");
	document.systemLoginForm.userName.focus(); 
</Script>
</body>
</html>
