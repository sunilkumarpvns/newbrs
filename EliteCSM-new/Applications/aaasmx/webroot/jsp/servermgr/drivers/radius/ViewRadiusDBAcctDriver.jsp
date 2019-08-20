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
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="driverInstanceName"/></td>
								 </tr>   
					    
					   			 <tr>
									<td class="tblfirstcol" width="20%" height="20%">
									<bean:message bundle="driverResources" key="driver.instdesc" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="driverDesp"/>&nbsp;</td>
														
								</tr>
						
								<tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="2">
									<bean:message bundle="driverResources" key="driver.driverdetails" /></td>
								</tr>   
					       
					      		<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.dbauthdriver.ds" /></td>
									<td class="tblcol" width="30%" height="20%" >
										<logic:notEmpty name="updateRadiusDBAcctDriverForm" property="databaseName">
						            		<span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="updateRadiusDBAcctDriverForm" property="databaseId"/>','<bean:write name="updateRadiusDBAcctDriverForm" property="databaseName"/>','<%=EliteViewCommonConstant.DATABASE_DATASOURCE%>');">
						            			<bean:write name="updateRadiusDBAcctDriverForm" property="databaseName"/>
						            		</span>
						            	</logic:notEmpty>
									</td>
								</tr>
						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.dsType" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="datasourceType"/></td>
								</tr>
						
														
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.querytimeout" /><bean:message key="general.seconds" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="dbQueryTimeout"/>&nbsp;</td>
								
							   </tr>						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.querytimeoutcount" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="maxQueryTimeoutCount"/>&nbsp;</td>
							   </tr>
																						
								<!-- 
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.storeTunnelStartRec" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="storeTunnelStartRec"/></td>															
							   </tr>                 				                				
                				<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.storeTunnelStopRec" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="storeTunnelStopRec"/></td>
								</tr>
						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.removeTunnelStopRec" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="removeTunnelStopRec"/></td>												
								</tr>						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.storeTunnelLinkStartRec" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="storeTunnelLinkStartRec"/></td>
								
							   </tr>						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.storeTunnelLinkStopRec" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="storeTunnelLinkStopRec"/></td>
							   </tr>
																
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.removeTunnelLinkStopRec" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="removeTunnelLinkStopRec"/></td>															
							   </tr> 
                				
                				
                				<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<<bean:message bundle="driverResources" key="driver.storeTunnelRejectRec" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="storeTunnelRejectRec"/></td>
								</tr>
						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.storeTunnelLinkRejectRec" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="storeTunnelLinkRejectRec"/></td>												
								</tr>	 -->					
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.multivalDelimeter" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="multivalDelimeter"/>&nbsp;</td>
								
							   </tr>
							   <tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="3">
									<bean:message bundle="driverResources" key="driver.cdrconf" /></td>
								</tr>
								 <tr>
									<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.cdrTablename" /></td>
										<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="cdrTablename"/>&nbsp;</td>															
							   </tr> 	
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.cdrIdDbField" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="cdrIdDbField"/>&nbsp;</td>															
							   </tr>                 				                													
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.cdrIdSeqName" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="cdrIdSeqName"/>&nbsp;</td>															
							   </tr>
							   
							   <tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.storeallcdr" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="storeAllCdr"/>&nbsp;</td>												
								</tr>
							    
							  	<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.storestprec" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="storeStopRec"/>&nbsp;</td>												
								</tr>				
								               				
								<tr>
										<td align="left" class="tblheader-bold" valign="top" colspan="3">
										<bean:message bundle="driverResources" key="driver.interimcdrconf" /></td>
								</tr>							
								
                				
               					<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.interimTblnm" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="interimTablename"/>&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.interimCdrIdDbField" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="interimCdrIdDbField"/>&nbsp;</td>															
							   </tr>                 				                													
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.interimCdrIdSeqName" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="interimCdrIdSeqName"/>&nbsp;</td>															
							   </tr>
							   <tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.storeInterimRec" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="storeInterimRec"/>&nbsp;</td>
								
							   </tr>						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.removeInterimOnStop" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="removeInterimOnStop"/>&nbsp;</td>
							   </tr>
							   
							   <tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="3">
									<bean:message bundle="driverResources" key="driver.fielddetails" /></td>
								</tr>   
							   <tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.callStartFieldName" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="callStartFieldName"/>&nbsp;</td>															
							   </tr>                 				                													
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.callEndFieldName" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="callEndFieldName"/>&nbsp;</td>															
							   </tr>
							   <tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.createDateFieldName" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="createDateFieldName"/>&nbsp;</td>															
							   </tr>                 				                													
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.lastModifiedDateFieldName" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="lastModifiedDateFieldName"/>&nbsp;</td>															
							   </tr>
							   
							   
							   <tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="3">
									<bean:message bundle="driverResources" key="driver.cdrtimestampdetails" /></td>
								</tr> 
							   
							   <tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.dbDateField" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="dbDateField"/>&nbsp;</td>
							   </tr>
							   
							   <tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.enabled" /></td>
									<td class="tblcol" width="30%" height="20%" ><bean:write name="updateRadiusDBAcctDriverForm" property="enabled"/>&nbsp;</td>															
							   </tr>   
</table>