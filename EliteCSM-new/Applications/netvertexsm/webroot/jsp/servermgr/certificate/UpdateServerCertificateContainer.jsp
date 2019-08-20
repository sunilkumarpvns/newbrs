<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>

<script>
$(document).ready(function(){
	setTitle('<bean:message bundle="servermgrResources" key="servermgr.certificate"/>');
});
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
							<tr>
								<td width="10" class="small-gap">&nbsp;</td>
								<td valign="top" align="right" width="85%">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<%@ include file="ViewServerCertificate.jsp"%>
											</td>
										</tr>
										<tr>
											<td valign="top" align="right">
												<%@ include file="UpdateServerCertificate.jsp"%>
											</td>
										</tr>
									</table>
								</td>
								<td width="15%" class="grey-bkgd" valign="top">
									<%@ include file="ServerCertificateNavigation.jsp"%>
								</td>
							</tr>
				<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>