<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*" errorPage="" %>
<jsp:directive.page import="java.util.Map"/>
<jsp:directive.page import="java.util.Iterator"/>
<jsp:directive.page import="org.apache.commons.collections.SequencedHashMap"/>
<jsp:directive.page import="com.elitecore.license.base.LicenseData"/>
<jsp:directive.page import="com.elitecore.license.base.commons.LicenseConstants"/>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>

<%
String path = request.getContextPath();
Map licMap = (SequencedHashMap) request.getSession(false).getAttribute("licMap");

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
		<form action="<%=path%>/ChooseModule.do" method="post" enctype="multipart/form-data">
	      <tr>
            <td height="16%" colspan="4"  class="tblheader-bold">Choose Modules</td>
          </tr>
		  <tr>
            <td height="19%" colspan="4" class="tblrows">&nbsp;</td>
          </tr>
           <tr>
            <td width="2%" height="25%" class="tblcol" >&nbsp;</td>
          	<td width="20%" height="25%" class="tblcol" align="left">Public Key</td>
			<td width="57%" height="25%" align="left" class="tblcol">&nbsp;<input name="pubkeyFile" type="file"></input></td>
			<td width="7%" height="25%"  class="tblcol-right">&nbsp;</td>
		 </tr>
          <tr>
            <td width="2%" height="25%" class="tblcol" >&nbsp;</td>
          	<td width="20%" height="25%" class="tblcol" align="left">Version</td>
			<td width="57%" height="25%" align="left" class="tblcol">&nbsp;<input name="version" type="text"></input></td>
			<td width="7%" height="25%"  class="tblcol-right">&nbsp;</td>
		 </tr>
		 
		  <%--<tr>
            <td width="2%" height="25%" class="tblcol" >&nbsp;</td>
          	<td width="2%" height="25%" class="tblcol" align="left">&nbsp;</td>
			<td width="57%" height="25%" align="left" class="tblcol">&nbsp;--%><input name="status" type="hidden" value="<%=LicenseConstants.DEFAULT_STATUS %>"></input>
			<%--</td>
			<td width="7%" height="25%"  class="tblcol-right">&nbsp;</td>
			 </tr>
			 --%>
		 <tr>
            <td width="2%" height="25%" class="tblcol" >&nbsp;</td>
          	<td width="2%" height="25%" class="tblcol" align="left">&nbsp;</td>
			<td width="57%" height="25%" align="left" class="tblcol">&nbsp;<input name="additionalKey" type="hidden" value=<%=LicenseConstants.DEFAULT_ADDITIONAL_KEY %>></input></td>
			<td width="7%" height="25%"  class="tblcol-right">&nbsp;</td>
		 </tr>
          
          <%
             Iterator<String> it = (Iterator<String>) licMap.keySet().iterator();
          	 while(it.hasNext())
          	 {
              String tempLicKey =  it.next();
           %>
          <tr>
            <td width="2%" height="25%" class="tblcol" >&nbsp;</td>
          	<td width="2%" height="25%" class="tblcol" align="left" ><input name="choice" type="radio" value="<%=tempLicKey%>"></input></td>
			<td width="57%" height="25%" align="left" class="tblcol">&nbsp;&nbsp;<%=((LicenseData)licMap.get(tempLicKey)).getDisplayName()%></td>
			<td width="7%" height="25%"  class="tblcol-right">&nbsp;</td>
		 </tr>
		<%} %>
		  <tr>
            <td height="25%" colspan="4" align="center" class="tblrows">&nbsp;</td>
          </tr>
		  <tr>
            <td height="25%" colspan="4" align="center" class="tblrows">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" name="next" value="   next   " class="large_button"></input> </td>
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
