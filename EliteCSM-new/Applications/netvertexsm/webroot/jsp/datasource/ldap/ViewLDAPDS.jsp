<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="java.util.*" %>
<%@ page import=" com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData" %>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility" %> 
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant" %>

<%
	LDAPDatasourceData ldapDatasourceData = (LDAPDatasourceData)session.getAttribute("ldapDatasourceData");
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
    
    <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr>
          	<td class="tblheader-bold" colspan="2"><bean:message bundle="datasourceResources" key="ldap.viewinformation" /></td>
          </tr>
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="datasourceResources" key="ldap.name" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="ldapDatasourceData" property="name"/></td>
          </tr>
          
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="ldap.timeout" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="ldapDatasourceData" property="timeout"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="ldap.address" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="ldapDatasourceData" property="address"/>&nbsp;</td>
          </tr>  
          
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="ldap.administrator" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="ldapDatasourceData" property="administrator"/>&nbsp;</td>
          </tr> 
            
           
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="ldap.statuscheckduration" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="ldapDatasourceData" property="statusCheckDuration"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="ldap.userdnprefix" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="ldapDatasourceData" property="userDNPrefix"/>&nbsp;</td>
          </tr>  
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="ldap.ldapsizelimit" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="ldapDatasourceData" property="ldapSizeLimit"/>&nbsp;</td>
          </tr>  
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="ldap.minimumpool" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="ldapDatasourceData" property="minimumPool"/>&nbsp;</td>
          </tr>  
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="ldap.maximumpool" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="ldapDatasourceData" property="maximumPool"/>&nbsp;</td>
          </tr> 
          <tr>
				<td class="tbllabelcol" width="30%" height="20%" ><bean:message key="general.createddate" /></td>
				<td class="tblcol" width="70%" height="20%" ><%=EliteUtility.dateToString(ldapDatasourceData.getCreatedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>					
		</tr>
		<tr>
				<td class="tbllabelcol" width="30%" height="20%" ><bean:message key="general.lastmodifieddate" /></td>
				<td class="tblcol" width="70%" height="20%" ><%=EliteUtility.dateToString(ldapDatasourceData.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>					
		</tr>	    
            
        </table>
		</td>
    </tr>
</table>








