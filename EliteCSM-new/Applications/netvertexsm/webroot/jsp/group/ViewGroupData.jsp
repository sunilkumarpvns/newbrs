
<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<bean:define id="groupData" name="groupData" scope="request" type="com.elitecore.corenetvertex.sm.acl.GroupData" />
	 <tr>
		<td valign="top" align="right"> 
			<table cellpadding="0" cellspacing="0" border="0" width="97%">
 			<tr>
				<td class="tblheader-bold"  valign="top" colspan="3"  ><bean:message  bundle="groupDataMgmtResources" key="group.view.title"/> </td>
			</tr>			
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="groupDataMgmtResources" key="group.name" /></td> 
					<td align="left" class="tblrows" valign="top" colspan="2"> <bean:write name="groupData" property="name"/>&nbsp;</td>					
				  </tr>
			   	 			
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="groupDataMgmtResources" key="group.description" /></td> 
					<td align="left" class="tblrows" valign="top"  colspan="2" > <bean:write name="groupData" property="description"/>&nbsp;</td> 
				  </tr>
				    <tr><td class="small-gap">&nbsp;</td></tr><tr><td class="small-gap">&nbsp;</td></tr>
				  
			 	
			</table></td>
	</tr>
	</tr>
</table>
