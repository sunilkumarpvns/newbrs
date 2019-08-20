<%--
  Created by IntelliJ IDEA.
  User: vijayrajsinh
  Date: 4/20/18
  Time: 12:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<% String basePath = request.getContextPath(); %>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<table width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td width="<%=ConfigConstant.PAGELEFTSPACE%>">
            &nbsp;
        </td>
        <td>
            <table cellpadding="0" cellspacing="0" border="0" width="100%">
                <tr>
                    <td  cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td valign="top" align="right">
                                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td>
                                                <%@ include file="ViewCorrelatedRadius.jsp" %>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                                <td width="168" class="grey-bkgd" valign="top">
                                    <%@ include file="CorrelatedRadiusNavigation.jsp" %>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <%@ include file="/jsp/core/includes/common/Footer.jsp"%>
            </table>
        </td>
    </tr>
</table>
<script>
    setTitle('<bean:message bundle="radiusResources" key="correlated.radius.title"/>');
</script>
