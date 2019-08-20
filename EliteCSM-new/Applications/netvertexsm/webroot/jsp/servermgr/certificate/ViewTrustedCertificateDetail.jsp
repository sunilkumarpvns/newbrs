<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.TrustedCertificateData"%>
<script type="text/javascript">
function downloadCertificate(trustedCertificateId){	  
	  location.href='<%=basePath%>/trustedCertificate.do?method=downloadFile&trustedCertificateId='+trustedCertificateId;
}
</script>
<table width="97%" border="0" cellspacing="0" cellpadding="0" align="right">
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
								<td class="tblfirstcol" width="30%">
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
								<td class="tblfirstcol" width="30%">
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
								<td class="tblfirstcol" width="30%">
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
								<td class="tblfirstcol" width="30%">
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
								<td class="tblfirstcol" width="30%">
									<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.issuer" />
								</td>
								<td class="tblcol" width="70%">
									<div style="width: 603px; overflow: auto; word-wrap: break-word;">
										<bean:write name="trustedCertificateDataBean" property="issuer" /> &nbsp;
									</div>
								</td>
							</tr>
						<%}%>
						<%if(trustedCertData.getPublicKey()!=null){%>
							<tr>
								<td class="tblfirstcol" width="30%">
									<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.publickey" />
								</td>
								<td class="tblcol" width="70%">
									<bean:define id="publicKeyData" name="trustedCertificateDataBean" property="publicKey" type="java.lang.String" /> 
									<%String strPublicKey=publicKeyData;%>
									<div style="width: 603px; overflow: auto; word-wrap: break-word;">
										<%=strPublicKey.substring(0,strPublicKey.indexOf("modulus")) %>
										<br />
										<%=strPublicKey.substring(strPublicKey.indexOf("modulus"), strPublicKey.indexOf("public exponent")) %>
										<br />
										<%=strPublicKey.substring(strPublicKey.indexOf("public exponent"), strPublicKey.length()) %>
									</div>
								</td>
							</tr>
						<%}%>
						<%if(trustedCertData.getBasicConstraint()!=null){%>
							<tr>
								<td class="tblfirstcol" width="30%">
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
								<td class="tblfirstcol" width="30%">
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
								<td class="tblfirstcol" width="30%">
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
								<td class="tblfirstcol" width="30%">
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
								<td class="tblfirstcol" width="30%">
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
						<td width="25%">&nbsp;</td>
					</tr>
					<tr>
						<td align="center" colspan="2">
							<input type="button" name="c_btnDownload" value="Download Trusted Certificate" class="light-btn" onclick="downloadCertificate(<bean:write name="trustedCertificateDataBean" property="trustedCertificateId"/>);">
						</td>
					</tr>
					<tr>
						<td width="25%">&nbsp;</td>
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