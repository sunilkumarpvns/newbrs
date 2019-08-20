<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>

<%
String path = request.getContextPath();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>EliteCodeGen</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="<%=path%>/css/style.css" rel="stylesheet" type="text/css">
<link href="../css/style.css" rel="stylesheet" type="text/css">
</head>

<body>

<table width="100%" height="100%">
  <tr>
    <td height="53" colspan="3">&nbsp;</td>
  </tr>
  <tr>
    <td width="20%">&nbsp;</td>
    <td width="60%" align="center">
    <form action="<%=path%>/viewTableList.do" method="get">
      <table width="100%" height="25%"  border="0" align="center" cellpadding="0" cellspacing="0">
        
          <tr style="height: 20%">
            <td >            
            <td >
            <td colspan="2"> 
          </tr>
          <tr>
            <td height="16%" colspan="4"><div align="center" class="title">Welcome to EliteCodeGen</div></td>
          </tr>
          <tr>
            <td height="19%" colspan="4">&nbsp;</td>
          </tr>
          <tr>
            <td width="10%">&nbsp;</td>
		    <td width="14%" height="19%" align="left">JDBC Url </td>
            <td colspan="2" align="left">
              <input name="strJdbcUrl" type="text" id="strJdbcUrl3" size="40" value="jdbc:oracle:thin:@192.168.1.171:1521:orcl92">
            </td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td height="21%" align="left"> Driver Name </td>
            <td colspan="2" align="left"> 
              <select name="strDriverName" id="strDriverName">
                <option value="oracle.jdbc.driver.OracleDriver">oracle.jdbc.driver.OracleDriver</option>
                <option value="oracle.jdbc.OracleDriver">oracle.jdbc.OracleDriver</option>
                <option value="sun.jdbc.odbc.JdbcOdbcDriver">sun.jdbc.odbc.JdbcOdbcDriver</option>
                <option value="org.gjt.mm.mysql.Driver">org.gjt.mm.mysql.Driver</option>
                <option value="COM.ibm.db2.jdbc.app.DB2Driver">COM.ibm.db2.jdbc.app.DB2Driver</option>
             
              </select>
            </td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td height="19%" align="left"> User Name </td>
            <td colspan="2" align="left">
              <input name="strUserName" type="text" size="40" id="strUserName" value="ELITERADIUSDEV501">
            </td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td height="25%" align="left"> Password</td>
            <td colspan="2" align="left">
              <input name="strPassword" type="text"  size="40" id="strPassword" value="ELITERADIUSDEV501">
            </td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td height="25%" align="left"> Schma</td>
            <td width="46%" align="left"><input name="strSchmaName"  size="40" type="text" id="strSchmaName" value="ELITERADIUSDEV501">            </td>
            <td width="30%" align="left">&nbsp;</td>
          </tr>
          
          <tr>
            <td>&nbsp;</td>
            <td height="25%" align="left">&nbsp;</td>
            <td width="46%" align="left">&nbsp;</td>
            <td width="30%" align="left">&nbsp;</td>
          </tr>
          
          <tr>
            <td height="25%" colspan="3">&nbsp;</td>
            <td height="25%"><input type="submit" name="Submit" value="Next"></td>
          </tr>
          <tr>
            <td height="25%" colspan="4" align="center">&nbsp;</td>
          </tr>
          <tr>
            <td height="25%" colspan="4" align="center"><FONT class="error" ><html:errors /></FONT></td>
          </tr>
          <tr style="height: 20%">
            <td >            
            <td > 
            <td colspan="2"> 
          </tr>
        
    </table>
    </form>
    </td>
    <td width="20%">&nbsp;</td>
  </tr>
  <tr style="height: 20%">
    <td></td>
  </tr>
  <tr>
    <td height="20%" colspan="3"></td>
  </tr>
</table>
</body>
</html>
