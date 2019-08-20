<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<HTML><HEAD><TITLE>CUSTOMER WEB SELF CARE</TITLE>
<META http-equiv=Content-Type content="text/html; charset=iso-8859-1">
<SCRIPT language=javascript src="<%=request.getContextPath()%>/js/cookie.js" type=text/javascript></SCRIPT>
<LINK  href="<%=request.getContextPath()%>/css/portal_style.css" rel=stylesheet>
<STYLE>.BODY1 {
	FONT-SIZE: 11px; BACKGROUND-IMAGE: none; BACKGROUND-COLOR: #ffffff
}
</STYLE>
<!--<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />-->
<META content="MSHTML 6.00.6000.16735" name=GENERATOR></HEAD>
<BODY class=BODY1>

<TABLE class=Box cellSpacing=0 cellPadding=0 width="90%" align=center border=0>
  <TBODY>
  <TR>
    <TD colSpan=2>
      <TABLE height=20 cellSpacing=0 cellPadding=0 width="100%" border=0>
        <TBODY>
        <TR>
          <TD background=<%=request.getContextPath()%>/images/Header.jpg height=83>
            <TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
              <TBODY>
              <TR>
                <TD align=middle width="24%"><IMG height=80 src="<%=request.getContextPath()%>/images/wsc.jpg" width=120></TD>
                <TD class=CompanyName width="76%">Elitecore Technologies Ltd. - Customer Web Self Care</TD></TR></TBODY></TABLE></TD></TR></TBODY></TABLE></TD></TR>
  <TR>
    <TD class=LoginBkgd colSpan=2>&nbsp;</TD></TR>
  <TR>
    <TD align=middle colSpan=2>
      <TABLE cellSpacing=2 cellPadding=3 width="95%" align=center border=0>
        <TBODY>
        <TR>
          <TD>&nbsp;</TD>
          <TD class=TextLabels align=right>
          </TD></TR></TBODY></TABLE></TD></TR>
  <TR>
    <TD class=MenuText width=578 height=210>
      <TABLE cellSpacing=2 cellPadding=3 width="60%" align=right 
        border=0><NOSCRIPT><SPAN style="COLOR: red"><B>Please enable the 
        JavaScript in your browser for proper functioning of site 
        </B></SPAN></NOSCRIPT>
        <s:form name="frmLogin" action="Login" method="post" theme="simple">
        <TBODY>
        <TR>
          <TD class=LoginText colSpan=2>&nbsp;</TD></TR>
        <TR>
          <TD class=WhiteHeader colSpan=2>Member Login</TD></TR>
        <TR>
          <TD class=SmallGap colSpan=2>&nbsp;</TD></TR>
        <TR>
          <TD class=LoginText width="32%">User Name</TD>
          <TD width="68%"><s:textfield  cssClass="InputFields" name="username" id="username"/></TD></TR>
        <TR>
          <TD class=LoginText>Password</TD>
          <TD><s:password cssClass="InputFields"  name="password"/></TD></TR>
        <TR>
          <TD class=LoginText>&nbsp;</TD>
<!--          <TD class=PwdText id=c_strRegister vAlign=bottom name="c_strRegister"><A -->
<!--            onclick='window.open("/jsp/customer/InitForgotPassword.jsp","hints","top=5,left=30,toolbars=no,maximize=yes,resize=yes,width=452,height=380,location=no,directories=no,scrollbars=yes")' -->
<!--            href="javascript:void(0)">Forgot Password?</A> -->
<!--            &nbsp;&nbsp;&nbsp;&nbsp; <A -->
<!--            href="http://ngt.jboss.datawsc.co.in:40080/jsp/customer/RegisterCustomer.jsp">Register</A> -->
<!--          </TD>-->
          <TD class=PwdText id=c_strRegister vAlign=bottom name="c_strRegister"><A 
            href="javascript:void(0)">Forgot Password?</A> 
            &nbsp;&nbsp;&nbsp;&nbsp; <A 
            href="#">Register</A> 
          </TD>
          </TR>
        <TR>
          <TD height=36>&nbsp;</TD>
          <TD>
          	<s:submit cssClass="Buttons"  type="submit" value="Login" name="Submit2" theme="simple"/> 
          </TD>
        </TR>
        <tr>
         	<TD height=5>&nbsp;</TD>
			<td align="left" height=1>
				<FONT color="#cc0000" size="2"  >
            	<s:actionerror cssClass="MenuText" theme="simple"/> 
            </FONT>
			</td>
		</tr>
        <TR>
          <TD>&nbsp;</TD>
          <TD>&nbsp;</TD>
        </TR>
        <TR>
          <TD class=MenuText align=middle 
      colSpan=2>&nbsp;</TD></TR></s:form></TBODY></TABLE></TD>
    <TD class=MenuText vAlign=top align=middle width=200>
      <TABLE cellSpacing=0 cellPadding=0 width="98%" border=0>
        <TBODY>
        <TR>
          <TD class=SmallGap align=middle>&nbsp;</TD></TR>
        <TR>
          <TD class=MenuText align=middle><STRONG>Helpline 
            Number464646464</STRONG></TD></TR></TBODY></TABLE></TD></TR>
  <TR>
    <TD align=middle colSpan=2 height=60>&nbsp;</TD></TR><!--<table align="center" bgcolor="#f8f8f8" border="0" cellpadding="2" cellspacing="0" width="98%">-->
  <TR></SPAN>
    <TD>&nbsp; </TD></TR><!--</table>-->
  <TR>
    <TD colSpan=2>
      <TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
        <TBODY>
        <TR>
          <TD class=PwdText align=middle colSpan=3></A></TD>
          <TD vAlign=top align=middle width=104 rowSpan=2><IMG height=25 src="<%=request.getContextPath()%>/images/Footer.jpg" width=68> </TD></TR><!--<tr class="SmallGap">
							<td height="15" class="Footer">&nbsp;</td>
						</tr>-->
        <TR class=SmallGap>
          <TD class=MenuText align=middle width=275>&nbsp;<!--<a href="http://www.contactus.com" target="_blank" class="PwdText" >Contact Us</a>--> 
          </TD>
          <TD class=MenuText align=middle width=200><A class=PwdText 
            href="http://www.contactus.com/" target=_blank>Contact Us</A> </TD>
          <TD class=MenuText align=right width=201><A class=PwdText 
            href="http://www.elitecore.com/" target=_blank>Powered by Elitecore Technologies Ltd.</A>&nbsp;&nbsp; 
</TD></TR></TBODY></TABLE></TD></TR></TBODY></TABLE></TD></TR></BODY>
<SCRIPT type="text/javascript">
document.getElementById("username").focus();
</SCRIPT>
</HTML>