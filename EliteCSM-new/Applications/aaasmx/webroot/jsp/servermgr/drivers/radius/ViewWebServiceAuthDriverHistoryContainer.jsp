<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	String basePath = request.getContextPath();
	//LDAPDatasourceData ldapDatasourceData = (LDAPDatasourceData)session.getAttribute("ldapDatasourceData");
%>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<table width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>"
	border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td width="100%" class="box" colspan="2">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td valign="top" align="right" width="*">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<table width="100%" border="0" cellspacing="0"
													cellpadding="0">

													<tr>
														<td>
															<table width="100%" border="0" cellspacing="0"
																cellpadding="0">
																<tr>
																	<td class="tblheader-bold" height="20%" colspan="2">
																		<bean:message bundle="driverResources"
																			key="driver.view" />
																	</td>
																</tr>
																<tr>
																	<td align="left" class="tblheader-bold" valign="top"
																		colspan="2"><bean:message
																			bundle="driverResources"
																			key="driver.driverinstancedetails" /></td>
																</tr>
																<tr>
																	<td class="tblfirstcol" width="20%" height="20%">
																		<bean:message bundle="driverResources"
																			key="driver.instname" />
																	</td>
																	<td class="tblcol" width="30%" height="20%"><bean:write
																			name="updateWebServiceAuthDriverForm"
																			property="driverInstanceName" /></td>
																</tr>

																<tr>
																	<td class="tblfirstcol" width="20%" height="20%">
																		<bean:message bundle="driverResources"
																			key="driver.instdesc" />
																	</td>
																	<td class="tblcol" width="30%" height="20%"><bean:write
																			name="updateWebServiceAuthDriverForm"
																			property="driverInstanceDesc" /></td>
																</tr>

																<tr>
																	<td align="left" class="tblheader-bold" valign="top"
																		colspan="2"><bean:message
																			bundle="driverResources" key="driver.driverdetails" /></td>
																</tr>

																<tr>
																	<td align="left" class="tblfirstcol" valign="top">
																		<bean:message bundle="driverResources"
																			key="driver.webserviceauthdriver.serviceaddress" />
																	</td>
																	<td class="tblcol" width="30%" height="20%"><bean:write
																			name="updateWebServiceAuthDriverForm"
																			property="serviceAddress" /></td>
																</tr>
															</table>
														</td>
													</tr>
												</table>

											</td>
										</tr>
										<tr>
											<td><%@ include
													file="/jsp/core/system/history/ViewHistoryJSON.jsp"%>
											</td>
										</tr>
									</table>
								</td>
								<td width="168" class="grey-bkgd" valign="top"><%@ include
										file="/jsp/servermgr/drivers/radius/WebServiceAuthDriverNavigation.jsp"%>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
			</table>
		</td>
	</tr>
</table>
<script>
setTitle('<bean:message bundle="driverResources" key="driver.driver"/>'); 
</script>