<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.ServerCertificateData"%>
<script type="text/javascript">
function downloadCertificate(serverCertificateId){	  
	  location.href='<%=basePath%>/serverCertificate.do?method=downloadFile&serverCertificateId='+serverCertificateId;
}
</script>
<table width="97%" border="0" cellspacing="0" cellpadding="0" align="right">
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
									<td class="tblfirstcol" width="30%">
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
									<td class="tblfirstcol" width="30%">
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
									<td class="tblfirstcol" width="30%">
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
									<td class="tblfirstcol" width="30%">
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
									<td class="tblfirstcol" width="30%">
										<bean:message bundle="servermgrResources" key="servermgr.certificate.issuer" />
									</td>
									<td class="tblcol" width="70%">
										<div style="width: 603px; overflow: auto; word-wrap: break-word;">
											<bean:write name="serverCertificateDataBean" property="issuer" /> &nbsp;
										</div>
									</td>
								</tr>
							<%} %>
							<%if(serverCertData.getPublicKey()!=null){ %>
								<tr>
									<td class="tblfirstcol" width="30%">
										<bean:message bundle="servermgrResources" key="servermgr.certificate.publickey" />
									</td>
									<td class="tblcol" width="70%">
										<bean:define id="publicKeyData" name="serverCertificateDataBean" property="publicKey" type="java.lang.String" /> 
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
							<%} %>
							<%if(serverCertData.getBasicConstraint()!=null){ %>
								<tr>
									<td class="tblfirstcol" width="30%">
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
									<td class="tblfirstcol" width="30%">
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
									<td class="tblfirstcol" width="30%">
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
									<td class="tblfirstcol" width="30%">
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
									<td class="tblfirstcol" width="30%">
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
						<!-- Private Key Detail -->
						<tr>
							<td class="tblheader-bold" colspan="2" height="20%">
								<bean:message bundle="servermgrResources" key="servermgr.certificate.privatekey.detail" />
							</td>
						</tr>
						<% if(serverCertData.getPrivateKey()!=null){ %>
							<tr>
								<td class="tblfirstcol" width="30%">
									<bean:message bundle="servermgrResources" key="servermgr.certificate.privatekey" />
								</td>
								<td class="tblcol" width="70%">
									<bean:define id="privateKeyData" name="serverCertificateDataBean" property="privateKeyData" type="java.lang.String" />
									<%String strPrivateKey=privateKeyData;%>
										<div style="width: 603px; overflow: auto; word-wrap: break-word;">
											<%=strPrivateKey.substring(0,strPrivateKey.indexOf("modulus")) %>
											<br />
											<%=strPrivateKey.substring(strPrivateKey.indexOf("modulus"), strPrivateKey.indexOf("public exponent")) %>
											<br />
											<%=strPrivateKey.substring(strPrivateKey.indexOf("public exponent"), strPrivateKey.indexOf("private exponent")) %>
											<br />
											<%=strPrivateKey.substring(strPrivateKey.indexOf("private exponent"), strPrivateKey.indexOf("prime p")) %>
											<br />
											<%=strPrivateKey.substring(strPrivateKey.indexOf("prime p"), strPrivateKey.indexOf("prime q")) %>
											<br />
											<%=strPrivateKey.substring(strPrivateKey.indexOf("prime q"), strPrivateKey.indexOf("prime exponent p")) %>
											<br />
											<%=strPrivateKey.substring(strPrivateKey.indexOf("prime exponent p"), strPrivateKey.indexOf("prime exponent q")) %>
											<br />
											<%=strPrivateKey.substring(strPrivateKey.indexOf("prime exponent q"), strPrivateKey.indexOf("crt coefficient")) %>
											<br />
											<%=strPrivateKey.substring(strPrivateKey.indexOf("crt coefficient"), strPrivateKey.length()) %>
										</div>
								</td>
							</tr>
						<%}else{ %>
							<tr>
								<td class="tblfirstcol" width="30%" colspan="2" align="center">
									<bean:message bundle="servermgrResources" key="servermgr.certificate.noprivatekey" />
								</td>
							</tr>
						<%} %>
						<tr>
							<td width="25%">&nbsp;</td>
						</tr>
						<%if(serverCertData.getPublicKey()!=null){ %>
							<tr>
								<td align="center" colspan="2">								
									<input type="button" name="c_btnDownload" value="Download Public Certificate" class="light-btn" onclick="downloadCertificate(<bean:write name="serverCertificateDataBean" property="serverCertificateId"/>);">
								</td>
							</tr>
						<%} %>
						<tr>
							<td width="25%">&nbsp;</td>
						</tr>
					</table>
			<%} %>
		</td>
	</tr>
</table>