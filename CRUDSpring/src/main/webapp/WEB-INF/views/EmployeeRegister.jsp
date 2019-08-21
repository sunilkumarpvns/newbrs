<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Employee Register</title>
</head>
<body>
<h1>Welcome to Employee Register Page!!</h1>
<form:form action="insert" method="post" modelAttribute="employee">
<pre>
NAME : <form:input path="empName"/>
SAL  : <form:input path="empSal"/>
<input type="submit" value="Register"/>		
</pre>
</form:form>
${message}
</body>
</html>