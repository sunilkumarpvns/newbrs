<%@page import="com.elitecore.netvertexsm.datamanager.bitemplate.data.BITemplateData"%>
<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%
	BITemplateData biTemplateData=(BITemplateData)request.getAttribute("biData");
%>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right"> 
			<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
					<tr>
			<td class=tblheader-bold colspan="6" >View BI Template</td>
		</tr>
			<tr>
					<td width="773" valign="top">  					   
					<table cellpadding="0" cellspacing="0" border="0" width="100%" >
			        <tr>
						<td class="tblfirstcol" width="20%" height="20%"><bean:message bundle="biTemplateResources" key="bitemplate.name" /></td>
						<td class="tblrows" width="30%" height="20%" ><bean:write name="biTemplateForm" property="name"/>&nbsp;</td>
					</tr>   
					<tr>
						<td class="tblfirstcol" width="20%" height="20%" ><bean:message bundle="biTemplateResources" key="bitemplate.key" /></td>
						<td class="tblrows" width="30%" height="20%" ><bean:write name="biTemplateForm" property="bikey"/>&nbsp;</td>
					</tr>   			    
					<tr>
						<td class="tblfirstcol" width="20%" height="20%" ><bean:message bundle="biTemplateResources" key="bitemplate.description" /></td>
						<td class="tblrows" width="30%" height="20%" ><bean:write name="biTemplateForm" property="description"/>&nbsp;</td>
					</tr> 
					<tr>
						<td class="tblfirstcol" width="20%" height="20%" ><bean:message key="general.createddate" /></td>
						<td class="tblrows" width="30%" height="20%"  ><%=EliteUtility.dateToString(biTemplateData.getCreatedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>					
					</tr>
					<tr>
						<td class="tblfirstcol" width="20%" height="20%" ><bean:message key="general.lastmodifieddate" /></td>
						<td class="tblrows" width="30%" height="20%" ><%=EliteUtility.dateToString(biTemplateData.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td> 				
					</tr>	    					   			
					</table>
					
			
				</td>
    </tr>
      <tr><td width="10" class="small-gap">&nbsp;</td></tr>
		<tr>
			<td class=tblheader-bold colspan="6" ><bean:message bundle="biTemplateResources" key="bitemplate.biceasubkey" /></td>
		</tr>
		
		

		<tr>
			<td valign="top" align="left"> 
			<table cellpadding="0" cellspacing="0" border="0" width="100%" align="left">
				<tr>
					<td align="left" class="tblheaderfirstcol" valign="top" width="50%"><bean:message bundle="biTemplateResources" key="bitemplate.subkey" /></td>
					<td align="left" class="tblheaderlastcol" valign="top" width="50%"><bean:message bundle="biTemplateResources" key="bitemplate.subkey.value" /></td>
				</tr>
		<logic:iterate id="biRelData" property="subKeyList" name="biTemplateForm" type="com.elitecore.netvertexsm.datamanager.bitemplate.data.BISubKeyData">
				<tr>
					<td align="left" class="tblfirstcol" valign="top" width="30%"><%=biRelData.getKey()%></td>
					<td align="left" class="tblrows" valign="top" width="30%"><%=biRelData.getValue()%></td>
				</tr>
		</logic:iterate>
	    <logic:empty  name="biTemplateForm" property="subKeyList">
	    <tr>
	    	<td colspan="2" align="center" class="tblfirstcol" ><bean:message bundle="datasourceResources" key="database.datasource.norecordfound"/> </td>
	    </tr>
	    </logic:empty>						
			</table>
			</td>
		</tr>

		<tr>
			<td width="10" class="">&nbsp;</td>
		</tr>   
</table>
</td>
</tr>
</table>