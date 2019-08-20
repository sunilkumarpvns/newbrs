<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The DOCTYPE declaration above will set the    -->
<!-- browser's rendering engine into               -->
<!-- "Standards Mode". Replacing this declaration  -->
<!-- with a "Quirks Mode" doctype may lead to some -->
<!-- differences in layout.                        -->

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <!--                                                               -->
    <!-- Consider inlining CSS to reduce the number of requested files -->
    <!--                                                               -->
    
    <LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >

    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>Live Monitoring</title>
    
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/gwtgraph/gwtgraph.nocache.js"></script>
  
  </head>

  <!--                                           -->
  <!-- The body can have arbitrary html, or      -->
  <!-- you can leave the body empty if you want  -->
  <!-- to create a completely dynamic UI.        -->
  <!--                                           -->
  <body>

    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
    
    <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
    <noscript>
      <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Your web browser must have JavaScript enabled
        in order for this application to display correctly.
      </div>
    </noscript>
    <%
    String basePath = request.getContextPath();
    String serverId = request.getParameter("serverId");
    String graphType = request.getParameter("graphType");
     %>
<script>
var jsVars = {serverId:"<%=serverId%>",
		graphType:"<%=graphType%>"}
</script>     
<table width="828" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="10">&nbsp;</td>
    <td colspan="2"> 
    
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
         <tr> 
          <td width="26" valign="top" rowspan="2"><img src="<%=basePath%>/images/left-curve.jpg"></td>
          <td background="<%=basePath%>/images/header-gradient.jpg" width="133" rowspan="2" class="page-header">Live Monitoring</td>
          <td width="32" rowspan="2"><img src="<%=basePath%>/images/right-curve.jpg"></td>
          <td width="633"></td>
        </tr>
        <tr> 
          <td width="633" valign="bottom"><img src="<%=basePath%>/images/line.jpg"></td>
        </tr>
      </table>
      
    </td>
  </tr>
  
  <tr> 
      <td width="10" class="small-gap">&nbsp;</td>
    <td class="small-gap" colspan="2">&nbsp;</td>
  </tr>
	<tr>
    <td width="10" class="small-gap">&nbsp;</td>	
    	<td  width="773" class="box" >
    <table  border="0" width="100%">
      <tr>
        <td id="demo"></td>
      </tr>
    </table>
    </td>
 	</tr>
 	 <tr> 
    <td colspan="3" class="small-gap">&nbsp;</td>
  </tr>
 	
 	<tr>
 		<td width="10">&nbsp;</td>  
		<td colspan="2" valign="top" align="right">
			<table width="99%" border="0" cellspacing="0" cellpadding="0">
				<tr>
						<td width="82%" valign="top"><img src="<%=basePath%>/images/btm-line.jpg"></td>
						<td align="right" rowspan="2" valign="top"><img src="<%=basePath%>/images/btm-gradient.jpg" width="140" height="23"></td>
				</tr>
				<tr>
						<td width="82%" valign="top" align="right" class="small-text-grey">Copyright&copy; <a href="http://www.elitecore.com" target="_blank">Elitecore Technologies Pvt. Ltd.</a></td>
				</tr>
			</table>
		</td>
	</tr>
	</table>
  </body>
</html>
