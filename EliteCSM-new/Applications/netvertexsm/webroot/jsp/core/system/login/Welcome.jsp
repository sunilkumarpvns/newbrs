<%@ page import="java.util.Date"%>
<%
    String basePath = request.getContextPath();
%>

<html>
<head>
<title>Server Manager</title>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=basePath%>/css/mllnstyles.css" >
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=basePath%>/css/popcalendar.css" >
<script language="javascript" src="<%=basePath%>/js/validation.js"></script>
<script language="javascript" src="<%=basePath%>/js/commonfunctions.js"></script>
<script language="javascript" src="<%=basePath%>/js/cookie.js"></script>
<script language="javascript" src="<%=basePath%>/js/popcalendar.js"></script>
<script language="javascript" src="<%=basePath%>/js/openhelp.js"></script>

<script language="javascript">
		
function storeDOM()
{
	pagecontent=('<pre><html>' + (document.documentElement.innerHTML) + '</html></pre>');
	
}													
</script>

</head>
<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="MM_preloadImages('images/csv-hover.jpg','images/pdf-hover.jpg','images/html-hover.jpg','images/filter-hover.jpg','images/previous-hover.jpg','images/next-hover.jpg','images/dnarrow-y.jpg','images/sublinks-dnarrow.jpg','images/sublinks-uparrow.jpg'); collapseAllRows();" onhelp="openHelpPage();return false;" onBlur="storeDOM()">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  <tr> 
    <td width="10">&nbsp;</td>
    <td width="85%" valign="top" class="box"> 
	 <table width="100%">
		<tr height=350px>
			<td>&nbsp;
				 
			</td>
		</tr>

	 </table>
    </td>
    <td width="15%" class="grey-bkgd" valign="top">&nbsp;
	 
    </td>
  </tr>
 <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>
