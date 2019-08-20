 <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" style="table-layout: fixed; ">
          						<tr> 
            						<td class="tblheader-bold" height="20%" colspan="4"><bean:message bundle="driverResources" key="driver.view"/></td>
          						</tr>  
          						 <tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="4">
									<bean:message bundle="driverResources" key="driver.driverinstancedetails" /></td>
								 </tr>  					
          						 <tr>
									<td class="tblfirstcol" width="20%" height="20%" >
									<bean:message bundle="driverResources" key="driver.instname" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="driverInstanceName"/></td>
								 </tr>   
					    
					   			 <tr>
									<td class="tblfirstcol" width="20%" height="20%">
									<bean:message bundle="driverResources" key="driver.instdesc" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="driverInstanceDesp"/>&nbsp;</td>
														
								</tr>
								<tr>
								<td align="left" class="tblheader-bold" valign="top" colspan="4">
									<bean:message bundle="driverResources" key="driver.detaillocal.details" /></td>
								</tr>
										
								
																
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.eventDateFormat" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="eventDateFormat"/>&nbsp;</td>															
							   </tr>                 				                							
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.writeAttributes" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="writeAttributes"/>&nbsp;</td>
								
							   </tr>						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.usedicval" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="useDictionaryValue"/>&nbsp;</td>
							   </tr>
																
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.avpairSeperator" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="avpairSeperator"/>&nbsp;</td>															
							   </tr> 
                				
								 <tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="4">
									<bean:message bundle="driverResources" key="driver.filedetail"/></td>
								</tr>
								
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.filename" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="fileName"/>&nbsp;</td>															
							   </tr>                 				                				
                				<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.location" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="location"/>&nbsp;</td>
								</tr>								 
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.defaultdirname" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="defaultDirName"/>&nbsp;</td>												
								</tr>	
									
                				<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.prefixFileName" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="prefixFileName"/>&nbsp;</td>
								</tr>
						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.foldername" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="folderName"/>&nbsp;</td>												
								</tr>																		
								
								<tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="4">
									<bean:message bundle="driverResources" key="driver.filerollingdetail"/>&nbsp;</td>
								</tr>
							    <tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.timeboundry" /></td>
									<%
									UpdateDiameterDetailLocalAcctDriverForm form = (UpdateDiameterDetailLocalAcctDriverForm)request.getAttribute("updateDiameterDetailLocalAcctDriverForm");
									if(form.getTimeBoundry() != null && form.getTimeBoundry() == 0){%>
									<td class="tblcol" width="30%" height="20%" colspan="3">NONE</td>	
									<%}else if(form.getTimeBoundry() != null && form.getTimeBoundry() == 1){%>
									<td class="tblcol" width="30%" height="20%" colspan="3">1 Min</td>	
									<%}else if(form.getTimeBoundry() != null && form.getTimeBoundry() == 2){ %>
									<td class="tblcol" width="30%" height="20%" colspan="3">2 Min</td>
									<%}else if(form.getTimeBoundry() != null && form.getTimeBoundry() == 3){ %>
									<td class="tblcol" width="30%" height="20%" colspan="3">3 Min</td>
									<%}else if(form.getTimeBoundry() != null && form.getTimeBoundry() == 5){ %>
									<td class="tblcol" width="30%" height="20%" colspan="3">5 Min</td>
									<%}else if(form.getTimeBoundry() != null && form.getTimeBoundry() == 10){ %>
									<td class="tblcol" width="30%" height="20%" colspan="3">10 Min</td>
									<%}else if(form.getTimeBoundry() != null && form.getTimeBoundry() == 15){ %>
									<td class="tblcol" width="30%" height="20%" colspan="3">15 Min</td>
									<%}else if(form.getTimeBoundry() != null && form.getTimeBoundry() == 20){ %>
									<td class="tblcol" width="30%" height="20%" colspan="3">20 Min</td>
									<%}else if(form.getTimeBoundry() != null && form.getTimeBoundry() == 30){ %>
									<td class="tblcol" width="30%" height="20%" colspan="3">30 Min</td>
									<%}else if(form.getTimeBoundry() != null && form.getTimeBoundry() == 60){ %>
									<td class="tblcol" width="30%" height="20%" colspan="3">Hourly</td>
									<%}else { %>
									<td class="tblcol" width="30%" height="20%" colspan="3" >Daily</td>
									<%} %>
							   </tr>  
                				<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.sizebasedrollingunit" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3">
									<logic:notEqual name="updateDiameterDetailLocalAcctDriverForm" property="sizeBasedRollingUnit" value="0">
										<bean:write name="updateDiameterDetailLocalAcctDriverForm" property="sizeBasedRollingUnit"/>
									</logic:notEqual>
									&nbsp;</td>
								</tr> 
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.timebasedrollingunit" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3">
									<logic:notEqual name="updateDiameterDetailLocalAcctDriverForm" property="timeBasedRollingUnit" value="0">
										<bean:write name="updateDiameterDetailLocalAcctDriverForm" property="timeBasedRollingUnit"/>
									</logic:notEqual>
									&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.recordbasedrollingunit" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3">
									<logic:notEqual name="updateDiameterDetailLocalAcctDriverForm" property="recordBasedRollingUnit" value="0">
										<bean:write name="updateDiameterDetailLocalAcctDriverForm" property="recordBasedRollingUnit"/>
									</logic:notEqual>
									&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.range" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="range"/>&nbsp;</td>
							   </tr>
																
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.pattern" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="pattern"/>&nbsp;</td>															
							   </tr>                 				                													
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.global" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="globalization"/>&nbsp;</td>															
							   </tr> 
								<tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="4">
									<bean:message bundle="driverResources" key="driver.filetransferdetails" /></td>
								</tr>
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.detaillocal.allocatingprotocol" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="allocatingProtocol"/>&nbsp;</td>
								</tr>
						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.ipaddress" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="ipaddress"/>&nbsp;</td>												
								</tr>						
													
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.remoteLocation" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="remoteLocation"/>&nbsp;</td>
							   </tr>
						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.username" /></td>
										<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="userName"/>&nbsp;</td>															
							   </tr> 
                				
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.postoperation" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="postOperation"/>&nbsp;</td>												
								</tr>						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.archiveloc" /></td>
									<td class="tblcol" width="30%" height="20%" style="word-wrap:break-word" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="archiveLocation"/>&nbsp;</td>
								
							   </tr>						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.failovertime" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="failOverTime"/>&nbsp;</td>
							   </tr>
								
								
</table>
