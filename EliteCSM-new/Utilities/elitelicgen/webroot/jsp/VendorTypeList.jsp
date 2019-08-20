<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*" errorPage="" %>
<jsp:directive.page import="java.util.Map"/>
<jsp:directive.page import="java.util.Iterator"/>
<jsp:directive.page import="org.apache.commons.collections.SequencedHashMap"/>
<jsp:directive.page import="com.elitecore.license.base.LicenseData"/>
<jsp:directive.page import="com.elitecore.license.base.commons.LicenseConstants"/>
<jsp:directive.page import="com.elitecore.license.configuration.LicenseConfigurationManager"/>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>

<%
String path = request.getContextPath();
Map licMap = (SequencedHashMap) request.getSession(false).getAttribute("licMap");

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="java.util.List"%><html>
<head>
<title>Elite License Generator</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="<%=path%>/css/mllnstyles.css" rel="stylesheet" type="text/css">
<link href="../css/style.css" rel="stylesheet" type="text/css">
<link href="../css/mllnstyles.css" rel="stylesheet" type="text/css">
<script language="Javascript">

function combineSelectedItems()
{
 	var selected_value = '' ;
 	for (var i=0; i < document.forms[0].choice.length; i++){
	    if (document.forms[0].choice[i].checked) {
	    	selected_value = selected_value + document.forms[0].choice[i].value + ",";
       }
    }
	if(selected_value.lastIndexOf(',') == selected_value.length-1){
		selected_value=selected_value.substring(0,selected_value.lastIndexOf(','));
	}
		   
    var field = window.opener.document.getElementById("vendortype");
    field.value= selected_value;
	window.close();

}

</script>
</head>

<body>
<table width="100%" border="0" height="30%">
 <tr>
    <td height="30">&nbsp;</td>
  </tr>
</table>
<table width="100%" height="100%">
<tr>
<td width="20%"></td>
<td>
	<table width="100%"   border="1" align="center" cellpadding="0" cellspacing="0">
		<form action="" >
	      <tr>
            <td height="16%" colspan="4"  class="tblheader-bold">Vendor Types</td>
          </tr>
		  <tr>
            <td height="19%" colspan="4" class="tblrows">&nbsp;</td>
          </tr>
          <%
             List VendorList =(List)request.getSession().getAttribute("nastypelist");
             Iterator<String> it = VendorList.iterator();
          	 while(it.hasNext())
          	 {
              String vendorType = it.next();
           %>
          <tr>
           <!--  <td width="2%" height="25%" class="tblcol" >&nbsp;</td> -->
          	<td width="2%" height="25%" class="tblcol" align="left" >
          	<input name="choice" id="choice" type="checkbox" value="<%=vendorType%>"><%=vendorType%></input></td>
		 </tr>
		<%} %>
		  <tr>
            <td height="25%" colspan="4" align="center" class="tblrows">&nbsp;</td>
          </tr>
		  <tr>
            <td height="25%" colspan="4" align="center" class="tblrows"><input type="submit" name="next" value="   OK   " class="large_button" onclick="combineSelectedItems()"></input> </td>
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
<td width="20%"></td>
</tr> 
</table>
<table width="100%" border="0" height="53">
 <tr>
    <td height="53">&nbsp;</td>
  </tr>
</table>
</body>
</html>
