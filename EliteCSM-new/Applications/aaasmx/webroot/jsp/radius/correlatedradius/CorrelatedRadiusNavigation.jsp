<%--
  Created by IntelliJ IDEA.
  User: vijayrajsinh
  Date: 4/20/18
  Time: 12:22 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.elitecore.elitesm.datamanager.radius.correlatedradius.data.CorrelatedRadiusData" %>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td colspan="2" valign="top">
            <%
                int a =12;
                CorrelatedRadiusData correlatedRadiusData = (CorrelatedRadiusData)request.getAttribute("correlatedEsi");

                String navigationBasePath = request.getContextPath();

                String updateBasicDetails = navigationBasePath+"/updateCorrelatedRadius.do?id="+correlatedRadiusData.getId();
                String viewcorrelatedRadiusDataSummary = navigationBasePath+"/viewCorrelatedRadius.do?id="+correlatedRadiusData.getId();
                String viewcorrelatedRadiusDataHistory = navigationBasePath+"/viewCorrelatedRadiusHistory.do?id="+correlatedRadiusData.getId()+"&auditUid="+correlatedRadiusData.getAuditUId()+"&name="+correlatedRadiusData.getName();
            %>
            <table border="0" width="100%" cellspacing="0" cellpadding="0">
                <tr id=header1>
                    <td class="subLinksHeader" width="87%">
                        <bean:message key="general.action" />
                    </td>
                    <td class="subLinksHeader" width="13%">
                        <a href="javascript:void(0)" onClick="STB('UpdateRadiusPolicy');swapImages()">
                            <img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow">
                        </a>
                    </td>
                </tr>
                <tr valign="top">
                    <td colspan="2" id="backgr1">
                        <div>
                            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td class="subLinks">
                                        <a href="<%=updateBasicDetails%>" class="subLink">
                                            <bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.update.basicdetail" />
                                        </a>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </td>
                </tr>
                <tr id=header1>
                    <td class="subLinksHeader" width="87%">
                        <bean:message key="general.view" />
                    </td>
                    <td class="subLinksHeader" width="13%">
                        <a href="javascript:void(0)" onClick="STB('ViewRadiusPolicy');swapImages()">
                            <img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow">
                        </a>
                    </td>
                </tr>
                <tr valign="top">
                    <td colspan="2" id="backgr1">
                        <div>
                            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td class="subLinks">
                                        <a href="<%=viewcorrelatedRadiusDataSummary%>" class="subLink">
                                            <bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewsummary" />
                                        </a>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="subLinks">
                                        <a href="<%=viewcorrelatedRadiusDataHistory%>" class="subLink">
                                            <bean:message key="view.history" />
                                        </a>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
