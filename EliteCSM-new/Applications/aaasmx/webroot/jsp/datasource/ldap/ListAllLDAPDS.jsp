<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.*"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@page import="com.elitecore.elitesm.web.datasource.ldap.forms.SearchLDAPDatasourceForm"%>

<%
    String basePath = request.getContextPath();
%>

<style>
.light-btn {
	border: medium none;
	font-family: Arial;
	font-size: 12px;
	color: #FFFFFF;
	background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg');
	font-weight: bold
}
</style>
<%
     SearchLDAPDatasourceForm searchLDAPDatasourceForm = (SearchLDAPDatasourceForm)request.getAttribute("searchLDAPDS");
     List lstLDAPDS = searchLDAPDatasourceForm.getSearchList();
     long totalRecord = searchLDAPDatasourceForm.getTotalRecords();
	 int count=1;
     String strTotalRecords = String.valueOf(totalRecord);
%>

<table cellpadding="0" cellspacing="0" border="0"
	width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td style="padding-left: 20px;">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
				<tr>
					<td width="98%" class="box">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td colspan="3">
									<table width="100%" name="c_tblDiameterPeer"
										id="c_tblDiameterPeer" align="right" border="0"
										cellpadding="0" cellspacing="0">

										<html:form action="/searchLDAPDS">
											<tr>
												<td align="left" class="labeltext" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="5" width="100%"
														border="0">
														<tr>
															<td class="table-header" width="50%"><bean:message
																	bundle="datasourceResources" key="ldap.ldapList" /></td>

															<td align="right" class="blue-text" valign="middle"
																width="50%">
																<%if(totalRecord > 0){ %> <bean:message
																	bundle="diameterResources"
																	key="diameterpeerprofile.totalrecords" /> <%=totalRecord%>
																<%} %>
															</td>
														</tr>
														<tr>
															<td></td>
														</tr>
														<tr height="2">
															<td></td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle" colspan="2">
																<table width="100%" border="0" cellpadding="0"
																	cellspacing="0" id="listTable">
																	<tr>
																		<td align="left" class="tblheader" valign="top"
																			width="8%"><bean:message
																				bundle="diameterResources"
																				key="diameterpeer.serialnumber" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="15%"><bean:message
																				bundle="datasourceResources" key="ldap.name" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="20%"><bean:message
																				bundle="datasourceResources" key="ldap.address" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="8%"><bean:message
																				bundle="datasourceResources" key="ldap.timeout" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="8%"><bean:message
																				bundle="datasourceResources"
																				key="ldap.ldapsizelimit" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="10%"><bean:message
																				bundle="datasourceResources" key="ldap.userdnprefix" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="5%"><bean:message
																				bundle="datasourceResources" key="ldap.maximumpool" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="5%"><bean:message
																				bundle="datasourceResources" key="ldap.minimumpool" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="15%"><bean:message
																				bundle="datasourceResources"
																				key="ldap.statuscheckduration" /></td>
																	</tr>
																	<%	if(lstLDAPDS!=null && lstLDAPDS.size()>0){%>
																	<logic:iterate id="ldapDSBean" name="searchLDAPDS"
																		property="searchList"
																		type="com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData">
																		<tr>
																			<td align="left" class="tblfirstcol"><%=count%></td>
																			<td align="left" class="tblrows"><bean:write
																					name="ldapDSBean" property="name" /></td>
																			<td align="left" class="tblrows"><bean:write
																					name="ldapDSBean" property="address" /> &nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="ldapDSBean" property="timeout" /> &nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="ldapDSBean" property="ldapSizeLimit" />&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="ldapDSBean" property="userDNPrefix" />
																				&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="ldapDSBean" property="maximumPool" /> &nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="ldapDSBean" property="minimumPool" /> &nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="ldapDSBean" property="statusCheckDuration" />&nbsp;</td>
																		</tr>
																		<% count=count+1; %>
																	</logic:iterate>

																	<%	}else{
																		%>
																	<tr>
																		<td align="center" class="tblfirstcol" colspan="8">
																			<bean:message bundle="diameterResources"
																				key="diameterpeer.norecordsmsg" />
																		</td>
																	</tr>
																	<%	}%>
																</table>
															</td>
														</tr>

														<tr height="2">
															<td></td>
														</tr>
													</table>
												</td>
											</tr>
										</html:form>
									</table>
								</td>
							</tr>

						</table>
					</td>
				</tr>
				<tr>
					<td width="2%">&nbsp;</td>
					<td>
					<td width="98%">
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
					</td>
				</tr>
				
			</table>
		</td>
	</tr>
</table>
<script type="text/javascript">
$("#headerTitle").empty();
$("#headerTitle").text('<bean:message bundle="datasourceResources" key="ldap.ldap"/>');
</script>