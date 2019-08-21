<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Student Data</title>
</head>
<body>
	<h1>Welcome to Student Data Page!!</h1>

	<table border="1">
		<tr>
			<th>ID</th>
			<th>NAME</th>
			<th>SALARY</th>
			<th>PASSWORD</th>
			<th>GENDER</th>
			<th>ADDRESS</th>
			<th>LANGUAGES</th>
			<th>COUNTRY</th>
			<th>OPERATION</th>
		</tr>
		<c:forEach items="${emps}" var="e">
			<tr>
				<td><c:out value="${e.stdId}" /></td>
				<td><c:out value="${e.stdName}" /></td>
				<td><c:out value="${e.stdSal}" /></td>
				<td><c:out value="${e.stdPwd}" /></td>
				<td><c:out value="${e.stdGen}" /></td>
				<td><c:out value="${e.stdAddr}" /></td>
				<td><c:out value="${e.stdLang}" /></td>
				<td><c:out value="${e.stdCntry}" /></td>
				<td>
					<a href="delete?stdId=${e.stdId}">DELETE</a>
				</td>
			</tr>
		</c:forEach>
	</table>
${message}	
</body>
</html>


