
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>







<table width="100%" border="0" cellspacing="0" cellpadding="0">
   
   <bean:define id="dynAuthPolicyInstBean" name="dynAuthPolicyInstData" scope="request" type="com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData" />
    
    <tr> 
    
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.viewsummary"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message  key="general.name" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="dynAuthPolicyInstBean" property="name"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message  key="general.description" /></td>
            <td class="tblcol" width="70%" height="20%"><%=EliteUtility.formatDescription(dynAuthPolicyInstBean.getDescription()) %>&nbsp;</td>
          </tr>
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.ruleset" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="dynAuthPolicyInstBean" property="ruleSet"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message key="general.status" /></td>
			<logic:equal name="dynAuthPolicyInstBean" property="status" value="CST01">
			    <td class="tblcol" width="70%" height="20%"><img src="<%=basePath%>/images/active.jpg"/>&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.active" /></td>
			</logic:equal>
			<logic:equal name="dynAuthPolicyInstBean" property="status" value="CST02">
			    <td class="tblcol" width="70%" height="20%"><img src="<%=basePath%>/images/deactive.jpg"/>&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.inactive" /></td>
			</logic:equal>
          </tr>
		</table>
		</td>
    </tr>
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
   </tr>
</table>


