<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<bean:define id="crlCertificateDataBean" name="crlCertificateData" scope="request" type="com.elitecore.elitesm.datamanager.servermgr.certificate.data.CrlCertificateData" />
	<%CrlCertificateData crlData=crlCertificateDataBean; %>
	<tr>
		<td valign="top" align="right">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="tblheader-bold" colspan="2" height="20%">
						<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.view" />
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="21%">
						<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.name" />
					</td>
					<td class="tblcol" width="70%">
						<bean:write name="crlCertificateDataBean" property="crlCertificateName" />&nbsp;
					</td>
				</tr>
				<%if(crlData.getSerialNo()!=null){ %> 
					<tr>
						<td class="tblfirstcol" width="21%">
							<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.serialno" />
						</td>
						<td class="tblcol" width="70%">
							<div style="width: 603px; overflow: auto; word-wrap: break-word;">
								<bean:write name="crlCertificateDataBean" property="serialNo" /> &nbsp;
							</div>
						</td>
					</tr>	
				<%} %>
				<%if(crlData.getNextUpdate()!=null){ %>
					<tr>
						<td class="tblfirstcol" width="21%">
							<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.nextupdate" />
						</td>
						<td class="tblcol" width="70%">
							<div style="width: 603px; overflow: auto; word-wrap: break-word;">
								<bean:write name="crlCertificateDataBean" property="nextUpdate" /> &nbsp;
							</div>
						</td>
					</tr>	
				<%} %>
				<%if(crlData.getLastUpdate()!=null){ %>
					<tr>
						<td class="tblfirstcol" width="21%">
							<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.lastupdate" />
						</td>
						<td class="tblcol" width="70%">
							<div style="width: 603px; overflow: auto; word-wrap: break-word;">
								<bean:write name="crlCertificateDataBean" property="lastUpdate" /> &nbsp;
							</div>
						</td>
					</tr>	
				<%} %>
				<%if(crlData.getIssuer()!=null){ %>
					<tr>
						<td class="tblfirstcol" width="21%">
							<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.issuer" />
						</td>
						<td class="tblcol" width="70%">
							<div style="width: 603px; overflow: auto; word-wrap: break-word;">
								<bean:write name="crlCertificateDataBean" property="issuer" /> &nbsp;
							</div>								
						</td>
					</tr>	
				<%} %>
				<%if(crlData.getSignatureAlgo()!=null){ %>
					<tr>
						<td class="tblfirstcol" width="21%">
							<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.signaturealgo" />
						</td>
						<td class="tblcol" width="70%">
							<div style="width: 603px; overflow: auto; word-wrap: break-word;">
								<bean:write name="crlCertificateDataBean" property="signatureAlgo" /> &nbsp;
							</div>
						</td>
					</tr>	
				<%} %>			
			</table>
		</td>
	</tr>
	<tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
	</tr>
</table>