<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>    
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
<table border="1">
  <tr>
  <th>ID</th>
  <th>NAME</th>
  <th>SAL</th>
  <th>DELETE</th>
  </tr>
  <c:forEach items="${emps}" var="e">
      <tr>
      <td><c:out value="${e.empId}" /></td>
      <td><c:out value="${e.empName}" /></td>
      <td><c:out value="${e.empSal}" /></td>
      <td>
					<a href="delete?empId=${e.empId}">DELETE</a>
	  </td>
      </tr>      
  </c:forEach>

</table>

</body>
</html>