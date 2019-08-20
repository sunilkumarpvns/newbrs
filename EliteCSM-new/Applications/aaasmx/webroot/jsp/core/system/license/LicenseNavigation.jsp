<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
<%
	String auditUid = (String)request.getAttribute("auditUid");
	String navigationBasePath = request.getContextPath();
	String updateLicense = navigationBasePath+"/viewLicenceAction.do?method=getExistedLicenseData&param=1";
	String viewLicense  = navigationBasePath+"/viewLicenceAction.do?method=getLicenceData";	
 	String viewHistoryLicense  = navigationBasePath+"/viewLicenceHistoryAction.do?method=getLicenceHistory";
	String deregisterLicense = navigationBasePath+"/viewServerLicenceAction.do?method=getServerLicenceInformation";	
%>

			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
					<bean:message key="general.action" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" swapImages()">
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
										<a href="<%=updateLicense%>">
											<bean:message bundle="LicenseResources" key="license.update" />
										</a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=deregisterLicense%>">
											<bean:message bundle="LicenseResources" key="license.deregister" />
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
					<a href="javascript:void(0)" swapImages()">
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
										<a href="<%=viewLicense%>">
											<bean:message bundle="LicenseResources" key="license.viewlicense" />
										</a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewHistoryLicense%>">
											<bean:message bundle="LicenseResources" key="license.history" />
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