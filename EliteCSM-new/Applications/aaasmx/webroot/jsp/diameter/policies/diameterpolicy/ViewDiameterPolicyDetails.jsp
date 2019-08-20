<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <bean:define id="diameterPolicyInstDataBean" name="diameterPolicyData" scope="request" type="com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData" />
    <tr> 
    
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%">
            <bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewsummary"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameterpolicy.name" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="diameterPolicyInstDataBean" property="name"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameterpolicy.description" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="diameterPolicyInstDataBean" property="description"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameterpolicy.checkitems.expression" /></td>
            <td class="tblcol" width="70%" height="20%" valign="top"><textarea rows="4" cols="60" style="width: 100%" readonly="readonly"><bean:write name="diameterPolicyInstDataBean" property="checkItem"/></textarea></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameterpolicy.rejectitems.expression" /></td>
            <td class="tblcol" width="70%" height="20%" valign="top"><textarea rows="4" cols="60" style="width: 100%" readonly="readonly"><bean:write name="diameterPolicyInstDataBean" property="rejectItem"/></textarea></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="diameterResources" key="diameterpolicy.replyitems.expression" /></td>
            <td class="tblcol" width="70%" height="20%" valign="top"><textarea rows="4" cols="60" style="width: 100%" readonly="readonly"><bean:write name="diameterPolicyInstDataBean" property="replyItem"/></textarea></td>
          </tr>           
		</table>
		</td>
    </tr>
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
   </tr>
</table>


