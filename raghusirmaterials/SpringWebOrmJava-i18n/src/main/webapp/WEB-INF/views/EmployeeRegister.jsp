<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>        
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Employee Register</title>
</head>
<body>
<a href="?lang=en">ENG</a>
<a href="?lang=hi">HINDI</a>
<a href="?lang=te">TELUGU</a>
<a href="?lang=kn">KANNDA</a>
<hr/>
<h1><spring:message code="title"/> </h1>
<form:form action="insert" method="post" modelAttribute="employee">
<pre>
<spring:message code="empname"/> : <form:input path="empName"/>
<form:errors path="empName"/>
<spring:message code="empsal"/> : <form:input path="empSal"/>
<form:errors path="empSal"/>
<spring:message code="emppwd"/>  : <form:password path="empPwd"/>
<form:errors path="empPwd"/>
GEN  : <form:radiobutton path="empGen" value="Male"/>Male <form:radiobutton path="empGen" value="Female"/>Female
<form:errors path="empGen"/>
ADDR : <form:textarea path="empAddr"/>
<form:errors path="empAddr"/>
LANG :
 <form:checkbox path="empLang" value="ENG"/>ENG
 <form:checkbox path="empLang" value="HIN"/>HIN
 <form:checkbox path="empLang" value="TEL"/>TEL
 <form:errors path="empLang"/>
CNTRY: <form:select path="empCntry">
			<form:option value="">--select--</form:option>
			<form:option value="IND">IND</form:option>
			<form:option value="AUS">AUS</form:option>
			<form:option value="US">US</form:option>
			<form:option value="UK">UK</form:option>
		</form:select> 
<form:errors path="empCntry"/>		
<input type="submit" value="Register"/>		
</pre>
</form:form>
${message}
</body>
</html>