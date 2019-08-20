
<%@page import="com.elitecore.elitesm.util.logger.Logger"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>


<%@ page import="com.elitecore.elitesm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page
	import="com.elitecore.elitesm.util.constants.RadiusPolicyConstant"%>
<%@page
	import="com.elitecore.elitesm.web.diameter.peer.forms.SearchDiameterPeerForm"%>
<%@page
	import="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData"%>
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
<script>

setTitle('<bean:message bundle="diameterResources" key="diameterpeer.diameterpeer"/>');
</script>
<%
     SearchDiameterPeerForm searchDiameterPeerForm = (SearchDiameterPeerForm)request.getAttribute("searchDiameterPeerForm");
     List lstDiameterPeer = searchDiameterPeerForm.getListDiameterPeer();
     long totalRecord = searchDiameterPeerForm.getTotalRecords();
	 int count=1;
     String strTotalRecords = String.valueOf(totalRecord);
%>

<table cellpadding="0" cellspacing="0" border="0"
	width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
				<tr>
					<td width="100%" class="box">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td colspan="3">
									<table width="100%" name="c_tblDiameterPeer"
										id="c_tblDiameterPeer" align="right" border="0"
										cellpadding="0" cellspacing="0">

										<html:form action="/miscDiameterPolicy">
											<tr>
												<td align="left" class="labeltext" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="5" width="100%"
														border="0">
														<tr>
															<td class="table-header" width="50%"><bean:message
																	bundle="diameterResources" key="diameterpeer.peerlist" />
															</td>

															<td align="right" class="blue-text" valign="middle"
																width="50%">
																<%if(totalRecord > 0){ %> <bean:message
																	bundle="diameterResources"
																	key="diameterpeerprofile.totalrecords" /> <%= totalRecord %>
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
																			width="20%"><bean:message
																				bundle="diameterResources"
																				key="diameterpeer.hostidentity" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="15%"><bean:message
																				bundle="diameterResources"
																				key="diameterpeer.realmname" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="15%"><bean:message
																				bundle="diameterResources"
																				key="diameterpeer.remoteaddress" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="15%"><bean:message
																				bundle="diameterResources"
																				key="diameterpeer.localaddress" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="25%"><bean:message
																				bundle="diameterResources"
																				key="diameterpeer.profilename" /></td>
																	</tr>
																	<%	if(lstDiameterPeer!=null && lstDiameterPeer.size()>0){%>
																	<logic:iterate id="diameterPeerBean"
																		name="searchDiameterPeerForm"
																		property="listDiameterPeer"
																		type="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData">
																		<tr>
																			<td align="left" class="tblfirstcol"><%=count%></td>
																			<td align="left" class="tblrows"><bean:write
																					name="diameterPeerBean" property="hostIdentity" /></td>
																			<td align="left" class="tblrows"><bean:write
																					name="diameterPeerBean" property="realmName" />
																				&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="diameterPeerBean" property="remoteAddress" />
																				&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="diameterPeerBean" property="localAddress" />
																				&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="diameterPeerBean"
																					property="diameterPeerProfileData.profileName" />&nbsp;</td>
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
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
			</table>
		</td>
	</tr>
</table>
<script type="text/javascript">
$("#headerTitle").empty();
$("#headerTitle").text('<bean:message bundle="diameterResources" key="diameterpeer.diameterpeer"/>');
</script>