<table width="100%" border="0" cellspacing="0" cellpadding="0">
 
  					<tr> 
    						<td valign="top" align="right"> 
    						  <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
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
							<bean:write name="updateDiameterUserFileAuthDriverForm" property="driverInstanceName"/></td>
						 </tr>   
			    
			   			 <tr>
							<td class="tblfirstcol" width="20%" height="20%">
							<bean:message bundle="driverResources" key="driver.instdesc" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateDiameterUserFileAuthDriverForm" property="driverInstanceDesc"/></td>
												
						</tr>
				
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="2">
							<bean:message bundle="driverResources" key="driver.driverdetails" /></td>
						</tr>   
			       
				
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.fileLocations" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateDiameterUserFileAuthDriverForm" property="fileLocations"/></td>
						</tr>
						<tr>
							<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.userfileauthdriver.expirydate" /></td>
							<td class="tblcol" width="30%" height="20%" >
							<bean:write name="updateDiameterUserFileAuthDriverForm" property="expiryDateFormat"/></td>
						</tr>				
			
</table>
</td>
</tr>
</table>