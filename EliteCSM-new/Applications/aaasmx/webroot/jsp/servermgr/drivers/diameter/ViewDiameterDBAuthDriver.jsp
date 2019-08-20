  <%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
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
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateDiameterDBAuthDriverForm" property="driverInstanceName"/></td>
								 </tr>   
					    
					   			 <tr>
									<td class="tblfirstcol" width="20%" height="20%">
									<bean:message bundle="driverResources" key="driver.instdesc" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateDiameterDBAuthDriverForm" property="driverInstanceDesc"/></td>
														
								</tr>
						
								<tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="2">
									<bean:message bundle="driverResources" key="driver.driverdetails" /></td>
								</tr>   
					       
						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.ds" /></td>
									<td class="tblcol" width="30%" height="20%" >
										<logic:notEmpty name="updateDiameterDBAuthDriverForm" property="databaseName">
						            		<span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="updateDiameterDBAuthDriverForm" property="databaseId"/>','<bean:write name="updateDiameterDBAuthDriverForm" property="databaseName"/>','<%=EliteViewCommonConstant.DATABASE_DATASOURCE%>');">
						            			<bean:write name="updateDiameterDBAuthDriverForm" property="databaseName"/>
						            		</span>
						            	</logic:notEmpty>
									</td>
								</tr>
						
														
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.tablename" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateDiameterDBAuthDriverForm" property="tableName"/></td>
								
							   </tr>						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.querytimeout" /><bean:message key="general.seconds" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateDiameterDBAuthDriverForm" property="dbQueryTimeout"/></td>
							   </tr>
						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.querytimeoutcount" />
									</td>
									<td class="tblcol" width="30%" height="20%" >
										<bean:write name="updateDiameterDBAuthDriverForm" property="maxQueryTimeoutCount"/>
									</td>															
							   </tr>
							   <tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.profilelookupcolumn" />
									</td>
									<td class="tblcol" width="30%" height="20%" >
									    <bean:write name="updateDiameterDBAuthDriverForm" property="profileLookupColumn"/>
									</td>															
							   </tr>
							   <tr>
									<td align="left" class="tblfirstcol" valign="top">
									   <bean:message bundle="driverResources" key="driver.useridentityattributes" />
									</td>
									<td class="tblcol" width="30%" height="20%" >
									    <bean:write name="updateDiameterDBAuthDriverForm" property="userIdentityAttributes"/>
									</td>															
							   </tr>
</table>