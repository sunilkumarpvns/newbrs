<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <bean:define id="diameterPolicyGroupDataBean" name="diameterPolicyGroup" scope="request" type="com.elitecore.elitesm.datamanager.diameter.diameterpolicygroup.data.DiameterPolicyGroup" />
    <tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewsummary"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources"  key="diameterpolicy.diameterpolicygroup.name" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="diameterPolicyGroupDataBean" property="policyName"/></td>
          </tr>
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameterpolicy.diameterpolicygroup.expression" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="diameterPolicyGroupDataBean" property="expression"/>&nbsp;</td>
          </tr>
		</table>
		</td>
    </tr>
</table>