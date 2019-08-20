<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
		  				<tr>
		  					<td colspan="4">
		  						<html:form action="/updateEAPGsmConfig">
		  						<html:hidden property="action" value="update"/>
		  						<html:hidden property="eapId" />
		  						<html:hidden property="simConfigId"/>
		  						<html:hidden property="akaConfigId"/>
		  						<html:hidden property="akaPrimeConfigId"/>
		  						<html:hidden property="auditUId"/>
		  						<html:hidden property="name"/>
		  						<table width="100%">
		  							<tr>
		  								<td>
		  									<table width="100%" >
		  									
		  									<tr>
												<td class="tblheader-bold" colspan="3">
													Update <bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.simconfig"/>
												</td>
		  									</tr>
		  									<logic:empty name="eapConfigBean" property="simConfigData">
		  									<tr>
												<td align="center"  class="tblfirstcol" valign="top" colspan="2">
													<bean:message bundle="servermgrResources" key="servermgr.eapconfig.simconfignotenable"/>									
												</td>
											</tr>
											</logic:empty>
							
											<logic:notEmpty name="eapConfigBean" property="simConfigData">
		  									<tr>
		  										<td class="box" width="49%">
		  											<table width="100%" cellspacing="1">
		  												<tr>
		  													<td class="tblheader-bold" colspan="2">
		  														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoconfig"/>
		  													</td>
		  												</tr>
		  												<tr>
		  													<td class="small-gap" colspan="2">&nbsp;</td>
		  												</tr>
		  												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.pseudomethod"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.sim.pseudonymmethod" 
																		header="servermgr.eapconfig.gsm.pseudomethod"/>
													</td>
													<td>
														<html:select property="simPseudonymGenMethod" tabindex="1" styleId="simPseudonymGenMethod">
															<html:option value="0">--None--</html:option>
															<html:optionsCollection property="defaultGenMethod" label="value" value="key"/>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.pseudoencoding"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.sim.pseudonymencoding" 
																		header="servermgr.eapconfig.gsm.pseudoencoding"/>
													</td>
													<td>
														<html:select property="simPseudonymHexenCoding" tabindex="2">
															<html:option value="ENABLE">Enable</html:option>
															<html:option value="DISABLE">Disable</html:option>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.pseudoprefix"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.sim.pseudonymprefix" 
																		header="servermgr.eapconfig.gsm.pseudoprefix"/>
													</td>
													<td>
														<html:text property="simPseudonymPrefix" maxlength="10" tabindex="3"></html:text>
													</td>
												</tr>
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.pseudorootNAI"/>
															<ec:elitehelp headerBundle="servermgrResources" 
																text="eapconfig.gsm.sim.pseudonymrootNAI" 
																	header="servermgr.eapconfig.gsm.pseudorootNAI"/>
													</td>
													<td>
														<html:select property="simPseudonymRootNAI" tabindex="2">
															<html:option value="ENABLE">Enable</html:option>
															<html:option value="DISABLE">Disable</html:option>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.pseudoAAAidentity"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.sim.pseudonymAAAidentityNAI" 
																		header="servermgr.eapconfig.gsm.pseudoAAAidentity"/>
													</td>
													<td>
														<html:text property="simPseudonymAAAIndentityInRootNAI"  tabindex="3"></html:text>
													</td>
												</tr>
												
		  											</table>
		  										</td>
		  										<!-- <td width="2%"></td> -->
		  										<td class="box" width="49%">
		  											<table width="100%" cellspacing="1">
		  												<tr>
		  													<td class="tblheader-bold" colspan="2">
		  														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthconfig"/>
		  													</td>
		  												</tr>
		  												<tr>
		  													<td class="small-gap" colspan="2">&nbsp;</td>
		  												</tr>
		  												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthmethod"/>
															<ec:elitehelp headerBundle="servermgrResources" 
																text="eapconfig.gsm.sim.fastreauthmethod" 
																	header="servermgr.eapconfig.gsm.fastreauthmethod"/>
													</td>
													<td>
														<html:select property="simFastReauthGenMethod" tabindex="4">
															<html:option value="0">--None--</html:option>
															<html:optionsCollection property="defaultGenMethod" label="value" value="key"/>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.fastreauthencoding"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.sim.fastreauthencoding" 
																		header="servermgr.eapconfig.gsm.fastreauthencoding"/>
													</td>
													<td>
														<html:select property="simFastReauthHexenCoding" tabindex="5">
															<html:option value="ENABLE">Enable</html:option>
															<html:option value="DISABLE">Disable</html:option>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.fastreauthprefix"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.sim.fastreauthprefix" 
																		header="servermgr.eapconfig.gsm.fastreauthprefix"/>
													</td>
													<td>
														<html:text property="simFastReauthPrefix" maxlength="10" tabindex="6"></html:text>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthrootNAI"/>
															<ec:elitehelp headerBundle="servermgrResources" 
																text="eapconfig.gsm.sim.fastreauthrootNAI" 
																	header="servermgr.eapconfig.gsm.fastreauthrootNAI"/>
													</td>
													<td>
														<html:select property="simFastReauthRootNAI" tabindex="2">
															<html:option value="ENABLE">Enable</html:option>
															<html:option value="DISABLE" >Disable</html:option>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.fastreauthAAAidentity"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.sim.fastreauthAAAidentityNAI" 
																		header="servermgr.eapconfig.gsm.fastreauthAAAidentity"/>
													</td>
													<td>
														<html:text property="simFastReauthAAAIndentityInRootNAI"  tabindex="3"></html:text>
													</td>
												
													</tr>
		  											</table>
		  										</td>
		  									</tr>
		  									</logic:notEmpty>
		  									</table>
		  								</td>
		  							</tr>
		  							<tr>
		  								<td class="small-gap">&nbsp;</td>
		  							</tr>
		  							<tr>
		  								<td class="small-gap">&nbsp;</td>
		  							</tr>
		  							<tr>
		  								<td>
		  									<table width="100%">
		  									<tr>
												<td class="tblheader-bold" colspan="3">
													Update <bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.akaconfig"/>
												</td>
		  									<tr>
		  									<logic:empty name="eapConfigBean" property="akaConfigData">
		  									<tr>
												<td align="center"  class="tblfirstcol" valign="top" colspan="2">
													<bean:message bundle="servermgrResources" key="servermgr.eapconfig.akaconfignotenable"/>									
												</td>
												</tr>
											</logic:empty>
							
											<logic:notEmpty name="eapConfigBean" property="akaConfigData">
		  									<tr>
		  										<td class="box" width="49%">
		  											<table width="100%" cellspacing="1">
		  												<tr>
		  													<td class="tblheader-bold" colspan="2">
		  														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoconfig"/>
		  													</td>
		  												</tr>
		  												<tr>
		  													<td class="small-gap" colspan="2">&nbsp;</td>
		  												</tr>
		  												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.pseudomethod"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.aka.pseudonymmethod" 
																		header="servermgr.eapconfig.gsm.pseudomethod"/> 
													</td>
													<td>
														<html:select property="akaPseudonymGenMethod" tabindex="7">
															<html:option value="0">--None--</html:option>
															<html:optionsCollection property="defaultGenMethod" label="value" value="key"/>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.pseudoencoding"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.aka.pseudonymencoding" 
																		header="servermgr.eapconfig.gsm.pseudoencoding"/>
													</td>
													<td>
														<html:select property="akaPseudonymHexenCoding" tabindex="8">
															<html:option value="ENABLE">Enable</html:option>
															<html:option value="DISABLE">Disable</html:option>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoprefix"/>
															<ec:elitehelp headerBundle="servermgrResources" 
																text="eapconfig.gsm.aka.pseudonymprefix" 
																	header="servermgr.eapconfig.gsm.pseudoprefix"/>
													</td>
													<td>
														<html:text property="akaPseudonymPrefix" maxlength="10" tabindex="9"></html:text>
													</td>
												</tr>
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.pseudorootNAI"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																text="eapconfig.gsm.aka.pseudonymrootNAI" 
																	header="servermgr.eapconfig.gsm.pseudorootNAI"/> 
													</td>
													<td>
														<html:select property="akaPseudonymRootNAI" tabindex="2">
															<html:option value="ENABLE">Enable</html:option>
															<html:option value="DISABLE">Disable</html:option>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.pseudoAAAidentity"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.aka.pseudonymAAAidentityNAI" 
																		header="servermgr.eapconfig.gsm.pseudoAAAidentity"/>
													</td>
													<td>
														<html:text property="akaPseudonymAAAIndentityInRootNAI"  tabindex="3"></html:text>
													</td>
												</tr>
												
												
		  											</table>
		  										</td>
		  										<!-- <td width="2%"></td> -->
		  										<td class="box" width="49%">
		  											<table width="100%" cellspacing="1">
		  												<tr>
		  													<td class="tblheader-bold" colspan="2">
		  														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthconfig"/>
		  													</td>
		  												</tr>
		  												<tr>
		  													<td class="small-gap" colspan="2">&nbsp;</td>
		  												</tr>
		  												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.fastreauthmethod"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.aka.fastreauthmethod" 
																		header="servermgr.eapconfig.gsm.fastreauthmethod"/> 													</td>
													<td>
														<html:select property="akaFastReauthGenMethod" tabindex="10">
															<html:option value="0">--None--</html:option>
															<html:optionsCollection property="defaultGenMethod" label="value" value="key"/>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthencoding"/>
															<ec:elitehelp headerBundle="servermgrResources" 
																text="eapconfig.gsm.aka.fastreauthencoding" 
																	header="servermgr.eapconfig.gsm.fastreauthencoding"/>
													</td>
													<td>
														<html:select property="akaFastReauthHexenCoding" tabindex="11">
															<html:option value="ENABLE">Enable</html:option>
															<html:option value="DISABLE">Disable</html:option>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.fastreauthprefix"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.aka.fastreauthprefix" 
																		header="servermgr.eapconfig.gsm.fastreauthprefix"/>
													</td>
													<td>
														<html:text property="akaFastReauthPrefix" maxlength="10" tabindex="12"></html:text>
													</td>
												</tr>
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.fastreauthrootNAI"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.aka.fastreauthrootNAI" 
																		header="servermgr.eapconfig.gsm.fastreauthrootNAI"/> 
													</td>
													<td>
														<html:select property="akaFastReauthRootNAI" tabindex="2">
															<html:option value="ENABLE">Enable</html:option>
															<html:option value="DISABLE">Disable</html:option>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.fastreauthAAAidentity"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.aka.fastreauthAAAidentityNAI" 
																		header="servermgr.eapconfig.gsm.fastreauthAAAidentity"/>
													</td>
													<td>
														<html:text property="akaFastReauthAAAIndentityInRootNAI"  tabindex="3"></html:text>
													</td>
												</tr>
												
		  											</table>
		  										</td>
		  									</tr>
		  									</logic:notEmpty>
		  									</table>
		  								</td>
		  							</tr>
		  							<tr>
		  								<td class="small-gap">&nbsp;</td>
		  							</tr>
		  							<tr>
		  								<td class="small-gap">&nbsp;</td>
		  							</tr>
		  							<tr>
		  								<td>
		  									<table width="100%">
		  									<tr>
												<td class="tblheader-bold" colspan="3">
													Update <bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.akaprimeconfig"/>
												</td>
		  									<tr>
		  									<logic:empty name="eapConfigBean" property="akaPrimeConfigData">
		  									<tr>
												<td align="center"  class="tblfirstcol" valign="top" colspan="2">
													<bean:message bundle="servermgrResources" key="servermgr.eapconfig.akaprimeconfignotenable"/>									
												</td>
												</tr>
											</logic:empty>
							
											<logic:notEmpty name="eapConfigBean" property="akaPrimeConfigData">
		  									<tr>
		  										<td class="box" width="49%">
		  											<table width="100%" cellspacing="1">
		  												<tr>
		  													<td class="tblheader-bold" colspan="2">
		  														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoconfig"/>
		  													</td>
		  												</tr>
		  												<tr>
		  													<td class="small-gap" colspan="2">&nbsp;</td>
		  												</tr>
		  												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.pseudomethod"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.akaprime.pseudonymmethod" 
																		header="servermgr.eapconfig.gsm.pseudomethod"/>
													</td>
													<td>
														<html:select property="akaPrimePseudonymGenMethod" tabindex="7">
															<html:option value="0">--None--</html:option>
															<html:optionsCollection property="defaultGenMethod" label="value" value="key"/>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.pseudoencoding"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.akaprime.pseudonymencoding" 
																		header="servermgr.eapconfig.gsm.pseudoencoding"/>
													</td>
													<td>
														<html:select property="akaPrimePseudonymHexenCoding" tabindex="8">
															<html:option value="ENABLE">Enable</html:option>
															<html:option value="DISABLE">Disable</html:option>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.pseudoprefix"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.akaprime.pseudonymprefix" 
																		header="servermgr.eapconfig.gsm.pseudoprefix"/>
													</td>
													<td>
														<html:text property="akaPrimePseudonymPrefix" maxlength="10" tabindex="9"></html:text>
													</td>
												</tr>
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.pseudorootNAI"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.akaprime.pseudonymrootNAI" 
																		header="servermgr.eapconfig.gsm.pseudorootNAI"/>
													</td>
													<td>
														<html:select property="akaPrimePseudonymRootNAI" tabindex="2">
															<html:option value="ENABLE">Enable</html:option>
															<html:option value="DISABLE">Disable</html:option>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.pseudoAAAidentity"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.akaprime.pseudonymAAAidentityNAI" 
																		header="servermgr.eapconfig.gsm.pseudoAAAidentity"/>
													</td>
													<td>
														<html:text property="akaPrimePseudonymAAAIndentityInRootNAI"  tabindex="3"></html:text>
													</td>
												</tr>
		  											</table>
		  										</td>
		  										<!-- <td width="2%"></td> -->
		  										<td class="box" width="49%">
		  											<table width="100%" cellspacing="1">
		  												<tr>
		  													<td class="tblheader-bold" colspan="2">
		  														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthconfig"/>
		  													</td>
		  												</tr>
		  												<tr>
		  													<td class="small-gap" colspan="2">&nbsp;</td>
		  												</tr>
		  												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.fastreauthmethod"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.akaprime.fastreauthmethod" 
																		header="servermgr.eapconfig.gsm.fastreauthmethod"/>
													</td>
													<td>
														<html:select property="akaPrimeFastReauthGenMethod" tabindex="10">
															<html:option value="0">--None--</html:option>
															<html:optionsCollection property="defaultGenMethod" label="value" value="key"/>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.fastreauthencoding"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.akaprime.fastreauthencoding" 
																		header="servermgr.eapconfig.gsm.fastreauthencoding"/>
													</td>
													<td>
														<html:select property="akaPrimeFastReauthHexenCoding" tabindex="11">
															<html:option value="ENABLE">Enable</html:option>
															<html:option value="DISABLE">Disable</html:option>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.fastreauthprefix"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.akaprime.fastreauthprefix" 
																		header="servermgr.eapconfig.gsm.fastreauthprefix"/>
													</td>
													<td>
														<html:text property="akaPrimeFastReauthPrefix" maxlength="10" tabindex="12"></html:text>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.fastreauthrootNAI"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.akaprime.fastreauthrootNAI" 
																		header="servermgr.eapconfig.gsm.fastreauthrootNAI"/>
													</td>
													<td>
														<html:select property="akaPrimeFastReauthRootNAI" tabindex="2">
															<html:option value="ENABLE">Enable</html:option>
															<html:option value="DISABLE">Disable</html:option>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" 
															key="servermgr.eapconfig.gsm.fastreauthAAAidentity"/>
																<ec:elitehelp headerBundle="servermgrResources" 
																	text="eapconfig.gsm.akaprime.fastreauthAAAidentityNAI" 
																		header="servermgr.eapconfig.gsm.fastreauthAAAidentity"/>
													</td>
													<td>
														<html:text property="akaPrimeFastReauthAAAIndentityInRootNAI"  tabindex="3"></html:text>
													</td>
												</tr>
		  											</table>
		  										</td>
		  									</tr>
		  									</logic:notEmpty>
		  									</table>
		  								</td>
		  							</tr>
		  							
		  							<tr>
		  								<td class="small-gap">&nbsp;</td>
		  							</tr>
		  							
		  							<tr>
		  								<td align="center">
		  									<input type="submit" name="c_btnCreate"    value=" Update "  class="light-btn" />
		  									<input type="button" name="c_btnCancel"  onclick="javascript:location.href='<%=basePath%>/initSearchEAPConfig.do?/>'"  value="   Cancel   " class="light-btn"  />
		  								</td>
		  								
		  							</tr>
		  						</table>
		  						</html:form>
		  					</td>
		  				</tr>
		  			</table>	