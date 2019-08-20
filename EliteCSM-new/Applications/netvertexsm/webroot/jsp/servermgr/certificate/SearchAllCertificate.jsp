<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.ServerCertificateData"%>
<%@page import="com.elitecore.netvertexsm.web.servermgr.certificate.form.ServerCertificateForm"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.CrlCertificateData"%>
<%@page import="com.elitecore.netvertexsm.web.servermgr.certificate.form.CrlCertificateForm"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.TrustedCertificateData"%>
<%@page import="com.elitecore.netvertexsm.web.servermgr.certificate.form.TrustedCertificateForm"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@page import="java.util.List"%>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td class="table-header" colspan="5">
			<bean:message bundle="servermgrResources" key="servermgr.certificate.searchcertificate" />
		</td>
	</tr>
	<tr>
		<td colspan="3">
			<table width="100%" name="c_tblSearchServerCertificate" id="c_tblSearchServerCertificate" align="right" border="0" cellpadding="0" cellspacing="0">
				<%@include file="SearchServerCertificate.jsp"%>
				<tr><td><hr sty/></td></tr>
				<%@include file="SearchCrlCertificate.jsp"%>				
				<tr><td><hr/></td></tr>				
				<%@include file="SearchTrustedCertificate.jsp"%>
			</table>
		</td>
	</tr>
</table>
		