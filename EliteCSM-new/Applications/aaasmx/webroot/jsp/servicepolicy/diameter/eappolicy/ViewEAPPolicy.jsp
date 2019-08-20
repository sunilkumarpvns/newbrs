<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>


<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <bean:define id="eapPolicyInstDataBean" name="eapPolicyData" scope="request" type="com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData" />
    <tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%">
            <bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewsummary"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.name" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="eapPolicyInstDataBean" property="name"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.desp" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="eapPolicyInstDataBean" property="description"/>&nbsp;</td>
          </tr>
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.ruleset"/></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="eapPolicyInstDataBean" property="ruleSet"/></td>
          </tr> 
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.sessionmanagement" /></td>
            <td class="tblcol" width="70%" height="20%">
            	<logic:equal value="true" name="eapPolicyInstDataBean" property="sessionManagement">True</logic:equal>
            	<logic:equal value="false" name="eapPolicyInstDataBean" property="sessionManagement">False</logic:equal>
            	&nbsp;
            </td>
          </tr> 
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.requestmode"/></td>
            <td class="tblcol" width="70%" height="20%">
            <logic:equal value="1" name="eapPolicyData" property="requestType">
            	Authenticate-Only
            </logic:equal>
            <logic:equal value="2" name="eapPolicyData" property="requestType">
            	Authorize-Only
            </logic:equal>
            <logic:equal value="3" name="eapPolicyData" property="requestType">
            	Authenticate and Authorize
            </logic:equal>
            &nbsp;</td>

          </tr>    
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.defaultresponsebehaviour"/></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="eapPolicyInstDataBean" property="defaultResponseBehaviour"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.defaultresponsebehaviourargument"/></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="eapPolicyInstDataBean" property="defaultResponseBehaviorArgument"/></td>
          </tr>      
          
		</table>
		</td>
    </tr>
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
   </tr>
</table>


