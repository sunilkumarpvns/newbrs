
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>





	
<table width="100%" border="0" cellspacing="0" cellpadding="0">
   
   <bean:define id="dynAuthPolicyInstBean" name="dynAuthPolicyInstData" scope="request" type="com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData" />
 
    <tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0"  >
          <tr> 
            <td class="tblfirstcol" width="30%" ><bean:message bundle="servicePolicyProperties"  key="servicepolicy.dynauthpolicy.resattrs" /></td>
            <td class="tblcol" width="70%"><bean:write name="dynAuthPolicyInstBean" property="responseAttributes"/>&nbsp; </td>
          </tr>
		  <tr> 
            <td class="tblfirstcol"><bean:message bundle="servicePolicyProperties"  key="servicepolicy.dynauthpolicy.tablename" /></td>
            <td class="tblcol" ><bean:write name="dynAuthPolicyInstBean" property="tableName"/>&nbsp; </td>
          </tr>
          <tr> 
            <td class="tblfirstcol"><bean:message bundle="servicePolicyProperties"  key="servicepolicy.dynauthpolicy.dbfailureaction" /></td>
            <td class="tblcol" ><bean:write name="dynAuthPolicyInstBean" property="dbFailureAction"/>&nbsp; </td>
          </tr>
		 <tr> 
            <td class="tblfirstcol"><bean:message bundle="servicePolicyProperties"  key="servicepolicy.dynauthpolicy.eligiblesession" /></td>
            <td class="tblcol" >
             <logic:equal value="1" name="dynAuthPolicyInstBean" property="eligibleSession">
             	<bean:message bundle="servicePolicyProperties"  key="servicepolicy.dynauthpolicy.eligiblesession.none" />
             </logic:equal>
              <logic:equal value="2" name="dynAuthPolicyInstBean" property="eligibleSession">
             	<bean:message bundle="servicePolicyProperties"  key="servicepolicy.dynauthpolicy.eligiblesession.recent" />
             </logic:equal>
              <logic:equal value="3" name="dynAuthPolicyInstBean" property="eligibleSession">
             	<bean:message bundle="servicePolicyProperties"  key="servicepolicy.dynauthpolicy.eligiblesession.oldest" />
             </logic:equal>
              <logic:equal value="4" name="dynAuthPolicyInstBean" property="eligibleSession">
             	<bean:message bundle="servicePolicyProperties"  key="servicepolicy.dynauthpolicy.eligiblesession.all" />
             </logic:equal>
            &nbsp; </td>
          </tr>
          <tr> 
            <td class="tblfirstcol"><bean:message bundle="servicePolicyProperties"  key="servicepolicy.dynauthpolicy.eventtimestamp" /></td>
            <td class="tblcol" ><bean:write name="dynAuthPolicyInstBean" property="eventTimestamp"/>&nbsp; </td>
          </tr>
            <tr> 
            <td class="tblfirstcol"><bean:message bundle="servicePolicyProperties"  key="servicepolicy.dynauthpolicy.validatepacket" /></td>
            <td class="tblcol" ><bean:write name="dynAuthPolicyInstBean" property="validatePacket"/>&nbsp; </td>
          </tr>
          
          </table>
		</td>
    </tr>
    
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
   </tr>
</table>
 

