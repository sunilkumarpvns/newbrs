<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<% String basePath = request.getContextPath(); %>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
	setTitle('<bean:message bundle="diameterResources" key="diameterpolicy.diameterpolicygroup"/>');
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
												<%@ include file="ViewDiameterPolicyGroup.jsp"%>
											</td>
										</tr>
										<tr>
											<td>
												<%@ include file="UpdateDiameterPolicyGroup.jsp"%>
											</td>
										</tr>
									</table>
								</td>
								<td width="168" class="grey-bkgd" valign="top">
									<%@ include file="DiameterPolicyGroupNavigation.jsp"%>
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
