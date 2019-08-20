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
									<td class="tblfirstcol" width="25%" height="20%" >
									<bean:message bundle="driverResources" key="driver.instname" /></td>
									<td class="tblcol" width="30%" height="20%" >
									<bean:write name="updateRadiusLDAPAuthDriverForm" property="driverInstanceName"/></td>
								 </tr>   
					    
					   			 <tr>
									<td class="tblfirstcol" width="25%" height="20%">
									<bean:message bundle="driverResources" key="driver.instdesc" /></td>
									<td class="tblcol" width="30%" height="20%" >
									<bean:write name="updateRadiusLDAPAuthDriverForm" property="driverDesp"/>&nbsp;</td>
														
								</tr>
						
								<tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="2">
									<bean:message bundle="driverResources" key="driver.driverdetails" /></td>
								</tr>   
					       
						
								<tr>
									<td align="left" class="tblfirstcol" valign="top" width="25%">
										<bean:message bundle="driverResources" key="driver.ds" /></td>
									<td class="tblcol" width="30%" height="20%" >
										<logic:notEmpty name="updateRadiusLDAPAuthDriverForm" property="ldapName">
						            		<span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="updateRadiusLDAPAuthDriverForm" property="ldapDsId"/>','<bean:write name="updateRadiusLDAPAuthDriverForm" property="ldapName"/>','<%=EliteViewCommonConstant.LDAP_DATASOURCE%>');">
						            			<bean:write name="updateRadiusLDAPAuthDriverForm" property="ldapName"/>
						            		</span>
						            	</logic:notEmpty>
						            </td>
								</tr>
						      
								<tr>
									<td align="left" class="tblfirstcol" valign="top" width="25%">
										<bean:message bundle="driverResources" key="driver.expirydatapattern" /></td>
									<td class="tblcol" width="30%" height="20%" >
									<bean:write name="updateRadiusLDAPAuthDriverForm" property="expiryDatePattern"/>&nbsp;</td>
								</tr>
								
								<tr>
									<td align="left" class="tblfirstcol" valign="top" width="25%">
										<bean:message bundle="driverResources" key="driver.pwd.decrypt.type" /></td>
									<td class="tblcol" width="30%" height="20%" >
									<bean:write name="updateRadiusLDAPAuthDriverForm" property="passwordDecryptType"/>&nbsp;</td>
								</tr>
								
								<tr>
									<td align="left" class="tblfirstcol" valign="top" width="25%">
										<bean:message bundle="driverResources" key="driver.ldapauthdriver.searchscope" /></td>
									<td class="tblcol" width="30%" height="20%" >
											<logic:equal value="2" name="updateRadiusLDAPAuthDriverForm" property="searchScope">
												SCOPE_SUB
											</logic:equal>
											<logic:equal value="1" name="updateRadiusLDAPAuthDriverForm" property="searchScope">
												SCOPE_ONE
											</logic:equal>
											<logic:equal value="0" name="updateRadiusLDAPAuthDriverForm" property="searchScope">
												SCOPE_BASE
											</logic:equal>&nbsp;
									</td>
								</tr>
								
								<tr>
									<td align="left" class="tblfirstcol" valign="top" width="25%">
										<bean:message bundle="driverResources" key="driver.max.exec.time" /></td>
									<td class="tblcol" width="30%" height="20%" >
									<bean:write name="updateRadiusLDAPAuthDriverForm" property="queryMaxExecTime"/>&nbsp;</td>
								</tr>
								
								<tr>
									<td align="left" class="tblfirstcol" valign="top" width="25%">
										<bean:message bundle="driverResources" key="driver.pwd.decrypt.type" /></td>
									<td class="tblcol" width="30%" height="20%" >
									<bean:write name="updateRadiusLDAPAuthDriverForm" property="passwordDecryptType"/>&nbsp;</td>
								</tr>
								
								<tr>
									<td align="left" class="tblfirstcol" valign="top" width="25%">
										<bean:message bundle="driverResources" key="driver.max.query.timeout.count" /></td>
									<td class="tblcol" width="30%" height="20%" >
									<bean:write name="updateRadiusLDAPAuthDriverForm" property="maxQueryTimeoutCnt"/>&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="tblfirstcol" valign="top" width="25%">
									  <bean:message bundle="driverResources" key="driver.useridentityattributes" />
									<td class="tblcol" width="30%" height="20%" >
									  <bean:write name="updateRadiusLDAPAuthDriverForm" property="userIdentityAttributes"/>&nbsp;
									</td>
								</tr>
								<tr>
									<td align="left" class="tblfirstcol" valign="top" width="25%">
									  <bean:message bundle="driverResources" key="driver.ldapauthdriver.searchfilter" />
									<td class="tblcol" width="30%" height="20%" >
									  <bean:write name="updateRadiusLDAPAuthDriverForm" property="searchFilter"/>&nbsp;
									</td>
								</tr>
</table>