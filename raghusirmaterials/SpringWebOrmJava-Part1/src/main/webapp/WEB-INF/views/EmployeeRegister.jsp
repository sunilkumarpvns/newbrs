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
PWD  : <form:password path="empPwd"/>
GEN  : <form:radiobutton path="empGen" value="Male"/>Male <form:radiobutton path="empGen" value="Female"/>Female
ADDR : <form:textarea path="empAddr"/>
LANG :
 <form:checkbox path="empLang" value="ENG"/>ENG
 <form:checkbox path="empLang" value="HIN"/>HIN
 <form:checkbox path="empLang" value="TEL"/>TEL
CNTRY: <form:select path="empCntry">
			<form:option value="">--select--</form:option>
			<form:option value="IND">IND</form:option>
			<form:option value="AUS">AUS</form:option>
			<form:option value="US">US</form:option>
			<form:option value="UK">UK</form:option>
		</form:select> 
<input type="submit" value="Register"/>		
</pre>
</form:form>
${message}
</body>
</html>