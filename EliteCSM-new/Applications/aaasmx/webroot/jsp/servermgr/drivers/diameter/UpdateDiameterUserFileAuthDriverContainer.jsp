<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
    String basePath = request.getContextPath();
	//LDAPDatasourceData ldapDatasourceData = (LDAPDatasourceData)session.getAttribute("ldapDatasourceData");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
setTitle('<bean:message bundle="driverResources" key="driver.driver"/>');
</script>

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
														<td valign="top" align="right">
															<table width="100%" border="0" cellspacing="0"
																cellpadding="0" height="15%">
																<tr>
																	<td class="tblheader-bold" colspan="4" height="20%">
																		<bean:message bundle="driverResources"
																			key="driver.driverinstancedetails" />
																	</td>
																</tr>

																<tr>
																	<td class="tblfirstcol" height="20%" width="25%">
																		<bean:message bundle="driverResources"
																			key="driver.instname" />
																	</td>
																	<td class="tblfirstcol" colspan="3" height="20%"
																		width="25%"><bean:write
																			name="updateDiameterUserFileAuthDriverForm"
																			property="driverInstanceName" /></td>
																</tr>
																<tr>
																	<td class="tblfirstcol" height="20%" width="25%">
																		<bean:message bundle="driverResources"
																			key="driver.instdesc" />
																	</td>
																	<td class="tblfirstcol" colspan="3" height="20%"
																		width="25%"><bean:write
																			name="updateDiameterUserFileAuthDriverForm"
																			property="driverInstanceDesc" /></td>

																</tr>
																<tr>
																	<td class="tblheader-bold" colspan="4" height="20%">
																		<bean:message bundle="driverResources"
																			key="driver.driverdetails" />
																	</td>
																</tr>


																<tr>
																	<td class="tblfirstcol" height="20%" width="25%">
																		<bean:message bundle="driverResources"
																			key="driver.fileLocations" />
																	</td>
																	<td class="tblfirstcol" height="20%" width="25%">
																		<bean:write
																			name="updateDiameterUserFileAuthDriverForm"
																			property="fileLocations" />
																	</td>

																</tr>
															</table>
														</td>
													</tr>

												</table>
											</td>
										</tr>
										<tr>
											<td valign="bottom"><%@ include
													file="/jsp/servermgr/drivers/diameter/UpdateDiameterUserFileAuthDriver.jsp"%>
											</td>
										</tr>
									</table>
								</td>
								<td width="168" class="grey-bkgd" valign="top"><%@ include
										file="/jsp/servermgr/drivers/diameter/DiameterUserFileAuthDriverNavigation.jsp"%>
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
