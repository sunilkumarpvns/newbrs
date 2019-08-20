 <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" style="table-layout: fixed; ">
          						<tr> 
            						<td class="tblheader-bold" height="20%" colspan="4"><bean:message bundle="driverResources" key="driver.view"/></td>
          						</tr>  
          						 <tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="4">
									<bean:message bundle="driverResources" key="driver.driverinstancedetails" /></td>
								 </tr>  					
          						 <tr>
									<td class="tblfirstcol" width="20%" height="20%">
									<bean:message bundle="driverResources" key="driver.instname" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3" ><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="driverInstanceName"/></td>
								 </tr>   
					    
					   			 <tr>
									<td class="tblfirstcol" width="20%" height="20%">
									<bean:message bundle="driverResources" key="driver.instdesc" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3" ><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="driverInstanceDesp"/>&nbsp;</td>
														
								</tr>
						
								<tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="4">
									<bean:message bundle="driverResources" key="driver.driverdetails" /></td>
								</tr>   					       									
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.header" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3" ><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="header"/>&nbsp;</td>												
								</tr>						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.delimeter" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3" ><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="delimeter"/>&nbsp;</td>
								
							   </tr>
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.multivaluedelimeter" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3" ><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="multivaluedelimeter"/>&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="classiccsvdriver.cdrtimestampheader" />
									</td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="cdrTimestampHeader"/>&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.putcdrtimestamp" />
									</td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="cdrtimestampFormat"/>&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="classiccsvdriver.cdrtimestampposition" />
									</td>
									<td class="tblcol" width="30%" height="20%" colspan="3" style="text-transform: capitalize;">
										<logic:equal name="updateRadiusClassicCSVAcctDriverForm" property="cdrTimestampPosition" value="SUFFIX">
											Suffix
										</logic:equal>
										<logic:equal name="updateRadiusClassicCSVAcctDriverForm" property="cdrTimestampPosition" value="PREFIX">
											Prefix
										</logic:equal>
										&nbsp;
									</td>
								</tr>
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.enclosingcharacter" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="enclosingCharacter"/>&nbsp;</td>
								</tr>			
						        <tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="4">
									<bean:message bundle="driverResources" key="driver.filedetail"/></td>
								</tr>
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.filename" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="filename"/>&nbsp;</td>												
								</tr>						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.location" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="location"/>&nbsp;</td>
								
							   </tr>						
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
								<bean:message bundle="driverResources" key="driver.createblankfile" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="createBlankFile"/>&nbsp;</td>
							   </tr>
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.prefixFileName" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3" ><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="prefixfilename"/>&nbsp;</td>												
								</tr>
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.defaultdirname" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3" ><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="defaultdirname"/>&nbsp;</td>															
							   </tr> 
							   							
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.foldername" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="foldername"/>&nbsp;</td>								
							   </tr>	                				                				                																													
								 <tr>
										<td align="left" class="tblheader-bold" valign="top" colspan="4">
										<bean:message bundle="driverResources" key="driver.filerollingdetail"/>&nbsp;</td>
								</tr>								
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.timeboundry" /></td>
									<%
									UpdateRadiusClassicCSVAcctDriverForm form = (UpdateRadiusClassicCSVAcctDriverForm)request.getAttribute("updateRadiusClassicCSVAcctDriverForm");
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
									<logic:notEqual name="updateRadiusClassicCSVAcctDriverForm" property="sizeBasedRollingUnit" value="0">
										<bean:write name="updateRadiusClassicCSVAcctDriverForm" property="sizeBasedRollingUnit"/>
									</logic:notEqual>
									&nbsp;</td>
								</tr> 
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.timebasedrollingunit" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3">
									<logic:notEqual name="updateRadiusClassicCSVAcctDriverForm" property="timeBasedRollingUnit" value="0">
										<bean:write name="updateRadiusClassicCSVAcctDriverForm" property="timeBasedRollingUnit"/>
									</logic:notEqual>
									&nbsp;
									</td>
								</tr>
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
										<bean:message bundle="driverResources" key="driver.recordbasedrollingunit" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3">
									<logic:notEqual name="updateRadiusClassicCSVAcctDriverForm" property="recordBasedRollingUnit" value="0">
										<bean:write name="updateRadiusClassicCSVAcctDriverForm" property="recordBasedRollingUnit"/>
									</logic:notEqual>
									&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.range" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="range"/>&nbsp;</td>												
								</tr>																			
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.pos" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="pattern"/>&nbsp;</td>
							   </tr>
																
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.global" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="globalization"/>&nbsp;</td>															
							   </tr> 
							   <tr>
									<td align="left" class="tblheader-bold" valign="top" colspan="4">
									<bean:message bundle="driverResources" key="driver.filetransferdetails" /></td>
								</tr>
							   
                				<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.detaillocal.allocatingprotocol" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="allocatingprotocol"/>&nbsp;</td>
							   </tr>
																
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.ipaddress" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="ipaddress"/>&nbsp;</td>															
							   </tr>                 				                													
							   <tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.remoteLocation" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="remotelocation"/>&nbsp;</td>
							   </tr>																
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.username" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="username"/>&nbsp;</td>															
							   </tr>    
							                  				                													
							   <tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.postoperation" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="postoperation"/>&nbsp;</td>															
							   </tr>                 				                													
								<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.archiveloc" /></td>
									<td class="tblcol" style="word-wrap:break-word" colspan="3"><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="archivelocation"/>&nbsp;</td>															
							   </tr> 
							   	<tr>
									<td align="left" class="tblfirstcol" valign="top">
									<bean:message bundle="driverResources" key="driver.failovertime" /></td>
									<td class="tblcol" width="30%" height="20%" colspan="3" ><bean:write name="updateRadiusClassicCSVAcctDriverForm" property="failovertime"/>&nbsp;</td>															
							   </tr> 
</table>