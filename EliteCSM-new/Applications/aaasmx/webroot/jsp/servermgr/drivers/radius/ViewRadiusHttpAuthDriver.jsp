  <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >            						  	
      						  	 <tr> 
            						<td class="tblheader-bold" height="20%" colspan="2"><bean:message bundle="driverResources" key="driver.view"/></td>
          						</tr>  
          						 <tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="2">
									<bean:message bundle="driverResources" key="driver.driverinstancedetails" /></td>
								 </tr>  					
          						 <tr>
									<td class="tblfirstcol" width="20%" height="20%" >
									<bean:message bundle="driverResources" key="driver.instname" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusHttpAuthDriverForm" property="driverInstanceName"/></td>
								 </tr>   
					    
					   			 <tr>
									<td class="tblfirstcol" width="20%" height="20%">
									<bean:message bundle="driverResources" key="driver.instdesc" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusHttpAuthDriverForm" property="driverInstanceDesc"/></td>
														
								</tr>
						
								<tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="2">
									<bean:message bundle="driverResources" key="driver.driverdetails" /></td>
								</tr>   
					       
						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.httpurl" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusHttpAuthDriverForm" property="http_url"/></td>
								</tr>
						
														
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.statuscheckduration" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusHttpAuthDriverForm" property="statusCheckDuration"/></td>
								
							   </tr>						
						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.querytimeoutcount" /></td>
										<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusHttpAuthDriverForm" property="maxQueryTimeoutCount"/></td>															
							   </tr>
							   
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.expirydatepatterns" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusHttpAuthDriverForm" property="expiryDateFormats"/></td>															
							   </tr>	
							   <tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.useridentityattributes" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusHttpAuthDriverForm" property="userIdentityAttributes"/></td>															
							   </tr>						   							  
</table>