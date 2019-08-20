<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%
	String errorCode = (String)request.getAttribute("errorCode");
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>NetVertex Enterprise Policy Control</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/common.css" />
<script type="text/javascript">
    function validate(){
		if(document.forms[0].userName.value=='' || document.forms[0].password.value==''){
			alert('Enter Username or Password');
			return false;
		}else{
			return true;
		}
	}
	function setDefaultFocus(){
    	document.getElementById("userName").focus();    	
	}    
</script>
</head>
<body onload="setDefaultFocus();">

<html:form action="/login">
<%
 application.setAttribute("selectedPortal", "enterprise");
%>
<input type="hidden" name="selectedPortal" value="enterprise"/>
<table width="1004" border="0" align="center" cellpadding="0" cellspacing="0" class="border">
  <tr>
    <td width="561" valign="top" bgcolor="#e63803" height="683"><img src="<%=request.getContextPath()%>/images/login-1.jpg" /></td>
    <td width="425" valign="top" height="683"><table width="100%" border="0" cellspacing="0" cellpadding="0" height="683">
        <tr>
          <td bgcolor="#dddddc" height="10"></td>
        </tr>
        <tr>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td class="header-menu"><a href="#">Services</a> <span class="line">|</span> <a href="#">New Offers</a> <span class="line">|</span> <a href="#">Contact</a></td>
        </tr>
        <tr>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td><table width="80%" border="0" align="center" cellpadding="0" cellspacing="0">              
              <tr>
                <td colspan="2" class="table-txt ">User Name</td>
              </tr>
              <tr>
                <td colspan="2"><html:text property="userName" styleId="userName" /></td>
              </tr>
              <tr>
                <td colspan="2">&nbsp;</td>
              </tr>
              <tr>
                <td colspan="2" class="table-txt ">Password</td>
              </tr>
              <tr>
                <td colspan="2" class="table-text "><html:password property="password" styleId="password"/></td>
              </tr>
              <tr>
                <td colspan="2" class="table-text ">&nbsp;</td>
              </tr>              
              <tr>
                <td width="23%" ><input class="button-txt" style="border:1px solid black;height:25px;" value="Login" type="submit" onclick="return validate();"/></td>
                <td width="77%" class="table-text ">&nbsp;</td>
              </tr>
              <tr>
                <td colspan="2" class="table-text ">&nbsp;</td>
              </tr>
              <%if(errorCode!=null) {%>
			  	<tr>
					<td width="30%" class="whitetext" style="color: red" colspan="2"><%=errorCode%></td>
				</tr>	
			  <%}%>
              <tr>
                <td colspan="2"><a href="#">New User Signup Here</a></td>
              </tr>
            </table></td>
        </tr>
        <tr>
          <td height="108">&nbsp;</td>
        </tr>
        <tr>
          <td><div align="right"><img src="<%=request.getContextPath()%>/images/Parental_Control.jpg" /></div></td>
        </tr>
        <tr>
          <td ><img src="<%=request.getContextPath()%>/images/login_bottom.jpg" height="58" width="362" /></td>
        </tr>
      </table></td>
    <td width="18" valign="top" bgcolor="#e83c00" height="683"><img src="<%=request.getContextPath()%>/images/login-2.jpg" /></td>
  </tr>
</table>
<input type="hidden" name="loginMode" value="1">
</html:form>
</body>
</html>
