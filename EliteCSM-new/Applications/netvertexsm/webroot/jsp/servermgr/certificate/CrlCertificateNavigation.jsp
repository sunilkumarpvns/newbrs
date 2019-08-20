<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.CrlCertificateData"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
			CrlCertificateData crlCertificateData = (CrlCertificateData) request.getAttribute("crlCertificateData");
			String navigationBasePath = request.getContextPath();	
			%>
			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.action" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)">
							<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow">
						</a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
									    <a href="javascript:void(0)" onclick="editDetail()" class="subLink"> 
											<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.update"/>
										</a>
									</td>
								</tr>
							</table>
						</div>
					</td>
				</tr>

				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.view" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)">
							<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow">
						</a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="javascript:void(0)" onclick="viewDetail()" class="subLink"> 
											<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.view"/>
										</a> 
									</td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>

</table>

<script language="javascript">
   function editDetail()
   {
  	  location.href='<%=basePath%>/crlCertificate.do?method=initUpdate&crlCertificateId='+'<%=crlCertificateData.getCrlCertificateId()%>';  
   }
   
   function viewDetail()
   {
  	  location.href='<%=basePath%>/crlCertificate.do?method=view&crlCertificateId='+'<%=crlCertificateData.getCrlCertificateId()%>';  
   }  
   
</script>
