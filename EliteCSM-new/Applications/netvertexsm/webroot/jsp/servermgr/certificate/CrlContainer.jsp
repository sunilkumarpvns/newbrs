<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
 
<script>
	setTitle('<bean:message key="server.certificaterevocationlist"/>');
</script>

<table width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH)%>" border="0" cellspacing="0" cellpadding="0">
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
					<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td valign="top" align="right">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<table width="100%" border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td valign="top" colspan="4">
													 		<%@ include file="SearchCrlCertificate.jsp"%>  
														</td>
													</tr>
												</table>
											</td>
<%-- 											<td width="168" class="grey-bkgd" valign="top">
												<%@  include file="CertificateNavigation.jsp"%>
											</td>
 --%>										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
			</table>
		</td>
	</tr>
</table>