<%@page import="com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData"%>
<%@ page import="com.elitecore.elitesm.web.servermgr.certificate.forms.ServerCertificateForm"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<script type="text/javascript">
$(document).ready(function() {
	   $("#servercertificateToggleImageElement").click(function(){
			var imgElement = document.getElementById("servercertificateToggleImageElement");
			 if ($("#servercertificateToggleDivElement").is(':hidden')) {
			        imgElement.src="<%=basePath%>/images/top-level.jpg";
		     } else {
			        imgElement.src="<%=basePath%>/images/bottom-level.jpg";
			 }
			 $("#servercertificateToggleDivElement").slideToggle("normal");
	  });
});

</script>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td class="table-header" colspan="5">
			<bean:message bundle="servermgrResources" key="servermgr.certificate.search" />
		</td>
	</tr>
	<tr>
		<td colspan="3">
		 <html:form action="/serverAllCertificates.do?method=initSearch" styleId="serverCertificateFormId">
			<table width="100%" name="c_tblSearchServerCertificate" id="c_tblSearchServerCertificate" align="right" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td>
						<%@ include file="SearchServerCertificate.jsp"%>
					</td>
				</tr>
				<tr>
					<td>
						<%@ include file="SearchTrustedCertificate.jsp"%>
					</td>
				</tr>
				<tr>
					<td>
						<%@ include file="SearchCrlCertificate.jsp"%> 
					</td>
				</tr>
				<tr>
					 <td>&nbsp;</td>
				</tr>
				
			</table>
			</html:form>
		</td>
	</tr>
</table>
		
