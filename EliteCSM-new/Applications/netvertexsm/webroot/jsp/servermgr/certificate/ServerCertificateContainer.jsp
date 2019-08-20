<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<script>
$(document).ready(function(){
	setTitle('<bean:message key="server.certificate"/>');
});
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<tr>
		<td width="10" class="small-gap">&nbsp;</td>
		<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top" colspan="4">
							<%@ include file="SearchAllCertificate.jsp"%>
					</td>
				</tr>
			</table>
		</td>
	</tr>	
	<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>