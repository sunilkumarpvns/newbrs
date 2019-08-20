<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.ServerCertificateData"%>

<table width="97%" border="0" cellspacing="0" cellpadding="0" align="right">
	<bean:define id="serverCertificateDataBean" name="serverCertificateData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.ServerCertificateData" />
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
					<td class="tblfirstcol" width="30%">
						<bean:message bundle="servermgrResources" key="servermgr.certificate.name" />
					</td>
					<td class="tblcol" width="70%">
						<bean:write name="serverCertificateDataBean" property="serverCertificateName" />&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="30%">
						<bean:message bundle="servermgrResources" key="servermgr.certificate.subject" />
					</td>
					<td class="tblcol" width="70%">
						<div style="width: 603px; overflow: auto; word-wrap: break-word;">
							<% if (data.getSubject() != null) { %>
								<bean:define id="subjectData" name="serverCertificateDataBean" property="subject" type="java.lang.String" />
								<% String[] subjectDetail = subjectData.split(",");
								for (String str : subjectDetail) {
									if (str.contains("CN=")) { %>
										<%=str.split("CN=")[1]%>
									<% } %>
								<% } %>
							<% } %>
						</div>
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="30%">
						<bean:message bundle="servermgrResources" key="servermgr.certificate.issuer" />
					</td>
					<td class="tblcol" width="70%">
						<div style="width: 603px; overflow: auto; word-wrap: break-word;">
							<% if (data.getIssuer() != null) { %>
								<bean:define id="issuerData" name="serverCertificateDataBean" property="issuer" type="java.lang.String" />
								<% String[] issuerDetail = issuerData.split(",");
								for (String str : issuerDetail) {
									if (str.contains("CN=")) { %>
										<%=str.split("CN=")[1]%>
									<% } %>
								<% } %>
							<% } %>
						</div>
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="30%">
						<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.validfrom" />
					</td>
					<td class="tblcol" width="70%">
						<bean:write name="serverCertificateDataBean" property="validFrom" />&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="30%">
						<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.expireddate" />
					</td>
					<td class="tblcol" width="70%">
						<bean:write name="serverCertificateDataBean" property="validTo" />&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="30%">
						<bean:message key="general.createddate" />
					</td>
					<td class="tblcol" width="70%">
						<%=EliteUtility.dateToString(serverCertificateDataBean.getCreateDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>
					</td>
				</tr><tr>
					<td class="tblfirstcol" width="30%">
						<bean:message key="general.lastmodifieddate" />
					</td>
					<td class="tblcol" width="70%">
						<%=EliteUtility.dateToString(serverCertificateDataBean.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;
					</td>
				</tr>					
			</table>
		</td>
	</tr>
	<tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
	</tr>
</table>