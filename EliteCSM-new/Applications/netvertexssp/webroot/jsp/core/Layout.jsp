

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
   <head>
   <title>Service Selection Portal</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<%=request.getContextPath() %>/css/stylesheet_innerpages.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath() %>/css/messagestyle.css" rel="stylesheet" type="text/css" />
	<link href="<%=request.getContextPath()%>/css/stylesheet.css" rel="stylesheet" type="text/css" />
	<script language="javascript" src="<%=request.getContextPath()%>/js/validation.js"></script>
   </head>
	<titl    
    <body>
        <table border="0" cellpadding="2" cellspacing="2" align="center" width="100%" height="100%">
            <tr>
                <td height="125px" colspan="3">
                    <tiles:insert attribute="header"/>
                </td>
            </tr>
            <tr>
                <td width="200px" height="400px" valign="top">
                    <tiles:insert attribute="leftPanel" />
                </td>
                <td valign="top" >
                    <tiles:insert attribute="body" />
                </td>
                <td width="200px" valign="top">
                    <tiles:insert attribute="rightPanel" />
                </td>
            </tr>
            <tr>
                <td height="75px" colspan="3">
                    <tiles:insert attribute="footer" />
                </td>
            </tr>
        </table>
    </body>
</html>
