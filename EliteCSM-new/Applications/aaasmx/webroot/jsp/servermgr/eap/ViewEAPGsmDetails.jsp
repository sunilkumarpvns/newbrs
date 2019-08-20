<%@page import="com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPSimAkaConfigData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<script type="text/javascript">
setTitle('<bean:message bundle="servermgrResources" key="servermgr.eapconfig.header"/>');	
</script>

	<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
		<bean:define id="eapConfigBean" name="eapConfigData" scope="request" type="EAPConfigData" />
		
				<tr>
		  			<td class="small-gap">&nbsp;</td>
		  		</tr> 
		  		<tr>
		  			<td class="small-gap">&nbsp;</td>
		  		</tr>
				
		  		<tr>
		  			<td>
		  				<table width="100%" >
		  					<tr>
								<td class="tblheader-bold" colspan="3">
									View <bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.simconfig"/>
								</td>
		  					</tr>
		  					
		  					<logic:empty name="eapConfigBean" property="simConfigData">
		  					<tr>
								<td align="center"  class="tblfirstcol" valign="top" colspan="2">
									<bean:message bundle="servermgrResources" key="servermgr.eapconfig.simconfigdoesnotexist"/>									
								</td>
							</tr>
							</logic:empty>
							
							<logic:notEmpty name="eapConfigBean" property="simConfigData">
							<bean:define id="simConfigBean" name="eapConfigBean" property="simConfigData" type="EAPSimAkaConfigData" />
		  					<tr>
		  						<td class="box" width="49%">
		  							<table width="100%" cellspacing="1">
		  								<tr>
		  									<td class="tblheader-bold" colspan="2">
		  										<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoconfig"/>
		  									</td>
		  								</tr>
		  								
		  								<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudomethod"/>
											</td>
											<td class="tblcol">
												<logic:empty name="simConfigBean" property="pseudonymGenMethod" > None</logic:empty>
												<logic:notEmpty name="simConfigBean" property="pseudonymGenMethod" > 
												<logic:iterate id="genMethodMap" name="eapGSMConfigForm" property="defaultGenMethod">
													<logic:equal  name="genMethodMap"  property="key" value="<%=simConfigBean.getPseudonymGenMethod() %>">
														<bean:write name="genMethodMap" property="value"/>
													</logic:equal>
												</logic:iterate>
												</logic:notEmpty>
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoencoding"/>
											</td>
											<td class="tblcol">
												<bean:write name="simConfigBean" property="pseudonymHexenCoding"/>&nbsp;
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoprefix"/>
											</td>
											<td class="tblcol">
												<bean:write name="simConfigBean" property="pseudonymPrefix"/>&nbsp;
											</td>
										</tr>
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudorootNAI"/>
											</td>
											<td class="tblcol">
												<bean:write name="simConfigBean" property="pseudonymRootNAI"/>&nbsp;
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoAAAidentity"/>
											</td>
											<td class="tblcol">
											<logic:empty name="simConfigBean" property="pseudonymAAAIdentityInRootNAI"> - </logic:empty>
												<bean:write name="simConfigBean" property="pseudonymAAAIdentityInRootNAI"/>&nbsp;
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
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthmethod"/>
											</td>
											<td class="tblcol">
												<logic:empty name="simConfigBean" property="fastReAuthGenMethod" > None</logic:empty>
												<logic:notEmpty name="simConfigBean" property="fastReAuthGenMethod">
													<logic:iterate id="genMethodMap" name="eapGSMConfigForm" property="defaultGenMethod">
														<logic:equal  name="genMethodMap"  property="key" value="<%=simConfigBean.getFastReAuthGenMethod() %>">
															<bean:write name="genMethodMap" property="value"/>
														</logic:equal>
													</logic:iterate>
												</logic:notEmpty>
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthencoding"/>
											</td>
											<td class="tblcol">
												<bean:write name="simConfigBean" property="fastReAuthHexenCoding"/>&nbsp;
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthprefix"/>
											</td>
											<td class="tblcol">
												<bean:write name="simConfigBean" property="fastReAuthPrefix"/>&nbsp;
											</td>
										</tr>
										
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudorootNAI"/>
											</td>
											<td class="tblcol">
												<bean:write name="simConfigBean" property="fastReAuthRootNAI"/>&nbsp;
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoAAAidentity"/>
											</td>
											<td class="tblcol">
											<logic:empty name="simConfigBean" property="fastReAuthAAAIdentityInRootNAI"> - </logic:empty>
												<bean:write name="simConfigBean" property="fastReAuthAAAIdentityInRootNAI"/>&nbsp;
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
		  			<td class="small-gap">&nbsp;</td>
		  		</tr>
		  		<tr>
		  			<td>
		  				<table width="100%">
		  					<tr>
								<td class="tblheader-bold" colspan="3">
									View <bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.akaconfig"/>
								</td>
		  					</tr>
		  					
		  					<logic:empty name="eapConfigBean" property="akaConfigData">
		  					<tr>
								<td align="center"  class="tblfirstcol" valign="top" colspan="2">
									<bean:message bundle="servermgrResources" key="servermgr.eapconfig.akaconfigdoesnotexist"/>									
								</td>
							</tr>
							</logic:empty>
							
							<logic:notEmpty name="eapConfigBean" property="akaConfigData">
							<bean:define id="akaConfigBean" name="eapConfigBean" property="akaConfigData" type="EAPSimAkaConfigData" />
		  					<tr>
		  						<td class="box" width="49%">
		  							<table width="100%" cellspacing="1">
		  								<tr>
		  									<td class="tblheader-bold" colspan="2">
		  										<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoconfig"/>
		  									</td>
		  								</tr>
		  								
		  								<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudomethod"/>
											</td>
											<td class="tblcol">
												<logic:empty name="akaConfigBean" property="pseudonymGenMethod" > None</logic:empty>
												<logic:notEmpty name="akaConfigBean" property="pseudonymGenMethod">
													<logic:iterate id="genMethodMap" name="eapGSMConfigForm" property="defaultGenMethod">
														<logic:equal  name="genMethodMap"  property="key" value="<%=akaConfigBean.getPseudonymGenMethod() %>">
															<bean:write name="genMethodMap" property="value"/>
														</logic:equal>
													</logic:iterate>
												</logic:notEmpty>
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoencoding"/>
											</td>
											<td class="tblcol">
												<bean:write name="akaConfigBean" property="pseudonymHexenCoding"/>&nbsp;
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoprefix"/>
											</td>
											<td class="tblcol">
												<bean:write name="akaConfigBean" property="pseudonymPrefix"/>&nbsp;
											</td>
										</tr>
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudorootNAI"/>
											</td>
											<td class="tblcol">
												<bean:write name="akaConfigBean" property="pseudonymRootNAI"/>&nbsp;
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoAAAidentity"/>
											</td>
											<td class="tblcol">
											<logic:empty name="akaConfigBean" property="pseudonymAAAIdentityInRootNAI"> - </logic:empty>
												<bean:write name="akaConfigBean" property="pseudonymAAAIdentityInRootNAI"/>&nbsp;
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
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthmethod"/>
											</td>
											<td class="tblcol">
												<logic:empty name="akaConfigBean" property="fastReAuthGenMethod" > None</logic:empty>
												<logic:notEmpty name="akaConfigBean" property="fastReAuthGenMethod">
													<logic:iterate id="genMethodMap" name="eapGSMConfigForm" property="defaultGenMethod">
														<logic:equal name="genMethodMap" property="key" value="<%=akaConfigBean.getFastReAuthGenMethod() %>">	
															<bean:write name="genMethodMap" property="value"/>
														</logic:equal>												
													</logic:iterate>
												</logic:notEmpty>
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthencoding"/>
											</td>
											<td class="tblcol">
												<bean:write name="akaConfigBean" property="fastReAuthHexenCoding"/>&nbsp;
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthprefix"/>
											</td>
											<td class="tblcol">
												<bean:write name="akaConfigBean" property="fastReAuthPrefix"/>&nbsp;
											</td>
										</tr>
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudorootNAI"/>
											</td>
											<td class="tblcol">
												<bean:write name="akaConfigBean" property="fastReAuthRootNAI"/>&nbsp;
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoAAAidentity"/>
											</td>
											<td class="tblcol">
											<logic:empty name="akaConfigBean" property="fastReAuthAAAIdentityInRootNAI"> - </logic:empty>
												<bean:write name="akaConfigBean" property="fastReAuthAAAIdentityInRootNAI"/>&nbsp;
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
		  			<td class="small-gap">&nbsp;</td>
		  		</tr>
		  		<tr>
		  			<td>
		  				<table width="100%">
		  					<tr>
								<td class="tblheader-bold" colspan="3">
									View <bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.akaprimeconfig"/>
								</td>
		  					</tr>
		  					
		  					<logic:empty name="eapConfigBean" property="akaPrimeConfigData">
		  					<tr>
								<td align="center"  class="tblfirstcol" valign="top" colspan="2">
									<bean:message bundle="servermgrResources" key="servermgr.eapconfig.akaprimeconfigdoesnotexist"/>									
								</td>
							</tr>
							</logic:empty>
							
							<logic:notEmpty name="eapConfigBean" property="akaPrimeConfigData">
							<bean:define id="akaPrimeConfigBean" name="eapConfigBean" property="akaPrimeConfigData" type="EAPSimAkaConfigData" />
		  					<tr>
		  						<td class="box" width="49%">
		  							<table width="100%" cellspacing="1">
		  								<tr>
		  									<td class="tblheader-bold" colspan="2">
		  										<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoconfig"/>
		  									</td>
		  								</tr>
		  								
		  								<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudomethod"/>
											</td>
											<td class="tblcol">
												<logic:empty name="akaPrimeConfigBean" property="pseudonymGenMethod" > None</logic:empty>
												<logic:notEmpty name="akaPrimeConfigBean" property="pseudonymGenMethod">
													<logic:iterate id="genMethodMap" name="eapGSMConfigForm" property="defaultGenMethod">
														<logic:equal  name="genMethodMap"  property="key" value="<%=akaPrimeConfigBean.getPseudonymGenMethod() %>">
															<bean:write name="genMethodMap" property="value"/>
														</logic:equal>
													</logic:iterate>
												</logic:notEmpty>
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoencoding"/>
											</td>
											<td class="tblcol">
												<bean:write name="akaPrimeConfigBean" property="pseudonymHexenCoding"/>&nbsp;
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoprefix"/>
											</td>
											<td class="tblcol">
												<bean:write name="akaPrimeConfigBean" property="pseudonymPrefix"/>&nbsp;
											</td>
										</tr>
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudorootNAI"/>
											</td>
											<td class="tblcol">
												<bean:write name="akaPrimeConfigBean" property="pseudonymRootNAI"/>&nbsp;
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoAAAidentity"/>
											</td>
											<td class="tblcol">
											<logic:empty name="akaPrimeConfigBean" property="pseudonymAAAIdentityInRootNAI"> - </logic:empty>
												<bean:write name="akaPrimeConfigBean" property="pseudonymAAAIdentityInRootNAI"/>&nbsp;
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
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthmethod"/>
											</td>
											<td class="tblcol">
												<logic:empty name="akaPrimeConfigBean" property="fastReAuthGenMethod" > None</logic:empty>
												<logic:notEmpty name="akaPrimeConfigBean" property="fastReAuthGenMethod">
													<logic:iterate id="genMethodMap" name="eapGSMConfigForm" property="defaultGenMethod">
														<logic:equal name="genMethodMap" property="key" value="<%=akaPrimeConfigBean.getFastReAuthGenMethod() %>">	
															<bean:write name="genMethodMap" property="value"/>
														</logic:equal>												
													</logic:iterate>
												</logic:notEmpty>
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthencoding"/>
											</td>
											<td class="tblcol">
												<bean:write name="akaPrimeConfigBean" property="fastReAuthHexenCoding"/>&nbsp;
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthprefix"/>
											</td>
											<td class="tblcol">
												<bean:write name="akaPrimeConfigBean" property="fastReAuthPrefix"/>&nbsp;
											</td>
										</tr>
										
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudorootNAI"/>
											</td>
											<td class="tblcol">
												<bean:write name="akaPrimeConfigBean" property="fastReAuthRootNAI"/>&nbsp;
											</td>
										</tr>
												
										<tr>
											<td class="tblfirstcol" width="40%">
												<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoAAAidentity"/>
											</td>
											<td class="tblcol">
											<logic:empty name="akaPrimeConfigBean" property="fastReAuthAAAIdentityInRootNAI"> - </logic:empty>
												<bean:write name="akaPrimeConfigBean" property="fastReAuthAAAIdentityInRootNAI"/>&nbsp;
											
											</td>
										</tr>
		  							</table>
		  						</td>
		  					</tr>
		  					</logic:notEmpty>
		  				</table>
		  			</td>
		  		</tr>
	</table>	
		  								
		

