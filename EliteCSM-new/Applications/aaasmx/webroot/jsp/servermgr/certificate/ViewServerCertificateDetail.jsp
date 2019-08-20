<%@page import="com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData"%>
<script type="text/javascript">
function downloadCertificate(serverCertificateId){	  
	  location.href='<%=basePath%>/serverAllCertificates.do?method=downloadFile&serverCertificateId='+serverCertificateId;
}
function searchTrustedCertificate(){
	location.href='<%=basePath%>/serverAllCertificates.do?method=initSearch';
}
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top" align="right">
			<% ServerCertificateData serverCertData=serverCertificateDataBean; 
			if(serverCertData!=null){ %> 
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="tblheader-bold" colspan="2" height="20%">
								<bean:message bundle="servermgrResources" key="servermgr.certificate.publiccert.detail" />
							</td>
						</tr>
						<%if(serverCertData.getPublicKey()!=null){ %>
							<%if(serverCertData.getVersion()!=null){ %>
								<tr>
									<td class="tblfirstcol" width="21%">
										<bean:message bundle="servermgrResources" key="servermgr.certificate.version" />
									</td>
									<td class="tblcol" width="70%">
										<div style="width: 603px; overflow: auto; word-wrap: break-word;">
											<bean:write name="serverCertificateDataBean" property="version" /> &nbsp;
										</div>
									</td>
								</tr>	
							<%} %>
							<%if(serverCertData.getSerialNo()!=null){ %>
								<tr>
									<td class="tblfirstcol" width="21%">
										<bean:message bundle="servermgrResources" key="servermgr.certificate.serialno" />
									</td>
									<td class="tblcol" width="70%">
										<div style="width: 603px; overflow: auto; word-wrap: break-word;">
											<bean:write name="serverCertificateDataBean" property="serialNo" /> &nbsp;
										</div>
									</td>
								</tr>
							<%} %>
							<%if(serverCertData.getSignatureAlgo()!=null){ %>
								<tr>
									<td class="tblfirstcol" width="21%">
										<bean:message bundle="servermgrResources" key="servermgr.certificate.signaturealgo" />
									</td>
									<td class="tblcol" width="70%">
										<div style="width: 603px; overflow: auto; word-wrap: break-word;">
											<bean:write name="serverCertificateDataBean" property="signatureAlgo" /> &nbsp;
										</div>
									</td>
								</tr>
							<%} %>
							<%if(serverCertData.getSubject()!=null){ %>
								<tr>
									<td class="tblfirstcol" width="21%">
										<bean:message bundle="servermgrResources" key="servermgr.certificate.subject" />
									</td>
									<td class="tblcol" width="70%">
										<div style="width: 603px; overflow: auto; word-wrap: break-word;">
											<bean:write name="serverCertificateDataBean" property="subject" /> &nbsp;
										</div>
									</td>
								</tr>
							<%} %>
							<%if(serverCertData.getIssuer()!=null){ %>
								<tr>
									<td class="tblfirstcol" width="21%">
										<bean:message bundle="servermgrResources" key="servermgr.certificate.issuer" />
									</td>
									<td class="tblcol" width="70%">
										<div style="width: 603px; overflow: auto; word-wrap: break-word;">
											<bean:write name="serverCertificateDataBean" property="issuer" /> &nbsp;
										</div>
									</td>
								</tr>
							<%} %>
							<%if(serverCertData.getBasicConstraint()!=null){ %>
								<tr>
									<td class="tblfirstcol" width="21%">
										<bean:message bundle="servermgrResources" key="servermgr.certificate.basicconstraint" />
									</td>
									<td class="tblcol" width="70%">
										<div style="width: 603px; overflow: auto; word-wrap: break-word;">
											<bean:write name="serverCertificateDataBean" property="basicConstraint" /> &nbsp;
										</div>
									</td>
								</tr>
							<%} %>
							<%if(serverCertData.getKeyUsage()!=null){ %>
								<tr>
									<td class="tblfirstcol" width="21%">
										<bean:message bundle="servermgrResources" key="servermgr.certificate.keyusage" />
									</td>
									<td class="tblcol" width="70%">
										<div style="width: 603px; overflow: auto; word-wrap: break-word;">
											<bean:write name="serverCertificateDataBean" property="keyUsage" /> &nbsp;
										</div>
									</td>
								</tr>
							<%} %>
							<%if(serverCertData.getSubjectUniqueID()!=null){ %>
								<tr>
									<td class="tblfirstcol" width="21%">
										<bean:message bundle="servermgrResources" key="servermgr.certificate.subjectuniqueid" />
									</td>
									<td class="tblcol" width="70%">
										<div style="width: 603px; overflow: auto; word-wrap: break-word;">
											<bean:write name="serverCertificateDataBean" property="subjectUniqueID" /> &nbsp;
										</div>
									</td>
								</tr>
							<%} %>
							<%if(serverCertData.getSubjectAltName()!=null){ %>
								<tr>
									<td class="tblfirstcol" width="21%">
										<bean:message bundle="servermgrResources" key="servermgr.certificate.subjectaltname" />
									</td>
									<td class="tblcol" width="70%">
										<div style="width: 603px; overflow: auto; word-wrap: break-word;">
											<bean:write name="serverCertificateDataBean" property="subjectAltName" /> &nbsp;
										</div>
									</td>
								</tr>
							<%} %>
							<%if(serverCertData.getIssuerAltName()!=null){ %>
								<tr>
									<td class="tblfirstcol" width="21%">
										<bean:message bundle="servermgrResources" key="servermgr.certificate.issueraltname" />
									</td>
									<td class="tblcol" width="70%">
										<div style="width: 603px; overflow: auto; word-wrap: break-word;">
											<bean:write name="serverCertificateDataBean" property="issuerAltName" /> &nbsp;
										</div>
									</td>
								</tr>
							<%} %>
						<%}else{ %>
							<tr>
								<td class="tblfirstcol" width="30%" colspan="2" align="center">
									<bean:message bundle="servermgrResources" key="servermgr.certificate.nopubliccertificate" />
								</td>
							</tr>
						<%} %>
						<tr>
							<td width="21%">&nbsp;</td>
						</tr>
						<%if(serverCertData.getPublicKey()!=null){ %>
							<tr>
								<td align="left" colspan="2" style="padding-left: 280px;">		
									<input type="button" name="c_btnDownload" value="Back" class="light-btn" onclick="searchTrustedCertificate();"/>						
									<input type="button" name="c_btnDownload" value="Download Public Certificate" class="light-btn" onclick="downloadCertificate('<bean:write name="serverCertificateDataBean" property="serverCertificateId"/>');">
								</td>
							</tr>
						<%} %>
						<tr>
							<td width="21%">&nbsp;</td>
						</tr>
					</table>
			<%} %>
		</td>
	</tr>
</table>