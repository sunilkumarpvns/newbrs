<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page
	import="com.elitecore.elitesm.datamanager.externalsystem.data.*"%>

<script>
setTitle('<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy"/>');
</script>

<%
    String basePath = request.getContextPath();	
%>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<link rel="stylesheet" href="<%=request.getContextPath()%>/jquery/development/themes/base/jquery.ui.all.css">
<script  src="<%=request.getContextPath()%>/js/history/jquery-2.0.3.min.js"></script>
<script  src="<%=request.getContextPath()%>/js/calender/jquery-ui.min.js"></script>
<table width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>"
	border="0" cellspacing="0" cellpadding="0">
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
												<%@ include file="ViewRadiusServicePolicyBasicDetails.jsp" %>
											</td>
										</tr>
										<tr>
											<td valign="top" align="right">
												  <%@ include file="UpdateRadiusServicePolicyAcctServiceFlow.jsp"%> 
											</td>
										</tr>
									</table>
								</td>
								<td width="168" class="grey-bkgd" valign="top">
									 <%@ include file="RadiusServicePolicyNavigation.jsp" %>
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
<div id="confirmDialog" title="Confirmation Required" class="labeltext">
  <b><span id="oldHandlerName"></span></b> has been renamed to <b><span id="newHandlerName"></span></b>.</br></br> Do you want to keep the changes ?
</div>