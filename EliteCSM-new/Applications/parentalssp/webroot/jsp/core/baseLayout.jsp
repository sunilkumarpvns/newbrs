<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>NetVertex Parental Control</title>
    </head>
    <body>
        <table border="0" cellpadding="2" cellspacing="2" align="center" width="100%">
            <tr>
                <td colspan="3" width="100%">
                    <tiles:insert attribute="header" ignore="true" />
                </td>
            </tr>
            <tr>
                <td width="23%" valign="top">
                    <tiles:insert attribute="menu" />
                </td>
                 <td valign="top" width="57%">
                    <tiles:insert attribute="body" />
                </td>
                <td valign="top" width="20%" align="right">
                    <tiles:insert attribute="rightpanel" />
                </td>               
            </tr>
            <tr>
                <td width="100%" colspan="3">
                    <tiles:insert attribute="footer" />
                </td>
            </tr>
        </table>
    </body>
</html>
