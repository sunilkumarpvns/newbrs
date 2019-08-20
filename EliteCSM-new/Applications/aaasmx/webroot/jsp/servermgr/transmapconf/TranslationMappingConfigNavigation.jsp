<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">
			<%
	TranslationMappingConfData translationMapConfData = (TranslationMappingConfData) request.getAttribute("translationMappingConfData");
	
	String navigationBasePath = request.getContextPath();	
	
	String viewConfiguration = navigationBasePath+"/viewCrestelRatingTransMapConfig.do?translationMapConfigId="+translationMapConfData.getTranslationMapConfigId();
	
	String viewBasicDetail = navigationBasePath+"/viewTranslationMappingConfigBasicDetail.do?translationMapConfigId="+translationMapConfData.getTranslationMapConfigId();

	String updateBasicDetails = navigationBasePath+"/updateTranslationMappingConfigBasicDetail.do?translationMapConfigId="+translationMapConfData.getTranslationMapConfigId();
	
	String updateRatingMappingConfig = navigationBasePath+"/initUpdateTranslationMappingConfig.do?translationMapConfigId="+translationMapConfData.getTranslationMapConfigId();
	
	String viewTranslationMappingHistory = navigationBasePath+"/viewTranslationMappingHistory.do?translationMapConfigId="+translationMapConfData.getTranslationMapConfigId()+"&auditUid="+translationMapConfData.getAuditUid()+"&name="+translationMapConfData.getName();
    
%>

			<table border="0" width="100%" cellspacing="0" cellpadding="0">

				<tr id=header1>
					<td class="subLinksHeader" width="87%"><bean:message
							key="general.action" /></td>
					<td class="subLinksHeader" width="13%"><a
						href="javascript:void(0)"
						onClick="STB('UpdateRadiusPolicy');swapImages()"><img
							src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg"
							border="0" name="arrow"></a></td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks"><a href="<%=updateBasicDetails%>"><bean:message
												bundle="servermgrResources"
												key="translationmapconf.update.basicdetail" /></a></td>
								</tr>
								<tr>
									<td class="subLinks"><a
										href="<%=updateRatingMappingConfig%>"><bean:message
												bundle="servermgrResources"
												key="translationmapconf.update.mappingconfig" /></a></td>
								</tr>
							</table>
						</div>
					</td>
				</tr>



				<tr id=header1>
					<td class="subLinksHeader" width="87%"><bean:message
							key="general.view" /></td>
					<td class="subLinksHeader" width="13%"><a
						href="javascript:void(0)"
						onClick="STB('ViewRadiusPolicy');swapImages()"><img
							src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg"
							border="0" name="arrow"></a></td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks"><a href="<%=viewBasicDetail%>"><bean:message
												bundle="servermgrResources"
												key="translationmapconf.view.basicdetail" /></a></td>
								</tr>
								<tr>
									<td class="subLinks"><a href="<%=viewConfiguration%>"><bean:message
												bundle="servermgrResources"
												key="translationmapconf.view.mappingconfig" /></a></td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewTranslationMappingHistory%>">
											<bean:message bundle="servermgrResources" key="translationmapconf.view.viewhistory" />
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
