<table width="100%" border="0" cellspacing="0" cellpadding="0">
 
  					<tr> 
    						<td> 
    						  <table width="100%" border="0" cellspacing="0" cellpadding="0">
        						<tr> 
          						<td class="tblheader-bold" height="20%" colspan="2">
          						<bean:message bundle="driverResources" key="driver.view"/></td>
        						</tr>  
        						 <tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="2">
							<bean:message bundle="driverResources" key="driver.driverinstancedetails" /></td>
						 </tr>  					
        						 <tr>
							<td class="tblfirstcol" width="20%" height="20%" >
							<bean:message bundle="driverResources" key="driver.instname" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateWebServiceAuthDriverForm" property="driverInstanceName"/></td>
						 </tr>   
			    
			   			 <tr>
							<td class="tblfirstcol" width="20%" height="20%">
							<bean:message bundle="driverResources" key="driver.instdesc" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateWebServiceAuthDriverForm" property="driverInstanceDesc"/></td>
												
						</tr>
				
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="2">
							<bean:message bundle="driverResources" key="driver.driverdetails" /></td>
						</tr>   
			       
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.webserviceauthdriver.serviceaddress" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateWebServiceAuthDriverForm" property="serviceAddress"/></td>
						</tr>
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.webserviceauthdriver.maxquerytimeoutcount" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateWebServiceAuthDriverForm" property="maxQueryTimeoutCnt"/></td>
						</tr>
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.webserviceauthdriver.statuschkduration" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateWebServiceAuthDriverForm" property="statusChkDuration"/></td>
						</tr>
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.useridentityattributes" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateWebServiceAuthDriverForm" property="userIdentityAttributes"/></td>
						</tr>						
</table>
</td>
</tr>
</table>
