<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <bean:define id="radServicePolicyDataBean" name="radServicePolicyData" scope="request" type="com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData" />
    <tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewsummary"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message  key="general.name" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="radServicePolicyDataBean" property="name"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message  key="general.description" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><%=EliteUtility.formatDescription(radServicePolicyDataBean.getDescription()) %>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message key="general.status" /></td>
			<logic:equal name="radServicePolicyDataBean" property="status" value="ACTIVE">
			    <td class="tblcol" width="70%" height="20%" colspan="3"><img src="<%=basePath%>/images/active.jpg"/>&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.active" /></td>
			</logic:equal>
			<logic:equal name="radServicePolicyDataBean" property="status" value="INACTIVE">
			    <td class="tblcol" width="70%" height="20%" colspan="3"><img src="<%=basePath%>/images/deactive.jpg"/>&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.inactive" /></td>
			</logic:equal>
          </tr>
		</table>
		</td>
    </tr>
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
   </tr>
</table>


