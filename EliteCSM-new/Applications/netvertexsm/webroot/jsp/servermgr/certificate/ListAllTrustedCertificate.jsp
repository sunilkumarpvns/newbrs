<%@ page import="com.elitecore.netvertexsm.web.servermgr.certificate.form.TrustedCertificateForm" %>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.TrustedCertificateData"%>
<%@ page import="com.elitecore.netvertexsm.util.logger.Logger"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
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
setTitle('<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate"/>');
</script>
<%
     TrustedCertificateForm trustedCertificateForm = (TrustedCertificateForm)request.getAttribute("showAllTrustedCertificateForm");
	 List lstTrustedCertificate=trustedCertificateForm.getListTrustedCertificate();
     long totalRecord = trustedCertificateForm.getTotalRecords();
	 int count=1;
     String strTotalRecords = String.valueOf(totalRecord);
%>
<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
 	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
					<td width="100%" colspan="2" >
				        <table cellpadding="0" cellspacing="0" border="0" width="100%" >
				          	<tr>
					      		<td width="100%" colspan="2" >
					      		      <table width="100%" border="0" cellspacing="0" cellpadding="0"> 
								        <tr> 
								          <td width="26" valign="top" rowspan="2"><img src="<%=basePath%>/images/left-curve.jpg"></td> 
								          <td background="<%=basePath%>/images/header-gradient.jpg" width="133" rowspan="2" align="center" class="page-header">
								        	<bean:message bundle="servermgrResources" key="servermgr.certificate.header" />
								          	</td> 
								          <td width="32" rowspan="2"><img src="<%=basePath%>/images/right-curve.jpg"></td> 
								          <td width="*"></td> 
								        </tr> 
								        <tr> 
								          <td width="*" valign="bottom"><img src="<%=basePath%>/images/line.jpg" style="width: 100%"  height="7" ></td> 
								        </tr> 
								      </table> 
					      		</td>
							</tr>
				        </table>
				      </td>
				</tr>	
				<tr>
					<td class="small-gap">
						&nbsp;
					</td>
				</tr>
				
				<tr>
					<td width="100%" class="box">
		 				<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td colspan="3">
									<table width="100%" name="c_tblTrustedCertificateList" id="c_tblTrustedCertificateList" align="right" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="left" class="labeltext" colspan="5" valign="top">
									 			<table cellSpacing="0" cellPadding="5" width="100%" border="0">
										 			<tr>
														<td class="table-header"  style="border: none;"  width="50%">
															<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.list" />
														</td>
														<td align="right" class="blue-text" valign="middle" width="50%">
															<%if(totalRecord > 0){ %> 
																<bean:message bundle="servermgrResources" key="servermgr.certificate.totalrecords" /> <%= totalRecord %>
															<%} %>
														</td>
													</tr>
													<tr>
														<td class="topHLine" style="font-size: 1px" colspan="2">&nbsp;</td>
													</tr>													
													<tr height="2">
														<td></td>
													</tr>
													<tr>
														<td class="btns-td" valign="middle" colspan="2">
															<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listTable">
																<tr>
																	<td align="center" class="tblheaderfirstcol" valign="top" width="5%">
																		<bean:message bundle="servermgrResources" key="servermgr.serialnumber" />
																	</td>
																	<td align="left" class="tblheader" valign="top" width="15%">
																		<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.name" />
																	</td>
																	<td align="left" class="tblheader" valign="top" width="25%">
																		<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.subject" />
																	</td>
																	<td align="left" class="tblheader" valign="top" width="25%">
																		<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.issuer" />
																	</td>
																	<td align="left" class="tblheaderlastcol" valign="top" width="16%">
																		<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.expireddate" />
																	</td>
																	<%-- <td align="center" class="tblheader" valign="top" width="5%">
																		<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.downloadcertificate" />
																	</td>  --%>																													
																</tr>
																<%	if(lstTrustedCertificate!=null && lstTrustedCertificate.size()>0){%>
																	<logic:iterate id="trustedCertificateIdBean" name="trustedCertificateForm" property="listTrustedCertificate" type="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.TrustedCertificateData">
										 								<%TrustedCertificateData trustedCertData=trustedCertificateIdBean; %>
										 								<tr>
										 									<td align="left" class="tblfirstcol"><%=count%></td>
																			<td align="left" class="tblrows">
																				<div style="overflow: auto; word-wrap: break-word;">
																					<bean:write name="trustedCertificateIdBean" property="trustedCertificateName" />
																				</div>
																			</td>
																			<td align="left" class="tblrows">
																				<div style="overflow: auto; word-wrap: break-word;">
																					<% if (trustedCertData.getSubject() != null) { %>
																						<bean:define id="subjectData" name="trustedCertificateIdBean" property="subject" type="java.lang.String" />
																						<% String[] subjectDetail = subjectData.split(",");
																						for (String str : subjectDetail) {
																							if (str.contains("CN=")) { %>
																								<%=str.split("CN=")[1]%>
																							<% } %>
																						<% } %>
																					<% } %>
																				</div>
																			</td>
																			<td align="left" class="tblrows">
																				<div style="overflow: auto; word-wrap: break-word;">
																					<% if (trustedCertData.getIssuer() != null) { %>
																						<bean:define id="issuerData" name="trustedCertificateIdBean" property="issuer" type="java.lang.String" />
																						<% String[] issuerDetail = issuerData.split(",");
																						for (String str : issuerDetail) { 
																							if (str.contains("CN=")) { %>
																								<%=str.split("CN=")[1]%>
																							<% } %>
																						<% } %>
																					<% } %>
																				</div>
																			</td>
																			<td align="left" class="tblrows">
																				<div style="overflow: auto; word-wrap: break-word;">
																					<bean:write name="trustedCertificateIdBean" property="validTo" /> &nbsp;
																				</div>
																			</td>
																			<%-- <td align="center" class="tblrows">
																				<a href="javascript:void(0)" onclick="download(<bean:write name="trustedCertificateIdBean" property="trustedCertificateId"/>);">
																					<img src="<%=basePath%>/images/download.jpg" alt="Download" border="0" height="20" width="20">
																				</a>
																			</td> --%>
																		</tr> 
																		<% count=count+1; %>									
																	</logic:iterate>
																<%}else{ %>
																	<tr>
																	<td align="center" class="tblfirstcol" colspan="8">
																			<bean:message bundle="servermgrResources" key="servermgr.fileinformation.norecordfound" />
																	</td>
																</tr>
																<%} %>
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