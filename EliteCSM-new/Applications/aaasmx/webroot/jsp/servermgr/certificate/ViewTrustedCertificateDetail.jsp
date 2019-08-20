<%@page import="com.elitecore.elitesm.datamanager.servermgr.certificate.data.TrustedCertificateData"%>
<script type="text/javascript">
function downloadCertificate(trustedCertificateId){	  
	  location.href='<%=basePath%>/serverAllCertificates.do?method=downloadTrustedFile&trustedCertificateId='+trustedCertificateId;
}
function searchCertificate(){
	location.href='<%=basePath%>/serverAllCertificates.do?method=initSearch';
}
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top" align="right">
		<%TrustedCertificateData trustedCertData=trustedCertificateDataBean;%>
		<%if(trustedCertData!=null){ %>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="tblheader-bold" colspan="2" height="20%">
								<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.trustedcert.detail" />
						</td>
					</tr>
					<%if(trustedCertData.getPublicKey()!=null){ %>
						<%if(trustedCertData.getVersion()!=null){ %>
							<tr>
								<td class="tblfirstcol" width="21%">
									<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.version" />
								</td>
								<td class="tblcol" width="70%">
									<div style="width: 603px; overflow: auto; word-wrap: break-word;">
										<bean:write name="trustedCertificateDataBean" property="version" /> &nbsp;
									</div>
								</td>
							</tr>	
						<%}%>
						<%if(trustedCertData.getSerialNo()!=null){%>
							<tr>
								<td class="tblfirstcol" width="21%">
									<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.serialno" />
								</td>
								<td class="tblcol" width="70%">
									<div style="width: 603px; overflow: auto; word-wrap: break-word;">
										<bean:write name="trustedCertificateDataBean" property="serialNo" /> &nbsp;
									</div>
								</td>
							</tr>
						<%}%>
						<%if(trustedCertData.getSignatureAlgo()!=null){%>
							<tr>
								<td class="tblfirstcol" width="21%">
									<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.signaturealgo" />
								</td>
								<td class="tblcol" width="70%">
									<div style="width: 603px; overflow: auto; word-wrap: break-word;">
										<bean:write name="trustedCertificateDataBean" property="signatureAlgo" /> &nbsp;
									</div>
								</td>
							</tr>
						<%}%>
						<%if(trustedCertData.getSubject()!=null){%>
							<tr>
								<td class="tblfirstcol" width="21%">
									<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.subject" />
								</td>
								<td class="tblcol" width="70%">
									<div style="width: 603px; overflow: auto; word-wrap: break-word;">
										<bean:write name="trustedCertificateDataBean" property="subject" /> &nbsp;
									</div>
								</td>
							</tr>
						<%}%>
						<%if(trustedCertData.getIssuer()!=null){%>
							<tr>
								<td class="tblfirstcol" width="21%">
									<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.issuer" />
								</td>
								<td class="tblcol" width="70%">
									<div style="width: 603px; overflow: auto; word-wrap: break-word;">
										<bean:write name="trustedCertificateDataBean" property="issuer" /> &nbsp;
									</div>
								</td>
							</tr>
						<%}%>
						<%if(trustedCertData.getBasicConstraint()!=null){%>
							<tr>
								<td class="tblfirstcol" width="21%">
									<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.basicconstraint" />
								</td>
								<td class="tblcol" width="70%">
									<div style="width: 603px; overflow: auto; word-wrap: break-word;">
										<bean:write name="trustedCertificateDataBean" property="basicConstraint" /> &nbsp;
									</div>
								</td>
							</tr>
						<%}%>
						<%if(trustedCertData.getKeyUsage()!=null){%>
							<tr>
								<td class="tblfirstcol" width="21%">
									<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.keyusage" />
								</td>
								<td class="tblcol" width="70%">
									<div style="width: 603px; overflow: auto; word-wrap: break-word;">
										<bean:write name="trustedCertificateDataBean" property="keyUsage" /> &nbsp;
									</div>
								</td>
							</tr>
						<%}%>
						<%if(trustedCertData.getSubjectUniqueID()!=null){%>
							<tr>
								<td class="tblfirstcol" width="21%">
									<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.subjectuniqueid" />
								</td>
								<td class="tblcol" width="70%">
									<div style="width: 603px; overflow: auto; word-wrap: break-word;">
										<bean:write name="trustedCertificateDataBean" property="subjectUniqueID" /> &nbsp;
									</div>
								</td>
							</tr>
						<%}%>
						<%if(trustedCertData.getSubjectAltName()!=null){%>
							<tr>
								<td class="tblfirstcol" width="21%">
									<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.subjectaltname" />
								</td>
								<td class="tblcol" width="70%">
									<div style="width: 603px; overflow: auto; word-wrap: break-word;">
										<bean:write name="trustedCertificateDataBean" property="subjectAltName" /> &nbsp;
									</div>
								</td>
							</tr>
						<%}%>
						<%if(trustedCertData.getIssuerAltName()!=null){%>
							<tr>
								<td class="tblfirstcol" width="21%">
									<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.issueraltname" />
								</td>
								<td class="tblcol" width="70%">
									<div style="width: 603px; overflow: auto; word-wrap: break-word;">
										<bean:write name="trustedCertificateDataBean" property="issuerAltName" /> &nbsp;
									</div>
								</td>
							</tr>
						<%}%>
					<tr>
						<td width="21%">&nbsp;</td>
					</tr>
					<tr>
						<td align="left" colspan="2" style="padding-left: 280px;">
							<input type="button" name="c_btnDownload" value="Back" class="light-btn" onclick="searchCertificate();"/>
							<input type="button" name="c_btnDownload" value="Download Trusted Certificate" class="light-btn" onclick="downloadCertificate('<bean:write name="trustedCertificateDataBean" property="trustedCertificateId"/>');">
						</td>
					</tr>
					<tr>
						<td width="21%">&nbsp;</td>
					</tr>
					<%}else{ %>
						<tr>
							<td class="tblfirstcol" width="30%" colspan="2" align="center">
								<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.notrustedcertificate" />
							</td>
						</tr>
					<%} %>
				</table>
			<%} %>
		</td>
	</tr>
</table>