<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@page import="java.security.cert.X509Certificate"%>
<%@page import="com.elitecore.elitesm.web.servermgr.certificate.forms.ServerCertificateForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%
	String localBasePath = request.getContextPath();
	ServerCertificateForm serverCertificateForm = (ServerCertificateForm) request.getAttribute("serverCertificateForm");
	X509Certificate x509Cert=EliteUtility.getX509Certificate();	
	boolean certificateIsExpired=EliteUtility.isCertificateIsExpired();
%>
<script type="text/javascript">
function showDetail(obj){
	var id=obj.id;
	//alert(id);
	var title=obj.title;
	<%-- var data=<%=x509Cert.getExtendedKeyUsage()%>
	alert("Data : "+data);  --%>
	//alert(title);
	document.getElementById('txtData').value=document.getElementById('row5').innerHtml;
	//document.getElementById(id).style.cssText ='bgcolor="#FFFFFF"';
	document.getElementById('row1').setAttribute('style','bgcolor="#FFFFFF"');	
	//alert("ID : "+id+" Data : "+title);	
} 
function showCertificateData(newId){
	var txtDetails=document.getElementById(newId).innerHTML;
	//alert(document.getElementById(newId).innerHTML);
	document.getElementById('txtData').value=txtDetails;
}
/* function showDetail(id, data){
	alert("bfdjfd");
	document.getElementById('txtData').value=data;
	alert("ID : "+id+" data : "+data);
	
} */
</script>
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top" class="box" colspan="8">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="81%" background="<%=localBasePath%>/images/popup-bkgd.jpg" valign="top">&nbsp;</td>
					<td width="3%">
						<img src="<%=localBasePath%>/images/popup-curve.jpg">
					</td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg" width="3%">
						<a href="#"> 
						<img src="<%=localBasePath%>/images/refresh.jpg" name="Image1" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image1','','<%=localBasePath%>/images/refresh-hover.jpg',1)" border="0"></a>
					</td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg" width="3%">
						<a href="#" onclick="window.print()">
						<img src="<%=localBasePath%>/images/print.jpg" name="Image2" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image2','','<%=localBasePath%>/images/print-hover.jpg',1)" border="0"> </a>
					</td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg" width="3%">
					 	<a href="#">
					 	<img src="<%=localBasePath%>/images/aboutus.jpg" name="Image3" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image3','','<%=localBasePath%>/images/aboutus-hover.jpg',1)" border="0"></a>
					</td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg" width="3%">
						<a href="#">
						<img src="<%=localBasePath%>/images/help.jpg" name="Image4" onMouseOut="MM_swapImgRestore()"onMouseOver="MM_swapImage('Image4','','<%=localBasePath%>/images/help-hover.jpg',1)" border="0"></a>
					</td>
					<td background="<%=localBasePath%>/images/popup-btn-bkgd.jpg" width="4%">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="8">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td width="38%" class="blue-text-bold" valign="bottom">Server Certificate Details</td>
								<td class="blue-text-bold" width="43%">
									<a href="#">
									<img src="<%=localBasePath%>/images/pdf.jpg" name="Image21" onMouseOver="MM_swapImage('Image21','','<%=localBasePath%>/images/pdf-hover.jpg',1)" border="0" alt="Save as PDF"></a>
									<a href="#">
									<img src="<%=localBasePath%>/images/html.jpg" name="Image31" onMouseOver="MM_swapImage('Image31','','<%=localBasePath%>/images/html-hover.jpg',1)" border="0" alt="Save as HTML"></a>
								</td>								
								<td width="3%" class="blue-text-bold">&nbsp;</td>
								<td width="3%" class="blue-text-bold">&nbsp;</td>
								<td width="3%" class="blue-text-bold">&nbsp;</td>
								<td width="3%" class="blue-text-bold">&nbsp;</td>
								<td width="3%" class="blue-text-bold">&nbsp;</td>
								<td width="4%" class="blue-text-bold">&nbsp;</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="8" class="small-gap">&nbsp;</td>
				</tr>
				<tr align="left">
					<td colspan="8" class="top-btmlines">
						<%if(x509Cert!=null){%> <html:form action="/downloadCSVFormatFile">
							<html:hidden property="action" value="downloadFile" />
							<table width="100%" border="0" cellspacing="0" cellpadding="0" class="box">
								<tr>
									<td height="17" colspan="2" class="labeltext">&nbsp;</td>
								</tr>
								<tr>
									<td colspan="2">
										<table width="100%" border="0" cellspacing=0 cellpadding=0 id="tblData">
											<tr bgcolor="#FFFFFF">
												<td class="tblheader" width="10%">Serial Number</td>
												<td width="25%" class="tblheader">Certificate</td>
											</tr>
											<%-- <logic:iterate id="csvFileData" name="csvFileData"
												indexId="lstIndex">
												<tr bgcolor="#FFFFFF">
													<td class="tblrows"><%=lstIndex + 1%></td>
													<td class="tblrows"><bean:write name="csvFileData" />
													</td>
												</tr>
											</logic:iterate> --%>
											<%if(certificateIsExpired){%>
											<h3>Certificate is Expired</h3>
											<%} %>
											<tr bgcolor="#FFFFFF" id="row1" onclick="showDetail(this)" title=<%=x509Cert.getVersion() %>>
												<td width="20%" class="tblrows">Version</td>
												<td class="tblrows"><%=x509Cert.getVersion() %></td>
											</tr>
											<tr bgcolor="#FFFFFF" id="row2" onclick="showDetail(this)" title=<%=x509Cert.getSerialNumber() %>>
												<td width="20%" class="tblrows">Serial No</td>
												<td class="tblrows"><%=x509Cert.getSerialNumber() %></td>
											</tr>
											<tr bgcolor="#FFFFFF" id="row3" onclick="showDetail(this)" title=<%=x509Cert.getSubjectDN() %>>
												<td width="20%" class="tblrows">Subject</td>
												<td class="tblrows"><%=x509Cert.getSubjectDN() %></td>
											</tr>
											<tr bgcolor="#FFFFFF" id="row4" onclick="showDetail(this)" title=<%=x509Cert.getIssuerDN() %>>
												<td width="20%" class="tblrows">Issuer</td>
												<td class="tblrows"><%=x509Cert.getIssuerDN() %></td>
											</tr>
											<tr bgcolor="#FFFFFF" id="row5" onclick="showDetail(this)" title=<%=x509Cert.getNotBefore() %>>
												<td width="20%" class="tblrows">Not Before</td>
												<td class="tblrows"><%=x509Cert.getNotBefore() %></td>
											</tr>
											<tr bgcolor="#FFFFFF" id="row6" onclick="showDetail(this)" title=<%=x509Cert.getNotAfter() %>>
												<td width="20%" class="tblrows">Not After</td>
												<td class="tblrows"><%=x509Cert.getNotAfter() %></td>
											</tr>
											<%-- <tr bgcolor="#FFFFFF" id="row7" onclick="showDetail(this)" title=<%=x509Cert.getPublicKey() %>>
												<td width="20%" class="tblrows">Public Key</td>
												<td class="tblrows"><%=x509Cert.getPublicKey() %></td>
											</tr> --%>
											
											<%-- <tr bgcolor="#FFFFFF" id="row1" onclick="showDetail('row1',<%=x509Cert.getVersion()%>)">
												<td width="30%" class="tblrows">Version</td>
												<td class="tblrows"><%=x509Cert.getVersion() %></td>
											</tr>											
											<tr bgcolor="#FFFFFF" id="row2" onclick="showDetail('row2',<%=x509Cert.getSerialNumber()%>)">
												<td class="tblrows">Serial No.</td>
												<td class="tblrows"><%=x509Cert.getSerialNumber() %></td>												
											</tr>											
																	
											<tr bgcolor="#FFFFFF" id="row3" onclick="showDetail('row3',<%=x509Cert.getSigAlgName()%>)">
												<td class="tblrows">Signature Algorithm</td>
												<td class="tblrows"><%=x509Cert.getSigAlgName() %></td>
											</tr>											
											<tr bgcolor="#FFFFFF" id="row4" onclick="showDetail('row4',<%=x509Cert.getSigAlgOID()%>)">
												<td class="tblrows">Signature Algorithm OID</td>
												<td class="tblrows"><%=x509Cert.getSigAlgOID() %></td>
											</tr>											
											<tr bgcolor="#FFFFFF" id="row5" onclick="showDetail('row5',<%=x509Cert.getExtendedKeyUsage()%>)">
												<td class="tblrows">Extended Key Usage</td>
												<td class="tblrows"><%=x509Cert.getExtendedKeyUsage() %></td>
											</tr>							
											<tr bgcolor="#FFFFFF" id="row6" onclick="showDetail('row6',<%=x509Cert.getIssuerDN()%>)">
												<td class="tblrows">Issuer DN</td>
												<td class="tblrows"><%=x509Cert.getIssuerDN() %></td>
											</tr>
											<tr bgcolor="#FFFFFF" id="row7" onclick="showDetail('row7',<%=x509Cert.getIssuerX500Principal()%>)">
												<td class="tblrows">Issuer X500 Prinicipal</td>
												<td class="tblrows"><%=x509Cert.getIssuerX500Principal() %></td>
											</tr>
											<tr bgcolor="#FFFFFF" id="row8" onclick="showDetail('row8',<%=x509Cert.getNotBefore()%>)">
												<td class="tblrows">Not Before</td>
												<td class="tblrows"><%=x509Cert.getNotBefore() %></td>
											</tr>
											<tr bgcolor="#FFFFFF" id="row9" onclick="showDetail('row9',<%=x509Cert.getNotAfter()%>)">
												<td class="tblrows">Not After</td>
												<td class="tblrows"><%=x509Cert.getNotAfter() %></td>
											</tr>
											 <tr bgcolor="#FFFFFF" id="row10" onclick="showDetail('row10',<%=x509Cert.getPublicKey()%>)">
												<td class="tblrows">Public Key</td>
												<td class="tblrows"><%=x509Cert.getPublicKey() %></td>
											</tr> 
											<tr bgcolor="#FFFFFF" id="row11" onclick="showDetail('row11',<%=x509Cert.getSubjectAlternativeNames()%>)">
												<td class="tblrows">Signature Alternative Names</td>
												<td class="tblrows"><%=x509Cert.getSubjectAlternativeNames() %></td>
											</tr>
											<tr bgcolor="#FFFFFF" id="row12" onclick="showDetail('row12',<%=x509Cert.getSubjectDN()%>)">
												<td class="tblrows">Subject DN</td>
												<td class="tblrows"><%=x509Cert.getSubjectDN() %></td>
											</tr>
											<tr bgcolor="#FFFFFF" id="row13" onclick="showDetail('row13',<%=x509Cert.getSubjectX500Principal()%>)">
												<td class="tblrows">Subject X500 Principal</td>
												<td class="tblrows"><%=x509Cert.getSubjectX500Principal() %></td>
											</tr>  --%>
										</table>
									</td>
								</tr>
								<!-- <tr>
									<td><textarea valign="center" align="center" id="txtData" rows="10" cols="50"></textarea></td>
								</tr> -->
								<tr>
									<td colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td width="25%">&nbsp;</td>
									<td class="btn-td">
										<input type="button" name="c_btnOk" value="Ok" class="light-btn" onclick="window.close();"> 
										<!-- <input type="button" name="c_btnClose" value="Close" class="light-btn" onClick="window.close();"> -->
									</td>
								</tr>
							</table>
						</html:form> 
						<%}else{ %> Data not found <%} %>
					</td>
				</tr>
				<tr>
					<td colspan="8" class="small-gap">&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td bgcolor="#00477F" class="small-gap" width="99%">&nbsp;</td>
		<td class="small-gap" width="1%">
			<img src="<%=localBasePath%>/images/pbtm-line-end.jpg"></td>
	</tr>
</table>
