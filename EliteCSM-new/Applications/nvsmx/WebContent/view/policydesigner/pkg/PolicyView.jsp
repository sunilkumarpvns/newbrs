<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<html>
<head>
<title>View</title>
</head>
<body> 
	<center>
		<div class="msg" >
			<s:property value="message"/>	
			<h2>Viewing Policy</h2>
			<a href="${pageContext.request.contextPath}policydesigner/policy/Policy/list">Ok</a>
		</div>
	</center>		 
</body>
</html>