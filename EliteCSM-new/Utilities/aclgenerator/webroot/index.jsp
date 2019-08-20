<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>Configuration DBC Creator</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<style type="text/css">
	    td.title-font { font-family: Arial;  font-weight: bold; font-size: 12px; color: #000000;padding-top: 1px; padding-right: 0px; padding-bottom: 1px; padding-left: 3px }
		td.notes-font { font-family: Arial;  font-weight: italic; font-size: 12px; color: #000000;padding-top: 1px; padding-right: 0px; padding-bottom: 1px; padding-left: 3px }	    
	</style>
  </head>
  
  
  <body>
  <center>
  <form action="<%=path%>/servlet/InputsServlet" method="post" ENCTYPE='multipart/form-data'>
    <table width="70%">
    	<tr>
	    	<td colspan="3" align="center" >
	    	<h3 >  	ACL SQL Generator  	</h3>
	    	</td>
    	</tr>
   		<tr>
   			<td width="38%" class="title-font">ACL File:</td>
   			<td width="30%"><input type="file" name="configfile" id="configfile" size="40" /></td>
   			<td width="25%"></td>
   		</tr>
   		<tr>
   			<td class="title-font">Last Model Id:</td>
   			<td><input type="text" name="last_model_id" id="last_model_id" size="10"/></td>
   		</tr>
   		<tr height="10">
   			<td colspan="3" class="notes-font"><i>Note: It is numeric value with that the DBC Generator will start to generate the new IDs.</i></td>
   			<td></td>
   		</tr>
   			<tr>
   			<td colspan="3">&nbsp;</td>
   		</tr>
   		<tr>
   			<td class="title-font">Last Module Id:</td>
   			<td><input type="text" name="last_module_id" id="last_module_id" size="10"/></td>
   		</tr>
   		<tr height="10">
   			<td colspan="3" class="notes-font"><i>Note: It is numeric value with that the DBC Generator will start to generate the new IDs.</i></td>
   			<td></td>
   		</tr>
   			<tr>
   			<td colspan="3">&nbsp;</td>
   		</tr>
   		<tr>
   			<td class="title-font">Last Submodule Id:</td>
   			<td><input type="text" name="last_submodule_id" id="last_submodule_id" size="10"/></td>
   		</tr>
   		<tr height="10">
   			<td colspan="3" class="notes-font"><i>Note: It is numeric value with that the DBC Generator will start to generate the new IDs.</i></td>
   			<td></td>
   		</tr>
   			<tr>
   			<td colspan="3">&nbsp;</td>
   		</tr>
   		<tr>
   			<td class="title-font">Last Action Id:</td>
   			<td><input type="text" name="last_action_id" id="last_action_id" size="10"/></td>
   		</tr>
   		<tr height="10">
   			<td colspan="3" class="notes-font"><i>Note: It is numeric value with that the DBC Generator will start to generate the new IDs.</i></td>
   			<td></td>
   		</tr>
   			<tr>
   			<td colspan="3">&nbsp;</td>
   		</tr>
  		 <tr>
   			<td></td>
   			<td><input type="submit" name="submit" id="submit" value="Generate DBC"/></td>
   			<td></td>
   		</tr>
    </table>
    </form>
    </center>
  </body>
</html>
