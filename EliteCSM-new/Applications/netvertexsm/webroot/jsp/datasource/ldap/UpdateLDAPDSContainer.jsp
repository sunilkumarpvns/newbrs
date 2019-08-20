<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<%
	LDAPDatasourceData ldapDatasourceData = (LDAPDatasourceData)session.getAttribute("ldapDatasourceData");
%>
<script type="text/javascript">
$(document).ready(function(){
	setTitle('<bean:message bundle="datasourceResources" key="ldap.ldap" />');
});
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<tr>
    <td width="10" class="small-gap">&nbsp;</td>	
    	<td width="85%" class="box" >
    		<table width="100%" border="0" cellspacing="0" cellpadding="0">				
				<tr>
					<td valign="top">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
   
    					<tr> 
      						<td valign="top" align="right"> 
      						  <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          						<tr> 
            						<td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="datasourceResources" key="ldap.viewinformation"/></td>
          						</tr>
          						<tr> 
            						<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="ldap.name" /></td>
            						<td class="tblcol" width="20%" height="20%"><bean:write name="ldapDatasourceData" property="name"/></td>
            						<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="ldap.timeout" /></td>
          							<td class="tblcol" width="20%" height="20%"><bean:write name="ldapDatasourceData" property="timeout"/></td>
         						 </tr>
         						 <tr> 
            						<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="ldap.address" /></td>
				 					<td class="tblcol" width="20%" height="20%" ><bean:write name="ldapDatasourceData" property="address"/></td>
									<td class="tbllabelcol" width="30%" height="20%">&nbsp;-</td>
          							<td class="tblcol" width="20%" height="20%" >&nbsp;-</td>
          						</tr>         
          						<tr> 
            						
          						</tr>
          						<tr> 
            						<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="ldap.administrator" /></td>
          							<td class="tblcol" width="20%" height="20%" ><bean:write name="ldapDatasourceData" property="administrator"/></td>
          							<td class="tbllabelcol" width="30%" height="30%"><bean:message bundle="datasourceResources" key="ldap.statuscheckduration" /></td>
             						<td class="tblcol" width="20%" height="20%" ><bean:write name="ldapDatasourceData" property="statusCheckDuration"/></td>
          						</tr>             
            					<tr> 
             						
          						</tr>   
            					<tr> 
             						<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="ldap.userdnprefix" /></td>
             						<td class="tblcol" width="20%" height="20%" ><bean:write name="ldapDatasourceData" property="userDNPrefix"/></td>
             						<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="ldap.ldapsizelimit" /></td>
             						<td class="tblcol" width="20%" height="20%" ><bean:write name="ldapDatasourceData" property="ldapSizeLimit"/></td>
          						</tr>   
            					
          						<tr> 
            						<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="ldap.minimumpool" /></td>
             						<td class="tblcol" width="20%" height="20%"><bean:write name="ldapDatasourceData" property="minimumPool"/></td>
             						<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="ldap.maximumpool" /></td>
             						<td class="tblcol" width="20%" height="20%" ><bean:write name="ldapDatasourceData" property="maximumPool"/></td>
          						</tr> 
 
                
        					</table>
						</td>
    				</tr>
    
					</table>
				  </td>
				</tr>
				<tr>
					<td valign="bottom">
						<%@ include file="/jsp/datasource/ldap/UpdateLDAPDS.jsp"%>
					</td>
				</tr>
    		</table>
    	</td>	
		<td width="15%" class="grey-bkgd" valign="top">
			<%@ include file="/jsp/datasource/ldap/LDAPNavigation.jsp"%>
		</td>
    	
	</tr>
	 
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>		  

