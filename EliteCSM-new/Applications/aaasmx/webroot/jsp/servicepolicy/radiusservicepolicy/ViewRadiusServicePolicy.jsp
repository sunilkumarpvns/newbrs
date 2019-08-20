<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<%
	String sessionManagerName= (String)request.getAttribute("sessionManagerName"); 
%>
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
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.authentication" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="radServicePolicyDataBean" property="authMsg"/>&nbsp;</td>
          </tr>
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.accounting" />
			</td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="radServicePolicyDataBean" property="acctMsg"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.authpolicy.ruleset" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="radServicePolicyDataBean" property="authRuleset"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.acctpolicy.ruleset" /> </td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="radServicePolicyDataBean" property="acctRuleset"/>&nbsp;</td>
          </tr>
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.validatepacket" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="radServicePolicyDataBean" property="validatepacket"/>&nbsp;</td>
          </tr>
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.defaultauthresponsebehavior" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3">
            	<bean:write name="radServicePolicyDataBean" property="defaultAuthResBehavior"/>
            	&nbsp;
            </td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.hotlinepolicy" /></td>
            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="radServicePolicyDataBean" property="hotlinePolicy"/>&nbsp;</td>
          </tr>
          <tr> 
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.defaultacctresponsebehavior" /></td>
	            <td class="tblcol" width="70%" height="20%" colspan="3">
	            	<bean:write name="radServicePolicyDataBean" property="defaultAcctResBehavior"/>
	            	&nbsp;
	            </td>
          </tr>
          <tr> 
           		<td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.sessionmanager" /></td>
            	<td class="tblcol" width="70%" height="20%" colspan="3">
            		<% if( sessionManagerName != null && sessionManagerName.length() > 0 && "--None--".equals(sessionManagerName) == false ){ %>
            			<span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="radServicePolicyDataBean" property="sessionManagerId"/>','<%=sessionManagerName%>','<%=EliteViewCommonConstant.SESSION_MANAGER%>');"><%=sessionManagerName%></span>
            		<%} %>
            		&nbsp;
            	</td>
          </tr>
           <tr> 
	            <td class="tblfirstcol" width="30%" height="20%">	
	            	<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.resattrs" /> 
				</td>
            	<td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="radServicePolicyDataBean" property="authResponseAttributes"/>&nbsp;</td>
          </tr>
          
          <tr> 
	            <td class="tblfirstcol" width="30%" height="20%">	
	            	<bean:message bundle="servicePolicyProperties" key="servicepolicy.acctpolicy.resattrs" /> 
				</td>
            	<td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="radServicePolicyDataBean" property="acctResponseAttributes"/>&nbsp;</td>
          </tr>
          
          
          <tr> 
	            <td class="tblfirstcol" width="30%" height="20%">	
	            	<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.useridentity" /> 
				</td>
            	<td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="radServicePolicyDataBean" property="userIdentity"/>&nbsp;</td>
          </tr>
           <tr> 
	            <td class="tblfirstcol" width="30%" height="20%">	
	            	<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.authttributes" /> 
				</td>
            	<td class="tblcol" width="70%" height="20%" colspan="3">
            		<bean:write name="radServicePolicyDataBean" property="authAttribute"/>&nbsp;
            	</td>
          </tr>
           <tr> 
	            <td class="tblfirstcol" width="30%" height="20%">	
	            	<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.acctttributes" /> 
				</td>
            	<td class="tblcol" width="70%" height="20%" colspan="3">
            		<bean:write name="radServicePolicyDataBean" property="acctAttribute"/>&nbsp;
            	</td>
          </tr>
           <tr> 
	            <td class="tblfirstcol" width="30%" height="20%">	
	            	<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.cui" /> 
				</td>
            	<td class="tblcol" width="70%" height="20%" colspan="3">
            		<bean:write name="radServicePolicyDataBean" property="cui"/>&nbsp;
            	</td>
          </tr>
           <tr> 
	            <td class="tblfirstcol" width="30%" height="20%">	
	            	<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.advancedcuiexpression" /> 
				</td>
            	<td class="tblcol" width="70%" height="20%" colspan="3">
            		<bean:write name="radServicePolicyDataBean" property="advancedCuiExpression"/>&nbsp;
            	</td>
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


