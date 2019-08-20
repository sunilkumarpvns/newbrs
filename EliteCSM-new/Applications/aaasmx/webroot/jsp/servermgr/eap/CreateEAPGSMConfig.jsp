<%@page import="com.elitecore.elitesm.util.constants.EAPConfigConstant"%>
<%@page import=" com.elitecore.elitesm.web.driver.radius.forms.CrestelOCSv2DriverForm" %>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<% 	
	String basePath = request.getContextPath();
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
	setTitle('<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsmconfig"/>');
	
	$(document).ready(function(){
		$("#simPseudonymGenMethod").focus();
		
	});
</script>

<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
  		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">
  			&nbsp;
  		</td>
		<td>
   			<table cellpadding="0" cellspacing="0" border="0" width="100%">
  		  		<tr>
  		  			<td  cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
						
		  				<tr>
		  					<td colspan="4">
		  						<html:form action="/createEAPGSMConfig">
		  						<html:hidden property="action" value="create"/>
		  						<table width="100%">
		  							<tr>
		  								<td>
		  									<table width="100%" >
		  									<tr>
												<td class="tblheader-bold" colspan="3">
													<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.simconfig"/>
												</td>
		  									<tr>
		  									<tr>
		  										<td class="box" width="49%">
		  											<table width="100%" cellspacing="2">
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudomethod"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.sim.pseudonymmethod"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudomethod"/>')"/>
													</td>
													<td>
														<html:select property="simPseudonymGenMethod"  styleId="simPseudonymGenMethod" tabindex="1">
															<html:option value="0">--None--</html:option>
															<html:optionsCollection property="defaultGenMethod" label="value" value="key"/>
														</html:select>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoencoding"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.sim.pseudonymencoding"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoencoding"/>')"/>
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoprefix"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.sim.pseudonymprefix"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoprefix"/>')"/>
													</td>
													<td>
														<html:text property="simPseudonymPrefix" maxlength="10" tabindex="3"></html:text>
													</td>
												</tr>
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudorootNAI"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.sim.pseudonymrootNAI"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoencoding"/>')"/>
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoAAAidentity"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.sim.pseudonymAAAidentityNAI"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoprefix"/>')"/>
													</td>
													<td>
														<html:text property="simPseudonymAAAIndentityInRootNAI"  tabindex="3"></html:text>
													</td>
												</tr>
									
		  									</table>
		  									</td>
		  										<td width="2%">
		  										</td>
		  										<td class="box" width="49%">
		  											<table width="100%" cellspacing="2">
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
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.sim.fastreauthmethod"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthmethod"/>')"/>
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthencoding"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.sim.fastreauthencoding"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthencoding"/>')"/>
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthprefix"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.sim.fastreauthprefix"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthprefix"/>')"/>
													</td>
													<td>
														<html:text property="simFastReauthPrefix" maxlength="10" tabindex="6"></html:text>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthrootNAI"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.sim.fastreauthrootNAI"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthrootNAI"/>')"/>
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthAAAidentity"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.sim.fastreauthAAAidentityNAI"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthAAAidentity"/>')"/>
													</td>
													<td>
														<html:text property="simFastReauthAAAIndentityInRootNAI"  tabindex="3"></html:text>
													</td>
												</tr>
		  											</table>
		  										</td>
		  									</tr>
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
													<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.akaconfig"/>
												</td>
		  									<tr>
		  									<tr>
		  										<td class="box" width="49%">
		  											<table width="100%" cellspacing="2">
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudomethod"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.aka.pseudonymmethod"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudomethod"/>')"/>
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoencoding"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.aka.pseudonymencoding"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoencoding"/>')"/>
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
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.aka.pseudonymprefix"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoprefix"/>')"/>
													</td>
													<td>
														<html:text property="akaPseudonymPrefix" maxlength="10" tabindex="9"></html:text>
													</td>
												</tr>
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudorootNAI"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.aka.pseudonymrootNAI"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudorootNAI"/>')"/>
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoAAAidentity"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.aka.pseudonymAAAidentityNAI"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoAAAidentity"/>')"/>
													</td>
													<td>
														<html:text property="akaPseudonymAAAIndentityInRootNAI"  tabindex="3"></html:text>
													</td>
												</tr>
												
												
		  											</table>
		  										</td>
		  										<td width="2%"></td>
		  										<td class="box" width="49%">
		  											<table width="100%" cellspacing="2">
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
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.aka.fastreauthmethod"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthmethod"/>')"/>
													</td>
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
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.aka.fastreauthencoding"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthencoding"/>')"/>
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthprefix"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.aka.fastreauthprefix"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthprefix"/>')"/>
													</td>
													<td>
														<html:text property="akaFastReauthPrefix" maxlength="10" tabindex="12"></html:text>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthrootNAI"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.aka.fastreauthrootNAI"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthrootNAI"/>')"/>
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthAAAidentity"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.aka.fastreauthAAAidentityNAI"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthAAAidentity"/>')"/>
													</td>
													<td>
														<html:text property="akaFastReauthAAAIndentityInRootNAI"  tabindex="3"></html:text>
													</td>
												</tr>
		  											</table>
		  										</td>
		  									</tr>
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
													<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.akaprimeconfig"/>
												</td>
		  									<tr>
		  									<tr>
		  										<td class="box" width="49%">
		  											<table width="100%" cellspacing="2">
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudomethod"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.akaprime.pseudonymmethod"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudomethod"/>')"/>
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoencoding"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.akaprime.pseudonymencoding"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoencoding"/>')"/>
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoprefix"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.akaprime.pseudonymprefix"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoprefix"/>')"/>
													</td>
													<td>
														<html:text property="akaPrimePseudonymPrefix" maxlength="10" tabindex="9"></html:text>
													</td>
												</tr>
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudorootNAI"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.akaprime.pseudonymrootNAI"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudorootNAI"/>')"/>
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoAAAidentity"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.akaprime.pseudonymAAAidentityNAI"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.pseudoAAAidentity"/>')"/>
													</td>
													<td>
														<html:text property="akaPrimePseudonymAAAIndentityInRootNAI"  tabindex="3"></html:text>
													</td>
												</tr>
		  											</table>
		  										</td>
		  										<td width="2%"></td>
		  										<td class="box" width="49%">
		  											<table width="100%" cellspacing="2">
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
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.akaprime.fastreauthmethod"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthmethod"/>')"/>
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthencoding"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.akaprime.fastreauthencoding"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthencoding"/>')"/>
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthprefix"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.akaprime.fastreauthprefix"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthprefix"/>')"/>
													</td>
													<td>
														<html:text property="akaPrimeFastReauthPrefix" maxlength="10" tabindex="12"></html:text>
													</td>
												</tr>
												
												<tr>
													<td class="captiontext" width="40%">
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthrootNAI"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.akaprime.fastreauthrootNAI"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthrootNAI"/>')"/>
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
														<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthAAAidentity"/>
														<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="eapconfig.gsm.akaprime.fastreauthAAAidentityNAI"/>','<bean:message bundle="servermgrResources" key="servermgr.eapconfig.gsm.fastreauthAAAidentity"/>')"/>
													</td>
													<td>
														<html:text property="akaPrimeFastReauthAAAIndentityInRootNAI"  tabindex="3"></html:text>
													</td>
												</tr>
		  											</table>
		  										</td>
		  									</tr>
		  									</table>
		  								</td>
		  							</tr> 
		  							
		  							<tr>
		  								<td class="small-gap">&nbsp;</td>
		  							</tr>
		  							
		  							<tr>
		  								<td align="center">
		  									<input type="button" name="c_btnPrevious"  tabindex="13"   value=" Previous "  class="light-btn" onclick="javascript:history.back()"/>
		  									<input type="submit" name="c_btnCreate"   tabindex="14"  value=" Create "  class="light-btn" />
		  									<input type="button" name="c_btnCancel" tabindex="15"  onclick="javascript:location.href='<%=basePath%>/initSearchEAPConfig.do?/>'"  value="   Cancel   " class="light-btn"  />
		  								</td>
		  								
		  							</tr>
		  						</table>
		  						</html:form>
		  					</td>
		  				</tr>
		  			</table>	
  		  		</tr>
  		  		<%@ include file="/jsp/core/includes/common/Footer.jsp" %>
  		  	</table>
  		  </td>
  	</tr>
</table>  		  		