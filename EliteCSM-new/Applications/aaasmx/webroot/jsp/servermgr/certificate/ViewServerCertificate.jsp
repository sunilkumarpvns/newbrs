<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<bean:define id="serverCertificateDataBean" name="serverCertificateData" scope="request" type="com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData" />
	<tr>
		<% ServerCertificateData data = serverCertificateDataBean; %>
		<td valign="top" align="right">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="tblheader-bold" colspan="2" height="20%">
						<bean:message bundle="servermgrResources" key="servermgr.certificate.view" />
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="21%">
						<bean:message bundle="servermgrResources" key="servermgr.certificate.name" />
					</td>
					<td class="tblcol" width="70%">
						<bean:write name="serverCertificateDataBean" property="serverCertificateName" />&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="21%">
						<bean:message bundle="servermgrResources" key="servermgr.certificate.subject" />
					</td>
					<td class="tblcol" width="70%">
						<div style="width: 603px; overflow: auto; word-wrap: break-word;">
							<bean:write name="serverCertificateDataBean" property="strSubjectName"/> &nbsp;
						</div>
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="21%">
						<bean:message bundle="servermgrResources" key="servermgr.certificate.issuer" />
					</td>
					<td class="tblcol" width="70%">
						<div style="width: 603px; overflow: auto; word-wrap: break-word;">
							<bean:write name="serverCertificateDataBean" property="strIssuerName"/> &nbsp;
						</div>
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="21%">
						<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.validfrom" />
					</td>
					<td class="tblcol" width="70%">
						<bean:write name="serverCertificateDataBean" property="validFrom" />&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="21%">
						<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.expireddate" />
					</td>
					<td class="tblcol" width="70%">
						<bean:write name="serverCertificateDataBean" property="validTo" />&nbsp;
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
	</tr>
</table>