<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
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
<h1><spring:message code="title"/></h1>
<form:form action="insert" method="post" modelAttribute="student">
<pre>
<spring:message code="sname"/><form:input path="stdName"/>
<form:errors path="stdName"/>
<spring:message code="ssal"/><form:input path="stdSal"/>
<form:errors path="stdSal"/>
<spring:message code="spwd"/><form:password path="stdPwd"/>
<form:errors path="stdPwd"/>
<spring:message code="sgen"/><form:radiobutton path="stdGen" value="Male"/>Male <form:radiobutton path="stdGen" value="Female"/>Female
<form:errors path="stdGen"/>
<spring:message code="saddr"/><form:textarea path="stdAddr"/>
<form:errors path="stdAddr"/>
<spring:message code="slang"/>
 <form:checkbox path="stdLang" value="ENG"/>ENG
 <form:checkbox path="stdLang" value="HIN"/>HIN
 <form:checkbox path="stdLang" value="TEL"/>TEL
 <form:errors path="stdLang"/>
<spring:message code="scntry"/><form:select path="stdCntry">
			<form:option value="">--select--</form:option>
			<form:option value="IND">IND</form:option>
			<form:option value="AUS">AUS</form:option>
			<form:option value="US">US</form:option>
			<form:option value="UK">UK</form:option>
		</form:select> 
		<form:errors path="stdCntry"/>
<input type="submit" value="Register"/>		
</pre>
</form:form>
${message}
</body>
</html>