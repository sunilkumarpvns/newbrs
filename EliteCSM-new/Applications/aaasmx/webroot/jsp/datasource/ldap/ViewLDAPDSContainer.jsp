<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPBaseDnDetailData"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
    String basePath = request.getContextPath();
	LDAPDatasourceData ldapDatasourceData = (LDAPDatasourceData)session.getAttribute("ldapDatasourceData");
	List<LDAPBaseDnDetailData> baseDNList = ldapDatasourceData.getSearchDnDetailList();
	pageContext.setAttribute("baseDNList",baseDNList);
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
setTitle('<bean:message bundle="datasourceResources" key="ldap.ldap"/>');
</script>

<table width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td cellpadding="0" cellspacing="0" border="0" width="100%"
						class="box">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td valign="top" align="right">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<table width="100%" border="0" cellspacing="0"
													cellpadding="0">
													<tr>
														<td valign="top">
															<table width="100%" border="0" cellspacing="0"
																cellpadding="0">
																<tr>
																	<td valign="top" align="right" colspan="2"><%@ include
																			file="/jsp/datasource/ldap/ViewLDAPDS.jsp"%>
																	</td>
																</tr>
																<tr>
																	<td colspan="2" class="tblheader-bold"><bean:message
																			bundle="datasourceResources" key="ldap.basedndetails" />
																	</td>
																</tr>
																<tr>
																	<td class="tblheader"><bean:message
																			key="general.serialnumber" /></td>
																	<td class="tblheader">BaseDn Name</td>
																</tr>

																<%if(baseDNList!=null && !baseDNList.isEmpty()){ 
					    					 int index=1;
					    					%>
																<logic:iterate id="baseDNDetailData" name="baseDNList"
																	type="LDAPBaseDnDetailData" scope="page">
																	<tr>
																		<td class="tblfirstcol"><%=index%></td>
																		<td class="tblrows"><bean:write
																				name="baseDNDetailData" property="searchBaseDn" /></td>
																	</tr>
																	<%index++; %>
																</logic:iterate>
																<%}else{%>
																<tr>
																	<td class="tblfirstcol">No Record Found.</td>
																</tr>
																<%}%>
															</table>
														</td>
													</tr>
												</table>
											</td>
											<td width="168" class="grey-bkgd" valign="top"><%@ include
													file="/jsp/datasource/ldap/LDAPNavigation.jsp"%>
											</td>
										</tr>
										<tr>
											<td colspan="3" class="small-gap">&nbsp;</td>
										</tr>
									</table>
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


