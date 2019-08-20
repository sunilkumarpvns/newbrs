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
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.util.constants.RadiusPolicyConstant"%>
<%@page import="com.elitecore.elitesm.web.diameter.routingconfig.forms.SearchDiameterRoutingConfForm"%>
<%@page import="com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions"%>
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

$(document).ready(function(){
	$('img').click(function () {
		var imageid=$(this).attr("id");	
		var divid="show"+imageid;
		var imgElement = document.getElementById(imageid);
		 if ($("#"+divid).is(':hidden')) {
	            imgElement.src="<%=basePath%>/images/top-level.jpg";
	       } else {
	            imgElement.src="<%=basePath%>/images/bottom-level.jpg";
	       }
		 $("#"+divid).slideToggle("normal");
    }); 
});

setTitle('<bean:message bundle="diameterResources" key="routingconf.title"/>');
</script>
<%
     SearchDiameterRoutingConfForm searchDiameterRoutingConfForm = (SearchDiameterRoutingConfForm)request.getAttribute("searchDiameterRoutingConfForm");
     List lstDiameterRoutingConf = searchDiameterRoutingConfForm.getListDiameterRoutingConf();
     List lstDiameterRoutingTable = searchDiameterRoutingConfForm.getListDiameterRoutingTable();
     long totalRecord = searchDiameterRoutingConfForm.getTotalRecords();
	 int count=1;
     String strTotalRecords = String.valueOf(totalRecord);
     int total=0;
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
									<table width="100%" name="c_tblCrossProductList"
										id="c_tblCrossProductList" align="right" border="0"
										cellpadding="0" cellspacing="0">
										<tr>
											<td align="left" class="labeltext" colspan="5" valign="top">
												<table cellPadding="5" cellspacing="0" width="100%"
													border="0">
													<tr>
														<td class="table-header" width="50%"><bean:message
																bundle="diameterResources"
																key="routingconf.routingconflist" /></td>

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
																		width="5%"><bean:message
																			bundle="diameterResources"
																			key="diameterpeerprofile.serialnumber" /></td>
																	<td align="left" class="tblheader" valign="top"
																		width="15%"><bean:message
																			bundle="diameterResources" key="routingconf.name" /></td>
																	<td align="left" class="tblheader" valign="top"
																		width="20%"><bean:message
																			bundle="diameterResources"
																			key="routingconf.realmname" /></td>
																	<td align="left" class="tblheader" valign="top"
																		width="10%"><bean:message
																			bundle="diameterResources" key="routingconf.appids" /></td>
																	<td align="left" class="tblheader" valign="top"
																		width="10%"><bean:message
																			bundle="diameterResources"
																			key="routingconf.originhost" /></td>
																	<td align="left" class="tblheader" valign="top"
																		width="10%"><bean:message
																			bundle="diameterResources"
																			key="routingconf.originrealm" /></td>
																	<td align="left" class="tblheader" valign="top"
																		width="10%"><bean:message
																			bundle="diameterResources" key="routingconf.ruleset" /></td>
																	<td align="left" class="tblheader" valign="top"
																		width="10%"><bean:message
																			bundle="diameterResources"
																			key="routingconf.transmapconf" /></td>
																	<td align="left" class="tblheader" valign="top"
																		width="10%"><bean:message
																			bundle="diameterResources"
																			key="routingconf.routingaction" /></td>
																</tr>
																<%	if(lstDiameterRoutingConf!=null && lstDiameterRoutingConf.size()>0 && lstDiameterRoutingTable !=null && lstDiameterRoutingTable.size()>0){%>
																<logic:iterate id="diameterRoutingTableBean"
																	name="searchDiameterRoutingConfForm"
																	property="listDiameterRoutingTable"
																	type="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData">
																	<bean:define id="tableid"
																		name="diameterRoutingTableBean"
																		property="routingTableId" type="java.lang.Long" />
																	<%	
																						    total=0;
																						%>
																	<tr>
																		<td align="left" colspan="100%" class="tblheader-bold">
																			<bean:write name="diameterRoutingTableBean"
																				property="routingTableName" /> <span align="right"
																			style="width: 100%"> <img align="right"
																				alt="bottom" id="${tableid}"
																				src="<%=basePath%>/images/top-level.jpg" />
																		</span>
																		</td>
																	</tr>
																	<tr>
																		<td colspan="100%">
																			<table id="show${tableid}" width="100%" border="0"
																				cellpadding="0" cellspacing="0">
																				<logic:iterate id="diameterRoutingConfBean"
																					name="searchDiameterRoutingConfForm"
																					property="listDiameterRoutingConf"
																					type="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData">
																					<logic:equal name="diameterRoutingConfBean"
																						property="routingTableId" value="${tableid}">
																						<tr>
																							<td align="left" width="5%" class="tblfirstcol"><%=count%></td>
																							<td align="left" width="15%" class="tblrows"><bean:write
																									name="diameterRoutingConfBean" property="name" /></td>
																							<td align="left" width="20%" class="tblrows"><bean:write
																									name="diameterRoutingConfBean"
																									property="realmName" /> &nbsp;</td>
																							<td align="left" width="10%" class="tblrows"><bean:write
																									name="diameterRoutingConfBean"
																									property="appIds" /> &nbsp;</td>
																							<td align="left" width="10%" class="tblrows"><bean:write
																									name="diameterRoutingConfBean"
																									property="originHost" /> &nbsp;</td>
																							<td align="left" width="10%" class="tblrows"><bean:write
																									name="diameterRoutingConfBean"
																									property="originRealm" />&nbsp;</td>
																							<td align="left" width="10%" class="tblrows"><bean:write
																									name="diameterRoutingConfBean"
																									property="ruleset" /> &nbsp;</td>
																							<%if(diameterRoutingConfBean.getTransMapConfId() != null){%>
																							<td align="left" width="10%" class="tblrows"><bean:write
																									name="diameterRoutingConfBean"
																									property="translationMappingConfData.name" />
																								&nbsp;</td>
																							<%}else{%>
																							<td align="left" width="10%" class="tblrows">
																								&nbsp;</td>
																							<%}%>
																							<td align="left" width="10%" class="tblrows">
																								<bean:write name="diameterRoutingConfBean" property="routingActionName" /> &nbsp;
																							</td>
																						</tr>
																						<%total=total+1; %>
																						<% count=count+1; %>
																					</logic:equal>
																				</logic:iterate>
																				<%if(total == 0){ %>
																				<tr>
																					<td align="center" class="tblfirstcol"
																						colspan="100%"><bean:message
																							bundle="diameterResources"
																							key="routingconf.norecords" /></td>
																				</tr>
																				<%}%>
																			</table>
																		</td>
																	</tr>
																</logic:iterate>
																<%	}else{
																		%>
																<tr>
																	<td align="center" class="tblfirstcol" colspan="100%">
																		<bean:message bundle="diameterResources"
																			key="diameterpeerprofile.norecordsmsg" />
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
$("#headerTitle").text('<bean:message bundle="diameterResources" key="routingconf.title"/>');
</script>