<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
				String navigationBasePath = request.getContextPath();
			%>
			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id="header">
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.action" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" onClick="STB('UpdateRadiusPolicy');swapImages()"> 
							<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"/>
						</a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=basePath%>/serverCertificate.do?method=initSearch">
											<bean:message key="server.certificate" />
										</a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=basePath%>/trustedCertificate.do?method=initSearch">
											<bean:message key="server.trustedcertificate" />
										</a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=basePath%>/crlCertificate.do?method=initSearch">
											<bean:message key="server.certificaterevocationlist" />
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