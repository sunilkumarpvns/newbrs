<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
	<tr> 
		<td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="datasourceResources" key="ldap.viewinformation"/></td>
	</tr>
	<tr> 
		<td class="tblfirstcol"  height="20%"><bean:message bundle="datasourceResources" key="ldap.name" /></td>
		<td class="tblrows"  colspan="3" height="20%"><bean:write name="ldapDatasourceData" property="name"/></td>
	</tr>
	<tr> 
		<td class="tblfirstcol" width="25%"  height="20%"><bean:message bundle="datasourceResources" key="ldap.address" /></td>
		<td class="tblrows" colspan="3" height="20%" ><bean:write name="ldapDatasourceData" property="address"/>&nbsp;</td>
	</tr>
	<tr> 
  		<td class="tblfirstcol"  height="20%"><bean:message bundle="datasourceResources" key="ldap.administrator" /></td>
		<td class="tblrows"  colspan="3" height="20%" ><bean:write name="ldapDatasourceData" property="administrator"/></td>
	</tr>				 				 
	<tr>
		<td class="tblfirstcol"  height="20%"><bean:message bundle="datasourceResources" key="ldap.timeout" /></td>
		<td class="tblrows" colspan="3" height="20%"><bean:write name="ldapDatasourceData" property="timeout"/></td>
	</tr>
	<tr>
		<td class="tblfirstcol"  height="30%"><bean:message bundle="datasourceResources" key="ldap.statuscheckduration" /></td>
		<td class="tblrows" colspan="3" height="20%" ><bean:write name="ldapDatasourceData" property="statusCheckDuration"/></td>
	</tr>             
	<tr> 
		<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="datasourceResources" key="ldap.minimumpool" /></td>
		<td class="tblrows" width="25%" height="20%"><bean:write name="ldapDatasourceData" property="minimumPool"/></td>
		<td class="tblrows" width="25%" height="20%"><bean:message bundle="datasourceResources" key="ldap.maximumpool" /></td>
		<td class="tblrows" width="25%" height="20%" ><bean:write name="ldapDatasourceData" property="maximumPool"/></td>
	</tr>
	<tr> 
		<td class="tblfirstcol"  height="20%"><bean:message bundle="datasourceResources" key="ldap.userdnprefix" /></td>
		<td class="tblrows" colspan="3" height="20%" ><bean:write name="ldapDatasourceData" property="userDNPrefix"/></td>
	</tr>
	<tr>
    	<td class="tblfirstcol"  height="20%"><bean:message bundle="datasourceResources" key="ldap.ldapsizelimit" /></td>
		<td class="tblrows" colspan="3" height="20%" ><bean:write name="ldapDatasourceData" property="ldapSizeLimit"/></td>
	</tr>
	<tr>
    	<td class="tblfirstcol"  height="20%"><bean:message bundle="datasourceResources" key="ldap.ldapversion" /></td>
		<td class="tblrows" colspan="3" height="20%" ><bean:write name="ldapDatasourceData" property="ldapVersion"/>&nbsp;</td>
	</tr>
	<%--tr>
    	<td class="tblfirstcol"  height="20%"><bean:message bundle="datasourceResources" key="ldap.searchfilter" /></td>
		<td class="tblrows" colspan="3" height="20%" ><bean:write name="ldapDatasourceData" property="searchFilter"/>&nbsp;</td>
	</tr--%>   
</table>
