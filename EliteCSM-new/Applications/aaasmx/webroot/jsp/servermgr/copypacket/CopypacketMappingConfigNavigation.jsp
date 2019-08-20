<%@page import="com.elitecore.elitesm.web.history.ViewHistoryAction"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData"%>

<%
	CopyPacketTranslationConfData copyPacketTranslationConfData = (CopyPacketTranslationConfData)request.getAttribute("copyPacketMappingConfData");
	String navigationBasePath = request.getContextPath();	
	String viewBasicDetail = navigationBasePath+"/viewCopyPacketConfigBasicDetail.do?copyPacketTransConfId="+copyPacketTranslationConfData.getCopyPacketTransConfId();
	String viewMappingConfig = navigationBasePath+"/viewCopyPacketMappingConfig.do?copyPacketTransConfId="+copyPacketTranslationConfData.getCopyPacketTransConfId();
	String updateBasicDetail = navigationBasePath+"/updateCopyPacketMappingConfig.do?copyPacketMappingConfigId="+copyPacketTranslationConfData.getCopyPacketTransConfId();
	String updateMappingConfiguraion = navigationBasePath+"/initUpdateCopyPacketMappingConfig.do?copyPacketMappingConfigId="+copyPacketTranslationConfData.getCopyPacketTransConfId();
	String viewCopyPacketMappingHistory = navigationBasePath+"/viewCopyPacketMappingHistory.do?copyPacketMappingConfigId="+copyPacketTranslationConfData.getCopyPacketTransConfId()+"&auditUid="+copyPacketTranslationConfData.getAuditUid()+"&name="+copyPacketTranslationConfData.getName();
	
	System.out.println(updateBasicDetail);

%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" valign="top">


			<table border="0" width="100%" cellspacing="0" cellpadding="0">

				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.action" />
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=updateBasicDetail%>"><bean:message bundle="servermgrResources" key="copypacket.updatebasicdetail" />
										
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=updateMappingConfiguraion%>"><bean:message bundle="servermgrResources" key="copypacket.updatemappingconfig" />
										
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
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=viewBasicDetail%>"><bean:message bundle="servermgrResources" key="copypacket.viewbasicdetail" /></a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewMappingConfig%>"><bean:message bundle="servermgrResources" key="copypacket.viewmappingconfig" /></a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewCopyPacketMappingHistory%>"><bean:message bundle="servermgrResources" key="copypacket.viewhistory" /></a>
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
