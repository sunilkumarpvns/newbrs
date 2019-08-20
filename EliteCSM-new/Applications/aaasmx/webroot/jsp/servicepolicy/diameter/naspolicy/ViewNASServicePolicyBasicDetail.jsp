<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>


<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <bean:define id="nasPolicyInstDataBean" name="nasPolicyInstData" scope="request" type="com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData" />
    <tr> 
    
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%">
            <bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewsummary"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.name" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="nasPolicyInstDataBean" property="name"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.desp" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="nasPolicyInstDataBean" property="description"/>&nbsp;</td>
          </tr>
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.ruleset"/></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="nasPolicyInstDataBean" property="ruleSet"/></td>
          </tr>
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.sessionmanagement"/></td>
            <td class="tblcol" width="70%" height="20%">
            	<logic:equal value="false" name="nasPolicyInstDataBean" property="sessionManagement">False</logic:equal>
            	<logic:equal value="true" name="nasPolicyInstDataBean" property="sessionManagement">True</logic:equal>
            	&nbsp;
            </td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.requestmode"/></td>
            <td class="tblcol" width="70%" height="20%">
            <logic:equal value="1" name="nasPolicyInstDataBean" property="requestType">
            	Authenticate-Only
            </logic:equal>
            <logic:equal value="2" name="nasPolicyInstDataBean" property="requestType">
            	Authorize-Only
            </logic:equal>
            <logic:equal value="3" name="nasPolicyInstDataBean" property="requestType">
            	Authenticate and Authorize
            </logic:equal>
            &nbsp;</td>
          </tr>       
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.defaultresponsebehaviour"/></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="nasPolicyInstDataBean" property="defaultResponseBehaviour"/></td>
          </tr>
           <tr> 
          	 <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.defaultresponsebehaviourargument"/></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="nasPolicyInstDataBean" property="defaultResponseBehaviourArgument"/></td>
          </tr>
		</table>
		</td>
    </tr>
</table>


