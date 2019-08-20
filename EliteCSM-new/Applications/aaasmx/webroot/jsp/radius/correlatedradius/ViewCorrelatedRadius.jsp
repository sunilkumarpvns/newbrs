<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td valign="top" align="right">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" >
                <tr>
                    <td class="tblheader-bold" colspan="5" height="20%">
                        <bean:message bundle="radiusResources" key="correlated.radius.view"/>
                    </td>
                </tr>
                <tr>
                    <td class="tblfirstcol" width="25%" ><bean:message bundle="radiusResources" key="correlated.radius.name" /></td>
                    <td class="tblcol" width="*" ><bean:write name="correlatedEsi" property="name"/>&nbsp;</td>
                </tr>
                <tr>
                    <td class="tblfirstcol" width="25%" ><bean:message bundle="radiusResources" key="correlated.radius.description" /></td>
                    <td class="tblcol" width="*" colspan="3"><bean:write name="correlatedEsi" property="description"/>&nbsp;</td>
                </tr>
                <tr>
                    <td class="tblfirstcol" width="25%" ><bean:message bundle="radiusResources" key="correlated.radius.authesi" /></td>
                    <td class="tblcol" width="*" colspan="3">
                        <logic:notEmpty name="correlatedEsi" property="authEsiName">
					    <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="correlatedEsi" property="authEsiId"/>','<bean:write name="correlatedEsi" property="authEsiName"/>','<%=EliteViewCommonConstant.EXTENDED_RADIUS%>');">
					     	<bean:write name="correlatedEsi" property="authEsiName"/>
					   </span>
                        </logic:notEmpty>
                        &nbsp;
                    </td>
                </tr>
                <tr>
                    <td class="tblfirstcol" width="25%" ><bean:message bundle="radiusResources" key="correlated.radius.acctesi" /></td>
                    <td class="tblcol" width="*" colspan="3">
                        <logic:notEmpty name="correlatedEsi" property="acctEsiName">
					    <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="correlatedEsi" property="acctEsiId"/>','<bean:write name="correlatedEsi" property="acctEsiName"/>','<%=EliteViewCommonConstant.EXTENDED_RADIUS%>');">
					     	<bean:write name="correlatedEsi" property="acctEsiName"/>
					   </span>
                        </logic:notEmpty>
                        &nbsp;
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td class="small-gap" colspan="2">&nbsp;</td>
    </tr>
</table>