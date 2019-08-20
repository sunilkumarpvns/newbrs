<%@page import="sun.security.x509.CRLDistributionPointsExtension"%>
<%@page import="java.security.cert.X509CRLEntry"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.CrlCertificateData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.TrustedCertificateData"%>
<%@page import="java.text.SimpleDateFormat"%>

<%@page import="java.util.Set"%>
<script type="text/javascript">
function downloadCertificate(crlCertificateId){	  
	  location.href='<%=basePath%>/crlCertificate.do?method=downloadFile&crlCertificateId='+crlCertificateId;
}
</script>
<table width="97%" border="0" cellspacing="0" cellpadding="0" align="right">
	<tr>
		<td valign="top" align="right">
		<%CrlCertificateData crlCertData=crlCertificateDataBean; %>
		<%if(crlCertData!=null){ %>
			<html:form action="/crlCertificate.do?method=downloadFile">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="tblheader-bold" colspan="2" height="20%">
							<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.crlcert.revokedcert" />
						</td>
					</tr>
					<%if(crlCertData.getRevokedList()!=null){ %>
						<%Set<X509CRLEntry> x509CrlEntries=crlCertData.getRevokedList();
						for(X509CRLEntry crlEntry : x509CrlEntries){%>
							<tr>
								<td class="tblfirstcol" width="30%">
									<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.serialno" />
								</td>
								<td class="tblcol" width="70%">
									<div style="width: 603px; overflow: auto; word-wrap: break-word;">
										<%=crlEntry.getSerialNumber() %> &nbsp;
									</div>
								</td>
							</tr>
							<tr>
								<td class="tblfirstcol" width="30%">
									<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.revocationdate" />
								</td>
								<td class="tblcol" width="70%">
									<div style="width: 603px; overflow: auto; word-wrap: break-word;">
										<%SimpleDateFormat simpleDate=new SimpleDateFormat("dd MMM yyyy kk:mm:ss ");%>
										<%=simpleDate.format(crlEntry.getRevocationDate()).toString() %> &nbsp;
									</div>
								</td>
							</tr>
							<%if(crlEntry.getCertificateIssuer()!=null){%>
							<tr>
								<td class="tblfirstcol" width="30%">
									<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.certificateissuer" />
								</td>
								<td class="tblcol" width="70%">
									<div style="width: 603px; overflow: auto; word-wrap: break-word;">
										<%=crlEntry.getCertificateIssuer()%> &nbsp;
									</div>
								</td>
							</tr>
							<%} %>
						<%}%>					
					<%}else{ %>
							<tr>
								<td class="tblfirstcol" width="30%" colspan="2" align="center">
									<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.norevokedcertificate" />
								</td>
							</tr>
						<%} %>
					<tr>
						<td width="25%">&nbsp;</td>
					</tr>
					<tr>
						<td align="center" colspan="2">
							<input type="button" name="c_btnDownload" value="Download CRL Certificate" class="light-btn" onclick="downloadCertificate(<bean:write name="crlCertificateDataBean" property="crlCertificateId"/>);">
						</td>
					</tr>
					<tr>
						<td width="25%">&nbsp;</td>
					</tr>
				</table>				
			</html:form>
			<%} %>
		</td>
	</tr>
</table>