<html>
<head>
<% 
	String localBasePath = request.getContextPath();
	String adminHost=request.getParameter("adminHost");
	String adminPort=request.getParameter("adminPort");
	String serverInstanceName=request.getParameter("serverInstanceName");
	
%>
<title>CLI - <%=serverInstanceName%> [<%=adminHost%>:<%=adminPort%>]
</title>
</head>

<LINK REL="stylesheet" TYPE="text/css"
	HREF="<%=request.getContextPath()%>/css/mllnstyles.css">


<script>
var jsVars = {adminHost:"<%=adminHost%>",
		adminPort:"<%=adminPort%>"}
</script>

<script type="text/javascript" language="javascript"
	src="<%=request.getContextPath()%>/cli/cli.nocache.js"></script>
<body>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">

		<tr>
			<td id="cliTerminal" width="100%"></td>
		</tr>

		<%-- 
   <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="330px">
            <tr>
             <td id="cliTerminal" width="100%"></td>
            </tr>
       
	     </table>
	    </td>
	   </tr>
	--%>
	</table>
</body>
</html>
